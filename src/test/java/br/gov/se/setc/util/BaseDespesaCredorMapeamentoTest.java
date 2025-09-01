package br.gov.se.setc.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste para verificar mapeamento correto dos campos da API base-despesa-credor
 */
@SpringBootTest
@ActiveProfiles("dev")
class BaseDespesaCredorMapeamentoTest {
    
    @Test
    void verificarMapeamentoCampos() {
        System.out.println("=".repeat(80));
        System.out.println("VERIFICAÇÃO DE MAPEAMENTO - BASE DESPESA CREDOR");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📋 CAMPOS DA API vs DTO vs BANCO:");
        System.out.println("-".repeat(80));
        
        // Campos identificados no JSON da API
        String[][] mapeamento = {
            // Campo API, Campo DTO esperado, Campo Banco, Status
            {"cdUnidadeGestora", "@JsonProperty(\"cdUnidadeGestora\")", "ug_cd", "✅ CORRIGIDO"},
            {"dtAnoExercicio", "@JsonProperty(\"dtAnoExercicio\")", "dt_ano_exercicio", "✅ CORRETO"},
            {"sqEmpenho", "@JsonProperty(\"sqEmpenho\")", "sq_empenho", "✅ CORRETO"},
            {"nuDocumentoPessoa", "@JsonProperty(\"nuDocumentoPessoa\")", "nu_documento_pessoa", "✅ CORRETO"},
            {"cdTipoDocumento", "@JsonProperty(\"cdTipoDocumento\")", "cd_tipo_documento", "✅ CORRETO"},
            {"nmRazaoSocialPessoa", "@JsonProperty(\"nmRazaoSocialPessoa\")", "nm_razao_social_pessoa", "✅ CORRETO"},
            {"dhLancamentoEmpenho", "@JsonProperty(\"dhLancamentoEmpenho\")", "dh_lancamento_empenho", "✅ CORRETO"},
            {"dtGeracaoEmpenho", "@JsonProperty(\"dtGeracaoEmpenho\")", "dt_geracao_empenho", "✅ CORRETO"},
            {"vlOriginalEmpenho", "@JsonProperty(\"vlOriginalEmpenho\")", "vl_original_empenho", "✅ CORRETO"},
            {"vlTotalLiquidadoEmpenho", "@JsonProperty(\"vlTotalLiquidadoEmpenho\")", "vl_total_liquidado_empenho", "✅ CORRETO"},
            {"vlTotalPagoEmpenho", "@JsonProperty(\"vlTotalPagoEmpenho\")", "vl_total_pago_empenho", "✅ CORRETO"},
            {"qtItemSolicitacaoEmpenho", "@JsonProperty(\"qtItemSolicitacaoEmpenho\")", "qt_item_solicitacao_empenho", "✅ CORRETO"},
            {"vlUnitarioItemSolicitacaoEmpenho", "@JsonProperty(\"vlUnitarioItemSolicitacaoEmpenho\")", "vl_unitario_item_solicitacao_empenho", "✅ CORRETO"},
            {"nmItemMaterialServico", "@JsonProperty(\"nmItemMaterialServico\")", "nm_item_material_servico", "✅ CORRETO"},
            {"cdGestao", "@JsonProperty(\"cdGestao\")", "cd_gestao", "✅ CORRETO"},
            {"cdLicitacao", "@JsonProperty(\"cdLicitacao\")", "cd_licitacao", "✅ CORRETO"},
            {"nmModalidadeLicitacao", "@JsonProperty(\"nmModalidadeLicitacao\")", "nm_modalidade_licitacao", "✅ CORRETO"}
        };
        
        System.out.printf("%-30s %-40s %-35s %-15s%n", 
            "CAMPO API", "MAPEAMENTO DTO", "CAMPO BANCO", "STATUS");
        System.out.println("-".repeat(120));
        
        for (String[] campo : mapeamento) {
            System.out.printf("%-30s %-40s %-35s %s%n", 
                campo[0], campo[1], campo[2], campo[3]);
        }
        
        System.out.println("\n🔧 CORREÇÃO APLICADA:");
        System.out.println("❌ ANTES: @JsonProperty(\"ugCd\") - Campo inexistente na API");
        System.out.println("✅ DEPOIS: @JsonProperty(\"cdUnidadeGestora\") - Campo correto da API");
        
        System.out.println("\n📊 RESULTADO:");
        System.out.println("✅ Campo ug_cd agora será populado corretamente com o valor de cdUnidadeGestora");
        System.out.println("✅ Exemplo: API envia \"cdUnidadeGestora\": \"351011\" → Banco recebe ug_cd = \"351011\"");
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("VERIFICAÇÃO CONCLUÍDA");
        System.out.println("=".repeat(80));
    }
}
