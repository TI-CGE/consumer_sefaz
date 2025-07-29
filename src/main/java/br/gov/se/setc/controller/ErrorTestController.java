package br.gov.se.setc.controller;

import br.gov.se.setc.logging.SimpleLogger;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para testar o sistema de logs de erro.
 * Simula diferentes tipos de erros que podem ocorrer na busca de contratos.
 */
@RestController
@RequestMapping("/api/test")
public class ErrorTestController {
    
    @Autowired
    private SimpleLogger simpleLogger;
    
    @Autowired
    private UnifiedLogger unifiedLogger;
    
    @Autowired
    private UserFriendlyLogger userFriendlyLogger;
    
    /**
     * Simula erro de conexão com API
     */
    @GetMapping("/error/api-connection")
    public String testApiConnectionError() {
        long startTime = System.currentTimeMillis();
        
        try {
            simpleLogger.start("CONTRACT_CONSUMER", "Testando erro de conexão");
            
            // Simular erro de conexão
            throw new RuntimeException("Connection timeout: Unable to connect to SEFAZ API");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log simples
            simpleLogger.error("CONTRACT_CONSUMER", "Falha na conexão com API", e);
            
            // Log técnico
            unifiedLogger.logOperationError("CONTRACT_CONSUMER", "CONSUMIR_CONTRATOS", 
                    duration, e, "ENDPOINT", "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais");
            
            // Log amigável
            userFriendlyLogger.logConnectionError("API SEFAZ");
            
            return "Erro de conexão simulado! Verifique os logs:\n" +
                   "- logs/errors.log (erro completo)\n" +
                   "- logs/simple.log (erro simplificado)\n" +
                   "- Console (mensagem amigável)";
        }
    }
    
    /**
     * Simula erro de autenticação
     */
    @GetMapping("/error/authentication")
    public String testAuthenticationError() {
        long startTime = System.currentTimeMillis();
        
        try {
            simpleLogger.start("SECURITY", "Testando erro de autenticação");
            
            // Simular erro de autenticação
            throw new SecurityException("Invalid credentials: Authentication failed");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            simpleLogger.error("SECURITY", "Falha na autenticação", e);
            unifiedLogger.logOperationError("SECURITY", "GET_TOKEN", duration, e);
            userFriendlyLogger.logAuthenticationError();
            
            return "Erro de autenticação simulado! Verifique os logs.";
        }
    }
    
    /**
     * Simula erro de processamento de dados
     */
    @GetMapping("/error/data-processing")
    public String testDataProcessingError() {
        long startTime = System.currentTimeMillis();
        
        try {
            simpleLogger.start("CONTRACT_CONSUMER", "Testando erro de processamento");
            
            // Simular erro de processamento
            throw new IllegalArgumentException("Invalid data format: Unable to parse contract data");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            simpleLogger.error("CONTRACT_CONSUMER", "Falha no processamento de dados", e);
            unifiedLogger.logOperationError("CONTRACT_CONSUMER", "PROCESSAR_DADOS", duration, e);
            userFriendlyLogger.logError("processamento de contratos", e.getMessage());
            
            return "Erro de processamento simulado! Verifique os logs.";
        }
    }
    
    /**
     * Simula erro de banco de dados
     */
    @GetMapping("/error/database")
    public String testDatabaseError() {
        long startTime = System.currentTimeMillis();
        
        try {
            simpleLogger.start("DATABASE", "Testando erro de banco");
            
            // Simular erro de banco
            throw new RuntimeException("Database connection failed: Unable to insert contracts");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            simpleLogger.error("DATABASE", "Falha na operação de banco", e);
            unifiedLogger.logOperationError("DATABASE", "INSERT_CONTRATOS", duration, e);
            userFriendlyLogger.logError("salvamento no banco", "Falha de conexão");
            
            return "Erro de banco simulado! Verifique os logs.";
        }
    }
    
    /**
     * Testa todos os tipos de erro em sequência
     */
    @GetMapping("/error/all")
    public String testAllErrors() {
        StringBuilder result = new StringBuilder("Testando todos os tipos de erro:\n\n");
        
        try { testApiConnectionError(); } catch (Exception ignored) {}
        result.append("✓ Erro de API testado\n");
        
        try { testAuthenticationError(); } catch (Exception ignored) {}
        result.append("✓ Erro de autenticação testado\n");
        
        try { testDataProcessingError(); } catch (Exception ignored) {}
        result.append("✓ Erro de processamento testado\n");
        
        try { testDatabaseError(); } catch (Exception ignored) {}
        result.append("✓ Erro de banco testado\n");
        
        result.append("\nTodos os erros foram simulados! Verifique os arquivos de log.");
        
        return result.toString();
    }
}
