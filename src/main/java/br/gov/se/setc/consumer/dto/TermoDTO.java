package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DTO para consumo de dados de Termo (Convênios) da API de transparência da SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/termo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermoDTO extends EndpontSefaz {
    @JsonProperty("cdConvenio")
    private Long cdConvenio;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("nmConvenio")
    private String nmConvenio;
    @JsonProperty("dsObjetoConvenio")
    private String dsObjetoConvenio;
    @JsonProperty("dtCelebracaoConvenio")
    private LocalDate dtCelebracaoConvenio;
    @JsonProperty("dtInicioVigenciaConvenio")
    private LocalDate dtInicioVigenciaConvenio;
    @JsonProperty("dtFimVigenciaConvenio")
    private LocalDate dtFimVigenciaConvenio;
    @JsonProperty("dtPublicacaoConvenio")
    private LocalDate dtPublicacaoConvenio;
    @JsonProperty("nuDocOficialConvenio")
    private String nuDocOficialConvenio;
    @JsonProperty("txIdentOriginalConvenio")
    private String txIdentOriginalConvenio;
    @JsonProperty("txObservacaoConvenio")
    private String txObservacaoConvenio;
    @JsonProperty("cdEfetivacaoUsuario")
    private String cdEfetivacaoUsuario;
    @JsonProperty("cdConvenioSituacao")
    private String cdConvenioSituacao;
    @JsonProperty("cdAreaAtuacao")
    private Integer cdAreaAtuacao;
    @JsonProperty("inLocalPublicacaoConvenio")
    private String inLocalPublicacaoConvenio;
    private String cdUnidadeGestoraFiltro;
    private String cdGestaoFiltro;
    private String situacaoFiltro;
    private LocalDate vigenciaDe;
    private LocalDate vigenciaAte;
    public TermoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.termo";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/termo";
        nomeDataInicialPadraoFiltro = "dt_inicio_vigencia_conv";
        nomeDataFinalPadraoFiltro = "dt_fim_vigencia_conv";
        dtAnoPadrao = "dt_inicio_vigencia_conv";
        parametrosRequeridos = false;
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_convenio", cdConvenio);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("nm_convenio", nmConvenio);
        camposResposta.put("ds_objeto_convenio", dsObjetoConvenio);
        camposResposta.put("dt_celebracao_convenio", dtCelebracaoConvenio);
        camposResposta.put("dt_inicio_vigencia_conv", dtInicioVigenciaConvenio);
        camposResposta.put("dt_fim_vigencia_conv", dtFimVigenciaConvenio);
        camposResposta.put("dt_publicacao_convenio", dtPublicacaoConvenio);
        camposResposta.put("nu_doc_oficial_convenio", nuDocOficialConvenio);
        camposResposta.put("tx_ident_original_conv", txIdentOriginalConvenio);
        camposResposta.put("tx_observacao_convenio", txObservacaoConvenio);
        camposResposta.put("cd_efetivacao_usuario", cdEfetivacaoUsuario);
        camposResposta.put("cd_convenio_situacao", cdConvenioSituacao);
        camposResposta.put("cd_area_atuacao", cdAreaAtuacao);
        camposResposta.put("in_local_publicacao_conv", inLocalPublicacaoConvenio);
    }
    @Override
    protected void mapearParametros() {
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        camposParametros.put("cdGestao", cdGestaoFiltro);
        camposParametros.put("situacao", situacaoFiltro);
        camposParametros.put("vigenciaDe", vigenciaDe);
        camposParametros.put("vigenciaAte", vigenciaAte);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        return camposParametros;
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
    public LocalDate getDtInicioVigenciaConvenio() {
        return dtInicioVigenciaConvenio;
    }
    public void setDtInicioVigenciaConvenio(LocalDate dtInicioVigenciaConvenio) {
        this.dtInicioVigenciaConvenio = dtInicioVigenciaConvenio;
    }
    public LocalDate getDtFimVigenciaConvenio() {
        return dtFimVigenciaConvenio;
    }
    public void setDtFimVigenciaConvenio(LocalDate dtFimVigenciaConvenio) {
        this.dtFimVigenciaConvenio = dtFimVigenciaConvenio;
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
    public String getTxIdentOriginalConvenio() {
        return txIdentOriginalConvenio;
    }
    public void setTxIdentOriginalConvenio(String txIdentOriginalConvenio) {
        this.txIdentOriginalConvenio = txIdentOriginalConvenio;
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
    public String getInLocalPublicacaoConvenio() {
        return inLocalPublicacaoConvenio;
    }
    public void setInLocalPublicacaoConvenio(String inLocalPublicacaoConvenio) {
        this.inLocalPublicacaoConvenio = inLocalPublicacaoConvenio;
    }
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }
    public String getCdGestaoFiltro() {
        return cdGestaoFiltro;
    }
    public void setCdGestaoFiltro(String cdGestaoFiltro) {
        this.cdGestaoFiltro = cdGestaoFiltro;
    }
    public String getSituacaoFiltro() {
        return situacaoFiltro;
    }
    public void setSituacaoFiltro(String situacaoFiltro) {
        this.situacaoFiltro = situacaoFiltro;
    }
    public LocalDate getVigenciaDe() {
        return vigenciaDe;
    }
    public void setVigenciaDe(LocalDate vigenciaDe) {
        this.vigenciaDe = vigenciaDe;
    }
    public LocalDate getVigenciaAte() {
        return vigenciaAte;
    }
    public void setVigenciaAte(LocalDate vigenciaAte) {
        this.vigenciaAte = vigenciaAte;
    }
}