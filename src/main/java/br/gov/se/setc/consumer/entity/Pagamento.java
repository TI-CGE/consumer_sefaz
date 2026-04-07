package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "pagamento", schema = "consumer_sefaz")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "dt_ano_exercicio_ctb")
    private Integer dtAnoExercicioCTB;
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    @Column(name = "cd_gestao")
    private String cdGestao;
    @Column(name = "sg_unidade_gestora")
    private String sgUnidadeGestora;
    @Column(name = "id_orgao")
    private String idOrgao;
    @Column(name = "sg_orgao")
    private String sgOrgao;
    @Column(name = "id_orgao_supervisor")
    private String idOrgaoSupervisor;
    @Column(name = "sg_orgao_supervisor")
    private String sgOrgaoSupervisor;
    @Column(name = "sq_previsao_desembolso")
    private Long sqPrevisaoDesembolso;
    @Column(name = "sq_empenho")
    private Long sqEmpenho;
    @Column(name = "sq_ob")
    private Long sqOB;
    @Column(name = "cd_natureza_despesa_completa")
    private String cdNaturezaDespesaCompleta;
    @Column(name = "nu_documento")
    private String nuDocumento;
    @Column(name = "tp_documento")
    private String tpDocumento;
    @Column(name = "nm_razao_social_pessoa")
    private String nmRazaoSocialPessoa;
    @Column(name = "vl_bruto_pd", precision = 15, scale = 2)
    private BigDecimal vlBrutoPD;
    @Column(name = "vl_retido_pd", precision = 15, scale = 2)
    private BigDecimal vlRetidoPD;
    @Column(name = "vl_ob", precision = 15, scale = 2)
    private BigDecimal vlOB;
    @Column(name = "dt_ano_exercicio_ctb_referencia")
    private Integer dtAnoExercicioCTBReferencia;
    @Column(name = "dt_previsao_desembolso")
    private LocalDate dtPrevisaoDesembolso;
    @Column(name = "dt_lancamento_ob")
    private LocalDate dtLancamentoOB;
    @Column(name = "in_situacao_pagamento")
    private String inSituacaoPagamento;
    @Column(name = "situacao_ob")
    private String situacaoOB;
    @Column(name = "cd_funcao")
    private Integer cdFuncao;
    @Column(name = "nm_funcao")
    private String nmFuncao;
    @Column(name = "cd_sub_funcao")
    private Integer cdSubFuncao;
    @Column(name = "nm_sub_funcao")
    private String nmSubFuncao;
    @Column(name = "cd_elemento_despesa")
    private Integer cdElementoDespesa;
    @Column(name = "cd_fonte_recurso")
    private Integer cdFonteRecurso;
    @Column(name = "cd_licitacao")
    private Integer cdLicitacao;
    @Column(name = "ds_objeto_licitacao", length = 1000)
    private String dsObjetoLicitacao;
    @Column(name = "nu_processo_licitacao")
    private String nuProcessoLicitacao;
    @Column(name = "nm_modalidade_licitacao")
    private String nmModalidadeLicitacao;
    public Pagamento() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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