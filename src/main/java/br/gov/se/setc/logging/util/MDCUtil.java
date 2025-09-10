package br.gov.se.setc.logging.util;
import org.slf4j.MDC;
import java.util.UUID;
/**
 * Utilitário simplificado para gerenciar contexto distribuído usando MDC (Mapped Diagnostic Context).
 * Mantém apenas as funcionalidades essenciais para rastreamento.
 */
public class MDCUtil {
    public static final String CORRELATION_ID = "correlationId";
    public static final String OPERATION = "operation";
    public static final String COMPONENT = "component";
    /**
     * Gera e define um novo correlation ID
     */
    public static String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(CORRELATION_ID, correlationId);
        return correlationId;
    }
    /**
     * Define o correlation ID
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            MDC.put(CORRELATION_ID, correlationId);
        }
    }
    /**
     * Obtém o correlation ID atual
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }
    /**
     * Define a operação atual
     */
    public static void setOperation(String operation) {
        if (operation != null && !operation.trim().isEmpty()) {
            MDC.put(OPERATION, operation);
        }
    }
    /**
     * Obtém a operação atual
     */
    public static String getOperation() {
        return MDC.get(OPERATION);
    }
    /**
     * Define o componente atual
     */
    public static void setComponent(String component) {
        if (component != null && !component.trim().isEmpty()) {
            MDC.put(COMPONENT, component);
        }
    }
    /**
     * Obtém o componente atual
     */
    public static String getComponent() {
        return MDC.get(COMPONENT);
    }
    /**
     * Limpa uma chave específica do MDC
     */
    public static void remove(String key) {
        MDC.remove(key);
    }
    /**
     * Limpa o correlation ID
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID);
    }
    /**
     * Limpa todas as chaves do MDC
     */
    public static void clear() {
        MDC.clear();
    }
    /**
     * Configura contexto básico para uma operação
     */
    public static void setupOperationContext(String component, String operation) {
        if (getCorrelationId() == null) {
            generateAndSetCorrelationId();
        }
        setComponent(component);
        setOperation(operation);
    }
}