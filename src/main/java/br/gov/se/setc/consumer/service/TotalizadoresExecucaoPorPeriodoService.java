package br.gov.se.setc.consumer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.TotalizadoresExecucaoDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class TotalizadoresExecucaoPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(TotalizadoresExecucaoPorPeriodoService.class.getName());
    private final ConsumoApiService<TotalizadoresExecucaoDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public TotalizadoresExecucaoPorPeriodoService(
            @Qualifier("totalizadoresExecucaoConsumoApiService") ConsumoApiService<TotalizadoresExecucaoDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE TOTALIZADORES EXECUÇÃO - ANO " + ano + " ===");
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("TOTALIZADORES_EXECUCAO", "Consumindo Totalizadores Execução para o ano " + ano);
        TotalizadoresExecucaoDTO dto = new TotalizadoresExecucaoDTO();
        dto.setDtAnoExercicioCTBFiltro(ano);
        List<TotalizadoresExecucaoDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        int total = resultado != null ? resultado.size() : 0;
        simpleLogger.consumptionEnd("TOTALIZADORES_EXECUCAO", total + " registros processados (ano " + ano + ")", executionTimeMs);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", total);
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano concluída. " + total + " registros processados.");
        return resumo;
    }
}
