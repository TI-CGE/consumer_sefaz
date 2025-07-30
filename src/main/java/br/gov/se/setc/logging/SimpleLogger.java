package br.gov.se.setc.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Logger simplificado com formato limpo e legível.
 * Foca na clareza e simplicidade, removendo informações desnecessárias.
 */
@Component
public class SimpleLogger {

    private static final Logger logger = LoggerFactory.getLogger("SIMPLE");
    private static final Logger errorLogger = LoggerFactory.getLogger(SimpleLogger.class);
    private static final Logger markdownLogger = LoggerFactory.getLogger("MARKDOWN");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Log de informação simples
     */
    public void info(String component, String message) {
        setContext(component);
        logger.info("{} | {} | {}", getCurrentTime(), component, message);
    }
    
    /**
     * Log de sucesso
     */
    public void success(String component, String message) {
        setContext(component);
        logger.info("{} | {} | ✅ {}", getCurrentTime(), component, message);
    }
    
    /**
     * Log de sucesso com duração
     */
    public void success(String component, String message, long durationMs) {
        setContext(component);
        String duration = formatDuration(durationMs);
        logger.info("{} | {} | ✅ {} ({})", getCurrentTime(), component, message, duration);
    }
    
    /**
     * Log de aviso
     */
    public void warning(String component, String message) {
        setContext(component);
        logger.warn("{} | {} | ⚠️ {}", getCurrentTime(), component, message);
    }
    
    /**
     * Log de erro
     */
    public void error(String component, String message) {
        setContext(component);
        // Log no formato simples
        logger.error("{} | {} | ❌ {}", getCurrentTime(), component, message);
        // Log técnico para arquivo de erros
        errorLogger.error("ERRO em {} - {}", component, message);
        // Log estruturado em markdown
        logErrorToMarkdown(component, message, null);
    }

    /**
     * Log de erro com exceção
     */
    public void error(String component, String message, Exception e) {
        setContext(component);
        // Log no formato simples
        logger.error("{} | {} | ❌ {}: {}", getCurrentTime(), component, message, e.getMessage());
        // Log técnico completo para arquivo de erros
        errorLogger.error("ERRO em {} - {}: {}", component, message, e.getMessage(), e);
        // Log estruturado em markdown
        logErrorToMarkdown(component, message, e);
    }
    
    /**
     * Log de progresso
     */
    public void progress(String component, String operation, int current, int total) {
        setContext(component);
        int percentage = (int) ((current * 100.0) / total);
        logger.info("{} | {} | 🔄 {} - {}/{} ({}%)", 
                getCurrentTime(), component, operation, current, total, percentage);
    }
    
    /**
     * Log de início de operação
     */
    public void start(String component, String operation) {
        setContext(component);
        logger.info("{} | {} | 🚀 Iniciando {}", getCurrentTime(), component, operation);
    }
    
    /**
     * Log de operação lenta
     */
    public void slow(String component, String message, long durationMs) {
        setContext(component);
        String duration = formatDuration(durationMs);
        logger.warn("{} | {} | 🐌 {} ({})", getCurrentTime(), component, message, duration);
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
    
    private String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.1fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    private void setContext(String component) {
        MDC.put("component", component);
        if (MDC.get("correlationId") == null) {
            MDC.put("correlationId", UUID.randomUUID().toString().substring(0, 8));
        }
    }

    /**
     * Log de erro estruturado em markdown
     */
    private void logErrorToMarkdown(String component, String message, Exception e) {
        StringBuilder markdownError = new StringBuilder();
        markdownError.append("\n## ").append(getCurrentTime()).append(" | ❌ ERRO em ").append(component).append("\n");
        markdownError.append("- 🚨 **Erro**: ").append(message).append("\n");

        if (e != null) {
            markdownError.append("- 🔍 **Tipo**: ").append(e.getClass().getSimpleName()).append("\n");
            markdownError.append("- 📋 **Detalhes**: ").append(e.getMessage()).append("\n");

            // Adicionar algumas linhas do stack trace mais relevantes
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                markdownError.append("- 📍 **Local**: ").append(stackTrace[0].toString()).append("\n");
            }
        }

        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            markdownError.append("- 🔗 **Correlation ID**: ").append(correlationId).append("\n");
        }

        markdownError.append("\n");
        markdownLogger.info(markdownError.toString());
    }
}
