package br.gov.se.setc.consumer.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.BaseDespesaCredorDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class BaseDespesaCredorPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(BaseDespesaCredorPorPeriodoService.class.getName());
    private final ConsumoApiService<BaseDespesaCredorDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public BaseDespesaCredorPorPeriodoService(
            @Qualifier("baseDespesaCredorConsumoApiService") ConsumoApiService<BaseDespesaCredorDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE BASE DESPESA CREDOR - ANO " + ano + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("BASE_DESPESA_CREDOR", "Consumindo Base Despesa Credor para o ano " + ano);
        BaseDespesaCredorDTO dto = new BaseDespesaCredorDTO();
        dto.setDtAnoExercicioFiltro(ano);
        List<BaseDespesaCredorDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("BASE_DESPESA_CREDOR", total + " registros processados (ano " + ano + ")", executionTimeMs);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", total);
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano concluída. " + total + " registros processados.");
        return resumo;
    }

    public Map<String, Object> consumirAnoInteiro(int ano, String cdUnidadeGestora) {
        if (cdUnidadeGestora == null || cdUnidadeGestora.trim().isEmpty()) {
            throw new IllegalArgumentException("cdUnidadeGestora é obrigatório");
        }
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE BASE DESPESA CREDOR - ANO " + ano + " UG " + cdUnidadeGestora + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("BASE_DESPESA_CREDOR", "Consumindo Base Despesa Credor ano " + ano + " UG " + cdUnidadeGestora);
        BaseDespesaCredorDTO dto = new BaseDespesaCredorDTO();
        dto.setDtAnoExercicioFiltro(ano);
        List<BaseDespesaCredorDTO> resultado = consumoApiService.consumirPersistir(dto, Collections.singletonList(cdUnidadeGestora.trim()));
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("BASE_DESPESA_CREDOR", total + " registros processados (ano " + ano + " UG " + cdUnidadeGestora + ")", executionTimeMs);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", total);
        resumo.put("ano", ano);
        resumo.put("cdUnidadeGestora", cdUnidadeGestora);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano concluída. " + total + " registros processados.");
        return resumo;
    }
}
