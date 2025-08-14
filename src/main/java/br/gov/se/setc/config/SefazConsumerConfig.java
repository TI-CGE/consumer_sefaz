package br.gov.se.setc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.BaseDespesaCredorDTO;
import br.gov.se.setc.consumer.dto.BaseDespesaLicitacaoDTO;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.dto.ContratoEmpenhoDTO;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.dto.DadosOrcamentariosDTO;
import br.gov.se.setc.consumer.dto.DespesaConvenioDTO;
import br.gov.se.setc.consumer.dto.DespesaDetalhadaDTO;
import br.gov.se.setc.consumer.dto.EmpenhoDTO;
import br.gov.se.setc.consumer.dto.LiquidacaoDTO;
import br.gov.se.setc.consumer.dto.OrdemFornecimentoDTO;
import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.dto.TotalizadoresExecucaoDTO;
import br.gov.se.setc.consumer.dto.UnidadeGestoraDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.logging.MarkdownLogger;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import br.gov.se.setc.util.ValidacaoUtil;

@Configuration
public class SefazConsumerConfig {

    private static final Logger logger = Logger.getLogger(SefazConsumerConfig.class.getName());

    private <T extends EndpontSefaz> ConsumoApiService<T> createConsumoApiService(
            String serviceName,
            Class<T> dtoClass,
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<T> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        logger.info("Creating " + serviceName + " ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            dtoClass
        );
    }

    private <T extends EndpontSefaz> ValidacaoUtil<T> createValidacaoUtil(String serviceName, JdbcTemplate jdbcTemplate) {
        logger.info("Creating " + serviceName + " ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
    }

    @Bean("sefazRestTemplate")
    @Primary
    public RestTemplate restTemplate() {
        logger.info("Creating primary SEFAZ RestTemplate bean with enhanced configuration");

        // Configurar cliente HTTP com timeouts e pool de conexões
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Timeouts mais conservadores para evitar connection reset
        factory.setConnectTimeout(15000); // 15 segundos para conectar
        factory.setReadTimeout(30000);    // 30 segundos para ler resposta

        RestTemplate restTemplate = new RestTemplate(factory);

        // Adicionar interceptor para logging de requisições
        restTemplate.getInterceptors().add((request, body, execution) -> {
            logger.info("HTTP Request: " + request.getMethod() + " " + request.getURI());
            return execution.execute(request, body);
        });

        logger.info("RestTemplate configured with connect timeout: 15s, read timeout: 30s");
        return restTemplate;
    }

    @Bean("unidadeGestoraConsumoApiService")
    public ConsumoApiService<UnidadeGestoraDTO> unidadeGestoraConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<UnidadeGestoraDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("UnidadeGestora", UnidadeGestoraDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean("contratosFiscaisConsumoApiService")
    public ConsumoApiService<ContratosFiscaisDTO> contratosFiscaisConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ContratosFiscaisDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("ContratosFiscais", ContratosFiscaisDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean("contratosFiscaisConsumoApiServiceTeste")
    public ConsumoApiService<ContratosFiscaisDTO> contratosFiscaisConsumoApiServiceTeste(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ContratosFiscaisDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("ContratosFiscaisTeste", ContratosFiscaisDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean("receitaConsumoApiService")
    public ConsumoApiService<ReceitaDTO> receitaConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ReceitaDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Receita", ReceitaDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<UnidadeGestoraDTO> unidadeGestoraValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("UnidadeGestora", jdbcTemplate);
    }

    @Bean
    public ValidacaoUtil<ContratosFiscaisDTO> contratosFiscaisValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("ContratosFiscais", jdbcTemplate);
    }

    @Bean
    public ValidacaoUtil<ReceitaDTO> receitaValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Receita", jdbcTemplate);
    }

    @Bean("despesaConvenioConsumoApiService")
    public ConsumoApiService<DespesaConvenioDTO> despesaConvenioConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<DespesaConvenioDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("DespesaConvenio", DespesaConvenioDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<DespesaConvenioDTO> despesaConvenioValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("DespesaConvenio", jdbcTemplate);
    }

    @Bean("pagamentoConsumoApiService")
    public ConsumoApiService<PagamentoDTO> pagamentoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<PagamentoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Pagamento", PagamentoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<PagamentoDTO> pagamentoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Pagamento", jdbcTemplate);
    }

    @Bean("ordemFornecimentoConsumoApiService")
    public ConsumoApiService<OrdemFornecimentoDTO> ordemFornecimentoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<OrdemFornecimentoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("OrdemFornecimento", OrdemFornecimentoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<OrdemFornecimentoDTO> ordemFornecimentoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("OrdemFornecimento", jdbcTemplate);
    }

    @Bean("liquidacaoConsumoApiService")
    public ConsumoApiService<LiquidacaoDTO> liquidacaoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<LiquidacaoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Liquidacao", LiquidacaoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<LiquidacaoDTO> liquidacaoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Liquidacao", jdbcTemplate);
    }

    @Bean("dadosOrcamentariosConsumoApiService")
    public ConsumoApiService<DadosOrcamentariosDTO> dadosOrcamentariosConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<DadosOrcamentariosDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("DadosOrcamentarios", DadosOrcamentariosDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<DadosOrcamentariosDTO> dadosOrcamentariosValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("DadosOrcamentarios", jdbcTemplate);
    }

    @Bean("empenhoConsumoApiService")
    public ConsumoApiService<EmpenhoDTO> empenhoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<EmpenhoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Empenho", EmpenhoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<EmpenhoDTO> empenhoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Empenho", jdbcTemplate);
    }

    @Bean("totalizadoresExecucaoConsumoApiService")
    public ConsumoApiService<TotalizadoresExecucaoDTO> totalizadoresExecucaoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<TotalizadoresExecucaoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("TotalizadoresExecucao", TotalizadoresExecucaoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<TotalizadoresExecucaoDTO> totalizadoresExecucaoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("TotalizadoresExecucao", jdbcTemplate);
    }

    @Bean("consultaGerencialConsumoApiService")
    public ConsumoApiService<ConsultaGerencialDTO> consultaGerencialConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ConsultaGerencialDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("ConsultaGerencial", ConsultaGerencialDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<ConsultaGerencialDTO> consultaGerencialValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("ConsultaGerencial", jdbcTemplate);
    }

    @Bean("contratoConsumoApiService")
    public ConsumoApiService<ContratoDTO> contratoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ContratoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Contrato", ContratoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<ContratoDTO> contratoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Contrato", jdbcTemplate);
    }

    @Bean("contratoEmpenhoConsumoApiService")
    public ConsumoApiService<ContratoEmpenhoDTO> contratoEmpenhoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ContratoEmpenhoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("ContratoEmpenho", ContratoEmpenhoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<ContratoEmpenhoDTO> contratoEmpenhoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("ContratoEmpenho", jdbcTemplate);
    }

    @Bean("baseDespesaCredorConsumoApiService")
    public ConsumoApiService<BaseDespesaCredorDTO> baseDespesaCredorConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<BaseDespesaCredorDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("BaseDespesaCredor", BaseDespesaCredorDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<BaseDespesaCredorDTO> baseDespesaCredorValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("BaseDespesaCredor", jdbcTemplate);
    }

    @Bean("baseDespesaLicitacaoConsumoApiService")
    public ConsumoApiService<BaseDespesaLicitacaoDTO> baseDespesaLicitacaoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<BaseDespesaLicitacaoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("BaseDespesaLicitacao", BaseDespesaLicitacaoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<BaseDespesaLicitacaoDTO> baseDespesaLicitacaoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("BaseDespesaLicitacao", jdbcTemplate);
    }

    @Bean("termoConsumoApiService")
    public ConsumoApiService<TermoDTO> termoConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<TermoDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("Termo", TermoDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<TermoDTO> termoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("Termo", jdbcTemplate);
    }

    @Bean("previsaoRealizacaoReceitaConsumoApiService")
    public ConsumoApiService<PrevisaoRealizacaoReceitaDTO> previsaoRealizacaoReceitaConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<PrevisaoRealizacaoReceitaDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("PrevisaoRealizacaoReceita", PrevisaoRealizacaoReceitaDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<PrevisaoRealizacaoReceitaDTO> previsaoRealizacaoReceitaValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("PrevisaoRealizacaoReceita", jdbcTemplate);
    }

    @Bean("despesaDetalhadaConsumoApiService")
    public ConsumoApiService<DespesaDetalhadaDTO> despesaDetalhadaConsumoApiService(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<DespesaDetalhadaDTO> validacaoUtil,
            UnifiedLogger unifiedLogger,
            UserFriendlyLogger userFriendlyLogger,
            MarkdownLogger markdownLogger) {

        return createConsumoApiService("DespesaDetalhada", DespesaDetalhadaDTO.class,
            restTemplate, acessoTokenService, jdbcTemplate, validacaoUtil,
            unifiedLogger, userFriendlyLogger, markdownLogger);
    }

    @Bean
    public ValidacaoUtil<DespesaDetalhadaDTO> despesaDetalhadaValidacaoUtil(JdbcTemplate jdbcTemplate) {
        return createValidacaoUtil("DespesaDetalhada", jdbcTemplate);
    }
}
