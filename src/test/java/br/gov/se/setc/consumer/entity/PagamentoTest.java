package br.gov.se.setc.consumer.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Testes para entidade Pagamento")
class PagamentoTest {

    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        pagamento = new Pagamento();
    }

    @Test
    @DisplayName("Deve criar instância vazia corretamente")
    void deveCriarInstanciaVazia() {
        // Assert
        assertNotNull(pagamento);
        assertNull(pagamento.getId());
        assertNull(pagamento.getDtAnoExercicioCTB());
        assertNull(pagamento.getCdUnidadeGestora());
    }

    @Test
    @DisplayName("Deve definir e obter campos básicos corretamente")
    void deveDefinirEObterCamposBasicos() {
        // Arrange & Act
        pagamento.setDtAnoExercicioCTB(2025);
        pagamento.setCdUnidadeGestora("123456");
        pagamento.setCdGestao("001");
        pagamento.setSgUnidadeGestora("UG001");
        pagamento.setIdOrgao("ORG001");
        pagamento.setSgOrgao("SORG");

        // Assert
        assertEquals(2025, pagamento.getDtAnoExercicioCTB());
        assertEquals("123456", pagamento.getCdUnidadeGestora());
        assertEquals("001", pagamento.getCdGestao());
        assertEquals("UG001", pagamento.getSgUnidadeGestora());
        assertEquals("ORG001", pagamento.getIdOrgao());
        assertEquals("SORG", pagamento.getSgOrgao());
    }

    @Test
    @DisplayName("Deve definir e obter valores monetários corretamente")
    void deveDefinirEObterValoresMonetarios() {
        // Arrange
        BigDecimal vlBruto = new BigDecimal("1000.50");
        BigDecimal vlRetido = new BigDecimal("100.25");
        BigDecimal vlOB = new BigDecimal("900.25");

        // Act
        pagamento.setVlBrutoPD(vlBruto);
        pagamento.setVlRetidoPD(vlRetido);
        pagamento.setVlOB(vlOB);

        // Assert
        assertEquals(vlBruto, pagamento.getVlBrutoPD());
        assertEquals(vlRetido, pagamento.getVlRetidoPD());
        assertEquals(vlOB, pagamento.getVlOB());
    }

    @Test
    @DisplayName("Deve definir e obter campos de data corretamente")
    void deveDefinirEObterCamposDatas() {
        // Arrange
        LocalDate dtPrevisao = LocalDate.of(2025, 1, 15);
        LocalDate dtLancamento = LocalDate.of(2025, 1, 20);

        // Act
        pagamento.setDtAnoExercicioCTBReferencia(2024);
        pagamento.setDtPrevisaoDesembolso(dtPrevisao);
        pagamento.setDtLancamentoOB(dtLancamento);

        // Assert
        assertEquals(2024, pagamento.getDtAnoExercicioCTBReferencia());
        assertEquals(dtPrevisao, pagamento.getDtPrevisaoDesembolso());
        assertEquals(dtLancamento, pagamento.getDtLancamentoOB());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em campos opcionais")
    void deveAceitarValoresNulos() {
        // Arrange & Act
        pagamento.setVlBrutoPD(null);
        pagamento.setDtPrevisaoDesembolso(null);
        pagamento.setCdLicitacao(null);
        pagamento.setDsObjetoLicitacao(null);

        // Assert
        assertNull(pagamento.getVlBrutoPD());
        assertNull(pagamento.getDtPrevisaoDesembolso());
        assertNull(pagamento.getCdLicitacao());
        assertNull(pagamento.getDsObjetoLicitacao());
    }
}
