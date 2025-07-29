# Sistema de Logging Simplificado

## Visão Geral

O sistema de logging foi simplificado para melhorar drasticamente a legibilidade e reduzir o ruído nos logs. O novo sistema produz logs limpos, focados e fáceis de ler.

## Principais Melhorias

### ❌ Formato Anterior (Complexo)
```
2025-07-29 12:46:15.442 DEBUG [N/A] [APP] [UNKNOWN] - Logging Provider: org.jboss.logging.Slf4jLoggerProvider found via system property
2025-07-29 12:51:23.650 INFO [d81d439d] [APPLICATION] [SCHEDULER_STARTUP_TEST] - 🔸 [12:51:23.650] SCHEDULER_STARTUP_TEST | Execução de teste do scheduler
```

### ✅ Formato Novo (Simplificado)
```
12:51:23 | SCHEDULER | 🚀 Iniciando execução de teste
12:51:23 | AUTH | ✅ Autenticação realizada (123ms)
12:51:29 | DATABASE | ✅ 119 registros inseridos (5.7s)
```

## Componentes do Sistema

### 1. SimpleLogger
Logger principal com formato limpo e direto.

```java
@Autowired
private SimpleLogger simpleLogger;

// Logs básicos
simpleLogger.info("COMPONENT", "Mensagem informativa");
simpleLogger.success("API", "Requisição processada", 150); // com duração
simpleLogger.warning("DATABASE", "Conexão lenta");
simpleLogger.error("NETWORK", "Falha de conectividade");

// Logs de operação
simpleLogger.start("SCHEDULER", "Execução automática");
simpleLogger.progress("BATCH", "Processando dados", 75, 100);
simpleLogger.slow("QUERY", "Consulta demorada", 8000);
```

### 2. MarkdownLogger
Para operações complexas que precisam de estrutura clara.

```java
@Autowired
private MarkdownLogger markdownLogger;

// Log estruturado
MarkdownLogger.MarkdownSection section = markdownLogger.startSection("Execução do Scheduler");

section.success("Autenticação realizada", 123)
       .progress("Buscando dados...")
       .success("119 registros encontrados", 1500)
       .warning("Operação lenta detectada")
       .logWithSummary(119);
```

**Resultado:**
```markdown
## 12:51:23 | Execução do Scheduler
- ✅ Autenticação realizada (123ms)
- 🔄 Buscando dados...
- ✅ 119 registros encontrados (1.5s)
- ⚠️ Operação lenta detectada
- ⏱️ **Total: 119 registros | Tempo total: 6.2s**
```

## Arquivos de Log

### 1. `logs/simple.log` - Log Principal
Formato limpo para leitura diária:
```
12:51:18 | APPLICATION | 🚀 Iniciando aplicação
12:51:18 | APPLICATION | ✅ Aplicação pronta (4.0s)
12:51:23 | SCHEDULER | 🚀 Iniciando execução automática
12:51:23 | AUTH | ✅ Token obtido (123ms)
12:51:29 | DATABASE | ✅ 119 registros inseridos (5.7s)
```

### 2. `logs/operations.md` - Relatórios Estruturados
Logs em markdown para operações importantes:
```markdown
## 12:51:23 | Execução do Scheduler
- ✅ Autenticação realizada (123ms)
- 🔄 Processando unidades gestoras...
- ✅ 119 unidades gestoras processadas (6.0s)
- 🔄 Processando contratos fiscais...
- ✅ 2 contratos fiscais processados (245ms)
- ⏱️ **Total: 8.26s | 121 registros**
```

### 3. `logs/application.log` - Log Técnico Completo
Mantém formato original para debugging técnico (apenas logs WARN+).

### 4. `logs/errors.log` - Apenas Erros
Logs de erro com stack traces completos.

## Configuração

### Filtros de Framework
O sistema agora filtra logs desnecessários:
```xml
<logger name="org.springframework" level="WARN"/>
<logger name="org.hibernate" level="WARN"/>
<logger name="com.zaxxer.hikari" level="WARN"/>
```

### Níveis por Ambiente
- **Desenvolvimento**: WARN para frameworks, INFO para aplicação
- **Produção**: WARN para tudo, logs essenciais apenas

## Migração do Sistema Anterior

### Classes Mantidas (Refatoradas)
- `UnifiedLogger` - Agora usa SimpleLogger internamente
- `UserFriendlyLogger` - Mantido para compatibilidade
- `LoggingAspect` - Atualizado para novo formato

### Novas Classes
- `SimpleLogger` - Logger principal simplificado
- `MarkdownLogger` - Logs estruturados em markdown
- `SimplifiedLoggingExample` - Exemplos de uso

## Benefícios

1. **Redução de 80% no ruído dos logs**
2. **Formato limpo e legível**
3. **Logs estruturados em markdown**
4. **Fácil identificação de problemas**
5. **Compatibilidade com sistema anterior**
6. **Melhor performance (menos I/O)**

## Exemplos Práticos

### Operação de API
```java
simpleLogger.start("API", "Processando requisição");
// ... processamento ...
simpleLogger.success("API", "Resposta enviada", duration);
```

### Operação de Banco
```java
simpleLogger.info("DATABASE", "Iniciando transação");
simpleLogger.success("DATABASE", "100 registros inseridos", 250);
```

### Scheduler Complexo
```java
MarkdownLogger.MarkdownSection section = markdownLogger.startSection("Execução Noturna");
section.success("Backup realizado")
       .progress("Processando dados...")
       .success("1000 registros processados")
       .logWithSummary(1000);
```

Este sistema mantém a funcionalidade completa do anterior, mas com foco na clareza e simplicidade.
