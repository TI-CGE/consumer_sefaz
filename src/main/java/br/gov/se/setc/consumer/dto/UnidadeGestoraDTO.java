package br.gov.se.setc.consumer.dto;

import java.util.HashMap;
import java.util.Map;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.util.ValidacaoUtil;

public class UnidadeGestoraDTO extends EndpontSefaz{
    String nmUnidadeGestora;
    String sgUnidadeGestora;
    String cdUnidadeGestora;
    String sgTipoUnidadeGestora;


    public UnidadeGestoraDTO() {
        inicializarDadosEndpoint();
        mapearCamposResposta();
        mapearParametros();
    }


    public UnidadeGestoraDTO(String nmUnidadeGestora, String sgUnidadeGestora, String cdUnidadeGestora, String sgTipoUnidadeGestora) {
        this.nmUnidadeGestora = nmUnidadeGestora;
        this.sgUnidadeGestora = sgUnidadeGestora;
        this.cdUnidadeGestora = cdUnidadeGestora;
        this.sgTipoUnidadeGestora = sgTipoUnidadeGestora;
    }


    public String getNmUnidadeGestora() {
        return nmUnidadeGestora;
    }

    public void setNmUnidadeGestora(String nmUnidadeGestora) {
        this.nmUnidadeGestora = nmUnidadeGestora;
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

    public String getSgTipoUnidadeGestora() {
        return sgTipoUnidadeGestora;
    }

    public void setSgTipoUnidadeGestora(String sgTipoUnidadeGestora) {
        this.sgTipoUnidadeGestora = sgTipoUnidadeGestora;
    }


    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        // UnidadeGestora endpoint doesn't need UG or year parameters
        // It returns all management units with sgTipoUnidadeGestora=E
        return new HashMap<>();
    }


    @Override
    public Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil<?> utilsService) {
        // UnidadeGestora endpoint doesn't need UG or date parameters
        // It returns all management units with sgTipoUnidadeGestora=E
        return  new HashMap<>();
    }


    @Override
    protected void inicializarDadosEndpoint() {
         tabelaBanco = "consumer_sefaz.unidade_gestora";
         url = "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora?sgTipoUnidadeGestora=E";
         // UnidadeGestora endpoint doesn't use date filters, so set to null
         nomeDataInicialPadraoFiltro = null;
         nomeDataFinalPadraoFiltro = null;
         dtAnoPadrao = null;
    }


    @Override
    public void mapearCamposResposta() {
        // Map the response fields for unidade gestora
        camposResposta.put("nm_unidade_gestora", nmUnidadeGestora);
        camposResposta.put("sg_unidade_gestora", sgUnidadeGestora);
        camposResposta.put("cd_unidade_gestora", cdUnidadeGestora);
        camposResposta.put("sg_tipo_unidade_gestora", sgTipoUnidadeGestora);
    }


    @Override
    protected void mapearParametros() {
        
    }
}
