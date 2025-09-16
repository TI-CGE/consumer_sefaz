package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "contrato", schema = "consumer_sefaz")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sg_unidade_gestora", length = 100, nullable = true)
    private String sgUnidadeGestora;
    @Column(name = "cd_unidade_gestora", length = 20, nullable = true)
    private String cdUnidadeGestora;
    @Column(name = "dt_ano_exercicio", nullable = true)
    private Integer dtAnoExercicio;
    @Column(name = "cd_contrato", length = 30, nullable = true)
    private String cdContrato;
    @Column(name = "cd_aditivo", length = 30, nullable = true)
    private String cdAditivo;
    @Column(name = "dt_inicio_vigencia", nullable = true)
    private LocalDate dtInicioVigencia;
    @Column(name = "dt_fim_vigencia", nullable = true)
    private LocalDate dtFimVigencia;
    @Column(name = "nm_categoria", length = 200, nullable = true)
    private String nmCategoria;
    @Column(name = "nm_fornecedor", length = 200, nullable = true)
    private String nmFornecedor;
    @Column(name = "nu_documento", length = 20, nullable = true)
    private String nuDocumento;
    @Column(name = "ds_objeto_contrato", columnDefinition = "TEXT", nullable = true)
    private String dsObjetoContrato;
    @Column(name = "vl_contrato", precision = 18, scale = 2, nullable = true)
    private BigDecimal vlContrato;
    @Column(name = "tp_contrato", length = 10, nullable = true)
    private String tpContrato;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public Contrato() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Integer getDtAnoExercicio() {
        return dtAnoExercicio;
    }
    public void setDtAnoExercicio(Integer dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }
    public String getCdContrato() {
        return cdContrato;
    }
    public void setCdContrato(String cdContrato) {
        this.cdContrato = cdContrato;
    }
    public String getCdAditivo() {
        return cdAditivo;
    }
    public void setCdAditivo(String cdAditivo) {
        this.cdAditivo = cdAditivo;
    }
    public LocalDate getDtInicioVigencia() {
        return dtInicioVigencia;
    }
    public void setDtInicioVigencia(LocalDate dtInicioVigencia) {
        this.dtInicioVigencia = dtInicioVigencia;
    }
    public LocalDate getDtFimVigencia() {
        return dtFimVigencia;
    }
    public void setDtFimVigencia(LocalDate dtFimVigencia) {
        this.dtFimVigencia = dtFimVigencia;
    }
    public String getNmCategoria() {
        return nmCategoria;
    }
    public void setNmCategoria(String nmCategoria) {
        this.nmCategoria = nmCategoria;
    }
    public String getNmFornecedor() {
        return nmFornecedor;
    }
    public void setNmFornecedor(String nmFornecedor) {
        this.nmFornecedor = nmFornecedor;
    }
    public String getNuDocumento() {
        return nuDocumento;
    }
    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }
    public String getDsObjetoContrato() {
        return dsObjetoContrato;
    }
    public void setDsObjetoContrato(String dsObjetoContrato) {
        this.dsObjetoContrato = dsObjetoContrato;
    }
    public BigDecimal getVlContrato() {
        return vlContrato;
    }
    public void setVlContrato(BigDecimal vlContrato) {
        this.vlContrato = vlContrato;
    }
    public String getTpContrato() {
        return tpContrato;
    }
    public void setTpContrato(String tpContrato) {
        this.tpContrato = tpContrato;
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