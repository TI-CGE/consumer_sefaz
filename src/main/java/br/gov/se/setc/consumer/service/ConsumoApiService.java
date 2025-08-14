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
import br.gov.se.setc.consumer.repository.EndpontSefazRepository;
import br.gov.se.setc.consumer.service.JpaPersistenceService;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import br.gov.se.setc.util.ValidacaoUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private final JpaPersistenceService jpaPersistenceService;
    private final Class<T> typeClass;

    private List<String> ugCdArray;
    private final UnifiedLogger unifiedLogger;
    private UserFriendlyLogger userFriendlyLogger;
    private MarkdownLogger markdownLogger;

    public ConsumoApiService(RestTemplate restTemplate,
    AcessoTokenService acessoTokenService,
    JdbcTemplate jdbcTemplate, ValidacaoUtil<T> utilsService,
    UnifiedLogger unifiedLogger, UserFriendlyLogger userFriendlyLogger,
    MarkdownLogger markdownLogger, JpaPersistenceService jpaPersistenceService, Class<T> type) {
        this.restTemplate = restTemplate;
        this.acessoTokenService = acessoTokenService;
        this.utilsService = utilsService;
        this.typeClass = type;
        this.unifiedLogger = unifiedLogger;
        this.userFriendlyLogger = userFriendlyLogger;
        this.markdownLogger = markdownLogger;
        this.jpaPersistenceService = jpaPersistenceService;
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
        String dataType = getDataTypeFromMapper(mapper);
        boolean isUnidadeGestora = mapper.getTabelaBanco().contains("unidade_gestora");

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
                // Tratamento especial para Unidades Gestoras
                if (isUnidadeGestora) {
                    logger.info("=== PROCESSAMENTO DE UNIDADES GESTORAS ===");
                    logger.info("Tipo: Dados independentes de UG e ano (busca todas as UGs ativas)");
                    logger.info("Filtro aplicado: sgTipoUnidadeGestora=E");
                    userFriendlyLogger.logInfo("Buscando todas as Unidades Gestoras ativas...");
                    markdownSection.info("Buscando todas as UGs ativas (sgTipoUnidadeGestora=E)")
                                  .info("Não utiliza filtros de ano ou UG específica");
                } else {
                    logger.info("Processando dados independentes de UG para: " + mapper.getTabelaBanco());
                    userFriendlyLogger.logInfo("Buscando dados independentes de UG...");
                }

                long ugStartTime = System.currentTimeMillis();
                List<T> result = pegarDadosIndependenteDataUg(mapper);
                long ugDuration = System.currentTimeMillis() - ugStartTime;

                if(result != null ){
                    resultList.addAll(result);
                    totalRecordsProcessed += result.size();

                    if (isUnidadeGestora) {
                        logger.info("UGs encontradas: " + result.size());
                        userFriendlyLogger.logInfo(result.size() + " Unidades Gestoras encontradas");
                        markdownSection.success(result.size() + " Unidades Gestoras processadas", ugDuration);
                    } else {
                        logger.info("Registros encontrados: " + result.size());
                    }
                } else {
                    if (isUnidadeGestora) {
                        logger.warning("Nenhuma UG encontrada");
                        userFriendlyLogger.logWarning("Nenhuma Unidade Gestora encontrada");
                        markdownSection.warning("Nenhuma UG encontrada");
                    }
                }
            } else {
                // Determinar se deve processar apenas ano atual ou todos os anos
                boolean hasDataInDatabase = utilsService.isPresenteBanco(mapper);
                String entityName = mapper.getTabelaBanco().substring(mapper.getTabelaBanco().lastIndexOf('.') + 1);

                logger.info("=== PROCESSAMENTO DE " + entityName.toUpperCase() + " ===");
                logger.info("Total de UGs a processar: " + ugCdArray.size());
                logger.info("Dados existentes no banco: " + (hasDataInDatabase ? "SIM" : "NÃO"));

                userFriendlyLogger.logInfo("Processando " + ugCdArray.size() + " Unidades Gestoras...");

                if (!hasDataInDatabase) {
                    // PRIMEIRA EXECUÇÃO: Não há dados no banco - processar TODOS OS ANOS
                    logger.info("Modo: TODOS OS ANOS (2020-2025) - Carga inicial completa");
                    userFriendlyLogger.logInfo("Modo: Carga inicial completa (todos os anos)");

                    int ugProcessed = 0;
                    for (String ugCd : ugCdArray) {
                        ugProcessed++;
                        logger.info("Processando UG " + ugProcessed + "/" + ugCdArray.size() + ": " + ugCd);
                        userFriendlyLogger.logInfo("Processando UG " + ugProcessed + "/" + ugCdArray.size() + ": " + ugCd);

                        List<T> result = pegarDeTodosAnos(ugCd,mapper);
                        if(result != null ){
                            resultList.addAll(result);
                            totalRecordsProcessed += result.size();
                            logger.info("UG " + ugCd + ": " + result.size() + " registros encontrados (todos os anos)");
                        } else {
                            logger.info("UG " + ugCd + ": 0 registros encontrados");
                        }
                    }
                } else {
                    // EXECUÇÃO INCREMENTAL: Há dados no banco - processar APENAS ANO ATUAL (DELETE-BEFORE-INSERT)
                    logger.info("Modo: APENAS ANO ATUAL (" + utilsService.getAnoAtual() + ") - DELETE-BEFORE-INSERT");
                    userFriendlyLogger.logInfo("Modo: Atualização incremental do ano atual (" + utilsService.getAnoAtual() + ")");

                    int ugProcessed = 0;
                    for (String ugCd : ugCdArray) {
                        ugProcessed++;
                        logger.info("Processando UG " + ugProcessed + "/" + ugCdArray.size() + ": " + ugCd);
                        userFriendlyLogger.logInfo("Processando UG " + ugProcessed + "/" + ugCdArray.size() + ": " + ugCd);

                        List<T> result = pegarDadosMesAnoVigente(ugCd,mapper);
                        if(result != null ){
                            resultList.addAll(result);
                            totalRecordsProcessed += result.size();
                            logger.info("UG " + ugCd + ": " + result.size() + " registros atualizados (ano atual)");
                        } else {
                            logger.info("UG " + ugCd + ": 0 registros para atualizar");
                        }
                        // Removido o break incorreto que estava interrompendo o processamento de todas as UGs
                    }
                }

                logger.info("=== PROCESSAMENTO CONCLUÍDO ===");
                logger.info("Total de registros processados: " + totalRecordsProcessed);
                userFriendlyLogger.logInfo("Processamento concluído: " + totalRecordsProcessed + " registros");
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

            // Aplicar deduplicação se necessário
            List<T> resultListDeduplicated = deduplicateIfNeeded(resultList);

            // Log de persistência
            long persistStartTime = System.currentTimeMillis();

            // Usar persistência JPA se suportada, senão usar sistema legado
            if (jpaPersistenceService.isJpaPersistenceSupported(mapper.getTabelaBanco())) {
                jpaPersistenceService.persist(resultListDeduplicated);
            } else {
                contratosFiscaisDAO.persist(resultListDeduplicated);
            }

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
            // Configurar suporte para LocalDate e BigDecimal
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Log the raw response for debugging
            logger.info("Resposta SEFAZ (primeiros 500 chars): " +  jsonResponse.substring(0, Math.min(500, jsonResponse.length())));

            // Log específico para ConsultaGerencial para debug
            if (mapper.getTabelaBanco().contains("consulta_gerencial")) {
                logger.info("Processando resposta da API de Consulta Gerencial");
                logger.info("Tipo do mapper: " + mapper.getClass().getSimpleName());
            }

            // Verificar se é BaseDespesaCredor que tem estrutura aninhada
            if (mapper.getTabelaBanco().contains("base_despesa_credor")) {
                return processarRespostaBaseDespesaCredor(rootNode, mapper);
            }

            // Handle array responses (most SEFAZ endpoints return arrays)
            if (rootNode.isArray()) {
                logger.info("Processando array com " + rootNode.size() + " itens");
                for (JsonNode itemNode : rootNode) {
                    T newInstance = criarInstanciaGenerica(itemNode, mapper);
                    if (newInstance != null) {
                        resultList.add(newInstance);
                    }
                }
                logger.info("Array processado: " + resultList.size() + " instâncias criadas");
            } else {
                // Handle single object response
                logger.info("Processando objeto único");
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
     * Processa resposta específica para BaseDespesaCredor com estrutura aninhada ou array direto
     * Estrutura esperada: result > dados > colecao[] OU array direto []
     */
    private List<T> processarRespostaBaseDespesaCredor(JsonNode rootNode, T mapper) {
        List<T> resultList = new ArrayList<>();

        try {
            // Verificar se tem a estrutura result > dados > colecao (estrutura aninhada)
            JsonNode resultNode = rootNode.get("result");
            if (resultNode != null) {
                logger.info("Processando estrutura aninhada (result > dados > colecao)");
                JsonNode dadosNode = resultNode.get("dados");
                if (dadosNode != null) {
                    JsonNode colecaoNode = dadosNode.get("colecao");
                    if (colecaoNode != null && colecaoNode.isArray()) {
                        logger.info("Processando estrutura aninhada - colecao com " + colecaoNode.size() + " itens");

                        for (JsonNode itemNode : colecaoNode) {
                            T newInstance = criarInstanciaGenerica(itemNode, mapper);
                            if (newInstance != null) {
                                resultList.add(newInstance);
                            }
                        }
                    }

                    // Extrair informações de paginação se disponível
                    JsonNode nuFaixaPaginacao = dadosNode.get("nuFaixaPaginacao");
                    JsonNode qtTotalFaixasPaginacao = dadosNode.get("qtTotalFaixasPaginacao");

                    if (nuFaixaPaginacao != null && qtTotalFaixasPaginacao != null) {
                        logger.info("Informações de paginação - Faixa: " + nuFaixaPaginacao.asInt() + "/" + qtTotalFaixasPaginacao.asInt());

                        // Adicionar informações de paginação ao primeiro item se existir
                        if (!resultList.isEmpty()) {
                            T firstItem = resultList.get(0);
                            // Usar reflexão para definir os campos de paginação
                            try {
                                firstItem.getClass().getMethod("setNuFaixaPaginacao", Integer.class)
                                         .invoke(firstItem, nuFaixaPaginacao.asInt());
                                firstItem.getClass().getMethod("setQtTotalFaixasPaginacao", Integer.class)
                                         .invoke(firstItem, qtTotalFaixasPaginacao.asInt());
                            } catch (Exception e) {
                                logger.warning("Erro ao definir informações de paginação: " + e.getMessage());
                            }
                        }
                    }
                }

                // Extrair campos de retorno técnico para logging
                JsonNode msgUsuario = resultNode.get("msgUsuario");
                JsonNode msgTecnica = resultNode.get("msgTecnica");
                JsonNode cdRetorno = resultNode.get("cdRetorno");

                if (msgUsuario != null) {
                    logger.info("Mensagem da API: " + msgUsuario.asText());
                }
                if (cdRetorno != null) {
                    logger.info("Código de retorno: " + cdRetorno.asText());
                }
                if (msgTecnica != null && !msgTecnica.asText().isEmpty()) {
                    logger.info("Mensagem técnica: " + msgTecnica.asText());
                }
            } else {
                // Estrutura de array direto (formato real da API)
                logger.info("Processando estrutura de array direto para BaseDespesaCredor");

                if (rootNode.isArray()) {
                    logger.info("Array direto com " + rootNode.size() + " itens");
                    for (JsonNode itemNode : rootNode) {
                        T newInstance = criarInstanciaGenerica(itemNode, mapper);
                        if (newInstance != null) {
                            resultList.add(newInstance);
                        }
                    }
                } else {
                    // Single object response
                    T newInstance = criarInstanciaGenerica(rootNode, mapper);
                    if (newInstance != null) {
                        resultList.add(newInstance);
                    }
                }

                // Para array direto, não há informações de paginação na resposta
                // A paginação é controlada pelos parâmetros da requisição
                logger.info("Array direto processado - " + resultList.size() + " registros");
            }

        } catch (Exception e) {
            logger.severe("Erro ao processar resposta de BaseDespesaCredor: " + e.getMessage());
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

            // CORREÇÃO CRÍTICA: Copiar parâmetros da consulta para a nova instância
            if (mapper.getCamposParametros() != null && !mapper.getCamposParametros().isEmpty()) {
                newInstance.setCamposParametros(new LinkedHashMap<>(mapper.getCamposParametros()));
            }

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
                        // Log específico para ConsultaGerencial
                        if (dtoInstance.getClass().getSimpleName().equals("ConsultaGerencialDTO")) {
                            logger.fine("Mapeando campo ConsultaGerencial: " + fieldName + " = " + fieldValue.asText());
                        }
                        // Try to find and invoke the corresponding setter method
                        invocarSetterSeExistir(dtoInstance, dtoClass, fieldName, fieldValue);
                    } else {
                        // Log campos nulos para ConsultaGerencial
                        if (dtoInstance.getClass().getSimpleName().equals("ConsultaGerencialDTO")) {
                            logger.warning("Campo null ou vazio em ConsultaGerencial: " + fieldName);
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Erro ao mapear campo '" + fieldName + "': " + e.getMessage());
                }
            });

            // Após o mapeamento, definir campos derivados se o DTO suportar
            definirCamposDerivadosSeSuportado(dtoInstance);

        } catch (Exception e) {
            logger.severe("Erro no mapeamento genérico JSON para DTO: " + e.getMessage());
        }
    }

    /**
     * Define campos derivados se o DTO suportar (especificamente para DespesaDetalhadaDTO)
     */
    private void definirCamposDerivadosSeSuportado(T dtoInstance) {
        try {
            // Verificar se é DespesaDetalhadaDTO e chamar definirCamposDerivados
            if (dtoInstance.getClass().getSimpleName().equals("DespesaDetalhadaDTO")) {
                Method definirCamposDerivadosMethod = dtoInstance.getClass().getDeclaredMethod("definirCamposDerivados");
                definirCamposDerivadosMethod.setAccessible(true);
                definirCamposDerivadosMethod.invoke(dtoInstance);
            }
        } catch (Exception e) {
            logger.warning("Erro ao executar definirCamposDerivados: " + e.getMessage());
        }
    }


    /**
     * Uses reflection to find and invoke setter methods for JSON fields.
     * Supports multiple naming conventions and data types.
     * Also considers @JsonProperty annotations to map field names correctly.
     */
    private void invocarSetterSeExistir(T dtoInstance, Class<?> dtoClass, String fieldName, JsonNode fieldValue) {
        try {
            // First, try to find a field with @JsonProperty annotation that matches the fieldName
            String targetSetterName = findSetterNameByJsonProperty(dtoClass, fieldName);

            // Try different setter naming conventions
            String[] possibleSetterNames;
            if (targetSetterName != null) {
                // If we found a field with matching @JsonProperty, prioritize its setter
                possibleSetterNames = new String[]{
                    targetSetterName,
                    "set" + capitalize(fieldName),                    // setFieldName
                    "set" + capitalize(toCamelCase(fieldName)),       // setFieldName (from snake_case)
                    "set" + fieldName,                               // setfieldName
                    "set" + fieldName.toUpperCase()                  // setFIELDNAME
                };
            } else {
                possibleSetterNames = new String[]{
                    "set" + capitalize(fieldName),                    // setFieldName
                    "set" + capitalize(toCamelCase(fieldName)),       // setFieldName (from snake_case)
                    "set" + fieldName,                               // setfieldName
                    "set" + fieldName.toUpperCase()                  // setFIELDNAME
                };
            }

            for (String setterName : possibleSetterNames) {
                if (tryInvokeSetterWithValue(dtoInstance, dtoClass, setterName, fieldValue)) {
                    logger.fine("Mapeado campo '" + fieldName + "' usando setter '" + setterName + "'");
                    return; // Success, stop trying other names
                }
            }

            // Log mais detalhado para ConsultaGerencial quando setter não é encontrado
            if (dtoInstance.getClass().getSimpleName().equals("ConsultaGerencialDTO")) {
                logger.warning("Nenhum setter encontrado para campo ConsultaGerencial: " + fieldName + " (valor: " + fieldValue.asText() + ")");
            } else {
                logger.fine("Nenhum setter encontrado para campo: " + fieldName);
            }

        } catch (Exception e) {
            logger.warning("Erro ao invocar setter para campo '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * Finds the setter name for a field that has a @JsonProperty annotation matching the given fieldName.
     */
    private String findSetterNameByJsonProperty(Class<?> dtoClass, String fieldName) {
        try {
            Field[] fields = dtoClass.getDeclaredFields();
            for (Field field : fields) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && jsonProperty.value().equals(fieldName)) {
                    // Found a field with matching @JsonProperty annotation
                    String fieldNameInClass = field.getName();
                    return "set" + capitalize(fieldNameInClass);
                }
            }
        } catch (Exception e) {
            logger.fine("Erro ao buscar campo com @JsonProperty: " + e.getMessage());
        }
        return null;
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

            // Try int parameter (primitive)
            try {
                var method = dtoClass.getMethod(setterName, int.class);
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

            // Try BigDecimal parameter
            try {
                var method = dtoClass.getMethod(setterName, java.math.BigDecimal.class);
                if (fieldValue.isNumber()) {
                    method.invoke(dtoInstance, fieldValue.decimalValue());
                } else {
                    method.invoke(dtoInstance, new java.math.BigDecimal(fieldValue.asText()));
                }
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try LocalDate parameter
            try {
                var method = dtoClass.getMethod(setterName, java.time.LocalDate.class);
                method.invoke(dtoInstance, java.time.LocalDate.parse(fieldValue.asText()));
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try LocalDateTime parameter
            try {
                var method = dtoClass.getMethod(setterName, java.time.LocalDateTime.class);
                method.invoke(dtoInstance, java.time.LocalDateTime.parse(fieldValue.asText()));
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
        boolean isUnidadeGestora = mapper.getTabelaBanco().contains("unidade_gestora");

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            apiUrl = builder.toUriString();

            if (isUnidadeGestora) {
                logger.info("URL da API UG: " + apiUrl);
                logger.info("Fazendo requisição para buscar todas as UGs ativas...");
            }

        } catch (Exception e) {
            logger.severe("Erro ao montar URL independente de data e ug: " + e.getMessage());
        }
        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);
            if (response != null) {
                if (isUnidadeGestora) {
                    logger.info("Resposta da API UG recebida. Status: " + response.getStatusCode());
                } else {
                    logger.info("Resposta da API recebida. Status: " + response.getStatusCode() + " | URL: " + apiUrl);
                }

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    if (isUnidadeGestora) {
                        logger.info("Processando JSON das Unidades Gestoras...");
                    } else {
                        logger.info("Processando JSON da resposta...");
                    }

                    List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                    if (processedData != null) {
                        resultadoAnoMesVigente.addAll(processedData);

                        if (isUnidadeGestora) {
                            logger.info("UGs processadas com sucesso: " + processedData.size() + " unidades gestoras");
                            userFriendlyLogger.logInfo("Processamento concluído: " + processedData.size() + " UGs");
                        } else {
                            logger.info("Dados processados com sucesso: " + processedData.size() + " registros");
                        }
                    } else {
                        if (isUnidadeGestora) {
                            logger.warning("Processamento de UGs retornou null");
                        } else {
                            logger.warning("processarRespostaSefaz retornou null");
                        }
                    }
                } else {
                    String errorMsg = "API retornou erro. Status: " + response.getStatusCode();
                    if (isUnidadeGestora) {
                        errorMsg = "API de UG retornou erro. Status: " + response.getStatusCode();
                    }
                    logger.warning(errorMsg + " | Body: " + (response.getBody() != null ? response.getBody().substring(0, Math.min(200, response.getBody().length())) : "null"));
                }
            } else {
                if (isUnidadeGestora) {
                    logger.warning("Resposta da API UG é null");
                } else {
                    logger.warning("Resposta da API é null");
                }
            }

        } catch (RestClientException e) {
            if (isUnidadeGestora) {
                logger.severe("Erro ao consumir API de UG: " + e.getMessage());
            } else {
                logger.severe("Erro ao consumir API vigente: " + e.getMessage());
            }
        }
        return resultadoAnoMesVigente;
    }

    private List<T> pegarDadosMesAnoVigente(String ugCd, T mapper){
        List<T> resultadoAnoMesVigente = new ArrayList<>();

        Short anoAtual = utilsService.getAnoAtual();
        Short mesAtual = utilsService.getMesAtual();

        logger.info("Buscando dados do ano atual (" + anoAtual + ") para UG: " + ugCd);

        // Verificar se o DTO requer iteração sobre cdGestao
        if (mapper.requerIteracaoCdGestao()) {
            logger.info("DTO requer iteração sobre cdGestao. Buscando todos os códigos de gestão...");
            List<String> cdGestaoList = utilsService.cdGestao();

            if (cdGestaoList != null && !cdGestaoList.isEmpty()) {
                logger.info("Encontrados " + cdGestaoList.size() + " códigos de gestão para iterar");

                for (String cdGestao : cdGestaoList) {
                    logger.info("Processando cdGestao: " + cdGestao + " para UG: " + ugCd);
                    List<T> resultadoCdGestao = processarComCdGestao(ugCd, mapper, cdGestao, true);
                    if (resultadoCdGestao != null) {
                        resultadoAnoMesVigente.addAll(resultadoCdGestao);
                        logger.info("cdGestao " + cdGestao + ": " + resultadoCdGestao.size() + " registros processados");
                    }
                }
            } else {
                logger.warning("Nenhum código de gestão encontrado na tabela consumer_sefaz.empenho");
            }
        } else {
            // Processamento normal sem iteração de cdGestao
            List<T> resultado = processarSemCdGestao(ugCd, mapper, true);
            if (resultado != null) {
                resultadoAnoMesVigente.addAll(resultado);
            }
        }

        return resultadoAnoMesVigente;
    }

    private List<T> pegarDeTodosAnos(String ugCd, T mapper){
        List<T> resultadoTodosAnos = new ArrayList<>();
        Short anoAtual = utilsService.getAnoAtual();

        for (Short dtAno = anoAtual; dtAno >= anoAtual-5;  dtAno--) {
            logger.info("Processando ano: " + dtAno + " para UG: " + ugCd);

            // Verificar se o DTO requer iteração sobre cdGestao
            if (mapper.requerIteracaoCdGestao()) {
                logger.info("DTO requer iteração sobre cdGestao para ano " + dtAno);
                List<String> cdGestaoList = utilsService.cdGestao();

                if (cdGestaoList != null && !cdGestaoList.isEmpty()) {
                    for (String cdGestao : cdGestaoList) {
                        logger.info("Processando cdGestao: " + cdGestao + " para UG: " + ugCd + " e ano: " + dtAno);
                        List<T> resultadoCdGestao = processarComCdGestaoTodosAnos(ugCd, mapper, cdGestao, dtAno);
                        if (resultadoCdGestao != null) {
                            resultadoTodosAnos.addAll(resultadoCdGestao);
                            logger.info("Ano " + dtAno + " cdGestao " + cdGestao + ": " + resultadoCdGestao.size() + " registros");
                        }
                    }
                } else {
                    logger.warning("Nenhum código de gestão encontrado para ano " + dtAno);
                }
            } else {
                // Processamento normal sem iteração de cdGestao
                List<T> resultadoAno = processarSemCdGestaoTodosAnos(ugCd, mapper, dtAno);
                if (resultadoAno != null) {
                    resultadoTodosAnos.addAll(resultadoAno);
                }
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

    /**
     * Determina o tipo de dados baseado no mapper
     */
    private String getDataTypeFromMapper(T mapper) {
        String tableName = mapper.getTabelaBanco();
        if (tableName.contains("unidade_gestora")) {
            return "unidades gestoras";
        } else if (tableName.contains("pagamento")) {
            return "pagamentos";
        } else if (tableName.contains("ordem_fornecimento")) {
            return "ordens de fornecimento";
        } else if (tableName.contains("receita")) {
            return "receitas";
        } else if (tableName.contains("contratos_fiscais")) {
            return "contratos fiscais";
        } else if (tableName.contains("base_despesa_credor")) {
            return "base despesa credor";
        } else {
            return "dados";
        }
    }

    /**
     * Processa requisição com cdGestao específico para dados atuais
     */
    private List<T> processarComCdGestao(String ugCd, T mapper, String cdGestao, boolean isAnoAtual) {
        List<T> resultado = new ArrayList<>();
        String apiUrl = null;

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            Map<String, Object> parametros = mapper.getCamposParametrosAtual(ugCd, utilsService);

            // Adicionar cdGestao aos parâmetros
            parametros.put("cdGestao", cdGestao);

            logger.info("Parâmetros da consulta com cdGestao " + cdGestao + ": " + parametros);

            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            apiUrl = builder.toUriString();

            logger.info("URL da API com cdGestao: " + apiUrl);

        } catch (Exception e) {
            logger.severe("Erro ao montar URL com cdGestao " + cdGestao + ": " + e.getMessage());
            return resultado;
        }

        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                if (processedData != null) {
                    resultado.addAll(processedData);
                    logger.info("Dados processados com sucesso para cdGestao " + cdGestao + ": " + processedData.size() + " registros");
                } else {
                    logger.warning("Processamento retornou null para cdGestao " + cdGestao);
                }
            } else {
                logger.warning("API retornou erro para cdGestao " + cdGestao + ". Status: " +
                             (response != null ? response.getStatusCode() : "null"));
            }

        } catch (RestClientException e) {
            logger.severe("Erro ao consumir API para cdGestao " + cdGestao + ": " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Processa requisição com cdGestao específico para todos os anos
     */
    private List<T> processarComCdGestaoTodosAnos(String ugCd, T mapper, String cdGestao, Short ano) {
        List<T> resultado = new ArrayList<>();
        String apiUrl = null;

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            Map<String, Object> parametros = mapper.getCamposParametrosTodosOsAnos(ugCd, ano);

            // Adicionar cdGestao aos parâmetros
            parametros.put("cdGestao", cdGestao);

            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            apiUrl = builder.toUriString();

        } catch (Exception e) {
            logger.severe("Erro ao montar URL com cdGestao " + cdGestao + " para ano " + ano + ": " + e.getMessage());
            return resultado;
        }

        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                if (processedData != null) {
                    resultado.addAll(processedData);
                }
            }
        } catch (RestClientException e) {
            logger.severe("Erro ao consumir API para cdGestao " + cdGestao + " e ano " + ano + ": " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Processa requisição sem cdGestao para dados atuais (DTOs que não precisam de iteração)
     */
    private List<T> processarSemCdGestao(String ugCd, T mapper, boolean isAnoAtual) {
        List<T> resultado = new ArrayList<>();
        String apiUrl = null;

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            Map<String, Object> parametros = mapper.getCamposParametrosAtual(ugCd, utilsService);

            logger.info("Parâmetros da consulta: " + parametros);

            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            apiUrl = builder.toUriString();

            logger.info("URL da API: " + apiUrl);

        } catch (Exception e) {
            logger.severe("Erro ao montar URL para ano vigente: " + e.getMessage());
            return resultado;
        }

        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<T> processedData = processarRespostaSefaz(response.getBody(), mapper);
                if (processedData != null) {
                    resultado.addAll(processedData);
                    logger.info("Dados processados com sucesso: " + processedData.size() + " registros para UG " + ugCd);
                } else {
                    logger.warning("Processamento retornou null para UG " + ugCd);
                }
            } else {
                logger.warning("API retornou erro para UG " + ugCd + ". Status: " +
                             (response != null ? response.getStatusCode() : "null"));
            }

        } catch (RestClientException e) {
            logger.severe("Erro ao consumir API para UG " + ugCd + ": " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Processa requisição sem cdGestao para todos os anos (DTOs que não precisam de iteração)
     */
    private List<T> processarSemCdGestaoTodosAnos(String ugCd, T mapper, Short ano) {
        List<T> resultado = new ArrayList<>();
        String apiUrl = null;

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
            for (Map.Entry<String, Object> entry : mapper.getCamposParametrosTodosOsAnos(ugCd, ano).entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            apiUrl = builder.toUriString();

        } catch (Exception e) {
            logger.severe("Erro ao montar URL antiga: " + e.getMessage());
            return resultado;
        }

        try {
            ResponseEntity<String> response = respostaApiRaw(apiUrl);

            if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<T> anoResult = processarRespostaSefaz(response.getBody(), mapper);
                if (anoResult != null) {
                    resultado.addAll(anoResult);
                }
            }

        } catch (RestClientException e) {
            logger.severe("Erro ao consumir API antiga: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Aplica deduplicação se necessário baseado no tipo de DTO
     * Para TermoDTO, remove duplicatas baseadas em cdConvenio
     */
    private List<T> deduplicateIfNeeded(List<T> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return originalList;
        }

        // Verificar se é TermoDTO que precisa de deduplicação
        T firstItem = originalList.get(0);
        if (firstItem.getTabelaBanco().contains("termo")) {
            return deduplicateTermoList(originalList);
        }

        // Verificar se é DespesaDetalhadaDTO que precisa de deduplicação
        if (firstItem.getTabelaBanco().contains("despesa_detalhada")) {
            return deduplicateDespesaDetalhadaList(originalList);
        }

        // Para outros DTOs, retornar lista original (sem deduplicação)
        return originalList;
    }

    /**
     * Remove duplicatas de TermoDTO baseadas em cdConvenio
     */
    @SuppressWarnings("unchecked")
    private List<T> deduplicateTermoList(List<T> originalList) {
        logger.info("Aplicando deduplicação para Termo baseada em cdConvenio...");

        Map<Long, T> uniqueTermos = new LinkedHashMap<>(); // Preserva ordem de inserção
        int duplicatesRemoved = 0;

        for (T item : originalList) {
            try {
                // Usar reflexão para obter cdConvenio
                Long cdConvenio = (Long) item.getClass().getMethod("getCdConvenio").invoke(item);

                if (cdConvenio != null) {
                    if (uniqueTermos.containsKey(cdConvenio)) {
                        duplicatesRemoved++;
                        logger.fine("Removendo duplicata: cdConvenio=" + cdConvenio);
                    } else {
                        uniqueTermos.put(cdConvenio, item);
                    }
                } else {
                    // Se cdConvenio for null, adicionar mesmo assim (não deveria acontecer)
                    logger.warning("Termo com cdConvenio null encontrado - mantendo registro");
                    uniqueTermos.put(System.currentTimeMillis(), item); // Usar timestamp como chave temporária
                }
            } catch (Exception e) {
                logger.warning("Erro ao obter cdConvenio para deduplicação: " + e.getMessage());
                // Em caso de erro, manter o registro
                uniqueTermos.put(System.currentTimeMillis(), item);
            }
        }

        List<T> deduplicatedList = new ArrayList<>(uniqueTermos.values());

        logger.info("Deduplicação concluída:");
        logger.info("  • Registros originais: " + originalList.size());
        logger.info("  • Registros únicos: " + deduplicatedList.size());
        logger.info("  • Duplicatas removidas: " + duplicatesRemoved);

        return deduplicatedList;
    }

    /**
     * Remove duplicatas de DespesaDetalhadaDTO baseadas na chave composta da constraint única
     * Chave: cd_unidade_gestora + dt_ano_exercicio_ctb + nu_mes + cd_orgao + cd_unid_orc + cd_natureza_despesa + cd_ppa_acao + cd_sub_acao
     */
    @SuppressWarnings("unchecked")
    private List<T> deduplicateDespesaDetalhadaList(List<T> originalList) {
        logger.info("Aplicando deduplicação para DespesaDetalhada baseada na chave composta da constraint única...");

        Map<String, T> uniqueDespesas = new LinkedHashMap<>(); // Preserva ordem de inserção
        int duplicatesRemoved = 0;

        for (T item : originalList) {
            try {
                // Usar reflexão para obter os campos da chave composta
                String cdUnidadeGestora = (String) item.getClass().getMethod("getCdUnidadeGestora").invoke(item);
                Integer dtAnoExercicioCTB = (Integer) item.getClass().getMethod("getDtAnoExercicioCTB").invoke(item);
                Integer nuMes = (Integer) item.getClass().getMethod("getNuMes").invoke(item);
                String cdOrgao = (String) item.getClass().getMethod("getCdOrgao").invoke(item);
                String cdUnidOrc = (String) item.getClass().getMethod("getCdUnidOrc").invoke(item);
                String cdNaturezaDespesa = (String) item.getClass().getMethod("getCdNaturezaDespesa").invoke(item);
                String cdPPAAcao = (String) item.getClass().getMethod("getCdPPAAcao").invoke(item);
                String cdSubAcao = (String) item.getClass().getMethod("getCdSubAcao").invoke(item);

                // Criar chave composta concatenando todos os campos
                String chaveComposta = String.format("%s|%s|%s|%s|%s|%s|%s|%s",
                    cdUnidadeGestora != null ? cdUnidadeGestora : "NULL",
                    dtAnoExercicioCTB != null ? dtAnoExercicioCTB.toString() : "NULL",
                    nuMes != null ? nuMes.toString() : "NULL",
                    cdOrgao != null ? cdOrgao : "NULL",
                    cdUnidOrc != null ? cdUnidOrc : "NULL",
                    cdNaturezaDespesa != null ? cdNaturezaDespesa : "NULL",
                    cdPPAAcao != null ? cdPPAAcao : "NULL",
                    cdSubAcao != null ? cdSubAcao : "NULL"
                );

                if (uniqueDespesas.containsKey(chaveComposta)) {
                    duplicatesRemoved++;
                    logger.fine("Removendo duplicata DespesaDetalhada: " + chaveComposta);
                } else {
                    uniqueDespesas.put(chaveComposta, item);
                }

            } catch (Exception e) {
                logger.warning("Erro ao obter campos para deduplicação de DespesaDetalhada: " + e.getMessage());
                // Em caso de erro, manter o registro usando timestamp como chave temporária
                uniqueDespesas.put("ERROR_" + System.currentTimeMillis(), item);
            }
        }

        List<T> deduplicatedList = new ArrayList<>(uniqueDespesas.values());

        logger.info("Deduplicação DespesaDetalhada concluída:");
        logger.info("  • Registros originais: " + originalList.size());
        logger.info("  • Registros únicos: " + deduplicatedList.size());
        logger.info("  • Duplicatas removidas: " + duplicatesRemoved);

        return deduplicatedList;
    }

}