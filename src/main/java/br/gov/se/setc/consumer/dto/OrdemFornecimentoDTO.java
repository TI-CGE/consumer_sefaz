package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
public class OrdemFornecimentoDTO extends EndpontSefaz {
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("cdUnidadeAdministrativa")
    private String cdUnidadeAdministrativa;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("dtAnoExercicioEmp")
    private Integer dtAnoExercicioEmp;
    @JsonProperty("dtAnoExercicioOF")
    private Integer dtAnoExercicioOF;
    @JsonProperty("sqEmpenho")
    private Long sqEmpenho;
    @JsonProperty("sqOrdemFornecimento")
    private Long sqOrdemFornecimento;
    @JsonProperty("dtRecebimento")
    private LocalDate dtRecebimento;
    @JsonProperty("vlOrdemFornecimento")
    private BigDecimal vlOrdemFornecimento;
    @JsonProperty("nuDanfe")
    private String nuDanfe;
    @JsonProperty("dtEmissao")
    private LocalDate dtEmissao;
    @JsonProperty("nuDocumentoDestinatario")
    private String nuDocumentoDestinatario;
    @JsonProperty("nmDestinatario")
    private String nmDestinatario;
    @JsonProperty("nuDocumentoEmitente")
    private String nuDocumentoEmitente;
    @JsonProperty("nmRemetente")
    private String nmRemetente;
    @JsonProperty("vltotaldesconto")
    private BigDecimal vltotaldesconto;
    @JsonProperty("vltotalnfe")
    private BigDecimal vltotalnfe;
    @JsonProperty("tpNfe")
    private String tpNfe;
    @JsonProperty("vlTotalProdServ")
    private BigDecimal vlTotalProdServ;
    @JsonProperty("vlTotalFrete")
    private BigDecimal vlTotalFrete;
    @JsonProperty("vlTotalSeguro")
    private BigDecimal vlTotalSeguro;
    @JsonProperty("vlBaseCalcIcms")
    private BigDecimal vlBaseCalcICMS;
    @JsonProperty("vlBaseCalcIcmsSt")
    private BigDecimal vlBaseCalcICMSST;
    @JsonProperty("vlTotalIcmsSt")
    private BigDecimal vlTotalICMSST;
    @JsonProperty("vlTotalIcms")
    private BigDecimal vlTotalICMS;
    @JsonProperty("vlIpi")
    private BigDecimal vlIpi;
    public OrdemFornecimentoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.ordem_fornecimento";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/ordem-fornecimento";
        nomeDataInicialPadraoFiltro = "dt_recebimento";
        nomeDataFinalPadraoFiltro = "dt_recebimento";
        dtAnoPadrao = "dt_ano_exercicio_emp";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("cd_unidade_administrativa", cdUnidadeAdministrativa);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("dt_ano_exercicio_emp", dtAnoExercicioEmp);
        camposResposta.put("dt_ano_exercicio_of", dtAnoExercicioOF);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("sq_ordem_fornecimento", sqOrdemFornecimento);
        camposResposta.put("dt_recebimento", dtRecebimento);
        camposResposta.put("vl_ordem_fornecimento", vlOrdemFornecimento);
        camposResposta.put("nu_danfe", nuDanfe);
        camposResposta.put("dt_emissao", dtEmissao);
        camposResposta.put("nu_documento_destinatario", nuDocumentoDestinatario);
        camposResposta.put("nm_destinatario", nmDestinatario);
        camposResposta.put("nu_documento_emitente", nuDocumentoEmitente);
        camposResposta.put("nm_remetente", nmRemetente);
        camposResposta.put("vl_total_desconto", vltotaldesconto);
        camposResposta.put("vl_total_nfe", vltotalnfe);
        camposResposta.put("tp_nfe", tpNfe);
        camposResposta.put("vl_total_prod_serv", vlTotalProdServ);
        camposResposta.put("vl_total_frete", vlTotalFrete);
        camposResposta.put("vl_total_seguro", vlTotalSeguro);
        camposResposta.put("vl_base_calc_icms", vlBaseCalcICMS);
        camposResposta.put("vl_base_calc_icms_st", vlBaseCalcICMSST);
        camposResposta.put("vl_total_icms_st", vlTotalICMSST);
        camposResposta.put("vl_total_icms", vlTotalICMS);
        camposResposta.put("vl_ipi", vlIpi);
    }
    @Override
    protected void mapearParametros() {
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioEmp", ano);
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioEmp", utilsService.getAnoAtual());
        camposParametros.put("nuMesRecebimento", utilsService.getMesAtual());
        return camposParametros;
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
    public LocalDate getDtEmissao() {
        return dtEmissao;
    }
    public void setDtEmissao(LocalDate dtEmissao) {
        this.dtEmissao = dtEmissao;
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
}