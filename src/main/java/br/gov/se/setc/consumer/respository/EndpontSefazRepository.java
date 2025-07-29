package br.gov.se.setc.consumer.respository;

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
            int percorrido = 0;
            int total = contratos.size();

            for (T contrato : contratos) {
                Map<String, Object> fieldMap = contrato.getCamposResposta();

                String columns = String.join(", ", fieldMap.keySet());
                String placeholders = fieldMap.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
                String insertSql = "INSERT INTO "+contrato.getTabelaBanco()+ " (" + columns + ") VALUES (" + placeholders + ")";

                jdbcTemplate.update(insertSql, fieldMap.values().toArray());
                percorrido++;

                // Log de progresso a cada 100 registros (apenas para arquivo técnico)
                if (percorrido % 100 == 0 || percorrido == total) {
                    unifiedLogger.logDatabaseOperation("INSERT_PROGRESS", contrato.getTabelaBanco(), percorrido, 0);
                }
            }

            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationSuccess("DATABASE", "INSERT_BATCH", executionTime, total, "TABLE", tableName);
            unifiedLogger.logDatabaseOperation("INSERT", tableName, total, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationError("DATABASE", "INSERT_BATCH", executionTime, e, "TABLE", tableName);
            throw e;
        }
    }

    @LogOperation(operation = "DELETE_BY_MONTH", component = "DATABASE")
    public void deleteByMesVigente(T endpointInstance) {
        if(endpointInstance.getNomeDataInicialPadraoFiltro() == null || endpointInstance.getNomeDataFinalPadraoFiltro() == null){
            return;
        }

        String tableName = endpointInstance.getTabelaBanco();
        long startTime = System.currentTimeMillis();

        String deleteSql = "DELETE FROM "+endpointInstance.getTabelaBanco()+ "  " +
                "WHERE EXTRACT(YEAR FROM "+endpointInstance.getNomeDataInicialPadraoFiltro()+" ) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "AND EXTRACT(MONTH FROM "+endpointInstance.getNomeDataFinalPadraoFiltro()+" ) = EXTRACT(MONTH FROM CURRENT_DATE)";

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

