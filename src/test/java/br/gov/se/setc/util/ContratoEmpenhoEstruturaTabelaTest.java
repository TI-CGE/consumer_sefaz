package br.gov.se.setc.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

/**
 * Verificação da estrutura da tabela contrato_empenho vs campos do DTO
 */
@SpringBootTest
@ActiveProfiles("dev")
class ContratoEmpenhoEstruturaTabelaTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void verificarEstruturaTabelaVsDTO() {
        System.out.println("=".repeat(80));
        System.out.println("VERIFICAÇÃO ESTRUTURA TABELA vs DTO - CONTRATO_EMPENHO");
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
        System.out.println("\n🔍 COLUNAS ATUAIS DA TABELA contrato_empenho:");
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
              AND table_name = 'contrato_empenho'
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
            "cd_solicitacao_compra",
            "ug_cd", 
            "ds_resumida_solicitacao_compra",
            "cd_licitacao",
            "dt_ano_exercicio",
            "situacao",
            "nm_modalidade_licitacao",
            "crit_julg_licitacao",
            "natureza_objeto_licitacao",
            "ug_se",
            "doc_se",
            "doc_referencia_ne",
            "valor_se",
            "ug_ne",
            "doc_ne",
            "doc_credor_ne",
            "nm_credor_ne",
            "tipo_empenho",
            "vl_original_ne",
            "vltotal_reforco_ne",
            "vltotal_anulado_ne",
            "vltotal_liquidado_ne",
            "vl_total_pago_ne",
            "cd_contrato",
            "tipo_contrato",
            "vl_contrato",
            "dt_inicio_vigencia_contrato",
            "dt_fim_vigencia_contrato",
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
              AND table_name = 'contrato_empenho'
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
