package br.gov.se.setc.controller;

import br.gov.se.setc.scheduler.ContractConsumptionScheduler;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para gerenciamento e monitoramento do scheduler
 */
@RestController
@RequestMapping("/scheduler")
@Tag(name = "Scheduler", description = "Endpoints para gerenciamento do scheduler de consumo de contratos")
public class SchedulerController {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
    
    @Autowired
    private ContractConsumptionScheduler scheduler;
    
    /**
     * Executa o consumo de contratos manualmente
     */
    @PostMapping("/execute")
    @Operation(summary = "Execução manual", description = "Executa o consumo de contratos manualmente para testes")
    @LogOperation(operation = "MANUAL_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeManually() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setComponent("SCHEDULER_CONTROLLER");
        MDCUtil.setOperation("MANUAL_EXECUTION");
        
        logger.info("Solicitação de execução manual recebida - Correlation ID: {}", correlationId);
        
        try {
            Map<String, Object> result = scheduler.executeManually();
            result.put("correlationId", correlationId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Erro durante execução manual", e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução manual: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            errorResult.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    
    /**
     * Verifica o status do scheduler
     */
    @GetMapping("/status")
    @Operation(summary = "Status do scheduler", description = "Retorna informações sobre o status atual do scheduler")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        try {
            Map<String, Object> status = scheduler.getSchedulerStatus();
            status.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            logger.error("Erro ao obter status do scheduler", e);
            
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "ERROR");
            errorStatus.put("message", "Erro ao obter status: " + e.getMessage());
            errorStatus.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorStatus);
        }
    }
    
    /**
     * Informações sobre configuração do scheduler
     */
    @GetMapping("/info")
    @Operation(summary = "Informações do scheduler", description = "Retorna informações detalhadas sobre a configuração do scheduler")
    public ResponseEntity<Map<String, Object>> getSchedulerInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("description", "Scheduler para consumo automático de contratos da SEFAZ");
        info.put("testExecution", "10 segundos após inicialização da aplicação - Empenho");
        info.put("productionSchedule", "Diariamente às 2:45 AM (se habilitado)");
        info.put("manualExecution", Map.of(
            "allEntities", "POST /scheduler/execute",
            "pagamentoOnly", "POST /scheduler/execute/pagamento",
            "liquidacaoOnly", "POST /scheduler/execute/liquidacao",
            "ordemFornecimentoOnly", "POST /scheduler/execute/ordem-fornecimento",
            "dadosOrcamentariosOnly", "POST /scheduler/execute/dados-orcamentarios",
            "empenhoOnly", "POST /scheduler/execute/empenho"
        ));
        info.put("endpoints", Map.of(
            "contratosFiscais", "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais",
            "unidadeGestora", "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora"
        ));
        info.put("logging", Map.of(
            "structured", true,
            "performance", true,
            "correlation", true,
            "logFiles", Map.of(
                "contracts", "./logs/contracts/contract-consumption.log",
                "performance", "./logs/performance/performance.log",
                "errors", "./logs/errors/errors.log"
            )
        ));
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Executa manualmente apenas o processamento de Pagamento
     */
    @PostMapping("/execute/pagamento")
    @Operation(summary = "Executar Apenas Pagamento", description = "Executa manualmente apenas o processamento da entidade Pagamento")
    @LogOperation(operation = "MANUAL_PAGAMENTO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executePagamentoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();

        try {
            logger.info("Execução manual de Pagamento solicitada via endpoint");
            Map<String, Object> result = scheduler.executePagamentoManually();

            logger.info("Execução manual de Pagamento concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro na execução manual de Pagamento via endpoint", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Pagamento: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Executa manualmente apenas o processamento de Liquidação
     */
    @PostMapping("/execute/liquidacao")
    @Operation(summary = "Executar Apenas Liquidação", description = "Executa manualmente apenas o processamento da entidade Liquidação")
    @LogOperation(operation = "MANUAL_LIQUIDACAO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeLiquidacaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();

        try {
            logger.info("Execução manual de Liquidação solicitada via endpoint");
            Map<String, Object> result = scheduler.executeLiquidacaoManually();

            logger.info("Execução manual de Liquidação concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro na execução manual de Liquidação via endpoint", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Liquidação: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Executa manualmente apenas o processamento de Ordem de Fornecimento
     */
    @PostMapping("/execute/ordem-fornecimento")
    @Operation(summary = "Executar Apenas Ordem de Fornecimento", description = "Executa manualmente apenas o processamento da entidade Ordem de Fornecimento")
    @LogOperation(operation = "MANUAL_ORDEM_FORNECIMENTO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeOrdemFornecimentoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();

        try {
            logger.info("Execução manual de Ordem de Fornecimento solicitada via endpoint");
            Map<String, Object> result = scheduler.executeOrdemFornecimentoManually();

            logger.info("Execução manual de Ordem de Fornecimento concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro na execução manual de Ordem de Fornecimento via endpoint", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Ordem de Fornecimento: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Executa manualmente apenas o processamento de Dados Orçamentários
     */
    @PostMapping("/execute/dados-orcamentarios")
    @Operation(summary = "Executar Apenas Dados Orçamentários", description = "Executa manualmente apenas o processamento da entidade Dados Orçamentários")
    @LogOperation(operation = "MANUAL_DADOS_ORCAMENTARIOS_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeDadosOrcamentariosOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();

        try {
            logger.info("Execução manual de Dados Orçamentários solicitada via endpoint");
            Map<String, Object> result = scheduler.executeDadosOrcamentariosManually();

            logger.info("Execução manual de Dados Orçamentários concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro na execução manual de Dados Orçamentários via endpoint", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Dados Orçamentários: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Executa manualmente apenas o processamento de Empenho
     */
    @PostMapping("/execute/empenho")
    @Operation(summary = "Executar Apenas Empenho", description = "Executa manualmente apenas o processamento da entidade Empenho")
    @LogOperation(operation = "MANUAL_EMPENHO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeEmpenhoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();

        try {
            logger.info("Execução manual de Empenho solicitada via endpoint");
            Map<String, Object> result = scheduler.executeEmpenhoManually();

            logger.info("Execução manual de Empenho concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro na execução manual de Empenho via endpoint", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Empenho: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Endpoint de teste simples
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Endpoint simples para verificar se o controller está funcionando")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Scheduler controller is running");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
