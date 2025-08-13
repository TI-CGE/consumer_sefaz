package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.util.ValidacaoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para ConsultaGerencialDTO")
class ConsultaGerencialDTOTest {

    private ConsultaGerencialDTO consultaGerencialDTO;

    @Mock
    private ValidacaoUtil<?> validacaoUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consultaGerencialDTO = new ConsultaGerencialDTO();
    }

    @Test
    @DisplayName("Deve converter string de data de saída corretamente")
    void deveConverterStringDataSaida() {
        // Act
        consultaGerencialDTO.setDtSaidaSolicitacaoDiaria("2025-08-13");

        // Assert
        assertEquals("2025-08-13", consultaGerencialDTO.getDtSaidaSolicitacaoDiariaStr());
        assertEquals(LocalDate.of(2025, 8, 13), consultaGerencialDTO.getDtSaidaSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve converter string de data de retorno corretamente")
    void deveConverterStringDataRetorno() {
        // Act
        consultaGerencialDTO.setDtRetornoSolicitacaoDiaria("2025-08-13");

        // Assert
        assertEquals("2025-08-13", consultaGerencialDTO.getDtRetornoSolicitacaoDiariaStr());
        assertEquals(LocalDate.of(2025, 8, 13), consultaGerencialDTO.getDtRetornoSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve converter string de valor total corretamente")
    void deveConverterStringValorTotal() {
        // Act
        consultaGerencialDTO.setVlTotalSolicitacaoDiaria("25");

        // Assert
        assertEquals("25", consultaGerencialDTO.getVlTotalSolicitacaoDiariaStr());
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve converter integer de valor total corretamente")
    void deveConverterIntegerValorTotal() {
        // Act
        consultaGerencialDTO.setVlTotalSolicitacaoDiaria(25);

        // Assert
        assertEquals("25", consultaGerencialDTO.getVlTotalSolicitacaoDiariaStr());
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve converter string de valor pago atualizado corretamente")
    void deveConverterStringValorPagoAtualizado() {
        // Act
        consultaGerencialDTO.setVlTotalValorPagoAtualizado("25");

        // Assert
        assertEquals("25", consultaGerencialDTO.getVlTotalValorPagoAtualizadoStr());
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalValorPagoAtualizado());
    }

    @Test
    @DisplayName("Deve converter integer de valor pago atualizado corretamente")
    void deveConverterIntegerValorPagoAtualizado() {
        // Act
        consultaGerencialDTO.setVlTotalValorPagoAtualizado(25);

        // Assert
        assertEquals("25", consultaGerencialDTO.getVlTotalValorPagoAtualizadoStr());
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalValorPagoAtualizado());
    }

    @Test
    @DisplayName("Deve tratar data inválida sem falhar")
    void deveTratarDataInvalidaSemFalhar() {
        // Act
        consultaGerencialDTO.setDtSaidaSolicitacaoDiaria("data-invalida");

        // Assert
        assertEquals("data-invalida", consultaGerencialDTO.getDtSaidaSolicitacaoDiariaStr());
        assertNull(consultaGerencialDTO.getDtSaidaSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve tratar valor inválido sem falhar")
    void deveTratarValorInvalidoSemFalhar() {
        // Act
        consultaGerencialDTO.setVlTotalSolicitacaoDiaria("valor-invalido");

        // Assert
        assertEquals("valor-invalido", consultaGerencialDTO.getVlTotalSolicitacaoDiariaStr());
        assertEquals(BigDecimal.ZERO, consultaGerencialDTO.getVlTotalSolicitacaoDiaria());
    }

    @Test
    @DisplayName("Deve obter parâmetros para todos os anos corretamente")
    void deveObterParametrosTodosAnos() {
        // Act
        Map<String, Object> parametros = consultaGerencialDTO.getCamposParametrosTodosOsAnos("222011", (short) 2025);

        // Assert
        assertEquals("222011", parametros.get("cdUnidadeGestora"));
        assertEquals(2025, parametros.get("dtAnoExercicioCTB"));
    }

    @Test
    @DisplayName("Deve obter parâmetros atuais corretamente")
    void deveObterParametrosAtuais() {
        // Act
        Map<String, Object> parametros = consultaGerencialDTO.getCamposParametrosAtual("222011", validacaoUtil);

        // Assert
        assertEquals("222011", parametros.get("cdUnidadeGestora"));
        assertEquals(2025, parametros.get("dtAnoExercicioCTB"));
    }

    @Test
    @DisplayName("Deve simular mapeamento completo da resposta da API")
    void deveSimularMapeamentoCompletoAPI() {
        // Arrange - Simular dados da API como fornecidos pelo usuário
        consultaGerencialDTO.setDtRetornoSolicitacaoDiaria("2025-08-13");
        consultaGerencialDTO.setDtSaidaSolicitacaoDiaria("2025-08-13");
        consultaGerencialDTO.setSqOB(8126L);
        consultaGerencialDTO.setVlTotalValorPagoAtualizado(25); // Integer da API
        consultaGerencialDTO.setTpViagemSolicitacaoDiaria("M");
        consultaGerencialDTO.setDestinoViagemMunicipioSolicitacaoDiaria("AQUIDABA");
        consultaGerencialDTO.setDestinoViagemPaisSolicitacaoDiaria("BRASIL");
        consultaGerencialDTO.setNmRazaoSocialPessoa("JOSE HELIO DE JESUS");
        consultaGerencialDTO.setQtdDiariaSolicitacaoDiaria(1);
        consultaGerencialDTO.setTpTransporteViagemSolicitacaoDiaria("C");
        consultaGerencialDTO.setCdUnidadeGestora("222011");
        consultaGerencialDTO.setDsQualificacaoVinculo("DEMAIS CARGOS, FUNÇÕES OU EMPREGOS.");
        consultaGerencialDTO.setDtAnoExercicioCTB(2025);
        consultaGerencialDTO.setDestinoViagemUFSolicitacaoDiaria("SERGIPE");
        consultaGerencialDTO.setCdGestao("22201");
        consultaGerencialDTO.setNuDocumento("60972270515");
        consultaGerencialDTO.setTpDocumento(3);
        consultaGerencialDTO.setTxMotivoSolicitacao("FISCALIZACAO DE AUTOESCOLA - AQUIDABA");
        consultaGerencialDTO.setSqSolicEmpenho(4710L);
        consultaGerencialDTO.setSqEmpenho(4630L);
        consultaGerencialDTO.setSgUnidadeGestora("DETRAN");
        consultaGerencialDTO.setSqSolicitacaoDiaria(3358L);
        consultaGerencialDTO.setSqPrevisaoDesembolso(8074L);
        consultaGerencialDTO.setVlTotalSolicitacaoDiaria(25); // Integer da API

        // Act
        consultaGerencialDTO.mapearCamposResposta();
        Map<String, Object> campos = consultaGerencialDTO.getCamposResposta();

        // Assert - Verificar se todos os campos foram mapeados corretamente
        assertEquals(LocalDate.of(2025, 8, 13), campos.get("dt_retorno_solicitacao_diaria"));
        assertEquals(LocalDate.of(2025, 8, 13), campos.get("dt_saida_solicitacao_diaria"));
        assertEquals(8126L, campos.get("sq_ob"));
        assertEquals(new BigDecimal("25"), campos.get("vl_total_valor_pago_atualizado"));
        assertEquals("M", campos.get("tp_viagem_solicitacao_diaria"));
        assertEquals("AQUIDABA", campos.get("destino_viagem_municipio_solicitacao_diaria"));
        assertEquals("BRASIL", campos.get("destino_viagem_pais_solicitacao_diaria"));
        assertEquals("JOSE HELIO DE JESUS", campos.get("nm_razao_social_pessoa"));
        assertEquals(1, campos.get("qtd_diaria_solicitacao_diaria"));
        assertEquals("C", campos.get("tp_transporte_viagem_solicitacao_diaria"));
        assertEquals("222011", campos.get("cd_unidade_gestora"));
        assertEquals("DEMAIS CARGOS, FUNÇÕES OU EMPREGOS.", campos.get("ds_qualificacao_vinculo"));
        assertEquals(2025, campos.get("dt_ano_exercicio_ctb"));
        assertEquals("SERGIPE", campos.get("destino_viagem_uf_solicitacao_diaria"));
        assertEquals("22201", campos.get("cd_gestao"));
        assertEquals("60972270515", campos.get("nu_documento"));
        assertEquals(3, campos.get("tp_documento"));
        assertEquals("FISCALIZACAO DE AUTOESCOLA - AQUIDABA", campos.get("tx_motivo_solicitacao"));
        assertEquals(4710L, campos.get("sq_solic_empenho"));
        assertEquals(4630L, campos.get("sq_empenho"));
        assertEquals("DETRAN", campos.get("sg_unidade_gestora"));
        assertEquals(3358L, campos.get("sq_solicitacao_diaria"));
        assertEquals(8074L, campos.get("sq_previsao_desembolso"));
        assertEquals(new BigDecimal("25"), campos.get("vl_total_solicitacao_diaria"));

        // Verificar se as datas foram convertidas corretamente
        assertEquals(LocalDate.of(2025, 8, 13), consultaGerencialDTO.getDtRetornoSolicitacaoDiaria());
        assertEquals(LocalDate.of(2025, 8, 13), consultaGerencialDTO.getDtSaidaSolicitacaoDiaria());

        // Verificar se os valores monetários foram convertidos corretamente
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalValorPagoAtualizado());
        assertEquals(new BigDecimal("25"), consultaGerencialDTO.getVlTotalSolicitacaoDiaria());
    }
}
