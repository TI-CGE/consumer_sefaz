package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class PagamentoPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(PagamentoPorPeriodoService.class.getName());
    private final ConsumoApiService<PagamentoDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public PagamentoPorPeriodoService(
            @Qualifier("pagamentoConsumoApiService") ConsumoApiService<PagamentoDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE PAGAMENTO - ANO INTEIRO " + ano + " ===");
        List<PagamentoDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("PAGAMENTO", "Consumindo Pagamento para todos os 12 meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("PAGAMENTO", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " MÊS " + mes + "/12 ===");
            try {
                PagamentoDTO dto = new PagamentoDTO();
                dto.setDtAnoExercicioCTBFiltro(ano);
                dto.setNuMesFiltro(mes);
                List<PagamentoDTO> resultadoMes = consumoApiService.consumirPersistir(dto);
                if (resultadoMes != null && !resultadoMes.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoMes);
                    logger.info("Ano " + ano + " Mês " + mes + ": " + resultadoMes.size() + " registros processados");
                } else {
                    logger.info("Ano " + ano + " Mês " + mes + ": 0 registros encontrados");
                }
                if (mes < 12) {
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Consumo interrompido: " + e.getMessage());
            } catch (Exception e) {
                logger.severe("Erro ao processar ano " + ano + " mês " + mes + ": " + e.getMessage());
                throw new RuntimeException("Erro ao consumir Pagamento para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("PAGAMENTO",
                resultadoConsolidado.size() + " registros processados (ano " + ano + ")",
                executionTimeMs);
        logger.info("=== CONSUMO PAGAMENTO ANO INTEIRO " + ano + " CONCLUÍDO === Total: " + resultadoConsolidado.size() + " registros");
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", resultadoConsolidado.size());
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano inteiro concluída. " + resultadoConsolidado.size() + " registros processados.");
        return resumo;
    }

    public List<PagamentoDTO> consumirAnoEMes(int ano, int mes) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO PAGAMENTO - ANO " + ano + " MÊS " + mes + " ===");
        long startTime = System.currentTimeMillis();
        PagamentoDTO dto = new PagamentoDTO();
        dto.setDtAnoExercicioCTBFiltro(ano);
        dto.setNuMesFiltro(mes);
        List<PagamentoDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        logger.info("Ano " + ano + " Mês " + mes + " processado: " + (resultado != null ? resultado.size() : 0) + " registros em " + executionTimeMs + " ms");
        return resultado != null ? resultado : new ArrayList<>();
    }
}
