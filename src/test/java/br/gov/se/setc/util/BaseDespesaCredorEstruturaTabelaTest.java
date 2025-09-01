package br.gov.se.setc.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

/**
 * Verificação da estrutura da tabela base_despesa_credor vs campos do DTO
 */
@SpringBootTest
@ActiveProfiles("dev")
class BaseDespesaCredorEstruturaTabelaTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void verificarEstruturaTabelaVsDTO() {
        System.out.println("=".repeat(80));
        System.out.println("VERIFICAÇÃO ESTRUTURA TABELA vs DTO - BASE_DESPESA_CREDOR");
        System.out.println("=".repeat(80));
        
        // Verificar colunas da tabela
        verificarColunasTabela();
        
        // Comparar com campos do DTO
        compararComDTO();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("VERIFICAÇÃO CONCLUÍDA");
        System.out.println("=".repeat(80));
    }
    
    private void verificarColunasTabela() {
        System.out.println("\n🔍 COLUNAS ATUAIS DA TABELA base_despesa_credor:");
        System.out.println("-".repeat(80));
        
        String query = """
            SELECT 
                column_name,
                data_type,
                character_maximum_length,
                numeric_precision,
                numeric_scale,
                is_nullable
            FROM information_schema.columns 
            WHERE table_schema = 'consumer_sefaz' 
              AND table_name = 'base_despesa_credor'
            ORDER BY ordinal_position
            """;
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            
            System.out.printf("%-35s %-20s %-10s %-10s %-10s%n", 
                "COLUNA", "TIPO", "TAMANHO", "PRECISÃO", "NULLABLE");
            System.out.println("-".repeat(85));
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                Object maxLength = column.get("character_maximum_length");
                Object precision = column.get("numeric_precision");
                Object scale = column.get("numeric_scale");
                String nullable = (String) column.get("is_nullable");
                
                String lengthStr = maxLength != null ? maxLength.toString() : "-";
                String precisionStr = precision != null ? precision.toString() : "-";
                if (scale != null && !scale.toString().equals("0")) {
                    precisionStr += "," + scale.toString();
                }
                
                System.out.printf("%-35s %-20s %-10s %-10s %-10s%n", 
                    columnName, dataType, lengthStr, precisionStr, nullable);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao verificar colunas: " + e.getMessage());
        }
    }
    
    private void compararComDTO() {
        System.out.println("\n📋 CAMPOS DO DTO vs COLUNAS DA TABELA:");
        System.out.println("-".repeat(80));
        
        // Campos que o DTO está tentando inserir (baseado no erro SQL)
        String[] camposDTO = {
            "cd_gestao",
            "ug_cd", 
            "dt_ano_exercicio",
            "sq_empenho",
            "nu_documento_pessoa",
            "cd_tipo_documento",
            "nm_razao_social_pessoa",
            "dh_lancamento_empenho",
            "dt_geracao_empenho",
            "vl_original_empenho",
            "vl_total_reforcado_empenho",
            "vl_total_anulado_empenho",
            "vl_empenhado_atual",
            "vl_total_liquidado_empenho",
            "vl_total_pago_empenho",
            "nm_modalidade_licitacao",
            "cd_licitacao",
            "nm_item_material_servico",
            "qt_item_solicitacao_empenho",
            "vl_unitario_item_solicitacao_empenho",
            "qt_reforcada",
            "vl_total_itens",
            "created_at",
            "updated_at"
        };
        
        System.out.printf("%-35s %-15s%n", "CAMPO DTO", "STATUS");
        System.out.println("-".repeat(50));
        
        for (String campo : camposDTO) {
            boolean existe = verificarSeColunaExiste(campo);
            String status = existe ? "✅ EXISTE" : "❌ NÃO EXISTE";
            System.out.printf("%-35s %s%n", campo, status);
        }
    }
    
    private boolean verificarSeColunaExiste(String nomeColuna) {
        String query = """
            SELECT COUNT(*) 
            FROM information_schema.columns 
            WHERE table_schema = 'consumer_sefaz' 
              AND table_name = 'base_despesa_credor'
              AND column_name = ?
            """;
        
        try {
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, nomeColuna);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
