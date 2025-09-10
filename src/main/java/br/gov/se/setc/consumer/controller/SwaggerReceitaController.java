package br.gov.se.setc.consumer.controller;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.se.setc.consumer.dto.ReceitaDTO;
import br.gov.se.setc.consumer.service.ConsumoApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/receita")
@Tag(name = "Receitas", description = "API para consumo e gestão de dados de receitas de convênios do SEFAZ")
public class SwaggerReceitaController {
    private static final Logger logger = Logger.getLogger(SwaggerReceitaController.class.getName());
    private final ConsumoApiService<ReceitaDTO> consumoApiService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public SwaggerReceitaController(
            @Qualifier("receitaConsumoApiService") ConsumoApiService<ReceitaDTO> consumoApiService
    ) {
        this.consumoApiService = consumoApiService;
    }
    @GetMapping
    @Operation(summary = "Lista todas as receitas de convênios", description = "Retorna uma lista com todas as receitas de convênios disponíveis.")
    public List<ReceitaDTO> listarReceita() {
        try {
            logger.info("Iniciando consumo da API de Receita");
            ReceitaDTO consumirPersistir = new ReceitaDTO();
            List<ReceitaDTO> result = consumoApiService.consumirPersistir(consumirPersistir);
            logger.info("Consumo concluído. Retornando " + (result != null ? result.size() : 0) + " registros");
            return result;
        } catch (Exception e) {
            logger.severe("Erro ao consumir API de Receita: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
  }
    @GetMapping("/test")
    @Operation(summary = "Teste básico do endpoint", description = "Retorna informações básicas para teste.")
    public ResponseEntity<String> testeEndpoint() {
        try {
            logger.info("Teste do endpoint de Receita");
            ReceitaDTO dto = new ReceitaDTO();
            StringBuilder info = new StringBuilder();
            info.append("Endpoint funcionando!\n");
            info.append("URL configurada: ").append(dto.getUrl()).append("\n");
            info.append("Tabela banco: ").append(dto.getTabelaBanco()).append("\n");
            info.append("Data inicial filtro: ").append(dto.getNomeDataInicialPadraoFiltro()).append("\n");
            info.append("Data final filtro: ").append(dto.getNomeDataFinalPadraoFiltro()).append("\n");
            info.append("Ano padrão: ").append(dto.getDtAnoPadrao()).append("\n");
            logger.info("Teste concluído com sucesso");
            return ResponseEntity.ok(info.toString());
        } catch (Exception e) {
            logger.severe("Erro no teste do endpoint de Receita: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
    @GetMapping("/check-duplicates")
    @Operation(summary = "Verifica duplicatas na tabela", description = "Retorna estatísticas sobre duplicatas na tabela de receita")
    public ResponseEntity<Map<String, Object>> checkDuplicates() {
        Map<String, Object> response = new HashMap<>();
        try {
            String sqlTotal = "SELECT COUNT(*) FROM consumer_sefaz.receita";
            Long totalRegistros = jdbcTemplate.queryForObject(sqlTotal, Long.class);
            String sqlUnicos = "SELECT COUNT(DISTINCT cd_convenio) FROM consumer_sefaz.receita";
            Long registrosUnicos = jdbcTemplate.queryForObject(sqlUnicos, Long.class);
            boolean temDuplicatas = totalRegistros > registrosUnicos;
            String sqlMesAtual = "SELECT COUNT(*) FROM consumer_sefaz.receita WHERE " +
                               "EXTRACT(YEAR FROM dt_celebracao_convenio) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                               "AND EXTRACT(MONTH FROM dt_celebracao_convenio) = EXTRACT(MONTH FROM CURRENT_DATE)";
            Long registrosMesAtual = jdbcTemplate.queryForObject(sqlMesAtual, Long.class);
            String sqlDistribuicao = "SELECT " +
                                   "EXTRACT(YEAR FROM dt_celebracao_convenio) as ano, " +
                                   "EXTRACT(MONTH FROM dt_celebracao_convenio) as mes, " +
                                   "COUNT(*) as quantidade " +
                                   "FROM consumer_sefaz.receita " +
                                   "GROUP BY EXTRACT(YEAR FROM dt_celebracao_convenio), EXTRACT(MONTH FROM dt_celebracao_convenio) " +
                                   "ORDER BY ano DESC, mes DESC LIMIT 10";
            List<Map<String, Object>> distribuicao = jdbcTemplate.queryForList(sqlDistribuicao);
            response.put("total_registros", totalRegistros);
            response.put("registros_unicos_por_convenio", registrosUnicos);
            response.put("tem_duplicatas", temDuplicatas);
            response.put("registros_mes_atual", registrosMesAtual);
            response.put("distribuicao_por_mes_ano", distribuicao);
            response.put("timestamp", LocalDateTime.now());
            if (temDuplicatas) {
                response.put("duplicatas_encontradas", totalRegistros - registrosUnicos);
                String sqlDuplicatas = "SELECT cd_convenio, COUNT(*) as quantidade " +
                                     "FROM consumer_sefaz.receita " +
                                     "GROUP BY cd_convenio " +
                                     "HAVING COUNT(*) > 1 " +
                                     "ORDER BY quantidade DESC LIMIT 5";
                List<Map<String, Object>> duplicatasEspecificas = jdbcTemplate.queryForList(sqlDuplicatas);
                response.put("exemplos_duplicatas", duplicatasEspecificas);
            }
        } catch (Exception e) {
            response.put("erro", "Erro ao verificar duplicatas: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }
}