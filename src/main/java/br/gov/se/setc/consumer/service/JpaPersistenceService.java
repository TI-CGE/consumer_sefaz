package br.gov.se.setc.consumer.service;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import br.gov.se.setc.consumer.entity.Contrato;
import br.gov.se.setc.consumer.entity.Termo;
import br.gov.se.setc.consumer.mapper.ConsultaGerencialMapper;
import br.gov.se.setc.consumer.mapper.ContratoMapper;
import br.gov.se.setc.consumer.mapper.TermoMapper;
import br.gov.se.setc.consumer.mapper.GenericEntityMapper;
import br.gov.se.setc.consumer.repository.ConsultaGerencialRepository;
import br.gov.se.setc.consumer.repository.ContratoRepository;
import br.gov.se.setc.consumer.repository.TermoRepository;
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
    private final TermoRepository termoRepository;
    private final ConsultaGerencialMapper consultaGerencialMapper;
    private final ContratoMapper contratoMapper;
    private final TermoMapper termoMapper;
    private final GenericEntityMapper genericEntityMapper;
    private final UnifiedLogger unifiedLogger;
    @Autowired
    public JpaPersistenceService(
            ConsultaGerencialRepository consultaGerencialRepository,
            ContratoRepository contratoRepository,
            TermoRepository termoRepository,
            ConsultaGerencialMapper consultaGerencialMapper,
            ContratoMapper contratoMapper,
            TermoMapper termoMapper,
            GenericEntityMapper genericEntityMapper,
            UnifiedLogger unifiedLogger) {
        this.consultaGerencialRepository = consultaGerencialRepository;
        this.contratoRepository = contratoRepository;
        this.termoRepository = termoRepository;
        this.consultaGerencialMapper = consultaGerencialMapper;
        this.contratoMapper = contratoMapper;
        this.termoMapper = termoMapper;
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
            } else if (tableName.contains("contratos_fiscais")) {
                // Contratos Fiscais não tem persistência JPA implementada ainda
                logger.warn("Persistência JPA não implementada para contratos fiscais. Usando persistência JDBC.");
                throw new UnsupportedOperationException("Persistência JPA não implementada para: " + tableName);
            } else if (tableName.contains("contrato") && !tableName.contains("empenho")) {
                persistContrato((List<ContratoDTO>) dtos);
            } else if (tableName.contains("termo")) {
                persistTermo((List<TermoDTO>) dtos);
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
     * Persiste lista de TermoDTO com estratégia upsert para evitar duplicatas.
     */
    private void persistTermo(List<TermoDTO> dtos) {
        logger.info("Persistindo {} registros de Termo via JPA", dtos.size());

        for (TermoDTO dto : dtos) {
            try {
                Termo entity = termoMapper.toEntity(dto);

                // Verifica se já existe um termo com o mesmo cd_convenio
                if (entity.getCdConvenio() != null && termoRepository.existsByCdConvenio(entity.getCdConvenio())) {
                    // Busca o registro existente e atualiza
                    Termo existingTermo = termoRepository.findByCdConvenio(entity.getCdConvenio()).orElse(null);
                    if (existingTermo != null) {
                        // Atualiza os campos do registro existente
                        updateExistingTermo(existingTermo, entity);
                        termoRepository.save(existingTermo);
                        logger.debug("Termo atualizado: cd_convenio={}", entity.getCdConvenio());
                    }
                } else {
                    // Insere novo registro
                    termoRepository.save(entity);
                    logger.debug("Novo termo inserido: cd_convenio={}", entity.getCdConvenio());
                }
            } catch (Exception e) {
                logger.error("Erro ao persistir termo com cd_convenio={}: {}",
                    dto.getCdConvenio(), e.getMessage());
                // Continua com os próximos registros em caso de erro
            }
        }

        logger.info("Persistência de Termo concluída com sucesso");
    }

    /**
     * Atualiza campos de um termo existente com dados do novo termo.
     */
    private void updateExistingTermo(Termo existing, Termo newTermo) {
        existing.setCdUnidadeGestora(newTermo.getCdUnidadeGestora());
        existing.setCdGestao(newTermo.getCdGestao());
        existing.setSgUnidadeGestora(newTermo.getSgUnidadeGestora());
        existing.setNmConvenio(newTermo.getNmConvenio());
        existing.setDsObjetoConvenio(newTermo.getDsObjetoConvenio());
        existing.setDtCelebracaoConvenio(newTermo.getDtCelebracaoConvenio());
        existing.setDtInicioVigenciaConv(newTermo.getDtInicioVigenciaConv());
        existing.setDtFimVigenciaConv(newTermo.getDtFimVigenciaConv());
        existing.setDtPublicacaoConvenio(newTermo.getDtPublicacaoConvenio());
        existing.setNuDocOficialConvenio(newTermo.getNuDocOficialConvenio());
        existing.setTxIdentOriginalConv(newTermo.getTxIdentOriginalConv());
        existing.setTxObservacaoConvenio(newTermo.getTxObservacaoConvenio());
        existing.setCdEfetivacaoUsuario(newTermo.getCdEfetivacaoUsuario());
        existing.setCdConvenioSituacao(newTermo.getCdConvenioSituacao());
        existing.setCdAreaAtuacao(newTermo.getCdAreaAtuacao());
        existing.setInLocalPublicacaoConv(newTermo.getInLocalPublicacaoConv());
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
            } else if (tableName.contains("termo")) {
                termoRepository.deleteByCurrentYearAndMonth();
                logger.info("Registros existentes de Termo removidos");
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
               (tableName.contains("contrato") && !tableName.contains("empenho") && !tableName.contains("contratos_fiscais")) ||
               tableName.contains("termo");
    }
}