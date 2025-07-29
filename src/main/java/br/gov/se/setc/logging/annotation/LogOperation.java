package br.gov.se.setc.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação unificada para logging automático de operações.
 * Substitui todas as anotações de logging anteriores (@LogExecution, @LogPerformance, @LogApiCall).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    
    /**
     * Nome da operação (obrigatório)
     */
    String operation();
    
    /**
     * Componente responsável pela operação (opcional, usa nome da classe se não especificado)
     */
    String component() default "";
    
    /**
     * Se deve logar parâmetros de entrada
     */
    boolean logParameters() default false;
    
    /**
     * Se deve logar resultado da operação
     */
    boolean logResult() default false;
    
    /**
     * Threshold em milissegundos para considerar operação lenta
     */
    long slowOperationThresholdMs() default 5000;
    
    /**
     * Se deve incluir contagem de dados processados no log
     */
    boolean includeDataCount() default true;
    
    /**
     * Se deve logar exceções automaticamente
     */
    boolean logExceptions() default true;
}
