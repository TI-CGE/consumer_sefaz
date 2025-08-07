package br.gov.se.setc.consumer.controller;

import br.gov.se.setc.consumer.dto.ConsultaGerencialDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/consulta-gerencial")
@Tag(name = "consulta-gerencial", description = "Endpoints para documentar consultas gerenciais (diárias)")
public class SwaggerConsultaGerencialController {

    private static final Logger logger = Logger.getLogger(SwaggerConsultaGerencialController.class.getName());
    private final ConsumoApiService<ConsultaGerencialDTO> consumoApiService;

    public SwaggerConsultaGerencialController(
            @Qualifier("consultaGerencialConsumoApiService") ConsumoApiService<ConsultaGerencialDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(
        summary = "Lista todas as consultas gerenciais (diárias)", 
        description = "Retorna uma lista com todas as consultas gerenciais (diárias) disponíveis. " +
                     "Este endpoint consome dados da API de transparência SEFAZ e persiste no banco local."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de consultas gerenciais retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<ConsultaGerencialDTO> listarConsultaGerencial(
            @Parameter(description = "Código da Unidade Gestora para filtrar os resultados", example = "123456")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Ano do exercício contábil para filtrar os resultados", example = "2025")
            @RequestParam(required = false) Integer dtAnoExercicioCTB
    ) {
        try {
            logger.info("Iniciando consumo da API de Consulta Gerencial (Diárias)");
            
            ConsultaGerencialDTO consumirPersistir = new ConsultaGerencialDTO();
            
            // Aplicar filtros se fornecidos
            if (cdUnidadeGestora != null) {
                consumirPersistir.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
                logger.info("Filtro aplicado - Código Unidade Gestora: " + cdUnidadeGestora);
            }
            
            if (dtAnoExercicioCTB != null) {
                consumirPersistir.setDtAnoExercicioCTBFiltro(dtAnoExercicioCTB);
                logger.info("Filtro aplicado - Ano Exercício CTB: " + dtAnoExercicioCTB);
            }
            
            List<ConsultaGerencialDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Consulta Gerencial: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint", 
        description = "Retorna informações básicas para teste do endpoint de consulta gerencial. " +
                     "Útil para verificar se a configuração está correta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de teste retornadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Consulta Gerencial");
            ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Filtros suportados: cdUnidadeGestora, dtAnoExercicioCTB\n");
            info.append("Exemplo de uso: /consulta-gerencial?cdUnidadeGestora=123456&dtAnoExercicioCTB=2025\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Consulta Gerencial: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(
        summary = "Informações detalhadas sobre a API", 
        description = "Retorna informações detalhadas sobre a configuração e estrutura da API de consulta gerencial."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações detalhadas retornadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> informacoesDetalhadas() {
        try {
            logger.info("Solicitação de informações detalhadas da Consulta Gerencial");
            ConsultaGerencialDTO dto = new ConsultaGerencialDTO();
            StringBuilder info = new StringBuilder();
            
            info.append("=== CONSULTA GERENCIAL (DIÁRIAS) - INFORMAÇÕES DETALHADAS ===\n\n");
            info.append("Descrição: Endpoint para consumo de dados de diárias da SEFAZ\n");
            info.append("URL da API: ").append(dto.getUrl()).append("\n");
            info.append("Tabela de destino: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Schema: consumer_sefaz\n\n");
            
            info.append("=== FILTROS DISPONÍVEIS ===\n");
            info.append("- cdUnidadeGestora: Código da Unidade Gestora (obrigatório na API)\n");
            info.append("- dtAnoExercicioCTB: Ano do exercício contábil (obrigatório na API)\n\n");
            
            info.append("=== CAMPOS PRINCIPAIS ===\n");
            info.append("- sqSolicitacaoDiaria: Chave primária da solicitação\n");
            info.append("- nmRazaoSocialPessoa: Nome da pessoa beneficiária\n");
            info.append("- vlTotalSolicitacaoDiaria: Valor total da diária\n");
            info.append("- dtSaidaSolicitacaoDiaria: Data de saída\n");
            info.append("- dtRetornoSolicitacaoDiaria: Data de retorno\n");
            info.append("- destinoViagemMunicipioSolicitacaoDiaria: Destino da viagem\n");
            info.append("- nmCargo: Cargo do beneficiário\n\n");
            
            info.append("=== EXEMPLOS DE USO ===\n");
            info.append("GET /consulta-gerencial\n");
            info.append("GET /consulta-gerencial?cdUnidadeGestora=123456\n");
            info.append("GET /consulta-gerencial?cdUnidadeGestora=123456&dtAnoExercicioCTB=2025\n");
            info.append("GET /consulta-gerencial/test\n");
            info.append("GET /consulta-gerencial/info\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro ao obter informações detalhadas: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
