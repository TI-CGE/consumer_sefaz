package br.gov.se.setc.consumer.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import br.gov.se.setc.consumer.dto.OrdemFornecimentoDTO;
import br.gov.se.setc.consumer.respository.EndpontSefazRepository;
import br.gov.se.setc.logging.UnifiedLogger;

class OrdemFornecimentoDeleteBeforeInsertTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    
    @Mock
    private UnifiedLogger unifiedLogger;
    
    private EndpontSefazRepository<OrdemFornecimentoDTO> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new EndpontSefazRepository<>(jdbcTemplate, unifiedLogger);
    }

    private OrdemFornecimentoDTO createSampleOrdemFornecimento() {
        OrdemFornecimentoDTO ordem = new OrdemFornecimentoDTO();
        ordem.setCdUnidadeGestora("001");
        ordem.setCdGestao("02");
        ordem.setDtAnoExercicioEmp(2024);
        ordem.setSqEmpenho(123L);
        ordem.setSqOrdemFornecimento(456L);
        ordem.setDtRecebimento(LocalDate.of(2024, 8, 5));
        ordem.setVlOrdemFornecimento(new BigDecimal("1500.00"));
        ordem.setNuNfe("123456789");
        ordem.setNmDestinatario("Empresa Teste Ltda");
        ordem.setVlTotalICMS(new BigDecimal("270.00"));
        return ordem;
    }

    @Test
    @DisplayName("Deve executar DELETE antes de INSERT seguindo padrão DELETE-BEFORE-INSERT")
    void deveExecutarDeleteAntesDeInsert() {
        // Arrange
        List<OrdemFornecimentoDTO> ordens = Arrays.asList(
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento()
        );
        
        when(jdbcTemplate.update((String) anyString())).thenReturn(5); // 5 registros deletados
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1); // 1 registro inserido por vez

        // Act
        repository.persist(ordens);

        // Assert
        // Verifica se o DELETE foi executado primeiro
        verify(jdbcTemplate, times(1)).update(argThat((String sql) ->
            sql.contains("DELETE FROM consumer_sefaz.ordem_fornecimento") &&
            sql.contains("EXTRACT(YEAR FROM dt_recebimento ) = EXTRACT(YEAR FROM CURRENT_DATE)") &&
            sql.contains("EXTRACT(MONTH FROM dt_recebimento ) = EXTRACT(MONTH FROM CURRENT_DATE)")
        ));

        // Verifica se os INSERTs foram executados após o DELETE
        verify(jdbcTemplate, times(ordens.size())).update(
            argThat((String sql) -> sql.contains("INSERT INTO consumer_sefaz.ordem_fornecimento")),
            any(Object[].class)
        );

        // Verifica logs
        verify(unifiedLogger, atLeastOnce()).logDatabaseOperation(eq("DELETE"), 
            eq("consumer_sefaz.ordem_fornecimento"), eq(5), anyLong());
    }

    @Test
    @DisplayName("Deve executar DELETE por mês vigente corretamente")
    void deveExecutarDeletePorMesVigenteCorretamente() {
        // Arrange
        OrdemFornecimentoDTO ordem = createSampleOrdemFornecimento();
        
        when(jdbcTemplate.update((String) anyString())).thenReturn(3);

        // Act
        repository.deleteByMesVigente(ordem);

        // Assert
        verify(jdbcTemplate, times(1)).update(argThat((String sql) ->
            sql.contains("DELETE FROM consumer_sefaz.ordem_fornecimento") &&
            sql.contains("EXTRACT(YEAR FROM dt_recebimento ) = EXTRACT(YEAR FROM CURRENT_DATE)") &&
            sql.contains("EXTRACT(MONTH FROM dt_recebimento ) = EXTRACT(MONTH FROM CURRENT_DATE)")
        ));

        verify(unifiedLogger, times(1)).logDatabaseOperation(eq("DELETE"), 
            eq("consumer_sefaz.ordem_fornecimento"), eq(3), anyLong());
    }

    @Test
    @DisplayName("Deve inserir registros com logging de progresso")
    void deveInserirRegistrosComLoggingProgresso() {
        // Arrange
        List<OrdemFornecimentoDTO> ordens = Arrays.asList(
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento(),
            createSampleOrdemFornecimento()
        );
        
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        // Act
        repository.insert(ordens);

        // Assert
        verify(jdbcTemplate, times(ordens.size())).update(anyString(), any(Object[].class));
        
        // Verifica se o log de início da operação foi chamado
        verify(unifiedLogger, times(1)).logOperationStart(eq("DATABASE"), eq("INSERT_BATCH"), 
            eq("TABLE"), eq("consumer_sefaz.ordem_fornecimento"), eq("COUNT"), eq(ordens.size()));
    }

    @Test
    @DisplayName("Deve tratar lista vazia sem executar operações")
    void deveTratarListaVaziaSemExecutarOperacoes() {
        // Arrange
        List<OrdemFornecimentoDTO> ordensVazia = Arrays.asList();

        // Act
        repository.persist(ordensVazia);

        // Assert
        verify(jdbcTemplate, never()).update(anyString());
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Deve tratar lista null sem executar operações")
    void deveTratarListaNullSemExecutarOperacoes() {
        // Arrange
        List<OrdemFornecimentoDTO> ordensNull = null;

        // Act
        repository.persist(ordensNull);

        // Assert
        verify(jdbcTemplate, never()).update(anyString());
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Deve usar configuração correta do endpoint")
    void deveUsarConfiguracaoCorretaEndpoint() {
        // Arrange
        OrdemFornecimentoDTO ordem = createSampleOrdemFornecimento();

        // Assert
        assertEquals("consumer_sefaz.ordem_fornecimento", ordem.getTabelaBanco());
        assertEquals("dt_recebimento", ordem.getNomeDataInicialPadraoFiltro());
        assertEquals("dt_recebimento", ordem.getNomeDataFinalPadraoFiltro());
        assertEquals("dt_ano_exercicio_emp", ordem.getDtAnoPadrao());
    }

    @Test
    @DisplayName("Deve mapear campos de resposta corretamente para inserção")
    void deveMapeiarCamposRespostaCorretamenteParaInsercao() {
        // Arrange
        OrdemFornecimentoDTO ordem = createSampleOrdemFornecimento();
        
        // Act
        ordem.mapearCamposResposta();
        
        // Assert
        assertNotNull(ordem.getCamposResposta());
        assertTrue(ordem.getCamposResposta().containsKey("cd_unidade_gestora"));
        assertTrue(ordem.getCamposResposta().containsKey("dt_recebimento"));
        assertTrue(ordem.getCamposResposta().containsKey("vl_ordem_fornecimento"));
        assertTrue(ordem.getCamposResposta().containsKey("sq_empenho"));
        assertTrue(ordem.getCamposResposta().containsKey("nu_nfe"));
        assertTrue(ordem.getCamposResposta().containsKey("nm_destinatario"));
        assertTrue(ordem.getCamposResposta().containsKey("vl_total_icms"));
    }
}
