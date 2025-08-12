package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.PrevisaoRealizacaoReceitaDTO;
import br.gov.se.setc.consumer.service.PrevisaoRealizacaoReceitaMultiMesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para execução de busca multi-mês da Previsão Realização Receita
 *
 * @deprecated Este controller foi movido para a seção Scheduler.
 *             Use os novos endpoints:
 *             - POST /scheduler/execute/previsao-realizacao-receita-multi-mes (todos os 12 meses)
 *             - POST /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes} (mês específico)
 */
@Deprecated
@RestController
@RequestMapping("/previsao-realizacao-receita-multi-mes")
@Tag(name = "Previsão Realização Receita - Multi Mês (DEPRECATED)",
     description = "⚠️ DEPRECATED: Endpoints movidos para a seção Scheduler. " +
                   "Use POST /scheduler/execute/previsao-realizacao-receita-multi-mes para todos os 12 meses ou " +
                   "POST /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes} para mês específico.")
public class PrevisaoRealizacaoReceitaMultiMesController {

    private static final Logger logger = Logger.getLogger(PrevisaoRealizacaoReceitaMultiMesController.class.getName());
    
    private final PrevisaoRealizacaoReceitaMultiMesService multiMesService;

    public PrevisaoRealizacaoReceitaMultiMesController(PrevisaoRealizacaoReceitaMultiMesService multiMesService) {
        this.multiMesService = multiMesService;
    }

    @GetMapping("/test")
    @Operation(summary = "Teste do serviço multi-mês")
    public ResponseEntity<String> testarServico() {
        StringBuilder info = new StringBuilder();
        
        try {
            info.append("=== SERVIÇO MULTI-MÊS PREVISÃO REALIZAÇÃO RECEITA ===\n");
            info.append("Status: Funcionando!\n\n");
            
            info.append("🎯 FUNCIONALIDADE:\n");
            info.append("• Busca automática de TODOS os 12 meses do ano\n");
            info.append("• 12 requisições sequenciais (mês 1 a 12)\n");
            info.append("• Pausa de 500ms entre requisições\n");
            info.append("• Consolidação automática dos dados\n\n");
            
            info.append("🔗 ENDPOINTS:\n");
            info.append("GET /previsao-realizacao-receita-multi-mes/test\n");
            info.append("POST /previsao-realizacao-receita-multi-mes/executar\n");
            info.append("POST /previsao-realizacao-receita-multi-mes/executar-mes/{mes}\n\n");
            
            info.append("⏱️ TEMPO ESTIMADO: ~6 minutos (12 meses)\n\n");
            
            return ResponseEntity.ok(info.toString());
            
        } catch (Exception e) {
            logger.severe("Erro no endpoint de teste multi-mês: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("❌ Erro no serviço: " + e.getMessage());
        }
    }

    @PostMapping("/executar")
    @Operation(summary = "Executar busca de todos os 12 meses",
               description = "⚠️ DEPRECATED: Use POST /scheduler/execute/previsao-realizacao-receita-multi-mes")
    @Deprecated
    public ResponseEntity<String> executarTodosMeses() {
        try {
            logger.info("Iniciando execução manual multi-mês via endpoint DEPRECATED");
            logger.warning("DEPRECATED: Este endpoint foi movido para /scheduler/execute/previsao-realizacao-receita-multi-mes");

            String resultado = multiMesService.executarManual();
            String deprecationWarning = "⚠️ DEPRECATED: Este endpoint foi movido para /scheduler/execute/previsao-realizacao-receita-multi-mes\n\n" + resultado;

            logger.info("Execução manual multi-mês concluída via endpoint DEPRECATED");
            return ResponseEntity.ok(deprecationWarning);
            
        } catch (Exception e) {
            logger.severe("Erro durante execução manual multi-mês: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("❌ Erro durante execução: " + e.getMessage());
        }
    }

    @PostMapping("/executar-mes/{mes}")
    @Operation(summary = "Executar busca de um mês específico",
               description = "⚠️ DEPRECATED: Use POST /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/{mes}")
    @Deprecated
    public ResponseEntity<String> executarMesEspecifico(
            @Parameter(description = "Número do mês (1-12)", example = "12")
            @PathVariable int mes) {
        
        try {
            if (mes < 1 || mes > 12) {
                return ResponseEntity.badRequest()
                    .body("❌ Mês inválido. Deve estar entre 1 e 12.");
            }
            
            logger.info("Iniciando execução manual para mês " + mes + " via endpoint DEPRECATED");
            logger.warning("DEPRECATED: Este endpoint foi movido para /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/" + mes);

            List<PrevisaoRealizacaoReceitaDTO> resultado = multiMesService.consumirMesEspecifico(mes);

            String resposta = "⚠️ DEPRECATED: Este endpoint foi movido para /scheduler/execute/previsao-realizacao-receita-multi-mes/mes/" + mes + "\n\n" +
                             "✅ Execução do mês " + mes + " concluída!\n" +
                             "Registros processados: " + (resultado != null ? resultado.size() : 0);

            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            logger.severe("Erro durante execução do mês " + mes + ": " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("❌ Erro durante execução do mês " + mes + ": " + e.getMessage());
        }
    }
}
