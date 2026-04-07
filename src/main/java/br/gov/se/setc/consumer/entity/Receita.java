package br.gov.se.setc.consumer.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "receita", schema = "consumer_sefaz")
public class Receita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "dt_fim_vigencia_convenio")
    private LocalDate dtFimVigenciaConvenio;
    @Column(name = "cd_convenio")
    private Integer cdConvenio;
    @Column(name = "cd_unidade_gestora")
    private String cdUnidadeGestora;
    @Column(name = "nm_concedente")
    private String nmConcedente;
    @Column(name = "in_convenio_ficha_ingresso")
    private String inConvenioFichaIngresso;
    @Column(name = "dt_publicacao_convenio")
    private LocalDate dtPublicacaoConvenio;
    @Column(name = "ds_objeto_convenio", length = 1000)
    private String dsObjetoConvenio;
    @Column(name = "vl_concedente_convenio", precision = 15, scale = 2)
    private BigDecimal vlConcedenteConvenio;
    @Column(name = "cd_gestao")
    private String cdGestao;
    @Column(name = "tx_ident_original_convenio")
    private String txIdentOriginalConvenio;
    @Column(name = "cd_concedente_pessoa")
    private Integer cdConcedentePessoa;
    @Column(name = "cd_efetivacao_usuario")
    private String cdEfetivacaoUsuario;
    @Column(name = "tx_observacao_convenio", length = 2000)
    private String txObservacaoConvenio;
    @Column(name = "cd_beneficiario_pessoa")
    private Integer cdBeneficiarioPessoa;
    @Column(name = "cd_convenio_situacao")
    private String cdConvenioSituacao;
    @Column(name = "cd_area_atuacao")
    private Integer cdAreaAtuacao;
    @Column(name = "dt_lancamento_convenio")
    private LocalDate dtLancamentoConvenio;
    @Column(name = "dt_prazo_prest_contas_convenio")
    private LocalDate dtPrazoPrestContasConvenio;
    @Column(name = "nm_beneficiario")
    private String nmBeneficiario;
    @Column(name = "dt_celebracao_convenio")
    private LocalDate dtCelebracaoConvenio;
    @Column(name = "dt_inicio_vigencia_convenio")
    private LocalDate dtInicioVigenciaConvenio;
    @Column(name = "sg_unidade_gestora")
    private String sgUnidadeGestora;
    @Column(name = "nm_convenio")
    private String nmConvenio;
    @Column(name = "in_local_publicacao_convenio")
    private String inLocalPublicacaoConvenio;
    @Column(name = "vl_contrapartida_convenio", precision = 15, scale = 2)
    private BigDecimal vlContrapartidaConvenio;
    @Column(name = "in_convenio_empenho_ingresso")
    private String inConvenioEmpenhoIngresso;
    @Column(name = "sq_unidade_gestora_gestao")
    private Integer sqUnidadeGestoraGestao;
    public Receita() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDtFimVigenciaConvenio() {
        return dtFimVigenciaConvenio;
    }
    public void setDtFimVigenciaConvenio(LocalDate dtFimVigenciaConvenio) {
        this.dtFimVigenciaConvenio = dtFimVigenciaConvenio;
    }
    public Integer getCdConvenio() {
        return cdConvenio;
    }
    public void setCdConvenio(Integer cdConvenio) {
        this.cdConvenio = cdConvenio;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public String getNmConcedente() {
        return nmConcedente;
    }
    public void setNmConcedente(String nmConcedente) {
        this.nmConcedente = nmConcedente;
    }
    public String getInConvenioFichaIngresso() {
        return inConvenioFichaIngresso;
    }
    public void setInConvenioFichaIngresso(String inConvenioFichaIngresso) {
        this.inConvenioFichaIngresso = inConvenioFichaIngresso;
    }
    public LocalDate getDtPublicacaoConvenio() {
        return dtPublicacaoConvenio;
    }
    public void setDtPublicacaoConvenio(LocalDate dtPublicacaoConvenio) {
        this.dtPublicacaoConvenio = dtPublicacaoConvenio;
    }
    public String getDsObjetoConvenio() {
        return dsObjetoConvenio;
    }
    public void setDsObjetoConvenio(String dsObjetoConvenio) {
        this.dsObjetoConvenio = dsObjetoConvenio;
    }
    public BigDecimal getVlConcedenteConvenio() {
        return vlConcedenteConvenio;
    }
    public void setVlConcedenteConvenio(BigDecimal vlConcedenteConvenio) {
        this.vlConcedenteConvenio = vlConcedenteConvenio;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public String getTxIdentOriginalConvenio() {
        return txIdentOriginalConvenio;
    }
    public void setTxIdentOriginalConvenio(String txIdentOriginalConvenio) {
        this.txIdentOriginalConvenio = txIdentOriginalConvenio;
    }
    public Integer getCdConcedentePessoa() {
        return cdConcedentePessoa;
    }
    public void setCdConcedentePessoa(Integer cdConcedentePessoa) {
        this.cdConcedentePessoa = cdConcedentePessoa;
    }
    public String getCdEfetivacaoUsuario() {
        return cdEfetivacaoUsuario;
    }
    public void setCdEfetivacaoUsuario(String cdEfetivacaoUsuario) {
        this.cdEfetivacaoUsuario = cdEfetivacaoUsuario;
    }
    public String getTxObservacaoConvenio() {
        return txObservacaoConvenio;
    }
    public void setTxObservacaoConvenio(String txObservacaoConvenio) {
        this.txObservacaoConvenio = txObservacaoConvenio;
    }
    public Integer getCdBeneficiarioPessoa() {
        return cdBeneficiarioPessoa;
    }
    public void setCdBeneficiarioPessoa(Integer cdBeneficiarioPessoa) {
        this.cdBeneficiarioPessoa = cdBeneficiarioPessoa;
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
    public LocalDate getDtLancamentoConvenio() {
        return dtLancamentoConvenio;
    }
    public void setDtLancamentoConvenio(LocalDate dtLancamentoConvenio) {
        this.dtLancamentoConvenio = dtLancamentoConvenio;
    }
    public LocalDate getDtPrazoPrestContasConvenio() {
        return dtPrazoPrestContasConvenio;
    }
    public void setDtPrazoPrestContasConvenio(LocalDate dtPrazoPrestContasConvenio) {
        this.dtPrazoPrestContasConvenio = dtPrazoPrestContasConvenio;
    }
    public String getNmBeneficiario() {
        return nmBeneficiario;
    }
    public void setNmBeneficiario(String nmBeneficiario) {
        this.nmBeneficiario = nmBeneficiario;
    }
    public LocalDate getDtCelebracaoConvenio() {
        return dtCelebracaoConvenio;
    }
    public void setDtCelebracaoConvenio(LocalDate dtCelebracaoConvenio) {
        this.dtCelebracaoConvenio = dtCelebracaoConvenio;
    }
    public LocalDate getDtInicioVigenciaConvenio() {
        return dtInicioVigenciaConvenio;
    }
    public void setDtInicioVigenciaConvenio(LocalDate dtInicioVigenciaConvenio) {
        this.dtInicioVigenciaConvenio = dtInicioVigenciaConvenio;
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
    public String getInLocalPublicacaoConvenio() {
        return inLocalPublicacaoConvenio;
    }
    public void setInLocalPublicacaoConvenio(String inLocalPublicacaoConvenio) {
        this.inLocalPublicacaoConvenio = inLocalPublicacaoConvenio;
    }
    public BigDecimal getVlContrapartidaConvenio() {
        return vlContrapartidaConvenio;
    }
    public void setVlContrapartidaConvenio(BigDecimal vlContrapartidaConvenio) {
        this.vlContrapartidaConvenio = vlContrapartidaConvenio;
    }
    public String getInConvenioEmpenhoIngresso() {
        return inConvenioEmpenhoIngresso;
    }
    public void setInConvenioEmpenhoIngresso(String inConvenioEmpenhoIngresso) {
        this.inConvenioEmpenhoIngresso = inConvenioEmpenhoIngresso;
    }
    public Integer getSqUnidadeGestoraGestao() {
        return sqUnidadeGestoraGestao;
    }
    public void setSqUnidadeGestoraGestao(Integer sqUnidadeGestoraGestao) {
        this.sqUnidadeGestoraGestao = sqUnidadeGestoraGestao;
    }
}