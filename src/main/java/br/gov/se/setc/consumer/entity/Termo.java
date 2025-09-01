package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Entidade JPA para Termo (Convênios) da SEFAZ
 * Tabela: consumer_sefaz.termo
 */
@Entity
@Table(name = "termo", schema = "consumer_sefaz")
public class Termo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_convenio", nullable = false, unique = true)
    private Long cdConvenio;
    @Column(name = "cd_unidade_gestora", length = 20)
    private String cdUnidadeGestora;
    @Column(name = "cd_gestao", length = 20)
    private String cdGestao;
    @Column(name = "sg_unidade_gestora", length = 50)
    private String sgUnidadeGestora;
    @Column(name = "nm_convenio", length = 300)
    private String nmConvenio;
    @Column(name = "ds_objeto_convenio", columnDefinition = "TEXT")
    private String dsObjetoConvenio;
    @Column(name = "dt_celebracao_convenio")
    private LocalDate dtCelebracaoConvenio;
    @Column(name = "dt_inicio_vigencia_conv")
    private LocalDate dtInicioVigenciaConv;
    @Column(name = "dt_fim_vigencia_conv")
    private LocalDate dtFimVigenciaConv;
    @Column(name = "dt_publicacao_convenio")
    private LocalDate dtPublicacaoConvenio;
    @Column(name = "nu_doc_oficial_convenio", length = 40)
    private String nuDocOficialConvenio;
    @Column(name = "tx_ident_original_conv", length = 60)
    private String txIdentOriginalConv;
    @Column(name = "tx_observacao_convenio", columnDefinition = "TEXT")
    private String txObservacaoConvenio;
    @Column(name = "cd_efetivacao_usuario", length = 20)
    private String cdEfetivacaoUsuario;
    @Column(name = "cd_convenio_situacao", length = 2)
    private String cdConvenioSituacao;
    @Column(name = "cd_area_atuacao")
    private Integer cdAreaAtuacao;
    @Column(name = "in_local_publicacao_conv", length = 2)
    private String inLocalPublicacaoConv;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public Termo() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCdConvenio() {
        return cdConvenio;
    }
    public void setCdConvenio(Long cdConvenio) {
        this.cdConvenio = cdConvenio;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public String getSgUnidadeGestora() {
        return sgUnidadeGestora;
    }
    public void setSgUnidadeGestora(String sgUnidadeGestora) {
        this.sgUnidadeGestora = sgUnidadeGestora;
    }
    public String getNmConvenio() {
        return nmConvenio;
    }
    public void setNmConvenio(String nmConvenio) {
        this.nmConvenio = nmConvenio;
    }
    public String getDsObjetoConvenio() {
        return dsObjetoConvenio;
    }
    public void setDsObjetoConvenio(String dsObjetoConvenio) {
        this.dsObjetoConvenio = dsObjetoConvenio;
    }
    public LocalDate getDtCelebracaoConvenio() {
        return dtCelebracaoConvenio;
    }
    public void setDtCelebracaoConvenio(LocalDate dtCelebracaoConvenio) {
        this.dtCelebracaoConvenio = dtCelebracaoConvenio;
    }
    public LocalDate getDtInicioVigenciaConv() {
        return dtInicioVigenciaConv;
    }
    public void setDtInicioVigenciaConv(LocalDate dtInicioVigenciaConv) {
        this.dtInicioVigenciaConv = dtInicioVigenciaConv;
    }
    public LocalDate getDtFimVigenciaConv() {
        return dtFimVigenciaConv;
    }
    public void setDtFimVigenciaConv(LocalDate dtFimVigenciaConv) {
        this.dtFimVigenciaConv = dtFimVigenciaConv;
    }
    public LocalDate getDtPublicacaoConvenio() {
        return dtPublicacaoConvenio;
    }
    public void setDtPublicacaoConvenio(LocalDate dtPublicacaoConvenio) {
        this.dtPublicacaoConvenio = dtPublicacaoConvenio;
    }
    public String getNuDocOficialConvenio() {
        return nuDocOficialConvenio;
    }
    public void setNuDocOficialConvenio(String nuDocOficialConvenio) {
        this.nuDocOficialConvenio = nuDocOficialConvenio;
    }
    public String getTxIdentOriginalConv() {
        return txIdentOriginalConv;
    }
    public void setTxIdentOriginalConv(String txIdentOriginalConv) {
        this.txIdentOriginalConv = txIdentOriginalConv;
    }
    public String getTxObservacaoConvenio() {
        return txObservacaoConvenio;
    }
    public void setTxObservacaoConvenio(String txObservacaoConvenio) {
        this.txObservacaoConvenio = txObservacaoConvenio;
    }
    public String getCdEfetivacaoUsuario() {
        return cdEfetivacaoUsuario;
    }
    public void setCdEfetivacaoUsuario(String cdEfetivacaoUsuario) {
        this.cdEfetivacaoUsuario = cdEfetivacaoUsuario;
    }
    public String getCdConvenioSituacao() {
        return cdConvenioSituacao;
    }
    public void setCdConvenioSituacao(String cdConvenioSituacao) {
        this.cdConvenioSituacao = cdConvenioSituacao;
    }
    public Integer getCdAreaAtuacao() {
        return cdAreaAtuacao;
    }
    public void setCdAreaAtuacao(Integer cdAreaAtuacao) {
        this.cdAreaAtuacao = cdAreaAtuacao;
    }
    public String getInLocalPublicacaoConv() {
        return inLocalPublicacaoConv;
    }
    public void setInLocalPublicacaoConv(String inLocalPublicacaoConv) {
        this.inLocalPublicacaoConv = inLocalPublicacaoConv;
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
        return "Termo{" +
                "id=" + id +
                ", cdConvenio=" + cdConvenio +
                ", cdUnidadeGestora='" + cdUnidadeGestora + '\'' +
                ", nmConvenio='" + nmConvenio + '\'' +
                ", cdConvenioSituacao='" + cdConvenioSituacao + '\'' +
                ", dtInicioVigenciaConv=" + dtInicioVigenciaConv +
                ", dtFimVigenciaConv=" + dtFimVigenciaConv +
                '}';
    }
}