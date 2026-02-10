package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.RestosAPagarDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class RestosAPagarPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(RestosAPagarPorPeriodoService.class.getName());
    private final ConsumoApiService<RestosAPagarDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public RestosAPagarPorPeriodoService(
            @Qualifier("restosAPagarConsumoApiService") ConsumoApiService<RestosAPagarDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE RESTOS A PAGAR - ANO " + ano + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("RESTOS_A_PAGAR", "Consumindo Restos a Pagar para o ano " + ano);
        RestosAPagarDTO dto = new RestosAPagarDTO();
        dto.setDtAnoExercicioCTBFiltro(ano);
        List<RestosAPagarDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("RESTOS_A_PAGAR", total + " registros processados (ano " + ano + ")", executionTimeMs);
        logger.info("=== CONSUMO RESTOS A PAGAR ANO " + ano + " CONCLUÍDO === Total: " + total + " registros");
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
        logger.info("=== INICIANDO CONSUMO DE RESTOS A PAGAR - ANO " + ano + " UG " + cdUnidadeGestora + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("RESTOS_A_PAGAR", "Consumindo Restos a Pagar ano " + ano + " UG " + cdUnidadeGestora);
        RestosAPagarDTO dto = new RestosAPagarDTO();
        dto.setDtAnoExercicioCTBFiltro(ano);
        List<RestosAPagarDTO> resultado = consumoApiService.consumirPersistir(dto, Collections.singletonList(cdUnidadeGestora.trim()));
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("RESTOS_A_PAGAR", total + " registros processados (ano " + ano + " UG " + cdUnidadeGestora + ")", executionTimeMs);
        logger.info("=== CONSUMO RESTOS A PAGAR ANO " + ano + " UG " + cdUnidadeGestora + " CONCLUÍDO === Total: " + total + " registros");
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
