package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
public class EmpenhoDTO extends EndpontSefaz {
    @JsonProperty("dtAnoExercicioCTB")
    private Integer dtAnoExercicioCTB;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("idOrgao")
    private String idOrgao;
    @JsonProperty("sgOrgao")
    private String sgOrgao;
    @JsonProperty("idOrgaoSupervisor")
    private String idOrgaoSupervisor;
    @JsonProperty("sgOrgaoSupervisor")
    private String sgOrgaoSupervisor;
    @JsonProperty("sqSolicEmpenho")
    private Integer sqSolicEmpenho;
    @JsonProperty("sqEmpenho")
    private Integer sqEmpenho;
    @JsonProperty("dtGeracaoEmpenho")
    private String dtGeracaoEmpenho;
    @JsonProperty("dtEmissaoEmpenho")
    private String dtEmissaoEmpenho;
    @JsonProperty("dtLancamentoEmpenho")
    private String dtLancamentoEmpenho;
    @JsonProperty("cdNaturezaDespesaCompleta")
    private String cdNaturezaDespesaCompleta;

    @JsonProperty("nmRazaoSocialPessoa")
    private String nmRazaoSocialPessoa;
    @JsonProperty("nuDocumento")
    private String nuDocumento;
    @JsonProperty("vlSolicEmpenho")
    private BigDecimal vlSolicEmpenho;
    @JsonProperty("vlOriginalEmpenho")
    private BigDecimal vlOriginalEmpenho;
    @JsonProperty("vlTotalReforcadoEmpenho")
    private BigDecimal vlTotalReforcadoEmpenho;
    @JsonProperty("vlTotalAnuladoEmpenho")
    private BigDecimal vlTotalAnuladoEmpenho;
    @JsonProperty("vlTotalLiquidadoEmpenho")
    private BigDecimal vlTotalLiquidadoEmpenho;
    @JsonProperty("vlTotalPagoEmpenho")
    private BigDecimal vlTotalPagoEmpenho;
    @JsonProperty("vlTotalExecutado")
    private BigDecimal vlTotalExecutado;
    @JsonProperty("cdFuncao")
    private Integer cdFuncao;
    @JsonProperty("nmFuncao")
    private String nmFuncao;
    @JsonProperty("cdSubFuncao")
    private Integer cdSubFuncao;
    @JsonProperty("nmSubFuncao")
    private String nmSubFuncao;
    @JsonProperty("cdElementoDespesa")
    private Integer cdElementoDespesa;
    @JsonProperty("cdFonteRecurso")
    private String cdFonteRecurso;
    @JsonProperty("cdLicitacao")
    private String cdLicitacao;
    @JsonProperty("dsObjetoLicitacao")
    private String dsObjetoLicitacao;
    @JsonProperty("nuProcessoLicitacao")
    private String nuProcessoLicitacao;
    @JsonProperty("nmModalidadeLicitacao")
    private String nmModalidadeLicitacao;
    private Integer nuMesFiltro;
    public EmpenhoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.empenho";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho";
        nomeDataInicialPadraoFiltro = "dt_geracao_empenho";
        nomeDataFinalPadraoFiltro = "dt_geracao_empenho";
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("id_orgao", idOrgao);
        camposResposta.put("sg_orgao", sgOrgao);
        camposResposta.put("id_orgao_supervisor", idOrgaoSupervisor);
        camposResposta.put("sg_orgao_supervisor", sgOrgaoSupervisor);
        camposResposta.put("sq_solic_empenho", sqSolicEmpenho);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("cd_natureza_despesa_completa", cdNaturezaDespesaCompleta);

        camposResposta.put("dt_geracao_empenho", dtGeracaoEmpenho);
        camposResposta.put("dt_emissao_empenho", dtEmissaoEmpenho);
        camposResposta.put("dt_lancamento_empenho", dtLancamentoEmpenho);
        camposResposta.put("nm_razao_social_pessoa", nmRazaoSocialPessoa);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("vl_solic_empenho", vlSolicEmpenho);
        camposResposta.put("vl_original_empenho", vlOriginalEmpenho);
        camposResposta.put("vl_total_reforcado_empenho", vlTotalReforcadoEmpenho);
        camposResposta.put("vl_total_anulado_empenho", vlTotalAnuladoEmpenho);
        camposResposta.put("vl_total_liquidado_empenho", vlTotalLiquidadoEmpenho);
        camposResposta.put("vl_total_pago_empenho", vlTotalPagoEmpenho);
        camposResposta.put("vl_total_executado", vlTotalExecutado);
        camposResposta.put("cd_funcao", cdFuncao);
        camposResposta.put("nm_funcao", nmFuncao);
        camposResposta.put("cd_sub_funcao", cdSubFuncao);
        camposResposta.put("nm_sub_funcao", nmSubFuncao);
        camposResposta.put("cd_elemento_despesa", cdElementoDespesa);
        camposResposta.put("cd_fonte_recurso", cdFonteRecurso);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("ds_objeto_licitacao", dsObjetoLicitacao);
        camposResposta.put("nu_processo_licitacao", nuProcessoLicitacao);
        camposResposta.put("nm_modalidade_licitacao", nmModalidadeLicitacao);
    }
    @Override
    protected void mapearParametros() {
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTB);
        camposParametros.put("sqEmpenho", sqEmpenho);
        camposParametros.put("nuDocumento", nuDocumento);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        } else {
            Short mesAtual = utilsService.getMesAtual();
            if (mesAtual != null) {
                camposParametros.put("nuMes", mesAtual.intValue());
            }
        }
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioCTB", ano);
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
        return camposParametros;
    }
    @Override
    public boolean requerIteracaoCdGestao() {
        return true;
    }
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public Integer getNuMesFiltro() {
        return nuMesFiltro;
    }
    public void setNuMesFiltro(Integer nuMesFiltro) {
        this.nuMesFiltro = nuMesFiltro;
    }
    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }
    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }
    public String getIdOrgao() {
        return idOrgao;
    }
    public void setIdOrgao(String idOrgao) {
        this.idOrgao = idOrgao;
    }
    public String getSgOrgao() {
        return sgOrgao;
    }
    public void setSgOrgao(String sgOrgao) {
        this.sgOrgao = sgOrgao;
    }
    public String getIdOrgaoSupervisor() {
        return idOrgaoSupervisor;
    }
    public void setIdOrgaoSupervisor(String idOrgaoSupervisor) {
        this.idOrgaoSupervisor = idOrgaoSupervisor;
    }
    public String getSgOrgaoSupervisor() {
        return sgOrgaoSupervisor;
    }
    public void setSgOrgaoSupervisor(String sgOrgaoSupervisor) {
        this.sgOrgaoSupervisor = sgOrgaoSupervisor;
    }
    public Integer getSqSolicEmpenho() {
        return sqSolicEmpenho;
    }
    public void setSqSolicEmpenho(Integer sqSolicEmpenho) {
        this.sqSolicEmpenho = sqSolicEmpenho;
    }
    public Integer getSqEmpenho() {
        return sqEmpenho;
    }
    public void setSqEmpenho(Integer sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }
    public String getDtGeracaoEmpenho() {
        return dtGeracaoEmpenho;
    }
    public void setDtGeracaoEmpenho(String dtGeracaoEmpenho) {
        this.dtGeracaoEmpenho = dtGeracaoEmpenho;
    }
    public String getDtEmissaoEmpenho() {
        return dtEmissaoEmpenho;
    }
    public void setDtEmissaoEmpenho(String dtEmissaoEmpenho) {
        this.dtEmissaoEmpenho = dtEmissaoEmpenho;
    }
    public String getDtLancamentoEmpenho() {
        return dtLancamentoEmpenho;
    }
    public void setDtLancamentoEmpenho(String dtLancamentoEmpenho) {
        this.dtLancamentoEmpenho = dtLancamentoEmpenho;
    }
    public String getCdNaturezaDespesaCompleta() {
        return cdNaturezaDespesaCompleta;
    }
    public void setCdNaturezaDespesaCompleta(String cdNaturezaDespesaCompleta) {
        this.cdNaturezaDespesaCompleta = cdNaturezaDespesaCompleta;
    }
    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }
    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }
    public String getNuDocumento() {
        return nuDocumento;
    }
    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }
    public BigDecimal getVlSolicEmpenho() {
        return vlSolicEmpenho;
    }
    public void setVlSolicEmpenho(BigDecimal vlSolicEmpenho) {
        this.vlSolicEmpenho = vlSolicEmpenho;
    }
    public BigDecimal getVlOriginalEmpenho() {
        return vlOriginalEmpenho;
    }
    public void setVlOriginalEmpenho(BigDecimal vlOriginalEmpenho) {
        this.vlOriginalEmpenho = vlOriginalEmpenho;
    }
    public BigDecimal getVlTotalReforcadoEmpenho() {
        return vlTotalReforcadoEmpenho;
    }
    public void setVlTotalReforcadoEmpenho(BigDecimal vlTotalReforcadoEmpenho) {
        this.vlTotalReforcadoEmpenho = vlTotalReforcadoEmpenho;
    }
    public BigDecimal getVlTotalAnuladoEmpenho() {
        return vlTotalAnuladoEmpenho;
    }
    public void setVlTotalAnuladoEmpenho(BigDecimal vlTotalAnuladoEmpenho) {
        this.vlTotalAnuladoEmpenho = vlTotalAnuladoEmpenho;
    }
    public BigDecimal getVlTotalLiquidadoEmpenho() {
        return vlTotalLiquidadoEmpenho;
    }
    public void setVlTotalLiquidadoEmpenho(BigDecimal vlTotalLiquidadoEmpenho) {
        this.vlTotalLiquidadoEmpenho = vlTotalLiquidadoEmpenho;
    }
    public BigDecimal getVlTotalPagoEmpenho() {
        return vlTotalPagoEmpenho;
    }
    public void setVlTotalPagoEmpenho(BigDecimal vlTotalPagoEmpenho) {
        this.vlTotalPagoEmpenho = vlTotalPagoEmpenho;
    }
    public BigDecimal getVlTotalExecutado() {
        return vlTotalExecutado;
    }
    public void setVlTotalExecutado(BigDecimal vlTotalExecutado) {
        this.vlTotalExecutado = vlTotalExecutado;
    }
    public Integer getCdFuncao() {
        return cdFuncao;
    }
    public void setCdFuncao(Integer cdFuncao) {
        this.cdFuncao = cdFuncao;
    }
    public String getNmFuncao() {
        return nmFuncao;
    }
    public void setNmFuncao(String nmFuncao) {
        this.nmFuncao = nmFuncao;
    }
    public Integer getCdSubFuncao() {
        return cdSubFuncao;
    }
    public void setCdSubFuncao(Integer cdSubFuncao) {
        this.cdSubFuncao = cdSubFuncao;
    }
    public String getNmSubFuncao() {
        return nmSubFuncao;
    }
    public void setNmSubFuncao(String nmSubFuncao) {
        this.nmSubFuncao = nmSubFuncao;
    }
    public Integer getCdElementoDespesa() {
        return cdElementoDespesa;
    }
    public void setCdElementoDespesa(Integer cdElementoDespesa) {
        this.cdElementoDespesa = cdElementoDespesa;
    }
    public String getCdFonteRecurso() {
        return cdFonteRecurso;
    }
    public void setCdFonteRecurso(String cdFonteRecurso) {
        this.cdFonteRecurso = cdFonteRecurso;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public String getDsObjetoLicitacao() {
        return dsObjetoLicitacao;
    }
    public void setDsObjetoLicitacao(String dsObjetoLicitacao) {
        this.dsObjetoLicitacao = dsObjetoLicitacao;
    }
    public String getNuProcessoLicitacao() {
        return nuProcessoLicitacao;
    }
    public void setNuProcessoLicitacao(String nuProcessoLicitacao) {
        this.nuProcessoLicitacao = nuProcessoLicitacao;
    }
    public String getNmModalidadeLicitacao() {
        return nmModalidadeLicitacao;
    }
    public void setNmModalidadeLicitacao(String nmModalidadeLicitacao) {
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
    }
}