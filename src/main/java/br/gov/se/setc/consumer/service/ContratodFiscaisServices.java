package br.gov.se.setc.consumer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.respository.EndpontSefazRepository;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.tokenSefaz.service.AcessoTokenService;
import br.gov.se.setc.util.ValidacaoUtil;

public class ContratodFiscaisServices {
     private static final Logger logger = Logger.getLogger(ConsumoApiService.class.getName());
    private final RestTemplate restTemplate;
    private final AcessoTokenService acessoTokenService;
    private ValidacaoUtil<ContratosFiscaisDTO> utilsService;
    private EndpontSefazRepository<ContratosFiscaisDTO > contratosFiscaisDAO;
    private List<String> ugCdArray;

    public ContratodFiscaisServices(RestTemplate restTemplate,
    AcessoTokenService acessoTokenService,
    JdbcTemplate jdbcTemplate, ValidacaoUtil<ContratosFiscaisDTO> utilsService,
    UnifiedLogger unifiedLogger
    ) {
        this.restTemplate = restTemplate;
        this.acessoTokenService = acessoTokenService;
        this.utilsService = utilsService;
        this.ugCdArray = utilsService.getUgs();
        contratosFiscaisDAO = new EndpontSefazRepository<ContratosFiscaisDTO>(jdbcTemplate, unifiedLogger);

    }


     public List<ContratosFiscaisDTO> consumirPersistir(ContratosFiscaisDTO mapper) {
        List<ContratosFiscaisDTO> resultList = new ArrayList<>();

        for (String ugCd : ugCdArray) {
            if (utilsService.isPresenteBanco(mapper)) {
                List<ContratosFiscaisDTO> result = pegarDadosMesAnoVigente(ugCd,mapper);
                if(result != null ){
                    resultList.addAll(result);
                }
                 return resultList;
                
            } else {
                 List<ContratosFiscaisDTO> result = pegarDeTodosAnos(ugCd,mapper);
                if(result != null ){
                 resultList.addAll(result);
                }
            }
        }
        if(resultList == null || resultList.isEmpty()) {
            throw new RuntimeException("Nenhum parametro encontrado no result list");
        }
        contratosFiscaisDAO.persistContratosFiscais(resultList);
        return resultList;
    }


    private List<ContratosFiscaisDTO> pegarDadosMesAnoVigente(String ugCd, ContratosFiscaisDTO mapper){
        List<ContratosFiscaisDTO> resultadoAnoMesVigente = new ArrayList<>();
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
                    ResponseEntity<List<ContratosFiscaisDTO>> response = respostaApiRaw(apiUrl);
                    if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        //resultadoAnoMesVigente = processarRespostaSefaz(response.getBody(), mapper);
                        return resultadoAnoMesVigente;
                    }

                } catch (RestClientException e) {
                    logger.severe("Erro ao consumir API vigente: " + e.getMessage());
                }
                return null;
    }

     private ResponseEntity<List<ContratosFiscaisDTO>> respostaApiRaw(String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + acessoTokenService.getToken());
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if (apiUrl != null) {
            try {
                ResponseEntity<List<ContratosFiscaisDTO>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                      new ParameterizedTypeReference<List<ContratosFiscaisDTO>>() {
                            }
                );
                return response;
            } catch (Exception e) {
                logger.severe("Erro na chamada da API SEFAZ: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    private List<ContratosFiscaisDTO> pegarDeTodosAnos(String ugCd, ContratosFiscaisDTO mapper){
        List<ContratosFiscaisDTO> resultadoTodosAnos = new ArrayList<>();
        Short anoAtual = utilsService.getAnoAtual();

        for (Short dtAno = anoAtual; dtAno >= anoAtual-5;  dtAno--) {
                    String apiUrl = null;
                    try {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapper.getUrl());
                       if(mapper.getCamposParametrosTodosOsAnos(ugCd, dtAno) != null){
                            for (Map.Entry<String, Object> entry : mapper.getCamposParametrosTodosOsAnos(ugCd, dtAno).entrySet()) {
                                    builder.queryParam(entry.getKey(), entry.getValue());
                            }
                        }
                        apiUrl = builder.toUriString();

                    } catch (Exception e) {
                         logger.severe("Erro ao montar URL antiga: " + e.getMessage());
                    }
                  try {
                        ResponseEntity<List<ContratosFiscaisDTO>> response = respostaApiRaw(apiUrl);

                        if (response != null &&  response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                            // List<ContratosFiscaisDTO> anoResult = processarRespostaSefaz(response.getBody(), mapper);
                            // if (anoResult != null) {
                                 resultadoTodosAnos.addAll(response.getBody());
                             //}
                        }

                  } catch (RestClientException e) {
                         logger.severe("Erro ao consumir API antiga: " + e.getMessage());
                }

            }
            if(resultadoTodosAnos == null || resultadoTodosAnos.size() == 0){
               return null;
            }
            return resultadoTodosAnos;
    }

     



}
