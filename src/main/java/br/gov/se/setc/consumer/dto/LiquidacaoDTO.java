package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiquidacaoDTO extends EndpontSefaz {
    @JsonProperty("sqEmpenho")
    private Long sqEmpenho;

    @JsonProperty("sqLiquidacao")
    private Long sqLiquidacao;

    @JsonProperty("dtLiquidacao")
    private LocalDate dtLiquidacao;

    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;

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

    @JsonProperty("vlBrutoLiquidacao")
    private BigDecimal vlBrutoLiquidacao;

    @JsonProperty("nuDocumento")
    private String nuDocumento;

    @JsonProperty("tpDocumento")
    private Integer tpDocumento;

    @JsonProperty("dtAnoExercicioCTB")
    private Integer dtAnoExercicioCTB;

    @JsonProperty("cdGestao")
    private String cdGestao;

    @JsonProperty("nmRazaoSocialPessoa")
    private String nmRazaoSocialPessoa;

    @JsonProperty("cdNaturezaDespesa")
    private String cdNaturezaDespesa;

    @JsonProperty("vlEstornadoLiquidacao")
    private BigDecimal vlEstornadoLiquidacao;
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
    private Integer cdFonteRecurso;

    @JsonProperty("cdLicitacao")
    private Integer cdLicitacao;

    @JsonProperty("dsObjetoLicitacao")
    private String dsObjetoLicitacao;

    @JsonProperty("nuProcessoLicitacao")
    private String nuProcessoLicitacao;

    @JsonProperty("nmModalidadeLicitacao")
    private String nmModalidadeLicitacao;

    public LiquidacaoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.liquidacao";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/liquidacao";
        nomeDataInicialPadraoFiltro = "dt_liquidacao";
        nomeDataFinalPadraoFiltro = "dt_liquidacao";
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }

    @Override
    public void mapearCamposResposta() {
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("sq_liquidacao", sqLiquidacao);
        camposResposta.put("dt_liquidacao", dtLiquidacao);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("id_orgao", idOrgao);
        camposResposta.put("sg_orgao", sgOrgao);
        camposResposta.put("id_orgao_supervisor", idOrgaoSupervisor);
        camposResposta.put("sg_orgao_supervisor", sgOrgaoSupervisor);
        camposResposta.put("vl_bruto_liquidacao", vlBrutoLiquidacao);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("tp_documento", tpDocumento);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("nm_razao_social_pessoa", nmRazaoSocialPessoa);
        camposResposta.put("cd_natureza_despesa", cdNaturezaDespesa);
        camposResposta.put("vl_estornado_liquidacao", vlEstornadoLiquidacao);
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
        camposParametros.put("sqLiquidacao", sqLiquidacao);
        camposParametros.put("nuDocumento", nuDocumento);
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
        camposParametros.put("nuAnoLancamento", utilsService.getAnoAtual());
        camposParametros.put("nuMesLancamento", utilsService.getMesAtual());

        // Nota: cdGestao será adicionado dinamicamente durante o processamento
        // Este método retorna os parâmetros base, e o cdGestao será iterado
        // no serviço de consumo para buscar dados de todos os códigos de gestão

        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioCTB", ano);
        camposParametros.put("nuAnoLancamento", ano);

        // Nota: cdGestao será adicionado dinamicamente durante o processamento
        // Este método retorna os parâmetros base, e o cdGestao será iterado
        // no serviço de consumo para buscar dados de todos os códigos de gestão

        return camposParametros;
    }

    @Override
    public boolean requerIteracaoCdGestao() {
        return true; // LiquidacaoDTO precisa iterar sobre todos os valores de cdGestao
    }

    // Getters and Setters
    public Long getSqEmpenho() {
        return sqEmpenho;
    }

    public void setSqEmpenho(Long sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }

    public Long getSqLiquidacao() {
        return sqLiquidacao;
    }

    public void setSqLiquidacao(Long sqLiquidacao) {
        this.sqLiquidacao = sqLiquidacao;
    }

    public LocalDate getDtLiquidacao() {
        return dtLiquidacao;
    }

    public void setDtLiquidacao(LocalDate dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
    }

    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }

    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
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

    public BigDecimal getVlBrutoLiquidacao() {
        return vlBrutoLiquidacao;
    }

    public void setVlBrutoLiquidacao(BigDecimal vlBrutoLiquidacao) {
        this.vlBrutoLiquidacao = vlBrutoLiquidacao;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    public Integer getTpDocumento() {
        return tpDocumento;
    }

    public void setTpDocumento(Integer tpDocumento) {
        this.tpDocumento = tpDocumento;
    }

    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }

    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }

    public String getCdGestao() {
        return cdGestao;
    }

    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }

    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }

    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }

    public String getCdNaturezaDespesa() {
        return cdNaturezaDespesa;
    }

    public void setCdNaturezaDespesa(String cdNaturezaDespesa) {
        this.cdNaturezaDespesa = cdNaturezaDespesa;
    }

    public BigDecimal getVlEstornadoLiquidacao() {
        return vlEstornadoLiquidacao;
    }

    public void setVlEstornadoLiquidacao(BigDecimal vlEstornadoLiquidacao) {
        this.vlEstornadoLiquidacao = vlEstornadoLiquidacao;
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

    public Integer getCdFonteRecurso() {
        return cdFonteRecurso;
    }

    public void setCdFonteRecurso(Integer cdFonteRecurso) {
        this.cdFonteRecurso = cdFonteRecurso;
    }

    public Integer getCdLicitacao() {
        return cdLicitacao;
    }

    public void setCdLicitacao(Integer cdLicitacao) {
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
