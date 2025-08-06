package br.gov.se.setc.consumer.controller;

import br.gov.se.setc.consumer.dto.DadosOrcamentariosDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import br.gov.se.setc.logging.UnifiedLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/dados-orcamentarios")
@Tag(name = "dados-orcamentarios", description = "Endpoints para documentar dados orçamentários")
public class SwaggerDadosOrcamentariosController {

    private static final Logger logger = Logger.getLogger(SwaggerDadosOrcamentariosController.class.getName());
    private final ConsumoApiService<DadosOrcamentariosDTO> consumoApiService;
    private final UnifiedLogger unifiedLogger;

    @Autowired
    public SwaggerDadosOrcamentariosController(
            @Qualifier("dadosOrcamentariosConsumoApiService") ConsumoApiService<DadosOrcamentariosDTO> consumoApiService,
            UnifiedLogger unifiedLogger
    ) {
        this.consumoApiService = consumoApiService;
        this.unifiedLogger = unifiedLogger;
    }
    
    @GetMapping
    @Operation(
        summary = "Lista todos os dados orçamentários", 
        description = "Retorna uma lista com todos os dados orçamentários disponíveis.",
        parameters = {
            @Parameter(name = "dtAnoExercicioCTB", description = "Ano do exercício contábil", example = "2025"),
            @Parameter(name = "cdUnidadeGestora", description = "Código da unidade gestora", example = "123456"),
            @Parameter(name = "cdGestao", description = "Código da gestão", example = "00001"),
            @Parameter(name = "sqEmpenho", description = "Sequencial do empenho", example = "1")
        }
    )
    public List<DadosOrcamentariosDTO> listarDadosOrcamentarios() {
        try {
            unifiedLogger.logOperationStart("CONTROLLER", "LISTAR_DADOS_ORCAMENTARIOS", "ENDPOINT", "/dados-orcamentarios");
            
            logger.info("Iniciando consumo da API de Dados Orçamentários");
            DadosOrcamentariosDTO consumirPersistir = new DadosOrcamentariosDTO();
            List<DadosOrcamentariosDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            
            unifiedLogger.logOperationSuccess("CONTROLLER", "LISTAR_DADOS_ORCAMENTARIOS", 0,
                result != null ? result.size() : 0, "ENDPOINT", "/dados-orcamentarios");
            
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            unifiedLogger.logOperationError("CONTROLLER", "LISTAR_DADOS_ORCAMENTARIOS", e.getMessage(), 
                "ENDPOINT", "/dados-orcamentarios");
            
            logger.severe("Erro ao consumir API de Dados Orçamentários: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint", 
        description = "Retorna informações básicas para teste do endpoint de dados orçamentários."
    )
    public ResponseEntity<String> testeEndpoint() {
        try {
            unifiedLogger.logOperationStart("CONTROLLER", "TESTE_DADOS_ORCAMENTARIOS", "ENDPOINT", "/dados-orcamentarios/test");
            
            logger.info("Teste do endpoint de Dados Orçamentários");
            DadosOrcamentariosDTO dto = new DadosOrcamentariosDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Parâmetros requeridos: ").append(dto.getParametrosRequeridos()).append("\n");
            info.append("\nParâmetros obrigatórios:\n");
            info.append("- dtAnoExercicioCTB (integer): Ano do exercício contábil\n");
            info.append("- cdUnidadeGestora (string): Código da unidade gestora\n");
            info.append("- cdGestao (string): Código da gestão\n");
            info.append("- sqEmpenho (integer): Sequencial do empenho\n");
            info.append("\nCampos de resposta (14 campos):\n");
            info.append("- cdFuncaoPLO, nmFuncaoPLO\n");
            info.append("- cdSubFuncao, nmSubFuncao\n");
            info.append("- cdProgramaGoverno, nmProgramaGoverno\n");
            info.append("- cdCategoriaEconomica, nmCategoriaEconomica\n");
            info.append("- cdModalidadeAplicacao, nmModalidadeAplicacao\n");
            info.append("- cdFonteRecurso, nmFonteRecurso\n");
            info.append("- cdGrupoDespesa, nmGrupoDespesa\n");
            
            unifiedLogger.logOperationSuccess("CONTROLLER", "TESTE_DADOS_ORCAMENTARIOS", 0, 1, 
                "ENDPOINT", "/dados-orcamentarios/test");
            
            logger.info("Teste do endpoint concluído com sucesso");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            unifiedLogger.logOperationError("CONTROLLER", "TESTE_DADOS_ORCAMENTARIOS", e.getMessage(), 
                "ENDPOINT", "/dados-orcamentarios/test");
            
            logger.severe("Erro no teste do endpoint de Dados Orçamentários: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro no teste: " + e.getMessage());
        }
    }
}
