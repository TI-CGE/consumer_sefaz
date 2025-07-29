package br.gov.se.setc.scheduler;

import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.dto.UnidadeGestoraDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Scheduler para execução automática do consumo de contratos
 */
@Component
public class ContractConsumptionScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractConsumptionScheduler.class);
    
    @Autowired
    @Qualifier("contratosFiscaisConsumoApiService")
    private ConsumoApiService<ContratosFiscaisDTO> contratosFiscaisService;
    
    @Autowired
    @Qualifier("unidadeGestoraConsumoApiService")
    private ConsumoApiService<UnidadeGestoraDTO> unidadeGestoraService;
    
    @Autowired
    private UnifiedLogger unifiedLogger;

    @Autowired
    private UserFriendlyLogger userFriendlyLogger;
    
    private boolean isFirstExecution = true;
    
    /**
     * Executa 5 segundos após a aplicação estar pronta (para testes)
     */
    @EventListener(ApplicationReadyEvent.class)
    public void executeOnStartup() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000); // Aguarda 5 segundos

                String correlationId = MDCUtil.generateAndSetCorrelationId();
                unifiedLogger.logApplicationEvent("SCHEDULER_STARTUP_TEST", "Execução de teste do scheduler");
                unifiedLogger.logOperationStart("SCHEDULER", "STARTUP_TEST", "CORRELATION_ID", correlationId);

                logger.info("=== INICIANDO EXECUÇÃO DE TESTE DO SCHEDULER ===");
                executeContractConsumption();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Execução de startup interrompida", e);
                unifiedLogger.logOperationError("SCHEDULER", "STARTUP_TEST", 0, e, "CORRELATION_ID", MDCUtil.getCorrelationId());
            }
        });
    }
    
    /**
     * Execução agendada para produção - diariamente às 2:45 AM
     * Comentado por padrão para testes
     */
    // @Scheduled(cron = "0 45 2 * * *")
    public void scheduledExecution() {
        logger.info("=== INICIANDO EXECUÇÃO AGENDADA DO SCHEDULER ===");
        executeContractConsumption();
    }
    
    /**
     * Execução a cada 10 minutos para testes (comentado por padrão)
     * Descomente para testes frequentes
     */
    // @Scheduled(fixedRate = 600000) // 10 minutos
    public void frequentTestExecution() {
        if (!isFirstExecution) {
            logger.info("=== EXECUÇÃO DE TESTE FREQUENTE ===");
            executeContractConsumption();
        }
    }
    
    /**
     * Método principal que executa o consumo de contratos
     */
    @LogOperation(operation = "SCHEDULED_CONTRACT_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 60000)
    private void executeContractConsumption() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "SCHEDULED_CONTRACT_CONSUMPTION");
        
        long totalStartTime = System.currentTimeMillis();
        Map<String, Integer> processingResults = new HashMap<>();
        int totalRecordsProcessed = 0;
        
        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "SCHEDULED_EXECUTION", "ENDPOINTS", "MULTIPLE_ENDPOINTS");

            // 1. Consumir Unidades Gestoras primeiro (necessário para contratos)
            logger.info("=== INICIANDO CONSUMO DE UNIDADES GESTORAS ===");
            try {
                UnidadeGestoraDTO ugDto = new UnidadeGestoraDTO();
                List<UnidadeGestoraDTO> ugResults = unidadeGestoraService.consumirPersistir(ugDto);
                int ugCount = ugResults != null ? ugResults.size() : 0;
                processingResults.put("UnidadeGestora", ugCount);
                totalRecordsProcessed += ugCount;
                
                logger.info("Unidades Gestoras processadas: {}", ugCount);
                
            } catch (Exception e) {
                logger.error("Erro ao consumir Unidades Gestoras", e);
                processingResults.put("UnidadeGestora", 0);
            }
            
            // 2. Aguardar um pouco antes de consumir contratos
            Thread.sleep(2000);
            
            // 3. Consumir Contratos Fiscais
            logger.info("=== INICIANDO CONSUMO DE CONTRATOS FISCAIS ===");
            try {
                ContratosFiscaisDTO contratoDto = new ContratosFiscaisDTO();
                List<ContratosFiscaisDTO> contratoResults = contratosFiscaisService.consumirPersistir(contratoDto);
                int contratoCount = contratoResults != null ? contratoResults.size() : 0;
                processingResults.put("ContratosFiscais", contratoCount);
                totalRecordsProcessed += contratoCount;
                
                logger.info("Contratos Fiscais processados: {}", contratoCount);
                
            } catch (Exception e) {
                logger.error("Erro ao consumir Contratos Fiscais", e);
                processingResults.put("ContratosFiscais", 0);
            }
            
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "SCHEDULED_CONTRACT_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "RESULTS", processingResults.toString());

            logger.info("=== EXECUÇÃO CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);
            logger.info("Resultados por tipo: {}", processingResults);
            
        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução automática", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "SCHEDULED_EXECUTION", totalExecutionTime, e,
                "ENDPOINTS", "MULTIPLE_ENDPOINTS");
            logger.error("Erro durante execução do scheduler", e);
        } finally {
            isFirstExecution = false;
            MDCUtil.clear();
        }
    }
    
    /**
     * Método para execução manual via endpoint (útil para testes)
     */
    public Map<String, Object> executeManually() {
        logger.info("=== EXECUÇÃO MANUAL SOLICITADA ===");
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            executeContractConsumption();
            
            result.put("status", "SUCCESS");
            result.put("message", "Execução manual concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }
        
        return result;
    }
    
    /**
     * Método para verificar status do scheduler
     */
    public Map<String, Object> getSchedulerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("schedulerActive", true);
        status.put("firstExecutionCompleted", !isFirstExecution);
        status.put("nextScheduledExecution", "2:45 AM daily (if enabled)");
        status.put("testExecutionOnStartup", "5 seconds after application ready");
        
        return status;
    }
}
