package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.EmpenhoMensalDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para consumo de empenhos filtrados por mês específico
 */
@RestController
@RequestMapping("/empenho/mensal")
@Tag(name = "Empenhos Mensais", description = "API para consumo de empenhos filtrados por mês específico")
public class SwaggerEmpenhoMensalController {
    
    private static final Logger logger = Logger.getLogger(SwaggerEmpenhoMensalController.class.getName());
    
    private final ConsumoApiService<EmpenhoMensalDTO> consumoApiService;
    
    public SwaggerEmpenhoMensalController(
            @Qualifier("empenhoMensalConsumoApiService") ConsumoApiService<EmpenhoMensalDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    /**
     * DTO para receber os parâmetros do POST
     */
    public static class EmpenhoMensalRequest {
        private Integer mes;
        private Integer ano;
        
        public EmpenhoMensalRequest() {}
        
        public EmpenhoMensalRequest(Integer mes, Integer ano) {
            this.mes = mes;
            this.ano = ano;
        }
        
        public Integer getMes() {
            return mes;
        }
        
        public void setMes(Integer mes) {
            this.mes = mes;
        }
        
        public Integer getAno() {
            return ano;
        }
        
        public void setAno(Integer ano) {
            this.ano = ano;
        }
    }
    
    @PostMapping("/execute")
    @Operation(
        summary = "Consumir empenhos de um mês específico",
        description = "Consome dados de empenhos da API de transparência SEFAZ filtrados por mês e ano específicos. " +
                     "Recebe via POST o mês e ano desejados e retorna uma lista com todos os empenhos processados " +
                     "para o período especificado, incluindo informações sobre valores empenhados, credores, " +
                     "natureza da despesa e datas de empenho.",
        tags = {"Empenhos Mensais"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empenhos do mês processados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos (mês deve estar entre 1-12)"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a requisição"),
        @ApiResponse(responseCode = "503", description = "Serviço temporariamente indisponível")
    })
    public ResponseEntity<List<EmpenhoMensalDTO>> consumirEmpenhoMensal(@RequestBody EmpenhoMensalRequest request) {
        try {
            // Validação dos parâmetros
            if (request.getMes() == null || request.getMes() < 1 || request.getMes() > 12) {
                logger.warning("Mês inválido recebido: " + request.getMes());
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getAno() == null || request.getAno() < 2000 || request.getAno() > 2030) {
                logger.warning("Ano inválido recebido: " + request.getAno());
                return ResponseEntity.badRequest().build();
            }
            
            logger.info("Iniciando consumo da API de Empenho para mês: " + request.getMes() + "/" + request.getAno());
            
            // Criar o DTO com os filtros específicos
            EmpenhoMensalDTO mapper = new EmpenhoMensalDTO(request.getMes(), request.getAno());
            
            // Executar o consumo
            List<EmpenhoMensalDTO> result = consumoApiService.consumirPersistir(mapper);
            
            logger.info("Consumo concluído para " + request.getMes() + "/" + request.getAno() + 
                       ". Retornando " + (result != null ? result.size() : 0) + " registros");
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warning("Erro de validação: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Empenho mensal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/execute-por-periodo")
    @Operation(
        summary = "Consumir empenhos por mês e ano (query params)",
        description = "Consome dados de empenhos da API de transparência SEFAZ para o mês e ano informados via query params. " +
                     "Utiliza a mesma regra de iteração (UG, cd_gestao, sq_empenho) e monta os query params cdUnidadeGestora, dtAnoExercicioCTB e nuMes.",
        tags = {"Empenhos Mensais"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empenhos do período processados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos (mês 1-12, ano 2000-2030)"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a requisição"),
        @ApiResponse(responseCode = "503", description = "Serviço temporariamente indisponível")
    })
    public ResponseEntity<List<EmpenhoMensalDTO>> consumirEmpenhoMensalPorPeriodo(
            @Parameter(description = "Mês (1-12)", required = true, example = "6")
            @RequestParam(name = "mes") Integer mes,
            @Parameter(description = "Ano (2000-2030)", required = true, example = "2024")
            @RequestParam(name = "ano") Integer ano) {
        try {
            if (mes == null || mes < 1 || mes > 12) {
                logger.warning("Mês inválido recebido: " + mes);
                return ResponseEntity.badRequest().build();
            }
            if (ano == null || ano < 2000 || ano > 2030) {
                logger.warning("Ano inválido recebido: " + ano);
                return ResponseEntity.badRequest().build();
            }
            logger.info("Iniciando consumo da API de Empenho para mês: " + mes + "/" + ano);
            EmpenhoMensalDTO mapper = new EmpenhoMensalDTO(mes, ano);
            List<EmpenhoMensalDTO> result = consumoApiService.consumirPersistir(mapper);
            logger.info("Consumo concluído para " + mes + "/" + ano +
                       ". Retornando " + (result != null ? result.size() : 0) + " registros");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warning("Erro de validação: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Empenho mensal por período: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
