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

import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.respository.EndpontSefazRepository;
import br.gov.se.setc.logging.UnifiedLogger;

@DisplayName("Testes de Integração - DELETE-BEFORE-INSERT para Pagamento")
class PagamentoDeleteBeforeInsertTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UnifiedLogger unifiedLogger;

    private EndpontSefazRepository<PagamentoDTO> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new EndpontSefazRepository<>(jdbcTemplate, unifiedLogger);
    }

    @Test
    @DisplayName("Deve executar DELETE-BEFORE-INSERT corretamente")
    void deveExecutarDeleteBeforeInsertCorretamente() {
        // Arrange
        List<PagamentoDTO> pagamentos = createSamplePagamentos();
        
        // Mock do delete
        when(jdbcTemplate.update(contains("DELETE FROM consumer_sefaz.pagamento")))
            .thenReturn(5); // Simula que 5 registros foram deletados

        // Mock do insert
        when(jdbcTemplate.update(contains("INSERT INTO consumer_sefaz.pagamento"), any(Object[].class)))
            .thenReturn(1); // Simula sucesso na inserção

        // Act
        repository.persist(pagamentos);

        // Assert
        // Verifica se o DELETE foi executado primeiro
        verify(jdbcTemplate, times(1)).update(argThat((String sql) ->
            sql.contains("DELETE FROM consumer_sefaz.pagamento") &&
            sql.contains("EXTRACT(YEAR FROM dt_lancamento_ob)") &&
            sql.contains("EXTRACT(MONTH FROM dt_lancamento_ob)")
        ));

        // Verifica se os INSERTs foram executados após o DELETE
        verify(jdbcTemplate, times(pagamentos.size())).update(
            contains("INSERT INTO consumer_sefaz.pagamento"), 
            any(Object[].class)
        );

        // Verifica logs
        verify(unifiedLogger, atLeastOnce()).logDatabaseOperation(eq("DELETE"), 
            eq("consumer_sefaz.pagamento"), eq(5), anyLong());
    }

    @Test
    @DisplayName("Deve executar DELETE por mês vigente corretamente")
    void deveExecutarDeletePorMesVigenteCorretamente() {
        // Arrange
        PagamentoDTO pagamento = createSamplePagamento();
        
        when(jdbcTemplate.update((String) anyString())).thenReturn(3);

        // Act
        repository.deleteByMesVigente(pagamento);

        // Assert
        verify(jdbcTemplate, times(1)).update(argThat((String sql) ->
            sql.contains("DELETE FROM consumer_sefaz.pagamento") &&
            sql.contains("EXTRACT(YEAR FROM dt_lancamento_ob) = EXTRACT(YEAR FROM CURRENT_DATE)") &&
            sql.contains("EXTRACT(MONTH FROM dt_lancamento_ob) = EXTRACT(MONTH FROM CURRENT_DATE)")
        ));

        verify(unifiedLogger, times(1)).logDatabaseOperation(eq("DELETE"), 
            eq("consumer_sefaz.pagamento"), eq(3), anyLong());
    }

    @Test
    @DisplayName("Deve inserir registros em lote corretamente")
    void deveInserirRegistrosEmLoteCorretamente() {
        // Arrange
        List<PagamentoDTO> pagamentos = createSamplePagamentos();
        
        when(jdbcTemplate.update((String) anyString(), any(Object[].class))).thenReturn(1);

        // Act
        repository.insert(pagamentos);

        // Assert
        verify(jdbcTemplate, times(pagamentos.size())).update(
            argThat(sql -> sql.contains("INSERT INTO consumer_sefaz.pagamento")), 
            any(Object[].class)
        );

        // Verifica se todos os campos estão sendo inseridos
        verify(jdbcTemplate, times(pagamentos.size())).update(
            argThat(sql -> 
                sql.contains("dt_ano_exercicio_ctb") &&
                sql.contains("cd_unidade_gestora") &&
                sql.contains("vl_bruto_pd") &&
                sql.contains("dt_lancamento_ob") &&
                sql.contains("in_situacao_pagamento")
            ), 
            any(Object[].class)
        );
    }

    @Test
    @DisplayName("Deve lidar com lista vazia sem executar operações")
    void deveLidarComListaVaziaSemExecutarOperacoes() {
        // Arrange
        List<PagamentoDTO> pagamentosVazios = Arrays.asList();

        // Act
        repository.persist(pagamentosVazios);

        // Assert
        verify(jdbcTemplate, never()).update(anyString());
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Deve lidar com lista nula sem executar operações")
    void deveLidarComListaNulaSemExecutarOperacoes() {
        // Act
        repository.persist(null);

        // Assert
        verify(jdbcTemplate, never()).update(anyString());
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Deve propagar exceção em caso de erro no DELETE")
    void devePropagrarExcecaoEmCasoDeErroNoDelete() {
        // Arrange
        List<PagamentoDTO> pagamentos = createSamplePagamentos();
        RuntimeException expectedException = new RuntimeException("Erro no DELETE");
        
        when(jdbcTemplate.update(contains("DELETE"))).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            repository.persist(pagamentos);
        });

        assertEquals("Erro no DELETE", thrownException.getMessage());
        
        // Verifica que o erro foi logado
        verify(unifiedLogger, times(1)).logOperationError(
            eq("DATABASE"), eq("DELETE_BY_MONTH"), anyLong(), eq(expectedException), 
            eq("TABLE"), eq("consumer_sefaz.pagamento")
        );
    }

    @Test
    @DisplayName("Deve propagar exceção em caso de erro no INSERT")
    void devePropagrarExcecaoEmCasoDeErroNoInsert() {
        // Arrange
        List<PagamentoDTO> pagamentos = createSamplePagamentos();
        RuntimeException expectedException = new RuntimeException("Erro no INSERT");
        
        when(jdbcTemplate.update(contains("DELETE"))).thenReturn(0);
        when(jdbcTemplate.update(contains("INSERT"), any(Object[].class))).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            repository.persist(pagamentos);
        });

        assertEquals("Erro no INSERT", thrownException.getMessage());
    }

    @Test
    @DisplayName("Deve executar operações na ordem correta: DELETE depois INSERT")
    void deveExecutarOperacoesNaOrdemCorreta() {
        // Arrange
        List<PagamentoDTO> pagamentos = createSamplePagamentos();
        
        when(jdbcTemplate.update(contains("DELETE"))).thenReturn(2);
        when(jdbcTemplate.update(contains("INSERT"), any(Object[].class))).thenReturn(1);

        // Act
        repository.persist(pagamentos);

        // Assert - Verifica ordem das operações usando InOrder
        var inOrder = inOrder(jdbcTemplate);
        inOrder.verify(jdbcTemplate).update(contains("DELETE"));
        inOrder.verify(jdbcTemplate, times(pagamentos.size())).update(contains("INSERT"), any(Object[].class));
    }

    private List<PagamentoDTO> createSamplePagamentos() {
        PagamentoDTO pagamento1 = createSamplePagamento();
        pagamento1.setSqEmpenho(1L);
        pagamento1.setCdUnidadeGestora("123456");

        PagamentoDTO pagamento2 = createSamplePagamento();
        pagamento2.setSqEmpenho(2L);
        pagamento2.setCdUnidadeGestora("789012");

        return Arrays.asList(pagamento1, pagamento2);
    }

    private PagamentoDTO createSamplePagamento() {
        PagamentoDTO pagamento = new PagamentoDTO();
        pagamento.setDtAnoExercicioCTB(2025);
        pagamento.setCdUnidadeGestora("123456");
        pagamento.setSgUnidadeGestora("UG001");
        pagamento.setSqEmpenho(1L);
        pagamento.setSqOB(100L);
        pagamento.setVlBrutoPD(new BigDecimal("1000.00"));
        pagamento.setVlOB(new BigDecimal("900.00"));
        pagamento.setDtLancamentoOB(LocalDate.of(2025, 1, 15));
        pagamento.setInSituacaoPagamento("PAGO");
        pagamento.setSituacaoOB("LIQUIDADO");
        return pagamento;
    }
}
