# Teste das Barras de Progresso - Sistema SEFAZ

## Como Testar

### 1. **Iniciar a Aplicação**
```bash
mvn spring-boot:run
```

### 2. **Acessar o Monitor de Logs**
- Abrir: http://localhost:8080
- Verificar se a conexão SSE está ativa (indicador verde)

### 3. **Executar um Consumo**
Executar qualquer um dos métodos de consumo disponíveis:

#### Opção A: Via Scheduler (Recomendado)
- Descomentar uma das execuções no `ContractConsumptionScheduler.java`
- Reiniciar a aplicação
- Observar as barras de progresso aparecerem automaticamente

#### Opção B: Via Endpoint REST
```bash
# Exemplo: Consumir Unidades Gestoras
curl -X POST http://localhost:8080/api/consume/unidade-gestora
```

### 4. **O que Observar**

#### No Monitor de Logs:
1. **Seção de Progresso aparece** quando um consumo inicia
2. **Barras de progresso** são criadas dinamicamente
3. **Progresso atualiza em tempo real** conforme o processamento
4. **Barras ficam verdes** quando completam
5. **Barras desaparecem** após alguns segundos

#### Tipos de Progresso Esperados:
- **SCHEDULER**: Progresso geral (1/6, 2/6, etc.)
- **Unidades Gestoras**: Progresso por UG processada
- **Contratos**: Progresso por lote de dados
- **Receitas**: Progresso por período
- **Pagamentos**: Progresso por transação

### 5. **Logs Estruturados Gerados**

#### Início de Consumo:
```
CONSUMPTION_START | Unidades Gestoras | Iniciando consumo de dados da API SEFAZ
```

#### Progresso:
```
PROGRESS_BAR | Unidades Gestoras | Processando UGs | 5/20 | 25% | UG: 12345
PROGRESS_BAR | SCHEDULER | Executando entidades | 2/6 | 33% | Consulta Gerencial
```

#### Finalização:
```
CONSUMPTION_END | Unidades Gestoras | 150 registros processados | 2m 30s
```

### 6. **Funcionalidades da Interface**

#### Controles:
- **Minimizar/Expandir**: Botão no header da seção de progresso
- **Auto-hide**: Seção desaparece quando não há consumos ativos
- **Tema**: Barras se adaptam ao tema dark/light

#### Estados Visuais:
- **Azul**: Progresso normal
- **Verde**: Concluído com sucesso
- **Vermelho**: Erro durante processamento
- **Animação**: Efeito shimmer durante progresso ativo

### 7. **Troubleshooting**

#### Se as barras não aparecem:
1. Verificar se SSE está conectado (indicador verde)
2. Verificar console do navegador para erros JavaScript
3. Verificar se os logs estruturados estão sendo gerados no backend

#### Se o progresso não atualiza:
1. Verificar se `SimpleLogger` está sendo injetado corretamente
2. Verificar se os métodos `consumptionProgress()` estão sendo chamados
3. Verificar se o formato dos logs está correto

### 8. **Personalização**

#### Adicionar Novos Tipos de Progresso:
1. No backend: Chamar `simpleLogger.consumptionProgress()`
2. No frontend: O sistema detecta automaticamente novos tipos

#### Modificar Aparência:
- Editar estilos em `styles.css` na seção "Progress Section"
- Cores, animações e layout são totalmente customizáveis

## Exemplo de Uso Completo

```java
// No seu serviço de consumo
simpleLogger.consumptionStart("Meu Consumo", "Processando dados importantes");

for (int i = 0; i < total; i++) {
    // Processar item
    processItem(items.get(i));
    
    // Atualizar progresso
    simpleLogger.consumptionProgress("Meu Consumo", "Processando itens", 
                                   i + 1, total, "Item: " + items.get(i).getId());
}

simpleLogger.consumptionEnd("Meu Consumo", total + " itens processados", duration);
```

## Resultado Esperado

✅ Interface moderna com barras de progresso em tempo real
✅ Feedback visual imediato do progresso dos consumos
✅ Informações detalhadas sobre cada etapa
✅ Design responsivo e acessível
✅ Integração perfeita com o sistema de logs existente
