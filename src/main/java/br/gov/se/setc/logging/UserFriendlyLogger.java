package br.gov.se.setc.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logger para mensagens simples e legíveis para usuários finais.
 * Produz mensagens claras em português sem jargões técnicos.
 */
@Component
public class UserFriendlyLogger {
    
    private static final Logger userLogger = LoggerFactory.getLogger("USER_FRIENDLY");
    
    /**
     * Log de início de autenticação
     */
    public void logAuthenticationStart() {
        userLogger.info("Obtendo token de autenticação...");
    }
    
    /**
     * Log de sucesso na autenticação
     */
    public void logAuthenticationSuccess() {
        userLogger.info("Token obtido com sucesso");
    }
    
    /**
     * Log de falha na autenticação
     */
    public void logAuthenticationError() {
        userLogger.error("ERRO: Falha na autenticação - detalhes em logs/errors.log");
    }
    
    /**
     * Log de início de busca de dados
     */
    public void logDataFetchStart(String dataType) {
        userLogger.info("Buscando {}...", dataType);
    }
    
    /**
     * Log de dados encontrados
     */
    public void logDataFound(String dataType, int count) {
        userLogger.info("Total de {} encontradas: {}", dataType, count);
    }
    
    /**
     * Log de início de processamento
     */
    public void logProcessingStart(String dataType) {
        userLogger.info("Processando {}...", dataType);
    }
    
    /**
     * Log de progresso de processamento
     */
    public void logProcessingProgress(int processed, int total) {
        int percentage = (int) ((processed * 100.0) / total);
        userLogger.info("Progresso: {}/{} ({}%)", processed, total, percentage);
    }
    
    /**
     * Log de dados processados
     */
    public void logDataProcessed(String dataType, int count) {
        userLogger.info("Total de {} processados: {}", dataType, count);
    }
    
    /**
     * Log de início de salvamento
     */
    public void logSavingStart() {
        userLogger.info("Salvando dados no banco...");
    }
    
    /**
     * Log de dados salvos
     */
    public void logDataSaved(int count) {
        userLogger.info("Dados salvos: {} registros", count);
    }
    
    /**
     * Log de operação concluída
     */
    public void logOperationComplete(long durationMs) {
        double seconds = durationMs / 1000.0;
        userLogger.info("Operação concluída em {}s", String.format("%.1f", seconds));
    }

    /**
     * Log de operação lenta
     */
    public void logSlowOperation(long durationMs) {
        double seconds = durationMs / 1000.0;
        userLogger.warn("Operação demorou mais que o esperado: {}s", String.format("%.1f", seconds));
    }
    
    /**
     * Log de início de aplicação
     */
    public void logApplicationStart(String appName) {
        userLogger.info("=== {} ===", appName);
        userLogger.info("Aplicação iniciando...");
    }
    
    /**
     * Log de aplicação pronta
     */
    public void logApplicationReady() {
        userLogger.info("Aplicação pronta para uso");
        userLogger.info("=====================================");
    }
    
    /**
     * Log de início de execução agendada
     */
    public void logScheduledExecutionStart() {
        userLogger.info("Iniciando execução automática...");
    }
    
    /**
     * Log de execução agendada concluída
     */
    public void logScheduledExecutionComplete(int totalRecords, long durationMs) {
        double seconds = durationMs / 1000.0;
        userLogger.info("Execução automática concluída");
        userLogger.info("Total processado: {} registros em {}s", totalRecords, String.format("%.1f", seconds));
    }
    
    /**
     * Log de erro genérico
     */
    public void logError(String operation, String errorMessage) {
        userLogger.error("ERRO em {}: {} - detalhes em logs/errors.log", operation, errorMessage);
    }
    
    /**
     * Log de erro de conexão
     */
    public void logConnectionError(String service) {
        userLogger.error("ERRO: Falha de conexão com {} - detalhes em logs/errors.log", service);
    }
    
    /**
     * Log de separador para organizar saída
     */
    public void logSeparator() {
        userLogger.info("-------------------------------------");
    }
    
    /**
     * Log de status de sistema
     */
    public void logSystemStatus(String status) {
        userLogger.info("Status do sistema: {}", status);
    }
    
    /**
     * Log de limpeza/manutenção
     */
    public void logMaintenanceStart(String task) {
        userLogger.info("Executando manutenção: {}", task);
    }
    
    /**
     * Log de manutenção concluída
     */
    public void logMaintenanceComplete(String task) {
        userLogger.info("Manutenção concluída: {}", task);
    }
    
    /**
     * Log de informação geral
     */
    public void logInfo(String message) {
        userLogger.info(message);
    }
    
    /**
     * Log de aviso
     */
    public void logWarning(String message) {
        userLogger.warn("AVISO: {}", message);
    }
}
