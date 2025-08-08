package br.gov.se.setc.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_despesa_credor", schema = "consumer_sefaz")
public class BaseDespesaCredor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Campos de identificação
    @Column(name = "cd_gestao", length = 20)
    private String cdGestao;
    
    @Column(name = "ug_cd", length = 20)
    private String ugCd;
    
    @Column(name = "dt_ano_exercicio")
    private Integer dtAnoExercicio;
    
    @Column(name = "sq_empenho", length = 30)
    private String sqEmpenho;
    
    // Campos do credor/pessoa
    @Column(name = "nu_documento_pessoa", length = 30)
    private String nuDocumentoPessoa;
    
    @Column(name = "cd_tipo_documento", length = 10)
    private String cdTipoDocumento;
    
    @Column(name = "nm_razao_social_pessoa", length = 200)
    private String nmRazaoSocialPessoa;
    
    // Campos de datas
    @Column(name = "dh_lancamento_empenho")
    private LocalDateTime dhLancamentoEmpenho;
    
    @Column(name = "dt_geracao_empenho", length = 20)
    private String dtGeracaoEmpenho;
    
    // Campos de valores monetários
    @Column(name = "vl_original_empenho", precision = 18, scale = 2)
    private BigDecimal vlOriginalEmpenho;
    
    @Column(name = "vl_total_reforcado_empenho", precision = 18, scale = 2)
    private BigDecimal vlTotalReforcadoEmpenho;
    
    @Column(name = "vl_total_anulado_empenho", precision = 18, scale = 2)
    private BigDecimal vlTotalAnuladoEmpenho;
    
    @Column(name = "vl_empenhado_atual", precision = 18, scale = 2)
    private BigDecimal vlEmpenhadoAtual;
    
    @Column(name = "vl_total_liquidado_empenho", precision = 18, scale = 2)
    private BigDecimal vlTotalLiquidadoEmpenho;
    
    @Column(name = "vl_total_pago_empenho", precision = 18, scale = 2)
    private BigDecimal vlTotalPagoEmpenho;
    
    // Campos de licitação
    @Column(name = "nm_modalidade_licitacao", length = 100)
    private String nmModalidadeLicitacao;
    
    @Column(name = "cd_licitacao", length = 50)
    private String cdLicitacao;
    
    // Campos de item/material/serviço
    @Column(name = "nm_item_material_servico", columnDefinition = "TEXT")
    private String nmItemMaterialServico;
    
    @Column(name = "qt_item_solicitacao_empenho")
    private Integer qtItemSolicitacaoEmpenho;
    
    @Column(name = "vl_unitario_item_solicitacao_empenho", precision = 18, scale = 2)
    private BigDecimal vlUnitarioItemSolicitacaoEmpenho;
    
    @Column(name = "qt_reforcada")
    private Integer qtReforcada;
    
    @Column(name = "vl_total_itens", precision = 18, scale = 2)
    private BigDecimal vlTotalItens;
    
    // Campos de auditoria
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public BaseDespesaCredor() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCdGestao() {
        return cdGestao;
    }

    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }

    public String getUgCd() {
        return ugCd;
    }

    public void setUgCd(String ugCd) {
        this.ugCd = ugCd;
    }

    public Integer getDtAnoExercicio() {
        return dtAnoExercicio;
    }

    public void setDtAnoExercicio(Integer dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }

    public String getSqEmpenho() {
        return sqEmpenho;
    }

    public void setSqEmpenho(String sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }

    public String getNuDocumentoPessoa() {
        return nuDocumentoPessoa;
    }

    public void setNuDocumentoPessoa(String nuDocumentoPessoa) {
        this.nuDocumentoPessoa = nuDocumentoPessoa;
    }

    public String getCdTipoDocumento() {
        return cdTipoDocumento;
    }

    public void setCdTipoDocumento(String cdTipoDocumento) {
        this.cdTipoDocumento = cdTipoDocumento;
    }

    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }

    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }

    public LocalDateTime getDhLancamentoEmpenho() {
        return dhLancamentoEmpenho;
    }

    public void setDhLancamentoEmpenho(LocalDateTime dhLancamentoEmpenho) {
        this.dhLancamentoEmpenho = dhLancamentoEmpenho;
    }

    public String getDtGeracaoEmpenho() {
        return dtGeracaoEmpenho;
    }

    public void setDtGeracaoEmpenho(String dtGeracaoEmpenho) {
        this.dtGeracaoEmpenho = dtGeracaoEmpenho;
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

    public BigDecimal getVlEmpenhadoAtual() {
        return vlEmpenhadoAtual;
    }

    public void setVlEmpenhadoAtual(BigDecimal vlEmpenhadoAtual) {
        this.vlEmpenhadoAtual = vlEmpenhadoAtual;
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

    public String getNmModalidadeLicitacao() {
        return nmModalidadeLicitacao;
    }

    public void setNmModalidadeLicitacao(String nmModalidadeLicitacao) {
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
    }

    public String getCdLicitacao() {
        return cdLicitacao;
    }

    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }

    public String getNmItemMaterialServico() {
        return nmItemMaterialServico;
    }

    public void setNmItemMaterialServico(String nmItemMaterialServico) {
        this.nmItemMaterialServico = nmItemMaterialServico;
    }

    public Integer getQtItemSolicitacaoEmpenho() {
        return qtItemSolicitacaoEmpenho;
    }

    public void setQtItemSolicitacaoEmpenho(Integer qtItemSolicitacaoEmpenho) {
        this.qtItemSolicitacaoEmpenho = qtItemSolicitacaoEmpenho;
    }

    public BigDecimal getVlUnitarioItemSolicitacaoEmpenho() {
        return vlUnitarioItemSolicitacaoEmpenho;
    }

    public void setVlUnitarioItemSolicitacaoEmpenho(BigDecimal vlUnitarioItemSolicitacaoEmpenho) {
        this.vlUnitarioItemSolicitacaoEmpenho = vlUnitarioItemSolicitacaoEmpenho;
    }

    public Integer getQtReforcada() {
        return qtReforcada;
    }

    public void setQtReforcada(Integer qtReforcada) {
        this.qtReforcada = qtReforcada;
    }

    public BigDecimal getVlTotalItens() {
        return vlTotalItens;
    }

    public void setVlTotalItens(BigDecimal vlTotalItens) {
        this.vlTotalItens = vlTotalItens;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
