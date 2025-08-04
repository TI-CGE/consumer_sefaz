package br.gov.se.setc.consumer.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;

@DisplayName("Testes para SwaggerPagamentoController")
class SwaggerPagamentoControllerTest {

    @Mock
    private ConsumoApiService<PagamentoDTO> consumoApiService;

    private SwaggerPagamentoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SwaggerPagamentoController(consumoApiService);
    }

    @Test
    @DisplayName("Deve listar pagamentos com sucesso")
    void deveListarPagamentosComSucesso() {
        // Arrange
        PagamentoDTO pagamento1 = createSamplePagamento(1L, "123456", new BigDecimal("1000.00"));
        PagamentoDTO pagamento2 = createSamplePagamento(2L, "789012", new BigDecimal("2000.00"));
        List<PagamentoDTO> expectedList = Arrays.asList(pagamento1, pagamento2);

        when(consumoApiService.consumirPersistir(any(PagamentoDTO.class))).thenReturn(expectedList);

        // Act
        List<PagamentoDTO> result = controller.listarPagamento();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(consumoApiService, times(1)).consumirPersistir(any(PagamentoDTO.class));
    }

    @Test
    @DisplayName("Deve executar teste de endpoint com sucesso")
    void deveExecutarTesteEndpointComSucesso() {
        // Act
        ResponseEntity<String> response = controller.testeEndpoint();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Endpoint funcionando!"));
        assertTrue(response.getBody().contains("consumer_sefaz.pagamento"));
    }

    @Test
    @DisplayName("Deve propagar exceção quando serviço falha")
    void devePropagrarExcecaoQuandoServicoFalha() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Erro no serviço");
        when(consumoApiService.consumirPersistir(any(PagamentoDTO.class))).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            controller.listarPagamento();
        });

        assertEquals("Erro no serviço", thrownException.getMessage());
        verify(consumoApiService, times(1)).consumirPersistir(any(PagamentoDTO.class));
    }

    private PagamentoDTO createSamplePagamento(Long sqEmpenho, String cdUnidadeGestora, BigDecimal vlBruto) {
        PagamentoDTO pagamento = new PagamentoDTO();
        pagamento.setSqEmpenho(sqEmpenho);
        pagamento.setCdUnidadeGestora(cdUnidadeGestora);
        pagamento.setVlBrutoPD(vlBruto);
        pagamento.setDtAnoExercicioCTB(2025);
        pagamento.setSgUnidadeGestora("UG" + cdUnidadeGestora.substring(0, 3));
        pagamento.setDtLancamentoOB(LocalDate.of(2025, 1, 15));
        pagamento.setInSituacaoPagamento("PAGO");
        return pagamento;
    }
}
