package br.gov.se.setc.consumer.service;
import br.gov.se.setc.consumer.dto.DespesaDetalhadaDTO;
import br.gov.se.setc.logging.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
/**
 * Serviço wrapper para DespesaDetalhada que implementa busca de todos os 12 meses
 *
 * Este serviço funciona como um wrapper em torno do ConsumoApiService padrão,
 * fazendo 12 requisições sequenciais (uma para cada mês) e consolidando os resultados.
 *
 * Funcionalidades:
 * - 12 requisições sequenciais (mês 1 a 12)
 * - Pausa de 500ms entre requisições
 * - Logs detalhados do progresso
 * - Consolidação automática dos dados
 * - Compatibilidade total com arquitetura existente
 */
@Service
public class DespesaDetalhadaMultiMesService {
    private static final Logger logger = Logger.getLogger(DespesaDetalhadaMultiMesService.class.getName());
    private final ConsumoApiService<DespesaDetalhadaDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;
    public DespesaDetalhadaMultiMesService(
            @Qualifier("despesaDetalhadaConsumoApiService")
            ConsumoApiService<DespesaDetalhadaDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }
    /**
     * Executa o consumo com busca de todos os 12 meses
     */
    public List<DespesaDetalhadaDTO> consumirTodosMeses() {
        logger.info("=== INICIANDO CONSUMO MULTI-MÊS DE DESPESA DETALHADA ===");
        logger.info("Implementação: busca todos os 12 meses do ano para dados completos");
        List<DespesaDetalhadaDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("DESPESA_DETALHADA", "Consumindo dados de todos os 12 meses");
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("DESPESA_DETALHADA", "Processando meses", mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO MÊS " + mes + "/12 ===");
            try {
                DespesaDetalhadaDTO mapper = criarMapperParaMes(mes);
                List<DespesaDetalhadaDTO> resultadoMes = consumoApiService.consumirPersistir(mapper);
                if (resultadoMes != null && !resultadoMes.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoMes);
                    logger.info("Mês " + mes + ": " + resultadoMes.size() + " registros processados");
                } else {
                    logger.info("Mês " + mes + ": 0 registros encontrados");
                }
                if (mes < 12) {
                    logger.info("Pausando 500ms antes do próximo mês...");
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                logger.severe("Erro ao processar mês " + mes + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("DESPESA_DETALHADA",
                resultadoConsolidado.size() + " registros processados de todos os meses",
                duration);
        logger.info("=== CONSUMO MULTI-MÊS CONCLUÍDO ===");
        logger.info("Total de registros consolidados: " + resultadoConsolidado.size());
        return resultadoConsolidado;
    }
    /**
     * Executa consumo para um mês específico (para testes)
     */
    public List<DespesaDetalhadaDTO> consumirMesEspecifico(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO MÊS ESPECÍFICO: " + mes + " ===");
        try {
            DespesaDetalhadaDTO mapper = criarMapperParaMes(mes);
            List<DespesaDetalhadaDTO> resultado = consumoApiService.consumirPersistir(mapper);
            logger.info("Mês " + mes + " processado: " +
                       (resultado != null ? resultado.size() : 0) + " registros");
            return resultado;
        } catch (Exception e) {
            logger.severe("Erro ao processar mês específico " + mes + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    /**
     * Executa consumo para todos os 12 meses de um ano específico
     */
    public List<DespesaDetalhadaDTO> consumirTodosMesesAno(int ano) {
        logger.info("=== INICIANDO CONSUMO MULTI-MÊS PARA ANO " + ano + " ===");
        List<DespesaDetalhadaDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("DESPESA_DETALHADA", "Consumindo dados de todos os meses do ano " + ano);
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("DESPESA_DETALHADA", "Processando meses do ano " + ano, mes, 12,
                    "Ano: " + ano + " | Mês: " + mes);
            logger.info("=== PROCESSANDO ANO " + ano + " - MÊS " + mes + "/12 ===");
            try {
                DespesaDetalhadaDTO mapper = criarMapperParaMesAno(mes, ano);
                List<DespesaDetalhadaDTO> resultadoMes = consumoApiService.consumirPersistir(mapper);
                if (resultadoMes != null && !resultadoMes.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoMes);
                    logger.info("Ano " + ano + " Mês " + mes + ": " + resultadoMes.size() + " registros processados");
                } else {
                    logger.info("Ano " + ano + " Mês " + mes + ": 0 registros encontrados");
                }
                if (mes < 12) {
                    logger.info("Pausando 500ms antes do próximo mês...");
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                logger.severe("Erro ao processar ano " + ano + " mês " + mes + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("DESPESA_DETALHADA",
                resultadoConsolidado.size() + " registros processados do ano " + ano,
                duration);
        logger.info("=== CONSUMO MULTI-MÊS PARA ANO " + ano + " CONCLUÍDO ===");
        logger.info("Total de registros consolidados: " + resultadoConsolidado.size());
        return resultadoConsolidado;
    }
    /**
     * Cria um mapper configurado para um mês específico (ano atual)
     */
    private DespesaDetalhadaDTO criarMapperParaMes(int mes) {
        DespesaDetalhadaDTO mapper = new DespesaDetalhadaDTO();
        mapper.setNuMesFiltro(mes);
        mapper.setDtAnoExercicioCTBFiltro(2025);
        mapper.setNuMes(mes);
        mapper.setDtAnoExercicioCTB(2025);
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("nuMes", mes);
        parametros.put("dtAnoExercicio", 2025);
        mapper.setCamposParametros(parametros);
        logger.info("Mapper criado - mes=" + mes + ", ano=2025");
        return mapper;
    }
    /**
     * Cria um mapper configurado para um mês e ano específicos
     */
    private DespesaDetalhadaDTO criarMapperParaMesAno(int mes, int ano) {
        DespesaDetalhadaDTO mapper = new DespesaDetalhadaDTO();
        mapper.setNuMesFiltro(mes);
        mapper.setDtAnoExercicioCTBFiltro(ano);
        mapper.setNuMes(mes);
        mapper.setDtAnoExercicioCTB(ano);
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("nuMes", mes);
        parametros.put("dtAnoExercicio", ano);
        mapper.setCamposParametros(parametros);
        logger.info("Mapper criado - mes=" + mes + ", ano=" + ano);
        return mapper;
    }
    /**
     * Executa consumo para todos os anos (2020-2025) e todos os 12 meses de cada ano
     */
    public List<DespesaDetalhadaDTO> consumirTodosAnosMeses() {
        logger.info("=== INICIANDO CONSUMO COMPLETO - TODOS OS ANOS E MESES ===");
        List<DespesaDetalhadaDTO> resultadoConsolidado = new ArrayList<>();
        int[] anos = {2020, 2021, 2022, 2023, 2024, 2025};
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("DESPESA_DETALHADA", "Consumindo dados de todos os anos e meses (2020-2025)");
        int anoProcessado = 0;
        for (int ano : anos) {
            anoProcessado++;
            simpleLogger.consumptionProgress("DESPESA_DETALHADA", "Processando anos completos", anoProcessado, anos.length,
                    "Ano: " + ano);
            logger.info("=== PROCESSANDO ANO " + ano + " ===");
            try {
                List<DespesaDetalhadaDTO> resultadoAno = consumirTodosMesesAno(ano);
                if (resultadoAno != null && !resultadoAno.isEmpty()) {
                    resultadoConsolidado.addAll(resultadoAno);
                    logger.info("Ano " + ano + " concluído: " + resultadoAno.size() + " registros");
                } else {
                    logger.info("Ano " + ano + ": 0 registros encontrados");
                }
                if (ano < 2025) {
                    logger.info("Pausando 1 segundo antes do próximo ano...");
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                logger.severe("Erro ao processar ano " + ano + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        simpleLogger.consumptionEnd("DESPESA_DETALHADA",
                resultadoConsolidado.size() + " registros processados de todos os anos e meses",
                duration);
        logger.info("=== CONSUMO COMPLETO CONCLUÍDO ===");
        logger.info("Total de registros consolidados: " + resultadoConsolidado.size());
        return resultadoConsolidado;
    }
}