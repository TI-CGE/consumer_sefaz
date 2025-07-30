package br.gov.se.setc.controller;

import br.gov.se.setc.logging.SimpleLogger;
import br.gov.se.setc.logging.MarkdownLogger;
import br.gov.se.setc.logging.SimplifiedLoggingExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para testar o sistema de logging simplificado.
 * Acesse /api/test/logging para ver os logs em ação.
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "Testes de Logging", description = "Endpoints para testar o sistema de logging simplificado")
public class LoggingTestController {
    
    @Autowired
    private SimpleLogger simpleLogger;
    
    @Autowired
    private MarkdownLogger markdownLogger;
    
    @Autowired
    private SimplifiedLoggingExample example;
    
    /**
     * Testa o sistema de logging simplificado
     */
    @GetMapping("/logging")
    @Operation(
        summary = "Testa o sistema de logging simplificado",
        description = "Executa testes do SimpleLogger e MarkdownLogger para demonstrar o funcionamento do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Testes executados com sucesso - verifique os arquivos de log"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public String testLogging() {
        simpleLogger.info("TEST", "Iniciando teste do sistema de logging");
        
        // Teste de logs simples
        simpleLogger.start("TEST", "Executando testes");
        simpleLogger.success("TEST", "Logs básicos funcionando");
        simpleLogger.warning("TEST", "Este é um aviso de teste");
        
        // Teste de log markdown
        MarkdownLogger.MarkdownSection section = markdownLogger.startSection("Teste do Sistema");
        section.success("SimpleLogger configurado")
               .success("MarkdownLogger configurado")
               .info("Todos os componentes funcionando")
               .logWithSummary(3);
        
        // Executar exemplos
        example.exemploOperacaoSimples();
        example.exemploOperacaoComplexa();
        
        simpleLogger.success("TEST", "Teste concluído com sucesso");
        
        return "Teste de logging executado! Verifique os arquivos:\n" +
               "- logs/simple.log (logs limpos)\n" +
               "- logs/operations.md (logs estruturados)\n" +
               "- logs/application.log (logs técnicos)";
    }
}
