package br.gov.se.setc.consumer.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.se.setc.util.ValidacaoUtil;

@DisplayName("Testes para PagamentoDTO")
class PagamentoDTOTest {

    @Mock
    private ValidacaoUtil<?> validacaoUtil;

    private PagamentoDTO pagamentoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pagamentoDTO = new PagamentoDTO();
    }

    @Test
    @DisplayName("Deve inicializar dados do endpoint corretamente")
    void deveInicializarDadosEndpoint() {
        // Assert
        assertEquals("consumer_sefaz.pagamento", pagamentoDTO.getTabelaBanco());
        assertEquals("https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/pagamento", pagamentoDTO.getUrl());
        assertEquals("dt_lancamento_ob", pagamentoDTO.getNomeDataInicialPadraoFiltro());
        assertEquals("dt_lancamento_ob", pagamentoDTO.getNomeDataFinalPadraoFiltro());
        assertEquals("dt_ano_exercicio_ctb", pagamentoDTO.getDtAnoPadrao());
    }

    @Test
    @DisplayName("Deve mapear campos de resposta corretamente")
    void deveMaperarCamposResposta() {
        // Arrange
        pagamentoDTO.setDtAnoExercicioCTB(2025);
        pagamentoDTO.setCdUnidadeGestora("123456");
        pagamentoDTO.setSgUnidadeGestora("UG001");
        pagamentoDTO.setVlBrutoPD(new BigDecimal("1000.50"));
        pagamentoDTO.setDtLancamentoOB(LocalDate.of(2025, 1, 15));

        // Act
        pagamentoDTO.mapearCamposResposta();
        Map<String, Object> campos = pagamentoDTO.getCamposResposta();

        // Assert
        assertEquals(2025, campos.get("dt_ano_exercicio_ctb"));
        assertEquals("123456", campos.get("cd_unidade_gestora"));
        assertEquals("UG001", campos.get("sg_unidade_gestora"));
        assertEquals(new BigDecimal("1000.50"), campos.get("vl_bruto_pd"));
        assertEquals(LocalDate.of(2025, 1, 15), campos.get("dt_lancamento_ob"));
    }

    @Test
    @DisplayName("Deve obter parâmetros atuais corretamente")
    void deveObterParametrosAtuais() {
        // Arrange
        when(validacaoUtil.getAnoAtual()).thenReturn((short) 2025);
        when(validacaoUtil.getMesAtual()).thenReturn((short) 1);

        // Act
        Map<String, Object> parametros = pagamentoDTO.getCamposParametrosAtual("123456", validacaoUtil);

        // Assert
        assertEquals("123456", parametros.get("cdUnidadeGestora"));
        assertEquals((short) 2025, parametros.get("nuAnoLancamento"));
        assertEquals((short) 1, parametros.get("nuMesLancamento"));
    }

    @Test
    @DisplayName("Deve obter parâmetros para todos os anos corretamente")
    void deveObterParametrosTodosAnos() {
        // Act
        Map<String, Object> parametros = pagamentoDTO.getCamposParametrosTodosOsAnos("123456", (short) 2024);

        // Assert
        assertEquals("123456", parametros.get("cdUnidadeGestora"));
        assertEquals((short) 2024, parametros.get("nuAnoLancamento"));
    }

    @Test
    @DisplayName("Deve criar instância com construtor completo")
    void deveCriarInstanciaComConstrutorCompleto() {
        // Arrange & Act
        PagamentoDTO dto = new PagamentoDTO(
            2025, "123456", "001", "UG001", "ORG001", "SORG", "ORGSUP001", "SORGSUP",
            1L, 2L, 3L, "NAT001", "DOC123", "TIPO1", "Razão Social Teste",
            new BigDecimal("1000.00"), new BigDecimal("100.00"), new BigDecimal("900.00"),
            2025, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 20),
            "PAGO", "LIQUIDADO", 1, "Função Teste", 10, "SubFunção Teste",
            100, 200, 300, "Objeto Licitação", "PROC001", "Modalidade Teste"
        );

        // Assert
        assertEquals(2025, dto.getDtAnoExercicioCTB());
        assertEquals("123456", dto.getCdUnidadeGestora());
        assertEquals("UG001", dto.getSgUnidadeGestora());
        assertEquals(new BigDecimal("1000.00"), dto.getVlBrutoPD());
        assertEquals("PAGO", dto.getInSituacaoPagamento());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos corretamente")
    void deveDefinirEObterTodosCampos() {
        // Arrange & Act
        pagamentoDTO.setDtAnoExercicioCTB(2025);
        pagamentoDTO.setCdUnidadeGestora("123456");
        pagamentoDTO.setCdGestao("001");
        pagamentoDTO.setSgUnidadeGestora("UG001");
        pagamentoDTO.setIdOrgao("ORG001");
        pagamentoDTO.setSgOrgao("SORG");
        pagamentoDTO.setIdOrgaoSupervisor("ORGSUP001");
        pagamentoDTO.setSgOrgaoSupervisor("SORGSUP");
        pagamentoDTO.setSqPrevisaoDesembolso(1L);
        pagamentoDTO.setSqEmpenho(2L);
        pagamentoDTO.setSqOB(3L);
        pagamentoDTO.setCdNaturezaDespesaCompleta("NAT001");
        pagamentoDTO.setNuDocumento("DOC123");
        pagamentoDTO.setTpDocumento("TIPO1");
        pagamentoDTO.setNmRazaoSocialPessoa("Razão Social Teste");
        pagamentoDTO.setVlBrutoPD(new BigDecimal("1000.00"));
        pagamentoDTO.setVlRetidoPD(new BigDecimal("100.00"));
        pagamentoDTO.setVlOB(new BigDecimal("900.00"));
        pagamentoDTO.setDtAnoExercicioCTBReferencia(2025);
        pagamentoDTO.setDtPrevisaoDesembolso(LocalDate.of(2025, 1, 15));
        pagamentoDTO.setDtLancamentoOB(LocalDate.of(2025, 1, 20));
        pagamentoDTO.setInSituacaoPagamento("PAGO");
        pagamentoDTO.setSituacaoOB("LIQUIDADO");
        pagamentoDTO.setCdFuncao(1);
        pagamentoDTO.setNmFuncao("Função Teste");
        pagamentoDTO.setCdSubFuncao(10);
        pagamentoDTO.setNmSubFuncao("SubFunção Teste");
        pagamentoDTO.setCdElementoDespesa(100);
        pagamentoDTO.setCdFonteRecurso(200);
        pagamentoDTO.setCdLicitacao(300);
        pagamentoDTO.setDsObjetoLicitacao("Objeto Licitação");
        pagamentoDTO.setNuProcessoLicitacao("PROC001");
        pagamentoDTO.setNmModalidadeLicitacao("Modalidade Teste");

        // Assert
        assertEquals(2025, pagamentoDTO.getDtAnoExercicioCTB());
        assertEquals("123456", pagamentoDTO.getCdUnidadeGestora());
        assertEquals("001", pagamentoDTO.getCdGestao());
        assertEquals("UG001", pagamentoDTO.getSgUnidadeGestora());
        assertEquals("ORG001", pagamentoDTO.getIdOrgao());
        assertEquals("SORG", pagamentoDTO.getSgOrgao());
        assertEquals("ORGSUP001", pagamentoDTO.getIdOrgaoSupervisor());
        assertEquals("SORGSUP", pagamentoDTO.getSgOrgaoSupervisor());
        assertEquals(1L, pagamentoDTO.getSqPrevisaoDesembolso());
        assertEquals(2L, pagamentoDTO.getSqEmpenho());
        assertEquals(3L, pagamentoDTO.getSqOB());
        assertEquals("NAT001", pagamentoDTO.getCdNaturezaDespesaCompleta());
        assertEquals("DOC123", pagamentoDTO.getNuDocumento());
        assertEquals("TIPO1", pagamentoDTO.getTpDocumento());
        assertEquals("Razão Social Teste", pagamentoDTO.getNmRazaoSocialPessoa());
        assertEquals(new BigDecimal("1000.00"), pagamentoDTO.getVlBrutoPD());
        assertEquals(new BigDecimal("100.00"), pagamentoDTO.getVlRetidoPD());
        assertEquals(new BigDecimal("900.00"), pagamentoDTO.getVlOB());
        assertEquals(2025, pagamentoDTO.getDtAnoExercicioCTBReferencia());
        assertEquals(LocalDate.of(2025, 1, 15), pagamentoDTO.getDtPrevisaoDesembolso());
        assertEquals(LocalDate.of(2025, 1, 20), pagamentoDTO.getDtLancamentoOB());
        assertEquals("PAGO", pagamentoDTO.getInSituacaoPagamento());
        assertEquals("LIQUIDADO", pagamentoDTO.getSituacaoOB());
        assertEquals(1, pagamentoDTO.getCdFuncao());
        assertEquals("Função Teste", pagamentoDTO.getNmFuncao());
        assertEquals(10, pagamentoDTO.getCdSubFuncao());
        assertEquals("SubFunção Teste", pagamentoDTO.getNmSubFuncao());
        assertEquals(100, pagamentoDTO.getCdElementoDespesa());
        assertEquals(200, pagamentoDTO.getCdFonteRecurso());
        assertEquals(300, pagamentoDTO.getCdLicitacao());
        assertEquals("Objeto Licitação", pagamentoDTO.getDsObjetoLicitacao());
        assertEquals("PROC001", pagamentoDTO.getNuProcessoLicitacao());
        assertEquals("Modalidade Teste", pagamentoDTO.getNmModalidadeLicitacao());
    }

    @Test
    @DisplayName("Deve mapear parâmetros corretamente")
    void deveMaperarParametros() {
        // Arrange
        pagamentoDTO.setCdUnidadeGestora("123456");
        pagamentoDTO.setDtAnoExercicioCTB(2025);
        pagamentoDTO.setSqEmpenho(100L);
        pagamentoDTO.setSqOB(200L);
        pagamentoDTO.setNuDocumento("DOC123");

        // Act
        pagamentoDTO.mapearParametros();
        Map<String, Object> parametros = pagamentoDTO.getCamposParametros();

        // Assert
        assertEquals("123456", parametros.get("cdUnidadeGestora"));
        assertEquals(2025, parametros.get("dtAnoExercicioCTB"));
        assertEquals(100L, parametros.get("sqEmpenho"));
        assertEquals(200L, parametros.get("sqOB"));
        assertEquals("DOC123", parametros.get("nuDocumento"));
    }
}
