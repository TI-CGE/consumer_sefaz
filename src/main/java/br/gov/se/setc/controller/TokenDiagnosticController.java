package br.gov.se.setc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import java.util.HashMap;
import java.util.Map;
/**
 * Controller para diagnóstico e gerenciamento de tokens
 */
@RestController
@RequestMapping("/api/token")
public class TokenDiagnosticController {
    @Autowired
    private AcessoTokenService acessoTokenService;
    /**
     * Verifica o status atual do token
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTokenStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            String status = acessoTokenService.getTokenStatus();
            response.put("status", "success");
            response.put("tokenStatus", status);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    /**
     * Testa a conectividade com o servidor de autenticação
     */
    @GetMapping("/connectivity")
    public ResponseEntity<Map<String, Object>> testConnectivity() {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isConnected = acessoTokenService.testConnectivity();
            response.put("status", "success");
            response.put("connected", isConnected);
            response.put("message", isConnected ? "Conectividade OK" : "Falha na conectividade");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("connected", false);
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    /**
     * Força a renovação do token
     */
    @PostMapping("/renew")
    public ResponseEntity<Map<String, Object>> renewToken() {
        Map<String, Object> response = new HashMap<>();
        try {
            String newToken = acessoTokenService.forceTokenRenewal();
            response.put("status", "success");
            response.put("message", "Token renovado com sucesso");
            response.put("tokenPreview", newToken.substring(0, Math.min(20, newToken.length())) + "...");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    /**
     * Obtém um token (usa cache se válido, senão solicita novo)
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getToken() {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = acessoTokenService.getToken();
            response.put("status", "success");
            response.put("message", "Token obtido com sucesso");
            response.put("tokenPreview", token.substring(0, Math.min(20, token.length())) + "...");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    /**
     * Endpoint de diagnóstico completo
     */
    @GetMapping("/diagnostic")
    public ResponseEntity<Map<String, Object>> fullDiagnostic() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> diagnostics = new HashMap<>();
        try {
            boolean connectivity = acessoTokenService.testConnectivity();
            diagnostics.put("connectivity", connectivity);
            String tokenStatus = acessoTokenService.getTokenStatus();
            diagnostics.put("tokenStatus", tokenStatus);
            try {
                String token = acessoTokenService.getToken();
                diagnostics.put("tokenObtained", true);
                diagnostics.put("tokenPreview", token.substring(0, Math.min(20, token.length())) + "...");
            } catch (Exception e) {
                diagnostics.put("tokenObtained", false);
                diagnostics.put("tokenError", e.getMessage());
            }
            response.put("status", "success");
            response.put("diagnostics", diagnostics);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}