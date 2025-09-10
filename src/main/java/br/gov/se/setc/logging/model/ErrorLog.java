package br.gov.se.setc.logging.model;
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * Modelo específico para logs de erro
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorLog extends LogEvent {
    private String exceptionType;
    private String exceptionMessage;
    private String stackTrace;
    private String rootCause;
    private String errorCode;
    private String errorCategory;
    private String severity;
    private String affectedOperation;
    private String recoveryAction;
    private String userImpact;
    private Integer httpStatusCode;
    private String requestId;
    private String apiEndpoint;
    private String ugCode;
    private String contractId;
    public ErrorLog() {
        super();
        setEventType("ERROR");
        setLevel("ERROR");
        setComponent("ERROR_HANDLER");
    }
    public ErrorLog(String operation, Exception exception) {
        this();
        setOperation(operation);
        this.exceptionType = exception.getClass().getSimpleName();
        this.exceptionMessage = exception.getMessage();
        this.stackTrace = getStackTraceAsString(exception);
        Throwable rootCauseThrowable = getRootCause(exception);
        if (rootCauseThrowable != null) {
            this.rootCause = rootCauseThrowable.getClass().getSimpleName() + ": " + rootCauseThrowable.getMessage();
        }
    }
    public String getExceptionType() {
        return exceptionType;
    }
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
    public String getExceptionMessage() {
        return exceptionMessage;
    }
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
    public String getStackTrace() {
        return stackTrace;
    }
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
    public String getRootCause() {
        return rootCause;
    }
    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorCategory() {
        return errorCategory;
    }
    public void setErrorCategory(String errorCategory) {
        this.errorCategory = errorCategory;
    }
    public String getSeverity() {
        return severity;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    public String getAffectedOperation() {
        return affectedOperation;
    }
    public void setAffectedOperation(String affectedOperation) {
        this.affectedOperation = affectedOperation;
    }
    public String getRecoveryAction() {
        return recoveryAction;
    }
    public void setRecoveryAction(String recoveryAction) {
        this.recoveryAction = recoveryAction;
    }
    public String getUserImpact() {
        return userImpact;
    }
    public void setUserImpact(String userImpact) {
        this.userImpact = userImpact;
    }
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
    public String getUgCode() {
        return ugCode;
    }
    public void setUgCode(String ugCode) {
        this.ugCode = ugCode;
    }
    public String getContractId() {
        return contractId;
    }
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    private String getStackTraceAsString(Exception exception) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}