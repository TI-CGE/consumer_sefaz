package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para documentação e teste da API de Previsão Realização Receita
 * Endpoint base: /previsao-realizacao-receita
 */
@RestController
@RequestMapping("/previsao-realizacao-receita")
@Tag(name = "Previsão Realização Receita", description = "API para consumo e gestão de dados de previsão e realização de receitas do SEFAZ")
public class SwaggerPrevisaoRealizacaoReceitaController {

    private static final Logger logger = Logger.getLogger(SwaggerPrevisaoRealizacaoReceitaController.class.getName());
    private final ConsumoApiService<PrevisaoRealizacaoReceitaDTO> consumoApiService;

    public SwaggerPrevisaoRealizacaoReceitaController(
            @Qualifier("previsaoRealizacaoReceitaConsumoApiService") ConsumoApiService<PrevisaoRealizacaoReceitaDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(
        summary = "Lista dados de previsão e realização de receitas", 
        description = "Retorna uma lista com dados de previsão e realização de receitas filtrados por unidade gestora e/ou ano de exercício. " +
                     "Os dados são organizados em hierarquia: Categoria Econômica → Origem → Espécie → Desdobramento → Tipo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<PrevisaoRealizacaoReceitaDTO> listarPrevisaoRealizacaoReceita(
            @Parameter(description = "Código da Unidade Gestora (ex: 110000)", example = "110000")
            @RequestParam(required = false) String cdUnidadeGestora,

            @Parameter(description = "Ano do exercício contábil (ex: 2024)", example = "2024")
            @RequestParam(required = false) Integer dtAnoExercicioCTB,

            @Parameter(description = "Número do mês (1-12, padrão: 12 para dados anuais)", example = "12")
            @RequestParam(required = false, defaultValue = "12") Integer nuMes
    ) {
        try {
            logger.info("Iniciando consumo da API de Previsão Realização Receita");
            logger.info("Parâmetros - UG: " + cdUnidadeGestora + ", Ano: " + dtAnoExercicioCTB + ", Mês: " + nuMes);

            PrevisaoRealizacaoReceitaDTO dto = new PrevisaoRealizacaoReceitaDTO();

            // Configurar filtros se fornecidos
            if (cdUnidadeGestora != null && !cdUnidadeGestora.trim().isEmpty()) {
                dto.setCdUnidadeGestoraFiltro(cdUnidadeGestora.trim());
            }
            if (dtAnoExercicioCTB != null) {
                dto.setDtAnoExercicioCTBFiltro(dtAnoExercicioCTB);
            }
            if (nuMes != null) {
                dto.setNuMesFiltro(nuMes);
            }
            
            List<PrevisaoRealizacaoReceitaDTO> result = consumoApiService.consumirPersistir(dto);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
            
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Previsão Realização Receita: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(
        summary = "Teste básico do endpoint", 
        description = "Retorna informações de configuração do endpoint para verificação de funcionamento. " +
                     "Útil para validar se o serviço está configurado corretamente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações do endpoint retornadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Previsão Realização Receita");
            PrevisaoRealizacaoReceitaDTO dto = new PrevisaoRealizacaoReceitaDTO();
            
            StringBuilder info = new StringBuilder();
            info.append("=== ENDPOINT PREVISÃO REALIZAÇÃO RECEITA ===\n");
            info.append("Status: Funcionando!\n\n");
            
            info.append("📊 CONFIGURAÇÕES:\n");
            info.append("URL da API: ").append(dto.getUrl()).append("\n");
            info.append("Tabela no banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Campo ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            info.append("Filtro data inicial: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Filtro data final: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n\n");
            
            info.append("🔍 PARÂMETROS SUPORTADOS:\n");
            info.append("• cdUnidadeGestora - Código da Unidade Gestora (ex: 110000)\n");
            info.append("• dtAnoExercicioCTB - Ano do exercício contábil (ex: 2024)\n");
            info.append("• nuMes - Número do mês (1-12, padrão: 12 para dados anuais)\n\n");
            
            info.append("📋 ESTRUTURA DE DADOS:\n");
            info.append("• Hierarquia: Categoria Econômica → Origem → Espécie → Desdobramento → Tipo\n");
            info.append("• Valores: vlPrevisto, vlAtualizado, vlRealizado\n");
            info.append("• Chave única: UG + Ano + Mês + Hierarquia completa\n");
            info.append("💡 BUSCA MULTI-MÊS: Use os endpoints do scheduler para busca de múltiplos meses\n\n");
            
            info.append("🔗 EXEMPLOS DE USO:\n");
            info.append("GET /previsao-realizacao-receita\n");
            info.append("GET /previsao-realizacao-receita?cdUnidadeGestora=110000\n");
            info.append("GET /previsao-realizacao-receita?dtAnoExercicioCTB=2024\n");
            info.append("GET /previsao-realizacao-receita?cdUnidadeGestora=110000&dtAnoExercicioCTB=2024\n");
            info.append("GET /previsao-realizacao-receita?cdUnidadeGestora=110000&dtAnoExercicioCTB=2024&nuMes=12\n\n");
            
            info.append("⚙️ ENDPOINTS MANUAIS:\n");
            info.append("POST /scheduler/execute/previsao-realizacao-receita (mês único)\n");
            info.append("POST /scheduler/execute/previsao-realizacao-receita-multi-mes (todos os 12 meses)\n");
            info.append("POST /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes} (mês específico)\n\n");
            
            info.append("📈 CONTEXTO:\n");
            info.append("Módulo: CTB (Contabilidade)\n");
            info.append("Fonte: API Transparência SEFAZ/SE\n");
            info.append("Atualização: Via scheduler automático ou execução manual\n");
            
            logger.info("Teste concluído com sucesso");
            return ResponseEntity.ok(info.toString());
            
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint: " + e.getMessage());
            e.printStackTrace();
            
            StringBuilder errorInfo = new StringBuilder();
            errorInfo.append("❌ ERRO NO ENDPOINT PREVISÃO REALIZAÇÃO RECEITA\n\n");
            errorInfo.append("Erro: ").append(e.getMessage()).append("\n");
            errorInfo.append("Tipo: ").append(e.getClass().getSimpleName()).append("\n");
            errorInfo.append("Verifique os logs para mais detalhes.\n");
            
            return ResponseEntity.internalServerError().body(errorInfo.toString());
        }
    }
}
