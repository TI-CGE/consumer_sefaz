package br.gov.se.setc.consumer.controller;
import br.gov.se.setc.consumer.dto.DespesaDetalhadaDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;
/**
 * Controller para consumo da API de Despesa Detalhada do SEFAZ
 *
 * Endpoints disponíveis:
 * - GET /despesa-detalhada: Consome dados de despesa detalhada com filtros opcionais
 * - GET /despesa-detalhada/test: Endpoint de teste para verificar funcionamento
 */
@RestController
@RequestMapping("/despesa-detalhada")
@Tag(name = "Despesa Detalhada", description = "API para consumo e gestão de dados de despesa detalhada do SEFAZ")
public class SwaggerDespesaDetalhadaController {
    private static final Logger logger = Logger.getLogger(SwaggerDespesaDetalhadaController.class.getName());
    private final ConsumoApiService<DespesaDetalhadaDTO> consumoApiService;
    public SwaggerDespesaDetalhadaController(
            @Qualifier("despesaDetalhadaConsumoApiService")
            ConsumoApiService<DespesaDetalhadaDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }
    /**
     * Consome dados de despesa detalhada da API SEFAZ
     */
    @GetMapping
    @Operation(
        summary = "Consumir Despesa Detalhada",
        description = "Consome dados de despesa detalhada da API SEFAZ com filtros opcionais. " +
                     "O parâmetro 'ano' é obrigatório. Outros filtros são opcionais para refinar a consulta."
    )
    @LogOperation(operation = "CONSUME_DESPESA_DETALHADA", component = "DESPESA_DETALHADA_CONTROLLER")
    public List<DespesaDetalhadaDTO> consumirDespesaDetalhada(
            @Parameter(description = "Código da Unidade Gestora", example = "173011")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Ano do exercício", example = "2025")
            @RequestParam(required = false) Integer ano,
            @Parameter(description = "Número do mês (1-12)", example = "12")
            @RequestParam(required = false) Integer nuMes,
            @Parameter(description = "Código do órgão", example = "16000")
            @RequestParam(required = false) String cdOrgao,
            @Parameter(description = "Código da unidade orçamentária", example = "16101")
            @RequestParam(required = false) String cdUnidOrc,
            @Parameter(description = "Código da função", example = "04")
            @RequestParam(required = false) String cdFuncao,
            @Parameter(description = "Código da subfunção", example = "122")
            @RequestParam(required = false) String cdSubFuncao,
            @Parameter(description = "Código do programa de governo", example = "0012")
            @RequestParam(required = false) String cdProgramaGoverno,
            @Parameter(description = "Código da ação PPA", example = "0789")
            @RequestParam(required = false) String cdPPAAcao,
            @Parameter(description = "Código da subação", example = "0000")
            @RequestParam(required = false) String cdSubAcao,
            @Parameter(description = "Código da categoria econômica", example = "3")
            @RequestParam(required = false) String cdCategoriaEconomica,
            @Parameter(description = "Código da natureza da despesa", example = "33904000")
            @RequestParam(required = false) String cdNaturezaDespesa
    ) {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setComponent("DESPESA_DETALHADA_CONTROLLER");
        MDCUtil.setOperation("CONSUME_DESPESA_DETALHADA");
        logger.info("Iniciando consumo de Despesa Detalhada - Correlation ID: " + correlationId);
        logger.info("Parâmetros: " +
                   (cdUnidadeGestora != null ? "cdUnidadeGestora=" + cdUnidadeGestora : "") +
                   (ano != null ? ", ano=" + ano : "") +
                   (nuMes != null ? ", nuMes=" + nuMes : "") +
                   (cdOrgao != null ? ", cdOrgao=" + cdOrgao : "") +
                   (cdUnidOrc != null ? ", cdUnidOrc=" + cdUnidOrc : "") +
                   (cdFuncao != null ? ", cdFuncao=" + cdFuncao : "") +
                   (cdSubFuncao != null ? ", cdSubFuncao=" + cdSubFuncao : "") +
                   (cdProgramaGoverno != null ? ", cdProgramaGoverno=" + cdProgramaGoverno : "") +
                   (cdPPAAcao != null ? ", cdPPAAcao=" + cdPPAAcao : "") +
                   (cdSubAcao != null ? ", cdSubAcao=" + cdSubAcao : "") +
                   (cdCategoriaEconomica != null ? ", cdCategoriaEconomica=" + cdCategoriaEconomica : "") +
                   (cdNaturezaDespesa != null ? ", cdNaturezaDespesa=" + cdNaturezaDespesa : ""));
        try {
            DespesaDetalhadaDTO dto = new DespesaDetalhadaDTO();
            if (cdUnidadeGestora != null && !cdUnidadeGestora.trim().isEmpty()) {
                dto.setCdUnidadeGestoraFiltro(cdUnidadeGestora.trim());
            }
            if (ano != null) {
                dto.setDtAnoExercicioCTBFiltro(ano);
            }
            if (nuMes != null) {
                dto.setNuMesFiltro(nuMes);
            }
            if (cdOrgao != null && !cdOrgao.trim().isEmpty()) {
                dto.setCdOrgaoFiltro(cdOrgao.trim());
            }
            if (cdUnidOrc != null && !cdUnidOrc.trim().isEmpty()) {
                dto.setCdUnidOrcFiltro(cdUnidOrc.trim());
            }
            if (cdFuncao != null && !cdFuncao.trim().isEmpty()) {
                dto.setCdFuncaoFiltro(cdFuncao.trim());
            }
            if (cdSubFuncao != null && !cdSubFuncao.trim().isEmpty()) {
                dto.setCdSubFuncaoFiltro(cdSubFuncao.trim());
            }
            if (cdProgramaGoverno != null && !cdProgramaGoverno.trim().isEmpty()) {
                dto.setCdProgramaGovernoFiltro(cdProgramaGoverno.trim());
            }
            if (cdPPAAcao != null && !cdPPAAcao.trim().isEmpty()) {
                dto.setCdPPAAcaoFiltro(cdPPAAcao.trim());
            }
            if (cdSubAcao != null && !cdSubAcao.trim().isEmpty()) {
                dto.setCdSubAcaoFiltro(cdSubAcao.trim());
            }
            if (cdCategoriaEconomica != null && !cdCategoriaEconomica.trim().isEmpty()) {
                dto.setCdCategoriaEconomicaFiltro(cdCategoriaEconomica.trim());
            }
            if (cdNaturezaDespesa != null && !cdNaturezaDespesa.trim().isEmpty()) {
                dto.setCdNaturezaDespesaFiltro(cdNaturezaDespesa.trim());
            }
            List<DespesaDetalhadaDTO> result = consumoApiService.consumirPersistir(dto);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Despesa Detalhada: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * Endpoint de teste para verificar funcionamento do serviço
     */
    @GetMapping("/test")
    @Operation(
        summary = "Teste do Serviço",
        description = "Endpoint de teste para verificar se o serviço de Despesa Detalhada está funcionando corretamente"
    )
    @LogOperation(operation = "TEST_DESPESA_DETALHADA", component = "DESPESA_DETALHADA_CONTROLLER")
    public ResponseEntity<String> test() {
        String correlationId = MDCUtil.generateAndSetCorrelationId();
        MDCUtil.setComponent("DESPESA_DETALHADA_CONTROLLER");
        MDCUtil.setOperation("TEST_DESPESA_DETALHADA");
        logger.info("Teste do serviço Despesa Detalhada solicitado - Correlation ID: " + correlationId);
        StringBuilder info = new StringBuilder();
        try {
            info.append("=== SERVIÇO DESPESA DETALHADA ===\n");
            info.append("Status: Funcionando!\n\n");
            info.append("🎯 FUNCIONALIDADE:\n");
            info.append("• Consumo de dados de despesa detalhada do SEFAZ\n");
            info.append("• Filtros por órgão, unidade, função, programa, ação e natureza\n");
            info.append("• Persistência automática no banco de dados\n");
            info.append("• Deduplicação baseada em chave natural\n\n");
            info.append("🔗 ENDPOINT:\n");
            info.append("GET /despesa-detalhada?cdUnidadeGestora={ug}&ano={ano}&nuMes={mes}&cdOrgao={cdOrgao}&...\n\n");
            info.append("📋 PARÂMETROS:\n");
            info.append("• cdUnidadeGestora (opcional): Código da Unidade Gestora (ex: 173011)\n");
            info.append("• ano (opcional): Ano do exercício (ex: 2025)\n");
            info.append("• nuMes (opcional): Número do mês (1-12, ex: 12)\n");
            info.append("• cdOrgao (opcional): Código do órgão\n");
            info.append("• cdUnidOrc (opcional): Código da unidade orçamentária\n");
            info.append("• cdFuncao (opcional): Código da função\n");
            info.append("• cdSubFuncao (opcional): Código da subfunção\n");
            info.append("• cdProgramaGoverno (opcional): Código do programa\n");
            info.append("• cdPPAAcao (opcional): Código da ação PPA\n");
            info.append("• cdSubAcao (opcional): Código da subação\n");
            info.append("• cdCategoriaEconomica (opcional): Categoria econômica\n");
            info.append("• cdNaturezaDespesa (opcional): Natureza da despesa\n\n");
            info.append("🗄️ TABELA: consumer_sefaz.despesa_detalhada\n");
            info.append("🔑 CHAVE NATURAL: ug + ano + mês + órgão + unidade + natureza + ação + subação\n\n");
            info.append("✅ Teste realizado com sucesso!\n");
            info.append("Correlation ID: ").append(correlationId);
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro durante teste do serviço: " + e.getMessage());
            info.append("❌ Erro durante teste: ").append(e.getMessage());
            return ResponseEntity.internalServerError().body(info.toString());
        }
    }
}