package br.gov.se.setc.logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * Logger que produz saída em formato Markdown para melhor legibilidade
 * de operações complexas e relatórios de execução.
 */
@Component
public class MarkdownLogger {
    private static final Logger logger = LoggerFactory.getLogger("MARKDOWN");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    /**
     * Inicia uma seção de log estruturado
     */
    public MarkdownSection startSection(String title) {
        return new MarkdownSection(title);
    }
    /**
     * Classe para construir logs estruturados em markdown
     */
    public class MarkdownSection {
        private final String title;
        private final String startTime;
        private final List<String> items;
        private long startTimestamp;
        public MarkdownSection(String title) {
            this.title = title;
            this.startTime = getCurrentTime();
            this.startTimestamp = System.currentTimeMillis();
            this.items = new ArrayList<>();
        }
        /**
         * Adiciona item de sucesso
         */
        public MarkdownSection success(String message) {
            items.add("- ✅ " + message);
            return this;
        }
        /**
         * Adiciona item de sucesso com duração
         */
        public MarkdownSection success(String message, long durationMs) {
            String duration = formatDuration(durationMs);
            items.add("- ✅ " + message + " (" + duration + ")");
            return this;
        }
        /**
         * Adiciona item de progresso
         */
        public MarkdownSection progress(String message) {
            items.add("- 🔄 " + message);
            return this;
        }
        /**
         * Adiciona item de aviso
         */
        public MarkdownSection warning(String message) {
            items.add("- ⚠️ " + message);
            return this;
        }
        /**
         * Adiciona item de erro
         */
        public MarkdownSection error(String message) {
            items.add("- ❌ " + message);
            return this;
        }
        /**
         * Adiciona item de informação
         */
        public MarkdownSection info(String message) {
            items.add("- 📋 " + message);
            return this;
        }
        /**
         * Adiciona resumo final
         */
        public MarkdownSection summary(String message) {
            long totalDuration = System.currentTimeMillis() - startTimestamp;
            String duration = formatDuration(totalDuration);
            items.add("- ⏱️ **" + message + " | Tempo total: " + duration + "**");
            return this;
        }
        /**
         * Finaliza e registra a seção
         */
        public void log() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n## ").append(startTime).append(" | ").append(title).append("\n");
            for (String item : items) {
                sb.append(item).append("\n");
            }
            logger.info(sb.toString());
        }
        /**
         * Finaliza e registra a seção com resumo automático
         */
        public void logWithSummary(int totalRecords) {
            long totalDuration = System.currentTimeMillis() - startTimestamp;
            summary("Total: " + totalRecords + " registros");
            log();
        }
    }
    /**
     * Log simples em formato markdown
     */
    public void logSimple(String title, String message) {
        String time = getCurrentTime();
        logger.info("\n## {} | {}\n- 📋 {}\n", time, title, message);
    }
    /**
     * Log de erro em formato markdown
     */
    public void logError(String title, String error, Exception e) {
        String time = getCurrentTime();
        logger.error("\n## {} | {}\n- ❌ {}\n- 🔍 Detalhes: {}\n", time, title, error, e.getMessage());
    }
    private String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
    private String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.1fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
}