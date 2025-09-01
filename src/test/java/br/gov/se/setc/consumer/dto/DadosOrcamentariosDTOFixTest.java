package br.gov.se.setc.consumer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste para verificar se o mapeamento dos campos cd_funcao_plo e nm_funcao_plo
 * está funcionando corretamente após a correção
 */
public class DadosOrcamentariosDTOFixTest {

    @Test
    void testMapeamentoCorrigidoFuncaoPLO() throws Exception {
        // Simular a resposta real da API SEFAZ
        String jsonResponse = """
            {
                "nuProcessoLicitacao": "292/2023",
                "cdProgramaGoverno": 36,
                "nmModalidadeAplicacao": "Aplicações Diretas",
                "cdSubFuncao": 122,
                "cdCategoriaEconomica": 3,
                "cdFuncao": 20,
                "dsObjetoLicitacao": "REGISTRO DE PREÇOS PARA FUTURA E EVENTUAL AQUISIÇÃO DE MATERIAIS",
                "nmCategoriaEconomica": "Despesas Correntes",
                "nmProgramaGoverno": "Coordenação e Manutenção do Poder Executivo",
                "nmGrupoDespesa": "Outras Despesas Correntes",
                "cdModalidadeAplicacao": 90,
                "cdElementoDespesa": 30,
                "nmSubFuncao": "Administração Geral",
                "nmFuncao": "Agricultura",
                "cdFonteRecurso": "1500000000",
                "nmFonteRecurso": "Recursos não Vinculados de Impostos",
                "cdGrupoDespesa": 3,
                "dsTipoDespesa": "NORMAL",
                "cdLicitacao": "1730212024000054",
                "cdTipoDespesa": 1,
                "nmElementoDespesa": "Material de Consumo"
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        DadosOrcamentariosDTO dados = objectMapper.readValue(jsonResponse, DadosOrcamentariosDTO.class);

        // Verificar se os campos que estavam vindo null agora estão sendo mapeados corretamente
        assertNotNull(dados.getCdFuncaoPLO(), "cd_funcao_plo não deve ser null");
        assertNotNull(dados.getNmFuncaoPLO(), "nm_funcao_plo não deve ser null");
        
        assertEquals(Integer.valueOf(20), dados.getCdFuncaoPLO(), "cdFuncao deve mapear para cdFuncaoPLO");
        assertEquals("Agricultura", dados.getNmFuncaoPLO(), "nmFuncao deve mapear para nmFuncaoPLO");
        
        // Verificar se cdFonteRecurso agora é tratado como String
        assertNotNull(dados.getCdFonteRecurso(), "cd_fonte_recurso não deve ser null");
        assertEquals("1500000000", dados.getCdFonteRecurso(), "cdFonteRecurso deve ser String");
        
        // Verificar outros campos importantes
        assertEquals("292/2023", dados.getNuProcessoLicitacao());
        assertEquals(Integer.valueOf(36), dados.getCdProgramaGoverno());
        assertEquals("Aplicações Diretas", dados.getNmModalidadeAplicacao());
        assertEquals(Integer.valueOf(122), dados.getCdSubFuncao());
        assertEquals("Administração Geral", dados.getNmSubFuncao());
        assertEquals("Recursos não Vinculados de Impostos", dados.getNmFonteRecurso());
    }

    @Test
    void testMapeamentoCamposResposta() {
        DadosOrcamentariosDTO dados = new DadosOrcamentariosDTO();
        
        // Verificar se o mapeamento dos campos de resposta está correto
        assertTrue(dados.getCamposResposta().containsKey("cd_funcao_plo"), 
                  "Deve conter mapeamento para cd_funcao_plo");
        assertTrue(dados.getCamposResposta().containsKey("nm_funcao_plo"), 
                  "Deve conter mapeamento para nm_funcao_plo");
        assertTrue(dados.getCamposResposta().containsKey("cd_fonte_recurso"), 
                  "Deve conter mapeamento para cd_fonte_recurso");
    }

    @Test
    void testValoresNullSafeHandling() throws Exception {
        // Testar com alguns campos null para garantir que não quebra
        String jsonResponse = """
            {
                "cdFuncao": null,
                "nmFuncao": null,
                "cdFonteRecurso": null,
                "nuProcessoLicitacao": "292/2023"
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        DadosOrcamentariosDTO dados = objectMapper.readValue(jsonResponse, DadosOrcamentariosDTO.class);

        // Verificar se campos null são tratados corretamente
        assertNull(dados.getCdFuncaoPLO(), "cdFuncaoPLO deve ser null quando API retorna null");
        assertNull(dados.getNmFuncaoPLO(), "nmFuncaoPLO deve ser null quando API retorna null");
        assertNull(dados.getCdFonteRecurso(), "cdFonteRecurso deve ser null quando API retorna null");
        
        // Campo que tem valor deve estar presente
        assertEquals("292/2023", dados.getNuProcessoLicitacao());
    }
}
