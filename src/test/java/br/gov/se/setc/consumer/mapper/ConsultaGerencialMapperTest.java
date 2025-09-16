package br.gov.se.setc.consumer.mapper;

import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.entity.ConsultaGerencial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Testes unitários para ConsultaGerencialMapper
 * 
 * Valida a conversão correta de DTO para Entity,
 * especialmente após as alterações nos setters numéricos.
 */
public class ConsultaGerencialMapperTest {

    private ConsultaGerencialMapper mapper;
    
    @Mock
    private TypeConverter typeConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ConsultaGerencialMapper(typeConverter);
    }

    @Test
    void testToEntityComDTONulo() {
        ConsultaGerencial result = mapper.toEntity(null);
        assertNull(result);
    }

    @Test
    void testToEntityComDTOCompleto() {
        // Preparar DTO com dados de exemplo da API
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("241101");
        dto.setSgUnidadeGestora("SUPDEC");
        dto.setDtAnoExercicioCTB(2025);
        dto.setCdGestao("00001");
        dto.setTxMotivoSolicitacao("FISCALIZACAO EM ARAUA/SE, NO DIA 10/06/2025, C.I. 1067/2025.");
        dto.setDtSaidaSolicitacaoDiariaStr("2025-06-10");
        dto.setDtRetornoSolicitacaoDiariaStr("2025-06-10");
        dto.setQtdDiariaSolicitacaoDiaria(1);
        dto.setVlTotalSolicitacaoDiaria(25); // Número como vem da API
        dto.setVlTotalValorPagoAtualizado(25); // Número como vem da API
        dto.setSqOB(454L);
        dto.setSqSolicEmpenho(320L);
        dto.setSqEmpenho(320L);
        dto.setSqSolicitacaoDiaria(140L);
        dto.setSqPrevisaoDesembolso(466L);
        dto.setTpDocumento(3);
        dto.setNuDocumento("96437634553");
        dto.setNmRazaoSocialPessoa("MOACIR SENA RIBEIRO");
        dto.setDsQualificacaoVinculo("DEMAIS CARGOS, FUNÇÕES OU EMPREGOS.");
        dto.setDestinoViagemPaisSolicitacaoDiaria("BRASIL");
        dto.setDestinoViagemUFSolicitacaoDiaria("SERGIPE");
        dto.setDestinoViagemMunicipioSolicitacaoDiaria("ARAUA");
        dto.setTpTransporteViagemSolicitacaoDiaria("C");
        dto.setTpViagemSolicitacaoDiaria("M");

        // Mock do TypeConverter - configurar todos os valores que serão usados
        when(typeConverter.stringToLocalDate("2025-06-10")).thenReturn(LocalDate.of(2025, 6, 10));
        when(typeConverter.stringToBigDecimal("25")).thenReturn(new BigDecimal("25"));
        when(typeConverter.stringToBigDecimal("100.5")).thenReturn(new BigDecimal("100.5"));
        when(typeConverter.stringToBigDecimal("25.75")).thenReturn(new BigDecimal("25.75"));
        when(typeConverter.stringToBigDecimal("0")).thenReturn(BigDecimal.ZERO);
        when(typeConverter.stringToBigDecimal(null)).thenReturn(null);
        when(typeConverter.stringToBigDecimal("")).thenReturn(BigDecimal.ZERO);
        when(typeConverter.stringToLocalDate(null)).thenReturn(null);

        // Executar conversão
        ConsultaGerencial entity = mapper.toEntity(dto);

        // Verificar resultados
        assertNotNull(entity);
        assertEquals("241101", entity.getCdUnidadeGestora());
        assertEquals("SUPDEC", entity.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2025), entity.getDtAnoExercicioCTB());
        assertEquals("00001", entity.getCdGestao());
        assertEquals("FISCALIZACAO EM ARAUA/SE, NO DIA 10/06/2025, C.I. 1067/2025.", entity.getTxMotivoSolicitacao());
        assertEquals(LocalDate.of(2025, 6, 10), entity.getDtSaidaSolicitacaoDiaria());
        assertEquals(LocalDate.of(2025, 6, 10), entity.getDtRetornoSolicitacaoDiaria());
        assertEquals(Integer.valueOf(1), entity.getQtdDiariaSolicitacaoDiaria());
        assertEquals(new BigDecimal("25"), entity.getVlTotalSolicitacaoDiaria());
        assertEquals(new BigDecimal("25"), entity.getVlTotalValorPagoAtualizado());
        assertEquals(Long.valueOf(454), entity.getSqOB());
        assertEquals(Long.valueOf(320), entity.getSqSolicEmpenho());
        assertEquals(Long.valueOf(320), entity.getSqEmpenho());
        assertEquals(Long.valueOf(140), entity.getSqSolicitacaoDiaria());
        assertEquals(Long.valueOf(466), entity.getSqPrevisaoDesembolso());
        assertEquals(Integer.valueOf(3), entity.getTpDocumento());
        assertEquals("96437634553", entity.getNuDocumento());
        assertEquals("MOACIR SENA RIBEIRO", entity.getNmRazaoSocialPessoa());
        assertEquals("DEMAIS CARGOS, FUNÇÕES OU EMPREGOS.", entity.getDsQualificacaoVinculo());
        assertEquals("BRASIL", entity.getDestinoViagemPaisSolicitacaoDiaria());
        assertEquals("SERGIPE", entity.getDestinoViagemUFSolicitacaoDiaria());
        assertEquals("ARAUA", entity.getDestinoViagemMunicipioSolicitacaoDiaria());
        assertEquals("C", entity.getTpTransporteViagemSolicitacaoDiaria());
        assertEquals("M", entity.getTpViagemSolicitacaoDiaria());
        
        // Verificar timestamps
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testToEntityComCamposNulos() {
        // Preparar DTO com campos nulos
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora(null);
        dto.setVlTotalValorPagoAtualizadoStr(null);
        dto.setDtSaidaSolicitacaoDiariaStr(null);

        // Mock do TypeConverter para valores nulos já está configurado acima

        // Executar conversão
        ConsultaGerencial entity = mapper.toEntity(dto);

        // Verificar que campos nulos são tratados corretamente
        assertNotNull(entity);
        assertNull(entity.getCdUnidadeGestora());
        assertNull(entity.getVlTotalValorPagoAtualizado());
        assertNull(entity.getDtSaidaSolicitacaoDiaria());
    }

    @Test
    void testToEntityComValoresZero() {
        // Preparar DTO com valores zero
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setVlTotalValorPagoAtualizado(0);
        dto.setQtdDiariaSolicitacaoDiaria(0);
        dto.setSqOB(0L);

        // Mock do TypeConverter para valores zero
        when(typeConverter.stringToBigDecimal("0")).thenReturn(BigDecimal.ZERO);

        // Executar conversão
        ConsultaGerencial entity = mapper.toEntity(dto);

        // Verificar que valores zero são preservados
        assertNotNull(entity);
        assertEquals(BigDecimal.ZERO, entity.getVlTotalValorPagoAtualizado());
        assertEquals(Integer.valueOf(0), entity.getQtdDiariaSolicitacaoDiaria());
        assertEquals(Long.valueOf(0), entity.getSqOB());
    }

    @Test
    void testToEntityBasico() {
        // Teste básico para verificar se o mapper funciona
        ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
        dto.setCdUnidadeGestora("123456");
        dto.setSgUnidadeGestora("TESTE");
        dto.setDtAnoExercicioCTB(2025);

        // Executar conversão
        ConsultaGerencial entity = mapper.toEntity(dto);

        // Verificar resultados básicos
        assertNotNull(entity);
        assertEquals("123456", entity.getCdUnidadeGestora());
        assertEquals("TESTE", entity.getSgUnidadeGestora());
        assertEquals(Integer.valueOf(2025), entity.getDtAnoExercicioCTB());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }
}
