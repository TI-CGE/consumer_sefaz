package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DTO para consumo da API de Base Despesa Credor do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-credor
 * Contexto: GBP (Gestão de Bens Públicos)
 * Estrutura de resposta: result > dados > colecao[] (aninhada e paginada)
 * Paginação: controlada por nuFaixaPaginacao e qtTotalFaixasPaginacao
 */
public class BaseDespesaCredorDTO extends EndpontSefaz {
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("cdUnidadeGestora")
    private String ugCd;
    @JsonProperty("dtAnoExercicio")
    private Integer dtAnoExercicio;
    @JsonProperty("sqEmpenho")
    private Integer sqEmpenho;
    @JsonProperty("nuDocumentoPessoa")
    private String nuDocumentoPessoa;
    @JsonProperty("cdTipoDocumento")
    private Integer cdTipoDocumento;
    @JsonProperty("nmRazaoSocialPessoa")
    private String nmRazaoSocialPessoa;
    @JsonProperty("dhLancamentoEmpenho")
    private LocalDateTime dhLancamentoEmpenho;
    @JsonProperty("dtGeracaoEmpenho")
    private String dtGeracaoEmpenho;
    @JsonProperty("vlOriginalEmpenho")
    private BigDecimal vlOriginalEmpenho;
    @JsonProperty("vlTotalLiquidadoEmpenho")
    private BigDecimal vlTotalLiquidadoEmpenho;
    @JsonProperty("vlTotalPagoEmpenho")
    private BigDecimal vlTotalPagoEmpenho;
    @JsonProperty("nmModalidadeLicitacao")
    private String nmModalidadeLicitacao;
    @JsonProperty("cdLicitacao")
    private String cdLicitacao;
    @JsonProperty("nmItemMaterialServico")
    private String nmItemMaterialServico;
    @JsonProperty("qtItemSolicitacaoEmpenho")
    private Integer qtItemSolicitacaoEmpenho;
    @JsonProperty("vlUnitarioItemSolicitacaoEmpenho")
    private BigDecimal vlUnitarioItemSolicitacaoEmpenho;
    @JsonProperty("nuFaixaPaginacao")
    private Integer nuFaixaPaginacao;
    @JsonProperty("qtTotalFaixasPaginacao")
    private Integer qtTotalFaixasPaginacao;
    @JsonProperty("msgUsuario")
    private String msgUsuario;
    @JsonProperty("msgTecnica")
    private String msgTecnica;
    @JsonProperty("cdRetorno")
    private String cdRetorno;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioFiltro;
    private Integer nuFaixaPaginacaoFiltro;
    public BaseDespesaCredorDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.base_despesa_credor";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-credor";
        nomeDataInicialPadraoFiltro = null;
        nomeDataFinalPadraoFiltro = null;
        dtAnoPadrao = "dt_ano_exercicio";
        parametrosRequeridos = true;
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("ug_cd", ugCd);
        camposResposta.put("dt_ano_exercicio", dtAnoExercicio);
        camposResposta.put("sq_empenho", sqEmpenho);
        camposResposta.put("nu_documento_pessoa", nuDocumentoPessoa);
        camposResposta.put("cd_tipo_documento", cdTipoDocumento);
        camposResposta.put("nm_razao_social_pessoa", nmRazaoSocialPessoa);
        camposResposta.put("dh_lancamento_empenho", dhLancamentoEmpenho);
        camposResposta.put("dt_geracao_empenho", dtGeracaoEmpenho);
        camposResposta.put("vl_original_empenho", vlOriginalEmpenho);
        camposResposta.put("vl_total_liquidado_empenho", vlTotalLiquidadoEmpenho);
        camposResposta.put("vl_total_pago_empenho", vlTotalPagoEmpenho);
        camposResposta.put("nm_modalidade_licitacao", nmModalidadeLicitacao);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("nm_item_material_servico", nmItemMaterialServico);
        camposResposta.put("qt_item_solicitacao_empenho", qtItemSolicitacaoEmpenho);
        camposResposta.put("vl_unitario_item_solicitacao_empenho", vlUnitarioItemSolicitacaoEmpenho);
        LocalDateTime now = LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioFiltro != null) {
            camposParametros.put("dtAnoExercicio", dtAnoExercicioFiltro);
        }
        if (nuFaixaPaginacaoFiltro != null) {
            camposParametros.put("nuFaixaPaginacao", nuFaixaPaginacaoFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            parametros.put("dtAnoExercicio", ano);
        }
        parametros.put("nuFaixaPaginacao", 1);
        return parametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestora", ugCd);
        }
        Integer anoUsar = dtAnoExercicioFiltro != null ? dtAnoExercicioFiltro : java.time.Year.now().getValue();
        parametros.put("dtAnoExercicio", anoUsar);
        parametros.put("nuFaixaPaginacao", nuFaixaPaginacaoFiltro != null ? nuFaixaPaginacaoFiltro : 1);
        return parametros;
    }
    /**
     * Método específico para obter parâmetros com faixa de paginação específica
     */
    public Map<String, Object> getCamposParametrosComFaixa(String ugCd, Integer ano, Integer faixa) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            parametros.put("dtAnoExercicio", ano);
        }
        if (faixa != null) {
            parametros.put("nuFaixaPaginacao", faixa);
        }
        return parametros;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public String getUgCd() {
        return ugCd;
    }
    public void setUgCd(String ugCd) {
        this.ugCd = ugCd;
    }
    public Integer getDtAnoExercicio() {
        return dtAnoExercicio;
    }
    public void setDtAnoExercicio(Integer dtAnoExercicio) {
        this.dtAnoExercicio = dtAnoExercicio;
    }
    public Integer getSqEmpenho() {
        return sqEmpenho;
    }
    public void setSqEmpenho(Integer sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }
    public String getNuDocumentoPessoa() {
        return nuDocumentoPessoa;
    }
    public void setNuDocumentoPessoa(String nuDocumentoPessoa) {
        this.nuDocumentoPessoa = nuDocumentoPessoa;
    }
    public Integer getCdTipoDocumento() {
        return cdTipoDocumento;
    }
    public void setCdTipoDocumento(Integer cdTipoDocumento) {
        this.cdTipoDocumento = cdTipoDocumento;
    }
    public String getNmRazaoSocialPessoa() {
        return nmRazaoSocialPessoa;
    }
    public void setNmRazaoSocialPessoa(String nmRazaoSocialPessoa) {
        this.nmRazaoSocialPessoa = nmRazaoSocialPessoa;
    }
    public LocalDateTime getDhLancamentoEmpenho() {
        return dhLancamentoEmpenho;
    }
    public void setDhLancamentoEmpenho(LocalDateTime dhLancamentoEmpenho) {
        this.dhLancamentoEmpenho = dhLancamentoEmpenho;
    }
    public String getDtGeracaoEmpenho() {
        return dtGeracaoEmpenho;
    }
    public void setDtGeracaoEmpenho(String dtGeracaoEmpenho) {
        this.dtGeracaoEmpenho = dtGeracaoEmpenho;
    }
    public BigDecimal getVlOriginalEmpenho() {
        return vlOriginalEmpenho;
    }
    public void setVlOriginalEmpenho(BigDecimal vlOriginalEmpenho) {
        this.vlOriginalEmpenho = vlOriginalEmpenho;
    }
    public BigDecimal getVlTotalLiquidadoEmpenho() {
        return vlTotalLiquidadoEmpenho;
    }
    public void setVlTotalLiquidadoEmpenho(BigDecimal vlTotalLiquidadoEmpenho) {
        this.vlTotalLiquidadoEmpenho = vlTotalLiquidadoEmpenho;
    }
    public BigDecimal getVlTotalPagoEmpenho() {
        return vlTotalPagoEmpenho;
    }
    public void setVlTotalPagoEmpenho(BigDecimal vlTotalPagoEmpenho) {
        this.vlTotalPagoEmpenho = vlTotalPagoEmpenho;
    }
    public String getNmModalidadeLicitacao() {
        return nmModalidadeLicitacao;
    }
    public void setNmModalidadeLicitacao(String nmModalidadeLicitacao) {
        this.nmModalidadeLicitacao = nmModalidadeLicitacao;
    }
    public String getCdLicitacao() {
        return cdLicitacao;
    }
    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }
    public String getNmItemMaterialServico() {
        return nmItemMaterialServico;
    }
    public void setNmItemMaterialServico(String nmItemMaterialServico) {
        this.nmItemMaterialServico = nmItemMaterialServico;
    }
    public Integer getQtItemSolicitacaoEmpenho() {
        return qtItemSolicitacaoEmpenho;
    }
    public void setQtItemSolicitacaoEmpenho(Integer qtItemSolicitacaoEmpenho) {
        this.qtItemSolicitacaoEmpenho = qtItemSolicitacaoEmpenho;
    }
    public BigDecimal getVlUnitarioItemSolicitacaoEmpenho() {
        return vlUnitarioItemSolicitacaoEmpenho;
    }
    public void setVlUnitarioItemSolicitacaoEmpenho(BigDecimal vlUnitarioItemSolicitacaoEmpenho) {
        this.vlUnitarioItemSolicitacaoEmpenho = vlUnitarioItemSolicitacaoEmpenho;
    }
    public Integer getNuFaixaPaginacao() {
        return nuFaixaPaginacao;
    }
    public void setNuFaixaPaginacao(Integer nuFaixaPaginacao) {
        this.nuFaixaPaginacao = nuFaixaPaginacao;
    }
    public Integer getQtTotalFaixasPaginacao() {
        return qtTotalFaixasPaginacao;
    }
    public void setQtTotalFaixasPaginacao(Integer qtTotalFaixasPaginacao) {
        this.qtTotalFaixasPaginacao = qtTotalFaixasPaginacao;
    }
    public String getMsgUsuario() {
        return msgUsuario;
    }
    public void setMsgUsuario(String msgUsuario) {
        this.msgUsuario = msgUsuario;
    }
    public String getMsgTecnica() {
        return msgTecnica;
    }
    public void setMsgTecnica(String msgTecnica) {
        this.msgTecnica = msgTecnica;
    }
    public String getCdRetorno() {
        return cdRetorno;
    }
    public void setCdRetorno(String cdRetorno) {
        this.cdRetorno = cdRetorno;
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
    public Integer getNuFaixaPaginacaoFiltro() {
        return nuFaixaPaginacaoFiltro;
    }
    public void setNuFaixaPaginacaoFiltro(Integer nuFaixaPaginacaoFiltro) {
        this.nuFaixaPaginacaoFiltro = nuFaixaPaginacaoFiltro;
    }
}