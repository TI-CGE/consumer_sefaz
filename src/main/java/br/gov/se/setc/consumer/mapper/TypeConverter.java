package br.gov.se.setc.consumer.mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/**
 * Conversor de tipos padronizado para eliminar inconsistências entre DTOs e entidades.
 *
 * Este componente centraliza todas as conversões de tipos que acontecem entre
 * os dados recebidos das APIs SEFAZ e os tipos esperados pelas entidades JPA.
 *
 * Principais conversões:
 * - String → BigDecimal (valores monetários)
 * - String → LocalDate (datas)
 * - String → LocalDateTime (timestamps)
 * - String → Integer/Long (números)
 */
@Component
public class TypeConverter {
    private static final Logger logger = LoggerFactory.getLogger(TypeConverter.class);
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ALTERNATIVE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Converte String para BigDecimal de forma segura.
     * Trata valores nulos, vazios e formatos inválidos.
     *
     * @param value String a ser convertida
     * @return BigDecimal convertido ou BigDecimal.ZERO se inválido
     */
    public BigDecimal stringToBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String cleanValue = value.trim().replace(",", ".");
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            logger.warn("Erro ao converter '{}' para BigDecimal. Retornando ZERO. Erro: {}", value, e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    /**
     * Converte String para BigDecimal, retornando null se inválido.
     * Útil quando null é um valor válido.
     *
     * @param value String a ser convertida
     * @return BigDecimal convertido ou null se inválido
     */
    public BigDecimal stringToBigDecimalNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            String cleanValue = value.trim().replace(",", ".");
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            logger.warn("Erro ao converter '{}' para BigDecimal. Retornando null. Erro: {}", value, e.getMessage());
            return null;
        }
    }
    /**
     * Converte String para LocalDate de forma segura.
     * Suporta formato ISO (yyyy-MM-dd).
     *
     * @param value String a ser convertida
     * @return LocalDate convertido ou null se inválido
     */
    public LocalDate stringToLocalDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            String cleanValue = value.trim();
            return LocalDate.parse(cleanValue, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.warn("Erro ao converter '{}' para LocalDate. Retornando null. Erro: {}", value, e.getMessage());
            return null;
        }
    }
    /**
     * Converte String para LocalDateTime de forma segura.
     * Suporta múltiplos formatos comuns.
     *
     * @param value String a ser convertida
     * @return LocalDateTime convertido ou null se inválido
     */
    public LocalDateTime stringToLocalDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String cleanValue = value.trim();
        try {
            return LocalDateTime.parse(cleanValue, ISO_DATETIME_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(cleanValue, ALTERNATIVE_DATETIME_FORMATTER);
            } catch (DateTimeParseException e2) {
                logger.warn("Erro ao converter '{}' para LocalDateTime. Retornando null. Erros: {} | {}",
                           value, e1.getMessage(), e2.getMessage());
                return null;
            }
        }
    }
    /**
     * Converte String para Integer de forma segura.
     *
     * @param value String a ser convertida
     * @return Integer convertido ou null se inválido
     */
    public Integer stringToInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Erro ao converter '{}' para Integer. Retornando null. Erro: {}", value, e.getMessage());
            return null;
        }
    }
    /**
     * Converte String para Long de forma segura.
     *
     * @param value String a ser convertida
     * @return Long convertido ou null se inválido
     */
    public Long stringToLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Erro ao converter '{}' para Long. Retornando null. Erro: {}", value, e.getMessage());
            return null;
        }
    }
    /**
     * Converte Object para String de forma segura.
     * Útil para campos que podem vir como diferentes tipos da API.
     *
     * @param value Object a ser convertido
     * @return String convertida ou null se nulo
     */
    public String objectToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString().trim();
    }
    /**
     * Converte Integer para BigDecimal de forma segura.
     * Útil quando a API retorna números inteiros mas o banco espera decimal.
     *
     * @param value Integer a ser convertido
     * @return BigDecimal convertido ou null se nulo
     */
    public BigDecimal integerToBigDecimal(Integer value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }
    /**
     * Converte Long para BigDecimal de forma segura.
     *
     * @param value Long a ser convertido
     * @return BigDecimal convertido ou null se nulo
     */
    public BigDecimal longToBigDecimal(Long value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }
}