package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO para consumo da API de Base Despesa Licitação do SEFAZ
 * Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-licitacao
 * Contexto: GBP (Gestão de Bens Públicos)
 * Estrutura de resposta: Array de objetos JSON com dados de despesas por licitação
 */
public class BaseDespesaLicitacaoDTO extends EndpontSefaz {
    
    // Campos obrigatórios conforme especificação
    @JsonProperty("nuProcesso")
    private String nuProcesso;
    
    @JsonProperty("siLicitacao")
    private String siLicitacao;
    
    @JsonProperty("nuDocumento")
    private String nuDocumento;
    
    @JsonProperty("nmRazaoSocialUg")
    private String nmRazaoSocialUg;
    
    @JsonProperty("nmRazaoSocialFornecedor")
    private String nmRazaoSocialFornecedor;
    
    @JsonProperty("cdLicitacao")
    private String cdLicitacao;
    
    @JsonProperty("vlLicitacao")
    private BigDecimal vlLicitacao;
    
    @JsonProperty("sgUnidadeGestora")
    private String sgUnidadeGestora;
    
    @JsonProperty("cdUnidadeGestora")
    private String cdUnidadeGestora;
    
    @JsonProperty("nmModalidade")
    private String nmModalidade;
    
    @JsonProperty("dtHomologacao")
    private LocalDate dtHomologacao;
    
    @JsonProperty("vlEstimado")
    private BigDecimal vlEstimado;
    
    @JsonProperty("dsObjeto")
    private String dsObjeto;

    // Campos para parâmetros de filtro
    private String cdUnidadeGestoraFiltro;
    private Integer dtAnoExercicioFiltro;

    public BaseDespesaLicitacaoDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }

    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "consumer_sefaz.base_despesa_licitacao";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-licitacao";
        nomeDataInicialPadraoFiltro = null; // Não usa filtros de data específicos
        nomeDataFinalPadraoFiltro = null;   // Não usa filtros de data específicos
        dtAnoPadrao = null; // Não usa ano padrão específico
        parametrosRequeridos = false; // Endpoint não requer parâmetros obrigatórios
    }

    @Override
    public void mapearCamposResposta() {
        // Mapear os campos da resposta para persistência no banco
        camposResposta.put("nu_processo", nuProcesso);
        camposResposta.put("si_licitacao", siLicitacao);
        camposResposta.put("nu_documento", nuDocumento);
        camposResposta.put("nm_razao_social_ug", nmRazaoSocialUg);
        camposResposta.put("nm_razao_social_fornecedor", nmRazaoSocialFornecedor);
        camposResposta.put("cd_licitacao", cdLicitacao);
        camposResposta.put("vl_licitacao", vlLicitacao);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("nm_modalidade", nmModalidade);
        camposResposta.put("dt_homologacao", dtHomologacao);
        camposResposta.put("vl_estimado", vlEstimado);
        camposResposta.put("ds_objeto", dsObjeto);

        // Adicionar campos de auditoria com timestamp atual
        LocalDateTime now = LocalDateTime.now();
        camposResposta.put("created_at", now);
        camposResposta.put("updated_at", now);
    }

    @Override
    protected void mapearParametros() {
        // Mapear parâmetros de filtro se necessário
        if (cdUnidadeGestoraFiltro != null) {
            camposParametros.put("cdUnidadeGestora", cdUnidadeGestoraFiltro);
        }
        if (dtAnoExercicioFiltro != null) {
            camposParametros.put("dtAnoExercicio", dtAnoExercicioFiltro);
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
        return parametros;
    }

    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            parametros.put("cdUnidadeGestora", ugCd);
        }
        // Usar ano atual se não especificado
        parametros.put("dtAnoExercicio", java.time.Year.now().getValue());
        return parametros;
    }

    // Getters e Setters
    public String getNuProcesso() {
        return nuProcesso;
    }

    public void setNuProcesso(String nuProcesso) {
        this.nuProcesso = nuProcesso;
    }

    public String getSiLicitacao() {
        return siLicitacao;
    }

    public void setSiLicitacao(String siLicitacao) {
        this.siLicitacao = siLicitacao;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    public String getNmRazaoSocialUg() {
        return nmRazaoSocialUg;
    }

    public void setNmRazaoSocialUg(String nmRazaoSocialUg) {
        this.nmRazaoSocialUg = nmRazaoSocialUg;
    }

    public String getNmRazaoSocialFornecedor() {
        return nmRazaoSocialFornecedor;
    }

    public void setNmRazaoSocialFornecedor(String nmRazaoSocialFornecedor) {
        this.nmRazaoSocialFornecedor = nmRazaoSocialFornecedor;
    }

    public String getCdLicitacao() {
        return cdLicitacao;
    }

    public void setCdLicitacao(String cdLicitacao) {
        this.cdLicitacao = cdLicitacao;
    }

    public BigDecimal getVlLicitacao() {
        return vlLicitacao;
    }

    public void setVlLicitacao(BigDecimal vlLicitacao) {
        this.vlLicitacao = vlLicitacao;
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

    public String getNmModalidade() {
        return nmModalidade;
    }

    public void setNmModalidade(String nmModalidade) {
        this.nmModalidade = nmModalidade;
    }

    public LocalDate getDtHomologacao() {
        return dtHomologacao;
    }

    public void setDtHomologacao(LocalDate dtHomologacao) {
        this.dtHomologacao = dtHomologacao;
    }

    public BigDecimal getVlEstimado() {
        return vlEstimado;
    }

    public void setVlEstimado(BigDecimal vlEstimado) {
        this.vlEstimado = vlEstimado;
    }

    public String getDsObjeto() {
        return dsObjeto;
    }

    public void setDsObjeto(String dsObjeto) {
        this.dsObjeto = dsObjeto;
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
