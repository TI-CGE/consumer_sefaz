import { query } from "../db/client.js";
import { logger } from "./logger.js";

export async function saveErrorLog(
  component: string,
  message: string,
  error?: Error | unknown,
  operation?: string
) {
  try {
    const correlationId = logger.getCorrelationId();
    const ugCode = extractUgCodeFromMessage(message);
    const contractId = extractContractIdFromMessage(message);
    const apiUrl = extractFullApiUrl(message);
    const apiEndpoint = apiUrl ? apiUrl.split("?")[0]?.substring(0, 500) : null;
    const httpStatusCode = extractHttpStatusCode(message);
    const severity = determineSeverity(error, message);
    const errorCategory = determineErrorCategory(component, message);
    const errorCode = extractErrorCode(message, error);

    let exceptionType: string | null = null;
    let exceptionMessage: string | null = null;
    let stackTrace: string | null = null;
    let rootCause: string | null = null;

    if (error instanceof Error) {
      exceptionType = error.constructor.name;
      exceptionMessage = error.message;
      stackTrace = error.stack?.substring(0, 10000) ?? null;
      let cause = error.cause;
      while (cause && cause instanceof Error && cause.cause && cause.cause !== cause) {
        cause = cause.cause;
      }
      if (cause instanceof Error) {
        rootCause = `${cause.constructor.name}: ${cause.message}`.substring(0, 1000);
      }
    }

    await query(
      `INSERT INTO consumer_sefaz.error_log (
        timestamp, component, operation, message, correlation_id,
        ug_code, contract_id, request_id, api_url_completa,
        api_endpoint, http_status_code, severity, error_category,
        error_code, exception_type, exception_message, stack_trace, root_cause
      ) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$17,$18)`,
      [
        new Date(), component, operation || null, message, correlationId,
        ugCode, contractId, correlationId, apiUrl,
        apiEndpoint, httpStatusCode, severity, errorCategory,
        errorCode, exceptionType, exceptionMessage, stackTrace, rootCause,
      ]
    );
  } catch (e) {
    // Avoid infinite recursion
    console.error("Erro ao salvar log de erro no banco de dados", e);
  }
}

function extractUgCodeFromMessage(message: string | null): string | null {
  if (!message) return null;
  const m1 = message.match(/cdUnidadeGestora=(\d{6})/);
  if (m1) return m1[1];
  const m2 = message.match(/UG[\s:]+(\d{6})|ugCd[\s:]+(\d{6})|Processando UG\s+(\d{6})|para UG[\s:]+(\d{6})/);
  if (m2) return m2[1] || m2[2] || m2[3] || m2[4];
  return null;
}

function extractContractIdFromMessage(message: string | null): string | null {
  if (!message) return null;
  const m = message.match(/cdContrato[=:]+([^&\s]+)|contractId[=:]+([^&\s]+)|sqContrato[=:]+([^&\s]+)/);
  if (m) {
    const val = m[1] || m[2] || m[3];
    return val ? val.substring(0, 50) : null;
  }
  return null;
}

function extractFullApiUrl(message: string | null): string | null {
  if (!message) return null;
  const m = message.match(/https?:\/\/[^\s]+/);
  return m ? m[0].substring(0, 2000) : null;
}

function extractHttpStatusCode(message: string | null): number | null {
  if (!message) return null;
  const m = message.match(/STATUS:\s*(\d{3})|HTTP\s+(\d{3})/i);
  if (m) {
    const code = m[1] || m[2];
    return code ? parseInt(code) : null;
  }
  for (const code of [500, 400, 404, 403, 401]) {
    if (message.includes(String(code))) return code;
  }
  return null;
}

function determineSeverity(error: Error | unknown, message: string | null): string {
  if (error instanceof Error) {
    const name = error.constructor.name;
    if (name.includes("Timeout") || name.includes("Connection")) return "HIGH";
    if (name.includes("NullPointer") || name.includes("TypeError")) return "CRITICAL";
  }
  if (message) {
    const msg = message.toUpperCase();
    if (msg.includes("TIMEOUT") || msg.includes("CONNECTION")) return "HIGH";
    if (msg.includes("500") || msg.includes("INTERNAL SERVER ERROR")) return "HIGH";
  }
  return "MEDIUM";
}

function determineErrorCategory(component: string | null, message: string | null): string {
  if (component) {
    if (component.includes("API") || component.includes("CLIENT")) return "API_ERROR";
    if (component.includes("DATABASE") || component.includes("DB")) return "DATABASE_ERROR";
    if (component.includes("VALIDATION")) return "VALIDATION_ERROR";
  }
  if (message) {
    const msg = message.toUpperCase();
    if (msg.includes("API") || msg.includes("HTTP")) return "API_ERROR";
    if (msg.includes("DATABASE") || msg.includes("SQL") || msg.includes("CONNECTION")) return "DATABASE_ERROR";
  }
  return "GENERAL_ERROR";
}

function extractErrorCode(message: string | null, error: Error | unknown): string | null {
  if (error instanceof Error) {
    const name = error.constructor.name;
    if (name.includes("Timeout")) return "TIMEOUT";
    if (name.includes("Connection") || name.includes("Connect")) return "CONNECTION_ERROR";
    if (name === "TypeError") return "TYPE_ERROR";
  }
  if (message) {
    if (message.includes("500")) return "HTTP_500";
    if (message.includes("400")) return "HTTP_400";
    if (message.includes("404")) return "HTTP_404";
    if (message.includes("403")) return "HTTP_403";
    if (message.includes("401")) return "HTTP_401";
  }
  return null;
}
