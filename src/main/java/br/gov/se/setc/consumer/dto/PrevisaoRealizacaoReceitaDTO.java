package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DTO para consumo da API de Previsão Realização Receita do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita
 * Contexto: CTB (Contabilidade)
 *
 * Esta API requer o parâmetro 'nuMes' (número do mês de 1-12).
 * Para busca de múltiplos meses, utilize os endpoints do scheduler.
 *
 * Estrutura de resposta: Array de objetos JSON com dados de previsão e realização de receitas
 * organizados em hierarquia de 5 níveis: Categoria → Origem → Espécie → Desdobramento → Tipo
 *
 * Chave única: cdUnidadeGestora + dtAnoExercicioCTB + nuMes + hierarquia completa
 */
public class PrevisaoRealizacaoReceitaDTO extends EndpontSefaz {
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("dtAnoExercicioCTB")
    private String dtAnoExercicioCTBString;
    @JsonProperty("cdCategoriaEconomica")
    private String cdCategoriaEconomica;
    @JsonProperty("nmCategoriaEconomica")
    private String nmCategoriaEconomica;
    @JsonProperty("cdOrigem")
    private String cdOrigem;
    @JsonProperty("nmOrigem")
    private String nmOrigem;
    @JsonProperty("cdEspecie")
    private String cdEspecie;
    @JsonProperty("nmEspecie")
    private String nmEspecie;
    @JsonProperty("cdDesdobramento")
    private String cdDesdobramento;
    @JsonProperty("nmDesdobramento")
    private String nmDesdobramento;
    @JsonProperty("cdTipo")
    private String cdTipo;
    @JsonProperty("nmTipo")
    private String nmTipo;
    @JsonProperty("vlPrevisto")
    private BigDecimal vlPrevisto;
    @JsonProperty("vlAtualizado")
    private BigDecimal vlAtualizado;
    @JsonProperty("vlRealizado")
    private BigDecimal vlRealizado;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioCTBFiltro;
    private Integer nuMesFiltro;
    private Integer dtAnoExercicioCTB;
    public PrevisaoRealizacaoReceitaDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.previsao_realizacao_receita";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita";
        nomeDataInicialPadraoFiltro = null;
        nomeDataFinalPadraoFiltro = null;
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        Integer mesParaSalvar = nuMesFiltro;
        if (mesParaSalvar == null) {
            mesParaSalvar = 12;
        }
        camposResposta.put("nu_mes", mesParaSalvar);
        camposResposta.put("cd_categoria_economica", cdCategoriaEconomica);
        camposResposta.put("nm_categoria_economica", nmCategoriaEconomica);
        camposResposta.put("cd_origem", cdOrigem);
        camposResposta.put("nm_origem", nmOrigem);
        camposResposta.put("cd_especie", cdEspecie);
        camposResposta.put("nm_especie", nmEspecie);
        camposResposta.put("cd_desdobramento", cdDesdobramento);
        camposResposta.put("nm_desdobramento", nmDesdobramento);
        camposResposta.put("cd_tipo", cdTipo);
        camposResposta.put("nm_tipo", nmTipo);
        camposResposta.put("vl_previsto", vlPrevisto);
        camposResposta.put("vl_atualizado", vlAtualizado);
        camposResposta.put("vl_realizado", vlRealizado);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTBFiltro);
        }
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicioCTB", ano.intValue());
        }
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        camposParametros.put("dtAnoExercicioCTB", 2025);
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);
        return camposParametros;
    }
    /**
     * Método para converter dtAnoExercicioCTBString para Integer após deserialização
     */
    public void processarCamposDerivados() {
        if (dtAnoExercicioCTBString != null && !dtAnoExercicioCTBString.trim().isEmpty()) {
            try {
                this.dtAnoExercicioCTB = Integer.parseInt(dtAnoExercicioCTBString.trim());
            } catch (NumberFormatException e) {
                this.dtAnoExercicioCTB = null;
            }
        }
    }
    /**
     * Método específico para Previsão Realização Receita que retorna parâmetros para todos os 12 meses
     * @param ugCd Código da Unidade Gestora
     * @param ano Ano do exercício
     * @return Lista com 12 mapas de parâmetros (um para cada mês)
     */
    public java.util.List<Map<String, Object>> getCamposParametrosTodosMeses(String ugCd, Short ano) {
        java.util.List<Map<String, Object>> parametrosMeses = new java.util.ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            Map<String, Object> parametrosMes = new LinkedHashMap<>();
            if (ugCd != null) {
                parametrosMes.put("cdUnidadeGestora", ugCd);
            }
            if (ano != null) {
                parametrosMes.put("dtAnoExercicioCTB", ano.intValue());
            }
            parametrosMes.put("nuMes", mes);
            parametrosMeses.add(parametrosMes);
        }
        return parametrosMeses;
    }
    /**
     * Método para definir o mês específico nos parâmetros
     * @param mes Número do mês (1-12)
     */
    public void setMesParametro(int mes) {
        if (mes >= 1 && mes <= 12) {
            camposParametros.put("nuMes", mes);
        }
    }
    /**
     * Indica se esta entidade requer múltiplas requisições (12 meses)
     * @return true para PrevisaoRealizacaoReceita
     */
    public boolean requerMultiplasRequisicoes() {
        return true;
    }
    /**
     * Retorna o número de requisições necessárias (12 meses)
     * @return 12
     */
    public int getNumeroRequisicoes() {
        return 12;
    }
    /**
     * Retorna o tempo de pausa entre requisições em milissegundos
     * @return 500ms
     */
    public long getPausaEntreRequisicoes() {
        return 500L;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getDtAnoExercicioCTBString() {
        return dtAnoExercicioCTBString;
    }
    public void setDtAnoExercicioCTBString(String dtAnoExercicioCTBString) {
        this.dtAnoExercicioCTBString = dtAnoExercicioCTBString;
        processarCamposDerivados();
    }
    @JsonIgnore
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }
    public String getCdCategoriaEconomica() {
        return cdCategoriaEconomica;
    }
    public void setCdCategoriaEconomica(String cdCategoriaEconomica) {
        this.cdCategoriaEconomica = cdCategoriaEconomica;
    }
    public String getNmCategoriaEconomica() {
        return nmCategoriaEconomica;
    }
    public void setNmCategoriaEconomica(String nmCategoriaEconomica) {
        this.nmCategoriaEconomica = nmCategoriaEconomica;
    }
    public String getCdOrigem() {
        return cdOrigem;
    }
    public void setCdOrigem(String cdOrigem) {
        this.cdOrigem = cdOrigem;
    }
    public String getNmOrigem() {
        return nmOrigem;
    }
    public void setNmOrigem(String nmOrigem) {
        this.nmOrigem = nmOrigem;
    }
    public String getCdEspecie() {
        return cdEspecie;
    }
    public void setCdEspecie(String cdEspecie) {
        this.cdEspecie = cdEspecie;
    }
    public String getNmEspecie() {
        return nmEspecie;
    }
    public void setNmEspecie(String nmEspecie) {
        this.nmEspecie = nmEspecie;
    }
    public String getCdDesdobramento() {
        return cdDesdobramento;
    }
    public void setCdDesdobramento(String cdDesdobramento) {
        this.cdDesdobramento = cdDesdobramento;
    }
    public String getNmDesdobramento() {
        return nmDesdobramento;
    }
    public void setNmDesdobramento(String nmDesdobramento) {
        this.nmDesdobramento = nmDesdobramento;
    }
    public String getCdTipo() {
        return cdTipo;
    }
    public void setCdTipo(String cdTipo) {
        this.cdTipo = cdTipo;
    }
    public String getNmTipo() {
        return nmTipo;
    }
    public void setNmTipo(String nmTipo) {
        this.nmTipo = nmTipo;
    }
    public BigDecimal getVlPrevisto() {
        return vlPrevisto;
    }
    public void setVlPrevisto(BigDecimal vlPrevisto) {
        this.vlPrevisto = vlPrevisto;
    }
    public BigDecimal getVlAtualizado() {
        return vlAtualizado;
    }
    public void setVlAtualizado(BigDecimal vlAtualizado) {
        this.vlAtualizado = vlAtualizado;
    }
    public BigDecimal getVlRealizado() {
        return vlRealizado;
    }
    public void setVlRealizado(BigDecimal vlRealizado) {
        this.vlRealizado = vlRealizado;
    }
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }
    public Integer getDtAnoExercicioCTBFiltro() {
        return dtAnoExercicioCTBFiltro;
    }
    public void setDtAnoExercicioCTBFiltro(Integer dtAnoExercicioCTBFiltro) {
        this.dtAnoExercicioCTBFiltro = dtAnoExercicioCTBFiltro;
    }
    public Integer getNuMesFiltro() {
        return nuMesFiltro;
    }
    public void setNuMesFiltro(Integer nuMesFiltro) {
        this.nuMesFiltro = nuMesFiltro;
    }
}