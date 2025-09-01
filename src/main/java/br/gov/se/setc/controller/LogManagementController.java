package br.gov.se.setc.controller;
import br.gov.se.setc.logging.LogCleanupService;
import br.gov.se.setc.logging.LogRotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Controller para gerenciar arquivos de log
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Gerenciamento de Logs", description = "API para gerenciamento, rotação e monitoramento de arquivos de log")
public class LogManagementController {
    @Autowired
    private LogRotationService logRotationService;
    @Autowired
    private LogCleanupService logCleanupService;
    private static final String LOGS_DIR = "logs";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Lista status de todos os arquivos de log
     */
    @GetMapping("/status")
    @Operation(
        summary = "Status dos arquivos de log",
        description = "Mostra informações sobre todos os arquivos de log existentes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status dos logs obtido com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public String getLogStatus() {
        StringBuilder result = new StringBuilder();
        result.append("📊 Status dos Logs - ").append(LocalDateTime.now().format(FORMATTER)).append("\n\n");
        File logsDir = new File(LOGS_DIR);
        if (!logsDir.exists()) {
            return "❌ Diretório de logs não encontrado: " + LOGS_DIR;
        }
        File[] logFiles = logsDir.listFiles((dir, name) -> 
            name.endsWith(".log") || name.endsWith(".md"));
        if (logFiles == null || logFiles.length == 0) {
            result.append("📁 Nenhum arquivo de log encontrado\n");
        } else {
            for (File file : logFiles) {
                long size = file.length();
                String sizeStr = formatFileSize(size);
                String lastModified = LocalDateTime.ofEpochSecond(
                    file.lastModified() / 1000, 0, 
                    java.time.ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now())
                ).format(FORMATTER);
                result.append("📄 ").append(file.getName())
                      .append(" - ").append(sizeStr)
                      .append(" (modificado: ").append(lastModified).append(")\n");
            }
        }
        return result.toString();
    }
    /**
     * Mostra as últimas linhas de um arquivo de log específico
     */
    @GetMapping("/tail/simple")
    @Operation(
        summary = "Últimas linhas do simple.log",
        description = "Mostra as últimas 20 linhas do arquivo simple.log"
    )
    public String tailSimpleLog() {
        return tailLogFile("simple.log", 20);
    }
    @GetMapping("/tail/errors")
    @Operation(
        summary = "Últimas linhas do errors.log",
        description = "Mostra as últimas 20 linhas do arquivo errors.log"
    )
    public String tailErrorsLog() {
        return tailLogFile("errors.log", 20);
    }
    @GetMapping("/tail/application")
    @Operation(
        summary = "Últimas linhas do application.log",
        description = "Mostra as últimas 20 linhas do arquivo application.log"
    )
    public String tailApplicationLog() {
        return tailLogFile("application.log", 20);
    }
    /**
     * Limpa todos os arquivos de log (apenas para desenvolvimento)
     */
    @DeleteMapping("/clear")
    @Operation(
        summary = "Limpa todos os logs",
        description = "Remove o conteúdo de todos os arquivos de log (apenas para desenvolvimento)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logs limpos com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao limpar logs")
    })
    public String clearLogs() {
        StringBuilder result = new StringBuilder();
        result.append("🧹 Limpando arquivos de log...\n\n");
        File logsDir = new File(LOGS_DIR);
        if (!logsDir.exists()) {
            return "❌ Diretório de logs não encontrado";
        }
        String[] logFiles = {"simple.log", "errors.log", "application.log", "operations.md"};
        int cleared = 0;
        for (String fileName : logFiles) {
            File file = new File(logsDir, fileName);
            if (file.exists()) {
                try {
                    Files.write(file.toPath(), new byte[0]);
                    result.append("✅ ").append(fileName).append(" - Limpo\n");
                    cleared++;
                } catch (IOException e) {
                    result.append("❌ ").append(fileName).append(" - Erro: ").append(e.getMessage()).append("\n");
                }
            } else {
                result.append("⚠️ ").append(fileName).append(" - Não encontrado\n");
            }
        }
        result.append("\n📊 Total de arquivos limpos: ").append(cleared).append("\n");
        result.append("🔄 Os logs serão recriados automaticamente quando a aplicação gerar novos eventos.\n");
        return result.toString();
    }
    private String tailLogFile(String fileName, int lines) {
        try {
            Path filePath = Paths.get(LOGS_DIR, fileName);
            if (!Files.exists(filePath)) {
                return "❌ Arquivo não encontrado: " + fileName;
            }
            java.util.List<String> allLines = Files.readAllLines(filePath);
            if (allLines.isEmpty()) {
                return "📄 " + fileName + " está vazio";
            }
            StringBuilder result = new StringBuilder();
            result.append("📄 Últimas ").append(Math.min(lines, allLines.size()))
                  .append(" linhas de ").append(fileName).append(":\n\n");
            int start = Math.max(0, allLines.size() - lines);
            for (int i = start; i < allLines.size(); i++) {
                result.append(String.format("%3d: %s\n", i + 1, allLines.get(i)));
            }
            return result.toString();
        } catch (IOException e) {
            return "❌ Erro ao ler arquivo " + fileName + ": " + e.getMessage();
        }
    }
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    /**
     * Força rotação de todos os arquivos de log
     */
    @PostMapping("/rotate")
    @Operation(
        summary = "Forçar rotação de todos os logs",
        description = "Força a rotação imediata de todos os arquivos de log (operations.md, application.log, simple.log, errors.log)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rotação executada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro durante a rotação")
    })
    public ResponseEntity<Map<String, Object>> forceRotation() {
        try {
            LogRotationService.AllLogsRotationResult result = logRotationService.forceAllLogsRotation();
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.success);
            response.put("message", result.message);
            response.put("successCount", result.successCount);
            response.put("failureCount", result.failureCount);
            response.put("totalOriginalBytes", result.totalOriginalBytes);
            response.put("totalCompressedBytes", result.totalCompressedBytes);
            response.put("overallCompressionRatio", String.format("%.1f%%", result.overallCompressionRatio * 100));
            response.put("errors", result.errors);
            List<Map<String, Object>> fileDetails = new ArrayList<>();
            for (LogRotationService.RotationResult fileResult : result.results) {
                Map<String, Object> fileDetail = new HashMap<>();
                fileDetail.put("fileName", fileResult.fileName);
                fileDetail.put("success", fileResult.success);
                fileDetail.put("message", fileResult.message);
                fileDetail.put("rotatedFileName", fileResult.rotatedFileName);
                fileDetail.put("originalSizeBytes", fileResult.originalSizeBytes);
                fileDetail.put("compressedSizeBytes", fileResult.compressedSizeBytes);
                fileDetail.put("compressionRatio", String.format("%.1f%%", fileResult.compressionRatio * 100));
                fileDetails.add(fileDetail);
            }
            response.put("fileDetails", fileDetails);
            if (result.success) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    /**
     * Força rotação apenas do arquivo operations.md
     */
    @PostMapping("/rotate/operations")
    @Operation(
        summary = "Forçar rotação apenas do operations.md",
        description = "Força a rotação imediata apenas do arquivo operations.md (compatibilidade com versão anterior)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rotação executada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro durante a rotação")
    })
    public ResponseEntity<Map<String, Object>> forceOperationsRotation() {
        try {
            LogRotationService.RotationResult result = logRotationService.forceOperationsRotation();
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.success);
            response.put("message", result.message);
            response.put("fileName", result.fileName);
            response.put("rotatedFileName", result.rotatedFileName);
            response.put("originalSizeBytes", result.originalSizeBytes);
            response.put("compressedSizeBytes", result.compressedSizeBytes);
            response.put("compressionRatio", String.format("%.1f%%", result.compressionRatio * 100));
            if (result.success) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    /**
     * Verifica informações detalhadas do operations.md
     */
    @GetMapping("/operations/info")
    @Operation(
        summary = "Informações do operations.md",
        description = "Retorna informações detalhadas sobre o arquivo operations.md"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações obtidas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Map<String, Object>> getOperationsInfo() {
        try {
            LogRotationService.FileInfo fileInfo = logRotationService.getCurrentFileInfo();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileExists", fileInfo.exists);
            response.put("sizeBytes", fileInfo.sizeBytes);
            response.put("sizeMB", String.format("%.2f", fileInfo.sizeMb));
            response.put("estimatedLines", fileInfo.estimatedLines);
            response.put("lastModified", fileInfo.lastModified);
            response.put("needsRotation", fileInfo.needsRotation);
            response.put("error", fileInfo.error);
            response.put("message", fileInfo.toString());
            if (fileInfo.needsRotation) {
                response.put("recommendation", "⚠️ Arquivo precisa de rotação! Execute POST /api/logs/rotate");
            } else if (fileInfo.sizeMb > 1.0) {
                response.put("recommendation", "📊 Arquivo está crescendo. Monitore o tamanho.");
            } else {
                response.put("recommendation", "✅ Arquivo em tamanho normal.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    /**
     * Executa limpeza automática de logs
     */
    @PostMapping("/cleanup")
    @Operation(
        summary = "Executar limpeza de logs",
        description = "Executa limpeza manual de logs antigos e otimização"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Limpeza executada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro durante a limpeza")
    })
    public ResponseEntity<Map<String, Object>> performCleanup() {
        try {
            LogCleanupService.CleanupResult result = logCleanupService.performCleanup();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filesDeleted", result.filesDeleted);
            response.put("emptyFilesDeleted", result.emptyFilesDeleted);
            response.put("largeFiles", result.largeFiles);
            response.put("bytesFreed", result.bytesFreed);
            response.put("totalSizeBytes", result.totalSizeBytes);
            response.put("sizeExceeded", result.sizeExceeded);
            response.put("errors", result.errors);
            response.put("message", result.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
