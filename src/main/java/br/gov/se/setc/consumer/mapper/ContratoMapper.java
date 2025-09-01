package br.gov.se.setc.consumer.mapper;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.entity.Contrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
/**
 * Mapper responsável por converter ContratoDTO para Contrato entity.
 *
 * Este mapper resolve as inconsistências de tipos identificadas:
 * - Datas que vêm como String da API → LocalDate na entidade
 * - Conversão manual de campos de data
 */
@Component
public class ContratoMapper {
    private final TypeConverter typeConverter;
    @Autowired
    public ContratoMapper(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }
    /**
     * Converte ContratoDTO para Contrato entity.
     *
     * @param dto DTO recebido da API SEFAZ
     * @return Entidade JPA pronta para persistência
     */
    public Contrato toEntity(ContratoDTO dto) {
        if (dto == null) {
            return null;
        }
        Contrato entity = new Contrato();
        entity.setSgUnidadeGestora(dto.getSgUnidadeGestora());
        entity.setCdUnidadeGestora(dto.getCdUnidadeGestora());
        entity.setDtAnoExercicio(dto.getDtAnoExercicio());
        entity.setCdContrato(dto.getCdContrato());
        entity.setCdAditivo(dto.getCdAditivo());
        entity.setNmCategoria(dto.getNmCategoria());
        entity.setNmFornecedor(dto.getNmFornecedor());
        entity.setNuDocumento(dto.getNuDocumento());
        entity.setDsObjetoContrato(dto.getDsObjetoContrato());
        entity.setVlContrato(dto.getVlContrato());
        entity.setTpContrato(dto.getTpContrato());
        entity.setDtInicioVigencia(dto.getDtInicioVigencia());
        entity.setDtFimVigencia(dto.getDtFimVigencia());
        if (entity.getDtInicioVigencia() == null && dto.getDtInicioVigenciaStr() != null) {
            entity.setDtInicioVigencia(typeConverter.stringToLocalDate(dto.getDtInicioVigenciaStr()));
        }
        if (entity.getDtFimVigencia() == null && dto.getDtFimVigenciaStr() != null) {
            entity.setDtFimVigencia(typeConverter.stringToLocalDate(dto.getDtFimVigenciaStr()));
        }
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
    /**
     * Converte Contrato entity para ContratoDTO.
     * Útil para operações de leitura e testes.
     *
     * @param entity Entidade JPA
     * @return DTO para resposta
     */
    public ContratoDTO toDTO(Contrato entity) {
        if (entity == null) {
            return null;
        }
        ContratoDTO dto = new ContratoDTO();
        dto.setSgUnidadeGestora(entity.getSgUnidadeGestora());
        dto.setCdUnidadeGestora(entity.getCdUnidadeGestora());
        dto.setDtAnoExercicio(entity.getDtAnoExercicio());
        dto.setCdContrato(entity.getCdContrato());
        dto.setCdAditivo(entity.getCdAditivo());
        dto.setNmCategoria(entity.getNmCategoria());
        dto.setNmFornecedor(entity.getNmFornecedor());
        dto.setNuDocumento(entity.getNuDocumento());
        dto.setDsObjetoContrato(entity.getDsObjetoContrato());
        dto.setVlContrato(entity.getVlContrato());
        dto.setTpContrato(entity.getTpContrato());
        dto.setDtInicioVigencia(entity.getDtInicioVigencia());
        dto.setDtFimVigencia(entity.getDtFimVigencia());
        if (entity.getDtInicioVigencia() != null) {
            dto.setDtInicioVigenciaStr(entity.getDtInicioVigencia().toString());
        }
        if (entity.getDtFimVigencia() != null) {
            dto.setDtFimVigenciaStr(entity.getDtFimVigencia().toString());
        }
        return dto;
    }
}