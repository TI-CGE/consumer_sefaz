package br.gov.se.setc.controller;
import br.gov.se.setc.logging.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/test")
public class TestProgressController {
    @Autowired
    private SimpleLogger simpleLogger;
    @GetMapping("/progress")
    public String testProgress() {
        new Thread(() -> {
            try {
                simpleLogger.consumptionStart("UNIDADE_GESTORA", "Consumindo dados de Unidades Gestoras da SEFAZ");
                Thread.sleep(1000);
                for (int i = 1; i <= 10; i++) {
                    simpleLogger.consumptionProgress("UNIDADE_GESTORA", "processamento", i, 10, "UG_" + (1000 + i));
                    Thread.sleep(800);
                }
                simpleLogger.consumptionEnd("UNIDADE_GESTORA", "10 registros processados com sucesso", 9000);
                Thread.sleep(2000);
                simpleLogger.consumptionStart("CONTRATOS_FISCAIS", "Consumindo dados de Contratos Fiscais");
                Thread.sleep(1000);
                for (int i = 1; i <= 15; i++) {
                    simpleLogger.consumptionProgress("CONTRATOS_FISCAIS", "processamento", i, 15, "CF_" + (2000 + i));
                    Thread.sleep(600);
                }
                simpleLogger.consumptionEnd("CONTRATOS_FISCAIS", "15 registros processados com sucesso", 12000);
                Thread.sleep(2000);
                simpleLogger.consumptionStart("RECEITA", "Consumindo dados de Receitas");
                Thread.sleep(1000);
                for (int i = 1; i <= 8; i++) {
                    simpleLogger.consumptionProgress("RECEITA", "processamento", i, 8, "REC_" + (3000 + i));
                    Thread.sleep(700);
                }
                simpleLogger.consumptionEnd("RECEITA", "8 registros processados com sucesso", 7000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        return "Teste de barras de progresso iniciado! Verifique o terminal para ver as barras de progresso.";
    }
    @GetMapping("/progress-multiple")
    public String testMultipleProgress() {
        new Thread(() -> {
            try {
                simpleLogger.consumptionStart("EMPENHO", "Consumindo dados de Empenhos");
                Thread.sleep(500);
                for (int i = 1; i <= 6; i++) {
                    simpleLogger.consumptionProgress("EMPENHO", "processamento", i, 6, "EMP_" + (4000 + i));
                    Thread.sleep(1200);
                }
                simpleLogger.consumptionEnd("EMPENHO", "6 registros processados", 8000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                simpleLogger.consumptionStart("PAGAMENTO", "Consumindo dados de Pagamentos");
                Thread.sleep(500);
                for (int i = 1; i <= 8; i++) {
                    simpleLogger.consumptionProgress("PAGAMENTO", "processamento", i, 8, "PAG_" + (5000 + i));
                    Thread.sleep(900);
                }
                simpleLogger.consumptionEnd("PAGAMENTO", "8 registros processados", 9000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        return "Teste de múltiplas barras de progresso iniciado! Verifique o terminal.";
    }
}
