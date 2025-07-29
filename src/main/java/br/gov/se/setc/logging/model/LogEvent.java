package br.gov.se.setc.logging.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Classe base para eventos de log estruturados
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEvent {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private String eventType;
    private String correlationId;
    private String sessionId;
    private String userId;
    private String component;
    private String operation;
    private String level;
    private String message;
    private Map<String, Object> metadata;
    
    public LogEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public LogEvent(String eventType, String component, String operation, String message) {
        this();
        this.eventType = eventType;
        this.component = component;
        this.operation = operation;
        this.message = message;
        this.level = "INFO";
    }
    
    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getComponent() {
        return component;
    }
    
    public void setComponent(String component) {
        this.component = component;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "LogEvent{" +
                "timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", component='" + component + '\'' +
                ", operation='" + operation + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
