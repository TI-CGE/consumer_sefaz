package br.gov.se.setc.consumer.contracts;

import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.se.setc.util.ValidacaoUtil;

public abstract class EndpontSefaz {
    // Instance fields instead of static - each subclass will have its own values
    protected String tabelaBanco;
    protected String url;
    protected String nomeDataInicialPadraoFiltro;
    protected String nomeDataFinalPadraoFiltro;
    protected String dtAnoPadrao;
    protected Boolean parametrosRequeridos;

    protected Map<String, Object> camposParametros = new LinkedHashMap<>();
    protected Map<String, Object> camposResposta = new LinkedHashMap<>();

    // Getters for instance fields
    public String getTabelaBanco() {
        return tabelaBanco;
    }

    public String getUrl() {
        return url;
    }

    public String getNomeDataInicialPadraoFiltro() {
        return nomeDataInicialPadraoFiltro;
    }

    public String getNomeDataFinalPadraoFiltro() {
        return nomeDataFinalPadraoFiltro;
    }

    public String getDtAnoPadrao() {
        return dtAnoPadrao;
    }

    public Map<String, Object> getCamposResposta(){
        return camposResposta;
    }

    public Map<String, Object> getCamposParametros() {
        return camposParametros;
    }

    public void setCamposParametros(Map<String, Object> camposParametros) {
        this.camposParametros = camposParametros;
    }

    public void setCamposResposta(Map<String, Object> camposResposta) {
        this.camposResposta = camposResposta;
    }


    /**
     * @param ugCd   unidade gestora code
     * @param ano    year to query
     * @return map of query parameters for this DTO and year
     */
    public abstract Map<String,Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano);
    /**
     * @param ugCd   unidade gestora code
     * @param utilsService   utility objects for consults and validation 
     * @return map of query parameters for this DTO and year
     */
     public abstract Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService);

     /**
      * Initialize the instance fields with endpoint-specific values
      * This method should set tabelaBanco, url, nomeDataInicialPadraoFiltro, etc.
      */
     protected abstract void inicializarDadosEndpoint();

     public abstract void mapearCamposResposta();

     protected abstract void mapearParametros();
    




}
