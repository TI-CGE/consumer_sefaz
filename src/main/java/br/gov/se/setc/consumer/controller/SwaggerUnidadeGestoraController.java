package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.UnidadeGestoraDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/unidade-gestora")
@Tag(name = "unidade gestora", description = "Endpoints para documentar unidade gestora")
public class SwaggerUnidadeGestoraController {

    private static final Logger logger = Logger.getLogger(SwaggerUnidadeGestoraController.class.getName());
    private final ConsumoApiService<UnidadeGestoraDTO> consumoApiService;

    public SwaggerUnidadeGestoraController(
            @Qualifier("unidadeGestoraConsumoApiService") ConsumoApiService<UnidadeGestoraDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
  @GetMapping
  @Operation(summary = "Lista todos os unidade gestora", description = "Retorna uma lista com todos os unidade gestora disponíveis.")
  public List<UnidadeGestoraDTO> listarUnidadeGestora() {
      try {
          logger.info("Iniciando consumo da API de Unidade Gestora");
          UnidadeGestoraDTO consumirPersistir = new UnidadeGestoraDTO();
          List<UnidadeGestoraDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
          logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
          return result;
      } catch (Exception e) {
          logger.severe("Erro ao consumir API de Unidade Gestora: " + e.getMessage());
          e.printStackTrace();
          throw e;
      }
  }

  @GetMapping("/test")
  @Operation(summary = "Teste básico do endpoint", description = "Retorna informações básicas para teste.")
  public ResponseEntity<String> testeEndpoint() {
      try {
          logger.info("Teste do endpoint de Unidade Gestora");
          UnidadeGestoraDTO dto = new UnidadeGestoraDTO();
          StringBuilder info = new StringBuilder();
          info.append("Endpoint funcionando!\n");
          info.append("URL configurada: ").append(dto.getUrl()).append("\n");
          info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
          info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
          info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
          info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
          return ResponseEntity.ok(info.toString());
      } catch (Exception e) {
          logger.severe("Erro no teste: " + e.getMessage());
          return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
      }
  }
}