import { env } from "../env.js";
import { logger } from "../logging/logger.js";

let cachedToken: string | null = null;
let tokenExpirationTime = 0;
const TOKEN_VALIDITY_DURATION = 3600000; // 1 hour
const TOKEN_REFRESH_BUFFER = 300000; // 5 minutes

function isTokenValid(): boolean {
  if (!cachedToken) return false;
  const timeUntilExpiration = tokenExpirationTime - Date.now();
  return timeUntilExpiration > TOKEN_REFRESH_BUFFER;
}

function cacheToken(token: string) {
  cachedToken = token;
  tokenExpirationTime = Date.now() + TOKEN_VALIDITY_DURATION;
  logger.logOperationSuccess("SECURITY", "TOKEN_CACHED", 0, 1, "EXPIRATION_TIME", String(tokenExpirationTime));
}

async function requestNewToken(): Promise<string> {
  const { SEFAZ_CLIENT_ID: clientId, SEFAZ_CLIENT_SECRET: clientSecret, SEFAZ_TOKEN_URL: tokenUrl } = env;

  logger.setContext("SECURITY", "GET_TOKEN");
  logger.logAuthenticationStart();
  logger.logOperationStart("SECURITY", "GET_NEW_TOKEN", "CLIENT_ID", clientId, "URL", tokenUrl);

  const maxRetries = 3;
  const baseDelay = 1000;

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    const startTime = Date.now();
    try {
      const body = new URLSearchParams({
        client_id: clientId,
        client_secret: clientSecret,
        grant_type: "client_credentials",
      }).toString();

      logger.logOperationStart("SECURITY", "TOKEN_REQUEST_ATTEMPT", "ATTEMPT", attempt, "MAX_RETRIES", maxRetries);

      const response = await fetch(tokenUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          "User-Agent": "SEFAZ-Consumer/1.0",
          Connection: "close",
        },
        body,
      });

      const responseTime = Date.now() - startTime;

      if (response.ok) {
        const data = await response.json() as { access_token: string };
        const token = data.access_token;
        cacheToken(token);
        logger.logAuthenticationSuccess();
        logger.logAuthentication(clientId, tokenUrl, true, responseTime, logger.getCorrelationId());
        return token;
      }

      const errorMsg = `HTTP ${response.status} na tentativa ${attempt}`;
      logger.logOperationError("SECURITY", "TOKEN_REQUEST_FAILED", responseTime, new Error(errorMsg), "ATTEMPT", attempt, "STATUS", response.status);

      if (attempt === maxRetries) {
        logger.logAuthenticationError();
        logger.logAuthentication(clientId, tokenUrl, false, responseTime, logger.getCorrelationId());
        throw new Error(`Erro ao obter token após ${maxRetries} tentativas: ${response.status}`);
      }
    } catch (e) {
      const responseTime = Date.now() - startTime;
      if (e instanceof Error && e.message.startsWith("Erro ao obter token")) throw e;

      logger.logOperationError("SECURITY", "TOKEN_REQUEST_EXCEPTION", responseTime, e, "ATTEMPT", attempt, "ERROR", e instanceof Error ? e.constructor.name : "Unknown");

      if (attempt === maxRetries) {
        logger.logAuthenticationError();
        logger.logAuthentication(clientId, tokenUrl, false, responseTime, logger.getCorrelationId());
        throw new Error(`Erro ao obter token após ${maxRetries} tentativas: ${e instanceof Error ? e.message : String(e)}`, {
          cause: e,
        });
      }
    }

    if (attempt < maxRetries) {
      const delay = baseDelay * Math.pow(2, attempt - 1);
      logger.logOperationStart("SECURITY", "TOKEN_RETRY_DELAY", "DELAY_MS", delay, "NEXT_ATTEMPT", attempt + 1);
      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }

  throw new Error("Falha inesperada ao obter token");
}

export async function getToken(): Promise<string> {
  if (isTokenValid()) {
    // Evita poluir o terminal: cada requisição à API chamava getToken() (centenas de linhas).
    // Detalhe permanece em application.log via logOperationStart se necessário.
    return cachedToken!;
  }
  return requestNewToken();
}

export async function forceTokenRenewal(): Promise<string> {
  logger.logOperationStart("SECURITY", "FORCE_TOKEN_RENEWAL", "REASON", "MANUAL_REQUEST");
  cachedToken = null;
  tokenExpirationTime = 0;
  return requestNewToken();
}

export async function testConnectivity(): Promise<boolean> {
  const tokenUrl = env.SEFAZ_TOKEN_URL;
  const startTime = Date.now();
  try {
    logger.logOperationStart("SECURITY", "CONNECTIVITY_TEST", "URL", tokenUrl);
    const response = await fetch(tokenUrl, {
      method: "POST",
      headers: { "User-Agent": "SEFAZ-Consumer/1.0" },
    });
    const responseTime = Date.now() - startTime;
    logger.logOperationSuccess("SECURITY", "CONNECTIVITY_TEST", responseTime, 1, "STATUS", String(response.status));
    return true;
  } catch (e) {
    const responseTime = Date.now() - startTime;
    logger.logOperationError("SECURITY", "CONNECTIVITY_TEST", responseTime, e, "ERROR", e instanceof Error ? e.constructor.name : "Unknown");
    return false;
  }
}

export function getTokenStatus(): string {
  if (!cachedToken) return "Token não existe em cache";
  const timeUntilExpiration = tokenExpirationTime - Date.now();
  if (timeUntilExpiration <= 0) {
    return `Token expirado há ${Math.abs(Math.floor(timeUntilExpiration / 1000))} segundos`;
  }
  if (timeUntilExpiration <= TOKEN_REFRESH_BUFFER) {
    return `Token expira em ${Math.floor(timeUntilExpiration / 1000)} segundos (dentro do buffer de renovação)`;
  }
  return `Token válido por mais ${Math.floor(timeUntilExpiration / 1000)} segundos`;
}
