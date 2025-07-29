package br.gov.se.setc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuração para habilitar AOP e logging
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfig {
    
    // Esta classe habilita o processamento de aspectos AOP
    // Os aspectos de logging serão automaticamente aplicados
    // aos métodos anotados com @LogExecution, @LogPerformance, etc.
    
}
