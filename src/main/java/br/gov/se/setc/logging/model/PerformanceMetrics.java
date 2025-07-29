package br.gov.se.setc.logging.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Modelo para métricas de performance
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceMetrics extends LogEvent {
    
    private String methodName;
    private String className;
    private Long executionTimeMs;
    private Long memoryUsedBytes;
    private Integer recordsProcessed;
    private Integer recordsInserted;
    private Integer recordsUpdated;
    private Integer recordsDeleted;
    private Long databaseTimeMs;
    private Long apiCallTimeMs;
    private Integer apiCallCount;
    private String operationType;
    private Double throughputRecordsPerSecond;
    private String status; // SUCCESS, FAILED, PARTIAL
    
    public PerformanceMetrics() {
        super();
        setEventType("PERFORMANCE");
        setComponent("PERFORMANCE_MONITOR");
    }
    
    public PerformanceMetrics(String className, String methodName, String operationType) {
        this();
        this.className = className;
        this.methodName = methodName;
        this.operationType = operationType;
        setOperation(methodName);
    }
    
    // Getters and Setters
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public Long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }
    
    public void setMemoryUsedBytes(Long memoryUsedBytes) {
        this.memoryUsedBytes = memoryUsedBytes;
    }
    
    public Integer getRecordsProcessed() {
        return recordsProcessed;
    }
    
    public void setRecordsProcessed(Integer recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }
    
    public Integer getRecordsInserted() {
        return recordsInserted;
    }
    
    public void setRecordsInserted(Integer recordsInserted) {
        this.recordsInserted = recordsInserted;
    }
    
    public Integer getRecordsUpdated() {
        return recordsUpdated;
    }
    
    public void setRecordsUpdated(Integer recordsUpdated) {
        this.recordsUpdated = recordsUpdated;
    }
    
    public Integer getRecordsDeleted() {
        return recordsDeleted;
    }
    
    public void setRecordsDeleted(Integer recordsDeleted) {
        this.recordsDeleted = recordsDeleted;
    }
    
    public Long getDatabaseTimeMs() {
        return databaseTimeMs;
    }
    
    public void setDatabaseTimeMs(Long databaseTimeMs) {
        this.databaseTimeMs = databaseTimeMs;
    }
    
    public Long getApiCallTimeMs() {
        return apiCallTimeMs;
    }
    
    public void setApiCallTimeMs(Long apiCallTimeMs) {
        this.apiCallTimeMs = apiCallTimeMs;
    }
    
    public Integer getApiCallCount() {
        return apiCallCount;
    }
    
    public void setApiCallCount(Integer apiCallCount) {
        this.apiCallCount = apiCallCount;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public Double getThroughputRecordsPerSecond() {
        return throughputRecordsPerSecond;
    }
    
    public void setThroughputRecordsPerSecond(Double throughputRecordsPerSecond) {
        this.throughputRecordsPerSecond = throughputRecordsPerSecond;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Calcula throughput baseado no tempo de execução e registros processados
     */
    public void calculateThroughput() {
        if (executionTimeMs != null && executionTimeMs > 0 && recordsProcessed != null && recordsProcessed > 0) {
            this.throughputRecordsPerSecond = (recordsProcessed.doubleValue() / executionTimeMs) * 1000;
        }
    }
}
