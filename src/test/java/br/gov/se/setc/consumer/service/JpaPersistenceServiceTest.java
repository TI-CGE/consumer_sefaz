package br.gov.se.setc.consumer.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para JpaPersistenceService.
 * Valida a lógica de persistência sem dependência de banco de dados.
 */
@DisplayName("JpaPersistenceService - Testes Unitários")
class JpaPersistenceServiceTest {
    
    @Mock
    private ConsultaGerencialRepository consultaGerencialRepository;
    
    @Mock
    private ContratoRepository contratoRepository;
    
    @Mock
    private ConsultaGerencialMapper consultaGerencialMapper;
    
    @Mock
    private ContratoMapper contratoMapper;
    
    @Mock
    private GenericEntityMapper genericEntityMapper;
    
    @Mock
    private UnifiedLogger unifiedLogger;
    
    private JpaPersistenceService jpaPersistenceService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jpaPersistenceService = new JpaPersistenceService(
            consultaGerencialRepository,
            contratoRepository,
            consultaGerencialMapper,
            contratoMapper,
            genericEntityMapper,
            unifiedLogger
        );
    }
    
    @Test
    @DisplayName("Deve verificar suporte para persistência JPA corretamente")
    void deveVerificarSuporteParaPersistenciaJpa() {
        // Assert
        assertTrue(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.consulta_gerencial"));
        assertTrue(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.contrato"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.contrato_empenho"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.empenho"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.pagamento"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.tabela_inexistente"));
    }
    
    @Test
    @DisplayName("Deve persistir ConsultaGerencial usando mapper e repository")
    void devePersistirConsultaGerencialUsandoMapperERepository() {
        // Arrange
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("123456");
        
        // Usar reflection para definir tabelaBanco
        try {
            java.lang.reflect.Field field = dto.getClass().getSuperclass().getDeclaredField("tabelaBanco");
            field.setAccessible(true);
            field.set(dto, "consumer_sefaz.consulta_gerencial");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar tabela para teste", e);
        }
        
        ConsultaGerencial entity = new ConsultaGerencial();
        entity.setCdUnidadeGestora("123456");
        
        List<ConsultaGerencialDTO> dtos = Arrays.asList(dto);
        
        when(consultaGerencialMapper.toEntity(dto)).thenReturn(entity);
        
        // Act
        jpaPersistenceService.persist(dtos);
        
        // Assert
        verify(consultaGerencialRepository).deleteByCurrentYear();
        verify(consultaGerencialMapper).toEntity(dto);
        verify(consultaGerencialRepository).saveAll(Arrays.asList(entity));
        verify(unifiedLogger, atLeastOnce()).logOperationStart(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
        verify(unifiedLogger, atLeastOnce()).logDatabaseOperation(anyString(), anyString(), anyInt(), anyLong());
    }
    
    @Test
    @DisplayName("Deve persistir Contrato usando mapper e repository")
    void devePersistirContratoUsandoMapperERepository() {
        // Arrange
        ContratoDTO dto = new ContratoDTO();
        dto.setCdUnidadeGestora("123456");
        
        // Usar reflection para definir tabelaBanco
        try {
            java.lang.reflect.Field field = dto.getClass().getSuperclass().getDeclaredField("tabelaBanco");
            field.setAccessible(true);
            field.set(dto, "consumer_sefaz.contrato");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar tabela para teste", e);
        }
        
        Contrato entity = new Contrato();
        entity.setCdUnidadeGestora("123456");
        
        List<ContratoDTO> dtos = Arrays.asList(dto);
        
        when(contratoMapper.toEntity(dto)).thenReturn(entity);
        
        // Act
        jpaPersistenceService.persist(dtos);
        
        // Assert
        verify(contratoRepository).deleteByCurrentYear();
        verify(contratoMapper).toEntity(dto);
        verify(contratoRepository).saveAll(Arrays.asList(entity));
        verify(unifiedLogger, atLeastOnce()).logOperationStart(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
        verify(unifiedLogger, atLeastOnce()).logDatabaseOperation(anyString(), anyString(), anyInt(), anyLong());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para tabelas não suportadas")
    void deveLancarExcecaoParaTabelasNaoSuportadas() {
        // Arrange
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("123456");
        
        // Usar reflection para definir tabelaBanco não suportada
        try {
            java.lang.reflect.Field field = dto.getClass().getSuperclass().getDeclaredField("tabelaBanco");
            field.setAccessible(true);
            field.set(dto, "consumer_sefaz.tabela_nao_suportada");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar tabela para teste", e);
        }
        
        List<ConsultaGerencialDTO> dtos = Arrays.asList(dto);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jpaPersistenceService.persist(dtos);
        });

        assertTrue(exception.getMessage().contains("Erro na persistência JPA para"));
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertTrue(exception.getCause().getMessage().contains("Persistência JPA não implementada para"));
        verify(unifiedLogger, atLeastOnce()).logOperationStart(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
        verify(unifiedLogger, atLeastOnce()).logOperationError(anyString(), anyString(), anyLong(), any(RuntimeException.class), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Deve lidar com lista vazia sem erro")
    void deveLidarComListaVaziaSemErro() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            jpaPersistenceService.persist(Arrays.asList());
        });
        
        // Verificar que nenhuma operação foi executada
        verifyNoInteractions(consultaGerencialRepository);
        verifyNoInteractions(contratoRepository);
        verifyNoInteractions(consultaGerencialMapper);
        verifyNoInteractions(contratoMapper);
    }
    
    @Test
    @DisplayName("Deve lidar com lista null sem erro")
    void deveLidarComListaNullSemErro() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            jpaPersistenceService.persist(null);
        });
        
        // Verificar que nenhuma operação foi executada
        verifyNoInteractions(consultaGerencialRepository);
        verifyNoInteractions(contratoRepository);
        verifyNoInteractions(consultaGerencialMapper);
        verifyNoInteractions(contratoMapper);
    }
}
