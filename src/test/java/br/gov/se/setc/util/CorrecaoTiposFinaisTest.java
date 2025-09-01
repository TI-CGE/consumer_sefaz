package br.gov.se.setc.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

/**
 * Análise e correção final de tipos inconsistentes identificados pelo usuário.
 */
@SpringBootTest
@ActiveProfiles("dev")
class CorrecaoTiposFinaisTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void analisarTiposInconsistentes() {
        System.out.println("=".repeat(80));
        System.out.println("ANÁLISE DE TIPOS INCONSISTENTES - CORREÇÃO FINAL");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📋 INCONSISTÊNCIAS IDENTIFICADAS PELO USUÁRIO:");
        System.out.println("1. pagamento.cdFonteDeRecurso = STRING (API) vs atual no banco");
        System.out.println("2. base_despesa_credor.sqEmpenho = INTEGER (API) vs atual no banco");
        System.out.println("3. base_despesa_credor.cdTipoDocumento = INTEGER (API) vs atual no banco");
        System.out.println("4. ordem_fornecimento.cdUnidadeGestora = STRING (API) vs atual no banco");
        
        // Analisar cada tabela
        analisarTabelaPagamento();
        analisarTabelaBaseDespesaCredor();
        analisarTabelaOrdemFornecimento();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE CONCLUÍDA");
        System.out.println("=".repeat(80));
    }
    
    private void analisarTabelaPagamento() {
        System.out.println("\n🔍 ANALISANDO TABELA: pagamento");
        System.out.println("-".repeat(50));
        
        String query = """
            SELECT 
                column_name,
                data_type,
                character_maximum_length,
                numeric_precision,
                numeric_scale
            FROM information_schema.columns 
            WHERE table_schema = 'consumer_sefaz' 
              AND table_name = 'pagamento'
              AND column_name IN ('cd_fonte_recurso', 'cd_fonte_de_recurso')
            ORDER BY column_name
            """;
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            
            if (columns.isEmpty()) {
                System.out.println("❌ Campo cd_fonte_recurso/cd_fonte_de_recurso não encontrado");
                return;
            }
            
            System.out.printf("%-25s %-20s %-15s %-15s%n", "CAMPO", "TIPO ATUAL", "TAMANHO", "STATUS");
            System.out.println("-".repeat(75));
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                Object maxLength = column.get("character_maximum_length");
                
                String lengthStr = maxLength != null ? maxLength.toString() : "-";
                String status = "character varying".equals(dataType) ? "✅ CORRETO (STRING)" : "❌ INCORRETO";
                
                System.out.printf("%-25s %-20s %-15s %s%n", columnName, dataType, lengthStr, status);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao analisar tabela pagamento: " + e.getMessage());
        }
    }
    
    private void analisarTabelaBaseDespesaCredor() {
        System.out.println("\n🔍 ANALISANDO TABELA: base_despesa_credor");
        System.out.println("-".repeat(50));
        
        String query = """
            SELECT 
                column_name,
                data_type,
                character_maximum_length,
                numeric_precision,
                numeric_scale
            FROM information_schema.columns 
            WHERE table_schema = 'consumer_sefaz' 
              AND table_name = 'base_despesa_credor'
              AND column_name IN ('sq_empenho', 'cd_tipo_documento')
            ORDER BY column_name
            """;
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            
            if (columns.isEmpty()) {
                System.out.println("❌ Campos sq_empenho/cd_tipo_documento não encontrados");
                return;
            }
            
            System.out.printf("%-25s %-20s %-15s %-15s%n", "CAMPO", "TIPO ATUAL", "TAMANHO", "STATUS");
            System.out.println("-".repeat(75));
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                Object maxLength = column.get("character_maximum_length");
                Object precision = column.get("numeric_precision");
                
                String lengthStr = maxLength != null ? maxLength.toString() : 
                                  precision != null ? precision.toString() : "-";
                
                String status;
                if ("sq_empenho".equals(columnName)) {
                    status = "bigint".equals(dataType) || "integer".equals(dataType) ? "✅ CORRETO (INTEGER)" : "❌ INCORRETO";
                } else if ("cd_tipo_documento".equals(columnName)) {
                    status = "integer".equals(dataType) ? "✅ CORRETO (INTEGER)" : "❌ INCORRETO";
                } else {
                    status = "❓ DESCONHECIDO";
                }
                
                System.out.printf("%-25s %-20s %-15s %s%n", columnName, dataType, lengthStr, status);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao analisar tabela base_despesa_credor: " + e.getMessage());
        }
    }
    
    private void analisarTabelaOrdemFornecimento() {
        System.out.println("\n🔍 ANALISANDO TABELA: ordem_fornecimento");
        System.out.println("-".repeat(50));
        
        String query = """
            SELECT 
                column_name,
                data_type,
                character_maximum_length,
                numeric_precision,
                numeric_scale
            FROM information_schema.columns 
            WHERE table_schema = 'consumer_sefaz' 
              AND table_name = 'ordem_fornecimento'
              AND column_name = 'cd_unidade_gestora'
            ORDER BY column_name
            """;
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(query);
            
            if (columns.isEmpty()) {
                System.out.println("❌ Campo cd_unidade_gestora não encontrado");
                return;
            }
            
            System.out.printf("%-25s %-20s %-15s %-15s%n", "CAMPO", "TIPO ATUAL", "TAMANHO", "STATUS");
            System.out.println("-".repeat(75));
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                Object maxLength = column.get("character_maximum_length");
                
                String lengthStr = maxLength != null ? maxLength.toString() : "-";
                String status = "character varying".equals(dataType) ? "✅ CORRETO (STRING)" : "❌ INCORRETO";
                
                System.out.printf("%-25s %-20s %-15s %s%n", columnName, dataType, lengthStr, status);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao analisar tabela ordem_fornecimento: " + e.getMessage());
        }
    }
}
