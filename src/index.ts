import { Hono } from "hono";
import { cors } from "hono/cors";
import { serveStatic } from "hono/bun";
import { swaggerUI } from "@hono/swagger-ui";
import { env } from "./env.js";
import { logger } from "./logging/logger.js";
import { healthRoutes } from "./routes/health.js";
import { tokenRoutes } from "./routes/token.js";
import { logManagementRoutes, logMonitorRoutes } from "./routes/logs.js";
import { schedulerRoutes } from "./routes/scheduler.js";
import { swaggerRoutes, ENTITY_CONFIGS } from "./routes/swagger-controllers.js";
import { startScheduler } from "./scheduler/cron.js";
import { consumirAnoEMes } from "./consumer/por-periodo-service.js";
import * as ep from "./consumer/endpoints/index.js";
import { readFileSync, existsSync } from "fs";
import { join } from "path";

const app = new Hono();
let runningPort = env.PORT;

app.use("*", cors({
  origin: "*",
  allowMethods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowHeaders: ["Content-Type", "Authorization"],
}));

// Static files
app.use("/css/*", serveStatic({ root: "./src/static" }));
app.use("/js/*", serveStatic({ root: "./src/static" }));

// Index page - inject context path
app.get("/", (c) => {
  const indexPath = join(import.meta.dir, "static", "index.html");
  if (!existsSync(indexPath)) {
    return c.text("SEFAZ Transparency Consumer - Running on Bun/Hono", 200);
  }
  let html = readFileSync(indexPath, "utf-8");
  const contextPath = env.CONTEXT_PATH === "/" ? "" : env.CONTEXT_PATH;
  html = html.replace(
    /<meta\s+name="context-path"\s+content="[^"]*"/,
    `<meta name="context-path" content="${contextPath}"`
  );
  return c.html(html);
});
app.get("/index.html", (c) => c.redirect("/"));

// Swagger UI
app.get("/swagger-ui", swaggerUI({ url: "/api-docs" }));
app.get("/swagger-ui.html", (c) => c.redirect("/swagger-ui"));

// API Docs (OpenAPI spec)
app.get("/api-docs", (c) => {
  const paths: Record<string, any> = {
    "/health": { get: { summary: "Health check", tags: ["Health"], responses: { "200": { description: "OK" } } } },
    "/health/token-test": { get: { summary: "Testar obtenção de token", tags: ["Health"], responses: { "200": { description: "OK" } } } },
    "/api/token/status": { get: { summary: "Status do token", tags: ["Token"], responses: { "200": { description: "OK" } } } },
    "/api/token/connectivity": { get: { summary: "Conectividade com SSO SEFAZ", tags: ["Token"], responses: { "200": { description: "OK" } } } },
    "/api/token/renew": { post: { summary: "Renovar token manualmente", tags: ["Token"], responses: { "200": { description: "OK" } } } },
    "/api/token/get": { get: { summary: "Obter token atual", tags: ["Token"], responses: { "200": { description: "OK" } } } },
    "/api/token/diagnostic": { get: { summary: "Diagnóstico completo do token", tags: ["Token"], responses: { "200": { description: "OK" } } } },
    "/api/logs/status": { get: { summary: "Status dos arquivos de log", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/tail/simple": { get: { summary: "Tail simple.log", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/tail/errors": { get: { summary: "Tail errors.log", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/tail/application": { get: { summary: "Tail application.log", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/clear": { delete: { summary: "Limpar logs", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/rotate": { post: { summary: "Rotacionar todos os logs", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/rotate/operations": { post: { summary: "Rotacionar operations.md", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/operations/info": { get: { summary: "Info de rotação do operations.md", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/api/logs/cleanup": { post: { summary: "Executar limpeza de logs", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/logs/info": { get: { summary: "Informações gerais de logs", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/logs/status": { get: { summary: "Status textual do monitor de logs", tags: ["Logs"], responses: { "200": { description: "OK" } } } },
    "/logs/{fileName}": {
      get: {
        summary: "Ler conteúdo de arquivo de log permitido",
        tags: ["Logs"],
        parameters: [
          { name: "fileName", in: "path", required: true, schema: { type: "string" } },
          { name: "lines", in: "query", schema: { type: "integer" } },
        ],
        responses: { "200": { description: "OK" } },
      },
    },
    "/scheduler/execute": { post: { summary: "Executar consumo completo", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } },
    "/scheduler/status": { get: { summary: "Status do scheduler", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } },
    "/scheduler/info": { get: { summary: "Informações dos endpoints de execução", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } },
    "/scheduler/ping": { get: { summary: "Ping do scheduler", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } },
    "/empenho/mensal/execute-por-periodo": {
      post: {
        summary: "Executar empenho mensal por período",
        tags: ["Scheduler"],
        parameters: [
          { name: "ano", in: "query", required: true, schema: { type: "integer" } },
          { name: "mes", in: "query", required: true, schema: { type: "integer" } },
        ],
        responses: { "200": { description: "OK" } },
      },
    },
  };

  // Documenta todas as rotas de consulta (GET) geradas em swagger-controllers.ts
  for (const config of ENTITY_CONFIGS) {
    paths[config.path] = {
      get: {
        summary: `Consultar ${config.path.replace("/", "")}`,
        tags: ["Consultas"],
        parameters: [
          { name: "page", in: "query", schema: { type: "integer" } },
          { name: "size", in: "query", schema: { type: "integer" } },
          { name: "ano", in: "query", schema: { type: "integer" } },
          { name: "cdUnidadeGestora", in: "query", schema: { type: "string" } },
          { name: "search", in: "query", schema: { type: "string" } },
        ],
        responses: { "200": { description: "Lista paginada" } },
      },
    };
    paths[`${config.path}/count`] = {
      get: {
        summary: `Contagem de registros em ${config.path.replace("/", "")}`,
        tags: ["Consultas"],
        responses: { "200": { description: "OK" } },
      },
    };
  }

  // Documenta as rotas padrão por entidade de execução
  const schedulerEntities = [
    "pagamento",
    "liquidacao",
    "empenho",
    "restos-a-pagar",
    "ordem-fornecimento",
    "dados-orcamentarios",
    "totalizadores-execucao",
    "consulta-gerencial",
    "contrato",
    "contratos-fiscais",
    "contrato-empenho",
    "base-despesa-credor",
    "base-despesa-licitacao",
    "despesa-detalhada",
    "previsao-realizacao-receita",
    "termo",
  ];

  for (const entity of schedulerEntities) {
    paths[`/scheduler/execute/${entity}`] = { post: { summary: `Executar ${entity}`, tags: ["Scheduler"], responses: { "200": { description: "OK" } } } };
    paths[`/scheduler/execute/${entity}/por-ano/{ano}`] = {
      post: {
        summary: `Executar ${entity} por ano`,
        tags: ["Scheduler"],
        parameters: [{ name: "ano", in: "path", required: true, schema: { type: "integer" } }],
        responses: { "200": { description: "OK" } },
      },
    };
    paths[`/scheduler/execute/${entity}/por-periodo`] = {
      post: {
        summary: `Executar ${entity} por período`,
        tags: ["Scheduler"],
        parameters: [
          { name: "ano", in: "query", required: true, schema: { type: "integer" } },
          { name: "mes", in: "query", required: true, schema: { type: "integer" } },
        ],
        responses: { "200": { description: "OK" } },
      },
    };
    paths[`/scheduler/execute/${entity}/por-ano/{ano}/ug/{cdUnidadeGestora}`] = {
      post: {
        summary: `Executar ${entity} por ano e UG`,
        tags: ["Scheduler"],
        parameters: [
          { name: "ano", in: "path", required: true, schema: { type: "integer" } },
          { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
        ],
        responses: { "200": { description: "OK" } },
      },
    };
    paths[`/scheduler/execute/${entity}/por-periodo/ug/{cdUnidadeGestora}`] = {
      post: {
        summary: `Executar ${entity} por período e UG`,
        tags: ["Scheduler"],
        parameters: [
          { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
          { name: "ano", in: "query", required: true, schema: { type: "integer" } },
          { name: "mes", in: "query", required: true, schema: { type: "integer" } },
        ],
        responses: { "200": { description: "OK" } },
      },
    };
  }

  // Rotas especiais de convênios e previsão multi-mês
  paths["/scheduler/execute/convenio/despesa"] = { post: { summary: "Executar despesa convênio", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } };
  paths["/scheduler/execute/convenio/despesa/por-ano/{ano}"] = {
    post: { summary: "Executar despesa convênio por ano", tags: ["Scheduler"], parameters: [{ name: "ano", in: "path", required: true, schema: { type: "integer" } }], responses: { "200": { description: "OK" } } },
  };
  paths["/scheduler/execute/convenio/despesa/por-periodo"] = {
    post: {
      summary: "Executar despesa convênio por período",
      tags: ["Scheduler"],
      parameters: [
        { name: "ano", in: "query", required: true, schema: { type: "integer" } },
        { name: "mes", in: "query", required: true, schema: { type: "integer" } },
      ],
      responses: { "200": { description: "OK" } },
    },
  };
  paths["/scheduler/execute/convenio/despesa/por-ano/{ano}/ug/{cdUnidadeGestora}"] = {
    post: {
      summary: "Executar despesa convênio por ano e UG",
      tags: ["Scheduler"],
      parameters: [
        { name: "ano", in: "path", required: true, schema: { type: "integer" } },
        { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
      ],
      responses: { "200": { description: "OK" } },
    },
  };
  paths["/scheduler/execute/convenio/despesa/por-periodo/ug/{cdUnidadeGestora}"] = {
    post: {
      summary: "Executar despesa convênio por período e UG",
      tags: ["Scheduler"],
      parameters: [
        { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
        { name: "ano", in: "query", required: true, schema: { type: "integer" } },
        { name: "mes", in: "query", required: true, schema: { type: "integer" } },
      ],
      responses: { "200": { description: "OK" } },
    },
  };

  paths["/scheduler/execute/convenio/receita"] = { post: { summary: "Executar receita convênio", tags: ["Scheduler"], responses: { "200": { description: "OK" }, "409": { description: "ALREADY_RUNNING" } } } };
  paths["/scheduler/execute/convenio/receita/por-ano/{ano}"] = {
    post: {
      summary: "Executar receita convênio por ano",
      tags: ["Scheduler"],
      parameters: [{ name: "ano", in: "path", required: true, schema: { type: "integer" } }],
      responses: { "200": { description: "OK" }, "409": { description: "ALREADY_RUNNING" } },
    },
  };
  paths["/scheduler/execute/convenio/receita/por-periodo"] = {
    post: {
      summary: "Executar receita convênio por período",
      tags: ["Scheduler"],
      parameters: [
        { name: "ano", in: "query", required: true, schema: { type: "integer" } },
        { name: "mes", in: "query", required: true, schema: { type: "integer" } },
      ],
      responses: { "200": { description: "OK" }, "409": { description: "ALREADY_RUNNING" } },
    },
  };
  paths["/scheduler/execute/convenio/receita/por-ano/{ano}/ug/{cdUnidadeGestora}"] = {
    post: {
      summary: "Executar receita convênio por ano e UG",
      tags: ["Scheduler"],
      parameters: [
        { name: "ano", in: "path", required: true, schema: { type: "integer" } },
        { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
      ],
      responses: { "200": { description: "OK" }, "409": { description: "ALREADY_RUNNING" } },
    },
  };
  paths["/scheduler/execute/convenio/receita/por-periodo/ug/{cdUnidadeGestora}"] = {
    post: {
      summary: "Executar receita convênio por período e UG",
      tags: ["Scheduler"],
      parameters: [
        { name: "cdUnidadeGestora", in: "path", required: true, schema: { type: "string" } },
        { name: "ano", in: "query", required: true, schema: { type: "integer" } },
        { name: "mes", in: "query", required: true, schema: { type: "integer" } },
      ],
      responses: { "200": { description: "OK" }, "409": { description: "ALREADY_RUNNING" } },
    },
  };

  paths["/scheduler/execute/previsao-realizacao-receita-multi-mes"] = { post: { summary: "Executar previsão realização receita multi-mês", tags: ["Scheduler"], responses: { "200": { description: "OK" } } } };
  paths["/scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes}"] = {
    post: {
      summary: "Executar previsão realização receita para mês específico",
      tags: ["Scheduler"],
      parameters: [{ name: "mes", in: "path", required: true, schema: { type: "integer" } }],
      responses: { "200": { description: "OK" } },
    },
  };

  return c.json({
    openapi: "3.0.3",
    info: {
      title: "SEFAZ Transparency Consumer API",
      description: "API consumer for Sergipe State Tax Authority transparency data",
      version: "1.0.0",
    },
    servers: [{ url: `http://localhost:${runningPort}${env.CONTEXT_PATH === "/" ? "" : env.CONTEXT_PATH}` }],
    paths,
  });
});

// Legacy-compatible endpoint from previous scheduler map
app.post("/empenho/mensal/execute-por-periodo", async (c) => {
  const correlationId = logger.generateCorrelationId();
  const ano = parseInt(c.req.query("ano") || "0");
  const mes = parseInt(c.req.query("mes") || "0");

  if (isNaN(ano) || ano < 2000 || ano > 2030) {
    return c.json({ status: "ERROR", message: "Ano deve estar entre 2000 e 2030", correlationId }, 400);
  }
  if (isNaN(mes) || mes < 1 || mes > 12) {
    return c.json({ status: "ERROR", message: "Mês deve estar entre 1 e 12", correlationId }, 400);
  }

  try {
    const processed = await consumirAnoEMes(ep.empenho, ano, mes);
    return c.json({
      status: "SUCCESS",
      correlationId,
      recordsProcessed: processed,
      ano,
      mes,
      message: `${processed} registros processados`,
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json(
      { status: "ERROR", correlationId, message: e instanceof Error ? e.message : String(e), timestamp: new Date().toISOString() },
      500
    );
  }
});

// Routes
app.route("/health", healthRoutes);
app.route("/api/token", tokenRoutes);
app.route("/api/logs", logManagementRoutes);
app.route("/logs", logMonitorRoutes);
app.route("/scheduler", schedulerRoutes);
app.route("/", swaggerRoutes);

// Startup
logger.logApplicationStart(env.APP_NAME);
logger.markdownSimple("Inicialização da Aplicação", `${env.APP_NAME} | Perfil: ${env.NODE_ENV} | Runtime: Bun`);

const shouldRunScheduler = env.SCHEDULER_ENABLED && env.CLUSTER_ROLE !== "worker";
if (shouldRunScheduler) {
  startScheduler();
} else if (env.CLUSTER_ENABLED) {
  logger.info("SCHEDULER", `Scheduler desabilitado para role '${env.CLUSTER_ROLE}' em modo cluster`);
}

logger.logApplicationReady();

function isAddressInUseError(error: unknown): boolean {
  if (!error || typeof error !== "object") return false;
  const candidate = error as { code?: string; message?: string };
  return candidate.code === "EADDRINUSE" || candidate.message?.includes("EADDRINUSE") === true;
}

function startServerWithPortFallback(initialPort: number) {
  if (env.CLUSTER_ENABLED) {
    const server = Bun.serve({
      port: initialPort,
      fetch: app.fetch,
      reusePort: true,
      /** Padrão Bun: 10s — requisições que disparam consumo longo precisam de margem (máx. 255s). */
      idleTimeout: 255,
    });
    return { server, port: initialPort };
  }

  let port = initialPort;

  while (true) {
    try {
      const server = Bun.serve({
        port,
        fetch: app.fetch,
        /** Padrão Bun: 10s — requisições que disparam consumo longo precisam de margem (máx. 255s). */
        idleTimeout: 255,
      });
      return { server, port };
    } catch (error) {
      if (!isAddressInUseError(error)) {
        throw error;
      }

      logger.warning("SERVER", `Erro específico EADDRINUSE na porta ${port}. Tentando próxima porta...`);
      port += 1;
    }
  }
}

const { port } = startServerWithPortFallback(env.PORT);
runningPort = port;
logger.displayAvailableLinks(runningPort);
