package br.gov.se.setc.consumer.mapper;
import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
/**
 * Mapper responsável por converter ConsultaGerencialDTO para ConsultaGerencial entity.
 *
 * Este mapper resolve as inconsistências de tipos identificadas:
 * - Valores monetários que vêm como String da API → BigDecimal na entidade
 * - Datas que vêm como String da API → LocalDate na entidade
 * - Campos que precisam de conversão de tipos
 */
@Component
public class ConsultaGerencialMapper {
    private final TypeConverter typeConverter;
    @Autowired
    public ConsultaGerencialMapper(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }
    /**
     * Converte ConsultaGerencialDTO para ConsultaGerencial entity.
     *
     * @param dto DTO recebido da API SEFAZ
     * @return Entidade JPA pronta para persistência
     */
    public ConsultaGerencial toEntity(ConsultaGerencialDTO dto) {
        if (dto == null) {
            return null;
        }
        ConsultaGerencial entity = new ConsultaGerencial();
        entity.setCdUnidadeGestora(dto.getCdUnidadeGestora());
        entity.setSgUnidadeGestora(dto.getSgUnidadeGestora());
        entity.setDtAnoExercicioCTB(dto.getDtAnoExercicioCTB());
        entity.setCdGestao(dto.getCdGestao());
        entity.setTxMotivoSolicitacao(dto.getTxMotivoSolicitacao());
        entity.setDtSaidaSolicitacaoDiaria(
            typeConverter.stringToLocalDate(dto.getDtSaidaSolicitacaoDiariaStr())
        );
        entity.setDtRetornoSolicitacaoDiaria(
            typeConverter.stringToLocalDate(dto.getDtRetornoSolicitacaoDiariaStr())
        );
        entity.setQtdDiariaSolicitacaoDiaria(dto.getQtdDiariaSolicitacaoDiaria());
        entity.setVlTotalSolicitacaoDiaria(
            typeConverter.stringToBigDecimal(dto.getVlTotalSolicitacaoDiariaStr())
        );
        entity.setVlDescontoSolicitacaoDiaria(
            typeConverter.stringToBigDecimal(dto.getVlDescontoSolicitacaoDiariaStr())
        );
        entity.setVlValorMoeda(
            typeConverter.stringToBigDecimal(dto.getVlValorMoedaStr())
        );
        entity.setVlTotalValorPagoAtualizado(
            typeConverter.stringToBigDecimal(dto.getVlTotalValorPagoAtualizadoStr())
        );
        entity.setSqSolicEmpenho(dto.getSqSolicEmpenho());
        entity.setSqEmpenho(dto.getSqEmpenho());
        entity.setSqSolicitacaoDiaria(dto.getSqSolicitacaoDiaria());
        entity.setSqOB(dto.getSqOB());
        entity.setSqPrevisaoDesembolso(dto.getSqPrevisaoDesembolso());
        entity.setTpDocumento(dto.getTpDocumento());
        entity.setNuDocumento(dto.getNuDocumento());
        entity.setNmRazaoSocialPessoa(dto.getNmRazaoSocialPessoa());
        entity.setDsQualificacaoVinculo(dto.getDsQualificacaoVinculo());
        entity.setDestinoViagemPaisSolicitacaoDiaria(dto.getDestinoViagemPaisSolicitacaoDiaria());
        entity.setDestinoViagemUFSolicitacaoDiaria(dto.getDestinoViagemUFSolicitacaoDiaria());
        entity.setDestinoViagemMunicipioSolicitacaoDiaria(dto.getDestinoViagemMunicipioSolicitacaoDiaria());
        entity.setTpTransporteViagemSolicitacaoDiaria(dto.getTpTransporteViagemSolicitacaoDiaria());
        entity.setTpViagemSolicitacaoDiaria(dto.getTpViagemSolicitacaoDiaria());
        entity.setNmCargo(dto.getNmCargo());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}