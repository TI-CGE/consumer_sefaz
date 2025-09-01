package br.gov.se.setc.consumer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PagamentoDTO extends EndpontSefaz {
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

    @JsonProperty("sqPrevisaoDesembolso")
    private Long sqPrevisaoDesembolso;

    @JsonProperty("sqEmpenho")
    private Long sqEmpenho;

    @JsonProperty("sqOrdemBancaria")
    private Long sqOB;

    @JsonProperty("cdNaturezaDespesa")
    private String cdNaturezaDespesaCompleta;

    @JsonProperty("nuDocumento")
    private String nuDocumento;

    @JsonProperty("tpDocumento")
    private String tpDocumento;

    @JsonProperty("nmRazaoSocialPessoa")
    private String nmRazaoSocialPessoa;

    @JsonProperty("vlBrutoPD")
    private BigDecimal vlBrutoPD;

    @JsonProperty("vlRetidoPD")
    private BigDecimal vlRetidoPD;

    @JsonProperty("vlOB")
    private BigDecimal vlOB;

    @JsonIgnore
    private Integer dtAnoExercicioCTBReferencia;

    @JsonProperty("dtPrevisaoDesembolso")
    private LocalDate dtPrevisaoDesembolso;

    @JsonProperty("dtLancamentoOB")
    private LocalDate dtLancamentoOB;
    @JsonProperty("inSituacaoPagamento")
    private String inSituacaoPagamento;

    @JsonProperty("situacaoOB")
    private String situacaoOB;

    @JsonProperty("cdFuncaoPLO")
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
    private Integer cdLicitacao;

    @JsonProperty("dsObjetoLicitacao")
    private String dsObjetoLicitacao;

    @JsonProperty("nuProcessoLicitacao")
    private String nuProcessoLicitacao;

    @JsonProperty("nmModalidadeLicitacao")
    private String nmModalidadeLicitacao;

    public PagamentoDTO() {
        inicializarDadosEndpoint();
    }

    public PagamentoDTO(Integer dtAnoExercicioCTB, String cdUnidadeGestora, String cdGestao, 
                       String sgUnidadeGestora, String idOrgao, String sgOrgao, String idOrgaoSupervisor,
                       String sgOrgaoSupervisor, Long sqPrevisaoDesembolso, Long sqEmpenho, Long sqOB,
                       String cdNaturezaDespesaCompleta, String nuDocumento, String tpDocumento,
                       String nmRazaoSocialPessoa, BigDecimal vlBrutoPD, BigDecimal vlRetidoPD,
                       BigDecimal vlOB, Integer dtAnoExercicioCTBReferencia, LocalDate dtPrevisaoDesembolso,
                       LocalDate dtLancamentoOB, String inSituacaoPagamento, String situacaoOB,
                       Integer cdFuncao, String nmFuncao, Integer cdSubFuncao, String nmSubFuncao,
                       Integer cdElementoDespesa, String cdFonteRecurso, Integer cdLicitacao,
                       String dsObjetoLicitacao, String nuProcessoLicitacao, String nmModalidadeLicitacao) {
        
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
        this.cdUnidadeGestora = cdUnidadeGestora;
        this.cdGestao = cdGestao;
        this.sgUnidadeGestora = sgUnidadeGestora;
        this.idOrgao = idOrgao;
        this.sgOrgao = sgOrgao;
        this.idOrgaoSupervisor = idOrgaoSupervisor;
        this.sgOrgaoSupervisor = sgOrgaoSupervisor;
        this.sqPrevisaoDesembolso = sqPrevisaoDesembolso;
        this.sqEmpenho = sqEmpenho;
        this.sqOB = sqOB;
        this.cdNaturezaDespesaCompleta = cdNaturezaDespesaCompleta;
        this.nuDocumento = nuDocumento;
        this.tpDocumento = tpDocumento;
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
        this.vlBrutoPD = vlBrutoPD;
        this.vlRetidoPD = vlRetidoPD;
        this.vlOB = vlOB;
        this.dtAnoExercicioCTBReferencia = dtAnoExercicioCTBReferencia;
        this.dtPrevisaoDesembolso = dtPrevisaoDesembolso;
        this.dtLancamentoOB = dtLancamentoOB;
        this.inSituacaoPagamento = inSituacaoPagamento;
        this.situacaoOB = situacaoOB;
        this.cdFuncao = cdFuncao;
        this.nmFuncao = nmFuncao;
        this.cdSubFuncao = cdSubFuncao;
        this.nmSubFuncao = nmSubFuncao;
        this.cdElementoDespesa = cdElementoDespesa;
        this.cdFonteRecurso = cdFonteRecurso;
        this.cdLicitacao = cdLicitacao;
        this.dsObjetoLicitacao = dsObjetoLicitacao;
        this.nuProcessoLicitacao = nuProcessoLicitacao;
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
        
        inicializarDadosEndpoint();
        mapearParametros();
        mapearCamposResposta();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.pagamento";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/pagamento";
        nomeDataInicialPadraoFiltro = "dt_lancamento_ob";
        nomeDataFinalPadraoFiltro = "dt_lancamento_ob";
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
        camposResposta.put("sq_previsao_desembolso", sqPrevisaoDesembolso);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("sq_ob", sqOB);
        camposResposta.put("cd_natureza_despesa_completa", cdNaturezaDespesaCompleta);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("tp_documento", tpDocumento);
        camposResposta.put("nm_razao_social_pessoa", nmRazaoSocialPessoa);
        camposResposta.put("vl_bruto_pd", vlBrutoPD);
        camposResposta.put("vl_retido_pd", vlRetidoPD);
        camposResposta.put("vl_ob", vlOB);
        camposResposta.put("dt_ano_exercicio_ctb_referencia", dtAnoExercicioCTBReferencia);
        camposResposta.put("dt_previsao_desembolso", dtPrevisaoDesembolso);
        camposResposta.put("dt_lancamento_ob", dtLancamentoOB);
        camposResposta.put("in_situacao_pagamento", inSituacaoPagamento);
        camposResposta.put("situacao_ob", situacaoOB);
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
        camposParametros.put("sqOB", sqOB);
        camposParametros.put("nuDocumento", nuDocumento);
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
        camposParametros.put("nuAnoLancamento", utilsService.getAnoAtual());
        camposParametros.put("nuMesLancamento", utilsService.getMesAtual());
        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioCTB", ano);
        camposParametros.put("nuAnoLancamento", ano);
        return camposParametros;
    }

    // Getters and Setters
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }

    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
        // Também definir o campo de referência com o mesmo valor
        this.dtAnoExercicioCTBReferencia = dtAnoExercicioCTB;
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

    public Long getSqPrevisaoDesembolso() {
        return sqPrevisaoDesembolso;
    }

    public void setSqPrevisaoDesembolso(Long sqPrevisaoDesembolso) {
        this.sqPrevisaoDesembolso = sqPrevisaoDesembolso;
    }

    public Long getSqEmpenho() {
        return sqEmpenho;
    }

    public void setSqEmpenho(Long sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }

    public Long getSqOB() {
        return sqOB;
    }

    public void setSqOB(Long sqOB) {
        this.sqOB = sqOB;
    }

    public String getCdNaturezaDespesaCompleta() {
        return cdNaturezaDespesaCompleta;
    }

    public void setCdNaturezaDespesaCompleta(String cdNaturezaDespesaCompleta) {
        this.cdNaturezaDespesaCompleta = cdNaturezaDespesaCompleta;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    public String getTpDocumento() {
        return tpDocumento;
    }

    public void setTpDocumento(String tpDocumento) {
        this.tpDocumento = tpDocumento;
    }

    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }

    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }

    public BigDecimal getVlBrutoPD() {
        return vlBrutoPD;
    }

    public void setVlBrutoPD(BigDecimal vlBrutoPD) {
        this.vlBrutoPD = vlBrutoPD;
    }

    public BigDecimal getVlRetidoPD() {
        return vlRetidoPD;
    }

    public void setVlRetidoPD(BigDecimal vlRetidoPD) {
        this.vlRetidoPD = vlRetidoPD;
    }

    public BigDecimal getVlOB() {
        return vlOB;
    }

    public void setVlOB(BigDecimal vlOB) {
        this.vlOB = vlOB;
    }

    public Integer getDtAnoExercicioCTBReferencia() {
        return dtAnoExercicioCTBReferencia;
    }

    public void setDtAnoExercicioCTBReferencia(Integer dtAnoExercicioCTBReferencia) {
        this.dtAnoExercicioCTBReferencia = dtAnoExercicioCTBReferencia;
    }

    public LocalDate getDtPrevisaoDesembolso() {
        return dtPrevisaoDesembolso;
    }

    public void setDtPrevisaoDesembolso(LocalDate dtPrevisaoDesembolso) {
        this.dtPrevisaoDesembolso = dtPrevisaoDesembolso;
    }

    public LocalDate getDtLancamentoOB() {
        return dtLancamentoOB;
    }

    public void setDtLancamentoOB(LocalDate dtLancamentoOB) {
        this.dtLancamentoOB = dtLancamentoOB;
    }

    public String getInSituacaoPagamento() {
        return inSituacaoPagamento;
    }

    public void setInSituacaoPagamento(String inSituacaoPagamento) {
        this.inSituacaoPagamento = inSituacaoPagamento;
    }

    public String getSituacaoOB() {
        return situacaoOB;
    }

    public void setSituacaoOB(String situacaoOB) {
        this.situacaoOB = situacaoOB;
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
