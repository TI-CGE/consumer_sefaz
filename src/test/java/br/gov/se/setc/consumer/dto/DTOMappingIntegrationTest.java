package br.gov.se.setc.consumer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para verificar se todos os DTOs corrigidos estão mapeando
 * corretamente os campos BigDecimal e LocalDate das APIs da SEFAZ
 */
public class DTOMappingIntegrationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testPagamentoDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "dtAnoExercicioCTB": 2020,
                "cdUnidadeGestora": "161011",
                "sqEmpenho": 123456,
                "vlBrutoPD": 1500.50,
                "vlRetidoPD": 150.05,
                "vlOB": 1350.45,
                "dtPrevisaoDesembolso": "2020-01-29",
                "dtLancamentoOB": "2020-01-30",
                "nmRazaoSocialPessoa": "FORNECEDOR TESTE LTDA"
            }
            """;

        PagamentoDTO pagamento = objectMapper.readValue(jsonResponse, PagamentoDTO.class);

        assertNotNull(pagamento.getDtPrevisaoDesembolso(), "dtPrevisaoDesembolso não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 29), pagamento.getDtPrevisaoDesembolso());

        assertNotNull(pagamento.getDtLancamentoOB(), "dtLancamentoOB não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 30), pagamento.getDtLancamentoOB());

        assertNotNull(pagamento.getVlBrutoPD(), "vlBrutoPD não deve ser null");
        assertEquals(new BigDecimal("1500.50"), pagamento.getVlBrutoPD());

        assertNotNull(pagamento.getVlRetidoPD(), "vlRetidoPD não deve ser null");
        assertEquals(new BigDecimal("150.05"), pagamento.getVlRetidoPD());

        assertNotNull(pagamento.getVlOB(), "vlOB não deve ser null");
        assertEquals(new BigDecimal("1350.45"), pagamento.getVlOB());

        assertEquals(Integer.valueOf(2020), pagamento.getDtAnoExercicioCTB());
        assertEquals("161011", pagamento.getCdUnidadeGestora());
        assertEquals(Long.valueOf(123456), pagamento.getSqEmpenho());
        assertEquals("FORNECEDOR TESTE LTDA", pagamento.getNmRazaoSocialPessoa());
    }

    @Test
    void testContratosFiscaisDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "dtFimVigenciaContrato": "2020-12-31",
                "dtInicioVigenciaContrato": "2020-01-01",
                "cdUnidadeGestora": "161011",
                "sgUnidadeGestora": "SETC",
                "cdContrato": "CT001",
                "nmContratado": "EMPRESA TESTE LTDA",
                "dtAnoExercicio": 2020
            }
            """;

        ContratosFiscaisDTO contrato = objectMapper.readValue(jsonResponse, ContratosFiscaisDTO.class);

        assertNotNull(contrato.getDtFimVigenciaContrato(), "dtFimVigenciaContrato não deve ser null");
        assertEquals(LocalDate.of(2020, 12, 31), contrato.getDtFimVigenciaContrato());

        assertNotNull(contrato.getDtInicioVigenciaContrato(), "dtInicioVigenciaContrato não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 1), contrato.getDtInicioVigenciaContrato());

        assertEquals("161011", contrato.getCdUnidadeGestora());
        assertEquals("SETC", contrato.getSgUnidadeGestora());
        assertEquals("CT001", contrato.getCdContrato());
        assertEquals("EMPRESA TESTE LTDA", contrato.getNmContratado());
        assertEquals(Integer.valueOf(2020), contrato.getDtAnoExercicio());
    }

    @Test
    void testOrdemFornecimentoDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "cdUnidadeGestora": "161011",
                "sqEmpenho": 123456,
                "vlOrdemFornecimento": 2500.75,
                "dtEmissao": "2020-01-29",
                "dtRecebimento": "2020-02-01",
                "vlTotalProdServ": 2000.00,
                "vlTotalICMS": 360.00,
                "vlPis": 32.50,
                "vlCofins": 150.00,
                "nmDestinatario": "ORGAO PUBLICO"
            }
            """;

        OrdemFornecimentoDTO ordem = objectMapper.readValue(jsonResponse, OrdemFornecimentoDTO.class);

        assertNotNull(ordem.getDtEmissao(), "dtEmissao não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 29), ordem.getDtEmissao());

        assertNotNull(ordem.getDtRecebimento(), "dtRecebimento não deve ser null");
        assertEquals(LocalDate.of(2020, 2, 1), ordem.getDtRecebimento());

        assertNotNull(ordem.getVlOrdemFornecimento(), "vlOrdemFornecimento não deve ser null");
        assertEquals(new BigDecimal("2500.75"), ordem.getVlOrdemFornecimento());

        assertNotNull(ordem.getVlTotalProdServ(), "vlTotalProdServ não deve ser null");
        assertEquals(new BigDecimal("2000.00"), ordem.getVlTotalProdServ());

        assertNotNull(ordem.getVlTotalICMS(), "vlTotalICMS não deve ser null");
        assertEquals(new BigDecimal("360.00"), ordem.getVlTotalICMS());

        assertEquals("161011", ordem.getCdUnidadeGestora());
        assertEquals(Long.valueOf(123456), ordem.getSqEmpenho());
        assertEquals("ORGAO PUBLICO", ordem.getNmDestinatario());
    }

    @Test
    void testReceitaDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "cdConvenio": 12345,
                "cdUnidadeGestora": "161011",
                "vlConcedenteConvenio": 50000.00,
                "vlContrapartidaConvenio": 10000.00,
                "dtCelebracaoConvenio": "2020-01-15",
                "dtInicioVigenciaConvenio": "2020-02-01",
                "dtFimVigenciaConvenio": "2020-12-31",
                "dtPublicacaoConvenio": "2020-01-20",
                "nmConvenio": "CONVENIO TESTE",
                "nmConcedente": "GOVERNO FEDERAL"
            }
            """;

        ReceitaDTO receita = objectMapper.readValue(jsonResponse, ReceitaDTO.class);

        assertNotNull(receita.getDtCelebracaoConvenio(), "dtCelebracaoConvenio não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 15), receita.getDtCelebracaoConvenio());

        assertNotNull(receita.getDtInicioVigenciaConvenio(), "dtInicioVigenciaConvenio não deve ser null");
        assertEquals(LocalDate.of(2020, 2, 1), receita.getDtInicioVigenciaConvenio());

        assertNotNull(receita.getDtFimVigenciaConvenio(), "dtFimVigenciaConvenio não deve ser null");
        assertEquals(LocalDate.of(2020, 12, 31), receita.getDtFimVigenciaConvenio());

        assertNotNull(receita.getVlConcedenteConvenio(), "vlConcedenteConvenio não deve ser null");
        assertEquals(new BigDecimal("50000.00"), receita.getVlConcedenteConvenio());

        assertNotNull(receita.getVlContrapartidaConvenio(), "vlContrapartidaConvenio não deve ser null");
        assertEquals(new BigDecimal("10000.00"), receita.getVlContrapartidaConvenio());

        assertEquals(Integer.valueOf(12345), receita.getCdConvenio());
        assertEquals("161011", receita.getCdUnidadeGestora());
        assertEquals("CONVENIO TESTE", receita.getNmConvenio());
        assertEquals("GOVERNO FEDERAL", receita.getNmConcedente());
    }

    @Test
    void testDadosOrcamentariosDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "cdFuncaoPLO": 10,
                "nmFuncaoPLO": "SAUDE",
                "cdSubFuncao": 301,
                "nmSubFuncao": "ATENCAO BASICA",
                "cdProgramaGoverno": 2015,
                "nmProgramaGoverno": "FORTALECIMENTO DO SUS",
                "dtAnoExercicioCTB": 2020,
                "cdUnidadeGestora": "161011"
            }
            """;

        DadosOrcamentariosDTO dados = objectMapper.readValue(jsonResponse, DadosOrcamentariosDTO.class);

        assertEquals(Integer.valueOf(10), dados.getCdFuncaoPLO());
        assertEquals("SAUDE", dados.getNmFuncaoPLO());
        assertEquals(Integer.valueOf(301), dados.getCdSubFuncao());
        assertEquals("ATENCAO BASICA", dados.getNmSubFuncao());
        assertEquals(Integer.valueOf(2015), dados.getCdProgramaGoverno());
        assertEquals("FORTALECIMENTO DO SUS", dados.getNmProgramaGoverno());
        assertEquals(Integer.valueOf(2020), dados.getDtAnoExercicioCTB());
        assertEquals("161011", dados.getCdUnidadeGestora());
    }

    @Test
    void testUnidadeGestoraDTOMapeamento() throws Exception {
        String jsonResponse = """
            {
                "nmUnidadeGestora": "SECRETARIA DE ESTADO DA FAZENDA",
                "sgUnidadeGestora": "SEFAZ",
                "cdUnidadeGestora": "161011",
                "sgTipoUnidadeGestora": "E"
            }
            """;

        UnidadeGestoraDTO unidade = objectMapper.readValue(jsonResponse, UnidadeGestoraDTO.class);

        assertEquals("SECRETARIA DE ESTADO DA FAZENDA", unidade.getNmUnidadeGestora());
        assertEquals("SEFAZ", unidade.getSgUnidadeGestora());
        assertEquals("161011", unidade.getCdUnidadeGestora());
        assertEquals("E", unidade.getSgTipoUnidadeGestora());
    }
}
