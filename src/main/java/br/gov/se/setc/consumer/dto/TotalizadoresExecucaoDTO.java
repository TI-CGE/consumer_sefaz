package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * DTO para consumo da API de Totalizadores de Execução do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/totalizadores-execucao
 */
public class TotalizadoresExecucaoDTO extends EndpontSefaz {
    @JsonProperty("cdProgramaGoverno")
    private Integer cdProgramaGoverno;
    @JsonProperty("cdNaturezaDespesaElementoDespesa")
    private String cdNaturezaDespesaElementoDespesa;
    @JsonProperty("nmNaturezaDespesaElementoDespesa")
    private String nmNaturezaDespesaElementoDespesa;
    @JsonProperty("nmProgramaGoverno")
    private String nmProgramaGoverno;
    @JsonProperty("vlTotalPago")
    private BigDecimal vlTotalPago;
    @JsonProperty("vlTotalLiquidado")
    private BigDecimal vlTotalLiquidado;
    @JsonProperty("cdUnidadeOrcamentaria")
    private String cdUnidadeOrcamentaria;
    @JsonProperty("vlTotalEmpenhado")
    private BigDecimal vlTotalEmpenhado;
    @JsonProperty("nmFonteRecurso")
    private String nmFonteRecurso;
    @JsonProperty("nmComplementoExecOrc")
    private String nmComplementoExecOrc;
    @JsonProperty("vlTotalDotacaoAtualizada")
    private BigDecimal vlTotalDotacaoAtualizada;
    @JsonProperty("cdFonteRecursoReduzida")
    private String cdFonteRecursoReduzida;
    @JsonProperty("cdSubAcao")
    private String cdSubAcao;
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    @JsonProperty("dhUltimaAlteracao")
    private String dhUltimaAlteracao;
    private LocalDateTime dhUltimaAlteracaoConverted;
    @JsonProperty("nmAcao")
    private String nmAcao;
    @JsonProperty("dtAnoExercicioCTB")
    private Integer dtAnoExercicioCTB;
    @JsonProperty("cdAcao")
    private String cdAcao;
    @JsonProperty("cdGestao")
    private String cdGestao;
    @JsonProperty("cdComplementoExecOrc")
    private String cdComplementoExecOrc;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioCTBFiltro;
    public TotalizadoresExecucaoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.totalizadores_execucao";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/totalizadores-execucao";
        nomeDataInicialPadraoFiltro = null;
        nomeDataFinalPadraoFiltro = null;
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }
    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_programa_governo", cdProgramaGoverno);
        camposResposta.put("cd_natureza_despesa_elemento_despesa", cdNaturezaDespesaElementoDespesa);
        camposResposta.put("nm_natureza_despesa_elemento_despesa", nmNaturezaDespesaElementoDespesa);
        camposResposta.put("nm_programa_governo", nmProgramaGoverno);
        camposResposta.put("vl_total_pago", vlTotalPago);
        camposResposta.put("vl_total_liquidado", vlTotalLiquidado);
        camposResposta.put("cd_unidade_orcamentaria", cdUnidadeOrcamentaria);
        camposResposta.put("vl_total_empenhado", vlTotalEmpenhado);
        camposResposta.put("nm_fonte_recurso", nmFonteRecurso);
        camposResposta.put("nm_complemento_exec_orc", nmComplementoExecOrc);
        camposResposta.put("vl_total_dotacao_atualizada", vlTotalDotacaoAtualizada);
        camposResposta.put("cd_fonte_recurso_reduzida", cdFonteRecursoReduzida);
        camposResposta.put("cd_sub_acao", cdSubAcao);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dh_ultima_alteracao", dhUltimaAlteracaoConverted);
        camposResposta.put("nm_acao", nmAcao);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("cd_acao", cdAcao);
        camposResposta.put("cd_gestao", cdGestao);
        camposResposta.put("cd_complemento_exec_orc", cdComplementoExecOrc);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTBFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicioCTB", ano.intValue());
        }
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        camposParametros.put("dtAnoExercicioCTB", 2025);
        return camposParametros;
    }
    public Integer getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }
    public void setCdProgramaGoverno(Integer cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }
    public String getCdNaturezaDespesaElementoDespesa() {
        return cdNaturezaDespesaElementoDespesa;
    }
    public void setCdNaturezaDespesaElementoDespesa(String cdNaturezaDespesaElementoDespesa) {
        this.cdNaturezaDespesaElementoDespesa = cdNaturezaDespesaElementoDespesa;
    }
    public String getNmNaturezaDespesaElementoDespesa() {
        return nmNaturezaDespesaElementoDespesa;
    }
    public void setNmNaturezaDespesaElementoDespesa(String nmNaturezaDespesaElementoDespesa) {
        this.nmNaturezaDespesaElementoDespesa = nmNaturezaDespesaElementoDespesa;
    }
    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }
    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }
    public BigDecimal getVlTotalPago() {
        return vlTotalPago;
    }
    public void setVlTotalPago(BigDecimal vlTotalPago) {
        this.vlTotalPago = vlTotalPago;
    }
    public BigDecimal getVlTotalLiquidado() {
        return vlTotalLiquidado;
    }
    public void setVlTotalLiquidado(BigDecimal vlTotalLiquidado) {
        this.vlTotalLiquidado = vlTotalLiquidado;
    }
    public String getCdUnidadeOrcamentaria() {
        return cdUnidadeOrcamentaria;
    }
    public void setCdUnidadeOrcamentaria(String cdUnidadeOrcamentaria) {
        this.cdUnidadeOrcamentaria = cdUnidadeOrcamentaria;
    }
    public BigDecimal getVlTotalEmpenhado() {
        return vlTotalEmpenhado;
    }
    public void setVlTotalEmpenhado(BigDecimal vlTotalEmpenhado) {
        this.vlTotalEmpenhado = vlTotalEmpenhado;
    }
    public String getNmFonteRecurso() {
        return nmFonteRecurso;
    }
    public void setNmFonteRecurso(String nmFonteRecurso) {
        this.nmFonteRecurso = nmFonteRecurso;
    }
    public String getNmComplementoExecOrc() {
        return nmComplementoExecOrc;
    }
    public void setNmComplementoExecOrc(String nmComplementoExecOrc) {
        this.nmComplementoExecOrc = nmComplementoExecOrc;
    }
    public BigDecimal getVlTotalDotacaoAtualizada() {
        return vlTotalDotacaoAtualizada;
    }
    public void setVlTotalDotacaoAtualizada(BigDecimal vlTotalDotacaoAtualizada) {
        this.vlTotalDotacaoAtualizada = vlTotalDotacaoAtualizada;
    }
    public String getCdFonteRecursoReduzida() {
        return cdFonteRecursoReduzida;
    }
    public void setCdFonteRecursoReduzida(String cdFonteRecursoReduzida) {
        this.cdFonteRecursoReduzida = cdFonteRecursoReduzida;
    }
    public String getCdSubAcao() {
        return cdSubAcao;
    }
    public void setCdSubAcao(String cdSubAcao) {
        this.cdSubAcao = cdSubAcao;
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
    public String getDhUltimaAlteracao() {
        return dhUltimaAlteracao;
    }
    public LocalDateTime getDhUltimaAlteracaoConverted() {
        return dhUltimaAlteracaoConverted;
    }
    public void setDhUltimaAlteracao(String dhUltimaAlteracao) {
        this.dhUltimaAlteracao = dhUltimaAlteracao;
        if (dhUltimaAlteracao != null && !dhUltimaAlteracao.isEmpty()) {
            try {
                this.dhUltimaAlteracaoConverted = LocalDateTime.parse(dhUltimaAlteracao);
            } catch (Exception e) {
                this.dhUltimaAlteracaoConverted = null;
            }
        } else {
            this.dhUltimaAlteracaoConverted = null;
        }
    }
    public String getNmAcao() {
        return nmAcao;
    }
    public void setNmAcao(String nmAcao) {
        this.nmAcao = nmAcao;
    }
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }
    public String getCdAcao() {
        return cdAcao;
    }
    public void setCdAcao(String cdAcao) {
        this.cdAcao = cdAcao;
    }
    public String getCdGestao() {
        return cdGestao;
    }
    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }
    public String getCdComplementoExecOrc() {
        return cdComplementoExecOrc;
    }
    public void setCdComplementoExecOrc(String cdComplementoExecOrc) {
        this.cdComplementoExecOrc = cdComplementoExecOrc;
    }
    public String getCdUnidadeGestoraFiltro() {
        return cdUnidadeGestoraFiltro;
    }
    public void setCdUnidadeGestoraFiltro(String cdUnidadeGestoraFiltro) {
        this.cdUnidadeGestoraFiltro = cdUnidadeGestoraFiltro;
    }
    public Integer getDtAnoExercicioCTBFiltro() {
        return dtAnoExercicioCTBFiltro;
    }
    public void setDtAnoExercicioCTBFiltro(Integer dtAnoExercicioCTBFiltro) {
        this.dtAnoExercicioCTBFiltro = dtAnoExercicioCTBFiltro;
    }
}