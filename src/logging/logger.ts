import { mkdirSync, appendFileSync, existsSync } from "fs";
import { join } from "path";
import { env } from "../env.js";
import { randomUUID } from "crypto";

const RESET = "\x1b[0m";
const RED = "\x1b[31m";
const GREEN = "\x1b[32m";
const YELLOW = "\x1b[33m";
const BLUE = "\x1b[34m";
const PURPLE = "\x1b[35m";
const CYAN = "\x1b[36m";
const WHITE = "\x1b[37m";
const BOLD = "\x1b[1m";

function getLogPath(): string {
  const logPath = env.LOG_PATH;
  if (!existsSync(logPath)) {
    mkdirSync(logPath, { recursive: true });
  }
  return logPath;
}

function formatTime(): string {
  const now = new Date();
  return now.toTimeString().substring(0, 8);
}

function formatTimestamp(): string {
  return new Date().toISOString().replace("T", " ").substring(0, 23);
}

function formatDuration(ms: number): string {
  if (ms < 1000) return `${ms}ms`;
  if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`;
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000);
  return `${minutes}m ${seconds}s`;
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`;
}

function writeToFile(filename: string, content: string) {
  try {
    const filePath = join(getLogPath(), filename);
    appendFileSync(filePath, content + "\n");
  } catch {
    // Silently ignore file write errors
  }
}

function centerText(text: string, width: number): string {
  const padding = Math.max(0, Math.floor((width - text.length) / 2));
  return " ".repeat(padding) + text + " ".repeat(Math.max(0, width - text.length - padding));
}

/** Linha do relatório final da execução automática (scheduler). */
export interface SchedulerEntityReportRow {
  name: string;
  records: number;
  durationMs: number;
  status: "ok" | "error" | "skipped";
  errorMessage?: string;
}

function createProgressBar(percentage: number, width: number): string {
  const filled = Math.floor((percentage / 100) * width);
  let bar = "[";
  for (let i = 0; i < width; i++) {
    bar += i < filled ? `${GREEN}\u2588${RESET}` : `${RED}\u2591${RESET}`;
  }
  bar += "]";
  return bar;
}

class Logger {
  private correlationId: string = "";
  private component: string = "APP";
  private operation: string = "UNKNOWN";

  generateCorrelationId(): string {
    this.correlationId = randomUUID().substring(0, 8);
    return this.correlationId;
  }

  setContext(component: string, operation?: string) {
    this.component = component;
    if (operation) this.operation = operation;
    if (!this.correlationId) this.generateCorrelationId();
  }

  getCorrelationId(): string {
    return this.correlationId || this.generateCorrelationId();
  }

  getComponent(): string { return this.component; }
  getOperation(): string { return this.operation; }

  clearContext() {
    this.correlationId = "";
    this.component = "APP";
    this.operation = "UNKNOWN";
  }

  // === SimpleLogger equivalents ===

  info(component: string, message: string) {
    this.setContext(component);
    const line = `${formatTime()} | ${component} | ${message}`;
    writeToFile("simple.log", line);
  }

  success(component: string, message: string, durationMs?: number) {
    this.setContext(component);
    const duration = durationMs != null ? ` (${formatDuration(durationMs)})` : "";
    const line = `${formatTime()} | ${component} | \u2705 ${message}${duration}`;
    writeToFile("simple.log", line);
  }

  warning(component: string, message: string) {
    this.setContext(component);
    const line = `${formatTime()} | ${component} | \u26a0\ufe0f ${message}`;
    writeToFile("simple.log", line);
  }

  error(component: string, message: string, error?: Error | unknown) {
    this.setContext(component);
    const errMsg = error instanceof Error ? error.message : String(error ?? "");
    const line = `${formatTime()} | ${component} | \u274c ${message}${errMsg ? `: ${errMsg}` : ""}`;
    writeToFile("simple.log", line);

    const errorLine = `${formatTimestamp()} ERROR [${this.correlationId || "N/A"}] [${component}] [${this.operation}] - ${message}${errMsg ? `: ${errMsg}` : ""}`;
    if (error instanceof Error && error.stack) {
      writeToFile("errors.log", `${errorLine}\n${error.stack}`);
    } else {
      writeToFile("errors.log", errorLine);
    }

    const mdError = `\n## ${formatTime()} | \u274c ERRO em ${component}\n- \ud83d\udea8 **Erro**: ${message}\n${error instanceof Error ? `- \ud83d\udd0d **Tipo**: ${error.constructor.name}\n- \ud83d\udccb **Detalhes**: ${error.message}\n` : ""}${this.correlationId ? `- \ud83d\udd17 **Correlation ID**: ${this.correlationId}\n` : ""}\n`;
    writeToFile("operations.md", mdError);
  }

  progress(component: string, operation: string, current: number, total: number) {
    this.setContext(component);
    const percentage = Math.floor((current * 100) / total);
    const line = `${formatTime()} | ${component} | \ud83d\udd04 ${operation} - ${current}/${total} (${percentage}%)`;
    writeToFile("simple.log", line);
  }

  start(component: string, operation: string) {
    this.setContext(component);
    const line = `${formatTime()} | ${component} | \ud83d\ude80 Iniciando ${operation}`;
    writeToFile("simple.log", line);
  }

  slow(component: string, message: string, durationMs: number) {
    this.setContext(component);
    const line = `${formatTime()} | ${component} | \ud83d\udc0c ${message} (${formatDuration(durationMs)})`;
    writeToFile("simple.log", line);
  }

  apiEndpoint(method: string, url: string) {
    this.setContext("API_CLIENT");
    writeToFile("simple.log", `API_CALL | ${method} | ${url || ""}`);
  }

  apiSkipUg(ugCd: string, reason: string) {
    this.setContext("API_CLIENT");
    writeToFile("simple.log", `API_SKIP | UG: ${ugCd} | ${reason}`);
  }

  // === UnifiedLogger equivalents ===

  logOperationStart(component: string, operation: string, ...context: (string | number)[]) {
    this.setContext(component, operation);
    const contextStr = context.length > 0 ? " | " + this.formatContext(context) : "";
    this.start(component, `${operation}${contextStr}`);
    const appLine = `${formatTimestamp()} INFO  [${this.correlationId || "N/A"}] [${component}] [${operation}] - START${contextStr}`;
    writeToFile("application.log", appLine);
  }

  logOperationSuccess(component: string, operation: string, durationMs: number, dataCount: number, ...context: (string | number)[]) {
    this.setContext(component, operation);
    const contextStr = context.length > 0 ? " | " + this.formatContext(context) : "";
    const message = `${operation} | ${dataCount} registros${contextStr}`;
    if (durationMs > 5000) {
      this.slow(component, message, durationMs);
    } else {
      this.success(component, message, durationMs);
    }
    const appLine = `${formatTimestamp()} INFO  [${this.correlationId || "N/A"}] [${component}] [${operation}] - SUCCESS ${dataCount} records in ${formatDuration(durationMs)}${contextStr}`;
    writeToFile("application.log", appLine);
  }

  logOperationError(component: string, operation: string, durationMs: number, error: Error | unknown, ...context: (string | number)[]) {
    this.setContext(component, operation);
    const contextStr = context.length > 0 ? " | " + this.formatContext(context) : "";
    this.error(component, `${operation}${contextStr}`, error);
    const errMsg = error instanceof Error ? error.message : String(error);
    const appLine = `${formatTimestamp()} ERROR [${this.correlationId || "N/A"}] [${component}] [${operation}] - FAILED after ${formatDuration(durationMs)}: ${errMsg}${contextStr}`;
    writeToFile("application.log", appLine);
  }

  logApiCall(endpoint: string, method: string, statusCode: number, responseTimeMs: number, requestSize: number, responseSize: number) {
    this.setContext("API_CLIENT", "API_CALL");
    const emoji = statusCode >= 400 ? "\ud83d\udea8" : responseTimeMs > 3000 ? "\ud83d\udc0c" : "\ud83c\udf10";
    const level = statusCode >= 400 ? "ERROR" : responseTimeMs > 3000 ? "WARN " : "INFO ";
    const line = `${formatTimestamp()} ${level} [${this.correlationId || "N/A"}] [API_CLIENT] [API_CALL] - ${emoji} ${method} ${endpoint} | STATUS: ${statusCode} | DURATION: ${formatDuration(responseTimeMs)} | REQUEST: ${formatBytes(requestSize)} | RESPONSE: ${formatBytes(responseSize)}`;
    writeToFile("application.log", line);
  }

  logDatabaseOperation(operation: string, table: string, recordCount: number, durationMs: number) {
    this.setContext("DATABASE", `${operation}_${table}`);
    const emoji = durationMs > 10000 ? "\ud83d\udc0c DB LENTO" : "\ud83d\uddc4\ufe0f DB";
    const line = `${formatTimestamp()} ${durationMs > 10000 ? "WARN " : "INFO "} [${this.correlationId || "N/A"}] [DATABASE] - ${emoji} ${operation} na tabela ${table} | RECORDS: ${recordCount} | DURATION: ${formatDuration(durationMs)}`;
    writeToFile("application.log", line);
  }

  logAuthentication(clientId: string, endpoint: string, success: boolean, durationMs: number, correlationId: string) {
    const emoji = success ? "\u2705" : "\u274c";
    const status = success ? "SUCESSO" : "FALHOU";
    const line = `${formatTimestamp()} ${success ? "INFO " : "ERROR"} [${correlationId}] [SECURITY] - ${emoji} AUTENTICA\u00c7\u00c3O ${status} | Cliente: ${clientId} | DURATION: ${formatDuration(durationMs)}`;
    writeToFile("application.log", line);
    if (!success) {
      writeToFile("errors.log", line);
    }
  }

  // === MarkdownLogger equivalents ===

  markdownSection(title: string) {
    return new MarkdownSection(title);
  }

  markdownSimple(title: string, message: string) {
    writeToFile("operations.md", `\n## ${formatTime()} | ${title}\n- \ud83d\udccb ${message}\n`);
  }

  markdownError(title: string, error: string, details?: string) {
    writeToFile("operations.md", `\n## ${formatTime()} | ${title}\n- \u274c ${error}\n${details ? `- \ud83d\udd0d Detalhes: ${details}\n` : ""}`);
  }

  // === UserFriendlyLogger equivalents ===

  userInfo(message: string) { console.log(message); }
  userError(message: string) { console.error(message); }
  userWarn(message: string) { console.warn(`AVISO: ${message}`); }

  logAuthenticationStart() {
    writeToFile("simple.log", `${formatTime()} | SECURITY | Obtendo token de autentica\u00e7\u00e3o...`);
  }
  logAuthenticationSuccess() {
    writeToFile("simple.log", `${formatTime()} | SECURITY | Token obtido com sucesso`);
  }
  logAuthenticationError() { console.error("ERRO: Falha na autentica\u00e7\u00e3o - detalhes em logs/errors.log"); }

  logDataFetchStart(dataType: string) { console.log(`Buscando ${dataType}...`); }
  logDataFound(dataType: string, count: number) { console.log(`Total de ${dataType} encontradas: ${count}`); }
  logProcessingStart(dataType: string) { console.log(`Processando ${dataType}...`); }
  logDataProcessed(dataType: string, count: number) { console.log(`Total de ${dataType} processados: ${count}`); }
  logSavingStart() {
    writeToFile("simple.log", `${formatTime()} | CONSUMER | Salvando dados no banco...`);
  }
  logDataSaved(count: number) {
    writeToFile("simple.log", `${formatTime()} | CONSUMER | Dados salvos: ${count} registros`);
  }

  logOperationComplete(durationMs: number) {
    console.log(`Opera\u00e7\u00e3o conclu\u00edda em ${(durationMs / 1000).toFixed(1)}s`);
  }

  logApplicationStart(appName: string) {
    console.log(`=== ${appName} ===`);
    console.log("Aplica\u00e7\u00e3o iniciando...");
  }

  logApplicationReady() {
    console.log("Aplica\u00e7\u00e3o pronta para uso");
    console.log("=====================================");
  }

  logScheduledExecutionStart() { console.log("Iniciando execu\u00e7\u00e3o autom\u00e1tica..."); }

  logScheduledExecutionComplete(totalRecords: number, durationMs: number) {
    console.log("Execu\u00e7\u00e3o autom\u00e1tica conclu\u00edda");
    console.log(`Total processado: ${totalRecords} registros em ${(durationMs / 1000).toFixed(1)}s`);
  }

  /**
   * Relatório completo ao fim do scheduler: console + simple.log + operations.md.
   */
  logScheduledExecutionReport(
    rows: SchedulerEntityReportRow[],
    totalRecords: number,
    totalDurationMs: number,
    correlationId: string,
    startedAtIso: string
  ) {
    const endedAtIso = new Date().toISOString();
    const sep = "═".repeat(72);
    const sepThin = "─".repeat(72);
    const padName = (s: string, w: number) => {
      const t = s.length > w ? `${s.slice(0, w - 1)}…` : s;
      return t.padEnd(w);
    };

    writeToFile(
      "application.log",
      `${formatTimestamp()} INFO  [${correlationId}] [SCHEDULER] [EXECUTE_ALL] - RELATÓRIO FINAL | total=${totalRecords} | ${formatDuration(totalDurationMs)} | linhas=${rows.length} | inicio=${startedAtIso} | fim=${endedAtIso}`
    );
    writeToFile(
      "simple.log",
      `SCHEDULER_REPORT | ${correlationId} | total=${totalRecords} | ${formatDuration(totalDurationMs)} | inicio=${startedAtIso} | fim=${endedAtIso}`
    );
    for (const r of rows) {
      const err = r.errorMessage ? ` | ${r.errorMessage}` : "";
      writeToFile(
        "simple.log",
        `SCHEDULER_REPORT_ROW | ${r.name} | ${r.records} | ${formatDuration(r.durationMs)} | ${r.status}${err}`
      );
    }

    const mdLines = [
      `\n## ${formatTime()} | Relatório final — execução automática`,
      `- **Correlation ID:** \`${correlationId}\``,
      `- **Início:** ${startedAtIso}`,
      `- **Fim:** ${endedAtIso}`,
      `- **Total de registros:** ${totalRecords}`,
      `- **Duração total:** ${formatDuration(totalDurationMs)}`,
      "",
      "| Entidade | Registros | Duração | Status |",
      "|----------|-----------|---------|--------|",
      ...rows.map((r) => {
        const st = r.status === "ok" ? "OK" : r.status === "skipped" ? "Pulado" : "Erro";
        const note = r.errorMessage ? ` (${r.errorMessage})` : "";
        return `| ${r.name} | ${r.records} | ${formatDuration(r.durationMs)} | ${st}${note} |`;
      }),
      "",
    ];
    writeToFile("operations.md", mdLines.join("\n"));

    console.log();
    console.log(`${GREEN}${BOLD}${sep}${RESET}`);
    console.log(`${GREEN}${BOLD}${centerText("RELATÓRIO FINAL — CONSUMO AUTOMÁTICO", 72)}${RESET}`);
    console.log(`${GREEN}${BOLD}${sep}${RESET}`);
    console.log(`${CYAN}Correlation ID:${RESET} ${WHITE}${correlationId}${RESET}`);
    console.log(`${CYAN}Início:${RESET}         ${WHITE}${startedAtIso}${RESET}`);
    console.log(`${CYAN}Fim:${RESET}            ${WHITE}${endedAtIso}${RESET}`);
    console.log(`${CYAN}Total registros:${RESET} ${WHITE}${BOLD}${totalRecords}${RESET}`);
    console.log(`${CYAN}Duração total:${RESET}   ${WHITE}${formatDuration(totalDurationMs)}${RESET}`);
    console.log(`${BLUE}${sepThin}${RESET}`);
    console.log(`${BOLD}${padName("Entidade", 40)} ${"Registros".padStart(10)} ${"Duração".padStart(12)} ${"Status".padStart(10)}${RESET}`);
    console.log(`${BLUE}${sepThin}${RESET}`);
    for (const r of rows) {
      const st =
        r.status === "ok" ? `${GREEN}OK${RESET}` :
        r.status === "skipped" ? `${YELLOW}PULADO${RESET}` :
        `${RED}ERRO${RESET}`;
      const line = `${padName(r.name, 40)} ${String(r.records).padStart(10)} ${formatDuration(r.durationMs).padStart(12)} ${st}`;
      const extra = r.errorMessage && r.status !== "ok" ? ` ${YELLOW}${r.errorMessage}${RESET}` : "";
      console.log(line + extra);
    }
    console.log(`${GREEN}${BOLD}${sep}${RESET}`);
    console.log();
  }

  // === Console banners (SimpleLogger equivalents) ===

  /**
   * Cabeçalho único de qualquer consumo (manual, scheduler, por ano, etc.): entidade, modo, período, escopo e ação num único bloco.
   * Com `quiet: true` grava apenas em simple.log (ex.: scheduler sem poluir o terminal).
   */
  consumptionSessionOpen(
    args: { entity: string; modo: string; periodo: string; escopo?: string; description: string },
    opts?: { quiet?: boolean }
  ) {
    const esc = args.escopo ? ` | ${args.escopo}` : "";
    writeToFile(
      "simple.log",
      `CONSUMPTION_SESSION | ${args.entity} | ${args.modo} | ${args.periodo}${esc} | ${args.description}`
    );
    if (opts?.quiet) return;

    console.log();
    console.log(`${GREEN}${BOLD}\u2554${"═".repeat(62)}\u2557${RESET}`);
    console.log(`${GREEN}${BOLD}\u2551${centerText("\ud83d\ude80 CONSUMO \u2014 " + args.entity, 62)}\u2551${RESET}`);
    console.log(`${GREEN}${BOLD}\u255a${"═".repeat(62)}\u255d${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83d\udd27 Modo:${RESET}     ${WHITE}${args.modo}${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83d\udcc5 Per\u00edodo:${RESET}  ${WHITE}${args.periodo}${RESET}`);
    if (args.escopo) {
      console.log(`${CYAN}${BOLD}\ud83d\udc65 Escopo:${RESET}   ${WHITE}${args.escopo}${RESET}`);
    }
    console.log(`${CYAN}${BOLD}\ud83d\udcac A\u00e7\u00e3o:${RESET}   ${WHITE}${args.description}${RESET}`);
    console.log(`${BLUE}${"─".repeat(64)}${RESET}`);
  }

  consumptionStart(consumptionType: string, description: string) {
    console.log();
    console.log(`${GREEN}${BOLD}\u2554${"═".repeat(62)}\u2557${RESET}`);
    console.log(`${GREEN}${BOLD}\u2551${centerText("\ud83d\ude80 INICIANDO CONSUMO \ud83d\ude80", 62)}\u2551${RESET}`);
    console.log(`${GREEN}${BOLD}\u255a${"═".repeat(62)}\u255d${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83d\udccb TIPO: ${WHITE}${consumptionType}${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83d\udcac DESC: ${WHITE}${description}${RESET}`);
    console.log(`${BLUE}${"─".repeat(64)}${RESET}`);
    writeToFile("simple.log", `CONSUMPTION_START | ${consumptionType} | ${description}`);
  }

  consumptionProgress(consumptionType: string, stage: string, current: number, total: number, details?: string) {
    const percentage = total > 0 ? Math.floor((current * 100) / total) : 0;
    const bar = createProgressBar(percentage, 40);
    process.stdout.write(`\r${YELLOW}${BOLD}\u26a1 ${CYAN}${consumptionType}${RESET} ${bar} ${WHITE}${BOLD}${percentage}%${RESET} ${PURPLE}${BOLD}(${current}/${total})${RESET}${details ? ` ${WHITE}${details}${RESET}` : ""}`);
    if (current >= total) console.log();
    writeToFile("simple.log", `PROGRESS_BAR | ${consumptionType} | ${stage} | ${current}/${total} | ${percentage}% | ${details || ""}`);
  }

  consumptionEnd(consumptionType: string, result: string, durationMs: number) {
    const duration = formatDuration(durationMs);
    console.log();
    console.log(`${GREEN}${BOLD}\u2554${"═".repeat(62)}\u2557${RESET}`);
    console.log(`${GREEN}${BOLD}\u2551${centerText("\u2705 CONSUMO FINALIZADO \u2705", 62)}\u2551${RESET}`);
    console.log(`${GREEN}${BOLD}\u255a${"═".repeat(62)}\u255d${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83c\udfaf TIPO: ${WHITE}${consumptionType}${RESET}`);
    console.log(`${CYAN}${BOLD}\ud83c\udf89 RESULTADO: ${WHITE}${result}${RESET}`);
    console.log(`${CYAN}${BOLD}\u23f1\ufe0f DURA\u00c7\u00c3O: ${WHITE}${duration}${RESET}`);
    console.log(`${BLUE}${"─".repeat(64)}${RESET}`);
    console.log();
    writeToFile("simple.log", `CONSUMPTION_END | ${consumptionType} | ${result} | ${duration}`);
  }

  consumptionStartQuiet(consumptionType: string, description: string) {
    writeToFile("simple.log", `CONSUMPTION_START | ${consumptionType} | ${description}`);
  }

  consumptionEndQuiet(consumptionType: string, result: string, durationMs: number, opts?: { silent?: boolean }) {
    if (!opts?.silent) {
      console.log(`${CYAN}${BOLD}[${consumptionType}]${RESET} ${result} (${formatDuration(durationMs)})`);
    }
    writeToFile("simple.log", `CONSUMPTION_END | ${consumptionType} | ${result} | ${formatDuration(durationMs)}`);
  }

  /** GET ao SEFAZ no console (URLs longas truncadas). Usado no scheduler com verboseRequestLog. */
  consumptionHttpGet(url: string) {
    const max = 130;
    const display = url.length <= max ? url : `${url.slice(0, 60)} … ${url.slice(-60)}`;
    console.log(`  ${CYAN}GET${RESET} ${WHITE}${display}${RESET}`);
    writeToFile("simple.log", `HTTP_GET | ${url}`);
  }

  // === Startup links display ===

  displayAvailableLinks(portOverride?: number) {
    const port = portOverride ?? env.PORT;
    const contextPath = env.CONTEXT_PATH === "/" ? "" : env.CONTEXT_PATH;
    const baseUrl = `http://localhost:${port}${contextPath}`;

    console.log();
    console.log("═══════════════════════════════════════════════════════════════");
    console.log("\ud83d\udd17 LINKS DISPON\u00cdVEIS:");
    console.log("═══════════════════════════════════════════════════════════════");
    console.log(`\ud83d\udcda Swagger UI:        ${baseUrl}/swagger-ui`);
    console.log(`\ud83d\udcd6 API Docs:          ${baseUrl}/api-docs`);
    console.log(`\ud83c\udfe0 P\u00e1gina Inicial:    ${baseUrl}/`);
    console.log(`\u2764\ufe0f  Health Check:      ${baseUrl}/health`);
    console.log(`\ud83d\udcca Monitor de Logs:   ${baseUrl}/logs/status`);
    console.log(`\ud83d\udcdd Gerenciamento Logs: ${baseUrl}/api/logs/status`);
    console.log(`\u23f0 Scheduler Info:    ${baseUrl}/scheduler/info`);
    console.log(`\ud83d\udd11 Token Status:      ${baseUrl}/api/token/status`);
    console.log("═══════════════════════════════════════════════════════════════");
    console.log();
  }

  // === Helpers ===

  private formatContext(context: (string | number)[]): string {
    const parts: string[] = [];
    for (let i = 0; i < context.length; i += 2) {
      if (i + 1 < context.length) {
        parts.push(`${context[i]}: ${context[i + 1]}`);
      }
    }
    return parts.join(" | ");
  }
}

class MarkdownSection {
  private title: string;
  private startTime: string;
  private startTimestamp: number;
  private items: string[] = [];

  constructor(title: string) {
    this.title = title;
    this.startTime = formatTime();
    this.startTimestamp = Date.now();
  }

  success(message: string, durationMs?: number): this {
    const duration = durationMs != null ? ` (${formatDuration(durationMs)})` : "";
    this.items.push(`- \u2705 ${message}${duration}`);
    return this;
  }

  progress(message: string): this {
    this.items.push(`- \ud83d\udd04 ${message}`);
    return this;
  }

  warning(message: string): this {
    this.items.push(`- \u26a0\ufe0f ${message}`);
    return this;
  }

  error(message: string): this {
    this.items.push(`- \u274c ${message}`);
    return this;
  }

  info(message: string): this {
    this.items.push(`- \ud83d\udccb ${message}`);
    return this;
  }

  summary(message: string): this {
    const totalDuration = Date.now() - this.startTimestamp;
    this.items.push(`- \u23f1\ufe0f **${message} | Tempo total: ${formatDuration(totalDuration)}**`);
    return this;
  }

  log() {
    const content = `\n## ${this.startTime} | ${this.title}\n${this.items.join("\n")}\n`;
    writeToFile("operations.md", content);
  }

  logWithSummary(totalRecords: number) {
    this.summary(`Total: ${totalRecords} registros`);
    this.log();
  }
}

export const logger = new Logger();
export { formatDuration, formatBytes, formatTime, formatTimestamp };
