package br.gov.se.setc.logging.model;
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * Modelo específico para logs de chamadas de API
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCallLog extends LogEvent {
    private String httpMethod;
    private String url;
    private String requestHeaders;
    private String requestBody;
    private Integer responseStatus;
    private String responseHeaders;
    private String responseBody;
    private Long responseTimeMs;
    private Integer requestSizeBytes;
    private Integer responseSizeBytes;
    private String errorMessage;
    private String errorType;
    private String endpoint;
    private String ugCode;
    private String contractId;
    public ApiCallLog() {
        super();
        setEventType("API_CALL");
        setComponent("API_CLIENT");
    }
    public ApiCallLog(String operation, String url, String httpMethod) {
        this();
        setOperation(operation);
        this.url = url;
        this.httpMethod = httpMethod;
    }
    public String getHttpMethod() {
        return httpMethod;
    }
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getRequestHeaders() {
        return requestHeaders;
    }
    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
    public String getRequestBody() {
        return requestBody;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public Integer getResponseStatus() {
        return responseStatus;
    }
    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }
    public String getResponseHeaders() {
        return responseHeaders;
    }
    public void setResponseHeaders(String responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
    public String getResponseBody() {
        return responseBody;
    }
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    public Long getResponseTimeMs() {
        return responseTimeMs;
    }
    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
    public Integer getRequestSizeBytes() {
        return requestSizeBytes;
    }
    public void setRequestSizeBytes(Integer requestSizeBytes) {
        this.requestSizeBytes = requestSizeBytes;
    }
    public Integer getResponseSizeBytes() {
        return responseSizeBytes;
    }
    public void setResponseSizeBytes(Integer responseSizeBytes) {
        this.responseSizeBytes = responseSizeBytes;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorType() {
        return errorType;
    }
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
}
