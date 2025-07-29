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

    @Autowired
    private UnifiedLogger unifiedLogger;

    @Autowired
    private UserFriendlyLogger userFriendlyLogger;

    @Autowired
    public AcessoTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LogOperation(operation = "GET_TOKEN", component = "SECURITY", logParameters = false, logResult = false, slowOperationThresholdMs = 5000)
    public String getToken() {
        String clientId = "87f72053";
        String clientSecret = "ee84146bb06f9fc7e6aca34bb84ca1a2";
        String grantType = "client_credentials";
        String tokenUrl = "https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token";

        // Configurar contexto de segurança
        MDCUtil.setComponent("SECURITY");
        MDCUtil.setOperation("GET_TOKEN");

        long startTime = System.currentTimeMillis();

        // Log simples para usuário
        userFriendlyLogger.logAuthenticationStart();

        // Log técnico para arquivo
        unifiedLogger.logOperationStart("SECURITY", "GET_TOKEN", "CLIENT_ID", clientId, "URL", tokenUrl);

        String body = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=" + grantType;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long responseTime = System.currentTimeMillis() - startTime;

            if (response.getStatusCode() == HttpStatus.OK) {
                String token = extractToken(response.getBody());

                // Log simples para usuário
                userFriendlyLogger.logAuthenticationSuccess();

                // Log técnico para arquivo
                unifiedLogger.logAuthentication(clientId, tokenUrl, true, responseTime, MDCUtil.getCorrelationId());

                return token;
            } else {
                // Log simples para usuário
                userFriendlyLogger.logAuthenticationError();

                // Log técnico para arquivo
                unifiedLogger.logAuthentication(clientId, tokenUrl, false, responseTime, MDCUtil.getCorrelationId());
                throw new RuntimeException("Erro ao obter token: " + response.getStatusCode());
            }

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            // Log simples para usuário
            userFriendlyLogger.logAuthenticationError();

            // Log técnico para arquivo
            unifiedLogger.logAuthentication(clientId, tokenUrl, false, responseTime, MDCUtil.getCorrelationId());
            throw new RuntimeException("Erro ao obter token: " + e.getMessage(), e);
        }
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
}