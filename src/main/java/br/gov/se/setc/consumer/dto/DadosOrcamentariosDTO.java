package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DadosOrcamentariosDTO extends EndpontSefaz {
    private Integer cdFuncaoPLO;
    private String nmFuncaoPLO;
    private Integer cdSubFuncao;
    private String nmSubFuncao;
    private Integer cdProgramaGoverno;
    private String nmProgramaGoverno;
    private Integer cdCategoriaEconomica;
    private String nmCategoriaEconomica;
    private Integer cdModalidadeAplicacao;
    private String nmModalidadeAplicacao;
    private Integer cdFonteRecurso;
    private String nmFonteRecurso;
    private Integer cdGrupoDespesa;
    private String nmGrupoDespesa;
    private String cdLicitacao;
    private String dsObjetoLicitacao;
    private String nuProcessoLicitacao;

    // Parâmetros para a API
    private Integer dtAnoExercicioCTB;
    private String cdUnidadeGestora;
    private String cdGestao;
    private Integer sqEmpenho;

    public DadosOrcamentariosDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.dados_orcamentarios";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho";
        nomeDataInicialPadraoFiltro = null; // Não há campo de data específico
        nomeDataFinalPadraoFiltro = null; // Não há campo de data específico
        dtAnoPadrao = null; // Não há campo de ano específico
        parametrosRequeridos = true;
    }

    @Override
    public void mapearCamposResposta() {
        camposResposta.put("cd_funcao_plo", cdFuncaoPLO);
        camposResposta.put("nm_funcao_plo", nmFuncaoPLO);
        camposResposta.put("cd_sub_funcao", cdSubFuncao);
        camposResposta.put("nm_sub_funcao", nmSubFuncao);
        camposResposta.put("cd_programa_governo", cdProgramaGoverno);
        camposResposta.put("nm_programa_governo", nmProgramaGoverno);
        camposResposta.put("cd_categoria_economica", cdCategoriaEconomica);
        camposResposta.put("nm_categoria_economica", nmCategoriaEconomica);
        camposResposta.put("cd_modalidade_aplicacao", cdModalidadeAplicacao);
        camposResposta.put("nm_modalidade_aplicacao", nmModalidadeAplicacao);
        camposResposta.put("cd_fonte_recurso", cdFonteRecurso);
        camposResposta.put("nm_fonte_recurso", nmFonteRecurso);
        camposResposta.put("cd_grupo_despesa", cdGrupoDespesa);
        camposResposta.put("nm_grupo_despesa", nmGrupoDespesa);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("ds_objeto_licitacao", dsObjetoLicitacao);
        camposResposta.put("nu_processo_licitacao", nuProcessoLicitacao);
    }

    @Override
    protected void mapearParametros() {
        camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTB);
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("cdGestao", cdGestao);
        camposParametros.put("sqEmpenho", sqEmpenho);
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);

        // Se o ano foi definido no DTO, usar ele; senão usar o ano atual
        if (this.dtAnoExercicioCTB != null) {
            camposParametros.put("dtAnoExercicioCTB", this.dtAnoExercicioCTB);
        } else {
            camposParametros.put("dtAnoExercicioCTB", utilsService.getAnoAtual());
        }

        // Busca apenas por UG e Ano - removido cdGestao e sqEmpenho
        return camposParametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        camposParametros.put("dtAnoExercicioCTB", ano);

        // Busca apenas por UG e Ano - removido cdGestao e sqEmpenho
        return camposParametros;
    }

    /**
     * Método específico para buscar dados orçamentários com UG e Ano específicos
     * Usado quando os parâmetros são fornecidos diretamente pelo controller
     */
    public Map<String, Object> getCamposParametrosEspecificos(String cdUnidadeGestora, Integer dtAnoExercicioCTB, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioCTB);

        // Busca apenas por UG e Ano - removido cdGestao
        return camposParametros;
    }

    // Getters and Setters
    public Integer getCdFuncaoPLO() {
        return cdFuncaoPLO;
    }

    public void setCdFuncaoPLO(Integer cdFuncaoPLO) {
        this.cdFuncaoPLO = cdFuncaoPLO;
    }

    public String getNmFuncaoPLO() {
        return nmFuncaoPLO;
    }

    public void setNmFuncaoPLO(String nmFuncaoPLO) {
        this.nmFuncaoPLO = nmFuncaoPLO;
    }

    public Integer getCdSubFuncao() {
        return cdSubFuncao;
    }

    public void setCdSubFuncao(Integer cdSubFuncao) {
        this.cdSubFuncao = cdSubFuncao;
    }

    public String getNmSubFuncao() {
        return nmSubFuncao;
    }

    public void setNmSubFuncao(String nmSubFuncao) {
        this.nmSubFuncao = nmSubFuncao;
    }

    public Integer getCdProgramaGoverno() {
        return cdProgramaGoverno;
    }

    public void setCdProgramaGoverno(Integer cdProgramaGoverno) {
        this.cdProgramaGoverno = cdProgramaGoverno;
    }

    public String getNmProgramaGoverno() {
        return nmProgramaGoverno;
    }

    public void setNmProgramaGoverno(String nmProgramaGoverno) {
        this.nmProgramaGoverno = nmProgramaGoverno;
    }

    public Integer getCdCategoriaEconomica() {
        return cdCategoriaEconomica;
    }

    public void setCdCategoriaEconomica(Integer cdCategoriaEconomica) {
        this.cdCategoriaEconomica = cdCategoriaEconomica;
    }

    public String getNmCategoriaEconomica() {
        return nmCategoriaEconomica;
    }

    public void setNmCategoriaEconomica(String nmCategoriaEconomica) {
        this.nmCategoriaEconomica = nmCategoriaEconomica;
    }

    public Integer getCdModalidadeAplicacao() {
        return cdModalidadeAplicacao;
    }

    public void setCdModalidadeAplicacao(Integer cdModalidadeAplicacao) {
        this.cdModalidadeAplicacao = cdModalidadeAplicacao;
    }

    public String getNmModalidadeAplicacao() {
        return nmModalidadeAplicacao;
    }

    public void setNmModalidadeAplicacao(String nmModalidadeAplicacao) {
        this.nmModalidadeAplicacao = nmModalidadeAplicacao;
    }

    public Integer getCdFonteRecurso() {
        return cdFonteRecurso;
    }

    public void setCdFonteRecurso(Integer cdFonteRecurso) {
        this.cdFonteRecurso = cdFonteRecurso;
    }

    public String getNmFonteRecurso() {
        return nmFonteRecurso;
    }

    public void setNmFonteRecurso(String nmFonteRecurso) {
        this.nmFonteRecurso = nmFonteRecurso;
    }

    public Integer getCdGrupoDespesa() {
        return cdGrupoDespesa;
    }

    public void setCdGrupoDespesa(Integer cdGrupoDespesa) {
        this.cdGrupoDespesa = cdGrupoDespesa;
    }

    public String getNmGrupoDespesa() {
        return nmGrupoDespesa;
    }

    public void setNmGrupoDespesa(String nmGrupoDespesa) {
        this.nmGrupoDespesa = nmGrupoDespesa;
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

    public String getCdGestao() {
        return cdGestao;
    }

    public void setCdGestao(String cdGestao) {
        this.cdGestao = cdGestao;
    }

    public Integer getSqEmpenho() {
        return sqEmpenho;
    }

    public void setSqEmpenho(Integer sqEmpenho) {
        this.sqEmpenho = sqEmpenho;
    }

    public String getCdLicitacao() {
        return cdLicitacao;
    }

    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }

    public String getDsObjetoLicitacao() {
        return dsObjetoLicitacao;
    }

    public void setDsObjetoLicitacao(String dsObjetoLicitacao) {
        this.dsObjetoLicitacao = dsObjetoLicitacao;
    }

    public String getNuProcessoLicitacao() {
        return nuProcessoLicitacao;
    }

    public void setNuProcessoLicitacao(String nuProcessoLicitacao) {
        this.nuProcessoLicitacao = nuProcessoLicitacao;
    }
}
