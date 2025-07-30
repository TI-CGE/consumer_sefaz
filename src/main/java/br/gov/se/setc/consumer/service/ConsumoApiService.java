package br.gov.se.setc.consumer.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.respository.EndpontSefazRepository;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import br.gov.se.setc.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import br.gov.se.setc.logging.MarkdownLogger;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.UserFriendlyLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import br.gov.se.setc.logging.util.LoggingUtils;
import org.springframework.beans.factory.annotation.Autowired;


public class ConsumoApiService<T extends EndpontSefaz> {

    private static final Logger logger = Logger.getLogger(ConsumoApiService.class.getName());
    private final RestTemplate restTemplate;
    private final AcessoTokenService acessoTokenService;
    private ValidacaoUtil<T> utilsService;
    private final EndpontSefazRepository<T > contratosFiscaisDAO;
    private final Class<T> typeClass;

    private List<String> ugCdArray;
    private final UnifiedLogger unifiedLogger;
    private UserFriendlyLogger userFriendlyLogger;
    private MarkdownLogger markdownLogger;

    public ConsumoApiService(RestTemplate restTemplate,
    AcessoTokenService acessoTokenService,
    JdbcTemplate jdbcTemplate, ValidacaoUtil<T> utilsService,
    UnifiedLogger unifiedLogger, UserFriendlyLogger userFriendlyLogger,
    MarkdownLogger markdownLogger, Class<T> type) {
        this.restTemplate = restTemplate;
        this.acessoTokenService = acessoTokenService;
        this.utilsService = utilsService;
        this.typeClass = type;
        this.unifiedLogger = unifiedLogger;
        this.userFriendlyLogger = userFriendlyLogger;
        this.markdownLogger = markdownLogger;
        contratosFiscaisDAO = new EndpontSefazRepository<T>(jdbcTemplate, unifiedLogger);
        this.ugCdArray = utilsService.getUgs();
    }

    //@Scheduled(fixedRate = 300000)
   // @Scheduled(cron = "0 45 2 * * *")
    @LogOperation(operation = "CONSUMIR_PERSISTIR", component = "CONTRACT_CONSUMER", slowOperationThresholdMs = 10000)
    public List<T> consumirPersistir(T mapper) {
        String operation = "CONSUMIR_PERSISTIR";
        String endpoint = mapper != null ? mapper.getUrl() : "UNKNOWN";

        // Configurar contexto de logging
        MDCUtil.setupOperationContext("CONTRACT_CONSUMER", operation);

        // Determinar tipo de dados baseado no mapper
        String dataType = mapper.getTabelaBanco().contains("unidade_gestora") ? "unidades gestoras" : "contratos fiscais";

        // Log simples para usuário
        userFriendlyLogger.logDataFetchStart(dataType);

        // Log técnico para arquivo
        unifiedLogger.logOperationStart("CONTRACT_CONSUMER", operation, "ENDPOINT", endpoint);

        // Iniciar seção de log estruturado em markdown
        MarkdownLogger.MarkdownSection markdownSection = markdownLogger.startSection("Consumo de " + dataType.substring(0, 1).toUpperCase() + dataType.substring(1));
        markdownSection.info("Endpoint: " + endpoint)
                      .progress("Iniciando consumo de dados...");

        long startTime = System.currentTimeMillis();
        List<T> resultList = new ArrayList<>();
        int totalRecordsProcessed = 0;

        try {
            if(utilsService.isEndpointIdependenteUGData(mapper)){
                List<T> result = pegarDadosIndependenteDataUg(mapper);
                if(result != null ){
                    resultList.addAll(result);
                    totalRecordsProcessed += result.size();
                }
            } else {
                for (String ugCd : ugCdArray) {
                    // Processar UG específica

                    if (utilsService.isPresenteBanco(mapper)) {
                        List<T> result = pegarDadosMesAnoVigente(ugCd,mapper);
                        if(result != null ){
                            resultList.addAll(result);
                            totalRecordsProcessed += result.size();
                        }
                        break; // Sair do loop após processar dados vigentes
                    } else {
                        List<T> result = pegarDeTodosAnos(ugCd,mapper);
                        if(result != null ){
                            resultList.addAll(result);
                            totalRecordsProcessed += result.size();
                        }
                    }
                }
            }

            if(resultList == null || resultList.isEmpty()) {
                String errorMessage = "Nenhum dado encontrado para o endpoint: " + endpoint;
                if(utilsService.isEndpointIdependenteUGData(mapper)) {
                    errorMessage += " (dados independentes de UG)";
                } else {
                    errorMessage += " (processando " + ugCdArray.size() + " UGs)";
                }
                logger.warning(errorMessage + " - Isso pode ser normal se não houver dados para o período consultado ou se a API estiver temporariamente indisponível.");

                // Log em markdown para dados não encontrados
                markdownSection.warning("Nenhum dado encontrado")
                              .info("Pode ser normal se não houver dados para o período")
                              .summary("0 registros processados")
                              .log();

                // Em vez de lançar exceção, vamos apenas retornar uma lista vazia
                // throw new RuntimeException(errorMessage);
                return new ArrayList<>();
            }

            // Log simples para usuário
            userFriendlyLogger.logDataFound(dataType, totalRecordsProcessed);

            // Log em markdown para dados encontrados
            markdownSection.success(totalRecordsProcessed + " registros encontrados");

            if (totalRecordsProcessed > 0) {
                userFriendlyLogger.logSavingStart();
                markdownSection.progress("Salvando dados no banco...");
            }

            // Log de persistência
            long persistStartTime = System.currentTimeMillis();
            contratosFiscaisDAO.persist(resultList);
            long persistTime = System.currentTimeMillis() - persistStartTime;

            // Log técnico para arquivo
            unifiedLogger.logDatabaseOperation("INSERT", mapper.getTabelaBanco(), resultList.size(), persistTime);

            long totalTime = System.currentTimeMillis() - startTime;

            // Log simples para usuário
            if (totalRecordsProcessed > 0) {
                userFriendlyLogger.logDataSaved(totalRecordsProcessed);
                markdownSection.success("Dados salvos no banco", persistTime);
            }
            userFriendlyLogger.logOperationComplete(totalTime);

            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("CONTRACT_CONSUMER", operation, totalTime, totalRecordsProcessed, "ENDPOINT", endpoint);

            // Finalizar log markdown com estatísticas
            markdownSection.info("📊 Estatísticas:")
                          .info("  • Registros processados: " + totalRecordsProcessed)
                          .info("  • Tempo de persistência: " + persistTime + "ms")
                          .info("  • Tabela: " + mapper.getTabelaBanco());

            if (totalTime > 10000) { // Mais de 10 segundos
                markdownSection.warning("Operação demorou mais que 10 segundos");
            }

            markdownSection.logWithSummary(totalRecordsProcessed);

            return resultList;

        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;

            // Log simples para usuário
            userFriendlyLogger.logError("busca de " + dataType, e.getMessage());

            // Log técnico para arquivo
            unifiedLogger.logOperationError("CONTRACT_CONSUMER", operation, totalTime, e, "ENDPOINT", endpoint);

            // Log de erro estruturado em markdown
            markdownSection.error("Falha na operação: " + e.getMessage())
                          .info("Tempo até falha: " + totalTime + "ms")
                          .summary("Operação interrompida por erro")
                          .log();
            throw e;
        } finally {
            MDCUtil.clear();
        }
    }

 

    @LogOperation(operation = "API_CALL_SEFAZ", component = "API_CLIENT")
    private ResponseEntity<String> respostaApiRaw(String apiUrl) {
        long startTime = System.currentTimeMillis();

        String token = acessoTokenService.getToken();
        logger.info("Fazendo requisição para: " + apiUrl);
        logger.info("Token obtido (primeiros 20 chars): " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if (apiUrl != null) {
            try {
                // Fazer chamada para API

                ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
                );

                long responseTime = System.currentTimeMillis() - startTime;
                int statusCode = response.getStatusCode().value();

                logger.info("Resposta recebida - Status: " + statusCode + " | Body length: " +
                           (response.getBody() != null ? response.getBody().length() : 0));

                if (statusCode != 200) {
                    logger.warning("Resposta não-200: " + statusCode + " | Body: " +
                                 (response.getBody() != null ? response.getBody().substring(0, Math.min(500, response.getBody().length())) : "null"));
                }

                // Log de performance da chamada de API
                int responseSize = response.getBody() != null ? LoggingUtils.calculateSizeInBytes(response.getBody()) : 0;
                unifiedLogger.logApiCall(apiUrl, "GET", statusCode, responseTime, 0, responseSize);

                // Log estruturado em markdown para chamada de API
                logApiCallToMarkdown(apiUrl, statusCode, responseTime, responseSize, null);

                return response;

            } catch (Exception e) {
                long responseTime = System.currentTimeMillis() - startTime;
                logger.severe("Erro na chamada da API SEFAZ: " + e.getMessage());
                e.printStackTrace();

                // Log de erro na API
                unifiedLogger.logApiCall(apiUrl, "GET", 500, responseTime, 0, 0);

                // Log estruturado em markdown para erro de API
                logApiCallToMarkdown(apiUrl, 500, responseTime, 0, e);

                return null;
            }
        }
        return null;
    }

    private List<T> processarRespostaSefaz(String jsonResponse, T mapper) {
        List<T> resultList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Log the raw response for debugging
            logger.info("Resposta SEFAZ (primeiros 500 chars): " +  jsonResponse.substring(0, Math.min(500, jsonResponse.length())));

            // Handle array responses (most SEFAZ endpoints return arrays)
            if (rootNode.isArray()) {
                for (JsonNode itemNode : rootNode) {
                    T newInstance = criarInstanciaGenerica(itemNode, mapper);
                    if (newInstance != null) {
                        resultList.add(newInstance);
                    }
                }
            } else {
                // Handle single object response
                T newInstance = criarInstanciaGenerica(rootNode, mapper);
                if (newInstance != null) {
                    resultList.add(newInstance);
                }
            }

        } catch (Exception e) {
            logger.severe("Erro ao processar resposta JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return resultList;
    }

    /**
     * Creates a new DTO instance and populates it with data from JSON using reflection
     * and the DTO's own mapearCamposResposta() method.
     * This approach is truly generic and works with any DTO that extends EndpontSefaz.
     */
    @SuppressWarnings("unchecked")
    private T criarInstanciaGenerica(JsonNode itemNode, T mapper) {
        try {
            // Create a new instance of the same type as the mapper
            T newInstance = (T) typeClass.getDeclaredConstructor().newInstance();

            // Populate the new instance with JSON data using reflection
            mapearJsonParaDTO(itemNode, newInstance);

            // Call the DTO's own mapearCamposResposta() method to finalize the mapping
            newInstance.mapearCamposResposta();

            return newInstance;

        } catch (Exception e) {
            logger.severe("Erro ao criar instância genérica do DTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generic method that uses reflection to map JSON fields to DTO properties.
     * This method automatically maps JSON fields to DTO setters without hardcoding.
     */
    private void mapearJsonParaDTO(JsonNode jsonNode, T dtoInstance) {
        try {
            Class<?> dtoClass = dtoInstance.getClass();

            // Iterate through all JSON fields
            jsonNode.fieldNames().forEachRemaining(fieldName -> {
                try {
                    JsonNode fieldValue = jsonNode.get(fieldName);
                    if (fieldValue != null && !fieldValue.isNull()) {
                        // Try to find and invoke the corresponding setter method
                        invocarSetterSeExistir(dtoInstance, dtoClass, fieldName, fieldValue);
                    }
                } catch (Exception e) {
                    logger.warning("Erro ao mapear campo '" + fieldName + "': " + e.getMessage());
                }
            });

        } catch (Exception e) {
            logger.severe("Erro no mapeamento genérico JSON para DTO: " + e.getMessage());
        }
    }

    /**
     * Uses reflection to find and invoke setter methods for JSON fields.
     * Supports multiple naming conventions and data types.
     */
    private void invocarSetterSeExistir(T dtoInstance, Class<?> dtoClass, String fieldName, JsonNode fieldValue) {
        try {
            // Try different setter naming conventions
            String[] possibleSetterNames = {
                "set" + capitalize(fieldName),                    // setFieldName
                "set" + capitalize(toCamelCase(fieldName)),       // setFieldName (from snake_case)
                "set" + fieldName,                               // setfieldName
                "set" + fieldName.toUpperCase()                  // setFIELDNAME
            };

            for (String setterName : possibleSetterNames) {
                if (tryInvokeSetterWithValue(dtoInstance, dtoClass, setterName, fieldValue)) {
                    logger.fine("Mapeado campo '" + fieldName + "' usando setter '" + setterName + "'");
                    return; // Success, stop trying other names
                }
            }

            logger.fine("Nenhum setter encontrado para campo: " + fieldName);

        } catch (Exception e) {
            logger.warning("Erro ao invocar setter para campo '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * Attempts to invoke a setter method with the appropriate data type conversion.
     */
    private boolean tryInvokeSetterWithValue(T dtoInstance, Class<?> dtoClass, String setterName, JsonNode fieldValue) {
        try {
            // Try String parameter first (most common)
            try {
                var method = dtoClass.getMethod(setterName, String.class);
                method.invoke(dtoInstance, fieldValue.asText());
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try Integer parameter
            try {
                var method = dtoClass.getMethod(setterName, Integer.class);
                method.invoke(dtoInstance, fieldValue.asInt());
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try Long parameter
            try {
                var method = dtoClass.getMethod(setterName, Long.class);
                method.invoke(dtoInstance, fieldValue.asLong());
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try Boolean parameter
            try {
                var method = dtoClass.getMethod(setterName, Boolean.class);
                method.invoke(dtoInstance, fieldValue.asBoolean());
                return true;
            } catch (NoSuchMethodException ignored) {}

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Utility method to capitalize the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Converts snake_case to camelCase.
     */
    private String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) return snakeCase;

        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                camelCase.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                camelCase.append(Character.toLowerCase(c));
            }
        }

        return camelCase.toString();
    }

    private List<T> pegarDadosIndependenteDataUg(T mapper) {
        List<T> resultadoAnoMesVigente = new ArrayList<>();
        String apiUrl = null;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            apiUrl = builder.toUriString();

        } catch (Exception e) {
            logger.severe("Erro ao montar URL independente de data e ug: " + e.getMessage());
        }
        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);
            if (response != null) {
                logger.info("Resposta da API recebida. Status: " + response.getStatusCode() + " | URL: " + apiUrl);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    logger.info("Processando JSON da resposta...");
                    List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                    if (processedData != null) {
                        resultadoAnoMesVigente.addAll(processedData);
                        logger.info("Dados processados com sucesso: " + processedData.size() + " registros");
                    } else {
                        logger.warning("processarRespostaSefaz retornou null");
                    }
                } else {
                    logger.warning("API retornou erro. Status: " + response.getStatusCode() +
                                 " | Body: " + (response.getBody() != null ? response.getBody().substring(0, Math.min(200, response.getBody().length())) : "null"));
                }
            } else {
                logger.warning("Resposta da API é null");
            }

            // Log técnico removido - dados processados internamente

        } catch (RestClientException e) {
            logger.severe("Erro ao consumir API vigente: " + e.getMessage());
        }
        return resultadoAnoMesVigente;
    }

    private List<T> pegarDadosMesAnoVigente(String ugCd, T mapper){
        List<T> resultadoAnoMesVigente = new ArrayList<>();
        String apiUrl = null;
                try {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
                    for (Map.Entry<String, Object> entry : mapper.getCamposParametrosAtual(ugCd, utilsService).entrySet()) {
                            builder.queryParam(entry.getKey(), entry.getValue());
                    }
                    apiUrl = builder.toUriString();

                } catch (Exception e) {
                      logger.severe("Erro ao consumir API vigente Url com parametros: " + e.getMessage());

                }
                try {
                    ResponseEntity<String> response = respostaApiRaw(apiUrl);
                    if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                        if (processedData != null) {
                            resultadoAnoMesVigente.addAll(processedData);
                        }
                    }

                } catch (RestClientException e) {
                    logger.severe("Erro ao consumir API vigente: " + e.getMessage());
                }
                return resultadoAnoMesVigente;
    }

    private List<T> pegarDeTodosAnos(String ugCd, T mapper){
        List<T> resultadoTodosAnos = new ArrayList<>();
        Short anoAtual = utilsService.getAnoAtual();

        for (Short dtAno = anoAtual; dtAno >= anoAtual-5;  dtAno--) {
                    String apiUrl = null;
                    try {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
                        for (Map.Entry<String, Object> entry : mapper.getCamposParametrosTodosOsAnos(ugCd, dtAno).entrySet()) {
                                builder.queryParam(entry.getKey(), entry.getValue());
                        }
                        apiUrl = builder.toUriString();

                    } catch (Exception e) {
                         logger.severe("Erro ao montar URL antiga: " + e.getMessage());
                    }
                  try {
                        ResponseEntity<String> response = respostaApiRaw(apiUrl);

                        if (response != null &&  response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                            List<T> anoResult = processarRespostaSefaz(response.getBody(), mapper);
                            if (anoResult != null) {
                                 resultadoTodosAnos.addAll(anoResult);
                            }
                        }

                  } catch (RestClientException e) {
                         logger.severe("Erro ao consumir API antiga: " + e.getMessage());
                }

            }
            return resultadoTodosAnos;
    }

    /**
     * Log estruturado em markdown para chamadas de API
     */
    private void logApiCallToMarkdown(String apiUrl, int statusCode, long responseTime, int responseSize, Exception error) {
        try {
            String endpoint = apiUrl.contains("?") ? apiUrl.substring(0, apiUrl.indexOf("?")) : apiUrl;
            String title = "Chamada de API SEFAZ";

            if (error != null) {
                // Log de erro de API
                markdownLogger.logError(title, "Falha na chamada: " + endpoint, error);
            } else {
                // Log de sucesso de API
                MarkdownLogger.MarkdownSection section = markdownLogger.startSection(title);

                if (statusCode == 200) {
                    section.success("GET " + endpoint + " - Status: " + statusCode, responseTime);
                } else if (statusCode >= 400) {
                    section.error("GET " + endpoint + " - Status: " + statusCode + " (Erro HTTP)");
                } else {
                    section.warning("GET " + endpoint + " - Status: " + statusCode + " (Status não-200)");
                }

                section.info("📊 Detalhes da chamada:")
                      .info("  • Tempo de resposta: " + responseTime + "ms")
                      .info("  • Tamanho da resposta: " + formatBytes(responseSize))
                      .info("  • Endpoint: " + endpoint);

                if (responseTime > 5000) {
                    section.warning("Chamada lenta detectada (>5s)");
                }

                section.log();
            }
        } catch (Exception e) {
            // Evitar que erros de logging quebrem a aplicação
            logger.warning("Erro ao registrar log de API em markdown: " + e.getMessage());
        }
    }

    /**
     * Formata bytes em formato legível
     */
    private String formatBytes(int bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

}