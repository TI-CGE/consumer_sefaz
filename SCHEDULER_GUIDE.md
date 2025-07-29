# Guia do Scheduler - SEFAZ Consumer

## Visão Geral

O scheduler foi implementado para executar automaticamente o consumo de contratos da API da SEFAZ, com logging abrangente e monitoramento completo.

## Configuração Atual

### Execução de Teste (Desenvolvimento)
- ✅ **Execução automática**: 5 segundos após a aplicação estar pronta
- ✅ **Execução manual**: Disponível via endpoint REST
- ✅ **Logging completo**: Todos os eventos são logados
- ✅ **Monitoramento**: Status e métricas disponíveis

### Execução de Produção (Configurável)
- ⚠️ **Execução diária**: 2:45 AM (desabilitada por padrão)
- ⚠️ **Testes frequentes**: A cada 10 minutos (desabilitado por padrão)

## Como Testar

### 1. Execução Automática no Startup
```bash
# Inicie a aplicação
mvn spring-boot:run

# Aguarde 5 segundos - o scheduler executará automaticamente
# Verifique os logs para ver a execução
```

### 2. Execução Manual via API
```bash
# Executar manualmente
curl -X POST http://localhost:8083/scheduler/execute

# Verificar status
curl http://localhost:8083/scheduler/status

# Obter informações
curl http://localhost:8083/scheduler/info

# Health check
curl http://localhost:8083/scheduler/ping
```

### 3. Monitoramento via Swagger
Acesse: `http://localhost:8083/swagger-ui.html`
- Navegue até "Scheduler"
- Teste os endpoints diretamente

## Endpoints Disponíveis

### POST /scheduler/execute
Executa o consumo de contratos manualmente.

**Resposta de Sucesso:**
```json
{
  "status": "SUCCESS",
  "message": "Execução manual concluída com sucesso",
  "executionTimeMs": 15432,
  "correlationId": "uuid-123-456"
}
```

**Resposta de Erro:**
```json
{
  "status": "ERROR",
  "message": "Erro durante execução manual: Connection timeout",
  "correlationId": "uuid-123-456",
  "error": "RestClientException"
}
```

### GET /scheduler/status
Retorna o status atual do scheduler.

**Resposta:**
```json
{
  "schedulerActive": true,
  "firstExecutionCompleted": true,
  "nextScheduledExecution": "2:45 AM daily (if enabled)",
  "testExecutionOnStartup": "5 seconds after application ready",
  "timestamp": 1706518800000
}
```

### GET /scheduler/info
Informações detalhadas sobre configuração.

**Resposta:**
```json
{
  "description": "Scheduler para consumo automático de contratos da SEFAZ",
  "testExecution": "5 segundos após inicialização da aplicação",
  "productionSchedule": "Diariamente às 2:45 AM (se habilitado)",
  "endpoints": {
    "contratosFiscais": "https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais",
    "unidadeGestora": "https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora"
  },
  "logging": {
    "structured": true,
    "performance": true,
    "correlation": true,
    "logFiles": {
      "contracts": "./logs/contracts/contract-consumption.log",
      "performance": "./logs/performance/performance.log",
      "errors": "./logs/errors/errors.log"
    }
  }
}
```

## Fluxo de Execução

### 1. Ordem de Processamento
1. **Unidades Gestoras** (obrigatório primeiro)
2. **Aguarda 2 segundos** (para não sobrecarregar a API)
3. **Contratos Fiscais** (depende dos dados de UG)

### 2. Logging Detalhado
Cada execução gera logs estruturados em:
- `./logs/contracts/contract-consumption.log`
- `./logs/performance/performance.log`
- `./logs/api/api-calls.log`
- `./logs/errors/errors.log` (se houver erros)

### 3. Métricas Capturadas
- Tempo total de execução
- Número de registros processados por tipo
- Throughput (registros/segundo)
- Tempo de resposta das APIs
- Tempo de persistência no banco

## Configuração para Produção

### Habilitar Execução Diária
No `application.properties`:
```properties
scheduler.production-schedule-enabled=true
```

Depois descomente no `ContractConsumptionScheduler.java`:
```java
@Scheduled(cron = "0 45 2 * * *")
public void scheduledExecution() {
    // ...
}
```

### Habilitar Testes Frequentes
No `application.properties`:
```properties
scheduler.frequent-test-enabled=true
scheduler.frequent-test-interval-minutes=10
```

Depois descomente no `ContractConsumptionScheduler.java`:
```java
@Scheduled(fixedRate = 600000) // 10 minutos
public void frequentTestExecution() {
    // ...
}
```

## Monitoramento de Logs

### Logs em Tempo Real
```bash
# Logs gerais
tail -f logs/application/application.log

# Logs específicos de contratos
tail -f logs/contracts/contract-consumption.log

# Logs de performance
tail -f logs/performance/performance.log

# Logs de erros
tail -f logs/errors/errors.log
```

### Análise de Performance
```bash
# Buscar execuções do scheduler
grep "SCHEDULED_CONTRACT_CONSUMPTION" logs/contracts/contract-consumption.log

# Verificar tempos de execução
grep "executionTimeMs" logs/performance/performance.log

# Contar registros processados
grep "recordsProcessed" logs/contracts/contract-consumption.log
```

## Troubleshooting

### Problemas Comuns

#### 1. Scheduler não executa no startup
**Verificar:**
- Logs de inicialização da aplicação
- Se `@EnableScheduling` está presente
- Se não há erros de dependência

**Solução:**
```bash
# Verificar logs de startup
grep "INICIANDO EXECUÇÃO DE TESTE" logs/application/application.log
```

#### 2. Erro de conexão com SEFAZ
**Verificar:**
- Conectividade de rede
- Logs de autenticação
- Status da API da SEFAZ

**Solução:**
```bash
# Testar conectividade
curl -I https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais

# Verificar logs de token
grep "TOKEN_" logs/security/security.log
```

#### 3. Erro de banco de dados
**Verificar:**
- Conexão com PostgreSQL
- Permissões de escrita
- Espaço em disco

**Solução:**
```bash
# Verificar logs de banco
grep "DB_" logs/database/database.log

# Verificar conexão
grep "CONNECTION" logs/application/application.log
```

### Logs de Diagnóstico

#### Execução Bem-sucedida
```
2025-01-29 10:30:45.123 INFO  - CONTRACT_CONSUMPTION_START: {"eventType":"CONTRACT_CONSUMPTION_START",...}
2025-01-29 10:30:47.456 INFO  - API_CALL: {"responseTimeMs":1234,"responseStatus":200,...}
2025-01-29 10:30:49.789 INFO  - DB_INSERT: {"recordsInserted":150,"executionTimeMs":2345,...}
2025-01-29 10:30:50.012 INFO  - CONTRACT_CONSUMPTION_SUCCESS: {"recordsProcessed":150,...}
```

#### Execução com Erro
```
2025-01-29 10:30:45.123 INFO  - CONTRACT_CONSUMPTION_START: {...}
2025-01-29 10:30:47.456 ERROR - API_CALL_ERROR: {"errorMessage":"Connection timeout",...}
2025-01-29 10:30:47.500 ERROR - CONTRACT_CONSUMPTION_ERROR: {"exceptionType":"RestClientException",...}
```

## Próximos Passos

### Para Desenvolvimento
1. ✅ Testar execução automática no startup
2. ✅ Verificar logs estruturados
3. ✅ Testar execução manual via API
4. ✅ Monitorar métricas de performance

### Para Produção
1. ⚠️ Configurar execução diária
2. ⚠️ Configurar alertas baseados em logs
3. ⚠️ Implementar monitoramento de saúde
4. ⚠️ Configurar backup de logs

O scheduler está **pronto para testes** e executará automaticamente 5 segundos após a inicialização da aplicação!
