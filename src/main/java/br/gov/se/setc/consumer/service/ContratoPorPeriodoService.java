package br.gov.se.setc.consumer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class ContratoPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(ContratoPorPeriodoService.class.getName());
    private final ConsumoApiService<ContratoDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public ContratoPorPeriodoService(
            @Qualifier("contratoConsumoApiService") ConsumoApiService<ContratoDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE CONTRATO - ANO " + ano + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("CONTRATO", "Consumindo Contrato para o ano " + ano);
        ContratoDTO dto = new ContratoDTO();
        dto.setDtAnoExercicioFiltro(ano);
        List<ContratoDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("CONTRATO", total + " registros processados (ano " + ano + ")", executionTimeMs);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", total);
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano concluída. " + total + " registros processados.");
        return resumo;
    }
}
