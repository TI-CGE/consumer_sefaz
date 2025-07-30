package br.gov.se.setc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
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
}
