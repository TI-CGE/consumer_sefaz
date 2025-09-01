package br.gov.se.setc.controller;
import br.gov.se.setc.scheduler.ContractConsumptionScheduler;
import br.gov.se.setc.consumer.service.PrevisaoRealizacaoReceitaMultiMesService;
import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
 * Controller para gerenciamento e monitoramento do scheduler
 */
@RestController
@RequestMapping("/scheduler")
@Tag(name = "Scheduler", description = "API para gerenciamento e controle do scheduler de consumo automático de dados")
public class SchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
    @Autowired
    private ContractConsumptionScheduler scheduler;
    @Autowired
    private PrevisaoRealizacaoReceitaMultiMesService previsaoRealizacaoReceitaMultiMesService;
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
        info.put("testExecution", "DESABILITADO - Sem execução automática no startup");
        info.put("productionSchedule", "Diariamente às 2:45 AM (se habilitado)");
        Map<String, String> manualExecutionMap = new HashMap<>();
        manualExecutionMap.put("allEntities", "POST /scheduler/execute");
        manualExecutionMap.put("pagamentoOnly", "POST /scheduler/execute/pagamento");
        manualExecutionMap.put("liquidacaoOnly", "POST /scheduler/execute/liquidacao");
        manualExecutionMap.put("ordemFornecimentoOnly", "POST /scheduler/execute/ordem-fornecimento");
        manualExecutionMap.put("dadosOrcamentariosOnly", "POST /scheduler/execute/dados-orcamentarios");
        manualExecutionMap.put("empenhoOnly", "POST /scheduler/execute/empenho");
        manualExecutionMap.put("contratoOnly", "POST /scheduler/execute/contrato");
        manualExecutionMap.put("contratoEmpenhoOnly", "POST /scheduler/execute/contrato-empenho");
        manualExecutionMap.put("baseDespesaCredorOnly", "POST /scheduler/execute/base-despesa-credor");
        manualExecutionMap.put("baseDespesaLicitacaoOnly", "POST /scheduler/execute/base-despesa-licitacao");
        manualExecutionMap.put("termoOnly", "POST /scheduler/execute/termo");
            manualExecutionMap.put("despesaConvenioOnly", "POST /scheduler/execute/convenio/despesa");
        manualExecutionMap.put("previsaoRealizacaoReceitaOnly", "POST /scheduler/execute/previsao-realizacao-receita");
        manualExecutionMap.put("previsaoRealizacaoReceitaMultiMes", "POST /scheduler/execute/previsao-realizacao-receita-multi-mes");
        manualExecutionMap.put("previsaoRealizacaoReceitaMesEspecifico", "POST /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes}");
        manualExecutionMap.put("despesaDetalhadaOnly", "POST /scheduler/execute/despesa-detalhada");
        info.put("manualExecution", manualExecutionMap);
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
     * Executa manualmente apenas o processamento de Totalizadores de Execução
     */
    @PostMapping("/execute/totalizadores-execucao")
    @Operation(summary = "Executar Apenas Totalizadores de Execução", description = "Executa manualmente apenas o processamento da entidade Totalizadores de Execução")
    @LogOperation(operation = "MANUAL_TOTALIZADORES_EXECUCAO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeTotalizadoresExecucaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Totalizadores de Execução solicitada via endpoint");
            Map<String, Object> result = scheduler.executeTotalizadoresExecucaoManually();
            logger.info("Execução manual de Totalizadores de Execução concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Totalizadores de Execução", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Consulta Gerencial
     */
    @PostMapping("/execute/consulta-gerencial")
    @Operation(summary = "Executar Apenas Consulta Gerencial", description = "Executa manualmente apenas o processamento da entidade Consulta Gerencial (Diárias)")
    @LogOperation(operation = "MANUAL_CONSULTA_GERENCIAL_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeConsultaGerencialOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Consulta Gerencial solicitada via endpoint");
            Map<String, Object> result = scheduler.executeConsultaGerencialManually();
            logger.info("Execução manual de Consulta Gerencial concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Consulta Gerencial", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Contratos
     */
    @PostMapping("/execute/contrato")
    @Operation(summary = "Executar Apenas Contratos", description = "Executa manualmente apenas o processamento da entidade Contratos")
    @LogOperation(operation = "MANUAL_CONTRATO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeContratoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Contratos solicitada via endpoint");
            Map<String, Object> result = scheduler.executeContratoManually();
            logger.info("Execução manual de Contratos concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Contratos", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Contrato-Empenho
     */
    @PostMapping("/execute/contrato-empenho")
    @Operation(summary = "Executar Apenas Contrato-Empenho", description = "Executa manualmente apenas o processamento da entidade Contrato-Empenho")
    @LogOperation(operation = "MANUAL_CONTRATO_EMPENHO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeContratoEmpenhoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Contrato-Empenho solicitada via endpoint");
            Map<String, Object> result = scheduler.executeContratoEmpenhoManually();
            logger.info("Execução manual de Contrato-Empenho concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Contrato-Empenho", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
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
    /**
     * Executa manualmente apenas o processamento de Base Despesa Credor
     */
    @PostMapping("/execute/base-despesa-credor")
    @Operation(summary = "Executar Apenas Base Despesa Credor", description = "Executa manualmente apenas o processamento da entidade Base Despesa Credor com suporte a paginação")
    @LogOperation(operation = "MANUAL_BASE_DESPESA_CREDOR_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeBaseDespesaCredorOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Base Despesa Credor solicitada via endpoint");
            Map<String, Object> result = scheduler.executeBaseDespesaCredorManually();
            logger.info("Execução manual de Base Despesa Credor concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro na execução manual de Base Despesa Credor via endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Base Despesa Credor: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Base Despesa Licitação
     */
    @PostMapping("/execute/base-despesa-licitacao")
    @Operation(summary = "Executar Apenas Base Despesa Licitação", description = "Executa manualmente apenas o processamento da entidade Base Despesa Licitação")
    @LogOperation(operation = "MANUAL_BASE_DESPESA_LICITACAO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeBaseDespesaLicitacaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Base Despesa Licitação solicitada via endpoint");
            Map<String, Object> result = scheduler.executeBaseDespesaLicitacaoManually();
            logger.info("Execução manual de Base Despesa Licitação concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro na execução manual de Base Despesa Licitação via endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execução manual de Base Despesa Licitação: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Despesa de Convenio
     */
    @PostMapping("/execute/convenio/despesa")
    @Operation(summary = "Executar Apenas Despesa de Convenio", description = "Executa manualmente apenas o processamento da entidade Despesa de Convenio")
    @LogOperation(operation = "MANUAL_DESPESA_CONVENIO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeDespesaConvenioOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execucao manual de Despesa de Convenio solicitada via endpoint");
            Map<String, Object> result = scheduler.executeDespesaConvenioManually();
            logger.info("Execucao manual de Despesa de Convenio concluida com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro na execucao manual de Despesa de Convenio via endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro durante execucao manual de Despesa de Convenio: " + e.getMessage());
            errorResponse.put("correlationId", correlationId);
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResponse);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Termo (Convênios)
     */
    @PostMapping("/execute/termo")
    @Operation(summary = "Executar Apenas Termo (Convênios)", description = "Executa manualmente apenas o processamento da entidade Termo (Convênios)")
    @LogOperation(operation = "MANUAL_TERMO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeTermoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("=== EXECUÇÃO MANUAL DE TERMO (CONVÊNIOS) INICIADA ===");
            logger.info("Correlation ID: {}", correlationId);
            Map<String, Object> result = scheduler.executeTermoManually();
            if ("SUCCESS".equals(result.get("status"))) {
                logger.info("Execução manual de Termo concluída com sucesso");
                return ResponseEntity.ok(result);
            } else {
                logger.error("Falha na execução manual de Termo: {}", result.get("message"));
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Termo", e);
            Map<String, Object> errorResult = Map.of(
                "status", "ERROR",
                "message", "Erro inesperado: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            );
            return ResponseEntity.status(500).body(errorResult);
        }
    }
    /**
     * Executa manualmente apenas o processamento de Previsão Realização Receita (usando scheduler padrão)
     */
    @PostMapping("/execute/previsao-realizacao-receita")
    @Operation(summary = "Executar Apenas Previsão Realização Receita",
               description = "Executa manualmente apenas o processamento da entidade Previsão Realização Receita usando o scheduler padrão. " +
                           "Para execução multi-mês (todos os 12 meses), use /execute/previsao-realizacao-receita-multi-mes")
    @LogOperation(operation = "MANUAL_PREVISAO_REALIZACAO_RECEITA_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executePrevisaoRealizacaoReceitaOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Previsão Realização Receita solicitada via endpoint");
            Map<String, Object> result = scheduler.executePrevisaoRealizacaoReceitaManually();
            logger.info("Execução manual de Previsão Realização Receita concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Previsão Realização Receita", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente a busca de todos os 12 meses da Previsão Realização Receita
     */
    @PostMapping("/execute/previsao-realizacao-receita-multi-mes")
    @Operation(summary = "Executar busca de todos os 12 meses",
               description = "Executa manualmente a busca de Previsão Realização Receita para todos os 12 meses do ano. " +
                           "Faz 12 requisições sequenciais (uma para cada mês) com pausa de 500ms entre elas.")
    @LogOperation(operation = "MANUAL_PREVISAO_REALIZACAO_RECEITA_MULTI_MES_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executePrevisaoRealizacaoReceitaMultiMes() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual multi-mês de Previsão Realização Receita solicitada via endpoint");
            String resultado = previsaoRealizacaoReceitaMultiMesService.executarManual();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", resultado);
            response.put("correlationId", correlationId);
            logger.info("Execução manual multi-mês de Previsão Realização Receita concluída");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro durante execução manual multi-mês de Previsão Realização Receita", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente a busca de um mês específico da Previsão Realização Receita
     */
    @PostMapping("/execute/previsao-realizacao-receita-multi-mes/mes/{mes}")
    @Operation(summary = "Executar busca de um mês específico",
               description = "Executa manualmente a busca de Previsão Realização Receita para um mês específico (1-12).")
    @LogOperation(operation = "MANUAL_PREVISAO_REALIZACAO_RECEITA_MES_ESPECIFICO_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executePrevisaoRealizacaoReceitaMesEspecifico(
            @Parameter(description = "Número do mês (1-12)", example = "12")
            @PathVariable int mes) {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            if (mes < 1 || mes > 12) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("status", "ERROR");
                errorResult.put("message", "Mês inválido. Deve estar entre 1 e 12.");
                errorResult.put("correlationId", correlationId);
                return ResponseEntity.badRequest().body(errorResult);
            }
            logger.info("Execução manual para mês {} de Previsão Realização Receita solicitada via endpoint", mes);
            List<PrevisaoRealizacaoReceitaDTO> resultado = previsaoRealizacaoReceitaMultiMesService.consumirMesEspecifico(mes);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Execução do mês " + mes + " concluída! Registros processados: " +
                        (resultado != null ? resultado.size() : 0));
            response.put("recordsProcessed", resultado != null ? resultado.size() : 0);
            response.put("month", mes);
            response.put("correlationId", correlationId);
            logger.info("Execução manual para mês {} de Previsão Realização Receita concluída", mes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro durante execução manual para mês {} de Previsão Realização Receita", mes, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("month", mes);
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
    /**
     * Executa manualmente apenas o processamento de Despesa Detalhada
     */
    @PostMapping("/execute/despesa-detalhada")
    @Operation(summary = "Executar Apenas Despesa Detalhada",
               description = "Executa manualmente apenas o processamento da entidade Despesa Detalhada")
    @LogOperation(operation = "MANUAL_DESPESA_DETALHADA_EXECUTION", component = "SCHEDULER_CONTROLLER")
    public ResponseEntity<Map<String, Object>> executeDespesaDetalhadaOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        try {
            logger.info("Execução manual de Despesa Detalhada solicitada via endpoint");
            Map<String, Object> result = scheduler.executeDespesaDetalhadaManually();
            logger.info("Execução manual de Despesa Detalhada concluída com status: {}", result.get("status"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro durante execução manual de Despesa Detalhada", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Erro durante execução: " + e.getMessage());
            errorResult.put("correlationId", correlationId);
            return ResponseEntity.internalServerError().body(errorResult);
        } finally {
            MDCUtil.clear();
        }
    }
}
