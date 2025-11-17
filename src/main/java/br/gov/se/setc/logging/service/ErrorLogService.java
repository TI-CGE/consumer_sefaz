package br.gov.se.setc.logging.service;

import br.gov.se.setc.logging.entity.ErrorLogEntity;
import br.gov.se.setc.logging.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErrorLogService {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogService.class);
    
    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(String component, String message, Exception exception) {
        try {
            ErrorLogEntity errorLog = new ErrorLogEntity();
            errorLog.setTimestamp(java.time.LocalDateTime.now());
            errorLog.setComponent(component);
            errorLog.setMessage(message);
            errorLog.setCorrelationId(MDC.get("correlationId"));
            errorLog.setOperation(MDC.get("operation"));
            
            String ugCode = MDC.get("ugCode");
            if (ugCode == null || ugCode.isEmpty()) {
                ugCode = extractUgCodeFromMessage(message);
            }
            errorLog.setUgCode(ugCode);
            
            String contractId = MDC.get("contractId");
            if (contractId == null || contractId.isEmpty()) {
                contractId = extractContractIdFromMessage(message);
            }
            errorLog.setContractId(contractId);
            
            String requestId = MDC.get("requestId");
            if (requestId == null || requestId.isEmpty()) {
                requestId = MDC.get("correlationId");
            }
            errorLog.setRequestId(requestId);
            
            String apiUrlCompleta = extractFullApiUrl(message);
            errorLog.setApiUrlCompleta(apiUrlCompleta);
            
            String apiEndpoint = MDC.get("apiEndpoint");
            if (apiEndpoint == null || apiEndpoint.isEmpty()) {
                apiEndpoint = extractApiEndpoint(message);
            }
            errorLog.setApiEndpoint(apiEndpoint);
            errorLog.setHttpStatusCode(extractHttpStatusCode(message));
            errorLog.setSeverity(determineSeverity(exception, message));
            errorLog.setErrorCategory(determineErrorCategory(component, message));
            errorLog.setErrorCode(extractErrorCode(message, exception));

            if (exception != null) {
                errorLog.setExceptionType(exception.getClass().getName());
                errorLog.setExceptionMessage(exception.getMessage());
                
                String stackTrace = getStackTraceAsString(exception);
                if (stackTrace != null && stackTrace.length() > 10000) {
                    stackTrace = stackTrace.substring(0, 10000);
                }
                errorLog.setStackTrace(stackTrace);
                
                Throwable rootCause = getRootCause(exception);
                if (rootCause != null) {
                    String rootCauseStr = rootCause.getClass().getName() + ": " + rootCause.getMessage();
                    if (rootCauseStr.length() > 1000) {
                        rootCauseStr = rootCauseStr.substring(0, 1000);
                    }
                    errorLog.setRootCause(rootCauseStr);
                }
            }

            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            logger.error("Erro ao salvar log de erro no banco de dados", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(String component, String operation, String message, Exception exception) {
        try {
            ErrorLogEntity errorLog = new ErrorLogEntity();
            errorLog.setTimestamp(java.time.LocalDateTime.now());
            errorLog.setComponent(component);
            errorLog.setOperation(operation);
            errorLog.setMessage(message);
            errorLog.setCorrelationId(MDC.get("correlationId"));
            
            String ugCode = MDC.get("ugCode");
            if (ugCode == null || ugCode.isEmpty()) {
                ugCode = extractUgCodeFromMessage(message);
            }
            errorLog.setUgCode(ugCode);
            
            String contractId = MDC.get("contractId");
            if (contractId == null || contractId.isEmpty()) {
                contractId = extractContractIdFromMessage(message);
            }
            errorLog.setContractId(contractId);
            
            String requestId = MDC.get("requestId");
            if (requestId == null || requestId.isEmpty()) {
                requestId = MDC.get("correlationId");
            }
            errorLog.setRequestId(requestId);
            
            String apiUrlCompleta = extractFullApiUrl(message);
            errorLog.setApiUrlCompleta(apiUrlCompleta);
            
            String apiEndpoint = MDC.get("apiEndpoint");
            if (apiEndpoint == null || apiEndpoint.isEmpty()) {
                apiEndpoint = extractApiEndpoint(message);
            }
            errorLog.setApiEndpoint(apiEndpoint);
            errorLog.setHttpStatusCode(extractHttpStatusCode(message));
            errorLog.setSeverity(determineSeverity(exception, message));
            errorLog.setErrorCategory(determineErrorCategory(component, message));
            errorLog.setErrorCode(extractErrorCode(message, exception));

            if (exception != null) {
                errorLog.setExceptionType(exception.getClass().getName());
                errorLog.setExceptionMessage(exception.getMessage());
                
                String stackTrace = getStackTraceAsString(exception);
                if (stackTrace != null && stackTrace.length() > 10000) {
                    stackTrace = stackTrace.substring(0, 10000);
                }
                errorLog.setStackTrace(stackTrace);
                
                Throwable rootCause = getRootCause(exception);
                if (rootCause != null) {
                    String rootCauseStr = rootCause.getClass().getName() + ": " + rootCause.getMessage();
                    if (rootCauseStr.length() > 1000) {
                        rootCauseStr = rootCauseStr.substring(0, 1000);
                    }
                    errorLog.setRootCause(rootCauseStr);
                }
            }

            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            logger.error("Erro ao salvar log de erro no banco de dados", e);
        }
    }

    private String getStackTraceAsString(Exception exception) {
        if (exception == null) {
            return null;
        }
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    private String extractApiEndpoint(String message) {
        if (message == null) {
            return null;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("https?://[^\\s]+");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String url = matcher.group();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.length() > 500) {
                url = url.substring(0, 500);
            }
            return url;
        }
        
        if (message.contains("GET ") || message.contains("POST ") || message.contains("PUT ") || message.contains("DELETE ")) {
            int start = message.indexOf("http");
            if (start >= 0) {
                int end = message.indexOf(" ", start);
                if (end < 0) {
                    end = message.length();
                }
                String url = message.substring(start, end);
                if (url.contains("?")) {
                    url = url.substring(0, url.indexOf("?"));
                }
                if (url.length() > 500) {
                    url = url.substring(0, 500);
                }
                return url;
            }
        }
        
        return null;
    }
    
    private Integer extractHttpStatusCode(String message) {
        if (message == null) {
            return null;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("STATUS:\\s*(\\d{3})|status:\\s*(\\d{3})|Status:\\s*(\\d{3})|HTTP\\s+(\\d{3})|\\b(\\d{3})\\s+Internal Server Error|\\b(\\d{3})\\s+Bad Request");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    try {
                        return Integer.parseInt(group);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }
        
        if (message.contains("500")) {
            return 500;
        } else if (message.contains("400")) {
            return 400;
        } else if (message.contains("404")) {
            return 404;
        } else if (message.contains("403")) {
            return 403;
        } else if (message.contains("401")) {
            return 401;
        }
        
        return null;
    }
    
    private String determineSeverity(Exception exception, String message) {
        if (exception != null) {
            String exceptionName = exception.getClass().getSimpleName();
            if (exceptionName.contains("Timeout") || exceptionName.contains("Connection")) {
                return "HIGH";
            }
            if (exceptionName.contains("NullPointer") || exceptionName.contains("IllegalArgument")) {
                return "CRITICAL";
            }
        }
        
        if (message != null) {
            String msg = message.toUpperCase();
            if (msg.contains("TIMEOUT") || msg.contains("CONNECTION")) {
                return "HIGH";
            }
            if (msg.contains("NULL") || msg.contains("ILLEGAL")) {
                return "CRITICAL";
            }
            if (msg.contains("500") || msg.contains("INTERNAL SERVER ERROR")) {
                return "HIGH";
            }
        }
        
        return "MEDIUM";
    }
    
    private String determineErrorCategory(String component, String message) {
        if (component != null) {
            if (component.contains("API") || component.contains("CLIENT")) {
                return "API_ERROR";
            }
            if (component.contains("DATABASE") || component.contains("DB")) {
                return "DATABASE_ERROR";
            }
            if (component.contains("VALIDATION")) {
                return "VALIDATION_ERROR";
            }
        }
        
        if (message != null) {
            String msg = message.toUpperCase();
            if (msg.contains("API") || msg.contains("HTTP")) {
                return "API_ERROR";
            }
            if (msg.contains("DATABASE") || msg.contains("SQL") || msg.contains("CONNECTION")) {
                return "DATABASE_ERROR";
            }
            if (msg.contains("VALIDATION") || msg.contains("VALIDATE")) {
                return "VALIDATION_ERROR";
            }
        }
        
        return "GENERAL_ERROR";
    }
    
    private String extractErrorCode(String message, Exception exception) {
        if (exception != null) {
            String exceptionName = exception.getClass().getSimpleName();
            if (exceptionName.contains("TimeoutException")) {
                return "TIMEOUT";
            }
            if (exceptionName.contains("ConnectionException") || exceptionName.contains("ConnectException")) {
                return "CONNECTION_ERROR";
            }
            if (exceptionName.contains("NullPointerException")) {
                return "NULL_POINTER";
            }
            if (exceptionName.contains("IllegalArgumentException")) {
                return "ILLEGAL_ARGUMENT";
            }
        }
        
        if (message != null) {
            if (message.contains("500")) {
                return "HTTP_500";
            }
            if (message.contains("400")) {
                return "HTTP_400";
            }
            if (message.contains("404")) {
                return "HTTP_404";
            }
            if (message.contains("403")) {
                return "HTTP_403";
            }
            if (message.contains("401")) {
                return "HTTP_401";
            }
        }
        
        return null;
    }
    
    private String extractFullApiUrl(String message) {
        if (message == null) {
            return null;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("https?://[^\\s]+");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String url = matcher.group();
            if (url.length() > 2000) {
                url = url.substring(0, 2000);
            }
            return url;
        }
        
        if (message.contains("GET ") || message.contains("POST ") || message.contains("PUT ") || message.contains("DELETE ")) {
            int start = message.indexOf("http");
            if (start >= 0) {
                int end = message.indexOf(" ", start);
                if (end < 0) {
                    end = message.length();
                }
                String url = message.substring(start, end);
                if (url.length() > 2000) {
                    url = url.substring(0, 2000);
                }
                return url;
            }
        }
        
        String apiEndpoint = MDC.get("apiEndpoint");
        if (apiEndpoint != null && !apiEndpoint.isEmpty()) {
            return apiEndpoint;
        }
        
        return null;
    }
    
    private String extractUgCodeFromMessage(String message) {
        if (message == null) {
            return null;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("cdUnidadeGestora=([0-9]{6})|cdUnidadeGestora=([^&\\s]+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    if (group.matches("[0-9]{6}")) {
                        return group;
                    }
                }
            }
        }
        
        pattern = java.util.regex.Pattern.compile("UG[\\s:]+([0-9]{6})|ugCd[\\s:]+([0-9]{6})|Processando UG[\\s]+([0-9]{6})|para UG[\\s:]+([0-9]{6})");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty() && group.matches("[0-9]{6}")) {
                    return group;
                }
            }
        }
        
        pattern = java.util.regex.Pattern.compile("\\b([0-9]{6})\\b");
        matcher = pattern.matcher(message);
        int count = 0;
        while (matcher.find() && count < 5) {
            String candidate = matcher.group(1);
            if (candidate != null && candidate.length() == 6 && (candidate.startsWith("1") || candidate.startsWith("0"))) {
                return candidate;
            }
            count++;
        }
        
        return null;
    }
    
    private String extractContractIdFromMessage(String message) {
        if (message == null) {
            return null;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("cdContrato[=:]+([^&\\s]+)|contractId[=:]+([^&\\s]+)|sqContrato[=:]+([^&\\s]+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    return group.length() > 50 ? group.substring(0, 50) : group;
                }
            }
        }
        
        pattern = java.util.regex.Pattern.compile("contrato[\\s_-]?fiscal[\\s:]+([^\\s]+)|contrato[\\s:]+([0-9]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    return group.length() > 50 ? group.substring(0, 50) : group;
                }
            }
        }
        
        return null;
    }
}

