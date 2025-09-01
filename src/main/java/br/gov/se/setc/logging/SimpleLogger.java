package br.gov.se.setc.logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
/**
 * Logger simplificado com formato limpo e legível.
 * Foca na clareza e simplicidade, removendo informações desnecessárias.
 */
@Component
public class SimpleLogger {
    private static final Logger logger = LoggerFactory.getLogger("SIMPLE");
    private static final Logger errorLogger = LoggerFactory.getLogger(SimpleLogger.class);
    private static final Logger markdownLogger = LoggerFactory.getLogger("MARKDOWN");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    /**
     * Log de informação simples
     */
    public void info(String component, String message) {
        setContext(component);
        logger.info("{} | {} | {}", getCurrentTime(), component, message);
    }
    /**
     * Log de sucesso
     */
    public void success(String component, String message) {
        setContext(component);
        logger.info("{} | {} | ✅ {}", getCurrentTime(), component, message);
    }
    /**
     * Log de sucesso com duração
     */
    public void success(String component, String message, long durationMs) {
        setContext(component);
        String duration = formatDuration(durationMs);
        logger.info("{} | {} | ✅ {} ({})", getCurrentTime(), component, message, duration);
    }
    /**
     * Log de aviso
     */
    public void warning(String component, String message) {
        setContext(component);
        logger.warn("{} | {} | ⚠️ {}", getCurrentTime(), component, message);
    }
    /**
     * Log de erro
     */
    public void error(String component, String message) {
        setContext(component);
        logger.error("{} | {} | ❌ {}", getCurrentTime(), component, message);
        errorLogger.error("ERRO em {} - {}", component, message);
        logErrorToMarkdown(component, message, null);
    }
    /**
     * Log de erro com exceção
     */
    public void error(String component, String message, Exception e) {
        setContext(component);
        logger.error("{} | {} | ❌ {}: {}", getCurrentTime(), component, message, e.getMessage());
        errorLogger.error("ERRO em {} - {}: {}", component, message, e.getMessage(), e);
        logErrorToMarkdown(component, message, e);
    }
    /**
     * Log de progresso
     */
    public void progress(String component, String operation, int current, int total) {
        setContext(component);
        int percentage = (int) ((current * 100.0) / total);
        logger.info("{} | {} | 🔄 {} - {}/{} ({}%)",
                getCurrentTime(), component, operation, current, total, percentage);
    }
    /**
     * Log de progresso de consumo com informações detalhadas
     */
    public void consumptionProgress(String consumptionType, String stage, int current, int total, String details) {
        setContext("CONSUMPTION_PROGRESS");
        int percentage = total > 0 ? (int) ((current * 100.0) / total) : 0;
        showProgressBar(consumptionType, current, total, details);
        logger.info("PROGRESS_BAR | {} | {} | {}/{} | {}% | {}",
                consumptionType, stage, current, total, percentage, details != null ? details : "");
    }
    /**
     * Log de início de consumo
     */
    public void consumptionStart(String consumptionType, String description) {
        setContext("CONSUMPTION_START");
        showStartBanner(consumptionType, description);
        logger.info("CONSUMPTION_START | {} | {}", consumptionType, description);
    }
    /**
     * Log de finalização de consumo
     */
    public void consumptionEnd(String consumptionType, String result, long durationMs) {
        setContext("CONSUMPTION_END");
        String duration = formatDuration(durationMs);
        showEndBanner(consumptionType, result, duration);
        logger.info("CONSUMPTION_END | {} | {} | {}", consumptionType, result, duration);
    }
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";
    private void showStartBanner(String consumptionType, String description) {
        System.out.println();
        System.out.println(GREEN + BOLD + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN + BOLD + "║" + centerText("🚀 INICIANDO CONSUMO 🚀", 62) + "║" + RESET);
        System.out.println(GREEN + BOLD + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println(CYAN + BOLD + "📋 TIPO: " + WHITE + consumptionType + RESET);
        System.out.println(CYAN + BOLD + "💬 DESC: " + WHITE + description + RESET);
        System.out.println(BLUE + "─".repeat(64) + RESET);
    }
    private void showProgressBar(String consumptionType, int current, int total, String details) {
        int percentage = total > 0 ? (int) ((current * 100.0) / total) : 0;
        String progressBar = createProgressBar(percentage, 40);
        System.out.print("\r" + YELLOW + BOLD + "⚡ " + CYAN + consumptionType + RESET + " ");
        System.out.print(progressBar + " ");
        System.out.print(WHITE + BOLD + percentage + "%" + RESET + " ");
        System.out.print(PURPLE + BOLD + "(" + current + "/" + total + ")" + RESET);
        if (details != null && !details.isEmpty()) {
            System.out.print(" " + WHITE + details + RESET);
        }
        if (current >= total) {
            System.out.println();
        }
    }
    private void showEndBanner(String consumptionType, String result, String duration) {
        System.out.println();
        System.out.println(GREEN + BOLD + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN + BOLD + "║" + centerText("✅ CONSUMO FINALIZADO ✅", 62) + "║" + RESET);
        System.out.println(GREEN + BOLD + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println(CYAN + BOLD + "🎯 TIPO: " + WHITE + consumptionType + RESET);
        System.out.println(CYAN + BOLD + "🎉 RESULTADO: " + WHITE + result + RESET);
        System.out.println(CYAN + BOLD + "⏱️ DURAÇÃO: " + WHITE + duration + RESET);
        System.out.println(BLUE + "─".repeat(64) + RESET);
        System.out.println();
    }
    private String createProgressBar(int percentage, int width) {
        int filled = (int) ((double) percentage / 100 * width);
        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                bar.append(GREEN + "█" + RESET);
            } else {
                bar.append(RED + "░" + RESET);
            }
        }
        bar.append("]");
        return bar.toString();
    }
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }
    private String formatDuration(long durationMs) {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.1fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    /**
     * Log de início de operação
     */
    public void start(String component, String operation) {
        setContext(component);
        logger.info("{} | {} | 🚀 Iniciando {}", getCurrentTime(), component, operation);
    }
    /**
     * Log de operação lenta
     */
    public void slow(String component, String message, long durationMs) {
        setContext(component);
        String duration = formatDuration(durationMs);
        logger.warn("{} | {} | 🐌 {} ({})", getCurrentTime(), component, message, duration);
    }
    private String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
    private void setContext(String component) {
        MDC.put("component", component);
        if (MDC.get("correlationId") == null) {
            MDC.put("correlationId", UUID.randomUUID().toString().substring(0, 8));
        }
    }
    /**
     * Log de erro estruturado em markdown
     */
    private void logErrorToMarkdown(String component, String message, Exception e) {
        StringBuilder markdownError = new StringBuilder();
        markdownError.append("\n## ").append(getCurrentTime()).append(" | ❌ ERRO em ").append(component).append("\n");
        markdownError.append("- 🚨 **Erro**: ").append(message).append("\n");
        if (e != null) {
            markdownError.append("- 🔍 **Tipo**: ").append(e.getClass().getSimpleName()).append("\n");
            markdownError.append("- 📋 **Detalhes**: ").append(e.getMessage()).append("\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                markdownError.append("- 📍 **Local**: ").append(stackTrace[0].toString()).append("\n");
            }
        }
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            markdownError.append("- 🔗 **Correlation ID**: ").append(correlationId).append("\n");
        }
        markdownError.append("\n");
        markdownLogger.info(markdownError.toString());
    }
}