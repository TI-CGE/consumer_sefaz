package br.gov.se.setc.consumer.dto;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.entity.DespesaDetalhada;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
public class DespesaDetalhadaDTO extends EndpontSefaz {
    @JsonProperty("cdOrgao")
    private String cdOrgao;
    @JsonProperty("nmOrgao")
    private String nmOrgao;
    @JsonProperty("cdUnidOrc")
    private String cdUnidOrc;
    @JsonProperty("nmUnidOrc")
    private String nmUnidOrc;
    @JsonProperty("cdFuncao")
    private String cdFuncao;
    @JsonProperty("nmFuncao")
    private String nmFuncao;
    @JsonProperty("cdSubFuncao")
    private String cdSubFuncao;
    @JsonProperty("nmSubFuncao")
    private String nmSubFuncao;
    @JsonProperty("cdProgramaGoverno")
    private String cdProgramaGoverno;
    @JsonProperty("nmProgramaGoverno")
    private String nmProgramaGoverno;
    @JsonProperty("cdPPAAcao")
    private String cdPPAAcao;
    @JsonProperty("nmPPAAcao")
    private String nmPPAAcao;
    @JsonProperty("cdSubAcao")
    private String cdSubAcao;
    @JsonProperty("nmSubacao")
    private String nmSubacao;
    @JsonProperty("cdCategoriaEconomica")
    private String cdCategoriaEconomica;
    @JsonProperty("nmCategoriaEconomica")
    private String nmCategoriaEconomica;
    @JsonProperty("cdNaturezaDespesa")
    private String cdNaturezaDespesa;
    @JsonProperty("nmNaturezaDespesa")
    private String nmNaturezaDespesa;
    @JsonProperty("vlDotacaoInicial")
    private BigDecimal vlDotacaoInicial;
    @JsonProperty("vlCreditoAdicional")
    private BigDecimal vlCreditoAdicional;
    @JsonProperty("vlDotacaoAtualizada")
    private BigDecimal vlDotacaoAtualizada;
    @JsonProperty("vlTotalEmpenhado")
    private BigDecimal vlTotalEmpenhado;
    @JsonProperty("vlTotalLiquidado")
    private BigDecimal vlTotalLiquidado;
    @JsonProperty("vlTotalPago")
    private BigDecimal vlTotalPago;
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioCTBFiltro;
    private Integer nuMesFiltro;
    private String cdOrgaoFiltro;
    private String cdUnidOrcFiltro;
    private String cdFuncaoFiltro;
    private String cdSubFuncaoFiltro;
    private String cdProgramaGovernoFiltro;
    private String cdPPAAcaoFiltro;
    private String cdSubAcaoFiltro;
    private String cdCategoriaEconomicaFiltro;
    private String cdNaturezaDespesaFiltro;
    private String cdUnidadeGestora;
    private Integer dtAnoExercicioCTB;
    private Integer nuMes;
    public DespesaDetalhadaDTO() {
        inicializarDadosEndpoint();
        mapearParametros();
    }
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.despesa_detalhada";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/despesa-detalhada";
        nomeDataInicialPadraoFiltro = null;
        nomeDataFinalPadraoFiltro = null;
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }
    @Override
    public void mapearCamposResposta() {
        definirCamposDerivados();
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("nu_mes", nuMes);
        camposResposta.put("cd_orgao", cdOrgao);
        camposResposta.put("nm_orgao", nmOrgao);
        camposResposta.put("cd_unid_orc", cdUnidOrc);
        camposResposta.put("nm_unid_orc", nmUnidOrc);
        camposResposta.put("cd_funcao", cdFuncao);
        camposResposta.put("nm_funcao", nmFuncao);
        camposResposta.put("cd_sub_funcao", cdSubFuncao);
        camposResposta.put("nm_sub_funcao", nmSubFuncao);
        camposResposta.put("cd_programa_governo", cdProgramaGoverno);
        camposResposta.put("nm_programa_governo", nmProgramaGoverno);
        camposResposta.put("cd_ppa_acao", cdPPAAcao);
        camposResposta.put("nm_ppa_acao", nmPPAAcao);
        camposResposta.put("cd_sub_acao", cdSubAcao);
        camposResposta.put("nm_subacao", nmSubacao);
        camposResposta.put("cd_categoria_economica", cdCategoriaEconomica);
        camposResposta.put("nm_categoria_economica", nmCategoriaEconomica);
        camposResposta.put("cd_natureza_despesa", cdNaturezaDespesa);
        camposResposta.put("nm_natureza_despesa", nmNaturezaDespesa);
        camposResposta.put("vl_dotacao_inicial", vlDotacaoInicial);
        camposResposta.put("vl_credito_adicional", vlCreditoAdicional);
        camposResposta.put("vl_dotacao_atualizada", vlDotacaoAtualizada);
        camposResposta.put("vl_total_empenhado", vlTotalEmpenhado);
        camposResposta.put("vl_total_liquidado", vlTotalLiquidado);
        camposResposta.put("vl_total_pago", vlTotalPago);
        LocalDateTime now = LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }
    @Override
    protected void mapearParametros() {
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicio", dtAnoExercicioCTBFiltro);
        }
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
        if (cdOrgaoFiltro != null) {
            camposParametros.put("cdOrgao", cdOrgaoFiltro);
        }
        if (cdUnidOrcFiltro != null) {
            camposParametros.put("cdUnidOrc", cdUnidOrcFiltro);
        }
        if (cdFuncaoFiltro != null) {
            camposParametros.put("cdFuncao", cdFuncaoFiltro);
        }
        if (cdSubFuncaoFiltro != null) {
            camposParametros.put("cdSubFuncao", cdSubFuncaoFiltro);
        }
        if (cdProgramaGovernoFiltro != null) {
            camposParametros.put("cdProgramaGoverno", cdProgramaGovernoFiltro);
        }
        if (cdPPAAcaoFiltro != null) {
            camposParametros.put("cdPPAAcao", cdPPAAcaoFiltro);
        }
        if (cdSubAcaoFiltro != null) {
            camposParametros.put("cdSubAcao", cdSubAcaoFiltro);
        }
        if (cdCategoriaEconomicaFiltro != null) {
            camposParametros.put("cdCategoriaEconomica", cdCategoriaEconomicaFiltro);
        }
        if (cdNaturezaDespesaFiltro != null) {
            camposParametros.put("cdNaturezaDespesa", cdNaturezaDespesaFiltro);
        }
    }
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicio", ano.intValue());
        }
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);
        return camposParametros;
    }
    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        camposParametros.put("dtAnoExercicio", 2025);
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : (utilsService != null && utilsService.getMesAtual() != null ? utilsService.getMesAtual().intValue() : 12);
        camposParametros.put("nuMes", mesParaUsar);
        return camposParametros;
    }
    public java.util.List<Map<String, Object>> getCamposParametrosTodosMeses(String ugCd, Short ano) {
        java.util.List<Map<String, Object>> parametrosMeses = new java.util.ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            Map<String, Object> parametrosMes = new LinkedHashMap<>();
            if (ugCd != null) {
                parametrosMes.put("cdUnidadeGestora", ugCd);
            }
            if (ano != null) {
                parametrosMes.put("dtAnoExercicio", ano.intValue());
            }
            parametrosMes.put("nuMes", mes);
            parametrosMeses.add(parametrosMes);
        }
        return parametrosMeses;
    }
    public void setMesParametro(int mes) {
        if (mes >= 1 && mes <= 12) {
            camposParametros.put("nuMes", mes);
        }
    }
    public boolean requerMultiplasRequisicoes() {
        return true;
    }
    public int getNumeroRequisicoes() {
        return 12;
    }
    public long getPausaEntreRequisicoes() {
        return 500L;
    }
    private void definirCamposDerivados() {
        if (cdUnidadeGestora == null && cdUnidadeGestoraFiltro != null) {
            cdUnidadeGestora = cdUnidadeGestoraFiltro;
        }
        if (dtAnoExercicioCTB == null && dtAnoExercicioCTBFiltro != null) {
            dtAnoExercicioCTB = dtAnoExercicioCTBFiltro;
        }
        if (nuMes == null && nuMesFiltro != null) {
            nuMes = nuMesFiltro;
        }
        if (dtAnoExercicioCTB == null && camposParametros.containsKey("dtAnoExercicio")) {
            Object anoParam = camposParametros.get("dtAnoExercicio");
            if (anoParam instanceof Integer) {
                dtAnoExercicioCTB = (Integer) anoParam;
            } else if (anoParam instanceof String) {
                try {
                    dtAnoExercicioCTB = Integer.parseInt((String) anoParam);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter dtAnoExercicio: " + anoParam);
                }
            }
        }
        if (cdUnidadeGestora == null && camposParametros.containsKey("cdUnidadeGestora")) {
            Object ugParam = camposParametros.get("cdUnidadeGestora");
            if (ugParam instanceof String) {
                cdUnidadeGestora = (String) ugParam;
            } else if (ugParam != null) {
                cdUnidadeGestora = ugParam.toString();
            }
        }
        if (nuMes == null && camposParametros.containsKey("nuMes")) {
            Object mesParam = camposParametros.get("nuMes");
            if (mesParam instanceof Integer) {
                nuMes = (Integer) mesParam;
            } else if (mesParam instanceof String) {
                try {
                    nuMes = Integer.parseInt((String) mesParam);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter nuMes: " + mesParam);
                }
            }
        }
        if (cdUnidadeGestora == null || dtAnoExercicioCTB == null || nuMes == null) {
            System.err.println("⚠️ AVISO: Campos obrigatórios NULL após definirCamposDerivados:");
            System.err.println("  - cdUnidadeGestora: " + cdUnidadeGestora);
            System.err.println("  - dtAnoExercicioCTB: " + dtAnoExercicioCTB);
            System.err.println("  - nuMes: " + nuMes);
            System.err.println("  - Parâmetros disponíveis: " + camposParametros.keySet());
        }
    }
    public DespesaDetalhada toEntity() {
        DespesaDetalhada entity = new DespesaDetalhada();
        definirCamposDerivados();
        entity.setCdUnidadeGestora(cdUnidadeGestora);
        entity.setDtAnoExercicioCTB(dtAnoExercicioCTB);
        entity.setNuMes(nuMes);
        entity.setCdOrgao(cdOrgao);
        entity.setNmOrgao(nmOrgao);
        entity.setCdUnidOrc(cdUnidOrc);
        entity.setNmUnidOrc(nmUnidOrc);
        entity.setCdFuncao(cdFuncao);
        entity.setNmFuncao(nmFuncao);
        entity.setCdSubFuncao(cdSubFuncao);
        entity.setNmSubFuncao(nmSubFuncao);
        entity.setCdProgramaGoverno(cdProgramaGoverno);
        entity.setNmProgramaGoverno(nmProgramaGoverno);
        entity.setCdPPAAcao(cdPPAAcao);
        entity.setNmPPAAcao(nmPPAAcao);
        entity.setCdSubAcao(cdSubAcao);
        entity.setNmSubacao(nmSubacao);
        entity.setCdCategoriaEconomica(cdCategoriaEconomica);
        entity.setNmCategoriaEconomica(nmCategoriaEconomica);
        entity.setCdNaturezaDespesa(cdNaturezaDespesa);
        entity.setNmNaturezaDespesa(nmNaturezaDespesa);
        entity.setVlDotacaoInicial(vlDotacaoInicial);
        entity.setVlCreditoAdicional(vlCreditoAdicional);
        entity.setVlDotacaoAtualizada(vlDotacaoAtualizada);
        entity.setVlTotalEmpenhado(vlTotalEmpenhado);
        entity.setVlTotalLiquidado(vlTotalLiquidado);
        entity.setVlTotalPago(vlTotalPago);
        return entity;
    }
    public String getCdOrgao() {
        return cdOrgao;
    }
    public void setCdOrgao(String cdOrgao) {
        this.cdOrgao = cdOrgao;
    }
    public String getNmOrgao() {
        return nmOrgao;
    }
    public void setNmOrgao(String nmOrgao) {
        this.nmOrgao = nmOrgao;
    }
    public String getCdUnidOrc() {
        return cdUnidOrc;
    }
    public void setCdUnidOrc(String cdUnidOrc) {
        this.cdUnidOrc = cdUnidOrc;
    }
    public String getNmUnidOrc() {
        return nmUnidOrc;
    }
    public void setNmUnidOrc(String nmUnidOrc) {
        this.nmUnidOrc = nmUnidOrc;
    }
    public String getCdFuncao() {
        return cdFuncao;
    }
    public void setCdFuncao(String cdFuncao) {
        this.cdFuncao = cdFuncao;
    }
    public String getNmFuncao() {
        return nmFuncao;
    }
    public void setNmFuncao(String nmFuncao) {
        this.nmFuncao = nmFuncao;
    }
    public String getCdSubFuncao() {
        return cdSubFuncao;
    }
    public void setCdSubFuncao(String cdSubFuncao) {
        this.cdSubFuncao = cdSubFuncao;
    }
    public String getNmSubFuncao() {
        return nmSubFuncao;
    }
    public void setNmSubFuncao(String nmSubFuncao) {
        this.nmSubFuncao = nmSubFuncao;
    }
    public String getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }
    public void setCdProgramaGoverno(String cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }
    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }
    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }
    public String getCdPPAAcao() {
        return cdPPAAcao;
    }
    public void setCdPPAAcao(String cdPPAAcao) {
        this.cdPPAAcao = cdPPAAcao;
    }
    public String getNmPPAAcao() {
        return nmPPAAcao;
    }
    public void setNmPPAAcao(String nmPPAAcao) {
        this.nmPPAAcao = nmPPAAcao;
    }
    public String getCdSubAcao() {
        return cdSubAcao;
    }
    public void setCdSubAcao(String cdSubAcao) {
        this.cdSubAcao = cdSubAcao;
    }
    public String getNmSubacao() {
        return nmSubacao;
    }
    public void setNmSubacao(String nmSubacao) {
        this.nmSubacao = nmSubacao;
    }
    public String getCdCategoriaEconomica() {
        return cdCategoriaEconomica;
    }
    public void setCdCategoriaEconomica(String cdCategoriaEconomica) {
        this.cdCategoriaEconomica = cdCategoriaEconomica;
    }
    public String getNmCategoriaEconomica() {
        return nmCategoriaEconomica;
    }
    public void setNmCategoriaEconomica(String nmCategoriaEconomica) {
        this.nmCategoriaEconomica = nmCategoriaEconomica;
    }
    public String getCdNaturezaDespesa() {
        return cdNaturezaDespesa;
    }
    public void setCdNaturezaDespesa(String cdNaturezaDespesa) {
        this.cdNaturezaDespesa = cdNaturezaDespesa;
    }
    public String getNmNaturezaDespesa() {
        return nmNaturezaDespesa;
    }
    public void setNmNaturezaDespesa(String nmNaturezaDespesa) {
        this.nmNaturezaDespesa = nmNaturezaDespesa;
    }
    public BigDecimal getVlDotacaoInicial() {
        return vlDotacaoInicial;
    }
    public void setVlDotacaoInicial(BigDecimal vlDotacaoInicial) {
        this.vlDotacaoInicial = vlDotacaoInicial;
    }
    public BigDecimal getVlCreditoAdicional() {
        return vlCreditoAdicional;
    }
    public void setVlCreditoAdicional(BigDecimal vlCreditoAdicional) {
        this.vlCreditoAdicional = vlCreditoAdicional;
    }
    public BigDecimal getVlDotacaoAtualizada() {
        return vlDotacaoAtualizada;
    }
    public void setVlDotacaoAtualizada(BigDecimal vlDotacaoAtualizada) {
        this.vlDotacaoAtualizada = vlDotacaoAtualizada;
    }
    public BigDecimal getVlTotalEmpenhado() {
        return vlTotalEmpenhado;
    }
    public void setVlTotalEmpenhado(BigDecimal vlTotalEmpenhado) {
        this.vlTotalEmpenhado = vlTotalEmpenhado;
    }
    public BigDecimal getVlTotalLiquidado() {
        return vlTotalLiquidado;
    }
    public void setVlTotalLiquidado(BigDecimal vlTotalLiquidado) {
        this.vlTotalLiquidado = vlTotalLiquidado;
    }
    public BigDecimal getVlTotalPago() {
        return vlTotalPago;
    }
    public void setVlTotalPago(BigDecimal vlTotalPago) {
        this.vlTotalPago = vlTotalPago;
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
    public Integer getNuMesFiltro() {
        return nuMesFiltro;
    }
    public void setNuMesFiltro(Integer nuMesFiltro) {
        this.nuMesFiltro = nuMesFiltro;
    }
    public String getCdOrgaoFiltro() {
        return cdOrgaoFiltro;
    }
    public void setCdOrgaoFiltro(String cdOrgaoFiltro) {
        this.cdOrgaoFiltro = cdOrgaoFiltro;
    }
    public String getCdUnidOrcFiltro() {
        return cdUnidOrcFiltro;
    }
    public void setCdUnidOrcFiltro(String cdUnidOrcFiltro) {
        this.cdUnidOrcFiltro = cdUnidOrcFiltro;
    }
    public String getCdFuncaoFiltro() {
        return cdFuncaoFiltro;
    }
    public void setCdFuncaoFiltro(String cdFuncaoFiltro) {
        this.cdFuncaoFiltro = cdFuncaoFiltro;
    }
    public String getCdSubFuncaoFiltro() {
        return cdSubFuncaoFiltro;
    }
    public void setCdSubFuncaoFiltro(String cdSubFuncaoFiltro) {
        this.cdSubFuncaoFiltro = cdSubFuncaoFiltro;
    }
    public String getCdProgramaGovernoFiltro() {
        return cdProgramaGovernoFiltro;
    }
    public void setCdProgramaGovernoFiltro(String cdProgramaGovernoFiltro) {
        this.cdProgramaGovernoFiltro = cdProgramaGovernoFiltro;
    }
    public String getCdPPAAcaoFiltro() {
        return cdPPAAcaoFiltro;
    }
    public void setCdPPAAcaoFiltro(String cdPPAAcaoFiltro) {
        this.cdPPAAcaoFiltro = cdPPAAcaoFiltro;
    }
    public String getCdSubAcaoFiltro() {
        return cdSubAcaoFiltro;
    }
    public void setCdSubAcaoFiltro(String cdSubAcaoFiltro) {
        this.cdSubAcaoFiltro = cdSubAcaoFiltro;
    }
    public String getCdCategoriaEconomicaFiltro() {
        return cdCategoriaEconomicaFiltro;
    }
    public void setCdCategoriaEconomicaFiltro(String cdCategoriaEconomicaFiltro) {
        this.cdCategoriaEconomicaFiltro = cdCategoriaEconomicaFiltro;
    }
    public String getCdNaturezaDespesaFiltro() {
        return cdNaturezaDespesaFiltro;
    }
    public void setCdNaturezaDespesaFiltro(String cdNaturezaDespesaFiltro) {
        this.cdNaturezaDespesaFiltro = cdNaturezaDespesaFiltro;
    }
    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }
    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
    }
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }
    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }
    public Integer getNuMes() {
        return nuMes;
    }
    public void setNuMes(Integer nuMes) {
        this.nuMes = nuMes;
    }
}