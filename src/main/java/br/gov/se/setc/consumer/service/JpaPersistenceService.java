package br.gov.se.setc.consumer.service;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import br.gov.se.setc.consumer.entity.Contrato;
import br.gov.se.setc.consumer.mapper.ConsultaGerencialMapper;
import br.gov.se.setc.consumer.mapper.ContratoMapper;
import br.gov.se.setc.consumer.mapper.GenericEntityMapper;
import br.gov.se.setc.consumer.repository.ConsultaGerencialRepository;
import br.gov.se.setc.consumer.repository.ContratoRepository;
import br.gov.se.setc.logging.UnifiedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * Serviço de persistência JPA que substitui o EndpontSefazRepository.
 *
 * Este serviço:
 * - Usa entidades JPA em vez de SQL direto
 * - Aplica mappers para conversão de tipos
 * - Garante consistência de dados
 * - Mantém compatibilidade com o sistema existente
 */
@Service
@Transactional
public class JpaPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(JpaPersistenceService.class);
    private final ConsultaGerencialRepository consultaGerencialRepository;
    private final ContratoRepository contratoRepository;
    private final ConsultaGerencialMapper consultaGerencialMapper;
    private final ContratoMapper contratoMapper;
    private final GenericEntityMapper genericEntityMapper;
    private final UnifiedLogger unifiedLogger;
    @Autowired
    public JpaPersistenceService(
            ConsultaGerencialRepository consultaGerencialRepository,
            ContratoRepository contratoRepository,
            ConsultaGerencialMapper consultaGerencialMapper,
            ContratoMapper contratoMapper,
            GenericEntityMapper genericEntityMapper,
            UnifiedLogger unifiedLogger) {
        this.consultaGerencialRepository = consultaGerencialRepository;
        this.contratoRepository = contratoRepository;
        this.consultaGerencialMapper = consultaGerencialMapper;
        this.contratoMapper = contratoMapper;
        this.genericEntityMapper = genericEntityMapper;
        this.unifiedLogger = unifiedLogger;
    }
    /**
     * Persiste uma lista de DTOs usando JPA.
     * Método principal que substitui o EndpontSefazRepository.persist().
     *
     * @param dtos Lista de DTOs para persistir
     * @param <T> Tipo do DTO
     */
    public <T extends EndpontSefaz> void persist(List<T> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }
        T firstDto = dtos.get(0);
        String tableName = firstDto.getTabelaBanco();
        long startTime = System.currentTimeMillis();
        unifiedLogger.logOperationStart("JPA_PERSISTENCE", "PERSIST_BATCH", "TABLE", tableName, "COUNT", dtos.size());
        try {
            deleteExistingRecords(firstDto);
            if (tableName.contains("consulta_gerencial")) {
                persistConsultaGerencial((List<ConsultaGerencialDTO>) dtos);
            } else if (tableName.contains("contrato") && !tableName.contains("empenho")) {
                persistContrato((List<ContratoDTO>) dtos);
            } else {
                logger.warn("Persistência JPA não implementada ainda para tabela: {}", tableName);
                throw new UnsupportedOperationException("Persistência JPA não implementada para: " + tableName);
            }
            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logDatabaseOperation("JPA_PERSIST_SUCCESS", tableName, dtos.size(), executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            unifiedLogger.logOperationError("JPA_PERSISTENCE", "PERSIST_BATCH", executionTime, e, "TABLE", tableName);
            throw new RuntimeException("Erro na persistência JPA para " + tableName, e);
        }
    }
    /**
     * Persiste lista de ConsultaGerencialDTO.
     */
    private void persistConsultaGerencial(List<ConsultaGerencialDTO> dtos) {
        logger.info("Persistindo {} registros de ConsultaGerencial via JPA", dtos.size());
        List<ConsultaGerencial> entities = dtos.stream()
            .map(consultaGerencialMapper::toEntity)
            .toList();
        consultaGerencialRepository.saveAll(entities);
        logger.info("Persistência de ConsultaGerencial concluída com sucesso");
    }
    /**
     * Persiste lista de ContratoDTO.
     */
    private void persistContrato(List<ContratoDTO> dtos) {
        logger.info("Persistindo {} registros de Contrato via JPA", dtos.size());
        List<Contrato> entities = dtos.stream()
            .map(contratoMapper::toEntity)
            .toList();
        contratoRepository.saveAll(entities);
        logger.info("Persistência de Contrato concluída com sucesso");
    }
    /**
     * Deleta registros existentes antes de inserir novos.
     * Mantém a lógica do deleteByMesVigente do sistema anterior.
     */
    private void deleteExistingRecords(EndpontSefaz dto) {
        String tableName = dto.getTabelaBanco();
        try {
            if (tableName.contains("consulta_gerencial")) {
                consultaGerencialRepository.deleteByCurrentYear();
                logger.info("Registros existentes de ConsultaGerencial removidos");
            } else if (tableName.contains("contrato") && !tableName.contains("empenho")) {
                contratoRepository.deleteByCurrentYear();
                logger.info("Registros existentes de Contrato removidos");
            }
        } catch (Exception e) {
            logger.warn("Erro ao deletar registros existentes de {}: {}", tableName, e.getMessage());
        }
    }
    /**
     * Verifica se a persistência JPA está implementada para uma tabela.
     */
    public boolean isJpaPersistenceSupported(String tableName) {
        return tableName.contains("consulta_gerencial") ||
               (tableName.contains("contrato") && !tableName.contains("empenho"));
    }
}