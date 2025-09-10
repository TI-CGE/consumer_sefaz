package br.gov.se.setc.consumer.service;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.logging.SimpleLogger;
/**
 * Serviço wrapper para PrevisaoRealizacaoReceita que implementa busca de todos os 12 meses
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
public class PrevisaoRealizacaoReceitaMultiMesService {
    private static final Logger logger = Logger.getLogger(PrevisaoRealizacaoReceitaMultiMesService.class.getName());
    private final ConsumoApiService<PrevisaoRealizacaoReceitaDTO> consumoApiService;
    @Autowired
    private SimpleLogger simpleLogger;
    public PrevisaoRealizacaoReceitaMultiMesService(
            @Qualifier("previsaoRealizacaoReceitaConsumoApiService")
            ConsumoApiService<PrevisaoRealizacaoReceitaDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }
    /**
     * Executa o consumo com busca de todos os 12 meses
     */
    public List<PrevisaoRealizacaoReceitaDTO> consumirTodosMeses() {
        logger.info("=== INICIANDO CONSUMO MULTI-MÊS DE PREVISÃO REALIZAÇÃO RECEITA ===");
        logger.info("Implementação: busca todos os 12 meses do ano para dados completos");
        List<PrevisaoRealizacaoReceitaDTO> resultadoConsolidado = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        simpleLogger.consumptionStart("PREVISAO_REALIZACAO_RECEITA", "Consumindo dados de todos os 12 meses");
        for (int mes = 1; mes <= 12; mes++) {
            simpleLogger.consumptionProgress("PREVISAO_REALIZACAO_RECEITA", "Processando meses", mes, 12, "Mês: " + mes);
            logger.info("=== PROCESSANDO MÊS " + mes + "/12 ===");
            try {
                PrevisaoRealizacaoReceitaDTO mapper = criarMapperParaMes(mes);
                List<PrevisaoRealizacaoReceitaDTO> resultadoMes = consumoApiService.consumirPersistir(mapper);
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
        simpleLogger.consumptionEnd("PREVISAO_REALIZACAO_RECEITA",
                resultadoConsolidado.size() + " registros processados de todos os meses",
                duration);
        logger.info("=== CONSUMO MULTI-MÊS CONCLUÍDO ===");
        logger.info("Total consolidado: " + resultadoConsolidado.size() + " registros");
        return resultadoConsolidado;
    }
    /**
     * Cria um mapper configurado para um mês específico
     */
    private PrevisaoRealizacaoReceitaDTO criarMapperParaMes(int mes) {
        PrevisaoRealizacaoReceitaDTO mapper = new PrevisaoRealizacaoReceitaDTO();
        mapper.setNuMesFiltro(mes);
        logger.info("Mapper configurado para mês " + mes);
        return mapper;
    }
    /**
     * Método de conveniência para execução manual
     */
    public String executarManual() {
        try {
            List<PrevisaoRealizacaoReceitaDTO> resultado = consumirTodosMeses();
            return "✅ Execução multi-mês concluída com sucesso!\n" +
                   "Total de registros processados: " + (resultado != null ? resultado.size() : 0) + "\n" +
                   "Meses processados: 1 a 12\n" +
                   "Tempo estimado: ~6 minutos (500ms entre requisições)";
        } catch (Exception e) {
            logger.severe("Erro durante execução manual multi-mês: " + e.getMessage());
            return "❌ Erro durante execução: " + e.getMessage();
        }
    }
    /**
     * Executa consumo para um mês específico (para testes)
     */
    public List<PrevisaoRealizacaoReceitaDTO> consumirMesEspecifico(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        logger.info("=== CONSUMINDO MÊS ESPECÍFICO: " + mes + " ===");
        try {
            PrevisaoRealizacaoReceitaDTO mapper = criarMapperParaMes(mes);
            List<PrevisaoRealizacaoReceitaDTO> resultado = consumoApiService.consumirPersistir(mapper);
            logger.info("Mês " + mes + " processado: " +
                       (resultado != null ? resultado.size() : 0) + " registros");
            return resultado;
        } catch (Exception e) {
            logger.severe("Erro ao processar mês " + mes + ": " + e.getMessage());
            throw e;
        }
    }
}