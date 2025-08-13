package br.gov.se.setc.consumer.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.se.setc.consumer.dto.PagamentoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamentos", description = "API para consumo e gestão de dados de pagamentos do SEFAZ")
public class SwaggerPagamentoController {

    private static final Logger logger = Logger.getLogger(SwaggerPagamentoController.class.getName());
    private final ConsumoApiService<PagamentoDTO> consumoApiService;

    public SwaggerPagamentoController(
            @Qualifier("pagamentoConsumoApiService") ConsumoApiService<PagamentoDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    @Operation(
        summary = "Consumir e listar pagamentos",
        description = "Consome dados de pagamentos da API de transparência SEFAZ e persiste no banco de dados local. " +
                     "Retorna uma lista com todos os pagamentos processados, incluindo informações sobre " +
                     "valores pagos, credores, situação do pagamento e datas de lançamento.",
        tags = {"Pagamentos"}
    )
    public List<PagamentoDTO> listarPagamento() {
        try {
            logger.info("Iniciando consumo da API de Pagamento");
            PagamentoDTO consumirPersistir = new PagamentoDTO();
            List<PagamentoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Pagamento: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Teste básico do endpoint", description = "Retorna informações básicas para teste.")
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Pagamento");
            PagamentoDTO dto = new PagamentoDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Pagamento: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}
