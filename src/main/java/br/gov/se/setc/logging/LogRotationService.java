package br.gov.se.setc.logging;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.Appender;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
/**
 * Serviço para rotação ativa de logs quando atingem tamanho crítico
 */
@Service
public class LogRotationService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogRotationService.class);
    @Value("${logging.path:./logs}")
    private String logPath;
    @Value("${logging.rotation.max-size-mb:3}")
    private long maxSizeMb;
    @Value("${logging.rotation.enabled:true}")
    private boolean rotationEnabled;
    private static final String OPERATIONS_FILE = "operations.md";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final List<String> LOG_FILES = Arrays.asList(
        "operations.md",
        "application.log",
        "simple.log",
        "errors.log"
    );
    private static final List<String> APPENDER_NAMES = Arrays.asList(
        "MARKDOWN_FILE",
        "APPLICATION_FILE",
        "SIMPLE_FILE",
        "ERROR_FILE"
    );
    /**
     * Monitora o tamanho do arquivo operations.md a cada 30 minutos
     */
    @Scheduled(fixedRate = 1800000)
    public void monitorLogSize() {
        if (!rotationEnabled) {
            return;
        }
        try {
            Path operationsFile = Paths.get(logPath, OPERATIONS_FILE);
            if (Files.exists(operationsFile)) {
                long sizeInBytes = Files.size(operationsFile);
                long sizeMb = sizeInBytes / (1024 * 1024);
                logger.debug("📊 Tamanho atual do operations.md: {} MB", sizeMb);
                if (sizeMb >= maxSizeMb) {
                    logger.info("⚠️ Arquivo operations.md atingiu {} MB, iniciando rotação...", sizeMb);
                    forceRotation();
                }
            }
        } catch (Exception e) {
            logger.error("❌ Erro ao monitorar tamanho do log", e);
        }
    }
    /**
     * Força a rotação do arquivo operations.md (mantido para compatibilidade)
     */
    public RotationResult forceRotation() {
        return forceOperationsRotation();
    }
    /**
     * Força a rotação de todos os arquivos de log
     */
    public AllLogsRotationResult forceAllLogsRotation() {
        logger.info("🔄 Iniciando rotação forçada de TODOS os logs...");
        AllLogsRotationResult allResult = new AllLogsRotationResult();
        List<RotationResult> results = new ArrayList<>();
        for (int i = 0; i < LOG_FILES.size(); i++) {
            String logFile = LOG_FILES.get(i);
            String appenderName = APPENDER_NAMES.get(i);
            logger.info("🔄 Rotacionando arquivo: {} (appender: {})", logFile, appenderName);
            RotationResult result = rotateLogFileViaLogback(logFile, appenderName);
            results.add(result);
            if (result.success) {
                allResult.successCount++;
                allResult.totalOriginalBytes += result.originalSizeBytes;
                allResult.totalCompressedBytes += result.compressedSizeBytes;
            } else {
                allResult.failureCount++;
                allResult.errors.add(logFile + ": " + result.message);
            }
        }
        allResult.results = results;
        allResult.success = allResult.failureCount == 0;
        if (allResult.totalOriginalBytes > 0) {
            allResult.overallCompressionRatio = (double) allResult.totalCompressedBytes / allResult.totalOriginalBytes;
        }
        allResult.message = String.format("Rotação de %d logs concluída. Sucessos: %d, Falhas: %d. " +
                "Compressão total: %.1f%% (%.1f MB → %.1f MB)",
                LOG_FILES.size(), allResult.successCount, allResult.failureCount,
                allResult.overallCompressionRatio * 100,
                allResult.totalOriginalBytes / (1024.0 * 1024.0),
                allResult.totalCompressedBytes / (1024.0 * 1024.0));
        logger.info("✅ {}", allResult.message);
        return allResult;
    }
    /**
     * Força a rotação apenas do arquivo operations.md
     */
    public RotationResult forceOperationsRotation() {
        logger.info("🔄 Iniciando rotação forçada do operations.md...");
        return rotateLogFileViaLogback(OPERATIONS_FILE, "MARKDOWN_FILE");
    }
    /**
     * Rotaciona um arquivo de log específico usando uma abordagem híbrida
     */
    private RotationResult rotateLogFileViaLogback(String fileName, String appenderName) {
        RotationResult result = new RotationResult();
        result.fileName = fileName;
        try {
            Path logFile = Paths.get(logPath, fileName);
            if (!Files.exists(logFile)) {
                logger.warn("Arquivo {} não encontrado", fileName);
                result.success = false;
                result.message = "Arquivo não encontrado";
                return result;
            }
            long originalSize = Files.size(logFile);
            result.originalSizeBytes = originalSize;
            if (originalSize == 0) {
                logger.info("📄 Arquivo {} está vazio, pulando rotação", fileName);
                result.success = true;
                result.message = "Arquivo vazio, rotação desnecessária";
                return result;
            }
            boolean rotationSuccess = performManualRotation(fileName, appenderName);
            if (rotationSuccess) {
                long newSize = Files.exists(logFile) ? Files.size(logFile) : 0;
                result.success = true;
                result.message = String.format("Rotação de %s concluída. Tamanho original: %s, novo tamanho: %s",
                        fileName, formatBytes(originalSize), formatBytes(newSize));
                result.compressedSizeBytes = newSize;
                result.compressionRatio = originalSize > 0 ? (double) newSize / originalSize : 0.0;
                result.rotatedFileName = "rotacionado_manualmente";
                logger.info("✅ {}", result.message);
            } else {
                result.success = false;
                result.message = "Falha ao forçar rotação";
                logger.error("❌ {}", result.message);
            }
        } catch (Exception e) {
            logger.error("❌ Erro durante rotação de {}", fileName, e);
            result.success = false;
            result.message = "Erro: " + e.getMessage();
        }
        return result;
    }
    /**
     * Realiza rotação manual segura do arquivo de log
     */
    private boolean performManualRotation(String fileName, String appenderName) {
        try {
            Path logFile = Paths.get(logPath, fileName);
            RollingFileAppender<?> appender = findRollingFileAppender(appenderName);
            if (appender == null) {
                logger.warn("⚠️ Appender {} não encontrado", appenderName);
                return false;
            }
            appender.stop();
            try {
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                String extension = fileName.substring(fileName.lastIndexOf('.'));
                String rotatedFileName = String.format("%s.%s%s", baseName, timestamp, extension);
                Path rotatedFile = Paths.get(logPath, rotatedFileName);
                Files.copy(logFile, rotatedFile);
                logger.info("📁 Arquivo copiado para: {}", rotatedFileName);
                Path compressedFile = compressFile(rotatedFile);
                Files.deleteIfExists(rotatedFile);
                Files.write(logFile, new byte[0]);
                logger.info("🧹 Arquivo original limpo: {}", fileName);
                logger.info("✅ Rotação manual concluída para: {}", fileName);
                return true;
            } finally {
                appender.start();
                logger.info("🔄 Appender {} reiniciado", appenderName);
            }
        } catch (Exception e) {
            logger.error("❌ Erro durante rotação manual de {}", fileName, e);
            return false;
        }
    }
    /**
     * Encontra um RollingFileAppender pelo nome
     */
    private RollingFileAppender<?> findRollingFileAppender(String appenderName) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (Logger loggerInstance : loggerContext.getLoggerList()) {
                Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenderIterator =
                    loggerInstance.iteratorForAppenders();
                while (appenderIterator.hasNext()) {
                    Appender<ch.qos.logback.classic.spi.ILoggingEvent> appender = appenderIterator.next();
                    if (appenderName.equals(appender.getName()) && appender instanceof RollingFileAppender) {
                        return (RollingFileAppender<?>) appender;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar appender {}", appenderName, e);
            return null;
        }
    }
    /**
     * Força a rotação de um appender específico do Logback (método original - mantido para referência)
     */
    private boolean forceLogbackRotation(String appenderName) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            logger.info("🔍 Buscando appender: {}", appenderName);
            logger.info("🔍 Loggers disponíveis:");
            Appender<?> foundAppender = null;
            for (Logger loggerInstance : loggerContext.getLoggerList()) {
                logger.info("🔍 Logger: {} - Appenders: {}",
                    loggerInstance.getName(),
                    getAppenderNames(loggerInstance));
                Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenderIterator =
                    loggerInstance.iteratorForAppenders();
                while (appenderIterator.hasNext()) {
                    Appender<ch.qos.logback.classic.spi.ILoggingEvent> appender = appenderIterator.next();
                    logger.info("🔍 Encontrado appender: {} (tipo: {})",
                        appender.getName(), appender.getClass().getSimpleName());
                    if (appenderName.equals(appender.getName())) {
                        foundAppender = appender;
                        logger.info("✅ Appender encontrado: {}", appenderName);
                        break;
                    }
                }
                if (foundAppender != null) {
                    break;
                }
            }
            if (foundAppender instanceof RollingFileAppender) {
                RollingFileAppender<?> rollingAppender = (RollingFileAppender<?>) foundAppender;
                rollingAppender.rollover();
                logger.info("🔄 Rotação forçada para appender: {}", appenderName);
                return true;
            } else if (foundAppender != null) {
                logger.warn("⚠️ Appender {} encontrado mas não é um RollingFileAppender (tipo: {})",
                    appenderName, foundAppender.getClass().getSimpleName());
                return false;
            } else {
                logger.warn("⚠️ Appender {} não foi encontrado", appenderName);
                return false;
            }
        } catch (Exception e) {
            logger.error("❌ Erro ao forçar rotação do appender {}", appenderName, e);
            return false;
        }
    }
    /**
     * Obtém os nomes dos appenders de um logger
     */
    private String getAppenderNames(Logger logger) {
        StringBuilder names = new StringBuilder();
        Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> iterator = logger.iteratorForAppenders();
        while (iterator.hasNext()) {
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(iterator.next().getName());
        }
        return names.toString();
    }
    /**
     * Formata bytes em formato legível
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    /**
     * Rotaciona um arquivo de log específico (método antigo - mantido para compatibilidade)
     */
    private RotationResult rotateLogFile(String fileName) {
        RotationResult result = new RotationResult();
        result.fileName = fileName;
        try {
            Path logFile = Paths.get(logPath, fileName);
            if (!Files.exists(logFile)) {
                logger.warn("Arquivo {} não encontrado", fileName);
                result.success = false;
                result.message = "Arquivo não encontrado";
                return result;
            }
            long originalSize = Files.size(logFile);
            result.originalSizeBytes = originalSize;
            if (originalSize == 0) {
                logger.info("📄 Arquivo {} está vazio, pulando rotação", fileName);
                result.success = true;
                result.message = "Arquivo vazio, rotação desnecessária";
                return result;
            }
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String rotatedFileName = String.format("%s.%s%s", baseName, timestamp, extension);
            Path rotatedFile = Paths.get(logPath, rotatedFileName);
            Files.move(logFile, rotatedFile);
            logger.info("📁 Arquivo {} movido para: {}", fileName, rotatedFileName);
            Path compressedFile = compressFile(rotatedFile);
            result.compressedSizeBytes = Files.size(compressedFile);
            result.compressionRatio = (double) result.compressedSizeBytes / result.originalSizeBytes;
            Files.deleteIfExists(rotatedFile);
            Files.createFile(logFile);
            result.success = true;
            result.rotatedFileName = compressedFile.getFileName().toString();
            result.message = String.format("Rotação de %s concluída. Arquivo compactado: %s (%.1f%% do tamanho original)",
                    fileName, result.rotatedFileName, result.compressionRatio * 100);
            logger.info("✅ {}", result.message);
        } catch (Exception e) {
            logger.error("❌ Erro durante rotação de {}", fileName, e);
            result.success = false;
            result.message = "Erro: " + e.getMessage();
        }
        return result;
    }
    /**
     * Compacta um arquivo usando GZIP
     */
    private Path compressFile(Path sourceFile) throws IOException {
        Path compressedFile = Paths.get(sourceFile.toString() + ".gz");
        try (FileInputStream fis = new FileInputStream(sourceFile.toFile());
             FileOutputStream fos = new FileOutputStream(compressedFile.toFile());
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, length);
            }
        }
        logger.info("📦 Arquivo compactado: {} -> {}",
                formatBytes(Files.size(sourceFile)),
                formatBytes(Files.size(compressedFile)));
        return compressedFile;
    }
    /**
     * Obtém informações sobre o arquivo operations.md atual
     */
    public FileInfo getCurrentFileInfo() {
        FileInfo info = new FileInfo();
        try {
            Path operationsFile = Paths.get(logPath, OPERATIONS_FILE);
            if (Files.exists(operationsFile)) {
                info.exists = true;
                info.sizeBytes = Files.size(operationsFile);
                info.sizeMb = info.sizeBytes / (1024.0 * 1024.0);
                info.lastModified = Files.getLastModifiedTime(operationsFile).toString();
                info.needsRotation = info.sizeMb >= maxSizeMb;
                long lineCount = Files.lines(operationsFile).count();
                info.estimatedLines = lineCount;
            }
        } catch (Exception e) {
            logger.error("Erro ao obter informações do arquivo", e);
            info.error = e.getMessage();
        }
        return info;
    }
    /**
     * Resultado da operação de rotação
     */
    public static class RotationResult {
        public boolean success = false;
        public String message = "";
        public String fileName = "";
        public String rotatedFileName = "";
        public long originalSizeBytes = 0;
        public long compressedSizeBytes = 0;
        public double compressionRatio = 0.0;
        @Override
        public String toString() {
            return message;
        }
    }
    /**
     * Resultado da operação de rotação de todos os logs
     */
    public static class AllLogsRotationResult {
        public boolean success = false;
        public String message = "";
        public int successCount = 0;
        public int failureCount = 0;
        public long totalOriginalBytes = 0;
        public long totalCompressedBytes = 0;
        public double overallCompressionRatio = 0.0;
        public List<RotationResult> results = new ArrayList<>();
        public List<String> errors = new ArrayList<>();
        @Override
        public String toString() {
            return message;
        }
    }
    /**
     * Informações sobre o arquivo atual
     */
    public static class FileInfo {
        public boolean exists = false;
        public long sizeBytes = 0;
        public double sizeMb = 0.0;
        public String lastModified = "";
        public boolean needsRotation = false;
        public long estimatedLines = 0;
        public String error = null;
        @Override
        public String toString() {
            if (!exists) return "Arquivo não existe";
            if (error != null) return "Erro: " + error;
            return String.format("Tamanho: %.1f MB (%d bytes), Linhas: ~%d, Última modificação: %s, Precisa rotação: %s",
                    sizeMb, sizeBytes, estimatedLines, lastModified, needsRotation ? "SIM" : "NÃO");
        }
    }
}