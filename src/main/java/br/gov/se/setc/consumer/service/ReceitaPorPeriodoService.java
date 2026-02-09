package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class ReceitaPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(ReceitaPorPeriodoService.class.getName());
    private final ConsumoApiService<ReceitaDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public ReceitaPorPeriodoService(
            @Qualifier("receitaConsumoApiService") ConsumoApiService<ReceitaDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE RECEITA (CONVÊNIO) - ANO INTEIRO " + ano + " ===");
        List<ReceitaDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("RECEITA_CONVENIO", "Consumindo Receita (Convênio) para todos os 12 meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("RECEITA_CONVENIO", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " MÊS " + mes + "/12 ===");
            try {
                ReceitaDTO dto = new ReceitaDTO();
                dto.setNuAnoCelebracaoFiltro(ano);
                dto.setNuMesCelebracaoFiltro(mes);
                List<ReceitaDTO> resultadoMes = consumoApiService.consumirPersistir(dto);
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
                throw new RuntimeException("Erro ao consumir Receita (Convênio) para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("RECEITA_CONVENIO",
                resultadoConsolidado.size() + " registros processados (ano " + ano + ")",
                executionTimeMs);
        logger.info("=== CONSUMO ANO INTEIRO " + ano + " CONCLUÍDO === Total: " + resultadoConsolidado.size() + " registros");
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", resultadoConsolidado.size());
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano inteiro concluída. " + resultadoConsolidado.size() + " registros processados.");
        return resumo;
    }

    public List<ReceitaDTO> consumirAnoEMes(int ano, int mes) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO RECEITA (CONVÊNIO) - ANO " + ano + " MÊS " + mes + " ===");
        long startTime = System.currentTimeMillis();
        ReceitaDTO dto = new ReceitaDTO();
        dto.setNuAnoCelebracaoFiltro(ano);
        dto.setNuMesCelebracaoFiltro(mes);
        List<ReceitaDTO> resultado = consumoApiService.consumirPersistir(dto);
        long executionTimeMs = System.currentTimeMillis() - startTime;
        logger.info("Ano " + ano + " Mês " + mes + " processado: " + (resultado != null ? resultado.size() : 0) + " registros em " + executionTimeMs + " ms");
        return resultado != null ? resultado : new ArrayList<>();
    }
}
