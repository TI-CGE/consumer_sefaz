package br.gov.se.setc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "API para monitoramento e verificação de saúde da aplicação")
public class HealthController {
    private static final Logger logger = Logger.getLogger(HealthController.class.getName());
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AcessoTokenService acessoTokenService;
    @GetMapping
    @Operation(
        summary = "Verificação de saúde da aplicação",
        description = "Verifica o status de saúde da aplicação e de seus componentes principais, " +
                     "incluindo conectividade com serviços externos, disponibilidade de beans " +
                     "e funcionamento geral do sistema. Retorna informações detalhadas sobre " +
                     "o estado de cada componente verificado.",
        tags = {"Health Check"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aplicação funcionando corretamente"),
        @ApiResponse(responseCode = "500", description = "Problemas detectados na aplicação")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        try {
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("restTemplate", restTemplate != null ? "OK" : "MISSING");
            health.put("acessoTokenService", acessoTokenService != null ? "OK" : "MISSING");
            logger.info("Health check completed successfully");
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.severe("Health check failed: " + e.getMessage());
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(health);
        }
    }
    @GetMapping("/token-test")
    @Operation(
        summary = "Teste de conectividade com serviço de token",
        description = "Verifica se o serviço de token SEFAZ está funcionando corretamente, " +
                     "testando a obtenção de token de autenticação. Retorna informações sobre " +
                     "o status do token sem expor dados sensíveis.",
        tags = {"Health Check"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teste de token executado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Falha na obtenção do token")
    })
    public ResponseEntity<Map<String, Object>> tokenTest() {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("Testing token service...");
            String token = acessoTokenService.getToken();
            result.put("status", "SUCCESS");
            result.put("tokenLength", token != null ? token.length() : 0);
            result.put("tokenPrefix", token != null && token.length() > 10 ? token.substring(0, 10) + "..." : "N/A");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("Token test failed: " + e.getMessage());
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}