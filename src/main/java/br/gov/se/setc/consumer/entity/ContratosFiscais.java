package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratos_fiscais", schema = "consumer_sefaz")
public class ContratosFiscais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sg_unidade_gestora", length = 100, nullable = true)
    private String sgUnidadeGestora;

    @Column(name = "cd_unidade_gestora", length = 20, nullable = true)
    private String cdUnidadeGestora;

    @Column(name = "dt_ano_exercicio", nullable = true)
    private Integer dtAnoExercicio;

    @Column(name = "cd_contrato", length = 50, nullable = true)
    private String cdContrato;

    @Column(name = "cd_licitacao", length = 50, nullable = true)
    private String cdLicitacao;

    @Column(name = "dt_inicio_vigencia_contrato", nullable = true)
    private LocalDate dtInicioVigenciaContrato;

    @Column(name = "dt_fim_vigencia_contrato", nullable = true)
    private LocalDate dtFimVigenciaContrato;

    @Column(name = "nm_contratado", length = 255, nullable = true)
    private String nmContratado;

    @Column(name = "nu_documento_contratado", length = 20, nullable = true)
    private String nuDocumentoContratado;

    @Column(name = "nm_fiscal", length = 255, nullable = true)
    private String nmFiscal;

    @Column(name = "ds_qualificador", length = 255, nullable = true)
    private String dsQualificador;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
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
    public int getDtAnoExercicio() {
        return dtAnoExercicio;
    }
    public void setDtAnoExercicio(int dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }
    public String getCdContrato() {
        return cdContrato;
    }
    public void setCdContrato(String cdContrato) {
        this.cdContrato = cdContrato;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public LocalDate getDtInicioVigenciaContrato() {
        return dtInicioVigenciaContrato;
    }
    public void setDtInicioVigenciaContrato(LocalDate dtInicioVigenciaContrato) {
        this.dtInicioVigenciaContrato = dtInicioVigenciaContrato;
    }
    public LocalDate getDtFimVigenciaContrato() {
        return dtFimVigenciaContrato;
    }
    public void setDtFimVigenciaContrato(LocalDate dtFimVigenciaContrato) {
        this.dtFimVigenciaContrato = dtFimVigenciaContrato;
    }
    public String getNmContratado() {
        return nmContratado;
    }
    public void setNmContratado(String nmContratado) {
        this.nmContratado = nmContratado;
    }
    public String getNuDocumentoContratado() {
        return nuDocumentoContratado;
    }
    public void setNuDocumentoContratado(String nuDocumentoContratado) {
        this.nuDocumentoContratado = nuDocumentoContratado;
    }
    public String getNmFiscal() {
        return nmFiscal;
    }
    public void setNmFiscal(String nmFiscal) {
        this.nmFiscal = nmFiscal;
    }
    public String getDsQualificador() {
        return dsQualificador;
    }
    public void setDsQualificador(String dsQualificador) {
        this.dsQualificador = dsQualificador;
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