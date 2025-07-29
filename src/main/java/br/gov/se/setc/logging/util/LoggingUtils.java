package br.gov.se.setc.logging.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utilitários para logging
 */
public class LoggingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    // Padrões para sanitização de dados sensíveis
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(Bearer\\s+)([A-Za-z0-9\\-._~+/]+=*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(password[\"']?\\s*[:=]\\s*[\"']?)([^\"',\\s}]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CLIENT_SECRET_PATTERN = Pattern.compile("(client_secret[\"']?\\s*[:=]\\s*[\"']?)([^\"',\\s}]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CPF_PATTERN = Pattern.compile("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("\\b\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}\\b");
    
    /**
     * Converte objeto para JSON string
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Error converting to JSON: " + e.getMessage();
        }
    }
    
    /**
     * Sanitiza dados sensíveis de uma string
     */
    public static String sanitizeSensitiveData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return data;
        }
        
        String sanitized = data;
        
        // Sanitizar tokens
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1***MASKED***");
        
        // Sanitizar senhas
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1***MASKED***");
        
        // Sanitizar client secrets
        sanitized = CLIENT_SECRET_PATTERN.matcher(sanitized).replaceAll("$1***MASKED***");
        
        // Sanitizar CPF
        sanitized = CPF_PATTERN.matcher(sanitized).replaceAll("***.***.***-**");
        
        // Sanitizar CNPJ
        sanitized = CNPJ_PATTERN.matcher(sanitized).replaceAll("**.***.***/****-**");
        
        return sanitized;
    }
    
    /**
     * Converte HttpHeaders para Map<String, String> sanitizado
     */
    public static Map<String, String> sanitizeHeaders(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        
        Map<String, String> sanitizedHeaders = new HashMap<>();
        headers.forEach((key, values) -> {
            String value = String.join(", ", values);
            if ("authorization".equalsIgnoreCase(key)) {
                sanitizedHeaders.put(key, "Bearer ***MASKED***");
            } else {
                sanitizedHeaders.put(key, value);
            }
        });
        
        return sanitizedHeaders;
    }
    
    /**
     * Trunca string se exceder o tamanho máximo
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Calcula tamanho em bytes de uma string
     */
    public static int calculateSizeInBytes(String str) {
        if (str == null) {
            return 0;
        }
        return str.getBytes().length;
    }
    
    /**
     * Formata tempo de execução em formato legível
     */
    public static String formatExecutionTime(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.2fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    /**
     * Formata tamanho em bytes em formato legível
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Extrai nome da classe sem o pacote
     */
    public static String getSimpleClassName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
    
    /**
     * Extrai nome da classe sem o pacote de um objeto
     */
    public static String getSimpleClassName(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName();
    }
    
    /**
     * Cria mapa de metadados básicos
     */
    public static Map<String, Object> createBasicMetadata(String operation, String component) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", operation);
        metadata.put("component", component);
        metadata.put("timestamp", System.currentTimeMillis());
        return metadata;
    }
    
    /**
     * Determina categoria de erro baseada na exceção
     */
    public static String determineErrorCategory(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName().toLowerCase();
        
        if (exceptionType.contains("security") || exceptionType.contains("auth") || 
            exceptionType.contains("unauthorized") || exceptionType.contains("forbidden")) {
            return "SECURITY";
        } else if (exceptionType.contains("sql") || exceptionType.contains("database") || 
                   exceptionType.contains("connection") || exceptionType.contains("jdbc")) {
            return "DATABASE";
        } else if (exceptionType.contains("http") || exceptionType.contains("rest") || 
                   exceptionType.contains("client") || exceptionType.contains("timeout")) {
            return "INTEGRATION";
        } else if (exceptionType.contains("validation") || exceptionType.contains("illegal") || 
                   exceptionType.contains("business")) {
            return "BUSINESS";
        } else {
            return "TECHNICAL";
        }
    }
    
    /**
     * Determina severidade baseada na exceção
     */
    public static String determineSeverity(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName().toLowerCase();
        
        if (exceptionType.contains("security") || exceptionType.contains("auth") || 
            exceptionType.contains("outofmemory") || exceptionType.contains("stackoverflow")) {
            return "CRITICAL";
        } else if (exceptionType.contains("sql") || exceptionType.contains("connection") || 
                   exceptionType.contains("timeout") || exceptionType.contains("runtime")) {
            return "HIGH";
        } else if (exceptionType.contains("validation") || exceptionType.contains("illegal")) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
