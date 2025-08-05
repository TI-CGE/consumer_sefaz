package br.gov.se.setc.consumer.controller;

import br.gov.se.setc.consumer.dto.OrdemFornecimentoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/ordem-fornecimento")
@Tag(name = "Ordem de Fornecimento", description = "API para consumo de dados de Ordem de Fornecimento da SEFAZ")
public class SwaggerOrdemFornecimentoController {

    private static final Logger logger = Logger.getLogger(SwaggerOrdemFornecimentoController.class.getName());

    @Autowired
    private ConsumoApiService<OrdemFornecimentoDTO> consumoApiService;
    
    @GetMapping
    @Operation(summary = "Lista todas as ordens de fornecimento", description = "Retorna uma lista com todas as ordens de fornecimento disponíveis.")
    public List<OrdemFornecimentoDTO> listarOrdemFornecimento() {
        try {
            logger.info("Iniciando consumo da API de Ordem de Fornecimento");
            OrdemFornecimentoDTO consumirPersistir = new OrdemFornecimentoDTO();
            List<OrdemFornecimentoDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Ordem de Fornecimento: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
