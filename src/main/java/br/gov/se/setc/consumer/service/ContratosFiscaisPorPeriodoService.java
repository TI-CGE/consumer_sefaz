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
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class ContratosFiscaisPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(ContratosFiscaisPorPeriodoService.class.getName());
    private final ConsumoApiService<ContratosFiscaisDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public ContratosFiscaisPorPeriodoService(
            @Qualifier("contratosFiscaisConsumoApiService") ConsumoApiService<ContratosFiscaisDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE CONTRATOS FISCAIS - ANO INTEIRO " + ano + " ===");
        List<ContratosFiscaisDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("CONTRATOS_FISCAIS", "Consumindo Contratos Fiscais para todos os 12 meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("CONTRATOS_FISCAIS", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            try {
                ContratosFiscaisDTO dto = new ContratosFiscaisDTO();
                dto.setDtAnoExercicioFiltro(ano);
                dto.setNuMesFiltro(mes);
                List<ContratosFiscaisDTO> resultadoMes = consumoApiService.consumirPersistir(dto);
                if (resultadoMes != null && !resultadoMes.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoMes);
                }
                if (mes < 12) {
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Consumo interrompido: " + e.getMessage());
            } catch (Exception e) {
                logger.severe("Erro ao processar ano " + ano + " mês " + mes + ": " + e.getMessage());
                throw new RuntimeException("Erro ao consumir Contratos Fiscais para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("CONTRATOS_FISCAIS", resultadoConsolidado.size() + " registros processados (ano " + ano + ")", executionTimeMs);
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
        List<String> ugFiltro = Collections.singletonList(cdUnidadeGestora.trim());
        logger.info("=== INICIANDO CONSUMO DE CONTRATOS FISCAIS - ANO " + ano + " UG " + cdUnidadeGestora + " ===");
        List<ContratosFiscaisDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("CONTRATOS_FISCAIS", "Consumindo Contratos Fiscais ano " + ano + " UG " + cdUnidadeGestora);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("CONTRATOS_FISCAIS", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            try {
                ContratosFiscaisDTO dto = new ContratosFiscaisDTO();
                dto.setDtAnoExercicioFiltro(ano);
                dto.setNuMesFiltro(mes);
                List<ContratosFiscaisDTO> resultadoMes = consumoApiService.consumirPersistir(dto, ugFiltro);
                if (resultadoMes != null && !resultadoMes.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoMes);
                }
                if (mes < 12) {
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Consumo interrompido: " + e.getMessage());
            } catch (Exception e) {
                logger.severe("Erro ao processar ano " + ano + " mês " + mes + ": " + e.getMessage());
                throw new RuntimeException("Erro ao consumir Contratos Fiscais para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("CONTRATOS_FISCAIS", resultadoConsolidado.size() + " registros processados (ano " + ano + " UG " + cdUnidadeGestora + ")", executionTimeMs);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("status", "SUCCESS");
        resumo.put("recordsProcessed", resultadoConsolidado.size());
        resumo.put("ano", ano);
        resumo.put("cdUnidadeGestora", cdUnidadeGestora);
        resumo.put("executionTimeMs", executionTimeMs);
        resumo.put("message", "Execução por ano inteiro concluída. " + resultadoConsolidado.size() + " registros processados.");
        return resumo;
    }

    public List<ContratosFiscaisDTO> consumirAnoEMes(int ano, int mes) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO CONTRATOS FISCAIS - ANO " + ano + " MÊS " + mes + " ===");
        ContratosFiscaisDTO dto = new ContratosFiscaisDTO();
        dto.setDtAnoExercicioFiltro(ano);
        dto.setNuMesFiltro(mes);
        List<ContratosFiscaisDTO> resultado = consumoApiService.consumirPersistir(dto);
        return resultado != null ? resultado : new ArrayList<>();
    }

    public List<ContratosFiscaisDTO> consumirAnoEMes(int ano, int mes, String cdUnidadeGestora) {
        if (cdUnidadeGestora == null || cdUnidadeGestora.trim().isEmpty()) {
            throw new IllegalArgumentException("cdUnidadeGestora é obrigatório");
        }
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO CONTRATOS FISCAIS - ANO " + ano + " MÊS " + mes + " UG " + cdUnidadeGestora + " ===");
        ContratosFiscaisDTO dto = new ContratosFiscaisDTO();
        dto.setDtAnoExercicioFiltro(ano);
        dto.setNuMesFiltro(mes);
        List<ContratosFiscaisDTO> resultado = consumoApiService.consumirPersistir(dto, Collections.singletonList(cdUnidadeGestora.trim()));
        return resultado != null ? resultado : new ArrayList<>();
    }
}
