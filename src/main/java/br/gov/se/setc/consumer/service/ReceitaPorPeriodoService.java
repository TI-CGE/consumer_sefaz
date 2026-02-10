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
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.logging.SimpleLogger;
import br.gov.se.setc.scheduler.ReceitaConvenioExecutionLock;

@Service
public class ReceitaPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(ReceitaPorPeriodoService.class.getName());
    private final ConsumoApiService<ReceitaDTO> consumoApiService;
    private final ReceitaConvenioExecutionLock receitaConvenioExecutionLock;
    @Autowired
    private SimpleLogger simpleLogger;

    public ReceitaPorPeriodoService(
            @Qualifier("receitaConsumoApiService") ConsumoApiService<ReceitaDTO> consumoApiService,
            ReceitaConvenioExecutionLock receitaConvenioExecutionLock) {
        this.consumoApiService = consumoApiService;
        this.receitaConvenioExecutionLock = receitaConvenioExecutionLock;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (!receitaConvenioExecutionLock.tryStart()) {
            logger.warning("Receita (Convênio): consumo já em andamento, ignorando consumirAnoInteiro(" + ano + ").");
            Map<String, Object> skip = new HashMap<>();
            skip.put("status", "ALREADY_RUNNING");
            skip.put("message", "Consumo de Receita (Convênio) já está em andamento. Solicitação ignorada.");
            skip.put("ano", ano);
            return skip;
        }
        try {
            return consumirAnoInteiroInternal(ano);
        } finally {
            receitaConvenioExecutionLock.finish();
        }
    }

    private Map<String, Object> consumirAnoInteiroInternal(int ano) {
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

    public Map<String, Object> consumirAnoInteiro(int ano, String cdUnidadeGestora) {
        if (cdUnidadeGestora == null || cdUnidadeGestora.trim().isEmpty()) {
            throw new IllegalArgumentException("cdUnidadeGestora é obrigatório");
        }
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (!receitaConvenioExecutionLock.tryStart()) {
            logger.warning("Receita (Convênio): consumo já em andamento, ignorando consumirAnoInteiro(" + ano + ", " + cdUnidadeGestora + ").");
            Map<String, Object> skip = new HashMap<>();
            skip.put("status", "ALREADY_RUNNING");
            skip.put("message", "Consumo de Receita (Convênio) já está em andamento. Solicitação ignorada.");
            skip.put("ano", ano);
            skip.put("cdUnidadeGestora", cdUnidadeGestora);
            return skip;
        }
        try {
            return consumirAnoInteiroInternal(ano, cdUnidadeGestora);
        } finally {
            receitaConvenioExecutionLock.finish();
        }
    }

    private Map<String, Object> consumirAnoInteiroInternal(int ano, String cdUnidadeGestora) {
        List<String> ugFiltro = Collections.singletonList(cdUnidadeGestora.trim());
        logger.info("=== INICIANDO CONSUMO DE RECEITA (CONVÊNIO) - ANO " + ano + " UG " + cdUnidadeGestora + " ===");
        List<ReceitaDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("RECEITA_CONVENIO", "Consumindo Receita (Convênio) ano " + ano + " UG " + cdUnidadeGestora);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("RECEITA_CONVENIO", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " MÊS " + mes + "/12 UG " + cdUnidadeGestora + " ===");
            try {
                ReceitaDTO dto = new ReceitaDTO();
                dto.setNuAnoCelebracaoFiltro(ano);
                dto.setNuMesCelebracaoFiltro(mes);
                List<ReceitaDTO> resultadoMes = consumoApiService.consumirPersistir(dto, ugFiltro);
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
                resultadoConsolidado.size() + " registros processados (ano " + ano + " UG " + cdUnidadeGestora + ")",
                executionTimeMs);
        logger.info("=== CONSUMO ANO INTEIRO " + ano + " UG " + cdUnidadeGestora + " CONCLUÍDO === Total: " + resultadoConsolidado.size() + " registros");
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", resultadoConsolidado.size());
        resumo.put("ano", ano);
        resumo.put("cdUnidadeGestora", cdUnidadeGestora);
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
        if (!receitaConvenioExecutionLock.tryStart()) {
            logger.warning("Receita (Convênio): consumo já em andamento.");
            throw new IllegalStateException("Consumo de Receita (Convênio) já está em andamento. Tente novamente mais tarde.");
        }
        try {
            logger.info("=== CONSUMINDO RECEITA (CONVÊNIO) - ANO " + ano + " MÊS " + mes + " ===");
            long startTime = System.currentTimeMillis();
            ReceitaDTO dto = new ReceitaDTO();
            dto.setNuAnoCelebracaoFiltro(ano);
            dto.setNuMesCelebracaoFiltro(mes);
            List<ReceitaDTO> resultado = consumoApiService.consumirPersistir(dto);
            long executionTimeMs = System.currentTimeMillis() - startTime;
            logger.info("Ano " + ano + " Mês " + mes + " processado: " + (resultado != null ? resultado.size() : 0) + " registros em " + executionTimeMs + " ms");
            return resultado != null ? resultado : new ArrayList<>();
        } finally {
            receitaConvenioExecutionLock.finish();
        }
    }

    public List<ReceitaDTO> consumirAnoEMes(int ano, int mes, String cdUnidadeGestora) {
        if (cdUnidadeGestora == null || cdUnidadeGestora.trim().isEmpty()) {
            throw new IllegalArgumentException("cdUnidadeGestora é obrigatório");
        }
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        if (!receitaConvenioExecutionLock.tryStart()) {
            logger.warning("Receita (Convênio): consumo já em andamento.");
            throw new IllegalStateException("Consumo de Receita (Convênio) já está em andamento. Tente novamente mais tarde.");
        }
        try {
            logger.info("=== CONSUMINDO RECEITA (CONVÊNIO) - ANO " + ano + " MÊS " + mes + " UG " + cdUnidadeGestora + " ===");
            long startTime = System.currentTimeMillis();
            ReceitaDTO dto = new ReceitaDTO();
            dto.setNuAnoCelebracaoFiltro(ano);
            dto.setNuMesCelebracaoFiltro(mes);
            List<ReceitaDTO> resultado = consumoApiService.consumirPersistir(dto, Collections.singletonList(cdUnidadeGestora.trim()));
            long executionTimeMs = System.currentTimeMillis() - startTime;
            logger.info("Ano " + ano + " Mês " + mes + " UG " + cdUnidadeGestora + " processado: " + (resultado != null ? resultado.size() : 0) + " registros em " + executionTimeMs + " ms");
            return resultado != null ? resultado : new ArrayList<>();
        } finally {
            receitaConvenioExecutionLock.finish();
        }
    }
}
