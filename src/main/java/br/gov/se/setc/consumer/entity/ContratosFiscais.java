package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;
@Entity
@Table(name = "contratos_fiscais", schema = "consumer_sefaz")
public class ContratosFiscais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sgUnidadeGestora;
    private String cdUnidadeGestora;
    private Integer dtAnoExercicio;
    private String cdContrato;
    private String cdLicitacao;
    private LocalDate dtInicioVigenciaContrato;
    private LocalDate dtFimVigenciaContrato;
    private String nmContratado;
    private String nuDocumentoContratado;
    private String nmFiscal;
    private String dsQualificador;
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
}