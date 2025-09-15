package br.gov.se.setc.consumer.dto;

import br.gov.se.setc.util.ValidacaoUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO específico para consumo de empenhos filtrados por mês
 * Estende EmpenhoDTO adicionando funcionalidade de filtro mensal
 */
public class EmpenhoMensalDTO extends EmpenhoDTO {
    
    @JsonProperty("nuMesFiltro")
    private Integer nuMesFiltro;
    
    @JsonProperty("dtAnoExercicioFiltro")
    private Integer dtAnoExercicioFiltro;
    
    public EmpenhoMensalDTO() {
        super();
    }
    
    public EmpenhoMensalDTO(Integer nuMesFiltro, Integer dtAnoExercicioFiltro) {
        super();
        this.nuMesFiltro = nuMesFiltro;
        this.dtAnoExercicioFiltro = dtAnoExercicioFiltro;
    }
    
    @Override
    protected void mapearParametros() {
        super.mapearParametros();
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
        if (dtAnoExercicioFiltro != null) {
            camposParametros.put("dtAnoExercicioCTB", dtAnoExercicioFiltro);
        }
    }
    
    @Override
    public Map<String, Object> getCamposParametrosAtual(String cdUnidadeGestora, ValidacaoUtil<?> utilsService) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", cdUnidadeGestora);
        
        // Usa o ano filtro se especificado, senão usa o ano atual
        Integer anoParaUsar = (dtAnoExercicioFiltro != null) ? dtAnoExercicioFiltro : utilsService.getAnoAtual().intValue();
        camposParametros.put("dtAnoExercicioCTB", anoParaUsar);
        
        // Adiciona filtro de mês se especificado
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
        
        return camposParametros;
    }
    
    @Override
    public Map<String, Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        camposParametros.put("cdUnidadeGestora", ugCd);
        
        // Usa o ano filtro se especificado, senão usa o ano passado como parâmetro
        Integer anoParaUsar = (dtAnoExercicioFiltro != null) ? dtAnoExercicioFiltro : ano.intValue();
        camposParametros.put("dtAnoExercicioCTB", anoParaUsar);
        
        // Adiciona filtro de mês se especificado
        if (nuMesFiltro != null) {
            camposParametros.put("nuMes", nuMesFiltro);
        }
        
        return camposParametros;
    }
    
    /**
     * Cria parâmetros específicos para um mês e ano
     */
    public Map<String, Object> getCamposParametrosMesEspecifico(String ugCd, Integer ano, Integer mes) {
        Map<String, Object> camposParametros = new LinkedHashMap<>();
        if (ugCd != null && !ugCd.trim().isEmpty()) {
            camposParametros.put("cdUnidadeGestora", ugCd);
        }
        if (ano != null) {
            camposParametros.put("dtAnoExercicioCTB", ano);
        }
        if (mes != null) {
            camposParametros.put("nuMes", mes);
        }
        return camposParametros;
    }
    
    // Getters e Setters
    public Integer getNuMesFiltro() {
        return nuMesFiltro;
    }
    
    public void setNuMesFiltro(Integer nuMesFiltro) {
        this.nuMesFiltro = nuMesFiltro;
    }
    
    public Integer getDtAnoExercicioFiltro() {
        return dtAnoExercicioFiltro;
    }
    
    public void setDtAnoExercicioFiltro(Integer dtAnoExercicioFiltro) {
        this.dtAnoExercicioFiltro = dtAnoExercicioFiltro;
    }
}
