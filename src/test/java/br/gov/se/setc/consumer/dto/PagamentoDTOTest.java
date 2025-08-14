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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;

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

    @Test
    @DisplayName("Deve mapear campos corrigidos do response da API corretamente")
    void deveMaperarCamposCorrigidosDoResponse() {
        // Arrange - Simulando dados que vêm do response da API
        pagamentoDTO.setCdFuncao(18); // cdFuncaoPLO do response
        pagamentoDTO.setCdNaturezaDespesaCompleta("33903943"); // cdNaturezaDespesa do response
        pagamentoDTO.setDtAnoExercicioCTBReferencia(2025); // dtAnoExercicioCTB do response
        pagamentoDTO.setSqOB(155L); // sqOrdemBancaria do response

        // Act
        pagamentoDTO.mapearCamposResposta();
        Map<String, Object> campos = pagamentoDTO.getCamposResposta();

        // Assert - Verificando se os campos são mapeados para as colunas corretas do banco
        assertEquals(18, campos.get("cd_funcao"));
        assertEquals("33903943", campos.get("cd_natureza_despesa_completa"));
        assertEquals(2025, campos.get("dt_ano_exercicio_ctb_referencia"));
        assertEquals(155L, campos.get("sq_ob"));

        // Verificando que os campos não são mais null
        assertNotNull(campos.get("cd_funcao"));
        assertNotNull(campos.get("cd_natureza_despesa_completa"));
        assertNotNull(campos.get("dt_ano_exercicio_ctb_referencia"));
        assertNotNull(campos.get("sq_ob"));
    }

    @Test
    @DisplayName("Deve mapear JSON real da API corretamente usando Jackson")
    void deveMaperarJsonRealDaApi() throws Exception {
        // Arrange - JSON real da API conforme fornecido pelo usuário
        String jsonResponse = """
            {
                "nmModalidadeLicitacao": "INEXIGÍVEL",
                "dtPrevisaoDesembolso": "2025-02-24",
                "situacaoOB": "D",
                "nmSubFuncao": "Preservação e Conservação Ambiental",
                "nmFuncao": "Gestão Ambiental",
                "CdSubFuncao": 541,
                "nmRazaoSocialPessoa": "COMPANHIA SUL SERGIPANA DE ELETRICIDADE",
                "cdUnidadeGestora": "351011",
                "idOrgao": 35000,
                "dtAnoExercicioCTB": 2025,
                "dtLancamentoOB": "2025-02-24",
                "cdGestao": "00001",
                "sgOrgao": "SEMAC",
                "nuProcessoLicitacao": "1248/2020-COMPRAS.GOV-SEA",
                "cdFuncaoPLO": 18,
                "nuDocumento": "13255658000196",
                "tpDocumento": 2,
                "dsObjetoLicitacao": "CONTRATAÇÃO DE EMPRESA PARA FORNECIMENTO DE ENERGIA ELÉTRICA",
                "vlBrutoPD": 401.96,
                "vlRetidoPD": 0,
                "cdElementoDespesa": 39,
                "cdFonteRecurso": "1799000000",
                "cdNaturezaDespesa": "33903943",
                "sqEmpenho": 21,
                "cdLicitacao": "1510412020000103",
                "sgUnidadeGestora": "SEMAC",
                "sqOrdemBancaria": 155,
                "sqPrevisaoDesembolso": 155,
                "vlOB": 401.96,
                "inSituacaoPagamento": "L"
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        // Act - Simular o mapeamento que acontece no ConsumoApiService
        PagamentoDTO dto = new PagamentoDTO();

        // Mapear campos manualmente como faria o Jackson
        if (jsonNode.has("cdFuncaoPLO")) {
            dto.setCdFuncao(jsonNode.get("cdFuncaoPLO").asInt());
        }
        if (jsonNode.has("cdNaturezaDespesa")) {
            dto.setCdNaturezaDespesaCompleta(jsonNode.get("cdNaturezaDespesa").asText());
        }
        if (jsonNode.has("dtAnoExercicioCTB")) {
            dto.setDtAnoExercicioCTBReferencia(jsonNode.get("dtAnoExercicioCTB").asInt());
        }
        if (jsonNode.has("sqOrdemBancaria")) {
            dto.setSqOB(jsonNode.get("sqOrdemBancaria").asLong());
        }

        // Mapear campos de resposta
        dto.mapearCamposResposta();
        Map<String, Object> campos = dto.getCamposResposta();

        // Assert - Verificar se os campos problemáticos agora são mapeados corretamente
        assertEquals(18, campos.get("cd_funcao"), "Campo cd_funcao deve ser mapeado do cdFuncaoPLO");
        assertEquals("33903943", campos.get("cd_natureza_despesa_completa"), "Campo cd_natureza_despesa_completa deve ser mapeado do cdNaturezaDespesa");
        assertEquals(2025, campos.get("dt_ano_exercicio_ctb_referencia"), "Campo dt_ano_exercicio_ctb_referencia deve ser mapeado do dtAnoExercicioCTB");
        assertEquals(155L, campos.get("sq_ob"), "Campo sq_ob deve ser mapeado do sqOrdemBancaria");

        // Verificar que os campos não são null
        assertNotNull(campos.get("cd_funcao"), "cd_funcao não deve ser null");
        assertNotNull(campos.get("cd_natureza_despesa_completa"), "cd_natureza_despesa_completa não deve ser null");
        assertNotNull(campos.get("dt_ano_exercicio_ctb_referencia"), "dt_ano_exercicio_ctb_referencia não deve ser null");
        assertNotNull(campos.get("sq_ob"), "sq_ob não deve ser null");
    }

    @Test
    @DisplayName("Deve mapear campos usando reflexão como no ConsumoApiService")
    void deveMaperarCamposUsandoReflexao() throws Exception {
        // Arrange - JSON real da API
        String jsonResponse = """
            {
                "cdFuncaoPLO": 18,
                "cdNaturezaDespesa": "33903943",
                "dtAnoExercicioCTB": 2025,
                "sqOrdemBancaria": 155
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        PagamentoDTO dto = new PagamentoDTO();

        // Act - Simular o processo de reflexão do ConsumoApiService
        jsonNode.fieldNames().forEachRemaining(fieldName -> {
            try {
                JsonNode fieldValue = jsonNode.get(fieldName);
                if (fieldValue != null && !fieldValue.isNull()) {
                    // Primeiro, tentar encontrar um campo com @JsonProperty que corresponda ao fieldName
                    String targetSetterName = findSetterNameByJsonProperty(PagamentoDTO.class, fieldName);

                    // Tentar diferentes convenções de nomes de setter
                    String[] possibleSetterNames;
                    if (targetSetterName != null) {
                        possibleSetterNames = new String[]{
                            targetSetterName,
                            "set" + capitalize(fieldName),
                            "set" + fieldName
                        };
                    } else {
                        possibleSetterNames = new String[]{
                            "set" + capitalize(fieldName),
                            "set" + fieldName
                        };
                    }

                    boolean mapped = false;
                    for (String setterName : possibleSetterNames) {
                        if (tryInvokeSetterWithValue(dto, setterName, fieldValue)) {
                            System.out.println("✅ Mapeado: " + fieldName + " -> " + setterName);
                            mapped = true;
                            break;
                        }
                    }

                    if (!mapped) {
                        System.out.println("❌ Não mapeado: " + fieldName);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao mapear " + fieldName + ": " + e.getMessage());
            }
        });

        // Mapear campos de resposta
        dto.mapearCamposResposta();
        Map<String, Object> campos = dto.getCamposResposta();

        // Assert - Verificar se os campos foram mapeados
        assertEquals(18, campos.get("cd_funcao"), "cd_funcao deve ser 18");
        assertEquals("33903943", campos.get("cd_natureza_despesa_completa"), "cd_natureza_despesa_completa deve ser '33903943'");
        assertEquals(2025, campos.get("dt_ano_exercicio_ctb_referencia"), "dt_ano_exercicio_ctb_referencia deve ser 2025");
        assertEquals(155L, campos.get("sq_ob"), "sq_ob deve ser 155");
    }

    private String findSetterNameByJsonProperty(Class<?> dtoClass, String fieldName) {
        try {
            Field[] fields = dtoClass.getDeclaredFields();
            for (Field field : fields) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && jsonProperty.value().equals(fieldName)) {
                    // Found a field with matching @JsonProperty annotation
                    String fieldNameInClass = field.getName();
                    return "set" + capitalize(fieldNameInClass);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar campo com @JsonProperty: " + e.getMessage());
        }
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private boolean tryInvokeSetterWithValue(PagamentoDTO dto, String setterName, JsonNode fieldValue) {
        try {
            // Try Integer parameter
            try {
                var method = dto.getClass().getMethod(setterName, Integer.class);
                method.invoke(dto, fieldValue.asInt());
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try String parameter
            try {
                var method = dto.getClass().getMethod(setterName, String.class);
                method.invoke(dto, fieldValue.asText());
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try Long parameter
            try {
                var method = dto.getClass().getMethod(setterName, Long.class);
                method.invoke(dto, fieldValue.asLong());
                return true;
            } catch (NoSuchMethodException ignored) {}

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
