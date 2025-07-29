package br.gov.se.setc.consumer.dto;

import java.util.Map;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;

/**
 * Exemplo de como criar um novo endpoint DTO sem modificar ConsumoApiService.
 * Este DTO demonstra que a solução é verdadeiramente genérica.
 */
public class ExemploNovoEndpointDTO extends EndpontSefaz {

    // Campos específicos deste endpoint
    private String nomeExemplo;
    private String codigoExemplo;
    private String descricaoExemplo;
    private Integer valorExemplo;

    // Construtores
    public ExemploNovoEndpointDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    // Getters e Setters
    public String getNomeExemplo() {
        return nomeExemplo;
    }

    public void setNomeExemplo(String nomeExemplo) {
        this.nomeExemplo = nomeExemplo;
    }

    public String getCodigoExemplo() {
        return codigoExemplo;
    }

    public void setCodigoExemplo(String codigoExemplo) {
        this.codigoExemplo = codigoExemplo;
    }

    public String getDescricaoExemplo() {
        return descricaoExemplo;
    }

    public void setDescricaoExemplo(String descricaoExemplo) {
        this.descricaoExemplo = descricaoExemplo;
    }

    public Integer getValorExemplo() {
        return valorExemplo;
    }

    public void setValorExemplo(Integer valorExemplo) {
        this.valorExemplo = valorExemplo;
    }

    // Implementação dos métodos abstratos
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "sco.exemplo_endpoint";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/exemplo/v1/dados";
        nomeDataInicialPadraoFiltro = "dt_inicio";
        nomeDataFinalPadraoFiltro = "dt_fim";
        dtAnoPadrao = "dt_ano";
    }

    @Override
    public void mapearCamposResposta() {
        // O ConsumoApiService automaticamente mapeará os campos JSON para os setters
        // usando reflexão, e depois este método será chamado para finalizar o mapeamento
        camposResposta.put("nome_exemplo", nomeExemplo);
        camposResposta.put("codigo_exemplo", codigoExemplo);
        camposResposta.put("descricao_exemplo", descricaoExemplo);
        camposResposta.put("valor_exemplo", valorExemplo);
    }

    @Override
    protected void mapearParametros() {
        // Mapear parâmetros se necessário
        camposParametros.put("exemplo", "valor");
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        // Implementar lógica específica se necessário
        return null;
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        // Implementar lógica específica se necessário
        return null;
    }
}
