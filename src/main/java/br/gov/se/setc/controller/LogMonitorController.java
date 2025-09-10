package br.gov.se.setc.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
/**
 * Controller para monitoramento de logs em tempo real
 * Fornece endpoints REST e Server-Sent Events para visualização de logs
 */
@RestController
@RequestMapping("/logs")
public class LogMonitorController {
    private static final Logger logger = Logger.getLogger(LogMonitorController.class.getName());
    @Value("${logging.path:./logs}")
    private String logPath;
    private final Map<String, Long> filePositions = new ConcurrentHashMap<>();
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final List<String> LOG_FILES = Arrays.asList(
        "simple.log", "application.log", "errors.log", "operations.md"
    );
    public LogMonitorController() {
    }
    @PostConstruct
    public void init() {
        initializeFileMonitoring();
    }
    /**
     * Informações sobre os arquivos de log disponíveis
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getLogInfo() {
        Map<String, Object> info = new HashMap<>();
        List<Map<String, Object>> files = new ArrayList<>();
        for (String fileName : LOG_FILES) {
            Map<String, Object> fileInfo = new HashMap<>();
            Path filePath = Paths.get(logPath, fileName);
            fileInfo.put("name", fileName);
            fileInfo.put("exists", Files.exists(filePath));
            if (Files.exists(filePath)) {
                try {
                    fileInfo.put("size", Files.size(filePath));
                    fileInfo.put("lastModified", Files.getLastModifiedTime(filePath).toString());
                    fileInfo.put("readable", Files.isReadable(filePath));
                } catch (IOException e) {
                    fileInfo.put("error", e.getMessage());
                }
            }
            files.add(fileInfo);
        }
        info.put("files", files);
        info.put("logPath", logPath);
        info.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        info.put("activeConnections", emitters.size());
        return ResponseEntity.ok(info);
    }
    /**
     * Endpoint para obter conteúdo de um arquivo de log específico
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Map<String, Object>> getLogContent(
            @PathVariable String fileName,
            @RequestParam(defaultValue = "100") int lines,
            @RequestParam(defaultValue = "0") long fromPosition) {
        try {
            if (!LOG_FILES.contains(fileName)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Arquivo de log não encontrado: " + fileName));
            }
            Path filePath = Paths.get(logPath, fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.ok(Map.of(
                    "content", "",
                    "lines", 0,
                    "size", 0L,
                    "lastModified", "",
                    "fileName", fileName
                ));
            }
            List<String> content = readLastLines(filePath, lines, fromPosition);
            long fileSize = Files.size(filePath);
            String lastModified = Files.getLastModifiedTime(filePath).toString();
            Map<String, Object> response = new HashMap<>();
            response.put("content", String.join("\n", content));
            response.put("lines", content.size());
            response.put("size", fileSize);
            response.put("lastModified", lastModified);
            response.put("fileName", fileName);
            response.put("position", fileSize);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Erro ao ler arquivo de log " + fileName + ": " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erro ao ler arquivo: " + e.getMessage()));
        }
    }
    /**
     * Server-Sent Events para atualizações em tempo real
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> {
            logger.warning("Erro no SSE: " + ex.getMessage());
            emitters.remove(emitter);
        });
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data(Map.of(
                    "message", "Conectado ao stream de logs",
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "files", LOG_FILES
                )));
        } catch (IOException e) {
            logger.severe("Erro ao enviar evento inicial: " + e.getMessage());
            emitters.remove(emitter);
        }
        return emitter;
    }
    /**
     * Inicializa o monitoramento de arquivos para detectar mudanças
     */
    private void initializeFileMonitoring() {
        try {
            if (logPath == null) {
                logPath = "./logs";
                logger.warning("logPath era null, usando valor padrão: ./logs");
            }
            Path logDir = Paths.get(logPath);
            if (!Files.exists(logDir)) {
                try {
                    Files.createDirectories(logDir);
                    logger.info("Diretório de logs criado: " + logPath);
                } catch (IOException e) {
                    logger.severe("Erro ao criar diretório de logs: " + e.getMessage());
                }
            }
            for (String fileName : LOG_FILES) {
                Path filePath = Paths.get(logPath, fileName);
                if (Files.exists(filePath)) {
                    try {
                        long fileSize = Files.size(filePath);
                        filePositions.put(fileName, fileSize);
                        logger.info("Arquivo " + fileName + " inicializado com tamanho: " + fileSize + " bytes");
                    } catch (IOException e) {
                        logger.warning("Erro ao obter tamanho inicial do arquivo " + fileName + ": " + e.getMessage());
                        filePositions.put(fileName, 0L);
                    }
                } else {
                    filePositions.put(fileName, 0L);
                    logger.info("Arquivo " + fileName + " não existe ainda");
                }
            }
            scheduler.scheduleAtFixedRate(this::checkForFileChanges, 1, 2, TimeUnit.SECONDS);
            logger.info("Monitoramento de arquivos de log inicializado para: " + logPath);
        } catch (Exception e) {
            logger.severe("Erro ao inicializar monitoramento de arquivos: " + e.getMessage());
        }
    }
    /**
     * Verifica mudanças nos arquivos de log e envia atualizações via SSE
     */
    private void checkForFileChanges() {
        for (String fileName : LOG_FILES) {
            try {
                Path filePath = Paths.get(logPath, fileName);
                if (!Files.exists(filePath)) continue;
                long currentSize = Files.size(filePath);
                long lastPosition = filePositions.getOrDefault(fileName, 0L);
                if (currentSize > lastPosition) {
                    List<String> newLines = readNewLines(filePath, lastPosition, currentSize);
                    if (!newLines.isEmpty()) {
                        sendLogUpdate(fileName, newLines, currentSize);
                        filePositions.put(fileName, currentSize);
                    }
                } else if (currentSize < lastPosition) {
                    filePositions.put(fileName, currentSize);
                    sendLogUpdate(fileName, Arrays.asList("--- Arquivo foi rotacionado ou truncado ---"), currentSize);
                }
            } catch (IOException e) {
                logger.warning("Erro ao verificar mudanças no arquivo " + fileName + ": " + e.getMessage());
            }
        }
    }
    /**
     * Envia atualizações de log via Server-Sent Events
     */
    private void sendLogUpdate(String fileName, List<String> newLines, long fileSize) {
        if (emitters.isEmpty()) return;
        Map<String, Object> updateData = Map.of(
            "fileName", fileName,
            "lines", newLines,
            "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "fileSize", fileSize
        );
        Set<SseEmitter> deadEmitters = new HashSet<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("logUpdate")
                    .data(updateData));
            } catch (IOException e) {
                logger.warning("Erro ao enviar atualização SSE: " + e.getMessage());
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
    /**
     * Lê as últimas N linhas de um arquivo com encoding UTF-8
     */
    private List<String> readLastLines(Path filePath, int maxLines, long fromPosition) throws IOException {
        List<String> allLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        if (allLines.isEmpty()) {
            return new ArrayList<>();
        }
        if (fromPosition <= 0) {
            int startIndex = Math.max(0, allLines.size() - maxLines);
            return new ArrayList<>(allLines.subList(startIndex, allLines.size()));
        }
        int startIndex = Math.max(0, allLines.size() - maxLines);
        return new ArrayList<>(allLines.subList(startIndex, allLines.size()));
    }
    /**
     * Lê novas linhas adicionadas ao arquivo com encoding UTF-8
     */
    private List<String> readNewLines(Path filePath, long fromPosition, long toPosition) throws IOException {
        List<String> newLines = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (allLines.isEmpty()) {
                return newLines;
            }
            int startIndex = Math.max(0, allLines.size() - 10);
            for (int i = startIndex; i < allLines.size(); i++) {
                newLines.add(allLines.get(i));
            }
            return newLines;
        } catch (IOException e) {
            logger.warning("Erro ao ler novas linhas do arquivo " + filePath + ": " + e.getMessage());
            return newLines;
        }
    }
}