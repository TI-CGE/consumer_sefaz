package br.gov.se.setc.controller;

import br.gov.se.setc.logging.SimpleLogger;
import br.gov.se.setc.logging.MarkdownLogger;
import br.gov.se.setc.logging.SimplifiedLoggingExample;
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
