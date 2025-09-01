package br.gov.se.setc.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

/**
 * Correção final de tipos inconsistentes identificados pelo usuário.
 */
@SpringBootTest
@ActiveProfiles("dev")
class CorrecaoTiposFinaisFixTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void corrigirTiposInconsistentes() {
        System.out.println("=".repeat(80));
        System.out.println("CORREÇÃO FINAL DE TIPOS INCONSISTENTES");
        System.out.println("=".repeat(80));
        
        try {
            // 1. Criar backup
            criarBackup();
            
            // 2. Aplicar correções
            aplicarCorrecoes();
            
            // 3. Verificar resultados
            verificarCorrecoes();
            
            System.out.println("\n✅ Todas as correções foram aplicadas com sucesso!");
            
        } catch (Exception e) {
            System.out.println("\n❌ Erro durante a correção: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CORREÇÃO FINAL CONCLUÍDA");
        System.out.println("=".repeat(80));
    }
    
    private void criarBackup() {
        System.out.println("\n🔄 Criando backup das tabelas...");
        
        try {
            // Backup da tabela pagamento
            jdbcTemplate.execute("DROP TABLE IF EXISTS backup_types_fix.pagamento_final_backup");
            jdbcTemplate.execute("""
                CREATE TABLE backup_types_fix.pagamento_final_backup AS 
                SELECT * FROM consumer_sefaz.pagamento
                """);
            
            // Backup da tabela base_despesa_credor
            jdbcTemplate.execute("DROP TABLE IF EXISTS backup_types_fix.base_despesa_credor_final_backup");
            jdbcTemplate.execute("""
                CREATE TABLE backup_types_fix.base_despesa_credor_final_backup AS 
                SELECT * FROM consumer_sefaz.base_despesa_credor
                """);
            
            System.out.println("✅ Backup criado com sucesso!");
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar backup: " + e.getMessage());
            throw e;
        }
    }
    
    private void aplicarCorrecoes() {
        System.out.println("\n🔧 Aplicando correções...");
        
        try {
            // 1. Corrigir pagamento.cd_fonte_recurso (integer → character varying)
            System.out.println("\n📊 Verificando valores atuais de cd_fonte_recurso:");
            List<Map<String, Object>> fonteRecursoValues = jdbcTemplate.queryForList("""
                SELECT cd_fonte_recurso, COUNT(*) as count
                FROM consumer_sefaz.pagamento 
                WHERE cd_fonte_recurso IS NOT NULL
                GROUP BY cd_fonte_recurso
                ORDER BY count DESC
                LIMIT 5
                """);
            
            if (!fonteRecursoValues.isEmpty()) {
                System.out.println("📋 Valores encontrados:");
                for (Map<String, Object> row : fonteRecursoValues) {
                    System.out.println("  - " + row.get("cd_fonte_recurso") + " (" + row.get("count") + " registros)");
                }
            }
            
            System.out.println("\n🔧 Convertendo cd_fonte_recurso para CHARACTER VARYING...");
            jdbcTemplate.execute("""
                ALTER TABLE consumer_sefaz.pagamento 
                ALTER COLUMN cd_fonte_recurso TYPE CHARACTER VARYING(50)
                """);
            System.out.println("✅ cd_fonte_recurso convertido para CHARACTER VARYING(50)");
            
            // 2. Corrigir base_despesa_credor.sq_empenho (character varying → integer)
            System.out.println("\n📊 Verificando valores não numéricos em sq_empenho:");
            List<Map<String, Object>> nonNumericEmpenho = jdbcTemplate.queryForList("""
                SELECT sq_empenho, COUNT(*) as count
                FROM consumer_sefaz.base_despesa_credor 
                WHERE sq_empenho !~ '^[0-9]+$' AND sq_empenho IS NOT NULL
                GROUP BY sq_empenho
                LIMIT 5
                """);
            
            if (nonNumericEmpenho.isEmpty()) {
                System.out.println("✅ Todos os valores de sq_empenho são numéricos");
            } else {
                System.out.println("⚠️ Encontrados valores não numéricos:");
                for (Map<String, Object> row : nonNumericEmpenho) {
                    System.out.println("  - " + row.get("sq_empenho") + " (" + row.get("count") + " registros)");
                }
            }
            
            System.out.println("\n🔧 Convertendo sq_empenho para INTEGER...");
            jdbcTemplate.execute("""
                ALTER TABLE consumer_sefaz.base_despesa_credor 
                ALTER COLUMN sq_empenho TYPE INTEGER 
                USING CASE 
                    WHEN sq_empenho ~ '^[0-9]+$' THEN sq_empenho::INTEGER
                    ELSE NULL
                END
                """);
            System.out.println("✅ sq_empenho convertido para INTEGER");
            
            // 3. Corrigir base_despesa_credor.cd_tipo_documento (character varying → integer)
            System.out.println("\n📊 Verificando valores não numéricos em cd_tipo_documento:");
            List<Map<String, Object>> nonNumericTipo = jdbcTemplate.queryForList("""
                SELECT cd_tipo_documento, COUNT(*) as count
                FROM consumer_sefaz.base_despesa_credor 
                WHERE cd_tipo_documento !~ '^[0-9]+$' AND cd_tipo_documento IS NOT NULL
                GROUP BY cd_tipo_documento
                LIMIT 5
                """);
            
            if (nonNumericTipo.isEmpty()) {
                System.out.println("✅ Todos os valores de cd_tipo_documento são numéricos");
            } else {
                System.out.println("⚠️ Encontrados valores não numéricos:");
                for (Map<String, Object> row : nonNumericTipo) {
                    System.out.println("  - " + row.get("cd_tipo_documento") + " (" + row.get("count") + " registros)");
                }
            }
            
            System.out.println("\n🔧 Convertendo cd_tipo_documento para INTEGER...");
            jdbcTemplate.execute("""
                ALTER TABLE consumer_sefaz.base_despesa_credor 
                ALTER COLUMN cd_tipo_documento TYPE INTEGER 
                USING CASE 
                    WHEN cd_tipo_documento ~ '^[0-9]+$' THEN cd_tipo_documento::INTEGER
                    ELSE NULL
                END
                """);
            System.out.println("✅ cd_tipo_documento convertido para INTEGER");
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao aplicar correções: " + e.getMessage());
            throw e;
        }
    }
    
    private void verificarCorrecoes() {
        System.out.println("\n🔍 Verificando correções aplicadas...");
        
        try {
            // Verificar tipos após correção
            List<Map<String, Object>> types = jdbcTemplate.queryForList("""
                SELECT 
                    table_name,
                    column_name,
                    data_type,
                    character_maximum_length,
                    numeric_precision
                FROM information_schema.columns 
                WHERE table_schema = 'consumer_sefaz'
                  AND ((table_name = 'pagamento' AND column_name = 'cd_fonte_recurso')
                    OR (table_name = 'base_despesa_credor' AND column_name IN ('sq_empenho', 'cd_tipo_documento')))
                ORDER BY table_name, column_name
                """);
            
            System.out.println("\n📋 Status dos tipos corrigidos:");
            System.out.printf("%-25s %-25s %-20s %-15s %-15s%n", "TABELA", "CAMPO", "TIPO", "TAMANHO", "STATUS");
            System.out.println("-".repeat(100));
            
            for (Map<String, Object> row : types) {
                String table = (String) row.get("table_name");
                String column = (String) row.get("column_name");
                String type = (String) row.get("data_type");
                Object length = row.get("character_maximum_length");
                Object precision = row.get("numeric_precision");
                
                String lengthStr = length != null ? length.toString() : 
                                  precision != null ? precision.toString() : "-";
                
                String status = getExpectedStatus(table, column, type);
                
                System.out.printf("%-25s %-25s %-20s %-15s %s%n", table, column, type, lengthStr, status);
            }
            
            // Contar registros
            System.out.println("\n📊 Estatísticas pós-correção:");
            
            Integer totalPagamento = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM consumer_sefaz.pagamento", Integer.class);
            System.out.println("Total de registros em pagamento: " + totalPagamento);
            
            Integer totalBaseDespesa = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM consumer_sefaz.base_despesa_credor", Integer.class);
            System.out.println("Total de registros em base_despesa_credor: " + totalBaseDespesa);
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao verificar correções: " + e.getMessage());
            throw e;
        }
    }
    
    private String getExpectedStatus(String table, String column, String type) {
        if ("pagamento".equals(table) && "cd_fonte_recurso".equals(column)) {
            return "character varying".equals(type) ? "✅ CORRETO (STRING)" : "❌ INCORRETO";
        } else if ("base_despesa_credor".equals(table) && "sq_empenho".equals(column)) {
            return "integer".equals(type) ? "✅ CORRETO (INTEGER)" : "❌ INCORRETO";
        } else if ("base_despesa_credor".equals(table) && "cd_tipo_documento".equals(column)) {
            return "integer".equals(type) ? "✅ CORRETO (INTEGER)" : "❌ INCORRETO";
        }
        return "❓ DESCONHECIDO";
    }
}
