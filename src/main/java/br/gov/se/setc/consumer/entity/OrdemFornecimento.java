package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "ordem_fornecimento", schema = "consumer_sefaz")
public class OrdemFornecimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    @Column(name = "cd_unidade_administrativa")
    private String cdUnidadeAdministrativa;
    @Column(name = "cd_gestao")
    private String cdGestao;
    @Column(name = "dt_ano_exercicio_emp")
    private Integer dtAnoExercicioEmp;
    @Column(name = "dt_ano_exercicio_of")
    private Integer dtAnoExercicioOF;
    @Column(name = "sq_empenho")
    private Long sqEmpenho;
    @Column(name = "sq_ordem_fornecimento")
    private Long sqOrdemFornecimento;
    @Column(name = "dt_recebimento")
    private LocalDate dtRecebimento;
    @Column(name = "vl_ordem_fornecimento", precision = 15, scale = 2)
    private BigDecimal vlOrdemFornecimento;
    @Column(name = "nu_danfe")
    private String nuDanfe;
    @Column(name = "nu_nfe")
    private String nuNfe;
    @Column(name = "cd_verificacao_nfse")
    private String cdVerificacaoNfse;
    @Column(name = "dt_emissao")
    private LocalDate dtEmissao;
    @Column(name = "dt_servico")
    private LocalDate dtServico;
    @Column(name = "nu_documento_destinatario")
    private String nuDocumentoDestinatario;
    @Column(name = "nm_destinatario")
    private String nmDestinatario;
    @Column(name = "nu_documento_emitente")
    private String nuDocumentoEmitente;
    @Column(name = "nm_remetente")
    private String nmRemetente;
    @Column(name = "vl_total_desconto", precision = 15, scale = 2)
    private BigDecimal vltotaldesconto;
    @Column(name = "vl_total_nfe", precision = 15, scale = 2)
    private BigDecimal vltotalnfe;
    @Column(name = "tp_nfe")
    private String tpNfe;
    @Column(name = "vl_total_prod_serv", precision = 15, scale = 2)
    private BigDecimal vlTotalProdServ;
    @Column(name = "vl_total_frete", precision = 15, scale = 2)
    private BigDecimal vlTotalFrete;
    @Column(name = "vl_total_seguro", precision = 15, scale = 2)
    private BigDecimal vlTotalSeguro;
    @Column(name = "vl_base_calc_icms", precision = 15, scale = 2)
    private BigDecimal vlBaseCalcICMS;
    @Column(name = "vl_base_calc_icms_st", precision = 15, scale = 2)
    private BigDecimal vlBaseCalcICMSST;
    @Column(name = "vl_total_icms_st", precision = 15, scale = 2)
    private BigDecimal vlTotalICMSST;
    @Column(name = "vl_pis", precision = 15, scale = 2)
    private BigDecimal vlPis;
    @Column(name = "vl_cofins", precision = 15, scale = 2)
    private BigDecimal vlCofins;
    @Column(name = "vl_iss", precision = 15, scale = 2)
    private BigDecimal vlIss;
    @Column(name = "vl_base_calc_iss", precision = 15, scale = 2)
    private BigDecimal vlBaseCalcIss;
    @Column(name = "vl_aliq_iss", precision = 15, scale = 2)
    private BigDecimal vlAliqISS;
    @Column(name = "vl_total_icms", precision = 15, scale = 2)
    private BigDecimal vlTotalICMS;
    @Column(name = "vl_ipi", precision = 15, scale = 2)
    private BigDecimal vlIpi;
    @Column(name = "vl_liquido", precision = 15, scale = 2)
    private BigDecimal vlLiquido;
    @Column(name = "vl_deducoes", precision = 15, scale = 2)
    private BigDecimal vlDeducoes;
    @Column(name = "vl_out_retencoes", precision = 15, scale = 2)
    private BigDecimal vlOutRetencoes;
    @Column(name = "vl_desc_incondicionado", precision = 15, scale = 2)
    private BigDecimal vlDescIncondicionado;
    @Column(name = "vl_desc_condicionado", precision = 15, scale = 2)
    private BigDecimal vlDescCondicionado;
    @Column(name = "discriminacao", length = 1000)
    private String discriminacao;
    @Column(name = "vl_inss", precision = 15, scale = 2)
    private BigDecimal vlInss;
    @Column(name = "vl_ret_pis", precision = 15, scale = 2)
    private BigDecimal vlRetPis;
    @Column(name = "vl_ret_cofins", precision = 15, scale = 2)
    private BigDecimal vlRetCofins;
    @Column(name = "vl_ret_csll", precision = 15, scale = 2)
    private BigDecimal vlRetCsll;
    @Column(name = "vl_ret_irrf", precision = 15, scale = 2)
    private BigDecimal vlRetIRRF;
    @Column(name = "vl_base_calc_ret_irrf", precision = 15, scale = 2)
    private BigDecimal vlBaseCalcRetIRRF;
    @Column(name = "vl_ret_prev", precision = 15, scale = 2)
    private BigDecimal vlRetPrev;
    @Column(name = "vl_base_calc_ret_prev", precision = 15, scale = 2)
    private BigDecimal vlBaseCalcRetPrev;
    public OrdemFornecimento() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getCdUnidadeAdministrativa() {
        return cdUnidadeAdministrativa;
    }
    public void setCdUnidadeAdministrativa(String cdUnidadeAdministrativa) {
        this.cdUnidadeAdministrativa = cdUnidadeAdministrativa;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public Integer getDtAnoExercicioEmp() {
        return dtAnoExercicioEmp;
    }
    public void setDtAnoExercicioEmp(Integer dtAnoExercicioEmp) {
        this.dtAnoExercicioEmp = dtAnoExercicioEmp;
    }
    public Integer getDtAnoExercicioOF() {
        return dtAnoExercicioOF;
    }
    public void setDtAnoExercicioOF(Integer dtAnoExercicioOF) {
        this.dtAnoExercicioOF = dtAnoExercicioOF;
    }
    public Long getSqEmpenho() {
        return sqEmpenho;
    }
    public void setSqEmpenho(Long sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }
    public Long getSqOrdemFornecimento() {
        return sqOrdemFornecimento;
    }
    public void setSqOrdemFornecimento(Long sqOrdemFornecimento) {
        this.sqOrdemFornecimento = sqOrdemFornecimento;
    }
    public LocalDate getDtRecebimento() {
        return dtRecebimento;
    }
    public void setDtRecebimento(LocalDate dtRecebimento) {
        this.dtRecebimento = dtRecebimento;
    }
    public BigDecimal getVlOrdemFornecimento() {
        return vlOrdemFornecimento;
    }
    public void setVlOrdemFornecimento(BigDecimal vlOrdemFornecimento) {
        this.vlOrdemFornecimento = vlOrdemFornecimento;
    }
    public String getNuDanfe() {
        return nuDanfe;
    }
    public void setNuDanfe(String nuDanfe) {
        this.nuDanfe = nuDanfe;
    }
    public String getNuNfe() {
        return nuNfe;
    }
    public void setNuNfe(String nuNfe) {
        this.nuNfe = nuNfe;
    }
    public String getCdVerificacaoNfse() {
        return cdVerificacaoNfse;
    }
    public void setCdVerificacaoNfse(String cdVerificacaoNfse) {
        this.cdVerificacaoNfse = cdVerificacaoNfse;
    }
    public LocalDate getDtEmissao() {
        return dtEmissao;
    }
    public void setDtEmissao(LocalDate dtEmissao) {
        this.dtEmissao = dtEmissao;
    }
    public LocalDate getDtServico() {
        return dtServico;
    }
    public void setDtServico(LocalDate dtServico) {
        this.dtServico = dtServico;
    }
    public String getNuDocumentoDestinatario() {
        return nuDocumentoDestinatario;
    }
    public void setNuDocumentoDestinatario(String nuDocumentoDestinatario) {
        this.nuDocumentoDestinatario = nuDocumentoDestinatario;
    }
    public String getNmDestinatario() {
        return nmDestinatario;
    }
    public void setNmDestinatario(String nmDestinatario) {
        this.nmDestinatario = nmDestinatario;
    }
    public String getNuDocumentoEmitente() {
        return nuDocumentoEmitente;
    }
    public void setNuDocumentoEmitente(String nuDocumentoEmitente) {
        this.nuDocumentoEmitente = nuDocumentoEmitente;
    }
    public String getNmRemetente() {
        return nmRemetente;
    }
    public void setNmRemetente(String nmRemetente) {
        this.nmRemetente = nmRemetente;
    }
    public BigDecimal getVltotaldesconto() {
        return vltotaldesconto;
    }
    public void setVltotaldesconto(BigDecimal vltotaldesconto) {
        this.vltotaldesconto = vltotaldesconto;
    }
    public BigDecimal getVltotalnfe() {
        return vltotalnfe;
    }
    public void setVltotalnfe(BigDecimal vltotalnfe) {
        this.vltotalnfe = vltotalnfe;
    }
    public String getTpNfe() {
        return tpNfe;
    }
    public void setTpNfe(String tpNfe) {
        this.tpNfe = tpNfe;
    }
    public BigDecimal getVlTotalProdServ() {
        return vlTotalProdServ;
    }
    public void setVlTotalProdServ(BigDecimal vlTotalProdServ) {
        this.vlTotalProdServ = vlTotalProdServ;
    }
    public BigDecimal getVlTotalFrete() {
        return vlTotalFrete;
    }
    public void setVlTotalFrete(BigDecimal vlTotalFrete) {
        this.vlTotalFrete = vlTotalFrete;
    }
    public BigDecimal getVlTotalSeguro() {
        return vlTotalSeguro;
    }
    public void setVlTotalSeguro(BigDecimal vlTotalSeguro) {
        this.vlTotalSeguro = vlTotalSeguro;
    }
    public BigDecimal getVlBaseCalcICMS() {
        return vlBaseCalcICMS;
    }
    public void setVlBaseCalcICMS(BigDecimal vlBaseCalcICMS) {
        this.vlBaseCalcICMS = vlBaseCalcICMS;
    }
    public BigDecimal getVlBaseCalcICMSST() {
        return vlBaseCalcICMSST;
    }
    public void setVlBaseCalcICMSST(BigDecimal vlBaseCalcICMSST) {
        this.vlBaseCalcICMSST = vlBaseCalcICMSST;
    }
    public BigDecimal getVlTotalICMSST() {
        return vlTotalICMSST;
    }
    public void setVlTotalICMSST(BigDecimal vlTotalICMSST) {
        this.vlTotalICMSST = vlTotalICMSST;
    }
    public BigDecimal getVlPis() {
        return vlPis;
    }
    public void setVlPis(BigDecimal vlPis) {
        this.vlPis = vlPis;
    }
    public BigDecimal getVlCofins() {
        return vlCofins;
    }
    public void setVlCofins(BigDecimal vlCofins) {
        this.vlCofins = vlCofins;
    }
    public BigDecimal getVlIss() {
        return vlIss;
    }
    public void setVlIss(BigDecimal vlIss) {
        this.vlIss = vlIss;
    }
    public BigDecimal getVlBaseCalcIss() {
        return vlBaseCalcIss;
    }
    public void setVlBaseCalcIss(BigDecimal vlBaseCalcIss) {
        this.vlBaseCalcIss = vlBaseCalcIss;
    }
    public BigDecimal getVlAliqISS() {
        return vlAliqISS;
    }
    public void setVlAliqISS(BigDecimal vlAliqISS) {
        this.vlAliqISS = vlAliqISS;
    }
    public BigDecimal getVlTotalICMS() {
        return vlTotalICMS;
    }
    public void setVlTotalICMS(BigDecimal vlTotalICMS) {
        this.vlTotalICMS = vlTotalICMS;
    }
    public BigDecimal getVlIpi() {
        return vlIpi;
    }
    public void setVlIpi(BigDecimal vlIpi) {
        this.vlIpi = vlIpi;
    }
    public BigDecimal getVlLiquido() {
        return vlLiquido;
    }
    public void setVlLiquido(BigDecimal vlLiquido) {
        this.vlLiquido = vlLiquido;
    }
    public BigDecimal getVlDeducoes() {
        return vlDeducoes;
    }
    public void setVlDeducoes(BigDecimal vlDeducoes) {
        this.vlDeducoes = vlDeducoes;
    }
    public BigDecimal getVlOutRetencoes() {
        return vlOutRetencoes;
    }
    public void setVlOutRetencoes(BigDecimal vlOutRetencoes) {
        this.vlOutRetencoes = vlOutRetencoes;
    }
    public BigDecimal getVlDescIncondicionado() {
        return vlDescIncondicionado;
    }
    public void setVlDescIncondicionado(BigDecimal vlDescIncondicionado) {
        this.vlDescIncondicionado = vlDescIncondicionado;
    }
    public BigDecimal getVlDescCondicionado() {
        return vlDescCondicionado;
    }
    public void setVlDescCondicionado(BigDecimal vlDescCondicionado) {
        this.vlDescCondicionado = vlDescCondicionado;
    }
    public String getDiscriminacao() {
        return discriminacao;
    }
    public void setDiscriminacao(String discriminacao) {
        this.discriminacao = discriminacao;
    }
    public BigDecimal getVlInss() {
        return vlInss;
    }
    public void setVlInss(BigDecimal vlInss) {
        this.vlInss = vlInss;
    }
    public BigDecimal getVlRetPis() {
        return vlRetPis;
    }
    public void setVlRetPis(BigDecimal vlRetPis) {
        this.vlRetPis = vlRetPis;
    }
    public BigDecimal getVlRetCofins() {
        return vlRetCofins;
    }
    public void setVlRetCofins(BigDecimal vlRetCofins) {
        this.vlRetCofins = vlRetCofins;
    }
    public BigDecimal getVlRetCsll() {
        return vlRetCsll;
    }
    public void setVlRetCsll(BigDecimal vlRetCsll) {
        this.vlRetCsll = vlRetCsll;
    }
    public BigDecimal getVlRetIRRF() {
        return vlRetIRRF;
    }
    public void setVlRetIRRF(BigDecimal vlRetIRRF) {
        this.vlRetIRRF = vlRetIRRF;
    }
    public BigDecimal getVlBaseCalcRetIRRF() {
        return vlBaseCalcRetIRRF;
    }
    public void setVlBaseCalcRetIRRF(BigDecimal vlBaseCalcRetIRRF) {
        this.vlBaseCalcRetIRRF = vlBaseCalcRetIRRF;
    }
    public BigDecimal getVlRetPrev() {
        return vlRetPrev;
    }
    public void setVlRetPrev(BigDecimal vlRetPrev) {
        this.vlRetPrev = vlRetPrev;
    }
    public BigDecimal getVlBaseCalcRetPrev() {
        return vlBaseCalcRetPrev;
    }
    public void setVlBaseCalcRetPrev(BigDecimal vlBaseCalcRetPrev) {
        this.vlBaseCalcRetPrev = vlBaseCalcRetPrev;
    }
}
