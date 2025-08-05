package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrdemFornecimentoDTO extends EndpontSefaz {
    private String cdUnidadeGestora;
    private String cdUnidadeAdministrativa;
    private String cdGestao;
    private Integer dtAnoExercicioEmp;
    private Integer dtAnoExercicioOF;
    private Long sqEmpenho;
    private Long sqOrdemFornecimento;
    private LocalDate dtRecebimento;
    private BigDecimal vlOrdemFornecimento;
    private String nuDanfe;
    private String nuNfe;
    private String cdVerificacaoNfse;
    private LocalDate dtEmissao;
    private LocalDate dtServico;
    private String nuDocumentoDestinatario;
    private String nmDestinatario;
    private String nuDocumentoEmitente;
    private String nmRemetente;
    private BigDecimal vltotaldesconto;
    private BigDecimal vltotalnfe;
    private String tpNfe;
    private BigDecimal vlTotalProdServ;
    private BigDecimal vlTotalFrete;
    private BigDecimal vlTotalSeguro;
    private BigDecimal vlBaseCalcICMS;
    private BigDecimal vlBaseCalcICMSST;
    private BigDecimal vlTotalICMSST;
    private BigDecimal vlPis;
    private BigDecimal vlCofins;
    private BigDecimal vlIss;
    private BigDecimal vlBaseCalcIss;
    private BigDecimal vlAliqISS;
    private BigDecimal vlTotalICMS;
    private BigDecimal vlIpi;
    private BigDecimal vlLiquido;
    private BigDecimal vlDeducoes;
    private BigDecimal vlOutRetencoes;
    private BigDecimal vlDescIncondicionado;
    private BigDecimal vlDescCondicionado;
    private String discriminacao;
    private BigDecimal vlInss;
    private BigDecimal vlRetPis;
    private BigDecimal vlRetCofins;
    private BigDecimal vlRetCsll;
    private BigDecimal vlRetIRRF;
    private BigDecimal vlBaseCalcRetIRRF;
    private BigDecimal vlRetPrev;
    private BigDecimal vlBaseCalcRetPrev;

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
        camposResposta.put("nu_nfe", nuNfe);
        camposResposta.put("cd_verificacao_nfse", cdVerificacaoNfse);
        camposResposta.put("dt_emissao", dtEmissao);
        camposResposta.put("dt_servico", dtServico);
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
        camposResposta.put("vl_pis", vlPis);
        camposResposta.put("vl_cofins", vlCofins);
        camposResposta.put("vl_iss", vlIss);
        camposResposta.put("vl_base_calc_iss", vlBaseCalcIss);
        camposResposta.put("vl_aliq_iss", vlAliqISS);
        camposResposta.put("vl_total_icms", vlTotalICMS);
        camposResposta.put("vl_ipi", vlIpi);
        camposResposta.put("vl_liquido", vlLiquido);
        camposResposta.put("vl_deducoes", vlDeducoes);
        camposResposta.put("vl_out_retencoes", vlOutRetencoes);
        camposResposta.put("vl_desc_incondicionado", vlDescIncondicionado);
        camposResposta.put("vl_desc_condicionado", vlDescCondicionado);
        camposResposta.put("discriminacao", discriminacao);
        camposResposta.put("vl_inss", vlInss);
        camposResposta.put("vl_ret_pis", vlRetPis);
        camposResposta.put("vl_ret_cofins", vlRetCofins);
        camposResposta.put("vl_ret_csll", vlRetCsll);
        camposResposta.put("vl_ret_irrf", vlRetIRRF);
        camposResposta.put("vl_base_calc_ret_irrf", vlBaseCalcRetIRRF);
        camposResposta.put("vl_ret_prev", vlRetPrev);
        camposResposta.put("vl_base_calc_ret_prev", vlBaseCalcRetPrev);
    }

    @Override
    protected void mapearParametros() {
        // Parâmetros serão definidos dinamicamente nos métodos getCamposParametros*
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

    // Getters and Setters
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
