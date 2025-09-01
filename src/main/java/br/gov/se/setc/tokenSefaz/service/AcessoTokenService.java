package br.gov.se.setc.tokenSefaz.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
@Service
public class AcessoTokenService {
    private final RestTemplate restTemplate;
    private String cachedToken;
    private long tokenExpirationTime;
    private static final long TOKEN_VALIDITY_DURATION = 3600000; // 1 hora em millisegundos
    private static final long TOKEN_REFRESH_BUFFER = 300000; // 5 minutos de buffer antes da expiração
    @Autowired
    private UnifiedLogger unifiedLogger;
    @Autowired
    private UserFriendlyLogger userFriendlyLogger;
    @Autowired
    public AcessoTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.cachedToken = null;
        this.tokenExpirationTime = 0;
    }
    @LogOperation(operation = "GET_TOKEN", component = "SECURITY", logParameters = false, logResult = false, slowOperationThresholdMs = 5000)
    public String getToken() {
        if (isTokenValid()) {
            unifiedLogger.logOperationStart("SECURITY", "TOKEN_CACHE_HIT", "CACHED_TOKEN", "REUSING_VALID_TOKEN");
            userFriendlyLogger.logInfo("Reutilizando token válido em cache");
            return cachedToken;
        }
        return requestNewToken();
    }
    /**
     * Verifica se o token em cache ainda é válido
     */
    private boolean isTokenValid() {
        if (cachedToken == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long timeUntilExpiration = tokenExpirationTime - currentTime;
        return timeUntilExpiration > TOKEN_REFRESH_BUFFER;
    }
    /**
     * Solicita um novo token da API com retry automático
     */
    private String requestNewToken() {
        String clientId = "87f72053";
        String clientSecret = "44019d983f556130ee774f1a36e5bcc2";
        String grantType = "client_credentials";
        String tokenUrl = "https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token";
        MDCUtil.setComponent("SECURITY");
        MDCUtil.setOperation("GET_TOKEN");
        userFriendlyLogger.logAuthenticationStart();
        unifiedLogger.logOperationStart("SECURITY", "GET_NEW_TOKEN", "CLIENT_ID", clientId, "URL", tokenUrl);
        int maxRetries = 3;
        long baseDelay = 1000; // 1 segundo
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            long startTime = System.currentTimeMillis();
            try {
                String body = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=" + grantType;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("User-Agent", "SEFAZ-Consumer/1.0");
                headers.set("Connection", "close"); // Evitar problemas de keep-alive
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                unifiedLogger.logOperationStart("SECURITY", "TOKEN_REQUEST_ATTEMPT",
                    "ATTEMPT", String.valueOf(attempt), "MAX_RETRIES", String.valueOf(maxRetries));
                ResponseEntity<String> response = restTemplate.exchange(
                        tokenUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );
                long responseTime = System.currentTimeMillis() - startTime;
                if (response.getStatusCode() == HttpStatus.OK) {
                    String token = extractToken(response.getBody());
                    cacheToken(token);
                    userFriendlyLogger.logAuthenticationSuccess();
                    unifiedLogger.logAuthentication(clientId, tokenUrl, true, responseTime, MDCUtil.getCorrelationId());
                    return token;
                } else {
                    String errorMsg = "HTTP " + response.getStatusCode() + " na tentativa " + attempt;
                    RuntimeException httpError = new RuntimeException(errorMsg);
                    unifiedLogger.logOperationError("SECURITY", "TOKEN_REQUEST_FAILED", responseTime, httpError,
                        "ATTEMPT", String.valueOf(attempt), "STATUS", response.getStatusCode().toString());
                    if (attempt == maxRetries) {
                        userFriendlyLogger.logAuthenticationError();
                        unifiedLogger.logAuthentication(clientId, tokenUrl, false, responseTime, MDCUtil.getCorrelationId());
                        throw new RuntimeException("Erro ao obter token após " + maxRetries + " tentativas: " + response.getStatusCode());
                    }
                }
            } catch (Exception e) {
                long responseTime = System.currentTimeMillis() - startTime;
                String errorMsg = "Erro na tentativa " + attempt + ": " + e.getMessage();
                unifiedLogger.logOperationError("SECURITY", "TOKEN_REQUEST_EXCEPTION", responseTime, e,
                    "ATTEMPT", String.valueOf(attempt), "ERROR", e.getClass().getSimpleName());
                if (attempt == maxRetries) {
                    userFriendlyLogger.logAuthenticationError();
                    unifiedLogger.logAuthentication(clientId, tokenUrl, false, responseTime, MDCUtil.getCorrelationId());
                    throw new RuntimeException("Erro ao obter token após " + maxRetries + " tentativas: " + e.getMessage(), e);
                }
            }
            if (attempt < maxRetries) {
                long delay = baseDelay * (long) Math.pow(2, attempt - 1);
                unifiedLogger.logOperationStart("SECURITY", "TOKEN_RETRY_DELAY",
                    "DELAY_MS", String.valueOf(delay), "NEXT_ATTEMPT", String.valueOf(attempt + 1));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrompido durante retry de token", ie);
                }
            }
        }
        throw new RuntimeException("Falha inesperada ao obter token");
    }
    /**
     * Armazena o token em cache com timestamp de expiração
     */
    private void cacheToken(String token) {
        this.cachedToken = token;
        this.tokenExpirationTime = System.currentTimeMillis() + TOKEN_VALIDITY_DURATION;
        unifiedLogger.logOperationSuccess("SECURITY", "TOKEN_CACHED", 
            0, 1, "EXPIRATION_TIME", String.valueOf(tokenExpirationTime));
    }
    private String extractToken(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta do token", e);
        }
    }
    /**
     * Testa a conectividade com o servidor de autenticação
     */
    public boolean testConnectivity() {
        String tokenUrl = "https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token";
        long startTime = System.currentTimeMillis();
        try {
            unifiedLogger.logOperationStart("SECURITY", "CONNECTIVITY_TEST", "URL", tokenUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "SEFAZ-Consumer/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            long responseTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationSuccess("SECURITY", "CONNECTIVITY_TEST",
                responseTime, 1, "STATUS", response.getStatusCode().toString());
            return true;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationError("SECURITY", "CONNECTIVITY_TEST", responseTime, e,
                "ERROR", e.getClass().getSimpleName(), "MESSAGE", e.getMessage());
            return false;
        }
    }
    /**
     * Força a renovação do token (limpa cache e solicita novo)
     */
    public String forceTokenRenewal() {
        unifiedLogger.logOperationStart("SECURITY", "FORCE_TOKEN_RENEWAL", "REASON", "MANUAL_REQUEST");
        this.cachedToken = null;
        this.tokenExpirationTime = 0;
        return requestNewToken();
    }
    /**
     * Retorna informações sobre o status do token
     */
    public String getTokenStatus() {
        if (cachedToken == null) {
            return "Token não existe em cache";
        }
        long currentTime = System.currentTimeMillis();
        long timeUntilExpiration = tokenExpirationTime - currentTime;
        if (timeUntilExpiration <= 0) {
            return "Token expirado há " + Math.abs(timeUntilExpiration / 1000) + " segundos";
        } else if (timeUntilExpiration <= TOKEN_REFRESH_BUFFER) {
            return "Token expira em " + (timeUntilExpiration / 1000) + " segundos (dentro do buffer de renovação)";
        } else {
            return "Token válido por mais " + (timeUntilExpiration / 1000) + " segundos";
        }
    }
}
