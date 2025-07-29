# Sistema de Logging Abrangente - SEFAZ Consumer

## Visão Geral

Este documento descreve o sistema de logging abrangente implementado para capturar todas as ações de consumo de contratos da API da SEFAZ. O sistema foi projetado para ser production-ready, com logs estruturados, rotação automática e métricas de performance.

## Estrutura de Arquivos de Log

```
./logs/
├── application/          # Logs gerais da aplicação
│   ├── application.log
│   └── application.YYYY-MM-DD.*.log.gz
├── contracts/           # Logs específicos de consumo de contratos
│   ├── contract-consumption.log
│   └── contract-consumption.YYYY-MM-DD.*.log.gz
├── api/                 # Logs de chamadas de API
│   ├── api-calls.log
│   └── api-calls.YYYY-MM-DD.*.log.gz
├── performance/         # Métricas de performance
│   ├── performance.log
│   └── performance.YYYY-MM-DD.*.log.gz
├── errors/              # Logs de erros
│   ├── errors.log
│   └── errors.YYYY-MM-DD.*.log.gz
├── security/            # Logs de segurança e autenticação
│   ├── security.log
│   └── security.YYYY-MM-DD.*.log.gz
└── database/            # Logs de operações de banco
    ├── database.log
    └── database.YYYY-MM-DD.*.log.gz
```

## Componentes do Sistema

### 1. Configuração de Logging (logback-spring.xml)
- **Appenders separados** para diferentes tipos de logs
- **Rotação automática** baseada em tempo e tamanho
- **Compressão automática** de arquivos antigos
- **Logging assíncrono** para melhor performance
- **Formato JSON estruturado** para facilitar parsing

### 2. Modelos de Dados Estruturados
- **LogEvent**: Classe base para todos os eventos de log
- **ApiCallLog**: Logs específicos para chamadas de API
- **PerformanceMetrics**: Métricas de performance e throughput
- **ErrorLog**: Logs estruturados de erros com categorização

### 3. Loggers Especializados
- **ContractConsumptionLogger**: Logs de consumo de contratos
- **PerformanceLogger**: Métricas de performance
- **SecurityLogger**: Eventos de autenticação e segurança
- **DatabaseLogger**: Operações de banco de dados

### 4. Anotações para Logging Automático
- **@LogExecution**: Logging automático de execução de métodos
- **@LogPerformance**: Captura automática de métricas de performance
- **@LogApiCall**: Logging específico para chamadas de API

### 5. Utilitários
- **MDCUtil**: Gerenciamento de contexto distribuído
- **LoggingUtils**: Utilitários para sanitização e formatação

## Tipos de Eventos Capturados

### Consumo de Contratos
```json
{
  "timestamp": "2025-01-29 10:30:45.123",
  "eventType": "CONTRACT_CONSUMPTION_START",
  "correlationId": "uuid-123",
  "component": "CONTRACT_CONSUMER",
  "operation": "CONSUMIR_PERSISTIR",
  "ugCode": "001",
  "endpoint": "https://api.sefaz.se.gov.br/contratos",
  "message": "Iniciando consumo de contratos"
}
```

### Chamadas de API
```json
{
  "timestamp": "2025-01-29 10:30:46.456",
  "eventType": "API_CALL",
  "httpMethod": "GET",
  "url": "https://api.sefaz.se.gov.br/contratos",
  "responseStatus": 200,
  "responseTimeMs": 1234,
  "requestSizeBytes": 0,
  "responseSizeBytes": 15678
}
```

### Métricas de Performance
```json
{
  "timestamp": "2025-01-29 10:30:47.789",
  "eventType": "PERFORMANCE",
  "className": "ConsumoApiService",
  "methodName": "consumirPersistir",
  "executionTimeMs": 5432,
  "recordsProcessed": 150,
  "throughputRecordsPerSecond": 27.6
}
```

### Eventos de Segurança
```json
{
  "timestamp": "2025-01-29 10:30:45.000",
  "eventType": "TOKEN_REQUEST",
  "component": "SECURITY",
  "operation": "REQUEST_TOKEN",
  "clientId": "87f72053",
  "endpoint": "https://sso.apps.sefaz.se.gov.br/auth/..."
}
```

### Operações de Banco de Dados
```json
{
  "timestamp": "2025-01-29 10:30:48.000",
  "eventType": "DB_INSERT",
  "component": "DATABASE",
  "operation": "INSERT",
  "tableName": "contratos_fiscais",
  "recordsInserted": 150,
  "executionTimeMs": 2345,
  "throughputRecordsPerSecond": 64.0
}
```

## Configurações

### application.properties
```properties
# Logging Configuration
logging.config=classpath:logback-spring.xml

# Application Logging Levels
logging.level.br.gov.se.setc=INFO
logging.level.br.gov.se.setc.logging=INFO

# Performance Logging
logging.performance.enabled=true
logging.performance.slow-operation-threshold-ms=5000

# Security Logging
logging.security.enabled=true
logging.security.log-sensitive-data=false

# Contract Consumption Logging
logging.contract.enabled=true
logging.contract.log-request-details=true

# Database Logging
logging.database.enabled=true
logging.database.slow-query-threshold-ms=3000
```

## Políticas de Retenção

- **Logs de aplicação**: 30 dias, máximo 3GB
- **Logs de contratos**: 60 dias, máximo 5GB
- **Logs de API**: 60 dias, máximo 5GB
- **Logs de performance**: 30 dias, máximo 2GB
- **Logs de erro**: 90 dias, máximo 5GB
- **Logs de segurança**: 90 dias, máximo 3GB
- **Logs de banco**: 30 dias, máximo 3GB

## Segurança e Sanitização

### Dados Sensíveis Automaticamente Mascarados
- Tokens de autenticação: `Bearer ***MASKED***`
- Senhas: `***MASKED***`
- Client secrets: `***MASKED***`
- CPF: `***.***.***-**`
- CNPJ: `**.***.***/****-**`

## Monitoramento e Alertas

### Métricas Importantes
- **Tempo de resposta** das APIs da SEFAZ
- **Taxa de erro** nas chamadas de API
- **Throughput** de processamento de contratos
- **Tempo de persistência** no banco de dados
- **Uso de memória** durante processamento

### Alertas Recomendados
- Operações que excedem 30 segundos
- Taxa de erro superior a 5%
- Falhas de autenticação
- Queries de banco que excedem 10 segundos
- Uso de memória superior a 1GB

## Como Usar

### 1. Logging Automático com Anotações
```java
@LogExecution(operation = "PROCESSAR_DADOS", includePerformance = true)
@LogPerformance(operationType = "DATA_PROCESSING", slowOperationThresholdMs = 5000)
public void processarDados() {
    // Seu código aqui
}
```

### 2. Logging Manual
```java
@Autowired
private ContractConsumptionLogger contractLogger;

public void exemploUso() {
    contractLogger.logConsumptionStart("001", "endpoint", "operacao");
    try {
        // Processar dados
        contractLogger.logConsumptionSuccess("001", "endpoint", 100, 5000, "operacao");
    } catch (Exception e) {
        contractLogger.logConsumptionError("001", "endpoint", "operacao", e, 5000);
    }
}
```

### 3. Configurar Contexto MDC
```java
MDCUtil.generateAndSetCorrelationId();
MDCUtil.setComponent("MEU_COMPONENTE");
MDCUtil.setOperation("MINHA_OPERACAO");
MDCUtil.setUgCode("001");
```

## Análise de Logs

### Ferramentas Recomendadas
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Grafana** + **Loki**
- **Fluentd** para coleta

### Queries Úteis (JSON)
```bash
# Buscar erros por UG
jq '.ugCode == "001" and .level == "ERROR"' logs/errors/errors.log

# Calcular tempo médio de resposta
jq '.responseTimeMs' logs/api/api-calls.log | awk '{sum+=$1; count++} END {print sum/count}'

# Contar operações por tipo
jq -r '.operationType' logs/performance/performance.log | sort | uniq -c
```

## Troubleshooting

### Problemas Comuns
1. **Logs não aparecem**: Verificar configuração do logback-spring.xml
2. **Performance degradada**: Verificar se logging assíncrono está habilitado
3. **Espaço em disco**: Verificar políticas de rotação e retenção
4. **Dados sensíveis expostos**: Verificar sanitização no LoggingUtils

### Verificação de Saúde
```bash
# Verificar se logs estão sendo gerados
ls -la logs/*/

# Verificar tamanho dos logs
du -sh logs/*/

# Verificar últimas entradas
tail -f logs/application/application.log
```

## Extensibilidade

Para adicionar novos tipos de logging:

1. Criar novo modelo de dados estendendo `LogEvent`
2. Criar logger especializado
3. Adicionar configuração no logback-spring.xml
4. Criar anotação específica se necessário
5. Atualizar documentação

Este sistema de logging fornece visibilidade completa sobre todas as operações de consumo de contratos, facilitando monitoramento, debugging e análise de performance em ambiente de produção.
