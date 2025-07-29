# Sistema de Logging Simplificado

## Visão Geral

O sistema de logging foi completamente simplificado para atender aos requisitos de:
- **Um arquivo principal** (`application.log`) com todas as operações
- **Um arquivo de erro** (`errors.log`) dedicado apenas para erros
- **Formato consistente e legível** em todos os logs
- **Informações essenciais**: steps, contagens de dados, timestamps, status, contexto

## Estrutura Simplificada

### Arquivos de Log
```
logs/
├── application.log     # Log principal com todas as operações
└── errors.log         # Log dedicado apenas para erros
```

### Componentes Principais

#### 1. UnifiedLogger
- **Localização**: `src/main/java/br/gov/se/setc/logging/UnifiedLogger.java`
- **Função**: Logger único que substitui todos os loggers especializados
- **Métodos principais**:
  - `logOperationStart()` - Início de operação
  - `logOperationSuccess()` - Sucesso com contagem de dados e duração
  - `logOperationError()` - Erro com detalhes e stack trace
  - `logDataProcessing()` - Processamento de dados com contadores
  - `logApiCall()` - Chamadas de API com métricas
  - `logDatabaseOperation()` - Operações de banco de dados
  - `logApplicationEvent()` - Eventos da aplicação

#### 2. @LogOperation
- **Localização**: `src/main/java/br/gov/se/setc/logging/annotation/LogOperation.java`
- **Função**: Anotação única que substitui @LogExecution, @LogPerformance, @LogApiCall
- **Parâmetros**:
  - `operation` - Nome da operação (obrigatório)
  - `component` - Componente responsável (opcional)
  - `logParameters` - Se deve logar parâmetros
  - `logResult` - Se deve logar resultado
  - `slowOperationThresholdMs` - Threshold para operação lenta
  - `includeDataCount` - Se deve incluir contagem de dados

#### 3. LoggingAspect Simplificado
- **Localização**: `src/main/java/br/gov/se/setc/logging/aspect/LoggingAspect.java`
- **Função**: AOP simplificado que intercepta @LogOperation
- **Funcionalidades**:
  - Log automático de início/sucesso/erro
  - Extração automática de contagem de dados
  - Configuração automática de correlation ID
  - Detecção de operações lentas

#### 4. MDCUtil Simplificado
- **Localização**: `src/main/java/br/gov/se/setc/logging/util/MDCUtil.java`
- **Função**: Gerenciamento de contexto distribuído simplificado
- **Campos essenciais**:
  - `correlationId` - ID de correlação (8 caracteres)
  - `component` - Componente atual
  - `operation` - Operação atual

## Formato dos Logs

### Log Principal (application.log)
```
2025-07-29 10:33:25.466 INFO [abc12345] [CONTRACT_CONSUMER] [CONSUME_CONTRACTS] - 🚀 INICIANDO CONSUME_CONTRACTS CONTRACT_CONSUMER | UG: 001
2025-07-29 10:33:26.123 INFO [abc12345] [CONTRACT_CONSUMER] [CONSUME_CONTRACTS] - ✅ SUCESSO CONSUME_CONTRACTS | DATA_COUNT: 150 | DURATION: 657ms | STATUS: SUCCESS
2025-07-29 10:33:26.124 INFO [abc12345] [API_CLIENT] [API_CALL] - 🌐 API SUCESSO GET /api/contracts | STATUS: 200 | DURATION: 245ms | REQUEST: 1.2KB | RESPONSE: 45.6KB
2025-07-29 10:33:26.125 INFO [abc12345] [DATABASE] [INSERT_contracts] - 🗄️ DB INSERT na tabela contracts | RECORDS: 150 | DURATION: 89ms
```

### Log de Erros (errors.log)
```
2025-07-29 10:33:27.456 ERROR [abc12345] [CONTRACT_CONSUMER] [CONSUME_CONTRACTS] - ❌ ERRO CONSUME_CONTRACTS | DURATION: 234ms | STATUS: FAILED | ERROR_CATEGORY: TECHNICAL | ERROR: Connection timeout
java.net.SocketTimeoutException: Connection timeout
    at java.net.SocketInputStream.socketRead0(Native Method)
    at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
    ...
```

## Como Usar

### 1. Anotação @LogOperation
```java
@LogOperation(operation = "PROCESSAR_DADOS", component = "DATA_PROCESSOR", slowOperationThresholdMs = 5000)
public List<Contrato> processarDados(String ugCode) {
    // Seu código aqui
    // O logging será automático via AOP
}
```

### 2. Logging Manual com UnifiedLogger
```java
@Autowired
private UnifiedLogger unifiedLogger;

public void exemploManual() {
    // Log de início
    unifiedLogger.logOperationStart("MY_COMPONENT", "MANUAL_OPERATION", "UG", "001");
    
    try {
        // Sua lógica aqui
        List<String> dados = processarAlgumaDados();
        
        // Log de sucesso
        unifiedLogger.logOperationSuccess("MY_COMPONENT", "MANUAL_OPERATION", 
                System.currentTimeMillis() - startTime, dados.size());
                
    } catch (Exception e) {
        // Log de erro
        unifiedLogger.logOperationError("MY_COMPONENT", "MANUAL_OPERATION", 
                System.currentTimeMillis() - startTime, e);
        throw e;
    }
}
```

### 3. Logs Específicos
```java
// Chamada de API
unifiedLogger.logApiCall("/api/endpoint", "GET", 200, 1500, 1024, 4096);

// Operação de banco
unifiedLogger.logDatabaseOperation("INSERT", "contratos", 100, 250);

// Processamento de dados
unifiedLogger.logDataProcessing("PROCESSOR", "VALIDATE_DATA", 1000, 950, 900, 50);

// Evento da aplicação
unifiedLogger.logApplicationEvent("STARTUP_COMPLETE", "Aplicação iniciada com sucesso");
```

## Migração do Sistema Anterior

### Classes Removidas
- `MasterLogger`
- `ContractConsumptionLogger`
- `PerformanceLogger`
- `SecurityLogger`
- `DatabaseLogger`
- `LoggingOptimizer`
- Modelos: `LogEvent`, `ApiCallLog`, `ErrorLog`, `PerformanceMetrics`
- Anotações: `@LogExecution`, `@LogPerformance`, `@LogApiCall`

### Substituições
| Antes | Depois |
|-------|--------|
| `@LogExecution` | `@LogOperation` |
| `@LogPerformance` | `@LogOperation` (funcionalidade integrada) |
| `@LogApiCall` | `@LogOperation` ou `unifiedLogger.logApiCall()` |
| `contractLogger.logXxx()` | `unifiedLogger.logXxx()` |
| `performanceLogger.logXxx()` | `unifiedLogger.logXxx()` |
| Múltiplos arquivos de log | `application.log` + `errors.log` |

### Exemplo de Migração
```java
// ANTES
@LogExecution(operation = "PROCESS", includePerformance = true)
@LogPerformance(operationType = "DATA_PROCESSING", slowOperationThresholdMs = 5000)
public void processar() { ... }

// DEPOIS
@LogOperation(operation = "PROCESS", slowOperationThresholdMs = 5000)
public void processar() { ... }
```

## Benefícios da Simplificação

1. **Redução de Complexidade**: De ~15 classes para ~4 classes
2. **Logs Unificados**: 2 arquivos ao invés de 8
3. **Formato Consistente**: Padrão único em todos os logs
4. **Manutenção Simples**: Muito mais fácil de manter e entender
5. **Performance Melhor**: Menos overhead de logging
6. **Rastreamento Claro**: Correlation IDs em todos os logs
7. **Informações Essenciais**: Foco no que realmente importa

## Configuração

A configuração está no arquivo `logback-spring.xml` simplificado que mantém apenas:
- Console appender para desenvolvimento
- Application file appender para log principal
- Error file appender para erros
- Configurações por perfil (dev/prod)

## Monitoramento

Para análise dos logs, recomenda-se:
- **Grep/Awk** para análises simples
- **ELK Stack** para análises avançadas
- **Correlation ID** para rastreamento de operações
- **Filtros por componente/operação** para debugging específico

## Exemplos de Consultas

```bash
# Buscar por correlation ID
grep "abc12345" logs/application.log

# Buscar operações lentas
grep "🐌" logs/application.log

# Buscar erros de um componente específico
grep "CONTRACT_CONSUMER" logs/errors.log

# Contar operações por tipo
grep -o "\[.*\].*\[.*\]" logs/application.log | sort | uniq -c
```
