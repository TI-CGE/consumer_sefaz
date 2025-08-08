package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.BaseDespesaCredorDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/base-despesa-credor")
@Tag(name = "base-despesa-credor", description = "Endpoints para consumo de dados de Base Despesa Credor do SEFAZ")
public class SwaggerBaseDespesaCredorController {

    private static final Logger logger = Logger.getLogger(SwaggerBaseDespesaCredorController.class.getName());
    private final ConsumoApiService<BaseDespesaCredorDTO> consumoApiService;

    public SwaggerBaseDespesaCredorController(
            @Qualifier("baseDespesaCredorConsumoApiService") ConsumoApiService<BaseDespesaCredorDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(
        summary = "Lista dados de Base Despesa Credor", 
        description = "Retorna uma lista com dados de Base Despesa Credor da API SEFAZ. " +
                     "Este endpoint consome dados do endpoint https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-credor " +
                     "e persiste os dados na tabela consumer_sefaz.base_despesa_credor. " +
                     "A API utiliza paginação controlada por nuFaixaPaginacao e qtTotalFaixasPaginacao. " +
                     "Os dados incluem informações sobre empenhos, credores, valores monetários e itens de material/serviço."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de dados de Base Despesa Credor retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos fornecidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a requisição"),
        @ApiResponse(responseCode = "503", description = "Serviço temporariamente indisponível")
    })
    public List<BaseDespesaCredorDTO> listarBaseDespesaCredor(
            @Parameter(description = "Código da Unidade Gestora para filtro", example = "123456")
            @RequestParam(required = false) String cdUnidadeGestora,
            
            @Parameter(description = "Ano do Exercício para filtro", example = "2025")
            @RequestParam(required = false) Integer dtAnoExercicio,
            
            @Parameter(description = "Número da Faixa de Paginação (1 para primeira página)", example = "1")
            @RequestParam(required = false) Integer nuFaixaPaginacao
    ) {
        try {
            logger.info("Iniciando consumo da API de Base Despesa Credor");
            
            BaseDespesaCredorDTO consumirPersistir = new BaseDespesaCredorDTO();
            
            // Aplicar filtros se fornecidos
            if (cdUnidadeGestora != null && !cdUnidadeGestora.trim().isEmpty()) {
                consumirPersistir.setCdUnidadeGestoraFiltro(cdUnidadeGestora);
                logger.info("Aplicando filtro por Unidade Gestora: " + cdUnidadeGestora);
            }
            
            if (dtAnoExercicio != null) {
                consumirPersistir.setDtAnoExercicioFiltro(dtAnoExercicio);
                logger.info("Aplicando filtro por Ano do Exercício: " + dtAnoExercicio);
            }
            
            if (nuFaixaPaginacao != null) {
                consumirPersistir.setNuFaixaPaginacaoFiltro(nuFaixaPaginacao);
                logger.info("Aplicando filtro por Faixa de Paginação: " + nuFaixaPaginacao);
            }
            
            List<BaseDespesaCredorDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            
            // Log informações de paginação se disponível
            if (result != null && !result.isEmpty() && result.get(0).getQtTotalFaixasPaginacao() != null) {
                logger.info("Informações de paginação - Faixa atual: " + result.get(0).getNuFaixaPaginacao() + 
                           ", Total de faixas: " + result.get(0).getQtTotalFaixasPaginacao());
            }
            
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Base Despesa Credor: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(
        summary = "Teste completo do endpoint", 
        description = "Executa um teste completo do endpoint de Base Despesa Credor, " +
                     "incluindo consumo de todas as faixas de paginação disponíveis. " +
                     "Útil para verificar se a configuração está correta, o endpoint está funcionando " +
                     "e a paginação está sendo processada adequadamente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teste executado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor durante o teste")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Iniciando teste completo do endpoint de Base Despesa Credor");
            BaseDespesaCredorDTO dto = new BaseDespesaCredorDTO();
            StringBuilder info = new StringBuilder();
            
            info.append("=== TESTE ENDPOINT BASE DESPESA CREDOR ===\n");
            info.append("Status: Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Parâmetros requeridos: ").append(dto.getParametrosRequeridos()).append("\n");
            info.append("Contexto: GBP (Gestão de Bens Públicos)\n");
            info.append("Estrutura de resposta: result > dados > colecao[] (aninhada e paginada)\n");
            info.append("Controle de paginação: nuFaixaPaginacao e qtTotalFaixasPaginacao\n");
            info.append("Descrição: Endpoint para consumo de dados de Base Despesa Credor da SEFAZ\n");
            info.append("Campos principais:\n");
            info.append("  - Identificação: cdGestao, ugCd, dtAnoExercicio, sqEmpenho\n");
            info.append("  - Credor: nuDocumentoPessoa, nmRazaoSocialPessoa, cdTipoDocumento\n");
            info.append("  - Datas: dhLancamentoEmpenho, dtGeracaoEmpenho\n");
            info.append("  - Valores: vlOriginalEmpenho, vlTotalPagoEmpenho, vlTotalItens\n");
            info.append("  - Licitação: nmModalidadeLicitacao, cdLicitacao\n");
            info.append("  - Itens: nmItemMaterialServico, qtItemSolicitacaoEmpenho\n");
            info.append("  - Paginação: nuFaixaPaginacao, qtTotalFaixasPaginacao\n");
            
            // Executar teste de consumo básico
            try {
                logger.info("Executando teste de consumo da API...");
                List<BaseDespesaCredorDTO> testResult = consumoApiService.consumirPersistir(dto);
                
                if (testResult != null) {
                    info.append("TESTE DE CONSUMO: SUCESSO\n");
                    info.append("Registros retornados: ").append(testResult.size()).append("\n");
                    
                    if (!testResult.isEmpty()) {
                        BaseDespesaCredorDTO firstRecord = testResult.get(0);
                        if (firstRecord.getQtTotalFaixasPaginacao() != null) {
                            info.append("Paginação detectada - Faixa: ").append(firstRecord.getNuFaixaPaginacao())
                                .append("/").append(firstRecord.getQtTotalFaixasPaginacao()).append("\n");
                        }
                        if (firstRecord.getMsgUsuario() != null) {
                            info.append("Mensagem da API: ").append(firstRecord.getMsgUsuario()).append("\n");
                        }
                    }
                } else {
                    info.append("TESTE DE CONSUMO: Retornou null\n");
                }
            } catch (Exception testException) {
                info.append("TESTE DE CONSUMO: ERRO - ").append(testException.getMessage()).append("\n");
                logger.warning("Erro durante teste de consumo: " + testException.getMessage());
            }
            
            info.append("=== FIM TESTE ===\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Base Despesa Credor: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
