package br.gov.se.setc.logging.util;
import org.slf4j.MDC;
import java.util.UUID;

public class MDCUtil {
    public static final String CORRELATION_ID = "correlationId";
    public static final String OPERATION = "operation";
    public static final String COMPONENT = "component";
    
    public static String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(CORRELATION_ID, correlationId);
        return correlationId;
    }
    
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            MDC.put(CORRELATION_ID, correlationId);
        }
    }
    
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }
    
    public static void setOperation(String operation) {
        if (operation != null && !operation.trim().isEmpty()) {
            MDC.put(OPERATION, operation);
        }
    }
    
    public static String getOperation() {
        return MDC.get(OPERATION);
    }
    
    public static void setComponent(String component) {
        if (component != null && !component.trim().isEmpty()) {
            MDC.put(COMPONENT, component);
        }
    }
    
    public static String getComponent() {
        return MDC.get(COMPONENT);
    }
    
    public static void remove(String key) {
        MDC.remove(key);
    }
    
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID);
    }
    
    public static void clear() {
        MDC.clear();
    }
    
    public static void setupOperationContext(String component, String operation) {
        if (getCorrelationId() == null) {
            generateAndSetCorrelationId();
        }
        setComponent(component);
        setOperation(operation);
    }
    
    public static void setUgCode(String ugCode) {
        if (ugCode != null && !ugCode.trim().isEmpty()) {
            MDC.put("ugCode", ugCode);
        }
    }
    
    public static void setContractId(String contractId) {
        if (contractId != null && !contractId.trim().isEmpty()) {
            MDC.put("contractId", contractId);
        }
    }
    
    public static void setRequestId(String requestId) {
        if (requestId != null && !requestId.trim().isEmpty()) {
            MDC.put("requestId", requestId);
        }
    }
    
    public static void setApiEndpoint(String apiEndpoint) {
        if (apiEndpoint != null && !apiEndpoint.trim().isEmpty()) {
            MDC.put("apiEndpoint", apiEndpoint);
        }
    }
}