package br.gov.se.setc.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidacaoUtil<T extends EndpontSefaz> {
    private final JdbcTemplate jdbcTemplate;

    public ValidacaoUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isPresenteBanco(T endpointInstance) {
        if (endpointInstance.getDtAnoPadrao() == null) {
            String sql = "select ( coalesce(count(*),0) > 0 ) as valor from " + endpointInstance.getTabelaBanco()
                    + "  LIMIT 1;";
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class));
        } else {
            String sql = "select ( coalesce(count( " + endpointInstance.getDtAnoPadrao() + "),0) > 0 ) as valor from "
                    + endpointInstance.getTabelaBanco() + "  LIMIT 1;";
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class));
        }
    }

    public boolean isEndpointIdependenteUGData(T mapper) {
        return mapper.getCamposParametrosAtual("", this) == null || mapper.getCamposParametrosAtual("", this).isEmpty();
    }

    public Short getAnoAtual() {
        String sql = "SELECT EXTRACT(YEAR FROM CURRENT_DATE) AS ano_atual";
        return jdbcTemplate.queryForObject(sql, Short.class);
    }

    public Short getMesAtual() {
        String sql = "SELECT EXTRACT(MONTH FROM CURRENT_DATE) AS mes_atual";
        return jdbcTemplate.queryForObject(sql, Short.class);
    }

    public List<int[]> getUltimos2Meses() {
        List<int[]> out = new ArrayList<>();
        Short anoAtual = getAnoAtual();
        Short mesAtual = getMesAtual();
        if (anoAtual == null || mesAtual == null) {
            return out;
        }
        int ano = anoAtual.intValue();
        int mes = mesAtual.intValue();
        if (mes >= 2) {
            out.add(new int[] { ano, mes - 1 });
            out.add(new int[] { ano, mes });
        } else {
            out.add(new int[] { ano - 1, 12 });
            out.add(new int[] { ano, 1 });
        }
        return out;
    }

    private Boolean hasDataInTable(String tableName, String columnName) {
        String sql = "Select count(" + columnName + ") > 0 from " + tableName + " LIMIT 1;";
        return jdbcTemplate.queryForObject(sql, Boolean.class);
    }

    public List<String> getUgs() {
        try {
            String sql = " select distinct cd_unidade_gestora from consumer_sefaz.unidade_gestora where sg_tipo_unidade_gestora = 'E' ";
            List<String> result = jdbcTemplate.queryForList(sql, String.class);
            if (result.isEmpty()) {
                List<String> testUgs = new ArrayList<>();
                testUgs.add("001");
                testUgs.add("002");
                return testUgs;
            }
            return result;
        } catch (Exception e) {
            System.err.println("Warning: Database query failed in getUgs(), using test data: " + e.getMessage());
            List<String> testUgs = new ArrayList<>();
            testUgs.add("001");
            testUgs.add("002");
            return testUgs;
        }
    }

    public List<String> cdGestao() {
        String sql = "SELECT DISTINCT cd_gestao FROM consumer_sefaz.empenho WHERE cd_gestao IS NOT NULL AND TRIM(cd_gestao) != ''";
        List<String> raw = jdbcTemplate.queryForList(sql, String.class);
        return raw != null
                ? raw.stream().filter(s -> s != null && !s.trim().isEmpty()).distinct().collect(Collectors.toList())
                : new ArrayList<>();
    }

    public List<String> cdGestaoSeed() {
        try {
            String sql = """
                    SELECT DISTINCT vugl.cd_gestao
                    FROM spt.vw_unidades_gestora_liq vugl
                    WHERE vugl.cd_gestao IS NOT NULL
                      AND vugl.cd_gestao != ''
                    ORDER BY vugl.cd_gestao
                    """;
            List<String> raw = jdbcTemplate.queryForList(sql, String.class);
            if (raw != null && !raw.isEmpty()) {
                return raw.stream().filter(s -> s != null && !s.trim().isEmpty()).distinct()
                        .collect(Collectors.toList());
            }
        } catch (Exception ignored) {
        }
        return cdGestao();
    }

    public List<String> cdGestaoPorUgAno(String cdUnidadeGestora, Short ano) {
        if (cdUnidadeGestora == null || ano == null) {
            return new ArrayList<>();
        }
        try {
            String sql = """
                    SELECT DISTINCT vugl.cd_gestao
                    FROM spt.vw_unidades_gestora_liq vugl
                    WHERE vugl.cd_unidade_gestora = ?
                      AND vugl.cd_gestao IS NOT NULL
                      AND vugl.cd_gestao != ''
                    ORDER BY vugl.cd_gestao
                    """;
            List<String> raw = jdbcTemplate.queryForList(sql, String.class, cdUnidadeGestora);
            if (raw != null && !raw.isEmpty()) {
                return raw.stream().filter(s -> s != null && !s.trim().isEmpty()).distinct()
                        .collect(Collectors.toList());
            }
        } catch (Exception ignored) {
        }
        String sqlEmpenho = "SELECT DISTINCT cd_gestao FROM consumer_sefaz.empenho WHERE cd_unidade_gestora = ? AND dt_ano_exercicio_ctb = ? AND cd_gestao IS NOT NULL AND TRIM(cd_gestao) != '' ORDER BY cd_gestao";
        List<String> raw = jdbcTemplate.queryForList(sqlEmpenho, String.class, cdUnidadeGestora, ano.intValue());
        return raw != null
                ? raw.stream().filter(s -> s != null && !s.trim().isEmpty()).distinct().collect(Collectors.toList())
                : new ArrayList<>();
    }

    public Boolean getValidaContratosFiscais() {
        return hasDataInTable("sco.contratos_fiscais", "dt_ano_exercicio");
    }

    public Boolean getValidaEmpenho() {
        return hasDataInTable("sco.empenhos", "dt_ano_exercicio_ctb");
    }

    public Boolean getValidaOrdemFornecimento() {
        return hasDataInTable("consumer_sefaz.ordem_fornecimento", "dt_recebimento");
    }

    public Boolean getValidaPrevisaoRealizacaoReceita() {
        return hasDataInTable("sco.previsao_realizacao_receita", "dt_ano_exercicio_ctb");
    }

    public Boolean getValidaLiquidacao() {
        return hasDataInTable("sco.liquidacao", "dt_liquidacao");
    }

    public Boolean getValidaDespesaConvenio() {
        return hasDataInTable("sco.despesa_convenio", "dt_lancamento_convenio");
    }

    public Boolean getValidaReceitaConvenio() {
        return hasDataInTable("sco.receita_convenio", "dt_lancamento_convenio");
    }

    public Boolean getValidaDespesaDetalhada() {
        return hasDataInTable("sco.classificacao_orcamentaria", "nu_mes");
    }

    public Boolean getValidaContrato() {
        return hasDataInTable("sco.contrato", "nu_documento");
    }

    public Boolean getValidaConvenio() {
        return hasDataInTable("sco.convenio", "cd_convenio");
    }

    public Boolean getValidaPagamento() {
        return hasDataInTable("sco.pagamento", "cd_unidade_gestora");
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}