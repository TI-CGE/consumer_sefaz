package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class ConsultaGerencialPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(ConsultaGerencialPorPeriodoService.class.getName());
    private final ConsumoApiService<ConsultaGerencialDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public ConsultaGerencialPorPeriodoService(
            @Qualifier("consultaGerencialConsumoApiService") ConsumoApiService<ConsultaGerencialDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE CONSULTA GERENCIAL - ANO INTEIRO " + ano + " ===");
        List<ConsultaGerencialDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("CONSULTA_GERENCIAL", "Consumindo Consulta Gerencial para todos os 12 meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("CONSULTA_GERENCIAL", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " MÊS " + mes + "/12 ===");
            try {
                ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
                dto.setDtAnoExercicioCTBFiltro(ano);
                dto.setNuMesFiltro(mes);
                List<ConsultaGerencialDTO> resultadoMes = consumoApiService.consumirPersistir(dto);
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
                throw new RuntimeException("Erro ao consumir Consulta Gerencial para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("CONSULTA_GERENCIAL",
                resultadoConsolidado.size() + " registros processados (ano " + ano + ")",
                executionTimeMs);
        logger.info("=== CONSUMO CONSULTA GERENCIAL ANO INTEIRO " + ano + " CONCLUÍDO === Total: " + resultadoConsolidado.size() + " registros");
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", resultadoConsolidado.size());
        resumo.put("ano", ano);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano inteiro concluída. " + resultadoConsolidado.size() + " registros processados.");
        return resumo;
    }

    public List<ConsultaGerencialDTO> consumirAnoEMes(int ano, int mes) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO CONSULTA GERENCIAL - ANO " + ano + " MÊS " + mes + " ===");
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setDtAnoExercicioCTBFiltro(ano);
        dto.setNuMesFiltro(mes);
        List<ConsultaGerencialDTO> resultado = consumoApiService.consumirPersistir(dto);
        logger.info("Ano " + ano + " Mês " + mes + " processado: " + (resultado != null ? resultado.size() : 0) + " registros");
        return resultado != null ? resultado : new ArrayList<>();
    }
}
