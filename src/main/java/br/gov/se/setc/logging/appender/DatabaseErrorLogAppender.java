package br.gov.se.setc.logging.appender;

import br.gov.se.setc.logging.service.ErrorLogService;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseErrorLogAppender extends AppenderBase<ILoggingEvent> implements ApplicationContextAware {
    
    private static volatile ApplicationContext applicationContext;
    private static volatile ErrorLogService errorLogService;
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ErrorLogAppender-Thread");
        t.setDaemon(true);
        return t;
    });
    
    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) {
        applicationContext = context;
    }
    
    @Override
    public void start() {
        super.start();
    }
    
    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        
        if (event.getLevel().levelInt >= ch.qos.logback.classic.Level.ERROR.levelInt) {
            String loggerName = event.getLoggerName();
            if (loggerName == null || 
                loggerName.contains("ErrorLogService") || 
                loggerName.contains("DatabaseErrorLogAppender")) {
                return;
            }
            
            try {
                ErrorLogService service = getErrorLogService();
                if (service == null) {
                    return;
                }
                
                java.util.Map<String, String> mdcMap = event.getMDCPropertyMap();
                String component = mdcMap != null ? mdcMap.get("component") : null;
                if (component == null || component.isEmpty()) {
                    component = loggerName;
                }
                
                String message = event.getFormattedMessage();
                if (message == null || message.isEmpty()) {
                    return;
                }
                
                Exception exception = null;
                IThrowableProxy throwableProxy = event.getThrowableProxy();
                if (throwableProxy != null) {
                    String exceptionMessage = throwableProxy.getMessage();
                    if (exceptionMessage == null) {
                        exceptionMessage = throwableProxy.getClassName();
                    }
                    exception = new Exception(throwableProxy.getClassName() + ": " + exceptionMessage);
                }
                
                final String finalComponent = component;
                final Exception finalException = exception;
                final String finalMessage = message;
                final String finalCorrelationId = mdcMap != null ? mdcMap.get("correlationId") : null;
                final String finalOperation = mdcMap != null ? mdcMap.get("operation") : null;
                final String finalUgCode = mdcMap != null ? mdcMap.get("ugCode") : null;
                final String finalContractId = mdcMap != null ? mdcMap.get("contractId") : null;
                final String finalRequestId = mdcMap != null ? mdcMap.get("requestId") : null;
                final String finalApiEndpoint = mdcMap != null ? mdcMap.get("apiEndpoint") : null;
                
                executorService.submit(() -> {
                    try {
                        if (finalCorrelationId != null) {
                            MDC.put("correlationId", finalCorrelationId);
                        }
                        if (finalOperation != null) {
                            MDC.put("operation", finalOperation);
                        }
                        if (finalUgCode != null) {
                            MDC.put("ugCode", finalUgCode);
                        }
                        if (finalContractId != null) {
                            MDC.put("contractId", finalContractId);
                        }
                        if (finalRequestId != null) {
                            MDC.put("requestId", finalRequestId);
                        }
                        if (finalApiEndpoint != null) {
                            MDC.put("apiEndpoint", finalApiEndpoint);
                        }
                        service.saveErrorLog(finalComponent, finalMessage, finalException);
                    } catch (Exception ex) {
                        System.err.println("Erro ao salvar log de erro no banco (silencioso): " + ex.getClass().getSimpleName());
                    } finally {
                        MDC.remove("correlationId");
                        MDC.remove("operation");
                        MDC.remove("ugCode");
                        MDC.remove("contractId");
                        MDC.remove("requestId");
                        MDC.remove("apiEndpoint");
                    }
                });
            } catch (Throwable t) {
                System.err.println("Erro ao processar log de erro (silencioso): " + t.getClass().getSimpleName());
            }
        }
    }
    
    private synchronized ErrorLogService getErrorLogService() {
        if (errorLogService != null) {
            return errorLogService;
        }
        
        if (applicationContext == null) {
            return null;
        }
        
        if (initialized.get()) {
            return null;
        }
        
        try {
            synchronized (initialized) {
                if (!initialized.get()) {
                    errorLogService = applicationContext.getBean(ErrorLogService.class);
                    initialized.set(true);
                }
            }
            return errorLogService;
        } catch (Exception e) {
            initialized.set(true);
            return null;
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        executorService.shutdown();
    }
}

