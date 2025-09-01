package br.gov.se.setc.consumer.contracts;
import java.util.LinkedHashMap;
import java.util.Map;
import br.gov.se.setc.util.ValidacaoUtil;
public abstract class EndpontSefaz {
    protected String tabelaBanco;
    protected String url;
    protected String nomeDataInicialPadraoFiltro;
    protected String nomeDataFinalPadraoFiltro;
    protected String dtAnoPadrao;
    protected Boolean parametrosRequeridos;
    protected Map<String, Object> camposParametros = new LinkedHashMap<>();
    protected Map<String, Object> camposResposta = new LinkedHashMap<>();
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
    public Boolean getParametrosRequeridos() {
        return parametrosRequeridos;
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
     /**
      * Indica se este endpoint requer iteração sobre todos os valores de cdGestao
      * @return true se o endpoint precisa iterar sobre cdGestao, false caso contrário
      */
     public boolean requerIteracaoCdGestao() {
         return false;
     }
     /**
      * Indica se este endpoint requer iteração sobre empenhos para obter cdGestao e sqEmpenho
      * @return true se o endpoint precisa iterar sobre empenhos, false caso contrário
      */
     public boolean requerIteracaoEmpenhos() {
         return false;
     }
     protected Map<String, Object> criarParametrosBasicos(String ugCd, Short ano) {
         Map<String, Object> parametros = new LinkedHashMap<>();
         if (ugCd != null && !ugCd.trim().isEmpty()) {
             parametros.put("cdUnidadeGestora", ugCd);
         }
         if (ano != null) {
             parametros.put("dtAnoExercicioCTB", ano);
         }
         return parametros;
     }
     protected Map<String, Object> criarParametrosComAnoAtual(String ugCd, ValidacaoUtil<?> utilsService) {
         Map<String, Object> parametros = new LinkedHashMap<>();
         if (ugCd != null && !ugCd.trim().isEmpty()) {
             parametros.put("cdUnidadeGestora", ugCd);
         }
         parametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
         return parametros;
     }
}