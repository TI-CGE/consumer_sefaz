package br.gov.se.setc.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Exemplo de como usar o sistema de logging simplificado.
 * Esta classe demonstra os diferentes tipos de logs disponíveis.
 */
@Component
public class SimplifiedLoggingExample {
    
    @Autowired
    private SimpleLogger simpleLogger;
    
    @Autowired
    private MarkdownLogger markdownLogger;
    
    /**
     * Exemplo de operação simples com logs básicos
     */
    public void exemploOperacaoSimples() {
        long startTime = System.currentTimeMillis();
        
        // Log de início
        simpleLogger.start("EXEMPLO", "Processamento de dados");
        
        try {
            // Simular processamento
            Thread.sleep(1000);
            
            // Log de progresso
            simpleLogger.progress("EXEMPLO", "Validando dados", 50, 100);
            
            Thread.sleep(500);
            
            // Log de sucesso
            long duration = System.currentTimeMillis() - startTime;
            simpleLogger.success("EXEMPLO", "Dados processados com sucesso", duration);
            
        } catch (Exception e) {
            // Log de erro
            simpleLogger.error("EXEMPLO", "Falha no processamento", e);
        }
    }
    
    /**
     * Exemplo de operação complexa com logs em markdown
     */
    public void exemploOperacaoComplexa() {
        // Criar seção de log estruturado
        MarkdownLogger.MarkdownSection section = markdownLogger.startSection("Execução do Scheduler");
        
        try {
            // Etapa 1: Autenticação
            long authStart = System.currentTimeMillis();
            Thread.sleep(200); // Simular autenticação
            section.success("Autenticação realizada", System.currentTimeMillis() - authStart);
            
            // Etapa 2: Buscar dados
            section.progress("Buscando unidades gestoras...");
            Thread.sleep(1000); // Simular busca
            section.success("119 unidades gestoras encontradas", 1000);
            
            // Etapa 3: Processar dados
            section.progress("Processando contratos fiscais...");
            Thread.sleep(300); // Simular processamento
            section.success("2 contratos fiscais processados", 300);
            
            // Finalizar com resumo
            section.logWithSummary(121);
            
        } catch (Exception e) {
            section.error("Falha na execução: " + e.getMessage());
            section.log();
        }
    }
    
    /**
     * Exemplo de logs de diferentes níveis
     */
    public void exemploNiveisDeLog() {
        // Informação simples
        simpleLogger.info("SISTEMA", "Aplicação iniciada");
        
        // Sucesso
        simpleLogger.success("API", "Requisição processada");
        
        // Aviso
        simpleLogger.warning("DATABASE", "Conexão lenta detectada");
        
        // Erro
        simpleLogger.error("NETWORK", "Falha de conectividade");
        
        // Operação lenta
        simpleLogger.slow("BATCH", "Processamento em lote finalizado", 8000);
    }
    
    /**
     * Exemplo de log markdown simples
     */
    public void exemploMarkdownSimples() {
        markdownLogger.logSimple("Inicialização", "Sistema iniciado com sucesso");
        
        markdownLogger.logError("Falha Crítica", "Erro de conexão com banco", 
                new RuntimeException("Connection timeout"));
    }
}
