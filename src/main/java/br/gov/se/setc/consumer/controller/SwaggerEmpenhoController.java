package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.EmpenhoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/empenho")
@Tag(name = "empenho", description = "Endpoints para documentar empenhos")
public class SwaggerEmpenhoController {

    private static final Logger logger = Logger.getLogger(SwaggerEmpenhoController.class.getName());
    private final ConsumoApiService<EmpenhoDTO> consumoApiService;

    public SwaggerEmpenhoController(
            @Qualifier("empenhoConsumoApiService") ConsumoApiService<EmpenhoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(summary = "Lista todos os empenhos", description = "Retorna uma lista com todos os empenhos disponíveis.")
    public List<EmpenhoDTO> listarEmpenho() {
        try {
            logger.info("Iniciando consumo da API de Empenho");
            EmpenhoDTO consumirPersistir = new EmpenhoDTO();
            List<EmpenhoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Empenho: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Teste básico do endpoint", description = "Retorna informações básicas para teste.")
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Empenho");
            EmpenhoDTO dto = new EmpenhoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Empenho: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
