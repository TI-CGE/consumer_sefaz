package br.gov.se.setc.scheduler;

import br.gov.se.setc.consumer.dto.BaseDespesaCredorDTO;
import br.gov.se.setc.consumer.dto.BaseDespesaLicitacaoDTO;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.dto.ContratoEmpenhoDTO;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.dto.DadosOrcamentariosDTO;
import br.gov.se.setc.consumer.dto.EmpenhoDTO;
import br.gov.se.setc.consumer.dto.LiquidacaoDTO;
import br.gov.se.setc.consumer.dto.OrdemFornecimentoDTO;
import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.dto.TotalizadoresExecucaoDTO;
import br.gov.se.setc.consumer.dto.UnidadeGestoraDTO;
import br.gov.se.setc.consumer.dto.DespesaConvenioDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.logging.MarkdownLogger;
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
    @Qualifier("receitaConsumoApiService")
    private ConsumoApiService<ReceitaDTO> receitaConsumoApiService;

    @Autowired
    @Qualifier("pagamentoConsumoApiService")
    private ConsumoApiService<PagamentoDTO> pagamentoConsumoApiService;

    @Autowired
    @Qualifier("ordemFornecimentoConsumoApiService")
    private ConsumoApiService<OrdemFornecimentoDTO> ordemFornecimentoConsumoApiService;

    @Autowired
    @Qualifier("liquidacaoConsumoApiService")
    private ConsumoApiService<LiquidacaoDTO> liquidacaoConsumoApiService;

    @Autowired
    @Qualifier("dadosOrcamentariosConsumoApiService")
    private ConsumoApiService<DadosOrcamentariosDTO> dadosOrcamentariosConsumoApiService;

    @Autowired
    @Qualifier("empenhoConsumoApiService")
    private ConsumoApiService<EmpenhoDTO> empenhoConsumoApiService;

    @Autowired
    @Qualifier("totalizadoresExecucaoConsumoApiService")
    private ConsumoApiService<TotalizadoresExecucaoDTO> totalizadoresExecucaoConsumoApiService;

    @Autowired
    @Qualifier("consultaGerencialConsumoApiService")
    private ConsumoApiService<ConsultaGerencialDTO> consultaGerencialConsumoApiService;

    @Autowired
    @Qualifier("contratoConsumoApiService")
    private ConsumoApiService<ContratoDTO> contratoConsumoApiService;

    @Autowired
    @Qualifier("contratoEmpenhoConsumoApiService")
    private ConsumoApiService<ContratoEmpenhoDTO> contratoEmpenhoConsumoApiService;

    @Autowired
    @Qualifier("baseDespesaCredorConsumoApiService")
    private ConsumoApiService<BaseDespesaCredorDTO> baseDespesaCredorConsumoApiService;

    @Autowired
    @Qualifier("baseDespesaLicitacaoConsumoApiService")
    private ConsumoApiService<BaseDespesaLicitacaoDTO> baseDespesaLicitacaoConsumoApiService;

    @Autowired
    @Qualifier("termoConsumoApiService")
    private ConsumoApiService<TermoDTO> termoConsumoApiService;

    @Autowired
    @Qualifier("despesaConvenioConsumoApiService")
    private ConsumoApiService<DespesaConvenioDTO> despesaConvenioConsumoApiService;

    @Autowired
    @Qualifier("previsaoRealizacaoReceitaConsumoApiService")
    private ConsumoApiService<PrevisaoRealizacaoReceitaDTO> previsaoRealizacaoReceitaConsumoApiService;

    @Autowired
    private UnifiedLogger unifiedLogger;

    @Autowired
    private UserFriendlyLogger userFriendlyLogger;

    @Autowired
    private MarkdownLogger markdownLogger;

    private boolean isFirstExecution = true;

    /**
     * Executa apenas Pagamento 10 segundos após a aplicação estar pronta (para testes)
     * COMENTADO - Sem execução automática no startup
     */
    // @EventListener(ApplicationReadyEvent.class)
    public void executeOnStartup() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000); // Aguarda 10 segundos

                String correlationId = MDCUtil.generateAndSetCorrelationId();
                unifiedLogger.logApplicationEvent("SCHEDULER_STARTUP_TEST", "Execução de teste do scheduler - Pagamento");
                unifiedLogger.logOperationStart("SCHEDULER", "STARTUP_TEST_PAGAMENTO", "CORRELATION_ID", correlationId);

                logger.info("=== INICIANDO EXECUÇÃO DE TESTE DO SCHEDULER - PAGAMENTO ===");
                executePagamentoOnly();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Execução de startup interrompida", e);
                unifiedLogger.logOperationError("SCHEDULER", "STARTUP_TEST", 0, e, "CORRELATION_ID", MDCUtil.getCorrelationId());
            }
        });
    }

    /**
     * Execução agendada para produção - diariamente às 2:45 AM
     * Processa todas as entidades (UG, Contratos, Receitas, Pagamentos)
     * Comentado por padrão para testes
     */
    // @Scheduled(cron = "0 45 2 * * *")
    public void scheduledExecution() {
        logger.info("=== INICIANDO EXECUÇÃO AGENDADA DO SCHEDULER - TODAS AS ENTIDADES ===");
        executeAllEntities();
    }

    /**
     * Execução a cada 10 minutos para testes (comentado por padrão)
     * Descomente para testes frequentes
     */
    // @Scheduled(fixedRate = 600000) // 10 minutos
    public void frequentTestExecution() {
        if (!isFirstExecution) {
            logger.info("=== EXECUÇÃO DE TESTE FREQUENTE ===");
            executeAllEntities();
        }
    }

    /**
     * Método principal que executa o consumo de todas as entidades
     */
    @LogOperation(operation = "SCHEDULED_ALL_ENTITIES_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 60000)
    private void executeAllEntities() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "SCHEDULED_CONTRACT_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        Map<String, Integer> processingResults = new HashMap<>();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Automática do Scheduler");

        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "SCHEDULED_EXECUTION", "ENDPOINTS", "MULTIPLE_ENDPOINTS");

            // Log estruturado em markdown
            markdownSection.info("Iniciando consumo automático de dados da SEFAZ")
                          .info("Correlation ID: " + correlationId);

            // 1. Consumir Unidades Gestoras primeiro (necessário para contratos)
            logger.info("=== INICIANDO CONSUMO DE UNIDADES GESTORAS ===");
            markdownSection.progress("Processando Unidades Gestoras...");

            try {
                long ugStartTime = System.currentTimeMillis();
                UnidadeGestoraDTO ugDto = new UnidadeGestoraDTO();
                List<UnidadeGestoraDTO> ugResults = unidadeGestoraService.consumirPersistir(ugDto);
                int ugCount = ugResults != null ? ugResults.size() : 0;
                processingResults.put("UnidadeGestora", ugCount);
                totalRecordsProcessed += ugCount;

                long ugDuration = System.currentTimeMillis() - ugStartTime;
                logger.info("Unidades Gestoras processadas: {}", ugCount);
                markdownSection.success(ugCount + " Unidades Gestoras processadas", ugDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Unidades Gestoras", e);
                processingResults.put("UnidadeGestora", 0);
                markdownSection.error("Falha no processamento de Unidades Gestoras: " + e.getMessage());
            }

            // 2. Aguardar um pouco antes de consumir contratos
            Thread.sleep(2000);

            // 3. Consumir Contratos Fiscais
            logger.info("=== INICIANDO CONSUMO DE CONTRATOS FISCAIS ===");
            markdownSection.progress("Processando Contratos Fiscais...");

            try {
                long cfStartTime = System.currentTimeMillis();
                ContratosFiscaisDTO contratoDto = new ContratosFiscaisDTO();
                List<ContratosFiscaisDTO> contratoResults = contratosFiscaisService.consumirPersistir(contratoDto);
                int contratoCount = contratoResults != null ? contratoResults.size() : 0;
                processingResults.put("ContratosFiscais", contratoCount);
                totalRecordsProcessed += contratoCount;

                long cfDuration = System.currentTimeMillis() - cfStartTime;
                logger.info("Contratos Fiscais processados: {}", contratoCount);
                markdownSection.success(contratoCount + " Contratos Fiscais processados", cfDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Contratos Fiscais", e);
                processingResults.put("ContratosFiscais", 0);
                markdownSection.error("Falha no processamento de Contratos Fiscais: " + e.getMessage());
            }

            // 4. Aguardar um pouco antes de consumir contratos
            Thread.sleep(2000);

            // 4.1. Consumir Contratos
            logger.info("=== INICIANDO CONSUMO DE CONTRATOS ===");
            markdownSection.progress("Processando Contratos...");

            try {
                long contratoStartTime = System.currentTimeMillis();
                ContratoDTO contratoDto = new ContratoDTO();
                List<ContratoDTO> contratoResults = contratoConsumoApiService.consumirPersistir(contratoDto);
                int contratoCount = contratoResults != null ? contratoResults.size() : 0;
                processingResults.put("Contrato", contratoCount);
                totalRecordsProcessed += contratoCount;

                long contratoDuration = System.currentTimeMillis() - contratoStartTime;
                logger.info("Contratos processados: {}", contratoCount);
                markdownSection.success(contratoCount + " registros de Contrato processados", contratoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Contratos", e);
                processingResults.put("Contrato", 0);
                markdownSection.error("Falha no processamento de Contratos: " + e.getMessage());
            }

            // 4.2. Aguardar um pouco antes de consumir contrato-empenho
            Thread.sleep(2000);

            // 4.3. Consumir Contrato-Empenho
            logger.info("=== INICIANDO CONSUMO DE CONTRATO-EMPENHO ===");
            markdownSection.progress("Processando Contrato-Empenho...");

            try {
                long contratoEmpenhoStartTime = System.currentTimeMillis();
                ContratoEmpenhoDTO contratoEmpenhoDto = new ContratoEmpenhoDTO();
                List<ContratoEmpenhoDTO> contratoEmpenhoResults = contratoEmpenhoConsumoApiService.consumirPersistir(contratoEmpenhoDto);
                int contratoEmpenhoCount = contratoEmpenhoResults != null ? contratoEmpenhoResults.size() : 0;
                processingResults.put("ContratoEmpenho", contratoEmpenhoCount);
                totalRecordsProcessed += contratoEmpenhoCount;

                long contratoEmpenhoDuration = System.currentTimeMillis() - contratoEmpenhoStartTime;
                logger.info("Contrato-Empenho processados: {}", contratoEmpenhoCount);
                markdownSection.success(contratoEmpenhoCount + " registros de Contrato-Empenho processados", contratoEmpenhoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Contrato-Empenho", e);
                processingResults.put("ContratoEmpenho", 0);
                markdownSection.error("Falha no processamento de Contrato-Empenho: " + e.getMessage());
            }

            // 5. Aguardar um pouco antes de consumir receita
            Thread.sleep(2000);

            // 6. Consumir Receita
            logger.info("=== INICIANDO CONSUMO DE RECEITA ===");
            markdownSection.progress("Processando Receita...");

            try {
                long receitaStartTime = System.currentTimeMillis();
                ReceitaDTO receitaDto = new ReceitaDTO();
                List<ReceitaDTO> receitaResults = receitaConsumoApiService.consumirPersistir(receitaDto);
                int receitaCount = receitaResults != null ? receitaResults.size() : 0;
                processingResults.put("Receita", receitaCount);
                totalRecordsProcessed += receitaCount;

                long receitaDuration = System.currentTimeMillis() - receitaStartTime;
                logger.info("Receita processada: {}", receitaCount);
                markdownSection.success(receitaCount + " registros de Receita processados", receitaDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Receita", e);
                processingResults.put("Receita", 0);
                markdownSection.error("Falha no processamento de Receita: " + e.getMessage());
            }

            // 6. Aguardar um pouco antes de consumir pagamentos
            Thread.sleep(2000);

            // 7. Consumir Pagamentos
            logger.info("=== INICIANDO CONSUMO DE PAGAMENTOS ===");
            markdownSection.progress("Processando Pagamentos...");

            try {
                long pagamentoStartTime = System.currentTimeMillis();
                PagamentoDTO pagamentoDto = new PagamentoDTO();
                List<PagamentoDTO> pagamentoResults = pagamentoConsumoApiService.consumirPersistir(pagamentoDto);
                int pagamentoCount = pagamentoResults != null ? pagamentoResults.size() : 0;
                processingResults.put("Pagamento", pagamentoCount);
                totalRecordsProcessed += pagamentoCount;

                long pagamentoDuration = System.currentTimeMillis() - pagamentoStartTime;
                logger.info("Pagamentos processados: {}", pagamentoCount);
                markdownSection.success(pagamentoCount + " registros de Pagamento processados", pagamentoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Pagamentos", e);
                processingResults.put("Pagamento", 0);
                markdownSection.error("Falha no processamento de Pagamentos: " + e.getMessage());
            }

            // 8. Aguardar um pouco antes de consumir liquidações
            Thread.sleep(2000);

            // 9. Consumir Liquidações
            logger.info("=== INICIANDO CONSUMO DE LIQUIDAÇÕES ===");
            markdownSection.progress("Processando Liquidações...");

            try {
                long liquidacaoStartTime = System.currentTimeMillis();
                LiquidacaoDTO liquidacaoDto = new LiquidacaoDTO();
                List<LiquidacaoDTO> liquidacaoResults = liquidacaoConsumoApiService.consumirPersistir(liquidacaoDto);
                int liquidacaoCount = liquidacaoResults != null ? liquidacaoResults.size() : 0;
                processingResults.put("Liquidacao", liquidacaoCount);
                totalRecordsProcessed += liquidacaoCount;

                long liquidacaoDuration = System.currentTimeMillis() - liquidacaoStartTime;
                logger.info("Liquidações processadas: {}", liquidacaoCount);
                markdownSection.success(liquidacaoCount + " registros de Liquidação processados", liquidacaoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Liquidações", e);
                processingResults.put("Liquidacao", 0);
                markdownSection.error("Falha no processamento de Liquidações: " + e.getMessage());
            }

            // 10. Aguardar um pouco antes de consumir ordens de fornecimento
            Thread.sleep(2000);

            // 11. Consumir Ordens de Fornecimento
            logger.info("=== INICIANDO CONSUMO DE ORDENS DE FORNECIMENTO ===");
            markdownSection.progress("Processando Ordens de Fornecimento...");

            try {
                long ordemFornecimentoStartTime = System.currentTimeMillis();
                OrdemFornecimentoDTO ordemFornecimentoDto = new OrdemFornecimentoDTO();
                List<OrdemFornecimentoDTO> ordemFornecimentoResults = ordemFornecimentoConsumoApiService.consumirPersistir(ordemFornecimentoDto);
                int ordemFornecimentoCount = ordemFornecimentoResults != null ? ordemFornecimentoResults.size() : 0;
                processingResults.put("OrdemFornecimento", ordemFornecimentoCount);
                totalRecordsProcessed += ordemFornecimentoCount;

                long ordemFornecimentoDuration = System.currentTimeMillis() - ordemFornecimentoStartTime;
                logger.info("Ordens de Fornecimento processadas: {}", ordemFornecimentoCount);
                markdownSection.success(ordemFornecimentoCount + " registros de Ordem de Fornecimento processados", ordemFornecimentoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Ordens de Fornecimento", e);
                processingResults.put("OrdemFornecimento", 0);
                markdownSection.error("Falha no processamento de Ordens de Fornecimento: " + e.getMessage());
            }

            // 6. Consumir Dados Orçamentários
            logger.info("=== INICIANDO CONSUMO DE DADOS ORÇAMENTÁRIOS ===");
            markdownSection.progress("Processando Dados Orçamentários...");

            try {
                long dadosOrcamentariosStartTime = System.currentTimeMillis();
                DadosOrcamentariosDTO dadosOrcamentariosDto = new DadosOrcamentariosDTO();
                List<DadosOrcamentariosDTO> dadosOrcamentariosResults = dadosOrcamentariosConsumoApiService.consumirPersistir(dadosOrcamentariosDto);
                int dadosOrcamentariosCount = dadosOrcamentariosResults != null ? dadosOrcamentariosResults.size() : 0;
                processingResults.put("DadosOrcamentarios", dadosOrcamentariosCount);
                totalRecordsProcessed += dadosOrcamentariosCount;

                long dadosOrcamentariosDuration = System.currentTimeMillis() - dadosOrcamentariosStartTime;
                logger.info("Dados Orçamentários processados: {}", dadosOrcamentariosCount);
                markdownSection.success(dadosOrcamentariosCount + " Dados Orçamentários processados", dadosOrcamentariosDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Dados Orçamentários", e);
                processingResults.put("DadosOrcamentarios", 0);
                markdownSection.error("Falha no processamento de Dados Orçamentários: " + e.getMessage());
            }

            // 13. Aguardar um pouco antes de consumir empenhos
            Thread.sleep(2000);

            // 14. Consumir Empenhos
            logger.info("=== INICIANDO CONSUMO DE EMPENHOS ===");
            markdownSection.progress("Processando Empenhos...");

            try {
                long empenhoStartTime = System.currentTimeMillis();
                EmpenhoDTO empenhoDto = new EmpenhoDTO();
                List<EmpenhoDTO> empenhoResults = empenhoConsumoApiService.consumirPersistir(empenhoDto);
                int empenhoCount = empenhoResults != null ? empenhoResults.size() : 0;
                processingResults.put("Empenho", empenhoCount);
                totalRecordsProcessed += empenhoCount;

                long empenhoDuration = System.currentTimeMillis() - empenhoStartTime;
                logger.info("Empenhos processados: {}", empenhoCount);
                markdownSection.success(empenhoCount + " registros de Empenho processados", empenhoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Empenhos", e);
                processingResults.put("Empenho", 0);
                markdownSection.error("Falha no processamento de Empenhos: " + e.getMessage());
            }

            // 15. Aguardar um pouco antes de consumir totalizadores de execução
            Thread.sleep(2000);

            // 16. Consumir Totalizadores de Execução
            logger.info("=== INICIANDO CONSUMO DE TOTALIZADORES DE EXECUÇÃO ===");
            markdownSection.progress("Processando Totalizadores de Execução...");

            try {
                long totalizadoresExecucaoStartTime = System.currentTimeMillis();
                TotalizadoresExecucaoDTO totalizadoresExecucaoDto = new TotalizadoresExecucaoDTO();
                List<TotalizadoresExecucaoDTO> totalizadoresExecucaoResults = totalizadoresExecucaoConsumoApiService.consumirPersistir(totalizadoresExecucaoDto);
                int totalizadoresExecucaoCount = totalizadoresExecucaoResults != null ? totalizadoresExecucaoResults.size() : 0;
                processingResults.put("TotalizadoresExecucao", totalizadoresExecucaoCount);
                totalRecordsProcessed += totalizadoresExecucaoCount;

                long totalizadoresExecucaoDuration = System.currentTimeMillis() - totalizadoresExecucaoStartTime;
                logger.info("Totalizadores de Execução processados: {}", totalizadoresExecucaoCount);
                markdownSection.success(totalizadoresExecucaoCount + " registros de Totalizadores de Execução processados", totalizadoresExecucaoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Totalizadores de Execução", e);
                processingResults.put("TotalizadoresExecucao", 0);
                markdownSection.error("Falha no processamento de Totalizadores de Execução: " + e.getMessage());
            }

            // 17. Aguardar um pouco antes de consumir consulta gerencial
            Thread.sleep(2000);

            // 18. Consumir Consulta Gerencial (Diárias)
            logger.info("=== INICIANDO CONSUMO DE CONSULTA GERENCIAL ===");
            markdownSection.progress("Processando Consulta Gerencial (Diárias)...");

            try {
                long consultaGerencialStartTime = System.currentTimeMillis();
                ConsultaGerencialDTO consultaGerencialDto = new ConsultaGerencialDTO();
                List<ConsultaGerencialDTO> consultaGerencialResults = consultaGerencialConsumoApiService.consumirPersistir(consultaGerencialDto);
                int consultaGerencialCount = consultaGerencialResults != null ? consultaGerencialResults.size() : 0;
                processingResults.put("ConsultaGerencial", consultaGerencialCount);
                totalRecordsProcessed += consultaGerencialCount;

                long consultaGerencialDuration = System.currentTimeMillis() - consultaGerencialStartTime;
                logger.info("Consulta Gerencial processada: {}", consultaGerencialCount);
                markdownSection.success(consultaGerencialCount + " registros de Consulta Gerencial processados", consultaGerencialDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Consulta Gerencial", e);
                processingResults.put("ConsultaGerencial", 0);
                markdownSection.error("Falha no processamento de Consulta Gerencial: " + e.getMessage());
            }

            Thread.sleep(2000);

            // 19. Consumir Base Despesa Credor
            logger.info("=== INICIANDO CONSUMO DE BASE DESPESA CREDOR ===");
            markdownSection.progress("Processando Base Despesa Credor...");

            try {
                long baseDespesaCredorStartTime = System.currentTimeMillis();
                BaseDespesaCredorDTO baseDespesaCredorDto = new BaseDespesaCredorDTO();
                List<BaseDespesaCredorDTO> baseDespesaCredorResults = baseDespesaCredorConsumoApiService.consumirPersistir(baseDespesaCredorDto);
                int baseDespesaCredorCount = baseDespesaCredorResults != null ? baseDespesaCredorResults.size() : 0;
                processingResults.put("BaseDespesaCredor", baseDespesaCredorCount);
                totalRecordsProcessed += baseDespesaCredorCount;

                long baseDespesaCredorDuration = System.currentTimeMillis() - baseDespesaCredorStartTime;
                logger.info("Base Despesa Credor processada: {}", baseDespesaCredorCount);
                markdownSection.success(baseDespesaCredorCount + " registros de Base Despesa Credor processados", baseDespesaCredorDuration);

                // Log informações de paginação se disponível
                if (baseDespesaCredorResults != null && !baseDespesaCredorResults.isEmpty() &&
                    baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao() != null) {
                    logger.info("Paginação processada - Total de faixas: {}", baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao());
                    markdownSection.info("Paginação: " + baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao() + " faixas processadas");
                }

            } catch (Exception e) {
                logger.error("Erro ao consumir Base Despesa Credor", e);
                processingResults.put("BaseDespesaCredor", 0);
                markdownSection.error("Falha no processamento de Base Despesa Credor: " + e.getMessage());
            }

            // 20. Consumir Base Despesa Licitação
            logger.info("=== INICIANDO CONSUMO DE BASE DESPESA LICITAÇÃO ===");
            markdownSection.progress("Processando Base Despesa Licitação...");

            try {
                long baseDespesaLicitacaoStartTime = System.currentTimeMillis();
                BaseDespesaLicitacaoDTO baseDespesaLicitacaoDto = new BaseDespesaLicitacaoDTO();
                List<BaseDespesaLicitacaoDTO> baseDespesaLicitacaoResults = baseDespesaLicitacaoConsumoApiService.consumirPersistir(baseDespesaLicitacaoDto);
                int baseDespesaLicitacaoCount = baseDespesaLicitacaoResults != null ? baseDespesaLicitacaoResults.size() : 0;
                processingResults.put("BaseDespesaLicitacao", baseDespesaLicitacaoCount);
                totalRecordsProcessed += baseDespesaLicitacaoCount;

                long baseDespesaLicitacaoDuration = System.currentTimeMillis() - baseDespesaLicitacaoStartTime;
                logger.info("Base Despesa Licitação processada: {}", baseDespesaLicitacaoCount);
                markdownSection.success(baseDespesaLicitacaoCount + " registros de Base Despesa Licitação processados", baseDespesaLicitacaoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Base Despesa Licitação", e);
                processingResults.put("BaseDespesaLicitacao", 0);
                markdownSection.error("Falha no processamento de Base Despesa Licitação: " + e.getMessage());
            }

            // 21. Aguardar um pouco antes de consumir Termo
            Thread.sleep(2000);

            // 22. Consumir Termo (Convênios)

            // 21.1 Consumir Despesa de Convênio
            Thread.sleep(2000);
            logger.info("=== INICIANDO CONSUMO DE DESPESA DE CONVÊNIO ===");
            try {
                long despesaConvenioStartTime = System.currentTimeMillis();
                DespesaConvenioDTO despesaConvenioDto = new DespesaConvenioDTO();
                List<DespesaConvenioDTO> despesaConvenioResults = despesaConvenioConsumoApiService.consumirPersistir(despesaConvenioDto);
                int despesaConvenioCount = despesaConvenioResults != null ? despesaConvenioResults.size() : 0;
                processingResults.put("DespesaConvenio", despesaConvenioCount);
                totalRecordsProcessed += despesaConvenioCount;

                long despesaConvenioDuration = System.currentTimeMillis() - despesaConvenioStartTime;
                logger.info("Despesa de Convênio processada: {}", despesaConvenioCount);
                markdownSection.success(despesaConvenioCount + " registros de Despesa de Convênio processados", despesaConvenioDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Despesa de Convênio", e);
                processingResults.put("DespesaConvenio", 0);
                markdownSection.error("Falha no processamento de Despesa de Convênio: " + e.getMessage());
            }

            logger.info("=== INICIANDO CONSUMO DE TERMO (CONVÊNIOS) ===");
            markdownSection.progress("Processando Termo (Convênios)...");

            try {
                long termoStartTime = System.currentTimeMillis();
                TermoDTO termoDto = new TermoDTO();
                List<TermoDTO> termoResults = termoConsumoApiService.consumirPersistir(termoDto);
                int termoCount = termoResults != null ? termoResults.size() : 0;
                processingResults.put("Termo", termoCount);
                totalRecordsProcessed += termoCount;

                long termoDuration = System.currentTimeMillis() - termoStartTime;
                logger.info("Termo (Convênios) processados: {}", termoCount);
                markdownSection.success(termoCount + " registros de Termo (Convênios) processados", termoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Termo (Convênios)", e);
                processingResults.put("Termo", 0);
                markdownSection.error("Falha no processamento de Termo (Convênios): " + e.getMessage());
            }

            // 23. Aguardar um pouco antes de consumir Previsão Realização Receita
            Thread.sleep(2000);

            // 24. Consumir Previsão Realização Receita
            logger.info("=== INICIANDO CONSUMO DE PREVISÃO REALIZAÇÃO RECEITA ===");
            markdownSection.progress("Processando Previsão Realização Receita...");

            try {
                long previsaoStartTime = System.currentTimeMillis();
                PrevisaoRealizacaoReceitaDTO previsaoDto = new PrevisaoRealizacaoReceitaDTO();
                List<PrevisaoRealizacaoReceitaDTO> previsaoResults = previsaoRealizacaoReceitaConsumoApiService.consumirPersistir(previsaoDto);
                int previsaoCount = previsaoResults != null ? previsaoResults.size() : 0;
                processingResults.put("PrevisaoRealizacaoReceita", previsaoCount);
                totalRecordsProcessed += previsaoCount;

                long previsaoDuration = System.currentTimeMillis() - previsaoStartTime;
                logger.info("Previsão Realização Receita processados: {}", previsaoCount);
                markdownSection.success(previsaoCount + " registros de Previsão Realização Receita processados", previsaoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Previsão Realização Receita", e);
                processingResults.put("PrevisaoRealizacaoReceita", 0);
                markdownSection.error("Falha no processamento de Previsão Realização Receita: " + e.getMessage());
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "SCHEDULED_CONTRACT_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "RESULTS", processingResults.toString());

            // Adicionar estatísticas ao log markdown
            if (totalRecordsProcessed > 0) {
                markdownSection.info("📊 Estatísticas de processamento:")
                              .info("  • Unidades Gestoras: " + processingResults.getOrDefault("UnidadeGestora", 0))
                              .info("  • Contratos Fiscais: " + processingResults.getOrDefault("ContratosFiscais", 0))
                              .info("  • Contratos: " + processingResults.getOrDefault("Contrato", 0))
                              .info("  • Contrato-Empenho: " + processingResults.getOrDefault("ContratoEmpenho", 0))
                              .info("  • Receitas: " + processingResults.getOrDefault("Receita", 0))
                              .info("  • Pagamentos: " + processingResults.getOrDefault("Pagamento", 0))
                              .info("  • Liquidações: " + processingResults.getOrDefault("Liquidacao", 0))
                              .info("  • Ordens de Fornecimento: " + processingResults.getOrDefault("OrdemFornecimento", 0))
                              .info("  • Dados Orçamentários: " + processingResults.getOrDefault("DadosOrcamentarios", 0))
                              .info("  • Empenhos: " + processingResults.getOrDefault("Empenho", 0))
                              .info("  • Totalizadores de Execução: " + processingResults.getOrDefault("TotalizadoresExecucao", 0))
                              .info("  • Consulta Gerencial: " + processingResults.getOrDefault("ConsultaGerencial", 0))
                              .info("  • Base Despesa Credor: " + processingResults.getOrDefault("BaseDespesaCredor", 0))
                              .info("  • Base Despesa Licitação: " + processingResults.getOrDefault("BaseDespesaLicitacao", 0))
                              .info("  • Termo (Convênios): " + processingResults.getOrDefault("Termo", 0))
                              .info("  • Previsão Realização Receita: " + processingResults.getOrDefault("PrevisaoRealizacaoReceita", 0));

                if (totalExecutionTime > 30000) { // Mais de 30 segundos
                    markdownSection.warning("Execução demorou mais que 30 segundos");
                }
            }

            // Finalizar log markdown com resumo
            markdownSection.logWithSummary(totalRecordsProcessed);

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

            // Log de erro estruturado em markdown
            markdownSection.error("Falha crítica na execução: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            isFirstExecution = false;
            MDCUtil.clear();
        }
    }

    /**
     * Método específico para executar apenas Pagamento
     */
    @LogOperation(operation = "SCHEDULED_PAGAMENTO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executePagamentoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "PAGAMENTO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Pagamento");

        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "PAGAMENTO_EXECUTION", "ENDPOINTS", "PAGAMENTO_ENDPOINT");

            // Log estruturado em markdown
            markdownSection.info("Iniciando consumo específico de Pagamentos da SEFAZ")
                          .info("Correlation ID: " + correlationId);

            // Consumir apenas Pagamentos
            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE PAGAMENTOS ===");
            markdownSection.progress("Processando Pagamentos...");

            try {
                long pagamentoStartTime = System.currentTimeMillis();
                PagamentoDTO pagamentoDto = new PagamentoDTO();
                List<PagamentoDTO> pagamentoResults = pagamentoConsumoApiService.consumirPersistir(pagamentoDto);
                int pagamentoCount = pagamentoResults != null ? pagamentoResults.size() : 0;
                totalRecordsProcessed = pagamentoCount;

                long pagamentoDuration = System.currentTimeMillis() - pagamentoStartTime;
                logger.info("Pagamentos processados: {}", pagamentoCount);
                markdownSection.success(pagamentoCount + " registros de Pagamento processados", pagamentoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Pagamentos", e);
                markdownSection.error("Falha no processamento de Pagamentos: " + e.getMessage());
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "PAGAMENTO_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "RESULTS", "Pagamentos: " + totalRecordsProcessed);

            // Adicionar estatísticas ao log markdown
            if (totalRecordsProcessed > 0) {
                markdownSection.info("📊 Estatísticas de processamento:")
                              .info("  • Pagamentos: " + totalRecordsProcessed);

                if (totalExecutionTime > 15000) { // Mais de 15 segundos
                    markdownSection.warning("Execução demorou mais que 15 segundos");
                }
            }

            // Finalizar log markdown com resumo
            markdownSection.logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ESPECÍFICA DE PAGAMENTO CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução específica de pagamento", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "PAGAMENTO_EXECUTION", totalExecutionTime, e,
                "ENDPOINTS", "PAGAMENTO_ENDPOINT");
            logger.error("Erro durante execução específica de pagamento", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha crítica na execução de pagamento: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            isFirstExecution = false;
            MDCUtil.clear();
        }
    }

    /**
     * Método específico para executar apenas Liquidação
     */
    @LogOperation(operation = "SCHEDULED_LIQUIDACAO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeLiquidacaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "LIQUIDACAO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Liquidação");

        try {
            markdownSection.info("🚀 Iniciando processamento de Liquidações")
                          .info("📅 Timestamp: " + java.time.LocalDateTime.now())
                          .info("🔗 Correlation ID: " + correlationId);

            // Log simples para usuário
            userFriendlyLogger.logInfo("Iniciando processamento específico de Liquidações");

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "LIQUIDACAO_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE LIQUIDAÇÕES ===");
            markdownSection.progress("Processando Liquidações...");

            long liquidacaoStartTime = System.currentTimeMillis();
            LiquidacaoDTO liquidacaoDto = new LiquidacaoDTO();
            List<LiquidacaoDTO> liquidacaoResults = liquidacaoConsumoApiService.consumirPersistir(liquidacaoDto);
            int liquidacaoCount = liquidacaoResults != null ? liquidacaoResults.size() : 0;
            totalRecordsProcessed = liquidacaoCount;

            long liquidacaoDuration = System.currentTimeMillis() - liquidacaoStartTime;
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            logger.info("Liquidações processadas: {}", liquidacaoCount);
            markdownSection.success(liquidacaoCount + " registros de Liquidação processados", liquidacaoDuration);

            // Log simples para usuário
            userFriendlyLogger.logOperationComplete(totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "LIQUIDACAO_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "liquidacao");

            // Finalizar log markdown com resumo
            markdownSection.info("📊 Resumo da execução:")
                          .info("  • Total de registros: " + totalRecordsProcessed)
                          .info("  • Tempo de execução: " + totalExecutionTime + " ms")
                          .logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO DE LIQUIDAÇÃO CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("processamento de Liquidações", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "LIQUIDACAO_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "liquidacao");
            logger.error("Erro durante execução específica de Liquidação", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Liquidações: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método específico para executar apenas Ordem de Fornecimento
     */
    @LogOperation(operation = "SCHEDULED_ORDEM_FORNECIMENTO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeOrdemFornecimentoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "ORDEM_FORNECIMENTO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Ordem de Fornecimento");

        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "ORDEM_FORNECIMENTO_EXECUTION", "ENDPOINTS", "ORDEM_FORNECIMENTO_ENDPOINT");

            // Log estruturado em markdown
            markdownSection.info("Iniciando consumo específico de Ordens de Fornecimento da SEFAZ")
                          .info("Correlation ID: " + correlationId);

            // Consumir apenas Ordens de Fornecimento
            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE ORDENS DE FORNECIMENTO ===");
            markdownSection.progress("Processando Ordens de Fornecimento...");

            try {
                long ordemFornecimentoStartTime = System.currentTimeMillis();
                OrdemFornecimentoDTO ordemFornecimentoDto = new OrdemFornecimentoDTO();
                List<OrdemFornecimentoDTO> ordemFornecimentoResults = ordemFornecimentoConsumoApiService.consumirPersistir(ordemFornecimentoDto);
                int ordemFornecimentoCount = ordemFornecimentoResults != null ? ordemFornecimentoResults.size() : 0;
                totalRecordsProcessed = ordemFornecimentoCount;

                long ordemFornecimentoDuration = System.currentTimeMillis() - ordemFornecimentoStartTime;
                logger.info("Ordens de Fornecimento processadas: {}", ordemFornecimentoCount);
                markdownSection.success(ordemFornecimentoCount + " registros de Ordem de Fornecimento processados", ordemFornecimentoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Ordens de Fornecimento", e);
                markdownSection.error("Falha no processamento de Ordens de Fornecimento: " + e.getMessage());
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "ORDEM_FORNECIMENTO_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "RESULTS", "Ordens de Fornecimento: " + totalRecordsProcessed);

            // Adicionar estatísticas ao log markdown
            if (totalRecordsProcessed > 0) {
                markdownSection.info("📊 Estatísticas de processamento:")
                              .info("  • Ordens de Fornecimento: " + totalRecordsProcessed);

                if (totalExecutionTime > 15000) { // Mais de 15 segundos
                    markdownSection.warning("Execução demorou mais que 15 segundos");
                }
            }

            // Finalizar log markdown com resumo
            markdownSection.logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ESPECÍFICA DE ORDEM DE FORNECIMENTO CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução específica de ordem de fornecimento", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "ORDEM_FORNECIMENTO_EXECUTION", totalExecutionTime, e,
                "ENDPOINTS", "ORDEM_FORNECIMENTO_ENDPOINT");
            logger.error("Erro durante execução específica de ordem de fornecimento", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha crítica na execução de ordem de fornecimento: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
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
            executeAllEntities();

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
     * Método para execução manual apenas de Pagamento via endpoint
     */
    public Map<String, Object> executePagamentoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE PAGAMENTO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executePagamentoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Pagamento concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Pagamento: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método para execução manual apenas de Liquidação via endpoint
     */
    public Map<String, Object> executeLiquidacaoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE LIQUIDAÇÃO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeLiquidacaoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Liquidação concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Liquidação: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método para execução manual apenas de Ordem de Fornecimento via endpoint
     */
    public Map<String, Object> executeOrdemFornecimentoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE ORDEM DE FORNECIMENTO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeOrdemFornecimentoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Ordem de Fornecimento concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Ordem de Fornecimento: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Dados Orçamentários
     */
    @LogOperation(operation = "DADOS_ORCAMENTARIOS_ONLY_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeDadosOrcamentariosOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "DADOS_ORCAMENTARIOS_ONLY");

        long startTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Manual - Dados Orçamentários");

        try {
            unifiedLogger.logOperationStart("SCHEDULER", "DADOS_ORCAMENTARIOS_ONLY", "CORRELATION_ID", correlationId);

            logger.info("=== INICIANDO CONSUMO DE DADOS ORÇAMENTÁRIOS ===");
            markdownSection.progress("Processando Dados Orçamentários...");

            try {
                long dadosOrcamentariosStartTime = System.currentTimeMillis();
                DadosOrcamentariosDTO dadosOrcamentariosDto = new DadosOrcamentariosDTO();
                List<DadosOrcamentariosDTO> dadosOrcamentariosResults = dadosOrcamentariosConsumoApiService.consumirPersistir(dadosOrcamentariosDto);
                int dadosOrcamentariosCount = dadosOrcamentariosResults != null ? dadosOrcamentariosResults.size() : 0;
                totalRecordsProcessed = dadosOrcamentariosCount;

                long dadosOrcamentariosDuration = System.currentTimeMillis() - dadosOrcamentariosStartTime;
                logger.info("Dados Orçamentários processados: {}", dadosOrcamentariosCount);
                markdownSection.success(dadosOrcamentariosCount + " registros de Dados Orçamentários processados", dadosOrcamentariosDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Dados Orçamentários", e);
                markdownSection.error("Falha no processamento de Dados Orçamentários: " + e.getMessage());
                throw e;
            }

            long totalExecutionTime = System.currentTimeMillis() - startTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "DADOS_ORCAMENTARIOS_ONLY",
                totalExecutionTime, totalRecordsProcessed, "ENTITY", "DadosOrcamentarios");

            // Finalizar log markdown com resumo
            markdownSection.logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO DE DADOS ORÇAMENTÁRIOS CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - startTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução de dados orçamentários", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "DADOS_ORCAMENTARIOS_ONLY", totalExecutionTime, e,
                "ENDPOINT", "dados-orcamentarios");
            logger.error("Erro durante execução de Dados Orçamentários", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha na execução de Dados Orçamentários: " + e.getMessage())
                          .summary("Execução de Dados Orçamentários interrompida por erro")
                          .log();
            throw e;
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Dados Orçamentários via endpoint
     */
    public Map<String, Object> executeDadosOrcamentariosManually() {
        logger.info("=== EXECUÇÃO MANUAL DE DADOS ORÇAMENTÁRIOS SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeDadosOrcamentariosOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Dados Orçamentários concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Dados Orçamentários: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Empenho
     */
    @LogOperation(operation = "SCHEDULED_EMPENHO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeEmpenhoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "EMPENHO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Empenho");

        try {
            // Log simples para usuário
            userFriendlyLogger.logInfo("Iniciando processamento específico de Empenhos");

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "EMPENHO_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE EMPENHOS ===");
            markdownSection.progress("Processando Empenhos...");

            long empenhoStartTime = System.currentTimeMillis();
            EmpenhoDTO empenhoDto = new EmpenhoDTO();
            List<EmpenhoDTO> empenhoResults = empenhoConsumoApiService.consumirPersistir(empenhoDto);
            int empenhoCount = empenhoResults != null ? empenhoResults.size() : 0;
            totalRecordsProcessed = empenhoCount;

            long empenhoDuration = System.currentTimeMillis() - empenhoStartTime;
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            logger.info("Empenhos processados: {}", empenhoCount);
            markdownSection.success(empenhoCount + " registros de Empenho processados", empenhoDuration);

            // Log simples para usuário
            userFriendlyLogger.logOperationComplete(totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "EMPENHO_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "empenho");

            // Finalizar log markdown com resumo
            markdownSection.summary("Processamento de Empenho concluído com sucesso")
                          .info("Total de registros: " + totalRecordsProcessed)
                          .info("Tempo de execução: " + totalExecutionTime + "ms")
                          .log();

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("processamento de Empenhos", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "EMPENHO_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "empenho");
            logger.error("Erro durante execução específica de Empenho", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Empenhos: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Empenho via endpoint
     */
    public Map<String, Object> executeEmpenhoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE EMPENHO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeEmpenhoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Empenho concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Empenho: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Totalizadores de Execução
     */
    @LogOperation(operation = "SCHEDULED_TOTALIZADORES_EXECUCAO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeTotalizadoresExecucaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "TOTALIZADORES_EXECUCAO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Totalizadores de Execução");

        try {
            unifiedLogger.logApplicationEvent("SCHEDULER_TOTALIZADORES_EXECUCAO_START", "Iniciando execução específica de Totalizadores de Execução");
            unifiedLogger.logOperationStart("SCHEDULER", "TOTALIZADORES_EXECUCAO_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            markdownSection.info("Iniciando processamento de Totalizadores de Execução...");

            long startTime = System.currentTimeMillis();
            TotalizadoresExecucaoDTO totalizadoresExecucaoDto = new TotalizadoresExecucaoDTO();
            List<TotalizadoresExecucaoDTO> totalizadoresExecucaoResults = totalizadoresExecucaoConsumoApiService.consumirPersistir(totalizadoresExecucaoDto);
            int totalizadoresExecucaoCount = totalizadoresExecucaoResults != null ? totalizadoresExecucaoResults.size() : 0;
            totalRecordsProcessed += totalizadoresExecucaoCount;

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Totalizadores de Execução processados: {}", totalizadoresExecucaoCount);
            markdownSection.success(totalizadoresExecucaoCount + " Totalizadores de Execução processados", duration);

            long totalDuration = System.currentTimeMillis() - totalStartTime;
            unifiedLogger.logOperationSuccess("SCHEDULER", "TOTALIZADORES_EXECUCAO_ONLY_CONSUMPTION", totalDuration,
                    totalRecordsProcessed, "CORRELATION_ID", correlationId);

            // Finalizar log markdown com resumo
            markdownSection.summary("Processamento de Totalizadores de Execução concluído com sucesso")
                          .info("Total de registros: " + totalRecordsProcessed)
                          .info("Tempo de execução: " + totalDuration + "ms")
                          .log();

        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - totalStartTime;
            logger.error("Erro durante execução específica de Totalizadores de Execução", e);
            unifiedLogger.logOperationError("SCHEDULER", "TOTALIZADORES_EXECUCAO_ONLY_CONSUMPTION", totalDuration, e, "CORRELATION_ID", correlationId);
            markdownSection.error("Erro durante execução específica: " + e.getMessage()).log();
            throw e;
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Totalizadores de Execução via endpoint
     */
    public Map<String, Object> executeTotalizadoresExecucaoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE TOTALIZADORES DE EXECUÇÃO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeTotalizadoresExecucaoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Totalizadores de Execução concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Totalizadores de Execução: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Consulta Gerencial
     */
    @LogOperation(operation = "SCHEDULED_CONSULTA_GERENCIAL_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeConsultaGerencialOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "CONSULTA_GERENCIAL_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Consulta Gerencial");

        try {
            markdownSection.info("Iniciando processamento de Consulta Gerencial (Diárias)");
            unifiedLogger.logOperationStart("SCHEDULER", "CONSULTA_GERENCIAL_CONSUMPTION", "CORRELATION_ID", correlationId);

            long consultaGerencialStartTime = System.currentTimeMillis();
            ConsultaGerencialDTO consultaGerencialDto = new ConsultaGerencialDTO();
            List<ConsultaGerencialDTO> consultaGerencialResults = consultaGerencialConsumoApiService.consumirPersistir(consultaGerencialDto);
            int consultaGerencialCount = consultaGerencialResults != null ? consultaGerencialResults.size() : 0;
            totalRecordsProcessed += consultaGerencialCount;

            long consultaGerencialDuration = System.currentTimeMillis() - consultaGerencialStartTime;
            logger.info("Consulta Gerencial processada: {}", consultaGerencialCount);
            markdownSection.success(consultaGerencialCount + " registros de Consulta Gerencial processados", consultaGerencialDuration);

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Adicionar estatísticas ao log markdown
            if (totalRecordsProcessed > 0) {
                markdownSection.info("📊 Estatísticas de processamento:")
                              .info("  • Consulta Gerencial: " + totalRecordsProcessed);

                if (totalExecutionTime > 15000) { // Mais de 15 segundos
                    markdownSection.warning("Execução demorou mais que 15 segundos");
                }
            }

            // Finalizar log markdown com resumo
            markdownSection.logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ESPECÍFICA DE CONSULTA GERENCIAL CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução específica de consulta gerencial", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "CONSULTA_GERENCIAL_EXECUTION", totalExecutionTime, e,
                "ENDPOINTS", "CONSULTA_GERENCIAL_ENDPOINT");
            logger.error("Erro durante execução específica de consulta gerencial", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha crítica na execução de consulta gerencial: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            isFirstExecution = false;
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Consulta Gerencial via endpoint
     */
    public Map<String, Object> executeConsultaGerencialManually() {
        logger.info("=== EXECUÇÃO MANUAL DE CONSULTA GERENCIAL SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeConsultaGerencialOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Consulta Gerencial concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Consulta Gerencial: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Despesa de Convênio
     */
    @LogOperation(operation = "SCHEDULED_DESPESA_CONVENIO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeDespesaConvenioOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "DESPESA_CONVENIO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Isolada - Despesa de Convênio");

        try {
            userFriendlyLogger.logDataFetchStart("Despesa de Convênio");
            unifiedLogger.logOperationStart("SCHEDULER", "DESPESA_CONVENIO_ONLY_CONSUMPTION", "ENDPOINT", "CONVENIO_DESPESA");
            markdownSection.info("Iniciando consumo isolado de Despesa de Convênio")
                          .info("Correlation ID: " + correlationId)
                          .progress("Processando dados...");

            logger.info("=== INICIANDO CONSUMO ISOLADO DE DESPESA DE CONVÊNIO ===");
            long start = System.currentTimeMillis();
            DespesaConvenioDTO dto = new DespesaConvenioDTO();
            List<DespesaConvenioDTO> results = despesaConvenioConsumoApiService.consumirPersistir(dto);
            int count = results != null ? results.size() : 0;
            totalRecordsProcessed = count;
            long duration = System.currentTimeMillis() - start;

            userFriendlyLogger.logDataProcessed("Despesa de Convênio", count);
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;
            unifiedLogger.logOperationSuccess("SCHEDULER", "DESPESA_CONVENIO_ONLY_CONSUMPTION", totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "CONVENIO_DESPESA");
            markdownSection.success(count + " registros processados", duration)
                          .info("📊 Estatísticas:")
                          .info("  • Registros processados: " + count)
                          .info("  • Tempo total: " + String.format("%.2f", totalExecutionTime / 1000.0) + " segundos")
                          .info("  • Endpoint: " + dto.getUrl())
                          .info("  • Tabela destino: " + dto.getTabelaBanco())
                          .logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ISOLADA DE DESPESA DE CONVÊNIO CONCLUÍDA ===");

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;
            userFriendlyLogger.logError("consumo de Despesa de Convênio", e.getMessage());
            unifiedLogger.logOperationError("SCHEDULER", "DESPESA_CONVENIO_ONLY_CONSUMPTION", totalExecutionTime, e, "ENDPOINT", "CONVENIO_DESPESA");
            logger.error("Erro durante execução isolada de Despesa de Convênio", e);
            markdownSection.error("Falha no processamento de Despesa de Convênio: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para verificar status do scheduler
     */
    public Map<String, Object> getSchedulerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("schedulerActive", true);
        status.put("firstExecutionCompleted", !isFirstExecution);
        status.put("nextScheduledExecution", "2:45 AM daily - All entities (if enabled)");
        status.put("testExecutionOnStartup", "DISABLED - No automatic execution on startup");
        status.put("availableEntities", "UG, Contratos, Receitas, Pagamentos, Liquidações, Ordens de Fornecimento, Dados Orçamentários, Empenhos, Totalizadores de Execução, Consulta Gerencial");
        status.put("startupExecution", "DISABLED");
        status.put("scheduledExecution", "All entities");

        return status;
    }

    /**
     * Método específico para executar apenas Contratos
     */
    @LogOperation(operation = "SCHEDULED_CONTRATO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeContratoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "CONTRATO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Contratos");

        try {
            markdownSection.progress("Processando Contratos...");

            try {
                long contratoStartTime = System.currentTimeMillis();
                ContratoDTO contratoDto = new ContratoDTO();
                List<ContratoDTO> contratoResults = contratoConsumoApiService.consumirPersistir(contratoDto);
                int contratoCount = contratoResults != null ? contratoResults.size() : 0;
                totalRecordsProcessed = contratoCount;

                long contratoDuration = System.currentTimeMillis() - contratoStartTime;
                logger.info("Contratos processados: {}", contratoCount);
                markdownSection.success(contratoCount + " registros de Contrato processados", contratoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Contratos", e);
                markdownSection.error("Falha no processamento de Contratos: " + e.getMessage());
                throw e;
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "CONTRATO_ONLY",
                totalExecutionTime, totalRecordsProcessed, "ENTITY", "Contrato");

            // Log de sucesso estruturado em markdown
            markdownSection.summary("Execução de Contratos concluída com sucesso")
                          .log();

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("execução de contratos", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "CONTRATO_ONLY", totalExecutionTime, e,
                "ENDPOINT", "contrato");
            logger.error("Erro durante execução de Contratos", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha na execução de Contratos: " + e.getMessage())
                          .summary("Execução de Contratos interrompida por erro")
                          .log();
            throw e;
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Contratos via endpoint
     */
    public Map<String, Object> executeContratoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE CONTRATOS SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeContratoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Contratos concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Contratos: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Contrato-Empenho
     */
    @LogOperation(operation = "SCHEDULED_CONTRATO_EMPENHO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeContratoEmpenhoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "CONTRATO_EMPENHO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Contrato-Empenho");

        try {
            // Log simples para usuário
            userFriendlyLogger.logInfo("Iniciando processamento específico de Contrato-Empenho");

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "CONTRATO_EMPENHO_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE CONTRATO-EMPENHO ===");
            markdownSection.progress("Processando Contrato-Empenho...");

            long contratoEmpenhoStartTime = System.currentTimeMillis();
            ContratoEmpenhoDTO contratoEmpenhoDto = new ContratoEmpenhoDTO();
            List<ContratoEmpenhoDTO> contratoEmpenhoResults = contratoEmpenhoConsumoApiService.consumirPersistir(contratoEmpenhoDto);
            int contratoEmpenhoCount = contratoEmpenhoResults != null ? contratoEmpenhoResults.size() : 0;
            totalRecordsProcessed = contratoEmpenhoCount;

            long contratoEmpenhoDuration = System.currentTimeMillis() - contratoEmpenhoStartTime;
            logger.info("Contrato-Empenho processados: {}", contratoEmpenhoCount);
            markdownSection.success(contratoEmpenhoCount + " registros de Contrato-Empenho processados", contratoEmpenhoDuration);

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logDataProcessed("Contrato-Empenho", contratoEmpenhoCount);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "CONTRATO_EMPENHO_ONLY_CONSUMPTION", totalExecutionTime, totalRecordsProcessed,
                "ENDPOINT", "contrato-empenho");

            // Finalizar log markdown com resumo
            markdownSection.summary("Processamento específico de Contrato-Empenho concluído com sucesso")
                          .info("Total processado: " + totalRecordsProcessed + " registros")
                          .info("Tempo de execução: " + totalExecutionTime + " ms")
                          .log();

            logger.info("=== CONSUMO ESPECÍFICO DE CONTRATO-EMPENHO CONCLUÍDO ===");
            logger.info("Registros processados: {}", totalRecordsProcessed);
            logger.info("Tempo total: {} ms", totalExecutionTime);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("processamento de Contrato-Empenho", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "CONTRATO_EMPENHO_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "contrato-empenho");
            logger.error("Erro durante execução específica de Contrato-Empenho", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Contrato-Empenho: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Contrato-Empenho via endpoint
     */
    public Map<String, Object> executeContratoEmpenhoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE CONTRATO-EMPENHO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeContratoEmpenhoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Contrato-Empenho concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Contrato-Empenho: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Base Despesa Credor
     */
    @LogOperation(operation = "SCHEDULED_BASE_DESPESA_CREDOR_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeBaseDespesaCredorOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "BASE_DESPESA_CREDOR_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Base Despesa Credor");

        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "BASE_DESPESA_CREDOR_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            // Consumir apenas Base Despesa Credor
            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE BASE DESPESA CREDOR ===");
            markdownSection.progress("Processando Base Despesa Credor...");

            try {
                long baseDespesaCredorStartTime = System.currentTimeMillis();
                BaseDespesaCredorDTO baseDespesaCredorDto = new BaseDespesaCredorDTO();
                List<BaseDespesaCredorDTO> baseDespesaCredorResults = baseDespesaCredorConsumoApiService.consumirPersistir(baseDespesaCredorDto);
                int baseDespesaCredorCount = baseDespesaCredorResults != null ? baseDespesaCredorResults.size() : 0;
                totalRecordsProcessed = baseDespesaCredorCount;

                long baseDespesaCredorDuration = System.currentTimeMillis() - baseDespesaCredorStartTime;
                logger.info("Base Despesa Credor processados: {}", baseDespesaCredorCount);
                markdownSection.success(baseDespesaCredorCount + " registros de Base Despesa Credor processados", baseDespesaCredorDuration);

                // Log informações de paginação se disponível
                if (baseDespesaCredorResults != null && !baseDespesaCredorResults.isEmpty() &&
                    baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao() != null) {
                    logger.info("Paginação processada - Total de faixas: {}", baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao());
                    markdownSection.info("Paginação: " + baseDespesaCredorResults.get(0).getQtTotalFaixasPaginacao() + " faixas processadas");
                }

            } catch (Exception e) {
                logger.error("Erro ao consumir Base Despesa Credor", e);
                markdownSection.error("Falha no processamento de Base Despesa Credor: " + e.getMessage());
                throw e; // Re-throw para ser capturado pelo catch externo
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "BASE_DESPESA_CREDOR_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "base-despesa-credor");

            // Finalizar log markdown com resumo
            markdownSection.summary("Processamento específico de Base Despesa Credor concluído com sucesso")
                          .info("Total processado: " + totalRecordsProcessed + " registros")
                          .info("Tempo de execução: " + totalExecutionTime + " ms")
                          .log();

            logger.info("=== CONSUMO ESPECÍFICO DE BASE DESPESA CREDOR CONCLUÍDO ===");
            logger.info("Registros processados: {}", totalRecordsProcessed);
            logger.info("Tempo total: {} ms", totalExecutionTime);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("processamento de Base Despesa Credor", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "BASE_DESPESA_CREDOR_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "base-despesa-credor");
            logger.error("Erro durante execução específica de Base Despesa Credor", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Base Despesa Credor: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Base Despesa Credor via endpoint
     */
    public Map<String, Object> executeBaseDespesaCredorManually() {
        logger.info("=== EXECUÇÃO MANUAL DE BASE DESPESA CREDOR SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeBaseDespesaCredorOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Base Despesa Credor concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Base Despesa Credor: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Base Despesa Licitação
     */
    @LogOperation(operation = "SCHEDULED_BASE_DESPESA_LICITACAO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeBaseDespesaLicitacaoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "BASE_DESPESA_LICITACAO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Específica - Base Despesa Licitação");

        try {
            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionStart();

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "BASE_DESPESA_LICITACAO_ONLY_CONSUMPTION", "CORRELATION_ID", correlationId);

            // Consumir apenas Base Despesa Licitação
            logger.info("=== INICIANDO CONSUMO ESPECÍFICO DE BASE DESPESA LICITAÇÃO ===");
            markdownSection.progress("Processando Base Despesa Licitação...");

            try {
                long baseDespesaLicitacaoStartTime = System.currentTimeMillis();
                BaseDespesaLicitacaoDTO baseDespesaLicitacaoDto = new BaseDespesaLicitacaoDTO();
                List<BaseDespesaLicitacaoDTO> baseDespesaLicitacaoResults = baseDespesaLicitacaoConsumoApiService.consumirPersistir(baseDespesaLicitacaoDto);
                int baseDespesaLicitacaoCount = baseDespesaLicitacaoResults != null ? baseDespesaLicitacaoResults.size() : 0;
                totalRecordsProcessed = baseDespesaLicitacaoCount;

                long baseDespesaLicitacaoDuration = System.currentTimeMillis() - baseDespesaLicitacaoStartTime;
                logger.info("Base Despesa Licitação processados: {}", baseDespesaLicitacaoCount);
                markdownSection.success(baseDespesaLicitacaoCount + " registros de Base Despesa Licitação processados", baseDespesaLicitacaoDuration);

            } catch (Exception e) {
                logger.error("Erro ao consumir Base Despesa Licitação", e);
                markdownSection.error("Falha no processamento de Base Despesa Licitação: " + e.getMessage());
                throw e; // Re-throw para ser capturado pelo catch externo
            }

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logScheduledExecutionComplete(totalRecordsProcessed, totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "BASE_DESPESA_LICITACAO_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "base-despesa-licitacao");

            // Finalizar log markdown com resumo
            markdownSection.summary("Processamento específico de Base Despesa Licitação concluído com sucesso")
                          .info("Total processado: " + totalRecordsProcessed + " registros")
                          .info("Tempo de execução: " + totalExecutionTime + " ms")
                          .log();

            logger.info("=== CONSUMO ESPECÍFICO DE BASE DESPESA LICITAÇÃO CONCLUÍDO ===");
            logger.info("Registros processados: {}", totalRecordsProcessed);
            logger.info("Tempo total: {} ms", totalExecutionTime);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("processamento de Base Despesa Licitação", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "BASE_DESPESA_LICITACAO_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "base-despesa-licitacao");
            logger.error("Erro durante execução específica de Base Despesa Licitação", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Base Despesa Licitação: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Método para execução manual apenas de Base Despesa Licitação via endpoint
     */
    public Map<String, Object> executeBaseDespesaLicitacaoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE BASE DESPESA LICITAÇÃO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeBaseDespesaLicitacaoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Base Despesa Licitação concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Base Despesa Licitação: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Método específico para executar apenas Termo (Convênios)
     */
    @LogOperation(operation = "SCHEDULED_TERMO_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executeTermoOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "TERMO_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Isolada - Termo (Convênios)");

        try {
            // Log simples para usuário
            userFriendlyLogger.logDataFetchStart("Termo (Convênios)");

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "TERMO_ONLY_CONSUMPTION", "ENDPOINT", "TERMO_CONVENIOS");

            // Log estruturado em markdown
            markdownSection.info("Iniciando consumo isolado de Termo (Convênios)")
                          .info("Correlation ID: " + correlationId)
                          .progress("Processando dados de convênios...");

            // Consumir Termo (Convênios)
            logger.info("=== INICIANDO CONSUMO ISOLADO DE TERMO (CONVÊNIOS) ===");

            long termoStartTime = System.currentTimeMillis();
            TermoDTO termoDto = new TermoDTO();
            List<TermoDTO> termoResults = termoConsumoApiService.consumirPersistir(termoDto);
            int termoCount = termoResults != null ? termoResults.size() : 0;
            totalRecordsProcessed = termoCount;

            long termoDuration = System.currentTimeMillis() - termoStartTime;
            logger.info("Termo (Convênios) processados: {}", termoCount);

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logDataProcessed("Termo (Convênios)", termoCount);
            userFriendlyLogger.logOperationComplete(totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "TERMO_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "TERMO_CONVENIOS");

            // Log estruturado em markdown
            markdownSection.success(termoCount + " registros de Termo (Convênios) processados", termoDuration)
                          .info("📊 Estatísticas:")
                          .info("  • Registros processados: " + termoCount)
                          .info("  • Tempo total: " + String.format("%.2f", totalExecutionTime / 1000.0) + " segundos")
                          .info("  • Endpoint: " + termoDto.getUrl())
                          .info("  • Tabela destino: " + termoDto.getTabelaBanco())
                          .logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ISOLADA DE TERMO CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("consumo de Termo (Convênios)", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "TERMO_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "TERMO_CONVENIOS");
            logger.error("Erro durante execução isolada de Termo", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Termo (Convênios): " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Execução manual de Despesa de Convênio
     */
    public Map<String, Object> executeDespesaConvenioManually() {
        logger.info("=== EXECUÇÃO MANUAL DE DESPESA DE CONVÊNIO SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeDespesaConvenioOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Despesa de Convênio concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro durante execução manual de Despesa de Convênio: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Execução manual de Termo (Convênios)
     */
    public Map<String, Object> executeTermoManually() {
        logger.info("=== EXECUÇÃO MANUAL DE TERMO (CONVÊNIOS) SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executeTermoOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Termo (Convênios) concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            logger.info("Execução manual de Termo concluída com sucesso");
            return result;

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro na execução manual de Termo (Convênios): " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());

            logger.error("Erro na execução manual de Termo", e);
            return result;
        }
    }

    /**
     * Método específico para executar apenas Previsão Realização Receita
     */
    @LogOperation(operation = "SCHEDULED_PREVISAO_REALIZACAO_RECEITA_CONSUMPTION", component = "SCHEDULER", slowOperationThresholdMs = 30000)
    private void executePrevisaoRealizacaoReceitaOnly() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setupOperationContext("SCHEDULER", "PREVISAO_REALIZACAO_RECEITA_ONLY_CONSUMPTION");

        long totalStartTime = System.currentTimeMillis();
        int totalRecordsProcessed = 0;

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Execução Isolada - Previsão Realização Receita");

        try {
            // Log simples para usuário
            userFriendlyLogger.logDataFetchStart("Previsão Realização Receita");

            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SCHEDULER", "PREVISAO_REALIZACAO_RECEITA_ONLY_CONSUMPTION", "ENDPOINT", "PREVISAO_REALIZACAO_RECEITA");

            // Log estruturado em markdown
            markdownSection.info("Iniciando consumo isolado de Previsão Realização Receita")
                          .info("Correlation ID: " + correlationId)
                          .progress("Processando dados de previsão e realização de receitas...");

            // Consumir Previsão Realização Receita
            logger.info("=== INICIANDO CONSUMO ISOLADO DE PREVISÃO REALIZAÇÃO RECEITA ===");

            long previsaoStartTime = System.currentTimeMillis();
            PrevisaoRealizacaoReceitaDTO previsaoDto = new PrevisaoRealizacaoReceitaDTO();
            List<PrevisaoRealizacaoReceitaDTO> previsaoResults = previsaoRealizacaoReceitaConsumoApiService.consumirPersistir(previsaoDto);
            int previsaoCount = previsaoResults != null ? previsaoResults.size() : 0;
            totalRecordsProcessed = previsaoCount;

            long previsaoDuration = System.currentTimeMillis() - previsaoStartTime;
            logger.info("Previsão Realização Receita processados: {}", previsaoCount);

            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logDataProcessed("Previsão Realização Receita", previsaoCount);
            userFriendlyLogger.logOperationComplete(totalExecutionTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SCHEDULER", "PREVISAO_REALIZACAO_RECEITA_ONLY_CONSUMPTION",
                totalExecutionTime, totalRecordsProcessed, "ENDPOINT", "PREVISAO_REALIZACAO_RECEITA");

            // Log estruturado em markdown
            markdownSection.success(previsaoCount + " registros de Previsão Realização Receita processados", previsaoDuration)
                          .info("📊 Estatísticas:")
                          .info("  • Registros processados: " + previsaoCount)
                          .info("  • Tempo total: " + String.format("%.2f", totalExecutionTime / 1000.0) + " segundos")
                          .info("  • Endpoint: " + previsaoDto.getUrl())
                          .info("  • Tabela destino: " + previsaoDto.getTabelaBanco())
                          .logWithSummary(totalRecordsProcessed);

            logger.info("=== EXECUÇÃO ISOLADA DE PREVISÃO REALIZAÇÃO RECEITA CONCLUÍDA ===");
            logger.info("Tempo total: {} ms", totalExecutionTime);
            logger.info("Total de registros processados: {}", totalRecordsProcessed);

        } catch (Exception e) {
            long totalExecutionTime = System.currentTimeMillis() - totalStartTime;

            // Log simples para usuário
            userFriendlyLogger.logError("consumo de Previsão Realização Receita", e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("SCHEDULER", "PREVISAO_REALIZACAO_RECEITA_ONLY_CONSUMPTION", totalExecutionTime, e,
                "ENDPOINT", "PREVISAO_REALIZACAO_RECEITA");
            logger.error("Erro durante execução isolada de Previsão Realização Receita", e);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha no processamento de Previsão Realização Receita: " + e.getMessage())
                          .summary("Execução interrompida por erro")
                          .log();
        } finally {
            MDCUtil.clear();
        }
    }

    /**
     * Execução manual de Previsão Realização Receita
     */
    public Map<String, Object> executePrevisaoRealizacaoReceitaManually() {
        logger.info("=== EXECUÇÃO MANUAL DE PREVISÃO REALIZAÇÃO RECEITA SOLICITADA ===");

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            executePrevisaoRealizacaoReceitaOnly();

            result.put("status", "SUCCESS");
            result.put("message", "Execução manual de Previsão Realização Receita concluída com sucesso");
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);

            logger.info("Execução manual de Previsão Realização Receita concluída com sucesso");
            return result;

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erro na execução manual de Previsão Realização Receita: " + e.getMessage());
            result.put("executionTimeMs", System.currentTimeMillis() - startTime);
            result.put("error", e.getClass().getSimpleName());

            logger.error("Erro na execução manual de Previsão Realização Receita", e);
            return result;
        }
    }
}
