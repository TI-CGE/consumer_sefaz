package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
public class RestosAPagarDTO extends EndpontSefaz {
    @JsonProperty("vlTotalTransferidoNPP")
    private BigDecimal vlTotalTransferidoNPP;
    @JsonProperty("vlEmpenhoInscritoNp")
    private BigDecimal vlEmpenhoInscritoNp;
    @JsonProperty("vlEmpenhoInscritoP")
    private BigDecimal vlEmpenhoInscritoP;
    @JsonProperty("vlEmpenhoCancRpnpNaoExec")
    private BigDecimal vlEmpenhoCancRpnpNaoExec;
    @JsonProperty("vlEmpenhoCanceladoRp")
    private BigDecimal vlEmpenhoCanceladoRp;
    @JsonProperty("vlEmpenhoCancDivdRpnpExec")
    private BigDecimal vlEmpenhoCancDivdRpnpExec;
    @JsonProperty("vlEmpenhoTotalPagoNp")
    private BigDecimal vlEmpenhoTotalPagoNp;
    @JsonProperty("sqEmpenho")
    private Integer sqEmpenho;
    @JsonProperty("vlEmpenhoCanceladoDividaRp")
    private BigDecimal vlEmpenhoCanceladoDividaRp;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("vlEmpenhoTotalPagoP")
    private BigDecimal vlEmpenhoTotalPagoP;
    @JsonProperty("vlExecutado")
    private BigDecimal vlExecutado;
    @JsonProperty("dtAnoExercicioCTB")
    private Integer dtAnoExercicioCTB;
    @JsonProperty("vlEmpenhoCancRpnpExecutado")
    private BigDecimal vlEmpenhoCancRpnpExecutado;
    @JsonProperty("sqSolicitacaoEmpenho")
    private Integer sqSolicitacaoEmpenho;
    @JsonProperty("cdGestao")
    private String cdGestao;
    public RestosAPagarDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.restos_a_pagar";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/restos-a-pagar";
        nomeDataInicialPadraoFiltro = "dt_ano_exercicio_ctb";
        nomeDataFinalPadraoFiltro = "dt_ano_exercicio_ctb";
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("vl_total_transferido_npp", vlTotalTransferidoNPP);
        camposResposta.put("vl_empenho_inscrito_np", vlEmpenhoInscritoNp);
        camposResposta.put("vl_empenho_inscrito_p", vlEmpenhoInscritoP);
        camposResposta.put("vl_empenho_canc_rpnp_nao_exec", vlEmpenhoCancRpnpNaoExec);
        camposResposta.put("vl_empenho_cancelado_rp", vlEmpenhoCanceladoRp);
        camposResposta.put("vl_empenho_canc_divd_rpnp_exec", vlEmpenhoCancDivdRpnpExec);
        camposResposta.put("vl_empenho_total_pago_np", vlEmpenhoTotalPagoNp);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("vl_empenho_cancelado_divida_rp", vlEmpenhoCanceladoDividaRp);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("vl_empenho_total_pago_p", vlEmpenhoTotalPagoP);
        camposResposta.put("vl_executado", vlExecutado);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("vl_empenho_canc_rpnp_executado", vlEmpenhoCancRpnpExecutado);
        camposResposta.put("sq_solicitacao_empenho", sqSolicitacaoEmpenho);
        camposResposta.put("cd_gestao", cdGestao);
    }
    @Override
    protected void mapearParametros() {
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTB);
        camposParametros.put("sqEmpenho", sqEmpenho);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioCTB", ano);
        return camposParametros;
    }
    @Override
    public boolean requerIteracaoCdGestao() {
        return true;
    }
    public BigDecimal getVlTotalTransferidoNPP() { return vlTotalTransferidoNPP; }
    public void setVlTotalTransferidoNPP(BigDecimal vlTotalTransferidoNPP) { this.vlTotalTransferidoNPP = vlTotalTransferidoNPP; }
    public BigDecimal getVlEmpenhoInscritoNp() { return vlEmpenhoInscritoNp; }
    public void setVlEmpenhoInscritoNp(BigDecimal vlEmpenhoInscritoNp) { this.vlEmpenhoInscritoNp = vlEmpenhoInscritoNp; }
    public BigDecimal getVlEmpenhoInscritoP() { return vlEmpenhoInscritoP; }
    public void setVlEmpenhoInscritoP(BigDecimal vlEmpenhoInscritoP) { this.vlEmpenhoInscritoP = vlEmpenhoInscritoP; }
    public BigDecimal getVlEmpenhoCancRpnpNaoExec() { return vlEmpenhoCancRpnpNaoExec; }
    public void setVlEmpenhoCancRpnpNaoExec(BigDecimal vlEmpenhoCancRpnpNaoExec) { this.vlEmpenhoCancRpnpNaoExec = vlEmpenhoCancRpnpNaoExec; }
    public BigDecimal getVlEmpenhoCanceladoRp() { return vlEmpenhoCanceladoRp; }
    public void setVlEmpenhoCanceladoRp(BigDecimal vlEmpenhoCanceladoRp) { this.vlEmpenhoCanceladoRp = vlEmpenhoCanceladoRp; }
    public BigDecimal getVlEmpenhoCancDivdRpnpExec() { return vlEmpenhoCancDivdRpnpExec; }
    public void setVlEmpenhoCancDivdRpnpExec(BigDecimal vlEmpenhoCancDivdRpnpExec) { this.vlEmpenhoCancDivdRpnpExec = vlEmpenhoCancDivdRpnpExec; }
    public BigDecimal getVlEmpenhoTotalPagoNp() { return vlEmpenhoTotalPagoNp; }
    public void setVlEmpenhoTotalPagoNp(BigDecimal vlEmpenhoTotalPagoNp) { this.vlEmpenhoTotalPagoNp = vlEmpenhoTotalPagoNp; }
    public Integer getSqEmpenho() { return sqEmpenho; }
    public void setSqEmpenho(Integer sqEmpenho) { this.sqEmpenho = sqEmpenho; }
    public BigDecimal getVlEmpenhoCanceladoDividaRp() { return vlEmpenhoCanceladoDividaRp; }
    public void setVlEmpenhoCanceladoDividaRp(BigDecimal vlEmpenhoCanceladoDividaRp) { this.vlEmpenhoCanceladoDividaRp = vlEmpenhoCanceladoDividaRp; }
    public String getCdUnidadeGestora() { return cdUnidadeGestora; }
    public void setCdUnidadeGestora(String cdUnidadeGestora) { this.cdUnidadeGestora = cdUnidadeGestora; }
    public BigDecimal getVlEmpenhoTotalPagoP() { return vlEmpenhoTotalPagoP; }
    public void setVlEmpenhoTotalPagoP(BigDecimal vlEmpenhoTotalPagoP) { this.vlEmpenhoTotalPagoP = vlEmpenhoTotalPagoP; }
    public BigDecimal getVlExecutado() { return vlExecutado; }
    public void setVlExecutado(BigDecimal vlExecutado) { this.vlExecutado = vlExecutado; }
    public Integer getDtAnoExercicioCTB() { return dtAnoExercicioCTB; }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) { this.dtAnoExercicioCTB = dtAnoExercicioCTB; }
    public BigDecimal getVlEmpenhoCancRpnpExecutado() { return vlEmpenhoCancRpnpExecutado; }
    public void setVlEmpenhoCancRpnpExecutado(BigDecimal vlEmpenhoCancRpnpExecutado) { this.vlEmpenhoCancRpnpExecutado = vlEmpenhoCancRpnpExecutado; }
    public Integer getSqSolicitacaoEmpenho() { return sqSolicitacaoEmpenho; }
    public void setSqSolicitacaoEmpenho(Integer sqSolicitacaoEmpenho) { this.sqSolicitacaoEmpenho = sqSolicitacaoEmpenho; }
    public String getCdGestao() { return cdGestao; }
    public void setCdGestao(String cdGestao) { this.cdGestao = cdGestao; }
}
