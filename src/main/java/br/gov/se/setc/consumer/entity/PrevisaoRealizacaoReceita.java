package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Entidade JPA para Previsão Realização Receita
 * Tabela: previsao_realizacao_receita no schema consumer_sefaz
 * 
 * Representa dados de previsão e realização de receitas por unidade gestora,
 * organizados em hierarquia de categorização (categoria → origem → espécie → desdobramento → tipo)
 */
@Entity
@Table(name = "previsao_realizacao_receita", schema = "consumer_sefaz",
       indexes = {
           @Index(name = "ix_prr_ug", columnList = "cd_unidade_gestora"),
           @Index(name = "ix_prr_ano", columnList = "dt_ano_exercicio_ctb"),
           @Index(name = "uq_previsao_realizacao_receita",
                  columnList = "cd_unidade_gestora, dt_ano_exercicio_ctb, nu_mes, cd_categoria_economica, cd_origem, cd_especie, cd_desdobramento, cd_tipo",
                  unique = true)
       })
public class PrevisaoRealizacaoReceita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_unidade_gestora", length = 20, nullable = false)
    private String cdUnidadeGestora;
    @Column(name = "dt_ano_exercicio_ctb", nullable = false)
    private Integer dtAnoExercicioCTB;
    @Column(name = "nu_mes", nullable = false)
    private Integer nuMes;
    @Column(name = "cd_categoria_economica", length = 10, nullable = false)
    private String cdCategoriaEconomica;
    @Column(name = "nm_categoria_economica", length = 200, nullable = false)
    private String nmCategoriaEconomica;
    @Column(name = "cd_origem", length = 10, nullable = false)
    private String cdOrigem;
    @Column(name = "nm_origem", length = 200, nullable = false)
    private String nmOrigem;
    @Column(name = "cd_especie", length = 10, nullable = false)
    private String cdEspecie;
    @Column(name = "nm_especie", length = 200, nullable = false)
    private String nmEspecie;
    @Column(name = "cd_desdobramento", length = 10, nullable = false)
    private String cdDesdobramento;
    @Column(name = "nm_desdobramento", length = 200, nullable = false)
    private String nmDesdobramento;
    @Column(name = "cd_tipo", length = 10, nullable = false)
    private String cdTipo;
    @Column(name = "nm_tipo", length = 300, nullable = false)
    private String nmTipo;
    @Column(name = "vl_previsto", precision = 18, scale = 2, nullable = true)
    private BigDecimal vlPrevisto;
    @Column(name = "vl_atualizado", precision = 18, scale = 2, nullable = true)
    private BigDecimal vlAtualizado;
    @Column(name = "vl_realizado", precision = 18, scale = 2, nullable = true)
    private BigDecimal vlRealizado;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public PrevisaoRealizacaoReceita() {}
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
    public String getCdOrigem() {
        return cdOrigem;
    }
    public void setCdOrigem(String cdOrigem) {
        this.cdOrigem = cdOrigem;
    }
    public String getNmOrigem() {
        return nmOrigem;
    }
    public void setNmOrigem(String nmOrigem) {
        this.nmOrigem = nmOrigem;
    }
    public String getCdEspecie() {
        return cdEspecie;
    }
    public void setCdEspecie(String cdEspecie) {
        this.cdEspecie = cdEspecie;
    }
    public String getNmEspecie() {
        return nmEspecie;
    }
    public void setNmEspecie(String nmEspecie) {
        this.nmEspecie = nmEspecie;
    }
    public String getCdDesdobramento() {
        return cdDesdobramento;
    }
    public void setCdDesdobramento(String cdDesdobramento) {
        this.cdDesdobramento = cdDesdobramento;
    }
    public String getNmDesdobramento() {
        return nmDesdobramento;
    }
    public void setNmDesdobramento(String nmDesdobramento) {
        this.nmDesdobramento = nmDesdobramento;
    }
    public String getCdTipo() {
        return cdTipo;
    }
    public void setCdTipo(String cdTipo) {
        this.cdTipo = cdTipo;
    }
    public String getNmTipo() {
        return nmTipo;
    }
    public void setNmTipo(String nmTipo) {
        this.nmTipo = nmTipo;
    }
    public BigDecimal getVlPrevisto() {
        return vlPrevisto;
    }
    public void setVlPrevisto(BigDecimal vlPrevisto) {
        this.vlPrevisto = vlPrevisto;
    }
    public BigDecimal getVlAtualizado() {
        return vlAtualizado;
    }
    public void setVlAtualizado(BigDecimal vlAtualizado) {
        this.vlAtualizado = vlAtualizado;
    }
    public BigDecimal getVlRealizado() {
        return vlRealizado;
    }
    public void setVlRealizado(BigDecimal vlRealizado) {
        this.vlRealizado = vlRealizado;
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
        return "PrevisaoRealizacaoReceita{" +
                "id=" + id +
                ", cdUnidadeGestora='" + cdUnidadeGestora + '\'' +
                ", dtAnoExercicioCTB=" + dtAnoExercicioCTB +
                ", cdCategoriaEconomica='" + cdCategoriaEconomica + '\'' +
                ", nmCategoriaEconomica='" + nmCategoriaEconomica + '\'' +
                ", cdOrigem='" + cdOrigem + '\'' +
                ", nmOrigem='" + nmOrigem + '\'' +
                ", cdEspecie='" + cdEspecie + '\'' +
                ", nmEspecie='" + nmEspecie + '\'' +
                ", cdDesdobramento='" + cdDesdobramento + '\'' +
                ", nmDesdobramento='" + nmDesdobramento + '\'' +
                ", cdTipo='" + cdTipo + '\'' +
                ", nmTipo='" + nmTipo + '\'' +
                ", vlPrevisto=" + vlPrevisto +
                ", vlAtualizado=" + vlAtualizado +
                ", vlRealizado=" + vlRealizado +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
