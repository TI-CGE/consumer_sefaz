package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO para consumo da API de Previsão Realização Receita do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita
 * Contexto: CTB (Contabilidade)
 *
 * ✅ IMPLEMENTAÇÃO COMPLETA: Esta API requer o parâmetro 'nuMes' (número do mês de 1-12).
 * A implementação customizada faz 12 requisições sequenciais (uma para cada mês) para
 * obter dados anuais completos, consolidando automaticamente os resultados.
 *
 * Funcionalidades:
 * - Busca automática de todos os 12 meses
 * - Pausa de 500ms entre requisições
 * - Consolidação automática dos dados
 * - Deduplicação baseada na chave única
 *
 * Estrutura de resposta: Array de objetos JSON com dados de previsão e realização de receitas
 * organizados em hierarquia de 5 níveis: Categoria → Origem → Espécie → Desdobramento → Tipo
 *
 * Chave única: cdUnidadeGestora + dtAnoExercicioCTB + nuMes + hierarquia completa
 */
public class PrevisaoRealizacaoReceitaDTO extends EndpontSefaz {

    // Campos de identificação
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;

    @JsonProperty("dtAnoExercicioCTB")
    private String dtAnoExercicioCTBString; // Recebido como String da API

    // Hierarquia de categorização da receita
    @JsonProperty("cdCategoriaEconomica")
    private String cdCategoriaEconomica;

    @JsonProperty("nmCategoriaEconomica")
    private String nmCategoriaEconomica;

    @JsonProperty("cdOrigem")
    private String cdOrigem;

    @JsonProperty("nmOrigem")
    private String nmOrigem;

    @JsonProperty("cdEspecie")
    private String cdEspecie;

    @JsonProperty("nmEspecie")
    private String nmEspecie;

    @JsonProperty("cdDesdobramento")
    private String cdDesdobramento;

    @JsonProperty("nmDesdobramento")
    private String nmDesdobramento;

    @JsonProperty("cdTipo")
    private String cdTipo;

    @JsonProperty("nmTipo")
    private String nmTipo;

    // Valores monetários
    @JsonProperty("vlPrevisto")
    private BigDecimal vlPrevisto;

    @JsonProperty("vlAtualizado")
    private BigDecimal vlAtualizado;

    @JsonProperty("vlRealizado")
    private BigDecimal vlRealizado;

    // Campos para parâmetros de filtro
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioCTBFiltro;
    private Integer nuMesFiltro;

    // Campo derivado para persistência (conversão de String para Integer)
    private Integer dtAnoExercicioCTB;

    public PrevisaoRealizacaoReceitaDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.previsao_realizacao_receita";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita";
        nomeDataInicialPadraoFiltro = null; // Não usa filtros de data
        nomeDataFinalPadraoFiltro = null;   // Não usa filtros de data
        dtAnoPadrao = "dt_ano_exercicio_ctb";
    }

    @Override
    public void mapearCamposResposta() {
        // Mapear todos os campos de resposta para persistência no banco
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("dt_ano_exercicio_ctb", dtAnoExercicioCTB);

        // Garantir que nu_mes nunca seja NULL
        Integer mesParaSalvar = nuMesFiltro;
        if (mesParaSalvar == null) {
            // Se nuMesFiltro for NULL, usar dezembro (12) como padrão
            mesParaSalvar = 12;
        }
        camposResposta.put("nu_mes", mesParaSalvar);
        
        // Hierarquia de categorização
        camposResposta.put("cd_categoria_economica", cdCategoriaEconomica);
        camposResposta.put("nm_categoria_economica", nmCategoriaEconomica);
        camposResposta.put("cd_origem", cdOrigem);
        camposResposta.put("nm_origem", nmOrigem);
        camposResposta.put("cd_especie", cdEspecie);
        camposResposta.put("nm_especie", nmEspecie);
        camposResposta.put("cd_desdobramento", cdDesdobramento);
        camposResposta.put("nm_desdobramento", nmDesdobramento);
        camposResposta.put("cd_tipo", cdTipo);
        camposResposta.put("nm_tipo", nmTipo);
        
        // Valores monetários
        camposResposta.put("vl_previsto", vlPrevisto);
        camposResposta.put("vl_atualizado", vlAtualizado);
        camposResposta.put("vl_realizado", vlRealizado);
    }

    @Override
    protected void mapearParametros() {
        // Mapear parâmetros de filtro para a API
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioCTBFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTBFiltro);
        }
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        // Para Previsão Realização Receita, usar mês específico se definido
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicioCTB", ano.intValue());
        }

        // Usar nuMesFiltro se definido, senão usar dezembro como padrão
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);

        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        // Para Previsão Realização Receita, usar ano atual
        camposParametros.clear();
        if (ugCd != null) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        // Usar ano atual (2025)
        camposParametros.put("dtAnoExercicioCTB", 2025);

        // Usar nuMesFiltro se definido, senão usar dezembro como padrão
        Integer mesParaUsar = (nuMesFiltro != null) ? nuMesFiltro : 12;
        camposParametros.put("nuMes", mesParaUsar);

        return camposParametros;
    }

    /**
     * Método para converter dtAnoExercicioCTBString para Integer após deserialização
     */
    public void processarCamposDerivados() {
        if (dtAnoExercicioCTBString != null && !dtAnoExercicioCTBString.trim().isEmpty()) {
            try {
                this.dtAnoExercicioCTB = Integer.parseInt(dtAnoExercicioCTBString.trim());
            } catch (NumberFormatException e) {
                // Log do erro e manter valor null
                this.dtAnoExercicioCTB = null;
            }
        }
    }

    /**
     * Método específico para Previsão Realização Receita que retorna parâmetros para todos os 12 meses
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
                parametrosMes.put("dtAnoExercicioCTB", ano.intValue());
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
     * @return true para PrevisaoRealizacaoReceita
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

    // Getters and Setters
    public String getCdUnidadeGestora() {
        return cdUnidadeGestora;
    }

    public void setCdUnidadeGestora(String cdUnidadeGestora) {
        this.cdUnidadeGestora = cdUnidadeGestora;
    }

    public String getDtAnoExercicioCTBString() {
        return dtAnoExercicioCTBString;
    }

    public void setDtAnoExercicioCTBString(String dtAnoExercicioCTBString) {
        this.dtAnoExercicioCTBString = dtAnoExercicioCTBString;
        processarCamposDerivados(); // Converter automaticamente
    }

    public Integer getDtAnoExercicioCTB() {
        return dtAnoExercicioCTB;
    }

    public void setDtAnoExercicioCTB(Integer dtAnoExercicioCTB) {
        this.dtAnoExercicioCTB = dtAnoExercicioCTB;
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

    public String getCdOrigem() {
        return cdOrigem;
    }

    public void setCdOrigem(String cdOrigem) {
        this.cdOrigem = cdOrigem;
    }

    public String getNmOrigem() {
        return nmOrigem;
    }

    public void setNmOrigem(String nmOrigem) {
        this.nmOrigem = nmOrigem;
    }

    public String getCdEspecie() {
        return cdEspecie;
    }

    public void setCdEspecie(String cdEspecie) {
        this.cdEspecie = cdEspecie;
    }

    public String getNmEspecie() {
        return nmEspecie;
    }

    public void setNmEspecie(String nmEspecie) {
        this.nmEspecie = nmEspecie;
    }

    public String getCdDesdobramento() {
        return cdDesdobramento;
    }

    public void setCdDesdobramento(String cdDesdobramento) {
        this.cdDesdobramento = cdDesdobramento;
    }

    public String getNmDesdobramento() {
        return nmDesdobramento;
    }

    public void setNmDesdobramento(String nmDesdobramento) {
        this.nmDesdobramento = nmDesdobramento;
    }

    public String getCdTipo() {
        return cdTipo;
    }

    public void setCdTipo(String cdTipo) {
        this.cdTipo = cdTipo;
    }

    public String getNmTipo() {
        return nmTipo;
    }

    public void setNmTipo(String nmTipo) {
        this.nmTipo = nmTipo;
    }

    public BigDecimal getVlPrevisto() {
        return vlPrevisto;
    }

    public void setVlPrevisto(BigDecimal vlPrevisto) {
        this.vlPrevisto = vlPrevisto;
    }

    public BigDecimal getVlAtualizado() {
        return vlAtualizado;
    }

    public void setVlAtualizado(BigDecimal vlAtualizado) {
        this.vlAtualizado = vlAtualizado;
    }

    public BigDecimal getVlRealizado() {
        return vlRealizado;
    }

    public void setVlRealizado(BigDecimal vlRealizado) {
        this.vlRealizado = vlRealizado;
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
}
