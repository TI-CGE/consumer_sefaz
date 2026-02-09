package br.gov.se.setc.consumer.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
public class ReceitaDTO extends EndpontSefaz {
    @JsonProperty("dtFimVigenciaConvenio")
    private LocalDate dtFimVigenciaConvenio;
    @JsonProperty("cdConvenio")
    private Integer cdConvenio;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("nmConcedente")
    private String nmConcedente;
    @JsonProperty("inConvenioFichaIngresso")
    private String inConvenioFichaIngresso;
    @JsonProperty("dtPublicacaoConvenio")
    private LocalDate dtPublicacaoConvenio;
    @JsonProperty("dsObjetoConvenio")
    private String dsObjetoConvenio;
    @JsonProperty("vlConcedenteConvenio")
    private BigDecimal vlConcedenteConvenio;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("txIdentOriginalConvenio")
    private String txIdentOriginalConvenio;
    @JsonProperty("cdConcedentePessoa")
    private Integer cdConcedentePessoa;
    @JsonProperty("cdEfetivacaoUsuario")
    private String cdEfetivacaoUsuario;
    @JsonProperty("txObservacaoConvenio")
    private String txObservacaoConvenio;
    @JsonProperty("cdBeneficiarioPessoa")
    private Integer cdBeneficiarioPessoa;
    @JsonProperty("cdConvenioSituacao")
    private String cdConvenioSituacao;
    @JsonProperty("cdAreaAtuacao")
    private Integer cdAreaAtuacao;
    @JsonProperty("dtLancamentoConvenio")
    private LocalDate dtLancamentoConvenio;
    @JsonProperty("dtPrazoPrestContasConvenio")
    private LocalDate dtPrazoPrestContasConvenio;
    @JsonProperty("nmBeneficiario")
    private String nmBeneficiario;
    @JsonProperty("dtCelebracaoConvenio")
    private LocalDate dtCelebracaoConvenio;
    @JsonProperty("dtInicioVigenciaConvenio")
    private LocalDate dtInicioVigenciaConvenio;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("nmConvenio")
    private String nmConvenio;
    @JsonProperty("inLocalPublicacaoConvenio")
    private String inLocalPublicacaoConvenio;
    @JsonProperty("vlContrapartidaConvenio")
    private BigDecimal vlContrapartidaConvenio;
    @JsonProperty("inConvenioEmpenhoIngresso")
    private String inConvenioEmpenhoIngresso;
    @JsonProperty("sqUnidadeGestoraGestao")
    private Integer sqUnidadeGestoraGestao;
    private Integer nuAnoCelebracaoFiltro;
    private Integer nuMesCelebracaoFiltro;
    public ReceitaDTO() {
        inicializarDadosEndpoint();
    }
    public ReceitaDTO(LocalDate dtFimVigenciaConvenio, Integer cdConvenio, String cdUnidadeGestora,
                     String nmConcedente, String inConvenioFichaIngresso, LocalDate dtPublicacaoConvenio,
                     String dsObjetoConvenio, BigDecimal vlConcedenteConvenio, String cdGestao,
                     String txIdentOriginalConvenio, Integer cdConcedentePessoa, String cdEfetivacaoUsuario,
                     String txObservacaoConvenio, Integer cdBeneficiarioPessoa, String cdConvenioSituacao,
                     Integer cdAreaAtuacao, LocalDate dtLancamentoConvenio, LocalDate dtPrazoPrestContasConvenio,
                     String nmBeneficiario, LocalDate dtCelebracaoConvenio, LocalDate dtInicioVigenciaConvenio,
                     String sgUnidadeGestora, String nmConvenio, String inLocalPublicacaoConvenio,
                     BigDecimal vlContrapartidaConvenio, String inConvenioEmpenhoIngresso, Integer sqUnidadeGestoraGestao) {
        this.dtFimVigenciaConvenio = dtFimVigenciaConvenio;
        this.cdConvenio = cdConvenio;
        this.cdUnidadeGestora = cdUnidadeGestora;
        this.nmConcedente = nmConcedente;
        this.inConvenioFichaIngresso = inConvenioFichaIngresso;
        this.dtPublicacaoConvenio = dtPublicacaoConvenio;
        this.dsObjetoConvenio = dsObjetoConvenio;
        this.vlConcedenteConvenio = vlConcedenteConvenio;
        this.cdGestao = cdGestao;
        this.txIdentOriginalConvenio = txIdentOriginalConvenio;
        this.cdConcedentePessoa = cdConcedentePessoa;
        this.cdEfetivacaoUsuario = cdEfetivacaoUsuario;
        this.txObservacaoConvenio = txObservacaoConvenio;
        this.cdBeneficiarioPessoa = cdBeneficiarioPessoa;
        this.cdConvenioSituacao = cdConvenioSituacao;
        this.cdAreaAtuacao = cdAreaAtuacao;
        this.dtLancamentoConvenio = dtLancamentoConvenio;
        this.dtPrazoPrestContasConvenio = dtPrazoPrestContasConvenio;
        this.nmBeneficiario = nmBeneficiario;
        this.dtCelebracaoConvenio = dtCelebracaoConvenio;
        this.dtInicioVigenciaConvenio = dtInicioVigenciaConvenio;
        this.sgUnidadeGestora = sgUnidadeGestora;
        this.nmConvenio = nmConvenio;
        this.inLocalPublicacaoConvenio = inLocalPublicacaoConvenio;
        this.vlContrapartidaConvenio = vlContrapartidaConvenio;
        this.inConvenioEmpenhoIngresso = inConvenioEmpenhoIngresso;
        this.sqUnidadeGestoraGestao = sqUnidadeGestoraGestao;
        inicializarDadosEndpoint();
        mapearParametros();
        mapearCamposResposta();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.receita";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/receita";
        nomeDataInicialPadraoFiltro = "dt_celebracao_convenio";
        nomeDataFinalPadraoFiltro = "dt_celebracao_convenio";
        dtAnoPadrao = null;
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("dt_fim_vigencia_convenio", dtFimVigenciaConvenio);
        camposResposta.put("cd_convenio", cdConvenio);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("nm_concedente", nmConcedente);
        camposResposta.put("in_convenio_ficha_ingresso", inConvenioFichaIngresso);
        camposResposta.put("dt_publicacao_convenio", dtPublicacaoConvenio);
        camposResposta.put("ds_objeto_convenio", dsObjetoConvenio);
        camposResposta.put("vl_concedente_convenio", vlConcedenteConvenio);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("tx_ident_original_convenio", txIdentOriginalConvenio);
        camposResposta.put("cd_concedente_pessoa", cdConcedentePessoa);
        camposResposta.put("cd_efetivacao_usuario", cdEfetivacaoUsuario);
        camposResposta.put("tx_observacao_convenio", txObservacaoConvenio);
        camposResposta.put("cd_beneficiario_pessoa", cdBeneficiarioPessoa);
        camposResposta.put("cd_convenio_situacao", cdConvenioSituacao);
        camposResposta.put("cd_area_atuacao", cdAreaAtuacao);
        camposResposta.put("dt_lancamento_convenio", dtLancamentoConvenio);
        camposResposta.put("dt_prazo_prest_contas_convenio", dtPrazoPrestContasConvenio);
        camposResposta.put("nm_beneficiario", nmBeneficiario);
        camposResposta.put("dt_celebracao_convenio", dtCelebracaoConvenio);
        camposResposta.put("dt_inicio_vigencia_convenio", dtInicioVigenciaConvenio);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("nm_convenio", nmConvenio);
        camposResposta.put("in_local_publicacao_convenio", inLocalPublicacaoConvenio);
        camposResposta.put("vl_contrapartida_convenio", vlContrapartidaConvenio);
        camposResposta.put("in_convenio_empenho_ingresso", inConvenioEmpenhoIngresso);
        camposResposta.put("sq_unidade_gestora_gestao", sqUnidadeGestoraGestao);
    }
    @Override
    protected void mapearParametros() {
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("cdConvenio", cdConvenio);
        camposParametros.put("cdConvenioSituacao", cdConvenioSituacao);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("nuAnoCelebracao", nuAnoCelebracaoFiltro != null ? nuAnoCelebracaoFiltro : utilsService.getAnoAtual());
        camposParametros.put("nuMesCelebracao", nuMesCelebracaoFiltro != null ? nuMesCelebracaoFiltro : utilsService.getMesAtual());
        camposParametros.put("inVigente", "S");
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("nuAnoCelebracao", ano);
        return camposParametros;
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
    public Integer getNuAnoCelebracaoFiltro() {
        return nuAnoCelebracaoFiltro;
    }
    public void setNuAnoCelebracaoFiltro(Integer nuAnoCelebracaoFiltro) {
        this.nuAnoCelebracaoFiltro = nuAnoCelebracaoFiltro;
    }
    public Integer getNuMesCelebracaoFiltro() {
        return nuMesCelebracaoFiltro;
    }
    public void setNuMesCelebracaoFiltro(Integer nuMesCelebracaoFiltro) {
        this.nuMesCelebracaoFiltro = nuMesCelebracaoFiltro;
    }
}