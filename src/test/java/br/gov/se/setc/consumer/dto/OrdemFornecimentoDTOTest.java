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

class OrdemFornecimentoDTOTest {

    private OrdemFornecimentoDTO ordemFornecimentoDTO;
    
    @Mock
    private ValidacaoUtil<?> validacaoUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordemFornecimentoDTO = new OrdemFornecimentoDTO();
    }

    @Test
    @DisplayName("Deve inicializar dados do endpoint corretamente")
    void deveInicializarDadosEndpoint() {
        // Assert
        assertEquals("consumer_sefaz.ordem_fornecimento", ordemFornecimentoDTO.getTabelaBanco());
        assertEquals("https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/ordem-fornecimento", ordemFornecimentoDTO.getUrl());
        assertEquals("dt_recebimento", ordemFornecimentoDTO.getNomeDataInicialPadraoFiltro());
        assertEquals("dt_recebimento", ordemFornecimentoDTO.getNomeDataFinalPadraoFiltro());
        assertEquals("dt_ano_exercicio_emp", ordemFornecimentoDTO.getDtAnoPadrao());
    }

    @Test
    @DisplayName("Deve mapear campos de resposta corretamente")
    void deveMapeiarCamposResposta() {
        // Arrange
        ordemFornecimentoDTO.setCdUnidadeGestora("001");
        ordemFornecimentoDTO.setDtAnoExercicioEmp(2024);
        ordemFornecimentoDTO.setSqEmpenho(123L);
        ordemFornecimentoDTO.setVlOrdemFornecimento(new BigDecimal("1000.50"));
        ordemFornecimentoDTO.setDtRecebimento(LocalDate.of(2024, 1, 15));
        
        // Act
        ordemFornecimentoDTO.mapearCamposResposta();
        Map<String, Object> campos = ordemFornecimentoDTO.getCamposResposta();
        
        // Assert
        assertEquals("001", campos.get("cd_unidade_gestora"));
        assertEquals(2024, campos.get("dt_ano_exercicio_emp"));
        assertEquals(123L, campos.get("sq_empenho"));
        assertEquals(new BigDecimal("1000.50"), campos.get("vl_ordem_fornecimento"));
        assertEquals(LocalDate.of(2024, 1, 15), campos.get("dt_recebimento"));
    }

    @Test
    @DisplayName("Deve retornar parâmetros para todos os anos corretamente")
    void deveRetornarParametrosTodosOsAnos() {
        // Arrange
        String ugCd = "001";
        Short ano = 2024;
        
        // Act
        Map<String, Object> parametros = ordemFornecimentoDTO.getCamposParametrosTodosOsAnos(ugCd, ano);
        
        // Assert
        assertNotNull(parametros);
        assertEquals("001", parametros.get("cdUnidadeGestora"));
        assertEquals((short) 2024, parametros.get("dtAnoExercicioEmp"));
        assertEquals(2, parametros.size());
    }

    @Test
    @DisplayName("Deve retornar parâmetros atuais corretamente")
    void deveRetornarParametrosAtuais() {
        // Arrange
        String ugCd = "001";
        when(validacaoUtil.getAnoAtual()).thenReturn((short) 2024);
        when(validacaoUtil.getMesAtual()).thenReturn((short) 8);
        
        // Act
        Map<String, Object> parametros = ordemFornecimentoDTO.getCamposParametrosAtual(ugCd, validacaoUtil);
        
        // Assert
        assertNotNull(parametros);
        assertEquals("001", parametros.get("cdUnidadeGestora"));
        assertEquals((short) 2024, parametros.get("dtAnoExercicioEmp"));
        assertEquals((short) 8, parametros.get("nuMesRecebimento"));
        assertEquals(3, parametros.size());
    }

    @Test
    @DisplayName("Deve definir e recuperar campos corretamente")
    void deveDefinirERecuperarCampos() {
        // Arrange & Act
        ordemFornecimentoDTO.setCdUnidadeGestora("001");
        ordemFornecimentoDTO.setCdGestao("02");
        ordemFornecimentoDTO.setDtAnoExercicioEmp(2024);
        ordemFornecimentoDTO.setSqEmpenho(456L);
        ordemFornecimentoDTO.setSqOrdemFornecimento(789L);
        ordemFornecimentoDTO.setVlOrdemFornecimento(new BigDecimal("2500.75"));
        ordemFornecimentoDTO.setDtRecebimento(LocalDate.of(2024, 8, 5));
        ordemFornecimentoDTO.setNuNfe("123456789");
        ordemFornecimentoDTO.setNmDestinatario("Empresa Teste");
        ordemFornecimentoDTO.setVlTotalICMS(new BigDecimal("180.00"));
        
        // Assert
        assertEquals("001", ordemFornecimentoDTO.getCdUnidadeGestora());
        assertEquals("02", ordemFornecimentoDTO.getCdGestao());
        assertEquals(2024, ordemFornecimentoDTO.getDtAnoExercicioEmp());
        assertEquals(456L, ordemFornecimentoDTO.getSqEmpenho());
        assertEquals(789L, ordemFornecimentoDTO.getSqOrdemFornecimento());
        assertEquals(new BigDecimal("2500.75"), ordemFornecimentoDTO.getVlOrdemFornecimento());
        assertEquals(LocalDate.of(2024, 8, 5), ordemFornecimentoDTO.getDtRecebimento());
        assertEquals("123456789", ordemFornecimentoDTO.getNuNfe());
        assertEquals("Empresa Teste", ordemFornecimentoDTO.getNmDestinatario());
        assertEquals(new BigDecimal("180.00"), ordemFornecimentoDTO.getVlTotalICMS());
    }

    @Test
    @DisplayName("Deve mapear todos os campos monetários corretamente")
    void deveMapeiarCamposMonetarios() {
        // Arrange
        BigDecimal valor = new BigDecimal("1234.56");
        
        // Act
        ordemFornecimentoDTO.setVlOrdemFornecimento(valor);
        ordemFornecimentoDTO.setVlTotalProdServ(valor);
        ordemFornecimentoDTO.setVlTotalFrete(valor);
        ordemFornecimentoDTO.setVlPis(valor);
        ordemFornecimentoDTO.setVlCofins(valor);
        ordemFornecimentoDTO.setVlIss(valor);
        ordemFornecimentoDTO.setVlInss(valor);
        ordemFornecimentoDTO.setVlRetIRRF(valor);
        
        // Assert
        assertEquals(valor, ordemFornecimentoDTO.getVlOrdemFornecimento());
        assertEquals(valor, ordemFornecimentoDTO.getVlTotalProdServ());
        assertEquals(valor, ordemFornecimentoDTO.getVlTotalFrete());
        assertEquals(valor, ordemFornecimentoDTO.getVlPis());
        assertEquals(valor, ordemFornecimentoDTO.getVlCofins());
        assertEquals(valor, ordemFornecimentoDTO.getVlIss());
        assertEquals(valor, ordemFornecimentoDTO.getVlInss());
        assertEquals(valor, ordemFornecimentoDTO.getVlRetIRRF());
    }

    @Test
    @DisplayName("Deve mapear campos de data corretamente")
    void deveMapeiarCamposData() {
        // Arrange
        LocalDate dataRecebimento = LocalDate.of(2024, 8, 5);
        LocalDate dataEmissao = LocalDate.of(2024, 8, 1);
        LocalDate dataServico = LocalDate.of(2024, 8, 3);
        
        // Act
        ordemFornecimentoDTO.setDtRecebimento(dataRecebimento);
        ordemFornecimentoDTO.setDtEmissao(dataEmissao);
        ordemFornecimentoDTO.setDtServico(dataServico);
        
        // Assert
        assertEquals(dataRecebimento, ordemFornecimentoDTO.getDtRecebimento());
        assertEquals(dataEmissao, ordemFornecimentoDTO.getDtEmissao());
        assertEquals(dataServico, ordemFornecimentoDTO.getDtServico());
    }
}
