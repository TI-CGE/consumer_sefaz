package br.gov.se.setc.consumer.respository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;


@Repository

public class EndpontSefazRepository<T extends EndpontSefaz> {

    private final JdbcTemplate jdbcTemplate;
    private final UnifiedLogger unifiedLogger;

    @Autowired
    public EndpontSefazRepository(JdbcTemplate jdbcTemplate, UnifiedLogger unifiedLogger) {
        this.jdbcTemplate = jdbcTemplate;
        this.unifiedLogger = unifiedLogger;
    }

    @LogOperation(operation = "INSERT_BATCH", component = "DATABASE", slowOperationThresholdMs = 10000)
    public void insert(List<T> contratos){

        if (contratos == null || contratos.isEmpty()) {
            return;
        }

        String tableName = contratos.get(0).getTabelaBanco();
        long startTime = System.currentTimeMillis();

        unifiedLogger.logOperationStart("DATABASE", "INSERT_BATCH", "TABLE", tableName, "COUNT", contratos.size());

        try {
            int total = contratos.size();
            int batchSize = 1000; // Tamanho do lote para evitar timeouts
            int processedTotal = 0;

            // Processar em lotes menores
            for (int i = 0; i < total; i += batchSize) {
                int endIndex = Math.min(i + batchSize, total);
                List<T> batch = contratos.subList(i, endIndex);

                // Log de início do lote
                long batchStartTime = System.currentTimeMillis();

                // Processar lote atual
                processBatch(batch, tableName);

                processedTotal += batch.size();
                long batchTime = System.currentTimeMillis() - batchStartTime;

                // Log de progresso do lote
                unifiedLogger.logDatabaseOperation("INSERT_BATCH_PROGRESS", tableName, batch.size(), batchTime);

                // Pequena pausa entre lotes para evitar sobrecarga
                if (endIndex < total) {
                    try {
                        Thread.sleep(100); // 100ms de pausa
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationSuccess("DATABASE", "INSERT_BATCH", executionTime, processedTotal, "TABLE", tableName);
            unifiedLogger.logDatabaseOperation("INSERT", tableName, processedTotal, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationError("DATABASE", "INSERT_BATCH", executionTime, e, "TABLE", tableName);
            throw e;
        }
    }

    /**
     * Processa um lote de registros usando batch insert otimizado
     */
    private void processBatch(List<T> batch, String tableName) {
        if (batch.isEmpty()) return;

        // Usar o primeiro item para obter a estrutura da query
        T firstItem = batch.get(0);
        Map<String, Object> fieldMap = firstItem.getCamposResposta();

        String columns = String.join(", ", fieldMap.keySet());
        String placeholders = fieldMap.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        String insertSql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        // Usar batchUpdate para melhor performance
        List<Object[]> batchArgs = new ArrayList<>();
        for (T item : batch) {
            Map<String, Object> itemFieldMap = item.getCamposResposta();
            batchArgs.add(itemFieldMap.values().toArray());
        }

        jdbcTemplate.batchUpdate(insertSql, batchArgs);
    }

    @LogOperation(operation = "DELETE_BY_MONTH", component = "DATABASE")
    public void deleteByMesVigente(T endpointInstance) {
        String tableName = endpointInstance.getTabelaBanco();
        long startTime = System.currentTimeMillis();
        String deleteSql;

        // Estratégia específica para previsao_realizacao_receita
        if ("consumer_sefaz.previsao_realizacao_receita".equals(tableName)) {
            // Para previsao_realizacao_receita, deletar TODOS os registros do ano atual
            // Isso é necessário porque o serviço processa todos os 12 meses
            deleteSql = "DELETE FROM " + tableName + " " +
                    "WHERE dt_ano_exercicio_ctb = EXTRACT(YEAR FROM CURRENT_DATE)";
        }
        // Estratégia específica para despesa_detalhada
        else if ("consumer_sefaz.despesa_detalhada".equals(tableName)) {
            // Para despesa_detalhada, deletar TODOS os registros do ano atual
            // Isso é necessário porque o serviço pode processar múltiplos meses
            // e não possui campos de data padrão para filtros
            deleteSql = "DELETE FROM " + tableName + " " +
                    "WHERE dt_ano_exercicio_ctb = EXTRACT(YEAR FROM CURRENT_DATE)";
        } else {
            // Estratégia padrão para outros endpoints
            if(endpointInstance.getNomeDataInicialPadraoFiltro() == null || endpointInstance.getNomeDataFinalPadraoFiltro() == null){
                return;
            }

            deleteSql = "DELETE FROM "+endpointInstance.getTabelaBanco()+ "  " +
                    "WHERE EXTRACT(YEAR FROM "+endpointInstance.getNomeDataInicialPadraoFiltro()+" ) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                    "AND EXTRACT(MONTH FROM "+endpointInstance.getNomeDataFinalPadraoFiltro()+" ) = EXTRACT(MONTH FROM CURRENT_DATE)";
        }

        try {
            int deletedRecords = jdbcTemplate.update(deleteSql);
            long executionTime = System.currentTimeMillis() - startTime;

            unifiedLogger.logDatabaseOperation("DELETE", tableName, deletedRecords, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationError("DATABASE", "DELETE_BY_MONTH", executionTime, e, "TABLE", tableName);
            throw e;
        }
    }

    public void persist(List<T> contratos) {
        if (contratos != null && !contratos.isEmpty()) {
            // Use the first contract to get endpoint configuration
            T amostraParaGeral = contratos.get(0);

            deleteByMesVigente(amostraParaGeral);
            insert(contratos);
        }
    }



    public void persistContratosFiscais(List<ContratosFiscaisDTO> contratos) {
        String deleteSql = "DELETE FROM consumer_sefaz.contratos_fiscais " +
                "WHERE EXTRACT(YEAR FROM dt_inicio_vigencia_contrato) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "AND EXTRACT(MONTH FROM dt_inicio_vigencia_contrato) = EXTRACT(MONTH FROM CURRENT_DATE)";
        jdbcTemplate.update(deleteSql);
///System.out.println("Registros removidos com sucesso!");

        String insertSql = "INSERT INTO consumer_sefaz.contratos_fiscais (sg_unidade_gestora, cd_unidade_gestora, dt_ano_exercicio, " +
                "cd_contrato, cd_licitacao, dt_inicio_vigencia_contrato, dt_fim_vigencia_contrato, " +
                "nm_contratado, nu_documento_contratado, nm_fiscal, ds_qualificador) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (ContratosFiscaisDTO contrato : contratos) {
            jdbcTemplate.update(insertSql,
                    contrato.getSgUnidadeGestora(),
                    contrato.getCdUnidadeGestora(),
                    contrato.getDtAnoExercicio(),
                    contrato.getCdContrato(),
                    contrato.getCdLicitacao(),
                    contrato.getDtInicioVigenciaContrato(),
                    contrato.getDtFimVigenciaContrato(),
                    contrato.getNmContratado(),
                    contrato.getNuDocumentoContratado(),
                    contrato.getNmFiscal(),
                    contrato.getDsQualificador()
            );
 //           System.out.println("Registro inserido com sucesso!");
        }
    }

    
}

