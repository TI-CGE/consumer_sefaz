package br.gov.se.setc.consumer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste para verificar se o mapeamento JSON do LiquidacaoDTO está funcionando corretamente
 * para os campos dtLiquidacao, vlBrutoLiquidacao e vlEstornadoLiquidacao
 */
public class LiquidacaoDTOMappingTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testMapeamentoJsonParaLiquidacaoDTO() throws Exception {
        // JSON simulando a resposta da API de transparência
        String jsonResponse = """
            {
                "sqEmpenho": 123456,
                "sqLiquidacao": 789012,
                "dtLiquidacao": "2020-01-29",
                "cdUnidadeGestora": "161011",
                "sgUnidadeGestora": "SETC",
                "vlBrutoLiquidacao": 170,
                "vlEstornadoLiquidacao": 0,
                "dtAnoExercicioCTB": 2020,
                "cdGestao": "00001",
                "nmRazaoSocialPessoa": "FORNECEDOR TESTE LTDA"
            }
            """;

        // Deserializar JSON para LiquidacaoDTO
        LiquidacaoDTO liquidacao = objectMapper.readValue(jsonResponse, LiquidacaoDTO.class);

        // Verificar se os campos problemáticos foram mapeados corretamente
        assertNotNull(liquidacao.getDtLiquidacao(), "dtLiquidacao não deve ser null");
        assertEquals(LocalDate.of(2020, 1, 29), liquidacao.getDtLiquidacao(), 
                    "dtLiquidacao deve ser 2020-01-29");

        assertNotNull(liquidacao.getVlBrutoLiquidacao(), "vlBrutoLiquidacao não deve ser null");
        assertEquals(new BigDecimal("170"), liquidacao.getVlBrutoLiquidacao(), 
                    "vlBrutoLiquidacao deve ser 170");

        assertNotNull(liquidacao.getVlEstornadoLiquidacao(), "vlEstornadoLiquidacao não deve ser null");
        assertEquals(new BigDecimal("0"), liquidacao.getVlEstornadoLiquidacao(), 
                    "vlEstornadoLiquidacao deve ser 0");

        // Verificar outros campos para garantir que o mapeamento geral está funcionando
        assertEquals(Long.valueOf(123456), liquidacao.getSqEmpenho());
        assertEquals(Long.valueOf(789012), liquidacao.getSqLiquidacao());
        assertEquals("161011", liquidacao.getCdUnidadeGestora());
        assertEquals("SETC", liquidacao.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2020), liquidacao.getDtAnoExercicioCTB());
        assertEquals("00001", liquidacao.getCdGestao());
        assertEquals("FORNECEDOR TESTE LTDA", liquidacao.getNmRazaoSocialPessoa());
    }

    @Test
    void testMapeamentoCamposResposta() {
        // Criar uma instância do DTO
        LiquidacaoDTO liquidacao = new LiquidacaoDTO();
        
        // Definir valores nos campos
        liquidacao.setDtLiquidacao(LocalDate.of(2020, 1, 29));
        liquidacao.setVlBrutoLiquidacao(new BigDecimal("170"));
        liquidacao.setVlEstornadoLiquidacao(new BigDecimal("0"));
        liquidacao.setSqEmpenho(123456L);
        liquidacao.setSqLiquidacao(789012L);
        liquidacao.setCdUnidadeGestora("161011");

        // Chamar mapearCamposResposta para popular o mapa
        liquidacao.mapearCamposResposta();

        // Verificar se os campos foram mapeados corretamente no mapa camposResposta
        assertEquals(LocalDate.of(2020, 1, 29), liquidacao.getCamposResposta().get("dt_liquidacao"));
        assertEquals(new BigDecimal("170"), liquidacao.getCamposResposta().get("vl_bruto_liquidacao"));
        assertEquals(new BigDecimal("0"), liquidacao.getCamposResposta().get("vl_estornado_liquidacao"));
        assertEquals(123456L, liquidacao.getCamposResposta().get("sq_empenho"));
        assertEquals(789012L, liquidacao.getCamposResposta().get("sq_liquidacao"));
        assertEquals("161011", liquidacao.getCamposResposta().get("cd_unidade_gestora"));
    }

    @Test
    void testValoresNulosNaoQuebramMapeamento() throws Exception {
        // JSON com alguns campos nulos
        String jsonResponse = """
            {
                "sqEmpenho": 123456,
                "sqLiquidacao": 789012,
                "dtLiquidacao": null,
                "cdUnidadeGestora": "161011",
                "vlBrutoLiquidacao": null,
                "vlEstornadoLiquidacao": null
            }
            """;

        // Deserializar JSON para LiquidacaoDTO
        LiquidacaoDTO liquidacao = objectMapper.readValue(jsonResponse, LiquidacaoDTO.class);

        // Verificar se campos nulos são tratados corretamente
        assertNull(liquidacao.getDtLiquidacao());
        assertNull(liquidacao.getVlBrutoLiquidacao());
        assertNull(liquidacao.getVlEstornadoLiquidacao());

        // Campos não nulos devem estar corretos
        assertEquals(Long.valueOf(123456), liquidacao.getSqEmpenho());
        assertEquals(Long.valueOf(789012), liquidacao.getSqLiquidacao());
        assertEquals("161011", liquidacao.getCdUnidadeGestora());
    }
}
