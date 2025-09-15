package br.gov.se.setc.consumer.mapper;

import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.entity.Termo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre TermoDTO e Termo entity.
 * 
 * Responsável por converter dados recebidos da API SEFAZ (DTO)
 * para entidades JPA prontas para persistência no banco de dados.
 */
@Component
public class TermoMapper {
    
    /**
     * Converte TermoDTO para Termo entity.
     * 
     * @param dto DTO recebido da API SEFAZ
     * @return Entidade JPA pronta para persistência
     */
    public Termo toEntity(TermoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Termo entity = new Termo();
        
        // Campos principais
        entity.setCdConvenio(dto.getCdConvenio());
        entity.setCdUnidadeGestora(dto.getCdUnidadeGestora());
        entity.setCdGestao(dto.getCdGestao());
        entity.setSgUnidadeGestora(dto.getSgUnidadeGestora());
        entity.setNmConvenio(dto.getNmConvenio());
        entity.setDsObjetoConvenio(dto.getDsObjetoConvenio());
        
        // Conversão de datas - TermoDTO já usa LocalDate diretamente
        entity.setDtCelebracaoConvenio(dto.getDtCelebracaoConvenio());
        entity.setDtInicioVigenciaConv(dto.getDtInicioVigenciaConvenio());
        entity.setDtFimVigenciaConv(dto.getDtFimVigenciaConvenio());
        entity.setDtPublicacaoConvenio(dto.getDtPublicacaoConvenio());
        
        // Campos adicionais
        entity.setNuDocOficialConvenio(dto.getNuDocOficialConvenio());
        entity.setTxIdentOriginalConv(dto.getTxIdentOriginalConvenio());
        entity.setTxObservacaoConvenio(dto.getTxObservacaoConvenio());
        entity.setCdEfetivacaoUsuario(dto.getCdEfetivacaoUsuario());
        entity.setCdConvenioSituacao(dto.getCdConvenioSituacao());
        entity.setCdAreaAtuacao(dto.getCdAreaAtuacao());
        entity.setInLocalPublicacaoConv(dto.getInLocalPublicacaoConvenio());
        
        return entity;
    }
    
    /**
     * Converte Termo entity para TermoDTO.
     * 
     * @param entity Entidade JPA
     * @return DTO para resposta da API
     */
    public TermoDTO toDto(Termo entity) {
        if (entity == null) {
            return null;
        }
        
        TermoDTO dto = new TermoDTO();
        
        // Campos principais
        dto.setCdConvenio(entity.getCdConvenio());
        dto.setCdUnidadeGestora(entity.getCdUnidadeGestora());
        dto.setCdGestao(entity.getCdGestao());
        dto.setSgUnidadeGestora(entity.getSgUnidadeGestora());
        dto.setNmConvenio(entity.getNmConvenio());
        dto.setDsObjetoConvenio(entity.getDsObjetoConvenio());
        
        // Conversão de datas - TermoDTO usa LocalDate diretamente
        dto.setDtCelebracaoConvenio(entity.getDtCelebracaoConvenio());
        dto.setDtInicioVigenciaConvenio(entity.getDtInicioVigenciaConv());
        dto.setDtFimVigenciaConvenio(entity.getDtFimVigenciaConv());
        dto.setDtPublicacaoConvenio(entity.getDtPublicacaoConvenio());
        
        // Campos adicionais
        dto.setNuDocOficialConvenio(entity.getNuDocOficialConvenio());
        dto.setTxIdentOriginalConvenio(entity.getTxIdentOriginalConv());
        dto.setTxObservacaoConvenio(entity.getTxObservacaoConvenio());
        dto.setCdEfetivacaoUsuario(entity.getCdEfetivacaoUsuario());
        dto.setCdConvenioSituacao(entity.getCdConvenioSituacao());
        dto.setCdAreaAtuacao(entity.getCdAreaAtuacao());
        dto.setInLocalPublicacaoConvenio(entity.getInLocalPublicacaoConv());
        
        return dto;
    }
}
