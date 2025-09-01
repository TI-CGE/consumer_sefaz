package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * DTO para consumo da API de Contrato-Empenho do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-empenho
 */
public class ContratoEmpenhoDTO extends EndpontSefaz {
    @JsonProperty("cdSolicitacaoCompra")
    private String cdSolicitacaoCompra;
    @JsonProperty("ugCd")
    private String ugCd;
    @JsonProperty("dsResumidaSolicitacaoCompra")
    private String dsResumidaSolicitacaoCompra;
    @JsonProperty("cdLicitacao")
    private String cdLicitacao;
    @JsonProperty("dtAnoExercicio")
    private Integer dtAnoExercicio;
    @JsonProperty("siLicitacao")
    private String situacao;
    @JsonProperty("nmModalidadeLicitacao")
    private String nmModalidadeLicitacao;
    @JsonProperty("critJulgLicitacao")
    private String critJulgLicitacao;
    @JsonProperty("naturezaObjetoLicitacao")
    private String naturezaObjetoLicitacao;
    @JsonProperty("cdUnidadeGestoraSE")
    private String ugSe;
    @JsonProperty("docSe")
    private String docSe;
    @JsonProperty("docReferenciaNe")
    private String docReferenciaNe;
    @JsonProperty("vlSE")
    private BigDecimal valorSe;
    @JsonProperty("cdUnidadeGestoraNE")
    private String ugNe;
    @JsonProperty("docNe")
    private String docNe;
    @JsonProperty("docCredorNe")
    private String docCredorNe;
    @JsonProperty("nmCredorNe")
    private String nmCredorNe;
    @JsonProperty("tpEmpenho")
    private String tipoEmpenho;
    @JsonProperty("vlOriginalNE")
    private BigDecimal vlOriginalNe;
    @JsonProperty("vlTotalReforcoNE")
    private BigDecimal vltotalReforcoNe;
    @JsonProperty("vlTotalAnuladoNE")
    private BigDecimal vltotalAnuladoNe;
    @JsonProperty("vlTotalLiquidadoNE")
    private BigDecimal vltotalLiquidadoNe;
    @JsonProperty("vlTotalPagoNE")
    private BigDecimal vlTotalPagoNe;
    @JsonProperty("cdContrato")
    private String cdContrato;
    @JsonProperty("tpContrato")
    private String tipoContrato;
    @JsonProperty("vlContrato")
    private BigDecimal vlContrato;
    @JsonProperty("dtInicioVigenciaContrato")
    private LocalDate dtInicioVigenciaContrato;
    @JsonProperty("dtFimVigenciaContrato")
    private LocalDate dtFimVigenciaContrato;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioFiltro;
    public ContratoEmpenhoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.contrato_empenho";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-empenho";
        nomeDataInicialPadraoFiltro = null; // Não usa filtros de data específicos
        nomeDataFinalPadraoFiltro = null;   // Não usa filtros de data específicos
        dtAnoPadrao = "dt_ano_exercicio";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_solicitacao_compra", cdSolicitacaoCompra);
        camposResposta.put("ug_cd", ugCd);
        camposResposta.put("ds_resumida_solicitacao_compra", dsResumidaSolicitacaoCompra);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("dt_ano_exercicio", dtAnoExercicio);
        camposResposta.put("situacao", situacao);
        camposResposta.put("nm_modalidade_licitacao", nmModalidadeLicitacao);
        camposResposta.put("crit_julg_licitacao", critJulgLicitacao);
        camposResposta.put("natureza_objeto_licitacao", naturezaObjetoLicitacao);
        camposResposta.put("ug_se", ugSe);
        camposResposta.put("doc_se", docSe);
        camposResposta.put("doc_referencia_ne", docReferenciaNe);
        camposResposta.put("valor_se", valorSe);
        camposResposta.put("ug_ne", ugNe);
        camposResposta.put("doc_ne", docNe);
        camposResposta.put("doc_credor_ne", docCredorNe);
        camposResposta.put("nm_credor_ne", nmCredorNe);
        camposResposta.put("tipo_empenho", tipoEmpenho);
        camposResposta.put("vl_original_ne", vlOriginalNe);
        camposResposta.put("vltotal_reforco_ne", vltotalReforcoNe);
        camposResposta.put("vltotal_anulado_ne", vltotalAnuladoNe);
        camposResposta.put("vltotal_liquidado_ne", vltotalLiquidadoNe);
        camposResposta.put("vl_total_pago_ne", vlTotalPagoNe);
        camposResposta.put("cd_contrato", cdContrato);
        camposResposta.put("tipo_contrato", tipoContrato);
        camposResposta.put("vl_contrato", vlContrato);
        camposResposta.put("dt_inicio_vigencia_contrato", dtInicioVigenciaContrato);
        camposResposta.put("dt_fim_vigencia_contrato", dtFimVigenciaContrato);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestoraContrato", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioFiltro != null) {
            camposParametros.put("dtAnoExercicio", dtAnoExercicioFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestoraContrato", ugCd);
        }
        if (ano != null) {
            parametros.put("dtAnoExercicio", ano);
        }
        return parametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestoraContrato", ugCd);
        }
        parametros.put("dtAnoExercicio", java.time.Year.now().getValue());
        return parametros;
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
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }
    public Integer getDtAnoExercicioFiltro() {
        return dtAnoExercicioFiltro;
    }
    public void setDtAnoExercicioFiltro(Integer dtAnoExercicioFiltro) {
        this.dtAnoExercicioFiltro = dtAnoExercicioFiltro;
    }
}
