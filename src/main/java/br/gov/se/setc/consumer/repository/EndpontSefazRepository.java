package br.gov.se.setc.consumer.repository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
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
            int batchSize = 1000;
            int processedTotal = 0;
            for (int i = 0; i < total; i += batchSize) {
                int endIndex = Math.min(i + batchSize, total);
                List<T> batch = contratos.subList(i, endIndex);
                long batchStartTime = System.currentTimeMillis();
                processBatch(batch, tableName);
                processedTotal += batch.size();
                long batchTime = System.currentTimeMillis() - batchStartTime;
                unifiedLogger.logDatabaseOperation("INSERT_BATCH_PROGRESS", tableName, batch.size(), batchTime);
                if (endIndex < total) {
                    try {
                        Thread.sleep(100);
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
    private void processBatch(List<T> batch, String tableName) {
        if (batch.isEmpty()) return;
        T firstItem = batch.get(0);
        Map<String, Object> fieldMap = firstItem.getCamposResposta();
        String columns = String.join(", ", fieldMap.keySet());
        String placeholders = fieldMap.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        String sql;
        if ("consumer_sefaz.despesa_detalhada".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("dt_ano_exercicio_ctb", "nu_mes", "cd_orgao", "cd_unid_orc", "cd_natureza_despesa", "cd_ppa_acao", "cd_sub_acao"));
        } else if ("consumer_sefaz.previsao_realizacao_receita".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("cd_unidade_gestora", "dt_ano_exercicio_ctb", "nu_mes", "cd_categoria_economica", "cd_origem", "cd_especie", "cd_desdobramento", "cd_tipo"));
        } else if ("consumer_sefaz.empenho".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho"));
        } else if ("consumer_sefaz.pagamento".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho", "sq_ob"));
        } else if ("consumer_sefaz.liquidacao".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho", "sq_liquidacao"));
        } else if ("consumer_sefaz.restos_a_pagar".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("dt_ano_exercicio_ctb", "cd_unidade_gestora", "cd_gestao", "sq_empenho"));
        } else if ("consumer_sefaz.contrato_empenho".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("cd_solicitacao_compra", "ug_cd", "dt_ano_exercicio"));
        } else if ("consumer_sefaz.ordem_fornecimento".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("cd_unidade_gestora", "cd_gestao", "dt_ano_exercicio_emp", "sq_empenho", "sq_ordem_fornecimento"));
        } else if ("consumer_sefaz.receita".equals(tableName)) {
            sql = buildUpsertSql(tableName, columns, fieldMap.keySet(),
                    Arrays.asList("cd_convenio"));
        } else {
            sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        }
        List<Object[]> batchArgs = new ArrayList<>();
        for (T item : batch) {
            Map<String, Object> itemFieldMap = item.getCamposResposta();
            batchArgs.add(itemFieldMap.values().toArray());
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private String buildUpsertSql(String tableName, String columns, Set<String> columnSet, List<String> conflictColumnsOrdered) {
        String placeholders = columnSet.stream().map(k -> "?").collect(Collectors.joining(", "));
        String conflictColumns = String.join(", ", conflictColumnsOrdered);
        Set<String> conflictSet = new LinkedHashSet<>(conflictColumnsOrdered);
        List<String> updateSet = columnSet.stream()
                .filter(c -> !conflictSet.contains(c))
                .map(c -> c + " = EXCLUDED." + c)
                .collect(Collectors.toList());
        String updateClause = String.join(", ", updateSet);
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ") " +
                "ON CONFLICT (" + conflictColumns + ") DO UPDATE SET " + updateClause;
    }
    @LogOperation(operation = "DELETE_BY_MONTH", component = "DATABASE")
    public void deleteByMesVigente(T endpointInstance) {
        String tableName = endpointInstance.getTabelaBanco();
        long startTime = System.currentTimeMillis();
        String deleteSql;
        if ("consumer_sefaz.previsao_realizacao_receita".equals(tableName)) {
            Integer nuMes = obterNuMesDoEndpoint(endpointInstance);
            Integer dtAno = obterDtAnoExercicioCtbDoEndpoint(endpointInstance);
            int anoDelete = dtAno != null ? dtAno : java.time.Year.now().getValue();
            if (nuMes != null) {
                deleteSql = "DELETE FROM " + tableName + " " +
                        "WHERE dt_ano_exercicio_ctb = ? AND nu_mes = ?";
                try {
                    int deletedRecords = jdbcTemplate.update(deleteSql, anoDelete, nuMes);
                    long executionTime = System.currentTimeMillis() - startTime;
                    unifiedLogger.logDatabaseOperation("DELETE", tableName, deletedRecords, executionTime);
                } catch (Exception e) {
                    long executionTime = System.currentTimeMillis() - startTime;
                    unifiedLogger.logOperationError("DATABASE", "DELETE_BY_MONTH", executionTime, e, "TABLE", tableName);
                    throw e;
                }
                return;
            }
            deleteSql = "DELETE FROM " + tableName + " " +
                    "WHERE dt_ano_exercicio_ctb = ?";
            try {
                int deletedRecords = jdbcTemplate.update(deleteSql, anoDelete);
                long executionTime = System.currentTimeMillis() - startTime;
                unifiedLogger.logDatabaseOperation("DELETE", tableName, deletedRecords, executionTime);
            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                unifiedLogger.logOperationError("DATABASE", "DELETE_BY_MONTH", executionTime, e, "TABLE", tableName);
                throw e;
            }
            return;
        }
        else if ("consumer_sefaz.despesa_detalhada".equals(tableName)) {
            Integer nuMes = obterNuMesDoEndpoint(endpointInstance);
            Integer dtAno = obterDtAnoExercicioCtbDoEndpoint(endpointInstance);
            int anoDelete = dtAno != null ? dtAno : java.time.Year.now().getValue();
            if (nuMes != null) {
                deleteSql = "DELETE FROM " + tableName + " " +
                        "WHERE dt_ano_exercicio_ctb = ? AND nu_mes = ?";
                try {
                    int deletedRecords = jdbcTemplate.update(deleteSql, anoDelete, nuMes);
                    long executionTime = System.currentTimeMillis() - startTime;
                    unifiedLogger.logDatabaseOperation("DELETE", tableName, deletedRecords, executionTime);
                } catch (Exception e) {
                    long executionTime = System.currentTimeMillis() - startTime;
                    unifiedLogger.logOperationError("DATABASE", "DELETE_BY_MONTH", executionTime, e, "TABLE", tableName);
                    throw e;
                }
                return;
            }
            deleteSql = "DELETE FROM " + tableName + " " +
                    "WHERE dt_ano_exercicio_ctb = ?";
            try {
                int deletedRecords = jdbcTemplate.update(deleteSql, anoDelete);
                long executionTime = System.currentTimeMillis() - startTime;
                unifiedLogger.logDatabaseOperation("DELETE", tableName, deletedRecords, executionTime);
            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                unifiedLogger.logOperationError("DATABASE", "DELETE_BY_MONTH", executionTime, e, "TABLE", tableName);
                throw e;
            }
            return;
        }
        else if ("consumer_sefaz.empenho".equals(tableName)) {
            return;
        }
        else if ("consumer_sefaz.pagamento".equals(tableName)
                || "consumer_sefaz.liquidacao".equals(tableName)
                || "consumer_sefaz.restos_a_pagar".equals(tableName)
                || "consumer_sefaz.contrato_empenho".equals(tableName)
                || "consumer_sefaz.ordem_fornecimento".equals(tableName)
                || "consumer_sefaz.receita".equals(tableName)) {
            return;
        }
        else {
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
    private Integer obterNuMesDoEndpoint(T endpointInstance) {
        String tableName = endpointInstance.getTabelaBanco();
        try {
            if (tableName.contains("despesa_detalhada") || tableName.contains("previsao_realizacao_receita")) {
                Object result = endpointInstance.getClass().getMethod("getNuMes").invoke(endpointInstance);
                return result instanceof Integer ? (Integer) result : null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Integer obterDtAnoExercicioCtbDoEndpoint(T endpointInstance) {
        String tableName = endpointInstance.getTabelaBanco();
        if (!tableName.contains("despesa_detalhada") && !tableName.contains("previsao_realizacao_receita")) {
            return null;
        }
        try {
            Object result = endpointInstance.getClass().getMethod("getDtAnoExercicioCTB").invoke(endpointInstance);
            if (result instanceof Integer) {
                return (Integer) result;
            }
            result = endpointInstance.getClass().getMethod("getDtAnoExercicioCTBFiltro").invoke(endpointInstance);
            return result instanceof Integer ? (Integer) result : null;
        } catch (Exception ignored) {
        }
        return null;
    }
    public void persist(List<T> contratos) {
        if (contratos != null && !contratos.isEmpty()) {
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
        }
    }
}