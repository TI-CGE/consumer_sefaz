package br.gov.se.setc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Tag(name = "Gerenciamento de Logs", description = "Endpoints para gerenciar e visualizar logs")
public class LogManagementController {
    
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
}
