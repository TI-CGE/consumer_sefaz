package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "restos_a_pagar", schema = "consumer_sefaz")
public class RestosAPagar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "vl_total_transferido_npp", precision = 15, scale = 2)
    private BigDecimal vlTotalTransferidoNPP;
    @Column(name = "vl_empenho_inscrito_np", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoInscritoNp;
    @Column(name = "vl_empenho_inscrito_p", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoInscritoP;
    @Column(name = "vl_empenho_canc_rpnp_nao_exec", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoCancRpnpNaoExec;
    @Column(name = "vl_empenho_cancelado_rp", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoCanceladoRp;
    @Column(name = "vl_empenho_canc_divd_rpnp_exec", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoCancDivdRpnpExec;
    @Column(name = "vl_empenho_total_pago_np", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoTotalPagoNp;
    @Column(name = "sq_empenho")
    private Integer sqEmpenho;
    @Column(name = "vl_empenho_cancelado_divida_rp", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoCanceladoDividaRp;
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    @Column(name = "vl_empenho_total_pago_p", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoTotalPagoP;
    @Column(name = "vl_executado", precision = 15, scale = 2)
    private BigDecimal vlExecutado;
    @Column(name = "dt_ano_exercicio_ctb")
    private Integer dtAnoExercicioCTB;
    @Column(name = "vl_empenho_canc_rpnp_executado", precision = 15, scale = 2)
    private BigDecimal vlEmpenhoCancRpnpExecutado;
    @Column(name = "sq_solicitacao_empenho")
    private Integer sqSolicitacaoEmpenho;
    @Column(name = "cd_gestao")
    private String cdGestao;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
