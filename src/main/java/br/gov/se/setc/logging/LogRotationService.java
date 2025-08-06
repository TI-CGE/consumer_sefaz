package br.gov.se.setc.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.slf4j.Logger;
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

/**
 * Serviço para rotação ativa de logs quando atingem tamanho crítico
 */
@Service
public class LogRotationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogRotationService.class);
    
    @Value("${logging.path:./logs}")
    private String logPath;
    
    @Value("${logging.rotation.max-size-mb:3}")
    private long maxSizeMb;
    
    @Value("${logging.rotation.enabled:true}")
    private boolean rotationEnabled;
    
    private static final String OPERATIONS_FILE = "operations.md";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Monitora o tamanho do arquivo operations.md a cada 30 minutos
     */
    @Scheduled(fixedRate = 1800000) // 30 minutos
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
     * Força a rotação do arquivo operations.md
     */
    public RotationResult forceRotation() {
        logger.info("🔄 Iniciando rotação forçada do operations.md...");
        
        RotationResult result = new RotationResult();
        
        try {
            Path operationsFile = Paths.get(logPath, OPERATIONS_FILE);
            
            if (!Files.exists(operationsFile)) {
                logger.warn("Arquivo operations.md não encontrado");
                result.success = false;
                result.message = "Arquivo não encontrado";
                return result;
            }
            
            // 1. Obter informações do arquivo atual
            long originalSize = Files.size(operationsFile);
            result.originalSizeBytes = originalSize;
            
            // 2. Criar nome do arquivo rotacionado
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String rotatedFileName = String.format("operations.%s.md", timestamp);
            Path rotatedFile = Paths.get(logPath, rotatedFileName);
            
            // 3. Mover arquivo atual para arquivo rotacionado
            Files.move(operationsFile, rotatedFile);
            logger.info("📁 Arquivo movido para: {}", rotatedFileName);
            
            // 4. Compactar arquivo rotacionado
            Path compressedFile = compressFile(rotatedFile);
            result.compressedSizeBytes = Files.size(compressedFile);
            result.compressionRatio = (double) result.compressedSizeBytes / result.originalSizeBytes;
            
            // 5. Remover arquivo não compactado
            Files.deleteIfExists(rotatedFile);
            
            // 6. Criar novo arquivo operations.md vazio
            Files.createFile(operationsFile);
            
            result.success = true;
            result.rotatedFileName = compressedFile.getFileName().toString();
            result.message = String.format("Rotação concluída. Arquivo compactado: %s (%.1f%% do tamanho original)", 
                    result.rotatedFileName, result.compressionRatio * 100);
            
            logger.info("✅ {}", result.message);
            
        } catch (Exception e) {
            logger.error("❌ Erro durante rotação forçada", e);
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
                
                // Contar linhas aproximadamente
                long lineCount = Files.lines(operationsFile).count();
                info.estimatedLines = lineCount;
            }
        } catch (Exception e) {
            logger.error("Erro ao obter informações do arquivo", e);
            info.error = e.getMessage();
        }
        
        return info;
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Resultado da operação de rotação
     */
    public static class RotationResult {
        public boolean success = false;
        public String message = "";
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
