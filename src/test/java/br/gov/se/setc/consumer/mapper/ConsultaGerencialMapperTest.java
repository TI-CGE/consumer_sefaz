package br.gov.se.setc.consumer.mapper;

import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ConsultaGerencialMapper.
 * Valida a conversão entre DTO e Entity com tipos consistentes.
 */
@DisplayName("ConsultaGerencialMapper - Testes de Mapeamento")
class ConsultaGerencialMapperTest {
    
    @Mock
    private TypeConverter typeConverter;
    
    private ConsultaGerencialMapper mapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ConsultaGerencialMapper(typeConverter);
    }
    
    @Test
    @DisplayName("Deve mapear DTO para Entity corretamente")
    void deveMaperarDtoParaEntity() {
        // Arrange
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("123456");
        dto.setSgUnidadeGestora("SETC");
        dto.setDtAnoExercicioCTB(2025);
        dto.setCdGestao("00001");
        dto.setTxMotivoSolicitacao("Viagem a serviço");
        dto.setDtSaidaSolicitacaoDiariaStr("2025-08-14");
        dto.setDtRetornoSolicitacaoDiariaStr("2025-08-15");
        dto.setQtdDiariaSolicitacaoDiaria(2);
        dto.setVlTotalSolicitacaoDiariaStr("150.00");
        dto.setVlDescontoSolicitacaoDiariaStr("10.00");
        dto.setVlValorMoedaStr("140.00");
        dto.setVlTotalValorPagoAtualizadoStr("140.00");
        dto.setSqSolicEmpenho(12345L);
        dto.setSqEmpenho(67890L);
        dto.setTpDocumento(1);
        dto.setNuDocumento("12345678901");
        dto.setNmRazaoSocialPessoa("João Silva");
        
        // Mock das conversões
        when(typeConverter.stringToLocalDate("2025-08-14")).thenReturn(LocalDate.of(2025, 8, 14));
        when(typeConverter.stringToLocalDate("2025-08-15")).thenReturn(LocalDate.of(2025, 8, 15));
        when(typeConverter.stringToBigDecimal("150.00")).thenReturn(new BigDecimal("150.00"));
        when(typeConverter.stringToBigDecimal("10.00")).thenReturn(new BigDecimal("10.00"));
        when(typeConverter.stringToBigDecimal("140.00")).thenReturn(new BigDecimal("140.00"));
        
        // Act
        ConsultaGerencial entity = mapper.toEntity(dto);
        
        // Assert
        assertNotNull(entity);
        assertEquals("123456", entity.getCdUnidadeGestora());
        assertEquals("SETC", entity.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2025), entity.getDtAnoExercicioCTB());
        assertEquals("00001", entity.getCdGestao());
        assertEquals("Viagem a serviço", entity.getTxMotivoSolicitacao());
        assertEquals(LocalDate.of(2025, 8, 14), entity.getDtSaidaSolicitacaoDiaria());
        assertEquals(LocalDate.of(2025, 8, 15), entity.getDtRetornoSolicitacaoDiaria());
        assertEquals(Integer.valueOf(2), entity.getQtdDiariaSolicitacaoDiaria());
        assertEquals(new BigDecimal("150.00"), entity.getVlTotalSolicitacaoDiaria());
        assertEquals(new BigDecimal("10.00"), entity.getVlDescontoSolicitacaoDiaria());
        assertEquals(new BigDecimal("140.00"), entity.getVlValorMoeda());
        assertEquals(new BigDecimal("140.00"), entity.getVlTotalValorPagoAtualizado());
        assertEquals(Long.valueOf(12345L), entity.getSqSolicEmpenho());
        assertEquals(Long.valueOf(67890L), entity.getSqEmpenho());
        assertEquals(Integer.valueOf(1), entity.getTpDocumento());
        assertEquals("12345678901", entity.getNuDocumento());
        assertEquals("João Silva", entity.getNmRazaoSocialPessoa());
        
        // Verificar que os campos de auditoria foram definidos
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        
        // Verificar que as conversões foram chamadas
        verify(typeConverter).stringToLocalDate("2025-08-14");
        verify(typeConverter).stringToLocalDate("2025-08-15");
        verify(typeConverter).stringToBigDecimal("150.00");
        verify(typeConverter).stringToBigDecimal("10.00");
        verify(typeConverter, times(2)).stringToBigDecimal("140.00");
    }
    
    @Test
    @DisplayName("Deve retornar null quando DTO for null")
    void deveRetornarNullQuandoDtoForNull() {
        // Act
        ConsultaGerencial entity = mapper.toEntity(null);
        
        // Assert
        assertNull(entity);
    }
    
    @Test
    @DisplayName("Deve lidar com valores nulos no DTO")
    void deveLidarComValoresNulosNoDto() {
        // Arrange
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("123456");
        // Deixar outros campos como null
        
        // Mock das conversões para null
        when(typeConverter.stringToLocalDate(null)).thenReturn(null);
        when(typeConverter.stringToBigDecimal(null)).thenReturn(BigDecimal.ZERO);
        
        // Act
        ConsultaGerencial entity = mapper.toEntity(dto);
        
        // Assert
        assertNotNull(entity);
        assertEquals("123456", entity.getCdUnidadeGestora());
        assertNull(entity.getSgUnidadeGestora());
        assertNull(entity.getDtAnoExercicioCTB());
        assertNull(entity.getDtSaidaSolicitacaoDiaria());
        assertNull(entity.getDtRetornoSolicitacaoDiaria());
        assertEquals(BigDecimal.ZERO, entity.getVlTotalSolicitacaoDiaria());
        assertEquals(BigDecimal.ZERO, entity.getVlDescontoSolicitacaoDiaria());
        assertEquals(BigDecimal.ZERO, entity.getVlValorMoeda());
        assertEquals(BigDecimal.ZERO, entity.getVlTotalValorPagoAtualizado());
    }
}
