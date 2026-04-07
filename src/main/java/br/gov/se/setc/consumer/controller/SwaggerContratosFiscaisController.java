package br.gov.se.setc.consumer.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.consumer.dto.ContratosFiscaisDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import br.gov.se.setc.logging.UnifiedLogger;
import br.gov.se.setc.logging.annotation.LogOperation;
import br.gov.se.setc.logging.util.MDCUtil;
import org.springframework.beans.factory.annotation.Autowired;
@RestController
@RequestMapping("/contratos-fiscais")
@Tag(name = "Contratos Fiscais", description = "API para consumo e gestão de dados de contratos fiscais do SEFAZ")
public class SwaggerContratosFiscaisController {
    private final ConsumoApiService<ContratosFiscaisDTO> consumoApiService;
    private final UnifiedLogger unifiedLogger;
    @Autowired
    public SwaggerContratosFiscaisController(
            @Qualifier("contratosFiscaisConsumoApiService") ConsumoApiService<ContratosFiscaisDTO> consumoApiService,
            UnifiedLogger unifiedLogger
    ) {
        this.consumoApiService = consumoApiService;
        this.unifiedLogger = unifiedLogger;
    }
  @GetMapping
  @Operation(summary = "Lista todos os contratos fiscais", description = "Retorna uma lista com todos os contratos fiscais disponíveis.")
  @LogOperation(operation = "LISTAR_CONTRATOS", component = "CONTROLLER", slowOperationThresholdMs = 30000)
  public List<ContratosFiscaisDTO> listarContratos() {
      String requestId = MDCUtil.generateAndSetCorrelationId();
      MDCUtil.setupOperationContext("CONTROLLER", "LISTAR_CONTRATOS");
      try {
          unifiedLogger.logOperationStart("CONTROLLER", "LISTAR_CONTRATOS", "ENDPOINT", "/contratos-fiscais");
          ContratosFiscaisDTO consumirPersistir = new ContratosFiscaisDTO();
          List<ContratosFiscaisDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
          unifiedLogger.logOperationSuccess("CONTROLLER", "LISTAR_CONTRATOS", 0,
              result != null ? result.size() : 0, "ENDPOINT", "/contratos-fiscais");
          return result;
      } catch (Exception e) {
          unifiedLogger.logOperationError("CONTROLLER", "LISTAR_CONTRATOS", 0, e, "ENDPOINT", "/contratos-fiscais");
          throw e;
      } finally {
          MDCUtil.clear();
      }
  }
}