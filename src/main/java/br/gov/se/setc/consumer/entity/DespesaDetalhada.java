package br.gov.se.setc.consumer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA para Despesa Detalhada
 * Tabela: despesa_detalhada no schema consumer_sefaz
 * 
 * Representa dados detalhados de despesas por órgão, unidade orçamentária,
 * organizados em hierarquia de categorização (função → subfunção → programa → ação → natureza)
 */
@Entity
@Table(name = "despesa_detalhada", schema = "consumer_sefaz",
       indexes = {
           @Index(name = "ix_dd_ug", columnList = "cd_unidade_gestora"),
           @Index(name = "ix_dd_ano", columnList = "dt_ano_exercicio_ctb"),
           @Index(name = "ix_dd_orgao", columnList = "cd_orgao"),
           @Index(name = "ix_dd_unid_orc", columnList = "cd_unid_orc"),
           @Index(name = "ix_dd_natureza", columnList = "cd_natureza_despesa"),
           @Index(name = "uq_despesa_detalhada",
                  columnList = "cd_unidade_gestora, dt_ano_exercicio_ctb, nu_mes, cd_orgao, cd_unid_orc, cd_natureza_despesa, cd_ppa_acao, cd_sub_acao",
                  unique = true)
       })
public class DespesaDetalhada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos obrigatórios
    @Column(name = "cd_unidade_gestora", length = 10, nullable = false)
    private String cdUnidadeGestora;

    @Column(name = "dt_ano_exercicio_ctb", nullable = false)
    private Integer dtAnoExercicioCTB;

    @Column(name = "nu_mes", nullable = false)
    private Integer nuMes;

    // Hierarquia organizacional
    @Column(name = "cd_orgao", length = 10, nullable = false)
    private String cdOrgao;

    @Column(name = "nm_orgao", length = 200)
    private String nmOrgao;

    @Column(name = "cd_unid_orc", length = 10, nullable = false)
    private String cdUnidOrc;

    @Column(name = "nm_unid_orc", length = 200)
    private String nmUnidOrc;

    // Hierarquia funcional
    @Column(name = "cd_funcao", length = 5)
    private String cdFuncao;

    @Column(name = "nm_funcao", length = 120)
    private String nmFuncao;

    @Column(name = "cd_sub_funcao", length = 10)
    private String cdSubFuncao;

    @Column(name = "nm_sub_funcao", length = 120)
    private String nmSubFuncao;

    // Hierarquia programática
    @Column(name = "cd_programa_governo", length = 10)
    private String cdProgramaGoverno;

    @Column(name = "nm_programa_governo", length = 200)
    private String nmProgramaGoverno;

    @Column(name = "cd_ppa_acao", length = 10, nullable = false)
    private String cdPPAAcao;

    @Column(name = "nm_ppa_acao", length = 200)
    private String nmPPAAcao;

    @Column(name = "cd_sub_acao", length = 10, nullable = false)
    private String cdSubAcao;

    @Column(name = "nm_subacao", length = 200)
    private String nmSubacao;

    // Categoria econômica
    @Column(name = "cd_categoria_economica", length = 5)
    private String cdCategoriaEconomica;

    @Column(name = "nm_categoria_economica", length = 120)
    private String nmCategoriaEconomica;

    // Natureza da despesa
    @Column(name = "cd_natureza_despesa", length = 20, nullable = false)
    private String cdNaturezaDespesa;

    @Column(name = "nm_natureza_despesa", length = 250)
    private String nmNaturezaDespesa;

    // Valores monetários
    @Column(name = "vl_dotacao_inicial", precision = 18, scale = 2)
    private BigDecimal vlDotacaoInicial;

    @Column(name = "vl_credito_adicional", precision = 18, scale = 2)
    private BigDecimal vlCreditoAdicional;

    @Column(name = "vl_dotacao_atualizada", precision = 18, scale = 2)
    private BigDecimal vlDotacaoAtualizada;

    @Column(name = "vl_total_empenhado", precision = 18, scale = 2)
    private BigDecimal vlTotalEmpenhado;

    @Column(name = "vl_total_liquidado", precision = 18, scale = 2)
    private BigDecimal vlTotalLiquidado;

    @Column(name = "vl_total_pago", precision = 18, scale = 2)
    private BigDecimal vlTotalPago;

    // Campos de auditoria
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtores
    public DespesaDetalhada() {
    }

    // Métodos de ciclo de vida JPA
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }

    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }

    public Integer getNuMes() {
        return nuMes;
    }

    public void setNuMes(Integer nuMes) {
        this.nuMes = nuMes;
    }

    public String getCdOrgao() {
        return cdOrgao;
    }

    public void setCdOrgao(String cdOrgao) {
        this.cdOrgao = cdOrgao;
    }

    public String getNmOrgao() {
        return nmOrgao;
    }

    public void setNmOrgao(String nmOrgao) {
        this.nmOrgao = nmOrgao;
    }

    public String getCdUnidOrc() {
        return cdUnidOrc;
    }

    public void setCdUnidOrc(String cdUnidOrc) {
        this.cdUnidOrc = cdUnidOrc;
    }

    public String getNmUnidOrc() {
        return nmUnidOrc;
    }

    public void setNmUnidOrc(String nmUnidOrc) {
        this.nmUnidOrc = nmUnidOrc;
    }

    public String getCdFuncao() {
        return cdFuncao;
    }

    public void setCdFuncao(String cdFuncao) {
        this.cdFuncao = cdFuncao;
    }

    public String getNmFuncao() {
        return nmFuncao;
    }

    public void setNmFuncao(String nmFuncao) {
        this.nmFuncao = nmFuncao;
    }

    public String getCdSubFuncao() {
        return cdSubFuncao;
    }

    public void setCdSubFuncao(String cdSubFuncao) {
        this.cdSubFuncao = cdSubFuncao;
    }

    public String getNmSubFuncao() {
        return nmSubFuncao;
    }

    public void setNmSubFuncao(String nmSubFuncao) {
        this.nmSubFuncao = nmSubFuncao;
    }

    public String getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }

    public void setCdProgramaGoverno(String cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }

    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }

    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }

    public String getCdPPAAcao() {
        return cdPPAAcao;
    }

    public void setCdPPAAcao(String cdPPAAcao) {
        this.cdPPAAcao = cdPPAAcao;
    }

    public String getNmPPAAcao() {
        return nmPPAAcao;
    }

    public void setNmPPAAcao(String nmPPAAcao) {
        this.nmPPAAcao = nmPPAAcao;
    }

    public String getCdSubAcao() {
        return cdSubAcao;
    }

    public void setCdSubAcao(String cdSubAcao) {
        this.cdSubAcao = cdSubAcao;
    }

    public String getNmSubacao() {
        return nmSubacao;
    }

    public void setNmSubacao(String nmSubacao) {
        this.nmSubacao = nmSubacao;
    }

    public String getCdCategoriaEconomica() {
        return cdCategoriaEconomica;
    }

    public void setCdCategoriaEconomica(String cdCategoriaEconomica) {
        this.cdCategoriaEconomica = cdCategoriaEconomica;
    }

    public String getNmCategoriaEconomica() {
        return nmCategoriaEconomica;
    }

    public void setNmCategoriaEconomica(String nmCategoriaEconomica) {
        this.nmCategoriaEconomica = nmCategoriaEconomica;
    }

    public String getCdNaturezaDespesa() {
        return cdNaturezaDespesa;
    }

    public void setCdNaturezaDespesa(String cdNaturezaDespesa) {
        this.cdNaturezaDespesa = cdNaturezaDespesa;
    }

    public String getNmNaturezaDespesa() {
        return nmNaturezaDespesa;
    }

    public void setNmNaturezaDespesa(String nmNaturezaDespesa) {
        this.nmNaturezaDespesa = nmNaturezaDespesa;
    }

    public BigDecimal getVlDotacaoInicial() {
        return vlDotacaoInicial;
    }

    public void setVlDotacaoInicial(BigDecimal vlDotacaoInicial) {
        this.vlDotacaoInicial = vlDotacaoInicial;
    }

    public BigDecimal getVlCreditoAdicional() {
        return vlCreditoAdicional;
    }

    public void setVlCreditoAdicional(BigDecimal vlCreditoAdicional) {
        this.vlCreditoAdicional = vlCreditoAdicional;
    }

    public BigDecimal getVlDotacaoAtualizada() {
        return vlDotacaoAtualizada;
    }

    public void setVlDotacaoAtualizada(BigDecimal vlDotacaoAtualizada) {
        this.vlDotacaoAtualizada = vlDotacaoAtualizada;
    }

    public BigDecimal getVlTotalEmpenhado() {
        return vlTotalEmpenhado;
    }

    public void setVlTotalEmpenhado(BigDecimal vlTotalEmpenhado) {
        this.vlTotalEmpenhado = vlTotalEmpenhado;
    }

    public BigDecimal getVlTotalLiquidado() {
        return vlTotalLiquidado;
    }

    public void setVlTotalLiquidado(BigDecimal vlTotalLiquidado) {
        this.vlTotalLiquidado = vlTotalLiquidado;
    }

    public BigDecimal getVlTotalPago() {
        return vlTotalPago;
    }

    public void setVlTotalPago(BigDecimal vlTotalPago) {
        this.vlTotalPago = vlTotalPago;
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

    @Override
    public String toString() {
        return "DespesaDetalhada{" +
                "id=" + id +
                ", cdUnidadeGestora='" + cdUnidadeGestora + '\'' +
                ", dtAnoExercicioCTB=" + dtAnoExercicioCTB +
                ", nuMes=" + nuMes +
                ", cdOrgao='" + cdOrgao + '\'' +
                ", nmOrgao='" + nmOrgao + '\'' +
                ", cdUnidOrc='" + cdUnidOrc + '\'' +
                ", cdNaturezaDespesa='" + cdNaturezaDespesa + '\'' +
                ", cdPPAAcao='" + cdPPAAcao + '\'' +
                ", cdSubAcao='" + cdSubAcao + '\'' +
                '}';
    }
}
