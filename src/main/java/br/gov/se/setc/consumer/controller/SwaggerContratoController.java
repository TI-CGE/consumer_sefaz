package br.gov.se.setc.consumer.controller;

import br.gov.se.setc.consumer.dto.ContratoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/contrato")
@Tag(name = "contrato", description = "Endpoints para documentar contratos")
public class SwaggerContratoController {

    private static final Logger logger = Logger.getLogger(SwaggerContratoController.class.getName());
    private final ConsumoApiService<ContratoDTO> consumoApiService;

    public SwaggerContratoController(
            @Qualifier("contratoConsumoApiService") ConsumoApiService<ContratoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(
        summary = "Lista todos os contratos", 
        description = "Retorna uma lista com todos os contratos disponíveis. " +
                     "Este endpoint consome dados da API de transparência SEFAZ e persiste no banco local."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contratos retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<ContratoDTO> listarContrato(
            @Parameter(description = "Código da Unidade Gestora para filtrar os resultados", example = "161011")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Ano do exercício para filtrar os resultados", example = "2022")
            @RequestParam(required = false) Integer dtAnoExercicio
    ) {
        try {
            logger.info("Iniciando consumo da API de Contratos");
            
            ContratoDTO consumirPersistir = new ContratoDTO();
            
            // Aplicar filtros se fornecidos
            if (cdUnidadeGestora != null) {
                consumirPersistir.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
                logger.info("Filtro aplicado - Código Unidade Gestora: " + cdUnidadeGestora);
            }
            
            if (dtAnoExercicio != null) {
                consumirPersistir.setDtAnoExercicioFiltro(dtAnoExercicio);
                logger.info("Filtro aplicado - Ano Exercício: " + dtAnoExercicio);
            }
            
            List<ContratoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Contratos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint", 
        description = "Retorna informações básicas para teste do endpoint de contratos. " +
                     "Útil para verificar se a configuração está correta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de teste retornadas com sucesso")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Contratos");
            ContratoDTO dto = new ContratoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Filtros suportados: cdUnidadeGestora, dtAnoExercicio\n");
            info.append("Exemplo de uso: /contrato?cdUnidadeGestora=161011&dtAnoExercicio=2022\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Contratos: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(
        summary = "Informações detalhadas do endpoint",
        description = "Retorna informações detalhadas sobre o endpoint de contratos, " +
                     "incluindo estrutura de dados e exemplos de uso."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações detalhadas retornadas com sucesso")
    })
    public ResponseEntity<String> informacoesDetalhadas() {
        try {
            logger.info("Solicitação de informações detalhadas de Contratos");
            ContratoDTO dto = new ContratoDTO();
            StringBuilder info = new StringBuilder();
            
            info.append("=== CONTRATOS - INFORMAÇÕES DETALHADAS ===\n\n");
            info.append("Descrição: Endpoint para consumo de dados de contratos da SEFAZ\n");
            info.append("URL da API: ").append(dto.getUrl()).append("\n");
            info.append("Tabela de destino: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Schema: consumer_sefaz\n\n");
            
            info.append("=== PARÂMETROS DE CONSULTA ===\n");
            info.append("- cdUnidadeGestora: Código da Unidade Gestora (obrigatório)\n");
            info.append("- dtAnoExercicio: Ano do exercício (obrigatório)\n\n");
            
            info.append("=== CAMPOS RETORNADOS ===\n");
            info.append("- sgUnidadeGestora: Sigla da Unidade Gestora\n");
            info.append("- cdUnidadeGestora: Código da Unidade Gestora\n");
            info.append("- dtAnoExercicio: Ano do exercício\n");
            info.append("- cdContrato: Código do contrato\n");
            info.append("- cdAditivo: Código do aditivo\n");
            info.append("- dtInicioVigencia: Data de início da vigência\n");
            info.append("- dtFimVigencia: Data de fim da vigência\n");
            info.append("- nmCategoria: Nome da categoria\n");
            info.append("- nmFornecedor: Nome do fornecedor\n");
            info.append("- nuDocumento: Número do documento\n");
            info.append("- dsObjetoContrato: Descrição do objeto do contrato\n");
            info.append("- vlContrato: Valor do contrato\n");
            info.append("- tpContrato: Tipo do contrato\n\n");
            
            info.append("=== EXEMPLOS DE USO ===\n");
            info.append("GET /contrato?cdUnidadeGestora=161011&dtAnoExercicio=2022\n");
            info.append("GET /contrato/test\n");
            info.append("GET /contrato/info\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro ao obter informações detalhadas de Contratos: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
