package br.gov.se.setc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final Logger logger = Logger.getLogger(HealthController.class.getName());
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private AcessoTokenService acessoTokenService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test basic application health
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            
            // Test RestTemplate bean
            health.put("restTemplate", restTemplate != null ? "OK" : "MISSING");
            
            // Test AcessoTokenService bean
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
