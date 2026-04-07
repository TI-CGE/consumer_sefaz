package br.gov.se.setc.consumer.controller;
import br.gov.se.setc.consumer.dto.TermoDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
/**
 * Controller REST para consumo de dados de Termo (Convênios) do SEFAZ
 * Endpoint base: /termo
 */
@RestController
@RequestMapping("/termo")
@Tag(name = "Termo (Convênios)", description = "API para consumo e gestão de dados de termo (convênios) do SEFAZ")
public class SwaggerTermoController {
    private static final Logger logger = Logger.getLogger(SwaggerTermoController.class.getName());
    @Autowired
    @Qualifier("termoConsumoApiService")
    private ConsumoApiService<TermoDTO> consumoApiService;
    @GetMapping
    @Operation(
        summary = "Consultar dados de Termo (Convênios)",
        description = "Consulta dados de Termo (Convênios) armazenados no banco de dados local com filtros opcionais. " +
                     "Retorna array de objetos com informações sobre convênios e termos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados consultados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<TermoDTO> consultarTermos(
            @Parameter(description = "Código da Unidade Gestora para filtro", example = "161011")
            @RequestParam(required = false) String cdUnidadeGestora,
            @Parameter(description = "Código de Gestão para filtro", example = "00001")
            @RequestParam(required = false) String cdGestao,
            @Parameter(description = "Situação do convênio (A=Ativo, C=Cancelado, etc.)", example = "A")
            @RequestParam(required = false) String situacao,
            @Parameter(description = "Data início da vigência (formato: YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) LocalDate vigenciaDe,
            @Parameter(description = "Data fim da vigência (formato: YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) LocalDate vigenciaAte) {
        try {
            logger.info("Consultando dados de Termo com filtros: cdUnidadeGestora=" + cdUnidadeGestora +
                       ", cdGestao=" + cdGestao + ", situacao=" + situacao +
                       ", vigenciaDe=" + vigenciaDe + ", vigenciaAte=" + vigenciaAte);
            TermoDTO termoDto = new TermoDTO();
            if (cdUnidadeGestora != null && !cdUnidadeGestora.trim().isEmpty()) {
                termoDto.setCdUnidadeGestoraFiltro(cdUnidadeGestora.trim());
            }
            if (cdGestao != null && !cdGestao.trim().isEmpty()) {
                termoDto.setCdGestaoFiltro(cdGestao.trim());
            }
            if (situacao != null && !situacao.trim().isEmpty()) {
                termoDto.setSituacaoFiltro(situacao.trim());
            }
            if (vigenciaDe != null) {
                termoDto.setVigenciaDe(vigenciaDe);
            }
            if (vigenciaAte != null) {
                termoDto.setVigenciaAte(vigenciaAte);
            }
            logger.info("Consulta de dados locais ainda não implementada - retornando lista vazia");
            return List.of();
        } catch (Exception e) {
            logger.severe("Erro ao consultar dados de Termo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @GetMapping("/test")
    @Operation(
        summary = "Consumir dados de Termo (Convênios) da API externa",
        description = "Consome dados da API de Termo (Convênios) do SEFAZ e persiste no banco de dados local. " +
                     "Retorna resumo da operação com quantidade de registros processados e tempo de execução. " +
                     "Este endpoint é útil para testes e execução manual do consumo de dados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados consumidos e persistidos com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("=== INICIANDO TESTE DO ENDPOINT DE TERMO (CONVÊNIOS) ===");
            long startTime = System.currentTimeMillis();
            TermoDTO dto = new TermoDTO();
            List<TermoDTO> result = consumoApiService.consumirPersistir(dto);
            long duration = System.currentTimeMillis() - startTime;
            int recordCount = result != null ? result.size() : 0;
            StringBuilder info = new StringBuilder();
            info.append("=== RESULTADO DO CONSUMO DE TERMO (CONVÊNIOS) ===\n");
            info.append("Status: SUCESSO\n");
            info.append("Endpoint: ").append(dto.getUrl()).append("\n");
            info.append("Tabela destino: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Registros processados: ").append(recordCount).append("\n");
            info.append("Tempo de execução: ").append(duration).append(" ms\n");
            info.append("Tempo de execução: ").append(String.format("%.2f", duration / 1000.0)).append(" segundos\n");
            if (recordCount > 0) {
                info.append("Estratégia: Upsert baseado em cdConvenio (chave natural)\n");
                info.append("Campos principais: cdConvenio, nmConvenio, cdUnidadeGestora, situacao\n");
                info.append("Filtros suportados: cdUnidadeGestora, cdGestao, situacao, vigenciaDe, vigenciaAte\n");
            } else {
                info.append("Nenhum registro retornado pela API externa\n");
            }
            logger.info("Consumo de Termo concluído. Registros processados: " + recordCount +
                       ", Tempo: " + duration + "ms");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Termo: " + e.getMessage());
            e.printStackTrace();
            StringBuilder errorInfo = new StringBuilder();
            errorInfo.append("=== ERRO NO CONSUMO DE TERMO (CONVÊNIOS) ===\n");
            errorInfo.append("Status: ERRO\n");
            errorInfo.append("Mensagem: ").append(e.getMessage()).append("\n");
            errorInfo.append("Tipo: ").append(e.getClass().getSimpleName()).append("\n");
            return ResponseEntity.status(500).body(errorInfo.toString());
        }
    }
    @GetMapping("/info")
    @Operation(
        summary = "Informações do endpoint",
        description = "Retorna informações técnicas sobre o endpoint de Termo (Convênios), " +
                     "incluindo configurações, filtros suportados e estrutura de dados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso")
    })
    public ResponseEntity<String> informacoesEndpoint() {
        try {
            logger.info("Solicitação de informações do endpoint de Termo");
            TermoDTO dto = new TermoDTO();
            StringBuilder info = new StringBuilder();
            info.append("=== INFORMAÇÕES DO ENDPOINT TERMO (CONVÊNIOS) ===\n");
            info.append("Descrição: Consumo de dados de convênios e termos da SEFAZ\n");
            info.append("URL da API: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Método HTTP: GET\n");
            info.append("Formato resposta: JSON Array\n");
            info.append("Chave natural: cdConvenio (Long)\n");
            info.append("Estratégia persistência: Upsert (INSERT ON CONFLICT UPDATE)\n\n");
            info.append("=== FILTROS SUPORTADOS ===\n");
            info.append("- cdUnidadeGestora (String): Código da Unidade Gestora\n");
            info.append("- cdGestao (String): Código de Gestão\n");
            info.append("- situacao (String): Situação do convênio (A=Ativo, C=Cancelado)\n");
            info.append("- vigenciaDe (LocalDate): Data início da vigência\n");
            info.append("- vigenciaAte (LocalDate): Data fim da vigência\n\n");
            info.append("=== CAMPOS PRINCIPAIS ===\n");
            info.append("- cdConvenio: Identificador único do convênio\n");
            info.append("- nmConvenio: Nome/título do convênio\n");
            info.append("- dsObjetoConvenio: Descrição do objeto do convênio\n");
            info.append("- dtCelebracaoConvenio: Data de celebração\n");
            info.append("- dtInicioVigenciaConvenio: Data início da vigência\n");
            info.append("- dtFimVigenciaConvenio: Data fim da vigência\n");
            info.append("- cdConvenioSituacao: Situação atual do convênio\n");
            info.append("- cdUnidadeGestora: Código da Unidade Gestora responsável\n\n");
            info.append("=== ENDPOINTS DISPONÍVEIS ===\n");
            info.append("GET /termo - Consultar dados locais com filtros\n");
            info.append("GET /termo/test - Consumir dados da API externa\n");
            info.append("GET /termo/info - Estas informações\n");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro ao obter informações do endpoint: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro ao obter informações: " + e.getMessage());
        }
    }
}