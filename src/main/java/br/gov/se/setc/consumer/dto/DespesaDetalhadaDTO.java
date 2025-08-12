package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.entity.DespesaDetalhada;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO para consumo da API de Despesa Detalhada do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/despesa-detalhada
 * Contexto: CTB (Contabilidade)
 *
 * Estrutura de resposta: Array de objetos JSON com dados detalhados de despesas
 * organizados por órgão, unidade orçamentária, função, programa, ação e natureza da despesa
 *
 * Filtros disponíveis:
 * - Obrigatório: dtAnoExercicioCTB (ano do exercício)
 * - Opcionais: cdOrgao, cdUnidOrc, cdFuncao, cdSubFuncao, cdProgramaGoverno, 
 *              cdPPAAcao, cdSubAcao, cdCategoriaEconomica, cdNaturezaDespesa
 *
 * Chave única: dtAnoExercicioCTB + cdOrgao + cdUnidOrc + cdNaturezaDespesa + cdPPAAcao + cdSubAcao
 */
public class DespesaDetalhadaDTO extends EndpontSefaz {

    // Campos de identificação e hierarquia organizacional
    @JsonProperty("cdOrgao")
    private String cdOrgao;

    @JsonProperty("nmOrgao")
    private String nmOrgao;

    @JsonProperty("cdUnidOrc")
    private String cdUnidOrc;

    @JsonProperty("nmUnidOrc")
    private String nmUnidOrc;

    // Hierarquia funcional
    @JsonProperty("cdFuncao")
    private String cdFuncao;

    @JsonProperty("nmFuncao")
    private String nmFuncao;

    @JsonProperty("cdSubFuncao")
    private String cdSubFuncao;

    @JsonProperty("nmSubFuncao")
    private String nmSubFuncao;

    // Hierarquia programática
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

    // Categoria econômica
    @JsonProperty("cdCategoriaEconomica")
    private String cdCategoriaEconomica;

    @JsonProperty("nmCategoriaEconomica")
    private String nmCategoriaEconomica;

    // Natureza da despesa
    @JsonProperty("cdNaturezaDespesa")
    private String cdNaturezaDespesa;

    @JsonProperty("nmNaturezaDespesa")
    private String nmNaturezaDespesa;

    // Valores monetários
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

    // Campos para filtros de consulta
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

    // Campos derivados para persistência
    private String cdUnidadeGestora;
    private Integer dtAnoExercicioCTB;
    private Integer nuMes;

    public DespesaDetalhadaDTO() {
        inicializarDadosEndpoint();
        // NÃO chamar mapearCamposResposta() no construtor - será chamado depois que os campos forem definidos
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.despesa_detalhada";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/despesa-detalhada";
        nomeDataInicialPadraoFiltro = null; // Não usa filtros de data
        nomeDataFinalPadraoFiltro = null;   // Não usa filtros de data
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }

    @Override
    public void mapearCamposResposta() {
        // Garantir que os campos derivados estejam definidos a partir dos filtros
        definirCamposDerivados();

        // Mapear todos os campos de resposta para persistência no banco
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);
        camposResposta.put("nu_mes", nuMes);
        
        // Hierarquia organizacional
        camposResposta.put("cd_orgao", cdOrgao);
        camposResposta.put("nm_orgao", nmOrgao);
        camposResposta.put("cd_unid_orc", cdUnidOrc);
        camposResposta.put("nm_unid_orc", nmUnidOrc);
        
        // Hierarquia funcional
        camposResposta.put("cd_funcao", cdFuncao);
        camposResposta.put("nm_funcao", nmFuncao);
        camposResposta.put("cd_sub_funcao", cdSubFuncao);
        camposResposta.put("nm_sub_funcao", nmSubFuncao);
        
        // Hierarquia programática
        camposResposta.put("cd_programa_governo", cdProgramaGoverno);
        camposResposta.put("nm_programa_governo", nmProgramaGoverno);
        camposResposta.put("cd_ppa_acao", cdPPAAcao);
        camposResposta.put("nm_ppa_acao", nmPPAAcao);
        camposResposta.put("cd_sub_acao", cdSubAcao);
        camposResposta.put("nm_subacao", nmSubacao);
        
        // Categoria econômica
        camposResposta.put("cd_categoria_economica", cdCategoriaEconomica);
        camposResposta.put("nm_categoria_economica", nmCategoriaEconomica);
        
        // Natureza da despesa
        camposResposta.put("cd_natureza_despesa", cdNaturezaDespesa);
        camposResposta.put("nm_natureza_despesa", nmNaturezaDespesa);
        
        // Valores monetários
        camposResposta.put("vl_dotacao_inicial", vlDotacaoInicial);
        camposResposta.put("vl_credito_adicional", vlCreditoAdicional);
        camposResposta.put("vl_dotacao_atualizada", vlDotacaoAtualizada);
        camposResposta.put("vl_total_empenhado", vlTotalEmpenhado);
        camposResposta.put("vl_total_liquidado", vlTotalLiquidado);
        camposResposta.put("vl_total_pago", vlTotalPago);

        // Adicionar campos de auditoria com timestamp atual
        LocalDateTime now = LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }

    @Override
    protected void mapearParametros() {
        // Mapear parâmetros de filtro para a API
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
        // Para Despesa Detalhada, usar UG, ano e mês como parâmetros obrigatórios
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicio", ano.intValue());
        }

        // Usar nuMesFiltro se definido, senão usar dezembro como padrão
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);

        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        // Para Despesa Detalhada, usar UG, ano atual e mês atual
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        // Usar ano atual (2025)
        camposParametros.put("dtAnoExercicio", 2025);

        // Usar nuMesFiltro se definido, senão usar dezembro como padrão
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);

        return camposParametros;
    }

    /**
     * Método específico para Despesa Detalhada que retorna parâmetros para todos os 12 meses
     * @param ugCd Código da Unidade Gestora
     * @param ano Ano do exercício
     * @return Lista com 12 mapas de parâmetros (um para cada mês)
     */
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

    /**
     * Método para definir o mês específico nos parâmetros
     * @param mes Número do mês (1-12)
     */
    public void setMesParametro(int mes) {
        if (mes >= 1 && mes <= 12) {
            camposParametros.put("nuMes", mes);
        }
    }

    /**
     * Indica se esta entidade requer múltiplas requisições (12 meses)
     * @return true para DespesaDetalhada
     */
    public boolean requerMultiplasRequisicoes() {
        return true;
    }

    /**
     * Retorna o número de requisições necessárias (12 meses)
     * @return 12
     */
    public int getNumeroRequisicoes() {
        return 12;
    }

    /**
     * Retorna o tempo de pausa entre requisições em milissegundos
     * @return 500ms
     */
    public long getPausaEntreRequisicoes() {
        return 500L;
    }

    /**
     * Define os campos derivados a partir dos filtros e parâmetros
     * CORREÇÃO: A API não retorna esses campos, eles devem ser derivados dos parâmetros da consulta
     */
    private void definirCamposDerivados() {
        // PRIORIDADE 1: Usar filtros se disponíveis
        if (cdUnidadeGestora == null && cdUnidadeGestoraFiltro != null) {
            cdUnidadeGestora = cdUnidadeGestoraFiltro;
        }

        if (dtAnoExercicioCTB == null && dtAnoExercicioCTBFiltro != null) {
            dtAnoExercicioCTB = dtAnoExercicioCTBFiltro;
        }

        if (nuMes == null && nuMesFiltro != null) {
            nuMes = nuMesFiltro;
        }

        // PRIORIDADE 2: Usar parâmetros da consulta (ESSENCIAL para DespesaDetalhada)
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

        // VALIDAÇÃO FINAL: Log se campos críticos ainda estão NULL
        if (cdUnidadeGestora == null || dtAnoExercicioCTB == null || nuMes == null) {
            System.err.println("⚠️ AVISO: Campos obrigatórios NULL após definirCamposDerivados:");
            System.err.println("  - cdUnidadeGestora: " + cdUnidadeGestora);
            System.err.println("  - dtAnoExercicioCTB: " + dtAnoExercicioCTB);
            System.err.println("  - nuMes: " + nuMes);
            System.err.println("  - Parâmetros disponíveis: " + camposParametros.keySet());
        }
    }

    /**
     * Converte o DTO para a entidade JPA
     * @return DespesaDetalhada entity
     */
    public DespesaDetalhada toEntity() {
        DespesaDetalhada entity = new DespesaDetalhada();

        // Garantir que os campos derivados estejam definidos
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

    // Getters and Setters
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

    // Getters e Setters para campos de filtro
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
