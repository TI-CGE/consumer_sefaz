package br.gov.se.setc.consumer.controller;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.consumer.dto.LiquidacaoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/liquidacao")
@Tag(name = "Liquidações", description = "API para consumo e gestão de dados de liquidações do SEFAZ")
public class SwaggerLiquidacaoController {
    private static final Logger logger = Logger.getLogger(SwaggerLiquidacaoController.class.getName());
    private final ConsumoApiService<LiquidacaoDTO> consumoApiService;
    public SwaggerLiquidacaoController(
            @Qualifier("liquidacaoConsumoApiService") ConsumoApiService<LiquidacaoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    @GetMapping
    @Operation(
        summary = "Consumir e listar liquidações",
        description = "Consome dados de liquidações da API de transparência SEFAZ e persiste no banco de dados local. " +
                     "Retorna uma lista com todas as liquidações processadas, incluindo informações sobre " +
                     "valores liquidados, empenhos relacionados, credores e datas de liquidação.",
        tags = {"Liquidações"}
    )
    public List<LiquidacaoDTO> listarLiquidacao() {
        try {
            logger.info("Iniciando consumo da API de Liquidação");
            LiquidacaoDTO consumirPersistir = new LiquidacaoDTO();
            List<LiquidacaoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Liquidação: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(
        summary = "Teste de configuração do endpoint",
        description = "Retorna informações técnicas sobre a configuração do endpoint de liquidações, " +
                     "incluindo URL da API SEFAZ, tabela de destino, filtros suportados e parâmetros de configuração. " +
                     "Útil para verificar se o endpoint está configurado corretamente.",
        tags = {"Liquidações"}
    )
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Liquidação");
            LiquidacaoDTO dto = new LiquidacaoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Liquidação: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}