package br.gov.se.setc.consumer.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TypeConverter.
 * Valida as conversões de tipos entre DTOs e entidades.
 */
@DisplayName("TypeConverter - Testes de Conversão de Tipos")
class TypeConverterTest {
    
    private TypeConverter typeConverter;
    
    @BeforeEach
    void setUp() {
        typeConverter = new TypeConverter();
    }
    
    @Test
    @DisplayName("Deve converter String para BigDecimal corretamente")
    void deveConverterStringParaBigDecimal() {
        // Arrange & Act & Assert
        assertEquals(new BigDecimal("123.45"), typeConverter.stringToBigDecimal("123.45"));
        assertEquals(new BigDecimal("1000"), typeConverter.stringToBigDecimal("1000"));
        assertEquals(new BigDecimal("0.99"), typeConverter.stringToBigDecimal("0.99"));
        assertEquals(new BigDecimal("123.45"), typeConverter.stringToBigDecimal("123,45")); // Vírgula → ponto
    }
    
    @Test
    @DisplayName("Deve retornar BigDecimal.ZERO para valores inválidos")
    void deveRetornarZeroParaValoresInvalidos() {
        // Arrange & Act & Assert
        assertEquals(BigDecimal.ZERO, typeConverter.stringToBigDecimal(null));
        assertEquals(BigDecimal.ZERO, typeConverter.stringToBigDecimal(""));
        assertEquals(BigDecimal.ZERO, typeConverter.stringToBigDecimal("   "));
        assertEquals(BigDecimal.ZERO, typeConverter.stringToBigDecimal("abc"));
        assertEquals(BigDecimal.ZERO, typeConverter.stringToBigDecimal("12.34.56"));
    }
    
    @Test
    @DisplayName("Deve converter String para BigDecimal nullable")
    void deveConverterStringParaBigDecimalNullable() {
        // Arrange & Act & Assert
        assertEquals(new BigDecimal("123.45"), typeConverter.stringToBigDecimalNullable("123.45"));
        assertNull(typeConverter.stringToBigDecimalNullable(null));
        assertNull(typeConverter.stringToBigDecimalNullable(""));
        assertNull(typeConverter.stringToBigDecimalNullable("abc"));
    }
    
    @Test
    @DisplayName("Deve converter String para LocalDate corretamente")
    void deveConverterStringParaLocalDate() {
        // Arrange & Act & Assert
        assertEquals(LocalDate.of(2025, 8, 14), typeConverter.stringToLocalDate("2025-08-14"));
        assertEquals(LocalDate.of(2023, 12, 31), typeConverter.stringToLocalDate("2023-12-31"));
        assertEquals(LocalDate.of(2024, 1, 1), typeConverter.stringToLocalDate("2024-01-01"));
    }
    
    @Test
    @DisplayName("Deve retornar null para datas inválidas")
    void deveRetornarNullParaDatasInvalidas() {
        // Arrange & Act & Assert
        assertNull(typeConverter.stringToLocalDate(null));
        assertNull(typeConverter.stringToLocalDate(""));
        assertNull(typeConverter.stringToLocalDate("   "));
        assertNull(typeConverter.stringToLocalDate("2025-13-01")); // Mês inválido
        assertNull(typeConverter.stringToLocalDate("2025/08/14")); // Formato inválido
        assertNull(typeConverter.stringToLocalDate("abc"));
    }
    
    @Test
    @DisplayName("Deve converter String para LocalDateTime corretamente")
    void deveConverterStringParaLocalDateTime() {
        // Arrange & Act & Assert
        assertEquals(
            LocalDateTime.of(2025, 8, 14, 10, 30, 45), 
            typeConverter.stringToLocalDateTime("2025-08-14T10:30:45")
        );
        assertEquals(
            LocalDateTime.of(2025, 8, 14, 10, 30, 45), 
            typeConverter.stringToLocalDateTime("2025-08-14 10:30:45")
        );
    }
    
    @Test
    @DisplayName("Deve retornar null para timestamps inválidos")
    void deveRetornarNullParaTimestampsInvalidos() {
        // Arrange & Act & Assert
        assertNull(typeConverter.stringToLocalDateTime(null));
        assertNull(typeConverter.stringToLocalDateTime(""));
        assertNull(typeConverter.stringToLocalDateTime("abc"));
        assertNull(typeConverter.stringToLocalDateTime("2025-08-14")); // Sem hora
    }
    
    @Test
    @DisplayName("Deve converter String para Integer corretamente")
    void deveConverterStringParaInteger() {
        // Arrange & Act & Assert
        assertEquals(Integer.valueOf(123), typeConverter.stringToInteger("123"));
        assertEquals(Integer.valueOf(-456), typeConverter.stringToInteger("-456"));
        assertEquals(Integer.valueOf(0), typeConverter.stringToInteger("0"));
    }
    
    @Test
    @DisplayName("Deve retornar null para integers inválidos")
    void deveRetornarNullParaIntegersInvalidos() {
        // Arrange & Act & Assert
        assertNull(typeConverter.stringToInteger(null));
        assertNull(typeConverter.stringToInteger(""));
        assertNull(typeConverter.stringToInteger("abc"));
        assertNull(typeConverter.stringToInteger("123.45"));
        assertNull(typeConverter.stringToInteger("999999999999999999999")); // Overflow
    }
    
    @Test
    @DisplayName("Deve converter String para Long corretamente")
    void deveConverterStringParaLong() {
        // Arrange & Act & Assert
        assertEquals(Long.valueOf(123456789L), typeConverter.stringToLong("123456789"));
        assertEquals(Long.valueOf(-987654321L), typeConverter.stringToLong("-987654321"));
        assertEquals(Long.valueOf(0L), typeConverter.stringToLong("0"));
    }
    
    @Test
    @DisplayName("Deve retornar null para longs inválidos")
    void deveRetornarNullParaLongsInvalidos() {
        // Arrange & Act & Assert
        assertNull(typeConverter.stringToLong(null));
        assertNull(typeConverter.stringToLong(""));
        assertNull(typeConverter.stringToLong("abc"));
        assertNull(typeConverter.stringToLong("123.45"));
    }
    
    @Test
    @DisplayName("Deve converter Object para String corretamente")
    void deveConverterObjectParaString() {
        // Arrange & Act & Assert
        assertEquals("123", typeConverter.objectToString(123));
        assertEquals("123.45", typeConverter.objectToString(123.45));
        assertEquals("true", typeConverter.objectToString(true));
        assertEquals("test", typeConverter.objectToString("  test  "));
        assertNull(typeConverter.objectToString(null));
    }
    
    @Test
    @DisplayName("Deve converter Integer para BigDecimal corretamente")
    void deveConverterIntegerParaBigDecimal() {
        // Arrange & Act & Assert
        assertEquals(new BigDecimal("123"), typeConverter.integerToBigDecimal(123));
        assertEquals(new BigDecimal("-456"), typeConverter.integerToBigDecimal(-456));
        assertEquals(new BigDecimal("0"), typeConverter.integerToBigDecimal(0));
        assertNull(typeConverter.integerToBigDecimal(null));
    }
    
    @Test
    @DisplayName("Deve converter Long para BigDecimal corretamente")
    void deveConverterLongParaBigDecimal() {
        // Arrange & Act & Assert
        assertEquals(new BigDecimal("123456789"), typeConverter.longToBigDecimal(123456789L));
        assertEquals(new BigDecimal("-987654321"), typeConverter.longToBigDecimal(-987654321L));
        assertEquals(new BigDecimal("0"), typeConverter.longToBigDecimal(0L));
        assertNull(typeConverter.longToBigDecimal(null));
    }
}
