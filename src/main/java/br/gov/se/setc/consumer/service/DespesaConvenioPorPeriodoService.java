package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.DespesaConvenioDTO;
import br.gov.se.setc.logging.SimpleLogger;

@Service
public class DespesaConvenioPorPeriodoService {
    private static final int ANO_MIN = 2000;
    private static final int ANO_MAX = 2030;
    private static final Logger logger = Logger.getLogger(DespesaConvenioPorPeriodoService.class.getName());
    private final ConsumoApiService<DespesaConvenioDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;

    public DespesaConvenioPorPeriodoService(
            @Qualifier("despesaConvenioConsumoApiService") ConsumoApiService<DespesaConvenioDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }

    public Map<String, Object> consumirAnoInteiro(int ano) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        logger.info("=== INICIANDO CONSUMO DE DESPESA CONVÊNIO - ANO INTEIRO " + ano + " ===");
        List<DespesaConvenioDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("DESPESA_CONVENIO", "Consumindo Despesa Convênio para todos os 12 meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("DESPESA_CONVENIO", "Processando meses ano " + ano, mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " MÊS " + mes + "/12 ===");
            try {
                DespesaConvenioDTO dto = new DespesaConvenioDTO();
                dto.setNuAnoLancamentoFiltro(ano);
                dto.setNuMesLancamentoFiltro(mes);
                List<DespesaConvenioDTO> resultadoMes = consumoApiService.consumirPersistir(dto);
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
                throw new RuntimeException("Erro ao consumir Despesa Convênio para ano " + ano + " mês " + mes + ": " + e.getMessage(), e);
            }
        }
        long executionTimeMs = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("DESPESA_CONVENIO",
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

    public List<DespesaConvenioDTO> consumirAnoEMes(int ano, int mes) {
        if (ano < ANO_MIN || ano > ANO_MAX) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MIN + " e " + ANO_MAX);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO DESPESA CONVÊNIO - ANO " + ano + " MÊS " + mes + " ===");
        DespesaConvenioDTO dto = new DespesaConvenioDTO();
        dto.setNuAnoLancamentoFiltro(ano);
        dto.setNuMesLancamentoFiltro(mes);
        List<DespesaConvenioDTO> resultado = consumoApiService.consumirPersistir(dto);
        logger.info("Ano " + ano + " Mês " + mes + " processado: " + (resultado != null ? resultado.size() : 0) + " registros");
        return resultado != null ? resultado : new ArrayList<>();
    }
}
