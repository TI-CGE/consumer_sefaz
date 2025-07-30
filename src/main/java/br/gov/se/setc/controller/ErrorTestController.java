package br.gov.se.setc.controller;

import br.gov.se.setc.logging.SimpleLogger;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.logging.MarkdownLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Testes de Erro", description = "Endpoints para testar o sistema de logging de erros")
public class ErrorTestController {
    
    @Autowired
    private SimpleLogger simpleLogger;
    
    @Autowired
    private UnifiedLogger unifiedLogger;
    
    @Autowired
    private UserFriendlyLogger userFriendlyLogger;

    @Autowired
    private MarkdownLogger markdownLogger;
    
    /**
     * Simula erro de conexão com API
     */
    @GetMapping("/error/api-connection")
    @Operation(
        summary = "Simula erro de conexão com API",
        description = "Testa o sistema de logging quando ocorre falha de conexão com APIs externas (SEFAZ)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erro simulado com sucesso - verifique os logs"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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
    @Operation(
        summary = "Simula erro de autenticação",
        description = "Testa o sistema de logging quando ocorre falha na autenticação com o SSO SEFAZ"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erro de autenticação simulado - verifique os logs"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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
    @Operation(
        summary = "Simula erro de processamento de dados",
        description = "Testa o sistema de logging quando ocorre falha no processamento/parsing dos dados de contratos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erro de processamento simulado - verifique os logs"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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
    @Operation(
        summary = "Simula erro de banco de dados",
        description = "Testa o sistema de logging quando ocorre falha nas operações de persistência no banco"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Erro de banco simulado - verifique os logs"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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
    @Operation(
        summary = "Testa todos os tipos de erro",
        description = "Executa todos os testes de erro em sequência para validar o sistema completo de logging"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todos os erros simulados com sucesso - verifique os logs"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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

    /**
     * Verifica se os logs estão sendo gravados nos arquivos corretos
     */
    @GetMapping("/verify-logs")
    @Operation(
        summary = "Verifica arquivos de log",
        description = "Mostra o status dos arquivos de log e suas últimas entradas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status dos logs verificado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public String verifyLogs() {
        StringBuilder result = new StringBuilder("📁 Status dos Arquivos de Log:\n\n");

        try {
            java.io.File logsDir = new java.io.File("logs");
            if (!logsDir.exists()) {
                result.append("❌ Diretório 'logs' não encontrado\n");
                return result.toString();
            }

            // Verificar arquivos de log
            String[] logFiles = {"simple.log", "errors.log", "application.log", "operations.md"};

            for (String fileName : logFiles) {
                java.io.File file = new java.io.File(logsDir, fileName);
                if (file.exists()) {
                    long size = file.length();
                    String sizeStr = size > 1024 ? (size / 1024) + " KB" : size + " bytes";
                    result.append("✅ ").append(fileName).append(" - ").append(sizeStr).append("\n");
                } else {
                    result.append("❌ ").append(fileName).append(" - Não encontrado\n");
                }
            }

            result.append("\n🧪 Executando teste de erro para verificar gravação...\n");

            // Executar um erro de teste
            simpleLogger.error("LOG_TEST", "Teste de verificação de logs",
                    new RuntimeException("Erro de teste para verificação"));

            // Testar MarkdownLogger diretamente
            userFriendlyLogger.logError("teste markdown", "Erro simulado para markdown");

            result.append("✅ Erro de teste executado!\n");
            result.append("\n📋 Verifique os arquivos:\n");
            result.append("- logs/simple.log (deve conter o erro)\n");
            result.append("- logs/errors.log (deve conter o stack trace)\n");
            result.append("- logs/application.log (deve conter log técnico)\n");

        } catch (Exception e) {
            result.append("❌ Erro ao verificar logs: ").append(e.getMessage());
        }

        return result.toString();
    }

    /**
     * Testa especificamente o log de erro em markdown
     */
    @GetMapping("/error/markdown-test")
    @Operation(
        summary = "Testa log de erro em markdown",
        description = "Testa se os erros estão sendo gravados no arquivo operations.md"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teste de markdown executado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public String testMarkdownError() {
        try {
            // Usar MarkdownLogger diretamente para testar
            markdownLogger.logError("Teste de Erro", "Erro simulado para testar markdown",
                    new RuntimeException("Erro de teste markdown"));

            // Também testar com SimpleLogger (se as mudanças foram aplicadas)
            simpleLogger.error("MARKDOWN_TEST", "Teste de erro para markdown",
                    new RuntimeException("Erro para testar markdown"));

            return "Teste de erro em markdown executado!\n" +
                   "Verifique:\n" +
                   "- logs/operations.md (deve conter erro estruturado)\n" +
                   "- logs/simple.log (deve conter erro simples)\n" +
                   "- logs/errors.log (deve conter stack trace)";

        } catch (Exception e) {
            return "Erro ao executar teste: " + e.getMessage();
        }
    }
}
