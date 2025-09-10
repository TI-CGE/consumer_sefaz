package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "empenho", schema = "consumer_sefaz")
public class Empenho {
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
    @Column(name = "sq_solic_empenho")
    private Integer sqSolicEmpenho;
    @Column(name = "sq_empenho")
    private Integer sqEmpenho;
    @Column(name = "dt_geracao_empenho")
    private String dtGeracaoEmpenho;
    @Column(name = "dt_emissao_empenho")
    private String dtEmissaoEmpenho;
    @Column(name = "dt_lancamento_empenho")
    private String dtLancamentoEmpenho;
    @Column(name = "cd_natureza_despesa_completa")
    private String cdNaturezaDespesaCompleta;
    @Column(name = "cd_natureza_despesa")
    private String cdNaturezaDespesa;
    @Column(name = "nm_razao_social_pessoa")
    private String nmRazaoSocialPessoa;
    @Column(name = "nu_documento")
    private String nuDocumento;
    @Column(name = "vl_solic_empenho", precision = 15, scale = 2)
    private BigDecimal vlSolicEmpenho;
    @Column(name = "vl_original_empenho", precision = 15, scale = 2)
    private BigDecimal vlOriginalEmpenho;
    @Column(name = "vl_total_reforcado_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalReforcadoEmpenho;
    @Column(name = "vl_total_anulado_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalAnuladoEmpenho;
    @Column(name = "vl_total_liquidado_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalLiquidadoEmpenho;
    @Column(name = "vl_total_estorno_liqd_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalEstornoLiqdEmpenho;
    @Column(name = "vl_total_pago_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalPagoEmpenho;
    @Column(name = "vl_total_executado", precision = 15, scale = 2)
    private BigDecimal vlTotalExecutado;
    @Column(name = "vl_total_devolvido_ob_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalDevolvidoOBEmpenho;
    @Column(name = "vl_total_devolvido_gr_empenho", precision = 15, scale = 2)
    private BigDecimal vlTotalDevolvidoGREmpenho;
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
    private String cdFonteRecurso;
    @Column(name = "cd_licitacao")
    private String cdLicitacao;
    @Column(name = "ds_objeto_licitacao", length = 1000)
    private String dsObjetoLicitacao;
    @Column(name = "nu_processo_licitacao")
    private String nuProcessoLicitacao;
    @Column(name = "nm_modalidade_licitacao")
    private String nmModalidadeLicitacao;
    public Empenho() {}
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
    public String getCdNaturezaDespesa() {
        return cdNaturezaDespesa;
    }
    public void setCdNaturezaDespesa(String cdNaturezaDespesa) {
        this.cdNaturezaDespesa = cdNaturezaDespesa;
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
    public BigDecimal getVlTotalEstornoLiqdEmpenho() {
        return vlTotalEstornoLiqdEmpenho;
    }
    public void setVlTotalEstornoLiqdEmpenho(BigDecimal vlTotalEstornoLiqdEmpenho) {
        this.vlTotalEstornoLiqdEmpenho = vlTotalEstornoLiqdEmpenho;
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
    public BigDecimal getVlTotalDevolvidoOBEmpenho() {
        return vlTotalDevolvidoOBEmpenho;
    }
    public void setVlTotalDevolvidoOBEmpenho(BigDecimal vlTotalDevolvidoOBEmpenho) {
        this.vlTotalDevolvidoOBEmpenho = vlTotalDevolvidoOBEmpenho;
    }
    public BigDecimal getVlTotalDevolvidoGREmpenho() {
        return vlTotalDevolvidoGREmpenho;
    }
    public void setVlTotalDevolvidoGREmpenho(BigDecimal vlTotalDevolvidoGREmpenho) {
        this.vlTotalDevolvidoGREmpenho = vlTotalDevolvidoGREmpenho;
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