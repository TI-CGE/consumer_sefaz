package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Entidade JPA para Totalizadores de Execução
 * Tabela: totalizadores_execucao no schema consumer_sefaz
 */
@Entity
@Table(name = "totalizadores_execucao", schema = "consumer_sefaz")
public class TotalizadoresExecucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_programa_governo")
    private Integer cdProgramaGoverno;
    @Column(name = "cd_natureza_despesa_elemento_despesa", length = 50)
    private String cdNaturezaDespesaElementoDespesa;
    @Column(name = "nm_natureza_despesa_elemento_despesa", length = 200)
    private String nmNaturezaDespesaElementoDespesa;
    @Column(name = "nm_programa_governo", length = 200)
    private String nmProgramaGoverno;
    @Column(name = "vl_total_pago", precision = 18, scale = 2)
    private BigDecimal vlTotalPago;
    @Column(name = "vl_total_liquidado", precision = 18, scale = 2)
    private BigDecimal vlTotalLiquidado;
    @Column(name = "cd_unidade_orcamentaria", length = 50)
    private String cdUnidadeOrcamentaria;
    @Column(name = "vl_total_empenhado", precision = 18, scale = 2)
    private BigDecimal vlTotalEmpenhado;
    @Column(name = "nm_fonte_recurso", length = 200)
    private String nmFonteRecurso;
    @Column(name = "nm_complemento_exec_orc", length = 200)
    private String nmComplementoExecOrc;
    @Column(name = "vl_total_dotacao_atualizada", precision = 18, scale = 2)
    private BigDecimal vlTotalDotacaoAtualizada;
    @Column(name = "cd_fonte_recurso_reduzida", length = 50)
    private String cdFonteRecursoReduzida;
    @Column(name = "cd_sub_acao", length = 50)
    private String cdSubAcao;
    @Column(name = "sg_unidade_gestora", length = 100)
    private String sgUnidadeGestora;
    @Column(name = "cd_unidade_gestora", length = 50)
    private String cdUnidadeGestora;
    @Column(name = "dh_ultima_alteracao")
    private LocalDateTime dhUltimaAlteracao;
    @Column(name = "nm_acao", length = 200)
    private String nmAcao;
    @Column(name = "dt_ano_exercicio_ctb")
    private Integer dtAnoExercicioCTB;
    @Column(name = "cd_acao", length = 50)
    private String cdAcao;
    @Column(name = "cd_gestao", length = 50)
    private String cdGestao;
    @Column(name = "cd_complemento_exec_orc", length = 50)
    private String cdComplementoExecOrc;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public TotalizadoresExecucao() {}
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }
    public void setCdProgramaGoverno(Integer cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }
    public String getCdNaturezaDespesaElementoDespesa() {
        return cdNaturezaDespesaElementoDespesa;
    }
    public void setCdNaturezaDespesaElementoDespesa(String cdNaturezaDespesaElementoDespesa) {
        this.cdNaturezaDespesaElementoDespesa = cdNaturezaDespesaElementoDespesa;
    }
    public String getNmNaturezaDespesaElementoDespesa() {
        return nmNaturezaDespesaElementoDespesa;
    }
    public void setNmNaturezaDespesaElementoDespesa(String nmNaturezaDespesaElementoDespesa) {
        this.nmNaturezaDespesaElementoDespesa = nmNaturezaDespesaElementoDespesa;
    }
    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }
    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }
    public BigDecimal getVlTotalPago() {
        return vlTotalPago;
    }
    public void setVlTotalPago(BigDecimal vlTotalPago) {
        this.vlTotalPago = vlTotalPago;
    }
    public BigDecimal getVlTotalLiquidado() {
        return vlTotalLiquidado;
    }
    public void setVlTotalLiquidado(BigDecimal vlTotalLiquidado) {
        this.vlTotalLiquidado = vlTotalLiquidado;
    }
    public String getCdUnidadeOrcamentaria() {
        return cdUnidadeOrcamentaria;
    }
    public void setCdUnidadeOrcamentaria(String cdUnidadeOrcamentaria) {
        this.cdUnidadeOrcamentaria = cdUnidadeOrcamentaria;
    }
    public BigDecimal getVlTotalEmpenhado() {
        return vlTotalEmpenhado;
    }
    public void setVlTotalEmpenhado(BigDecimal vlTotalEmpenhado) {
        this.vlTotalEmpenhado = vlTotalEmpenhado;
    }
    public String getNmFonteRecurso() {
        return nmFonteRecurso;
    }
    public void setNmFonteRecurso(String nmFonteRecurso) {
        this.nmFonteRecurso = nmFonteRecurso;
    }
    public String getNmComplementoExecOrc() {
        return nmComplementoExecOrc;
    }
    public void setNmComplementoExecOrc(String nmComplementoExecOrc) {
        this.nmComplementoExecOrc = nmComplementoExecOrc;
    }
    public BigDecimal getVlTotalDotacaoAtualizada() {
        return vlTotalDotacaoAtualizada;
    }
    public void setVlTotalDotacaoAtualizada(BigDecimal vlTotalDotacaoAtualizada) {
        this.vlTotalDotacaoAtualizada = vlTotalDotacaoAtualizada;
    }
    public String getCdFonteRecursoReduzida() {
        return cdFonteRecursoReduzida;
    }
    public void setCdFonteRecursoReduzida(String cdFonteRecursoReduzida) {
        this.cdFonteRecursoReduzida = cdFonteRecursoReduzida;
    }
    public String getCdSubAcao() {
        return cdSubAcao;
    }
    public void setCdSubAcao(String cdSubAcao) {
        this.cdSubAcao = cdSubAcao;
    }
    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }
    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public LocalDateTime getDhUltimaAlteracao() {
        return dhUltimaAlteracao;
    }
    public void setDhUltimaAlteracao(LocalDateTime dhUltimaAlteracao) {
        this.dhUltimaAlteracao = dhUltimaAlteracao;
    }
    public String getNmAcao() {
        return nmAcao;
    }
    public void setNmAcao(String nmAcao) {
        this.nmAcao = nmAcao;
    }
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }
    public String getCdAcao() {
        return cdAcao;
    }
    public void setCdAcao(String cdAcao) {
        this.cdAcao = cdAcao;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public String getCdComplementoExecOrc() {
        return cdComplementoExecOrc;
    }
    public void setCdComplementoExecOrc(String cdComplementoExecOrc) {
        this.cdComplementoExecOrc = cdComplementoExecOrc;
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
