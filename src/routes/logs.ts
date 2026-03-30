import { Hono } from "hono";
import { existsSync, statSync, readdirSync, readFileSync, writeFileSync } from "fs";
import { join } from "path";
import { env } from "../env.js";
import { forceRotation, forceAllLogsRotation, getCurrentFileInfo } from "../logging/log-rotation.js";
import { performCleanup } from "../logging/log-cleanup.js";

export const logManagementRoutes = new Hono();
export const logMonitorRoutes = new Hono();

function getLogPath() { return env.LOG_PATH; }

// === Log Management (/api/logs) ===

logManagementRoutes.get("/status", (c) => {
  const logPath = getLogPath();
  if (!existsSync(logPath)) return c.text("Diretório de logs não encontrado");
  const files = readdirSync(logPath);
  const lines: string[] = ["=== Status dos Logs ===\n"];
  for (const file of files) {
    try {
      const stats = statSync(join(logPath, file));
      if (stats.isFile()) {
        const sizeMb = (stats.size / (1024 * 1024)).toFixed(2);
        lines.push(`${file}: ${sizeMb} MB (modificado: ${stats.mtime.toISOString()})`);
      }
    } catch { /* skip */ }
  }
  return c.text(lines.join("\n"));
});

function tailFile(filename: string, lines = 20): string {
  const filePath = join(getLogPath(), filename);
  if (!existsSync(filePath)) return `Arquivo ${filename} não encontrado`;
  const content = readFileSync(filePath, "utf-8");
  return content.split("\n").slice(-lines).join("\n");
}

logManagementRoutes.get("/tail/simple", (c) => c.text(tailFile("simple.log")));
logManagementRoutes.get("/tail/errors", (c) => c.text(tailFile("errors.log")));
logManagementRoutes.get("/tail/application", (c) => c.text(tailFile("application.log")));

logManagementRoutes.delete("/clear", (c) => {
  const logPath = getLogPath();
  for (const file of ["simple.log", "errors.log", "application.log", "operations.md"]) {
    const filePath = join(logPath, file);
    if (existsSync(filePath)) writeFileSync(filePath, "");
  }
  return c.json({ status: "OK", message: "Logs limpos" });
});

logManagementRoutes.post("/rotate", (c) => {
  const results = forceAllLogsRotation();
  return c.json(results);
});

logManagementRoutes.post("/rotate/operations", (c) => {
  const result = forceRotation("operations.md");
  return c.json(result);
});

logManagementRoutes.get("/operations/info", (c) => {
  const info = getCurrentFileInfo("operations.md");
  return c.json({
    ...info,
    recommendations: info.needsRotation ? "Recomendado executar rotação" : "Tamanho OK",
  });
});

logManagementRoutes.post("/cleanup", (c) => {
  const result = performCleanup();
  return c.json(result);
});

// === Log Monitor (/logs) ===

const ALLOWED_FILES = new Set(["simple.log", "errors.log", "application.log", "operations.md"]);
const activeStreamClients = new Set<ReadableStreamDefaultController<Uint8Array>>();

function toUtf8(input: string): Uint8Array {
  return new TextEncoder().encode(input);
}

logMonitorRoutes.get("/info", (c) => {
  const logPath = getLogPath();
  const fileInfos: Record<string, any> = {};
  for (const file of ALLOWED_FILES) {
    const filePath = join(logPath, file);
    if (existsSync(filePath)) {
      const stats = statSync(filePath);
      fileInfos[file] = { size: stats.size, lastModified: stats.mtime.toISOString() };
    }
  }
  return c.json({
    files: fileInfos,
    activeConnections: activeStreamClients.size,
    timestamp: new Date().toISOString(),
  });
});

logMonitorRoutes.get("/status", (c) => {
  const logPath = getLogPath();
  if (!existsSync(logPath)) return c.text("Diretório de logs não encontrado");
  const files = readdirSync(logPath);
  const lines: string[] = ["=== Monitor de Logs ===\n"];
  for (const file of files) {
    try {
      const stats = statSync(join(logPath, file));
      if (stats.isFile()) {
        const sizeMb = (stats.size / (1024 * 1024)).toFixed(2);
        lines.push(`${file}: ${sizeMb} MB`);
      }
    } catch { /* skip */ }
  }
  return c.text(lines.join("\n"));
});

logMonitorRoutes.get("/stream", async (c) => {
  const logPath = getLogPath();
  const filePositions: Record<string, number> = {};
  let streamController: ReadableStreamDefaultController<Uint8Array> | null = null;
  let interval: ReturnType<typeof setInterval> | null = null;
  let keepAlive: ReturnType<typeof setInterval> | null = null;

  for (const fileName of ALLOWED_FILES) {
    const filePath = join(logPath, fileName);
    if (existsSync(filePath)) {
      const stats = statSync(filePath);
      filePositions[fileName] = stats.size;
    } else {
      filePositions[fileName] = 0;
    }
  }

  const stream = new ReadableStream<Uint8Array>({
    start(controller) {
      streamController = controller;
      activeStreamClients.add(controller);

      // SSE handshake event
      controller.enqueue(
        toUtf8(
          `event: connected\n` +
          `data: ${JSON.stringify({
            message: "Conectado ao monitor de logs",
            activeConnections: activeStreamClients.size,
            timestamp: new Date().toISOString(),
          })}\n\n`
        )
      );

      interval = setInterval(() => {
        try {
          for (const fileName of ALLOWED_FILES) {
            const filePath = join(logPath, fileName);
            if (!existsSync(filePath)) continue;

            const stats = statSync(filePath);
            const previousPos = filePositions[fileName] ?? 0;

            // Handle truncation/rotation
            if (stats.size < previousPos) {
              filePositions[fileName] = 0;
            }

            const currentPos = filePositions[fileName] ?? 0;
            if (stats.size > currentPos) {
              const content = readFileSync(filePath, "utf-8");
              const newChunk = content.substring(currentPos);
              const lines = newChunk
                .split("\n")
                .map((line) => line.trimEnd())
                .filter((line) => line.length > 0);

              filePositions[fileName] = stats.size;

              if (lines.length > 0) {
                controller.enqueue(
                  toUtf8(
                    `event: logUpdate\n` +
                    `data: ${JSON.stringify({
                      fileName,
                      lines,
                      timestamp: new Date().toISOString(),
                    })}\n\n`
                  )
                );
              }
            }
          }
        } catch {
          // Ignore transient errors during streaming
        }
      }, 2000);

      // Keep-alive ping every 25 seconds to avoid proxies dropping SSE
      keepAlive = setInterval(() => {
        try {
          controller.enqueue(toUtf8(`: keep-alive ${Date.now()}\n\n`));
        } catch {
          // no-op
        }
      }, 25000);
    },
    cancel() {
      if (interval) clearInterval(interval);
      if (keepAlive) clearInterval(keepAlive);
      if (streamController) activeStreamClients.delete(streamController);
    },
  });

  c.req.raw.signal.addEventListener("abort", () => {
    if (interval) clearInterval(interval);
    if (keepAlive) clearInterval(keepAlive);
    if (streamController) activeStreamClients.delete(streamController);
  });

  return new Response(stream, {
    headers: {
      "Content-Type": "text/event-stream",
      "Cache-Control": "no-cache",
      Connection: "keep-alive",
      "X-Accel-Buffering": "no",
    },
  });
});

logMonitorRoutes.get("/:fileName", (c) => {
  const fileName = c.req.param("fileName");
  if (!ALLOWED_FILES.has(fileName)) return c.json({ error: "Arquivo não permitido" }, 400);
  const lines = parseInt(c.req.query("lines") || "100");
  const filePath = join(getLogPath(), fileName);
  if (!existsSync(filePath)) {
    return c.json({ error: `Arquivo ${fileName} não encontrado` }, 404);
  }

  const stats = statSync(filePath);
  const content = tailFile(fileName, lines);
  const lineCount = content.trim() ? content.split("\n").length : 0;

  return c.json({
    fileName,
    content,
    lines: lineCount,
    size: stats.size,
    lastModified: stats.mtime.toISOString(),
  });
});
