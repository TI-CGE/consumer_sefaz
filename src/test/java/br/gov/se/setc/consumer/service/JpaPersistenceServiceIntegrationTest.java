package br.gov.se.setc.consumer.service;

import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import br.gov.se.setc.consumer.entity.Contrato;
import br.gov.se.setc.consumer.repository.ConsultaGerencialRepository;
import br.gov.se.setc.consumer.repository.ContratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para JpaPersistenceService.
 * Valida a persistência completa com tipos consistentes.
 */
// @SpringBootTest
// @ActiveProfiles("test")
// @Transactional
@DisplayName("JpaPersistenceService - Testes de Integração")
class JpaPersistenceServiceIntegrationTest {
    
    @Autowired
    private JpaPersistenceService jpaPersistenceService;
    
    @Autowired
    private ConsultaGerencialRepository consultaGerencialRepository;
    
    @Autowired
    private ContratoRepository contratoRepository;
    
    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        consultaGerencialRepository.deleteAll();
        contratoRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve persistir ConsultaGerencial com tipos corretos")
    void devePersistirConsultaGerencialComTiposCorretos() {
        // Arrange
        ConsultaGerencialDTO dto1 = criarConsultaGerencialDTO("123456", 2025, "150.00", "10.00");
        ConsultaGerencialDTO dto2 = criarConsultaGerencialDTO("654321", 2025, "200.50", "15.25");
        
        List<ConsultaGerencialDTO> dtos = Arrays.asList(dto1, dto2);
        
        // Act
        jpaPersistenceService.persist(dtos);
        
        // Assert
        List<ConsultaGerencial> entitiesSalvas = consultaGerencialRepository.findAll();
        assertEquals(2, entitiesSalvas.size());
        
        // Verificar primeiro registro
        ConsultaGerencial entity1 = entitiesSalvas.stream()
            .filter(e -> "123456".equals(e.getCdUnidadeGestora()))
            .findFirst()
            .orElseThrow();
        
        assertEquals("123456", entity1.getCdUnidadeGestora());
        assertEquals("SETC", entity1.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2025), entity1.getDtAnoExercicioCTB());
        assertEquals(LocalDate.of(2025, 8, 14), entity1.getDtSaidaSolicitacaoDiaria());
        assertEquals(new BigDecimal("150.00"), entity1.getVlTotalSolicitacaoDiaria());
        assertEquals(new BigDecimal("10.00"), entity1.getVlDescontoSolicitacaoDiaria());
        assertNotNull(entity1.getCreatedAt());
        assertNotNull(entity1.getUpdatedAt());
        
        // Verificar segundo registro
        ConsultaGerencial entity2 = entitiesSalvas.stream()
            .filter(e -> "654321".equals(e.getCdUnidadeGestora()))
            .findFirst()
            .orElseThrow();
        
        assertEquals("654321", entity2.getCdUnidadeGestora());
        assertEquals(new BigDecimal("200.50"), entity2.getVlTotalSolicitacaoDiaria());
        assertEquals(new BigDecimal("15.25"), entity2.getVlDescontoSolicitacaoDiaria());
    }
    
    @Test
    @DisplayName("Deve persistir Contrato com tipos corretos")
    void devePersistirContratoComTiposCorretos() {
        // Arrange
        ContratoDTO dto1 = criarContratoDTO("123456", 2025, "2025-01-01", "2025-12-31");
        ContratoDTO dto2 = criarContratoDTO("654321", 2025, "2025-06-01", "2026-05-31");
        
        List<ContratoDTO> dtos = Arrays.asList(dto1, dto2);
        
        // Act
        jpaPersistenceService.persist(dtos);
        
        // Assert
        List<Contrato> entitiesSalvas = contratoRepository.findAll();
        assertEquals(2, entitiesSalvas.size());
        
        // Verificar primeiro registro
        Contrato entity1 = entitiesSalvas.stream()
            .filter(e -> "123456".equals(e.getCdUnidadeGestora()))
            .findFirst()
            .orElseThrow();
        
        assertEquals("123456", entity1.getCdUnidadeGestora());
        assertEquals("SETC", entity1.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2025), entity1.getDtAnoExercicio());
        assertEquals(LocalDate.of(2025, 1, 1), entity1.getDtInicioVigencia());
        assertEquals(LocalDate.of(2025, 12, 31), entity1.getDtFimVigencia());
        assertEquals(new BigDecimal("50000.00"), entity1.getVlContrato());
        assertNotNull(entity1.getCreatedAt());
        assertNotNull(entity1.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve deletar registros existentes antes de inserir novos")
    void deveDeletarRegistrosExistentesAntesDeInserirNovos() {
        // Arrange - Inserir dados iniciais
        ConsultaGerencialDTO dtoInicial = criarConsultaGerencialDTO("111111", 2025, "100.00", "5.00");
        jpaPersistenceService.persist(Arrays.asList(dtoInicial));
        
        assertEquals(1, consultaGerencialRepository.count());
        
        // Act - Inserir novos dados (deve deletar os anteriores)
        ConsultaGerencialDTO dtoNovo = criarConsultaGerencialDTO("222222", 2025, "200.00", "10.00");
        jpaPersistenceService.persist(Arrays.asList(dtoNovo));
        
        // Assert
        List<ConsultaGerencial> entidades = consultaGerencialRepository.findAll();
        assertEquals(1, entidades.size());
        assertEquals("222222", entidades.get(0).getCdUnidadeGestora());
    }
    
    @Test
    @DisplayName("Deve verificar suporte para persistência JPA")
    void deveVerificarSuporteParaPersistenciaJpa() {
        // Assert
        assertTrue(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.consulta_gerencial"));
        assertTrue(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.contrato"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.contrato_empenho"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.empenho"));
        assertFalse(jpaPersistenceService.isJpaPersistenceSupported("consumer_sefaz.pagamento"));
    }
    
    @Test
    @DisplayName("Deve lançar exceção para tabelas não suportadas")
    void deveLancarExcecaoParaTabelasNaoSuportadas() {
        // Arrange
        ConsultaGerencialDTO dto = criarConsultaGerencialDTO("123456", 2025, "100.00", "5.00");
        // Simular tabela não suportada - vamos usar reflection para alterar o campo
        try {
            java.lang.reflect.Field field = dto.getClass().getSuperclass().getDeclaredField("tabelaBanco");
            field.setAccessible(true);
            field.set(dto, "consumer_sefaz.tabela_nao_suportada");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar tabela para teste", e);
        }
        
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            jpaPersistenceService.persist(Arrays.asList(dto));
        });
    }
    
    private ConsultaGerencialDTO criarConsultaGerencialDTO(String cdUnidadeGestora, Integer ano, 
                                                          String vlTotal, String vlDesconto) {
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora(cdUnidadeGestora);
        dto.setSgUnidadeGestora("SETC");
        dto.setDtAnoExercicioCTB(ano);
        dto.setCdGestao("00001");
        dto.setTxMotivoSolicitacao("Viagem a serviço");
        dto.setDtSaidaSolicitacaoDiariaStr("2025-08-14");
        dto.setDtRetornoSolicitacaoDiariaStr("2025-08-15");
        dto.setQtdDiariaSolicitacaoDiaria(2);
        dto.setVlTotalSolicitacaoDiariaStr(vlTotal);
        dto.setVlDescontoSolicitacaoDiariaStr(vlDesconto);
        dto.setVlValorMoedaStr("140.00");
        dto.setVlTotalValorPagoAtualizadoStr("140.00");
        dto.setSqSolicEmpenho(12345L);
        dto.setSqEmpenho(67890L);
        dto.setTpDocumento(1);
        dto.setNuDocumento("12345678901");
        dto.setNmRazaoSocialPessoa("João Silva");
        return dto;
    }
    
    private ContratoDTO criarContratoDTO(String cdUnidadeGestora, Integer ano, 
                                        String dtInicio, String dtFim) {
        ContratoDTO dto = new ContratoDTO();
        dto.setCdUnidadeGestora(cdUnidadeGestora);
        dto.setSgUnidadeGestora("SETC");
        dto.setDtAnoExercicio(ano);
        dto.setCdContrato("CONT001");
        dto.setCdAditivo("ADT001");
        dto.setNmCategoria("Serviços");
        dto.setNmFornecedor("Empresa XYZ Ltda");
        dto.setNuDocumento("12345678000199");
        dto.setDsObjetoContrato("Prestação de serviços de consultoria");
        dto.setVlContrato(new BigDecimal("50000.00"));
        dto.setTpContrato("Serviços");
        dto.setDtInicioVigenciaStr(dtInicio);
        dto.setDtFimVigenciaStr(dtFim);
        dto.setDtInicioVigencia(LocalDate.parse(dtInicio));
        dto.setDtFimVigencia(LocalDate.parse(dtFim));
        return dto;
    }
}
