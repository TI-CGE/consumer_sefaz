package br.gov.se.setc.logging.aspect;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.LoggingUtils;
import br.gov.se.setc.logging.util.MDCUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
/**
 * Aspecto simplificado para logging automático usando AOP
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final UnifiedLogger unifiedLogger;
    @Autowired
    public LoggingAspect(UnifiedLogger unifiedLogger) {
        this.unifiedLogger = unifiedLogger;
    }
    /**
     * Intercepta métodos anotados com @LogOperation
     */
    @Around("@annotation(logOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String operation = logOperation.operation();
        String component = logOperation.component().isEmpty() ? className : logOperation.component();
        MDCUtil.setupOperationContext(component, operation);
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception thrownException = null;
        int dataCount = 0;
        try {
            if (logOperation.logParameters()) {
                unifiedLogger.logOperationStart(component, operation, "PARAMETERS", Arrays.toString(joinPoint.getArgs()));
            } else {
                unifiedLogger.logOperationStart(component, operation);
            }
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            if (logOperation.includeDataCount() && result != null) {
                dataCount = extractDataCount(result);
            }
            if (logOperation.logResult() && result != null) {
                unifiedLogger.logOperationSuccess(component, operation, executionTime, dataCount,
                        "RESULT", LoggingUtils.truncate(result.toString(), 200));
            } else {
                unifiedLogger.logOperationSuccess(component, operation, executionTime, dataCount);
            }
            return result;
        } catch (Exception e) {
            thrownException = e;
            long executionTime = System.currentTimeMillis() - startTime;
            if (logOperation.logExceptions()) {
                unifiedLogger.logOperationError(component, operation, executionTime, e);
            }
            throw e;
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Extrai contagem de dados do resultado quando possível
     */
    private int extractDataCount(Object result) {
        if (result == null) return 0;
        if (result instanceof java.util.Collection) {
            return ((java.util.Collection<?>) result).size();
        }
        if (result.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(result);
        }
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        return 1;
    }
}