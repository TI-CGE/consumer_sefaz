package br.gov.se.setc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.dto.DadosOrcamentariosDTO;
import br.gov.se.setc.consumer.dto.EmpenhoDTO;
import br.gov.se.setc.consumer.dto.LiquidacaoDTO;
import br.gov.se.setc.consumer.dto.OrdemFornecimentoDTO;
import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.consumer.dto.UnidadeGestoraDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.consumer.service.ContratodFiscaisServices;
import br.gov.se.setc.logging.MarkdownLogger;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import br.gov.se.setc.util.ValidacaoUtil;

@Configuration
public class SefazConsumerConfig {

    private static final Logger logger = Logger.getLogger(SefazConsumerConfig.class.getName());

    // Primary RestTemplate bean for the entire application
    @Bean("sefazRestTemplate")
    @Primary
    public RestTemplate restTemplate() {
        logger.info("Creating primary SEFAZ RestTemplate bean");
        return new RestTemplate();
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

        logger.info("Creating UnidadeGestora ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            UnidadeGestoraDTO.class
        );
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

        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            ContratosFiscaisDTO.class
        );
    }

    @Bean("contratosFiscaisConsumoApiServiceTeste")
    public ContratodFiscaisServices contratosFiscaisConsumoApiServiceTeste(
            RestTemplate restTemplate,
            AcessoTokenService acessoTokenService,
            JdbcTemplate jdbcTemplate,
            ValidacaoUtil<ContratosFiscaisDTO> validacaoUtil,
            UnifiedLogger unifiedLogger) {

        return new ContratodFiscaisServices(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger
        );
    }

    @Bean
    public ValidacaoUtil<UnidadeGestoraDTO> unidadeGestoraValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating UnidadeGestora ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
    }

    @Bean
    public ValidacaoUtil<ContratosFiscaisDTO> contratosFiscaisValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating ContratosFiscais ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating Receita ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            ReceitaDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<ReceitaDTO> receitaValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating Receita ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating Pagamento ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            PagamentoDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<PagamentoDTO> pagamentoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating Pagamento ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating OrdemFornecimento ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            OrdemFornecimentoDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<OrdemFornecimentoDTO> ordemFornecimentoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating OrdemFornecimento ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating Liquidacao ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            LiquidacaoDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<LiquidacaoDTO> liquidacaoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating Liquidacao ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating DadosOrcamentarios ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            DadosOrcamentariosDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<DadosOrcamentariosDTO> dadosOrcamentariosValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating DadosOrcamentarios ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
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

        logger.info("Creating Empenho ConsumoApiService bean");
        return new ConsumoApiService<>(
            restTemplate,
            acessoTokenService,
            jdbcTemplate,
            validacaoUtil,
            unifiedLogger,
            userFriendlyLogger,
            markdownLogger,
            EmpenhoDTO.class
        );
    }

    @Bean
    public ValidacaoUtil<EmpenhoDTO> empenhoValidacaoUtil(JdbcTemplate jdbcTemplate) {
        logger.info("Creating Empenho ValidacaoUtil bean");
        return new ValidacaoUtil<>(jdbcTemplate);
    }
}
