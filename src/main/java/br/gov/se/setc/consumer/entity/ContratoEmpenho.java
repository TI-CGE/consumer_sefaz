package br.gov.se.setc.consumer.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "contrato_empenho", schema = "consumer_sefaz")
public class ContratoEmpenho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cd_solicitacao_compra")
    private String cdSolicitacaoCompra;
    @Column(name = "ug_cd")
    private String ugCd;
    @Column(name = "ds_resumida_solicitacao_compra", length = 300)
    private String dsResumidaSolicitacaoCompra;
    @Column(name = "cd_licitacao")
    private String cdLicitacao;
    @Column(name = "dt_ano_exercicio")
    private Integer dtAnoExercicio;
    @Column(name = "situacao")
    private String situacao;
    @Column(name = "nm_modalidade_licitacao", length = 100)
    private String nmModalidadeLicitacao;
    @Column(name = "crit_julg_licitacao", length = 100)
    private String critJulgLicitacao;
    @Column(name = "natureza_objeto_licitacao", length = 100)
    private String naturezaObjetoLicitacao;
    @Column(name = "ug_se")
    private String ugSe;
    @Column(name = "doc_se", length = 100)
    private String docSe;
    @Column(name = "doc_referencia_ne", length = 100)
    private String docReferenciaNe;
    @Column(name = "valor_se", precision = 18, scale = 2)
    private BigDecimal valorSe;
    @Column(name = "ug_ne")
    private String ugNe;
    @Column(name = "doc_ne", length = 100)
    private String docNe;
    @Column(name = "doc_credor_ne", length = 100)
    private String docCredorNe;
    @Column(name = "nm_credor_ne", length = 200)
    private String nmCredorNe;
    @Column(name = "tipo_empenho")
    private String tipoEmpenho;
    @Column(name = "vl_original_ne", precision = 18, scale = 2)
    private BigDecimal vlOriginalNe;
    @Column(name = "vltotal_reforco_ne", precision = 18, scale = 2)
    private BigDecimal vltotalReforcoNe;
    @Column(name = "vltotal_anulado_ne", precision = 18, scale = 2)
    private BigDecimal vltotalAnuladoNe;
    @Column(name = "vltotal_liquidado_ne", precision = 18, scale = 2)
    private BigDecimal vltotalLiquidadoNe;
    @Column(name = "vl_total_pago_ne", precision = 18, scale = 2)
    private BigDecimal vlTotalPagoNe;
    @Column(name = "cd_contrato")
    private String cdContrato;
    @Column(name = "tipo_contrato")
    private String tipoContrato;
    @Column(name = "vl_contrato", precision = 18, scale = 2)
    private BigDecimal vlContrato;
    @Column(name = "dt_inicio_vigencia_contrato")
    private LocalDate dtInicioVigenciaContrato;
    @Column(name = "dt_fim_vigencia_contrato")
    private LocalDate dtFimVigenciaContrato;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public ContratoEmpenho() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCdSolicitacaoCompra() {
        return cdSolicitacaoCompra;
    }
    public void setCdSolicitacaoCompra(String cdSolicitacaoCompra) {
        this.cdSolicitacaoCompra = cdSolicitacaoCompra;
    }
    public String getUgCd() {
        return ugCd;
    }
    public void setUgCd(String ugCd) {
        this.ugCd = ugCd;
    }
    public String getDsResumidaSolicitacaoCompra() {
        return dsResumidaSolicitacaoCompra;
    }
    public void setDsResumidaSolicitacaoCompra(String dsResumidaSolicitacaoCompra) {
        this.dsResumidaSolicitacaoCompra = dsResumidaSolicitacaoCompra;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public Integer getDtAnoExercicio() {
        return dtAnoExercicio;
    }
    public void setDtAnoExercicio(Integer dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }
    public String getSituacao() {
        return situacao;
    }
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
    public String getNmModalidadeLicitacao() {
        return nmModalidadeLicitacao;
    }
    public void setNmModalidadeLicitacao(String nmModalidadeLicitacao) {
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
    }
    public String getCritJulgLicitacao() {
        return critJulgLicitacao;
    }
    public void setCritJulgLicitacao(String critJulgLicitacao) {
        this.critJulgLicitacao = critJulgLicitacao;
    }
    public String getNaturezaObjetoLicitacao() {
        return naturezaObjetoLicitacao;
    }
    public void setNaturezaObjetoLicitacao(String naturezaObjetoLicitacao) {
        this.naturezaObjetoLicitacao = naturezaObjetoLicitacao;
    }
    public String getUgSe() {
        return ugSe;
    }
    public void setUgSe(String ugSe) {
        this.ugSe = ugSe;
    }
    public String getDocSe() {
        return docSe;
    }
    public void setDocSe(String docSe) {
        this.docSe = docSe;
    }
    public String getDocReferenciaNe() {
        return docReferenciaNe;
    }
    public void setDocReferenciaNe(String docReferenciaNe) {
        this.docReferenciaNe = docReferenciaNe;
    }
    public BigDecimal getValorSe() {
        return valorSe;
    }
    public void setValorSe(BigDecimal valorSe) {
        this.valorSe = valorSe;
    }
    public String getUgNe() {
        return ugNe;
    }
    public void setUgNe(String ugNe) {
        this.ugNe = ugNe;
    }
    public String getDocNe() {
        return docNe;
    }
    public void setDocNe(String docNe) {
        this.docNe = docNe;
    }
    public String getDocCredorNe() {
        return docCredorNe;
    }
    public void setDocCredorNe(String docCredorNe) {
        this.docCredorNe = docCredorNe;
    }
    public String getNmCredorNe() {
        return nmCredorNe;
    }
    public void setNmCredorNe(String nmCredorNe) {
        this.nmCredorNe = nmCredorNe;
    }
    public String getTipoEmpenho() {
        return tipoEmpenho;
    }
    public void setTipoEmpenho(String tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }
    public BigDecimal getVlOriginalNe() {
        return vlOriginalNe;
    }
    public void setVlOriginalNe(BigDecimal vlOriginalNe) {
        this.vlOriginalNe = vlOriginalNe;
    }
    public BigDecimal getVltotalReforcoNe() {
        return vltotalReforcoNe;
    }
    public void setVltotalReforcoNe(BigDecimal vltotalReforcoNe) {
        this.vltotalReforcoNe = vltotalReforcoNe;
    }
    public BigDecimal getVltotalAnuladoNe() {
        return vltotalAnuladoNe;
    }
    public void setVltotalAnuladoNe(BigDecimal vltotalAnuladoNe) {
        this.vltotalAnuladoNe = vltotalAnuladoNe;
    }
    public BigDecimal getVltotalLiquidadoNe() {
        return vltotalLiquidadoNe;
    }
    public void setVltotalLiquidadoNe(BigDecimal vltotalLiquidadoNe) {
        this.vltotalLiquidadoNe = vltotalLiquidadoNe;
    }
    public BigDecimal getVlTotalPagoNe() {
        return vlTotalPagoNe;
    }
    public void setVlTotalPagoNe(BigDecimal vlTotalPagoNe) {
        this.vlTotalPagoNe = vlTotalPagoNe;
    }
    public String getCdContrato() {
        return cdContrato;
    }
    public void setCdContrato(String cdContrato) {
        this.cdContrato = cdContrato;
    }
    public String getTipoContrato() {
        return tipoContrato;
    }
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
    public BigDecimal getVlContrato() {
        return vlContrato;
    }
    public void setVlContrato(BigDecimal vlContrato) {
        this.vlContrato = vlContrato;
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