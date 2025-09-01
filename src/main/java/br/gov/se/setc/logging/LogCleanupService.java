package br.gov.se.setc.logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Serviço para limpeza automática de logs antigos e otimização de espaço
 */
@Service
public class LogCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(LogCleanupService.class);
    @Autowired
    private LogRotationService logRotationService;
    @Value("${logging.cleanup.enabled:true}")
    private boolean cleanupEnabled;
    @Value("${logging.cleanup.max-age-days:7}")
    private int maxAgeDays;
    @Value("${logging.cleanup.max-size-mb:500}")
    private long maxSizeMb;
    @Value("${logging.path:./logs}")
    private String logPath;
    /**
     * Executa limpeza diária às 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledCleanup() {
        if (cleanupEnabled) {
            performCleanup();
        }
    }
    /**
     * Executa limpeza manual
     */
    public CleanupResult performCleanup() {
        logger.info("🧹 Iniciando limpeza automática de logs...");
        CleanupResult result = new CleanupResult();
        Path logsDir = Paths.get(logPath);
        if (!Files.exists(logsDir)) {
            logger.warn("Diretório de logs não encontrado: {}", logPath);
            return result;
        }
        try {
            checkOperationsRotation(result);
            removeOldFiles(logsDir, result);
            compressLargeFiles(logsDir, result);
            removeEmptyFiles(logsDir, result);
            checkTotalSize(logsDir, result);
            logger.info("✅ Limpeza concluída: {}", result);
        } catch (IOException e) {
            logger.error("❌ Erro durante limpeza de logs", e);
            result.errors++;
        }
        return result;
    }
    /**
     * Verifica se o operations.md precisa de rotação e executa se necessário
     */
    private void checkOperationsRotation(CleanupResult result) {
        try {
            LogRotationService.FileInfo fileInfo = logRotationService.getCurrentFileInfo();
            if (fileInfo.needsRotation) {
                logger.info("🔄 operations.md precisa de rotação ({}MB), executando...",
                        String.format("%.1f", fileInfo.sizeMb));
                LogRotationService.RotationResult rotationResult = logRotationService.forceRotation();
                if (rotationResult.success) {
                    result.operationsRotated = true;
                    result.operationsRotationMessage = rotationResult.message;
                    logger.info("✅ Rotação do operations.md concluída: {}", rotationResult.message);
                } else {
                    logger.error("❌ Falha na rotação do operations.md: {}", rotationResult.message);
                    result.errors++;
                }
            }
        } catch (Exception e) {
            logger.error("❌ Erro ao verificar rotação do operations.md", e);
            result.errors++;
        }
    }
    /**
     * Remove arquivos mais antigos que o limite configurado
     */
    private void removeOldFiles(Path logsDir, CleanupResult result) throws IOException {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(maxAgeDays);
        Files.walkFileTree(logsDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                LocalDateTime fileTime = LocalDateTime.ofInstant(
                        attrs.lastModifiedTime().toInstant(),
                        ZoneId.systemDefault()
                );
                if (fileTime.isBefore(cutoff) && !isCurrentLogFile(file)) {
                    long size = attrs.size();
                    Files.delete(file);
                    result.filesDeleted++;
                    result.bytesFreed += size;
                    logger.debug("🗑️ Removido arquivo antigo: {} ({})", file.getFileName(), formatBytes(size));
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * Compacta arquivos de log grandes que não estão sendo usados atualmente
     */
    private void compressLargeFiles(Path logsDir, CleanupResult result) throws IOException {
        final long LARGE_FILE_THRESHOLD = 10 * 1024 * 1024;
        Files.walkFileTree(logsDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.size() > LARGE_FILE_THRESHOLD &&
                    !isCurrentLogFile(file) &&
                    !file.toString().endsWith(".gz")) {
                    logger.debug("📦 Arquivo grande detectado: {} ({})",
                            file.getFileName(), formatBytes(attrs.size()));
                    result.largeFiles++;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * Remove arquivos de log vazios
     */
    private void removeEmptyFiles(Path logsDir, CleanupResult result) throws IOException {
        Files.walkFileTree(logsDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.size() == 0 && !isCurrentLogFile(file)) {
                    Files.delete(file);
                    result.emptyFilesDeleted++;
                    logger.debug("🗑️ Removido arquivo vazio: {}", file.getFileName());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * Verifica se o tamanho total dos logs excede o limite
     */
    private void checkTotalSize(Path logsDir, CleanupResult result) throws IOException {
        AtomicLong totalSize = new AtomicLong(0);
        Files.walkFileTree(logsDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                totalSize.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        });
        result.totalSizeBytes = totalSize.get();
        long maxSizeBytes = maxSizeMb * 1024 * 1024;
        if (result.totalSizeBytes > maxSizeBytes) {
            logger.warn("⚠️ Tamanho total dos logs ({}) excede o limite configurado ({})",
                    formatBytes(result.totalSizeBytes), formatBytes(maxSizeBytes));
            result.sizeExceeded = true;
        }
    }
    /**
     * Verifica se é um arquivo de log atual (sendo usado)
     */
    private boolean isCurrentLogFile(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.endsWith(".log") && !fileName.contains(".");
    }
    /**
     * Formata bytes em formato legível
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    /**
     * Resultado da operação de limpeza
     */
    public static class CleanupResult {
        public int filesDeleted = 0;
        public int emptyFilesDeleted = 0;
        public int largeFiles = 0;
        public long bytesFreed = 0;
        public long totalSizeBytes = 0;
        public boolean sizeExceeded = false;
        public int errors = 0;
        public boolean operationsRotated = false;
        public String operationsRotationMessage = "";
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Arquivos removidos: %d, Vazios removidos: %d, Espaço liberado: %s, Tamanho total: %s",
                    filesDeleted, emptyFilesDeleted, formatBytes(bytesFreed), formatBytes(totalSizeBytes)));
            if (operationsRotated) {
                sb.append(", Operations.md rotacionado: ").append(operationsRotationMessage);
            }
            if (errors > 0) {
                sb.append(", Erros: ").append(errors);
            }
            return sb.toString();
        }
        private String formatBytes(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}