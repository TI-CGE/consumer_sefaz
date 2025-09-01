package br.gov.se.setc.logging;
import br.gov.se.setc.logging.util.MDCUtil;
import br.gov.se.setc.logging.util.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Logger unificado que substitui todos os loggers especializados.
 * Agora usa formato simplificado através do SimpleLogger.
 */
@Component
public class UnifiedLogger {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedLogger.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final AtomicLong operationCounter = new AtomicLong(0);
    @Autowired
    private SimpleLogger simpleLogger;
    /**
     * Log de início de operação
     */
    public void logOperationStart(String component, String operation, Object... context) {
        setupMDC(component, operation);
        String contextStr = context.length > 0 ? " | " + formatContext(context) : "";
        String message = operation + contextStr;
        simpleLogger.start(component, message);
    }
    /**
     * Log de sucesso de operação
     */
    public void logOperationSuccess(String component, String operation, long durationMs, int dataCount, Object... context) {
        setupMDC(component, operation);
        String contextStr = context.length > 0 ? " | " + formatContext(context) : "";
        String message = operation + " | " + dataCount + " registros" + contextStr;
        if (durationMs > 5000) {
            simpleLogger.slow(component, message, durationMs);
        } else {
            simpleLogger.success(component, message, durationMs);
        }
    }
    /**
     * Log de erro de operação
     */
    public void logOperationError(String component, String operation, long durationMs, Exception error, Object... context) {
        setupMDC(component, operation);
        String contextStr = context.length > 0 ? " | " + formatContext(context) : "";
        String message = operation + contextStr;
        simpleLogger.error(component, message, error);
    }
    /**
     * Log de processamento de dados
     */
    public void logDataProcessing(String component, String operation, int recordsReceived,
                                int recordsProcessed, int recordsValid, int recordsInvalid) {
        setupMDC(component, operation);
        logger.info("📊 PROCESSAMENTO {} | RECEBIDOS: {} | PROCESSADOS: {} | VÁLIDOS: {} | INVÁLIDOS: {}",
                operation, recordsReceived, recordsProcessed, recordsValid, recordsInvalid);
    }
    /**
     * Log de chamada de API
     */
    public void logApiCall(String endpoint, String method, int statusCode, long responseTimeMs,
                          int requestSize, int responseSize) {
        setupMDC("API_CLIENT", "API_CALL");
        String responseTimeStr = LoggingUtils.formatExecutionTime(responseTimeMs);
        String requestSizeStr = LoggingUtils.formatBytes(requestSize);
        String responseSizeStr = LoggingUtils.formatBytes(responseSize);
        if (responseTimeMs > 3000) {
            logger.warn("🐌 API LENTA {} {} | STATUS: {} | DURATION: {} | REQUEST: {} | RESPONSE: {}",
                    method, endpoint, statusCode, responseTimeStr, requestSizeStr, responseSizeStr);
        } else if (statusCode >= 400) {
            logger.error("🌐 API ERRO {} {} | STATUS: {} | DURATION: {} | REQUEST: {} | RESPONSE: {}",
                    method, endpoint, statusCode, responseTimeStr, requestSizeStr, responseSizeStr);
        } else {
            logger.info("🌐 API SUCESSO {} {} | STATUS: {} | DURATION: {} | REQUEST: {} | RESPONSE: {}",
                    method, endpoint, statusCode, responseTimeStr, requestSizeStr, responseSizeStr);
        }
    }
    /**
     * Log de operação de banco de dados
     */
    public void logDatabaseOperation(String operation, String table, int recordCount, long durationMs) {
        setupMDC("DATABASE", operation + "_" + table);
        String durationStr = LoggingUtils.formatExecutionTime(durationMs);
        if (durationMs > 10000) {
            logger.warn("🐌 DB LENTO {} na tabela {} | RECORDS: {} | DURATION: {}",
                    operation, table, recordCount, durationStr);
        } else {
            logger.info("🗄️ DB {} na tabela {} | RECORDS: {} | DURATION: {}",
                    operation, table, recordCount, durationStr);
        }
    }
    /**
     * Log de evento da aplicação
     */
    public void logApplicationEvent(String event, String details) {
        setupMDC("APPLICATION", event);
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        logger.info("🔸 [{}] {} | {}", timestamp, event, details);
    }
    /**
     * Log de inicialização da aplicação
     */
    public void logApplicationStartup(String applicationName, String version, String profile) {
        setupMDC("APPLICATION", "STARTUP");
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        logger.info("🚀 [{}] APLICAÇÃO INICIADA | {} v{} | Perfil: {}",
                timestamp, applicationName, version, profile);
        logger.info("═══════════════════════════════════════════════════════════════════════════════");
        logger.info("🔸 [{}] APLICAÇÃO PRONTA PARA RECEBER REQUISIÇÕES", timestamp);
        logger.info("═══════════════════════════════════════════════════════════════════════════════");
    }
    /**
     * Log de autenticação
     */
    public void logAuthentication(String clientId, String endpoint, boolean success, long durationMs, String correlationId) {
        setupMDC("SECURITY", success ? "AUTH_SUCCESS" : "AUTH_FAILED");
        String durationStr = LoggingUtils.formatExecutionTime(durationMs);
        if (success) {
            logger.info("✅ AUTENTICAÇÃO SUCESSO | Cliente: {} | Endpoint: {} | DURATION: {} | ID: {}",
                    clientId, endpoint, durationStr, correlationId);
        } else {
            logger.error("❌ AUTENTICAÇÃO FALHOU | Cliente: {} | Endpoint: {} | DURATION: {} | ID: {}",
                    clientId, endpoint, durationStr, correlationId);
        }
    }
    /**
     * Configura o contexto MDC
     */
    private void setupMDC(String component, String operation) {
        if (MDCUtil.getCorrelationId() == null) {
            MDCUtil.generateAndSetCorrelationId();
        }
        MDCUtil.setComponent(component);
        MDCUtil.setOperation(operation);
    }
    /**
     * Formata contexto adicional
     */
    private String formatContext(Object... context) {
        if (context.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < context.length; i += 2) {
            if (i + 1 < context.length) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(context[i]).append(": ").append(context[i + 1]);
            }
        }
        return sb.toString();
    }
}