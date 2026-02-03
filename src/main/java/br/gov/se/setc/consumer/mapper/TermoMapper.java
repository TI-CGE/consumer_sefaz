package br.gov.se.setc.consumer.mapper;

import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.entity.Termo;
import org.springframework.stereotype.Component;

@Component
public class TermoMapper {

    public Termo toEntity(TermoDTO dto) {
        if (dto == null) {
            return null;
        }
        Termo entity = new Termo();
        entity.setCdConvenio(dto.getCdConvenio());
        entity.setCdUnidadeGestora(dto.getCdUnidadeGestora());
        entity.setCdGestao(dto.getCdGestao());
        entity.setSgUnidadeGestora(dto.getSgUnidadeGestora());
        entity.setNmConvenio(dto.getNmConvenio());
        entity.setDsObjetoConvenio(dto.getDsObjetoConvenio());
        entity.setDtCelebracaoConvenio(dto.getDtCelebracaoConvenio());
        entity.setDtInicioVigenciaConv(dto.getDtInicioVigenciaConvenio());
        entity.setDtFimVigenciaConv(dto.getDtFimVigenciaConvenio());
        entity.setDtPublicacaoConvenio(dto.getDtPublicacaoConvenio());
        entity.setNuDocOficialConvenio(dto.getNuDocOficialConvenio());
        entity.setTxIdentOriginalConv(dto.getTxIdentOriginalConvenio());
        entity.setTxObservacaoConvenio(dto.getTxObservacaoConvenio());
        entity.setCdEfetivacaoUsuario(dto.getCdEfetivacaoUsuario());
        entity.setCdConvenioSituacao(dto.getCdConvenioSituacao());
        entity.setCdAreaAtuacao(dto.getCdAreaAtuacao());
        entity.setInLocalPublicacaoConv(dto.getInLocalPublicacaoConvenio());
        
        return entity;
    }

    public TermoDTO toDto(Termo entity) {
        if (entity == null) {
            return null;
        }
        TermoDTO dto = new TermoDTO();
        dto.setCdConvenio(entity.getCdConvenio());
        dto.setCdUnidadeGestora(entity.getCdUnidadeGestora());
        dto.setCdGestao(entity.getCdGestao());
        dto.setSgUnidadeGestora(entity.getSgUnidadeGestora());
        dto.setNmConvenio(entity.getNmConvenio());
        dto.setDsObjetoConvenio(entity.getDsObjetoConvenio());
        dto.setDtCelebracaoConvenio(entity.getDtCelebracaoConvenio());
        dto.setDtInicioVigenciaConvenio(entity.getDtInicioVigenciaConv());
        dto.setDtFimVigenciaConvenio(entity.getDtFimVigenciaConv());
        dto.setDtPublicacaoConvenio(entity.getDtPublicacaoConvenio());
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
