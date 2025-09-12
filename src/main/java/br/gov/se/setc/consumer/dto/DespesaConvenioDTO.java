package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DTO para consumo da API de Despesa de Convênios da SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/despesa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DespesaConvenioDTO extends EndpontSefaz {
    @JsonProperty("dtFimVigenciaConvenio")
    private LocalDate dtFimVigenciaConvenio;
    @JsonProperty("dtPublicacaoConvenio")
    private LocalDate dtPublicacaoConvenio;
    @JsonProperty("dtLancamentoConvenio")
    private LocalDate dtLancamentoConvenio;
    @JsonProperty("dtPrazoPrestContasConvenio")
    private LocalDate dtPrazoPrestContasConvenio;
    @JsonProperty("dtCelebracaoConvenio")
    private LocalDate dtCelebracaoConvenio;
    @JsonProperty("dtInicioVigenciaConvenio")
    private LocalDate dtInicioVigenciaConvenio;
    @JsonProperty("cdConvenio")
    private Long cdConvenio;
    @JsonProperty("vlConcedenteConvenio")
    private BigDecimal vlConcedenteConvenio;
    @JsonProperty("cdConcedentePessoa")
    private Long cdConcedentePessoa;
    @JsonProperty("cdBeneficiarioPessoa")
    private Long cdBeneficiarioPessoa;
    @JsonProperty("cdAreaAtuacao")
    private Integer cdAreaAtuacao;
    @JsonProperty("vlContrapartidaConvenio")
    private BigDecimal vlContrapartidaConvenio;
    @JsonProperty("sqUnidadeGestoraGestao")
    private Long sqUnidadeGestoraGestao;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("nmConcedente")
    private String nmConcedente;
    @JsonProperty("inConvenioFichaIngresso")
    private String inConvenioFichaIngresso;
    @JsonProperty("dsObjetoConvenio")
    private String dsObjetoConvenio;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("txIdentOriginalConvenio")
    private String txIdentOriginalConvenio;
    @JsonProperty("cdEfetivacaoUsuario")
    private String cdEfetivacaoUsuario;
    @JsonProperty("txObservacaoConvenio")
    private String txObservacaoConvenio;
    @JsonProperty("cdConvenioSituacao")
    private String cdConvenioSituacao;
    @JsonProperty("nmBeneficiario")
    private String nmBeneficiario;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("nmConvenio")
    private String nmConvenio;
    @JsonProperty("inLocalPublicacaoConvenio")
    private String inLocalPublicacaoConvenio;
    @JsonProperty("inConvenioEmpenhoIngresso")
    private String inConvenioEmpenhoIngresso;
    private String cdUnidadeGestoraFiltro;
    private String cdGestaoFiltro;
    private Integer nuAnoLancamentoFiltro;
    private Integer nuMesLancamentoFiltro;
    public DespesaConvenioDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.convenio_despesa";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/despesa";
        nomeDataInicialPadraoFiltro = "dt_lancamento_convenio";
        nomeDataFinalPadraoFiltro = "dt_lancamento_convenio";
        dtAnoPadrao = null;
        parametrosRequeridos = false;
    }
    @Override
    public void mapearCamposResposta() {
        // Mapear todos os campos permitindo NULL quando não vierem da API
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

        // Campos de auditoria - sempre preenchidos automaticamente
        LocalDateTime now = LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        if (cdGestaoFiltro != null) camposParametros.put("cdGestao", cdGestaoFiltro);
        if (nuAnoLancamentoFiltro != null) camposParametros.put("nuAnoLancamento", nuAnoLancamentoFiltro);
        if (nuMesLancamentoFiltro != null) camposParametros.put("nuMesLancamento", nuMesLancamentoFiltro);
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("cdUnidadeGestora", ugCd);
        params.put("nuAnoLancamento", utilsService.getAnoAtual());
        params.put("nuMesLancamento", utilsService.getMesAtual());
        params.put("inVigente", "S");
        return params;
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("cdUnidadeGestora", ugCd);
        params.put("nuAnoLancamento", ano);
        return params;
    }
    public LocalDate getDtFimVigenciaConvenio() { return dtFimVigenciaConvenio; }
    public void setDtFimVigenciaConvenio(LocalDate dtFimVigenciaConvenio) { this.dtFimVigenciaConvenio = dtFimVigenciaConvenio; }
    public Long getCdConvenio() { return cdConvenio; }
    public void setCdConvenio(Long cdConvenio) { this.cdConvenio = cdConvenio; }
    public String getCdUnidadeGestora() { return cdUnidadeGestora; }
    public void setCdUnidadeGestora(String cdUnidadeGestora) { this.cdUnidadeGestora = cdUnidadeGestora; }
    public String getNmConcedente() { return nmConcedente; }
    public void setNmConcedente(String nmConcedente) { this.nmConcedente = nmConcedente; }
    public String getInConvenioFichaIngresso() { return inConvenioFichaIngresso; }
    public void setInConvenioFichaIngresso(String inConvenioFichaIngresso) { this.inConvenioFichaIngresso = inConvenioFichaIngresso; }
    public LocalDate getDtPublicacaoConvenio() { return dtPublicacaoConvenio; }
    public void setDtPublicacaoConvenio(LocalDate dtPublicacaoConvenio) { this.dtPublicacaoConvenio = dtPublicacaoConvenio; }
    public String getDsObjetoConvenio() { return dsObjetoConvenio; }
    public void setDsObjetoConvenio(String dsObjetoConvenio) { this.dsObjetoConvenio = dsObjetoConvenio; }
    public BigDecimal getVlConcedenteConvenio() { return vlConcedenteConvenio; }
    public void setVlConcedenteConvenio(BigDecimal vlConcedenteConvenio) { this.vlConcedenteConvenio = vlConcedenteConvenio; }
    public String getCdGestao() { return cdGestao; }
    public void setCdGestao(String cdGestao) { this.cdGestao = cdGestao; }
    public String getTxIdentOriginalConvenio() { return txIdentOriginalConvenio; }
    public void setTxIdentOriginalConvenio(String txIdentOriginalConvenio) { this.txIdentOriginalConvenio = txIdentOriginalConvenio; }
    public Long getCdConcedentePessoa() { return cdConcedentePessoa; }
    public void setCdConcedentePessoa(Long cdConcedentePessoa) { this.cdConcedentePessoa = cdConcedentePessoa; }
    public String getCdEfetivacaoUsuario() { return cdEfetivacaoUsuario; }
    public void setCdEfetivacaoUsuario(String cdEfetivacaoUsuario) { this.cdEfetivacaoUsuario = cdEfetivacaoUsuario; }
    public String getTxObservacaoConvenio() { return txObservacaoConvenio; }
    public void setTxObservacaoConvenio(String txObservacaoConvenio) { this.txObservacaoConvenio = txObservacaoConvenio; }
    public Long getCdBeneficiarioPessoa() { return cdBeneficiarioPessoa; }
    public void setCdBeneficiarioPessoa(Long cdBeneficiarioPessoa) { this.cdBeneficiarioPessoa = cdBeneficiarioPessoa; }
    public String getCdConvenioSituacao() { return cdConvenioSituacao; }
    public void setCdConvenioSituacao(String cdConvenioSituacao) { this.cdConvenioSituacao = cdConvenioSituacao; }
    public Integer getCdAreaAtuacao() { return cdAreaAtuacao; }
    public void setCdAreaAtuacao(Integer cdAreaAtuacao) { this.cdAreaAtuacao = cdAreaAtuacao; }
    public LocalDate getDtLancamentoConvenio() { return dtLancamentoConvenio; }
    public void setDtLancamentoConvenio(LocalDate dtLancamentoConvenio) { this.dtLancamentoConvenio = dtLancamentoConvenio; }
    public LocalDate getDtPrazoPrestContasConvenio() { return dtPrazoPrestContasConvenio; }
    public void setDtPrazoPrestContasConvenio(LocalDate dtPrazoPrestContasConvenio) { this.dtPrazoPrestContasConvenio = dtPrazoPrestContasConvenio; }
    public String getNmBeneficiario() { return nmBeneficiario; }
    public void setNmBeneficiario(String nmBeneficiario) { this.nmBeneficiario = nmBeneficiario; }
    public LocalDate getDtCelebracaoConvenio() { return dtCelebracaoConvenio; }
    public void setDtCelebracaoConvenio(LocalDate dtCelebracaoConvenio) { this.dtCelebracaoConvenio = dtCelebracaoConvenio; }
    public LocalDate getDtInicioVigenciaConvenio() { return dtInicioVigenciaConvenio; }
    public void setDtInicioVigenciaConvenio(LocalDate dtInicioVigenciaConvenio) { this.dtInicioVigenciaConvenio = dtInicioVigenciaConvenio; }
    public String getSgUnidadeGestora() { return sgUnidadeGestora; }
    public void setSgUnidadeGestora(String sgUnidadeGestora) { this.sgUnidadeGestora = sgUnidadeGestora; }
    public String getNmConvenio() { return nmConvenio; }
    public void setNmConvenio(String nmConvenio) { this.nmConvenio = nmConvenio; }
    public String getInLocalPublicacaoConvenio() { return inLocalPublicacaoConvenio; }
    public void setInLocalPublicacaoConvenio(String inLocalPublicacaoConvenio) { this.inLocalPublicacaoConvenio = inLocalPublicacaoConvenio; }
    public BigDecimal getVlContrapartidaConvenio() { return vlContrapartidaConvenio; }
    public void setVlContrapartidaConvenio(BigDecimal vlContrapartidaConvenio) { this.vlContrapartidaConvenio = vlContrapartidaConvenio; }
    public String getInConvenioEmpenhoIngresso() { return inConvenioEmpenhoIngresso; }
    public void setInConvenioEmpenhoIngresso(String inConvenioEmpenhoIngresso) { this.inConvenioEmpenhoIngresso = inConvenioEmpenhoIngresso; }
    public Long getSqUnidadeGestoraGestao() { return sqUnidadeGestoraGestao; }
    public void setSqUnidadeGestoraGestao(Long sqUnidadeGestoraGestao) { this.sqUnidadeGestoraGestao = sqUnidadeGestoraGestao; }
    public String getCdUnidadeGestoraFiltro() { return cdUnidadeGestoraFiltro; }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) { this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro; }
    public String getCdGestaoFiltro() { return cdGestaoFiltro; }
    public void setCdGestaoFiltro(String cdGestaoFiltro) { this.cdGestaoFiltro = cdGestaoFiltro; }
    public Integer getNuAnoLancamentoFiltro() { return nuAnoLancamentoFiltro; }
    public void setNuAnoLancamentoFiltro(Integer nuAnoLancamentoFiltro) { this.nuAnoLancamentoFiltro = nuAnoLancamentoFiltro; }
    public Integer getNuMesLancamentoFiltro() { return nuMesLancamentoFiltro; }
    public void setNuMesLancamentoFiltro(Integer nuMesLancamentoFiltro) { this.nuMesLancamentoFiltro = nuMesLancamentoFiltro; }
}