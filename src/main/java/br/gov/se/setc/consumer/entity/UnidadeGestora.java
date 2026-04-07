package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "unidade_gestora", schema ="consumer_sefaz")
public class UnidadeGestora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nmUnidadeGestora;
    private String sgUnidadeGestora;
    private String cdUnidadeGestora;
    private String sgTipoUnidadeGestora;
    public UnidadeGestora(String nmUnidadeGestora, String sgUnidadeGestora, String cdUnidadeGestora, String sgTipoUnidadeGestora) {
        this.nmUnidadeGestora = nmUnidadeGestora;
        this.sgUnidadeGestora = sgUnidadeGestora;
        this.cdUnidadeGestora = cdUnidadeGestora;
        this.sgTipoUnidadeGestora = sgTipoUnidadeGestora;
    }
    public String getNmUnidadeGestora() {
        return nmUnidadeGestora;
    }
    public void setNmUnidadeGestora(String nmUnidadeGestora) {
        this.nmUnidadeGestora = nmUnidadeGestora;
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
    public String getSgTipoUnidadeGestora() {
        return sgTipoUnidadeGestora;
    }
    public void setSgTipoUnidadeGestora(String sgTipoUnidadeGestora) {
        this.sgTipoUnidadeGestora = sgTipoUnidadeGestora;
    }
}