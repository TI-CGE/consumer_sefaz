package br.gov.se.setc.consumer.controller;
import br.gov.se.setc.consumer.dto.DespesaConvenioDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.logging.Logger;
@RestController
@RequestMapping("/convenio/despesa")
@Tag(name = "Despesa Convênio", description = "API para consumo e gestão de dados de despesas de convênios do SEFAZ")
public class SwaggerDespesaConvenioController {
    private static final Logger logger = Logger.getLogger(SwaggerDespesaConvenioController.class.getName());
    @Autowired
    @Qualifier("despesaConvenioConsumoApiService")
    private ConsumoApiService<DespesaConvenioDTO> consumoApiService;
    @GetMapping
    @Operation(
        summary = "Consumir dados de Despesa de Convênios",
        description = "Consome dados da API de Despesa de Convênios da SEFAZ e persiste no banco de dados local.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Dados consumidos e persistidos com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespesaConvenioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    public List<DespesaConvenioDTO> consumirDespesaConvenio(
            @Parameter(description = "Código da Unidade Gestora para filtro", example = "161011")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Código da Gestão para filtro", example = "00001")
            @RequestParam(required = false) String cdGestao,
            @Parameter(description = "Ano do lançamento para filtro", example = "2025")
            @RequestParam(required = false) Integer nuAnoLancamento,
            @Parameter(description = "Mês do lançamento para filtro", example = "8")
            @RequestParam(required = false) Integer nuMesLancamento
    ) {
        try {
            logger.info("Iniciando consumo da API de Despesa de Convênios");
            DespesaConvenioDTO dto = new DespesaConvenioDTO();
            if (cdUnidadeGestora != null) dto.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
            if (cdGestao != null) dto.setCdGestaoFiltro(cdGestao);
            if (nuAnoLancamento != null) dto.setNuAnoLancamentoFiltro(nuAnoLancamento);
            if (nuMesLancamento != null) dto.setNuMesLancamentoFiltro(nuMesLancamento);
            return consumoApiService.consumirPersistir(dto);
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Despesa de Convênios: " + e.getMessage());
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(summary = "Teste básico do endpoint", description = "Retorna informações básicas para teste do endpoint de Despesa de Convênios.")
    public ResponseEntity<String> testeEndpoint() {
        try {
            DespesaConvenioDTO dto = new DespesaConvenioDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
