package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "base_despesa_licitacao", schema = "consumer_sefaz")
public class BaseDespesaLicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nu_processo", length = 50)
    private String nuProcesso;
    @Column(name = "si_licitacao", length = 50)
    private String siLicitacao;
    @Column(name = "nu_documento", length = 30)
    private String nuDocumento;
    @Column(name = "nm_razao_social_ug", length = 200)
    private String nmRazaoSocialUg;
    @Column(name = "nm_razao_social_fornecedor", length = 200)
    private String nmRazaoSocialFornecedor;
    @Column(name = "cd_licitacao", length = 50)
    private String cdLicitacao;
    @Column(name = "vl_licitacao", precision = 18, scale = 2)
    private BigDecimal vlLicitacao;
    @Column(name = "sg_unidade_gestora", length = 30)
    private String sgUnidadeGestora;
    @Column(name = "cd_unidade_gestora", length = 30)
    private String cdUnidadeGestora;
    @Column(name = "nm_modalidade", length = 100)
    private String nmModalidade;
    @Column(name = "dt_homologacao")
    private LocalDate dtHomologacao;
    @Column(name = "vl_estimado", precision = 18, scale = 2)
    private BigDecimal vlEstimado;
    @Column(name = "ds_objeto", columnDefinition = "TEXT")
    private String dsObjeto;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public BaseDespesaLicitacao() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNuProcesso() {
        return nuProcesso;
    }
    public void setNuProcesso(String nuProcesso) {
        this.nuProcesso = nuProcesso;
    }
    public String getSiLicitacao() {
        return siLicitacao;
    }
    public void setSiLicitacao(String siLicitacao) {
        this.siLicitacao = siLicitacao;
    }
    public String getNuDocumento() {
        return nuDocumento;
    }
    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }
    public String getNmRazaoSocialUg() {
        return nmRazaoSocialUg;
    }
    public void setNmRazaoSocialUg(String nmRazaoSocialUg) {
        this.nmRazaoSocialUg = nmRazaoSocialUg;
    }
    public String getNmRazaoSocialFornecedor() {
        return nmRazaoSocialFornecedor;
    }
    public void setNmRazaoSocialFornecedor(String nmRazaoSocialFornecedor) {
        this.nmRazaoSocialFornecedor = nmRazaoSocialFornecedor;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public BigDecimal getVlLicitacao() {
        return vlLicitacao;
    }
    public void setVlLicitacao(BigDecimal vlLicitacao) {
        this.vlLicitacao = vlLicitacao;
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
    public String getNmModalidade() {
        return nmModalidade;
    }
    public void setNmModalidade(String nmModalidade) {
        this.nmModalidade = nmModalidade;
    }
    public LocalDate getDtHomologacao() {
        return dtHomologacao;
    }
    public void setDtHomologacao(LocalDate dtHomologacao) {
        this.dtHomologacao = dtHomologacao;
    }
    public BigDecimal getVlEstimado() {
        return vlEstimado;
    }
    public void setVlEstimado(BigDecimal vlEstimado) {
        this.vlEstimado = vlEstimado;
    }
    public String getDsObjeto() {
        return dsObjeto;
    }
    public void setDsObjeto(String dsObjeto) {
        this.dsObjeto = dsObjeto;
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