package br.gov.se.setc.consumer.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

/**
 * Testes unitários para ConsultaGerencialDTO
 * 
 * Valida especialmente os novos setters que aceitam valores numéricos
 * para resolver problemas de conversão da API.
 */
public class ConsultaGerencialDTOTest {

    private ConsultaGerencialDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ConsultaGerencialDTO();
    }

    @Test
    void testSetVlTotalValorPagoAtualizadoComNumber() {
        // Teste com Integer (como vem na API: "vlTotalValorPagoAtualizado": 25)
        dto.setVlTotalValorPagoAtualizado(25);
        assertEquals("25", dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(new BigDecimal("25"), dto.getVlTotalValorPagoAtualizado());
    }

    @Test
    void testSetVlTotalValorPagoAtualizadoComDouble() {
        // Teste com Double
        dto.setVlTotalValorPagoAtualizado(25.50);
        assertEquals("25.5", dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(new BigDecimal("25.5"), dto.getVlTotalValorPagoAtualizado());
    }

    @Test
    void testSetVlTotalValorPagoAtualizadoComString() {
        // Teste com String
        dto.setVlTotalValorPagoAtualizadoStr("25.75");
        assertEquals("25.75", dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(new BigDecimal("25.75"), dto.getVlTotalValorPagoAtualizado());
    }

    @Test
    void testSetVlTotalSolicitacaoDiariaComNumber() {
        // Teste com Integer
        dto.setVlTotalSolicitacaoDiaria(100);
        assertEquals("100", dto.getVlTotalSolicitacaoDiariaStr());
        assertEquals(new BigDecimal("100"), dto.getVlTotalSolicitacaoDiaria());
    }

    @Test
    void testSetVlDescontoSolicitacaoDiariaComNumber() {
        // Teste com Double
        dto.setVlDescontoSolicitacaoDiaria(15.25);
        assertEquals("15.25", dto.getVlDescontoSolicitacaoDiariaStr());
        assertEquals(new BigDecimal("15.25"), dto.getVlDescontoSolicitacaoDiaria());
    }

    @Test
    void testSetVlValorMoedaComNumber() {
        // Teste com Integer
        dto.setVlValorMoeda(50);
        assertEquals("50", dto.getVlValorMoedaStr());
        assertEquals(new BigDecimal("50"), dto.getVlValorMoeda());
    }

    @Test
    void testSetValoresNulos() {
        // Teste com valores nulos - quando passamos null para Number, o valor fica null
        dto.setVlTotalValorPagoAtualizado((Number) null);
        assertNull(dto.getVlTotalValorPagoAtualizadoStr());
        assertNull(dto.getVlTotalValorPagoAtualizado());

        dto.setVlTotalSolicitacaoDiaria((Number) null);
        assertNull(dto.getVlTotalSolicitacaoDiariaStr());
        assertNull(dto.getVlTotalSolicitacaoDiaria());

        // Teste com string null - quando passamos null para String, o valor fica como ZERO
        dto.setVlTotalValorPagoAtualizadoStr(null);
        assertNull(dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(BigDecimal.ZERO, dto.getVlTotalValorPagoAtualizado());
    }

    @Test
    void testCamposObrigatoriosInicializacao() {
        // Verificar se os campos essenciais são inicializados corretamente
        assertNotNull(dto.getUrl());
        assertNotNull(dto.getTabelaBanco());
        assertEquals("consumer_sefaz.consulta_gerencial", dto.getTabelaBanco());
        assertEquals("https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial", dto.getUrl());
    }

    @Test
    void testMapeamentoCamposResposta() {
        // Verificar se o mapeamento de campos está funcionando
        dto.setCdUnidadeGestora("123456");
        dto.setSgUnidadeGestora("TESTE");
        dto.setDtAnoExercicioCTB(2025);
        
        assertNotNull(dto.getCamposResposta());
        assertTrue(dto.getCamposResposta().containsKey("cd_unidade_gestora"));
        assertTrue(dto.getCamposResposta().containsKey("sg_unidade_gestora"));
        assertTrue(dto.getCamposResposta().containsKey("dt_ano_exercicio_ctb"));
    }

    @Test
    void testParametrosFiltro() {
        // Teste dos parâmetros de filtro
        dto.setCdUnidadeGestoraFiltro("123456");
        dto.setDtAnoExercicioCTBFiltro(2025);
        
        assertNotNull(dto.getCamposParametros());
        assertEquals("123456", dto.getCdUnidadeGestoraFiltro());
        assertEquals(Integer.valueOf(2025), dto.getDtAnoExercicioCTBFiltro());
    }

    @Test
    void testConversaoStringVazia() {
        // Teste com string vazia
        dto.setVlTotalValorPagoAtualizadoStr("");
        assertEquals("", dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(BigDecimal.ZERO, dto.getVlTotalValorPagoAtualizado());
    }

    @Test
    void testConversaoStringInvalida() {
        // Teste com string inválida
        dto.setVlTotalValorPagoAtualizadoStr("abc");
        assertEquals("abc", dto.getVlTotalValorPagoAtualizadoStr());
        assertEquals(BigDecimal.ZERO, dto.getVlTotalValorPagoAtualizado());
    }
}
