package br.gov.se.setc.consumer.controller;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.consumer.dto.TotalizadoresExecucaoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * Controller REST para endpoints de Totalizadores de Execução
 * Endpoint base: /totalizadores-execucao
 */
@RestController
@RequestMapping("/totalizadores-execucao")
@Tag(name = "Totalizadores Execução", description = "API para consumo e gestão de dados de totalizadores de execução do SEFAZ")
public class SwaggerTotalizadoresExecucaoController {
    private static final Logger logger = Logger.getLogger(SwaggerTotalizadoresExecucaoController.class.getName());
    private final ConsumoApiService<TotalizadoresExecucaoDTO> consumoApiService;
    public SwaggerTotalizadoresExecucaoController(
            @Qualifier("totalizadoresExecucaoConsumoApiService") ConsumoApiService<TotalizadoresExecucaoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    @GetMapping
    @Operation(
        summary = "Lista todos os totalizadores de execução",
        description = "Retorna uma lista com todos os totalizadores de execução disponíveis. Suporta filtros opcionais por unidade gestora e ano do exercício."
    )
    public List<TotalizadoresExecucaoDTO> listarTotalizadoresExecucao(
            @Parameter(description = "Código da Unidade Gestora para filtro", example = "123456")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Ano do Exercício Contábil para filtro", example = "2025")
            @RequestParam(required = false) Integer dtAnoExercicioCTB
    ) {
        try {
            logger.info("Iniciando consumo da API de Totalizadores de Execução");
            TotalizadoresExecucaoDTO consumirPersistir = new TotalizadoresExecucaoDTO();
            if (cdUnidadeGestora != null && !cdUnidadeGestora.trim().isEmpty()) {
                consumirPersistir.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
                logger.info("Aplicando filtro por Unidade Gestora: " + cdUnidadeGestora);
            }
            if (dtAnoExercicioCTB != null) {
                consumirPersistir.setDtAnoExercicioCTBFiltro(dtAnoExercicioCTB);
                logger.info("Aplicando filtro por Ano do Exercício: " + dtAnoExercicioCTB);
            }
            List<TotalizadoresExecucaoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Totalizadores de Execução: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint",
        description = "Retorna informações básicas para teste do endpoint de totalizadores de execução."
    )
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Totalizadores de Execução");
            TotalizadoresExecucaoDTO dto = new TotalizadoresExecucaoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Parâmetros requeridos: ").append(dto.getParametrosRequeridos()).append("\n");
            info.append("Campos de resposta mapeados: ").append(dto.getCamposResposta().size()).append("\n");
            info.append("Campos de parâmetros mapeados: ").append(dto.getCamposParametros().size()).append("\n");
            info.append("\n=== INFORMAÇÕES DA API ===\n");
            info.append("Endpoint SEFAZ: https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/totalizadores-execucao\n");
            info.append("Filtros suportados:\n");
            info.append("- cdUnidadeGestora: Código da Unidade Gestora\n");
            info.append("- dtAnoExercicioCTB: Ano do Exercício Contábil\n");
            info.append("\n=== CAMPOS PRINCIPAIS ===\n");
            info.append("- Valores monetários: vlTotalPago, vlTotalLiquidado, vlTotalEmpenhado, vlTotalDotacaoAtualizada\n");
            info.append("- Identificadores: cdUnidadeGestora, cdProgramaGoverno, cdAcao, cdGestao\n");
            info.append("- Descrições: nmProgramaGoverno, nmAcao, nmFonteRecurso\n");
            info.append("- Data: dhUltimaAlteracao\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Totalizadores de Execução: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erro no teste do endpoint: " + e.getMessage());
        }
    }
}