package br.gov.se.setc.consumer.controller;
import br.gov.se.setc.consumer.dto.BaseDespesaLicitacaoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;
@RestController
@RequestMapping("/base-despesa-licitacao")
@Tag(name = "Base Despesa Licitação", description = "API para consumo e gestão de dados de base despesa licitação do SEFAZ")
public class SwaggerBaseDespesaLicitacaoController {
    private static final Logger logger = Logger.getLogger(SwaggerBaseDespesaLicitacaoController.class.getName());
    @Autowired
    @Qualifier("baseDespesaLicitacaoConsumoApiService")
    private ConsumoApiService<BaseDespesaLicitacaoDTO> consumoApiService;
    @GetMapping
    @Operation(
        summary = "Consumir dados de Base Despesa Licitação", 
        description = "Consome dados da API de Base Despesa Licitação do SEFAZ e persiste no banco de dados local. " +
                     "Retorna array de objetos com informações sobre despesas por licitação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados consumidos e persistidos com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<BaseDespesaLicitacaoDTO> consumirBaseDespesaLicitacao(
            @Parameter(description = "Código da Unidade Gestora para filtro", example = "161011")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Ano de exercício para filtro", example = "2024")
            @RequestParam(required = false) Integer dtAnoExercicio) {
        try {
            logger.info("Iniciando consumo de Base Despesa Licitação");
            BaseDespesaLicitacaoDTO consumirPersistir = new BaseDespesaLicitacaoDTO();
            if (cdUnidadeGestora != null) {
                consumirPersistir.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
                logger.info("Filtro aplicado - Código Unidade Gestora: " + cdUnidadeGestora);
            }
            if (dtAnoExercicio != null) {
                consumirPersistir.setDtAnoExercicioFiltro(dtAnoExercicio);
                logger.info("Filtro aplicado - Ano Exercício: " + dtAnoExercicio);
            }
            List<BaseDespesaLicitacaoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Base Despesa Licitação: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint", 
        description = "Retorna informações básicas para teste do endpoint de Base Despesa Licitação. " +
                     "Útil para verificar se a configuração está correta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de teste retornadas com sucesso")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Base Despesa Licitação");
            BaseDespesaLicitacaoDTO dto = new BaseDespesaLicitacaoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Filtros suportados: cdUnidadeGestora, dtAnoExercicio\n");
            info.append("Exemplo de uso: /base-despesa-licitacao?cdUnidadeGestora=161011&dtAnoExercicio=2024\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Base Despesa Licitação: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
