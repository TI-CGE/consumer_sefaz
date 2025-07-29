# Sistema de Logging Amigável ao Usuário

## Visão Geral

O sistema de logging foi aprimorado com uma camada adicional de mensagens simples e legíveis para usuários finais, mantendo os logs técnicos detalhados para desenvolvedores.

## Estrutura do Sistema

### Duas Camadas de Logging

1. **UserFriendlyLogger** - Mensagens simples para usuários
2. **UnifiedLogger** - Logs técnicos detalhados para desenvolvedores

### Saídas de Log

- **Console**: Apenas mensagens simples do UserFriendlyLogger
- **Arquivos**: Logs técnicos completos (application.log e errors.log)

## Formato das Mensagens

### Console (Usuário Final)
```
=== SEFAZ Transparency Consumer ===
Aplicação iniciando...
Aplicação pronta para uso
=====================================
Iniciando execução automática...
Obtendo token de autenticação...
Token obtido com sucesso
Buscando unidades gestoras...
Total de unidades gestoras encontradas: 119
Salvando dados no banco...
Dados salvos: 119 registros
Buscando contratos fiscais...
Total de contratos fiscais encontradas: 2
Salvando dados no banco...
Dados salvos: 2 registros
Operação concluída em 5.3s
Execução automática concluída
Total processado: 121 registros em 5.3s
```

### Arquivos (Desenvolvedores)
```
2025-07-29 12:15:30.123 INFO [abc12345] [CONTRACT_CONSUMER] [CONSUME_CONTRACTS] - 🚀 INICIANDO CONSUME_CONTRACTS...
2025-07-29 12:15:30.456 INFO [abc12345] [API_CLIENT] [API_CALL] - 🌐 API SUCESSO GET /api/contracts | STATUS: 200...
```

## Como Usar

### 1. UserFriendlyLogger - Mensagens Simples

```java
@Autowired
private UserFriendlyLogger userFriendlyLogger;

// Início de operação
userFriendlyLogger.logAuthenticationStart();
// Output: "Obtendo token de autenticação..."

// Sucesso
userFriendlyLogger.logAuthenticationSuccess();
// Output: "Token obtido com sucesso"

// Busca de dados
userFriendlyLogger.logDataFetchStart("unidades gestoras");
// Output: "Buscando unidades gestoras..."

// Dados encontrados
userFriendlyLogger.logDataFound("unidades gestoras", 119);
// Output: "Total de unidades gestoras encontradas: 119"

// Processamento
userFriendlyLogger.logProcessingStart("contratos");
// Output: "Processando contratos..."

// Progresso
userFriendlyLogger.logProcessingProgress(50, 100);
// Output: "Progresso: 50/100 (50%)"

// Salvamento
userFriendlyLogger.logSavingStart();
// Output: "Salvando dados no banco..."

userFriendlyLogger.logDataSaved(119);
// Output: "Dados salvos: 119 registros"

// Conclusão
userFriendlyLogger.logOperationComplete(5300);
// Output: "Operação concluída em 5.3s"

// Erros
userFriendlyLogger.logError("autenticação", "Credenciais inválidas");
// Output: "ERRO em autenticação: Credenciais inválidas - detalhes em logs/errors.log"
```

### 2. Métodos Disponíveis

#### Autenticação
- `logAuthenticationStart()` - "Obtendo token de autenticação..."
- `logAuthenticationSuccess()` - "Token obtido com sucesso"
- `logAuthenticationError()` - "ERRO: Falha na autenticação - detalhes em logs/errors.log"

#### Busca de Dados
- `logDataFetchStart(String dataType)` - "Buscando {dataType}..."
- `logDataFound(String dataType, int count)` - "Total de {dataType} encontradas: {count}"

#### Processamento
- `logProcessingStart(String dataType)` - "Processando {dataType}..."
- `logProcessingProgress(int processed, int total)` - "Progresso: {processed}/{total} ({percentage}%)"
- `logDataProcessed(String dataType, int count)` - "Total de {dataType} processados: {count}"

#### Banco de Dados
- `logSavingStart()` - "Salvando dados no banco..."
- `logDataSaved(int count)` - "Dados salvos: {count} registros"

#### Operações
- `logOperationComplete(long durationMs)` - "Operação concluída em {seconds}s"
- `logSlowOperation(long durationMs)` - "Operação demorou mais que o esperado: {seconds}s"

#### Aplicação
- `logApplicationStart(String appName)` - "=== {appName} ==="
- `logApplicationReady()` - "Aplicação pronta para uso"
- `logScheduledExecutionStart()` - "Iniciando execução automática..."
- `logScheduledExecutionComplete(int total, long duration)` - "Execução automática concluída"

#### Erros e Avisos
- `logError(String operation, String message)` - "ERRO em {operation}: {message} - detalhes em logs/errors.log"
- `logConnectionError(String service)` - "ERRO: Falha de conexão com {service} - detalhes em logs/errors.log"
- `logWarning(String message)` - "AVISO: {message}"

#### Utilitários
- `logSeparator()` - "-------------------------------------"
- `logInfo(String message)` - Mensagem personalizada
- `logSystemStatus(String status)` - "Status do sistema: {status}"

## Configuração

### Logback Configuration

O arquivo `logback-spring.xml` foi configurado para:

1. **Console Simples**: Apenas mensagens do UserFriendlyLogger
2. **Console Técnico**: Logs detalhados (comentado por padrão)
3. **Arquivos**: Logs técnicos completos

```xml
<!-- Console para usuários -->
<logger name="USER_FRIENDLY" level="INFO" additivity="false">
    <appender-ref ref="CONSOLE"/>
</logger>

<!-- Para ver logs técnicos no console, descomente: -->
<!-- <appender-ref ref="CONSOLE_TECHNICAL"/> -->
```

## Exemplo de Implementação

```java
@Service
public class ExampleService {
    
    @Autowired
    private UserFriendlyLogger userFriendlyLogger;
    
    @Autowired
    private UnifiedLogger unifiedLogger;
    
    public void processData() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Log simples para usuário
            userFriendlyLogger.logDataFetchStart("dados importantes");
            
            // Log técnico para arquivo
            unifiedLogger.logOperationStart("SERVICE", "PROCESS_DATA");
            
            // Sua lógica aqui...
            List<Data> data = fetchData();
            
            // Log simples para usuário
            userFriendlyLogger.logDataFound("dados importantes", data.size());
            userFriendlyLogger.logProcessingStart("dados");
            
            // Processar dados...
            processDataList(data);
            
            // Log simples para usuário
            userFriendlyLogger.logDataProcessed("dados", data.size());
            userFriendlyLogger.logOperationComplete(System.currentTimeMillis() - startTime);
            
            // Log técnico para arquivo
            unifiedLogger.logOperationSuccess("SERVICE", "PROCESS_DATA", 
                System.currentTimeMillis() - startTime, data.size());
                
        } catch (Exception e) {
            // Log simples para usuário
            userFriendlyLogger.logError("processamento de dados", e.getMessage());
            
            // Log técnico para arquivo
            unifiedLogger.logOperationError("SERVICE", "PROCESS_DATA", 
                System.currentTimeMillis() - startTime, e);
            throw e;
        }
    }
}
```

## Benefícios

### Para Usuários Finais
- **Mensagens claras** em português
- **Sem jargões técnicos** ou IDs de correlação
- **Progresso visível** das operações
- **Erros compreensíveis** com referência aos logs detalhados

### Para Desenvolvedores
- **Logs técnicos completos** mantidos nos arquivos
- **Debugging facilitado** com correlation IDs
- **Métricas de performance** preservadas
- **Stack traces completos** para análise

### Para Operações
- **Monitoramento simples** via console
- **Logs estruturados** para análise automatizada
- **Separação clara** entre informações de usuário e técnicas
- **Facilidade de troubleshooting** com referências cruzadas

## Migração

Para migrar código existente:

1. **Adicione o UserFriendlyLogger** ao seu service
2. **Mantenha o UnifiedLogger** para logs técnicos
3. **Adicione mensagens simples** para operações principais
4. **Teste a saída** no console e arquivos

O sistema é **retrocompatível** - logs existentes continuam funcionando normalmente nos arquivos.
