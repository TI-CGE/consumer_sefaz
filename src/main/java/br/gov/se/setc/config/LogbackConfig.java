package br.gov.se.setc.config;

import br.gov.se.setc.logging.appender.DatabaseErrorLogAppender;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LogbackConfig implements ApplicationListener<ContextRefreshedEvent> {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            DatabaseErrorLogAppender appender = new DatabaseErrorLogAppender();
            appender.setContext(loggerContext);
            appender.setName("DATABASE_ERROR");
            appender.setApplicationContext(applicationContext);
            appender.start();
            
            ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.addAppender(appender);
            
            ch.qos.logback.classic.Logger appLogger = loggerContext.getLogger("br.gov.se.setc");
            appLogger.addAppender(appender);
            appLogger.setAdditive(false);
        } catch (Exception e) {
            System.err.println("Erro ao configurar DatabaseErrorLogAppender: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

