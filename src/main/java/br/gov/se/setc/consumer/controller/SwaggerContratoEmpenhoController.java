package br.gov.se.setc.consumer.controller;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.consumer.dto.ContratoEmpenhoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/contrato-empenho")
@Tag(name = "Contrato Empenho", description = "API para consumo e gestão de dados de contrato-empenho do SEFAZ")
public class SwaggerContratoEmpenhoController {
    private static final Logger logger = Logger.getLogger(SwaggerContratoEmpenhoController.class.getName());
    private final ConsumoApiService<ContratoEmpenhoDTO> consumoApiService;
    public SwaggerContratoEmpenhoController(
            @Qualifier("contratoEmpenhoConsumoApiService") ConsumoApiService<ContratoEmpenhoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    @GetMapping
    @Operation(
        summary = "Lista todos os contratos-empenho",
        description = "Retorna uma lista com todos os contratos-empenho disponíveis da API SEFAZ. " +
                     "Este endpoint consome dados do endpoint https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-empenho " +
                     "e persiste os dados na tabela consumer_sefaz.contrato_empenho."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contratos-empenho retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a requisição"),
        @ApiResponse(responseCode = "503", description = "Serviço temporariamente indisponível")
    })
    public List<ContratoEmpenhoDTO> listarContratoEmpenho() {
        try {
            logger.info("Iniciando consumo da API de Contrato-Empenho");
            ContratoEmpenhoDTO consumirPersistir = new ContratoEmpenhoDTO();
            List<ContratoEmpenhoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Contrato-Empenho: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint",
        description = "Retorna informações básicas para teste do endpoint de Contrato-Empenho. " +
                     "Útil para verificar se a configuração está correta e o endpoint está funcionando."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de teste retornadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Contrato-Empenho");
            ContratoEmpenhoDTO dto = new ContratoEmpenhoDTO();
            StringBuilder info = new StringBuilder();
            info.append("=== TESTE ENDPOINT CONTRATO-EMPENHO ===\n");
            info.append("Status: Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Descrição: Endpoint para consumo de dados de Contrato-Empenho da SEFAZ\n");
            info.append("Campos principais: cdSolicitacaoCompra, cdContrato, cdLicitacao, valores monetários, datas de vigência\n");
            info.append("=== FIM TESTE ===\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Contrato-Empenho: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}