# Guia dos Endpoints de Teste - Swagger/OpenAPI

## 🌐 **Acesso ao Swagger UI**

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/api-docs

## 📋 **Endpoints Documentados**

### 🔧 **Testes de Logging** (`/api/test`)

#### 1. Teste do Sistema de Logging
- **Endpoint**: `GET /api/test/logging`
- **Descrição**: Executa testes do SimpleLogger e MarkdownLogger
- **Resultado**: Demonstra o funcionamento do sistema de logging simplificado
- **Logs Gerados**:
  - `logs/simple.log` - Logs limpos
  - `logs/operations.md` - Logs estruturados em markdown

### ❌ **Testes de Erro** (`/api/test/error`)

#### 1. Erro de Conexão com API
- **Endpoint**: `GET /api/test/error/api-connection`
- **Descrição**: Simula falha de conexão com APIs externas (SEFAZ)
- **Erro Simulado**: `Connection timeout`
- **Logs Gerados**: Erro de conectividade nos arquivos de log

#### 2. Erro de Autenticação
- **Endpoint**: `GET /api/test/error/authentication`
- **Descrição**: Simula falha na autenticação com SSO SEFAZ
- **Erro Simulado**: `Invalid credentials`
- **Logs Gerados**: Erro de autenticação nos logs de segurança

#### 3. Erro de Processamento de Dados
- **Endpoint**: `GET /api/test/error/data-processing`
- **Descrição**: Simula falha no processamento/parsing dos dados
- **Erro Simulado**: `Invalid data format`
- **Logs Gerados**: Erro de processamento nos logs

#### 4. Erro de Banco de Dados
- **Endpoint**: `GET /api/test/error/database`
- **Descrição**: Simula falha nas operações de persistência
- **Erro Simulado**: `Database connection failed`
- **Logs Gerados**: Erro de banco nos logs de database

#### 5. Teste Completo de Erros
- **Endpoint**: `GET /api/test/error/all`
- **Descrição**: Executa todos os testes de erro em sequência
- **Resultado**: Validação completa do sistema de logging de erros

#### 6. Verificação de Logs
- **Endpoint**: `GET /api/test/verify-logs`
- **Descrição**: Verifica se os logs estão sendo gravados corretamente
- **Resultado**: Status dos arquivos de log e teste de gravação

### 📊 **Endpoints de Dados** (`/contratos-fiscais`, `/unidade-gestora`)

#### 1. Contratos Fiscais
- **Endpoint**: `GET /contratos-fiscais`
- **Descrição**: Lista todos os contratos fiscais disponíveis
- **Funcionalidade**: Consome dados da API SEFAZ e persiste no banco

#### 2. Unidades Gestoras
- **Endpoint**: `GET /unidade-gestora`
- **Descrição**: Lista todas as unidades gestoras disponíveis
- **Funcionalidade**: Consome dados da API SEFAZ e persiste no banco

### 🔧 **Gerenciamento de Logs** (`/api/logs`)

#### 1. Status dos Logs
- **Endpoint**: `GET /api/logs/status`
- **Descrição**: Mostra informações sobre todos os arquivos de log
- **Resultado**: Tamanho, data de modificação de cada arquivo

#### 2. Visualizar Logs
- **Endpoint**: `GET /api/logs/tail/simple` - Últimas linhas do simple.log
- **Endpoint**: `GET /api/logs/tail/errors` - Últimas linhas do errors.log
- **Endpoint**: `GET /api/logs/tail/application` - Últimas linhas do application.log

#### 3. Limpar Logs
- **Endpoint**: `DELETE /api/logs/clear`
- **Descrição**: Remove conteúdo de todos os arquivos de log (desenvolvimento)
- **Resultado**: Logs zerados para testes limpos

## 🧪 **Como Testar**

### 1. **Teste Básico do Sistema de Logging**
```bash
curl -X GET "http://localhost:8083/api/test/logging"
```

### 2. **Teste de Erro Específico**
```bash
# Erro de API
curl -X GET "http://localhost:8083/api/test/error/api-connection"

# Erro de Autenticação
curl -X GET "http://localhost:8083/api/test/error/authentication"

# Erro de Processamento
curl -X GET "http://localhost:8083/api/test/error/data-processing"

# Erro de Banco
curl -X GET "http://localhost:8083/api/test/error/database"
```

### 3. **Teste Completo**
```bash
# Todos os erros
curl -X GET "http://localhost:8083/api/test/error/all"
```

## 📁 **Verificação dos Logs**

Após executar os testes, verifique os arquivos:

### Logs de Sucesso
- **`logs/simple.log`** - Logs limpos e legíveis
- **`logs/operations.md`** - Relatórios estruturados em markdown

### Logs de Erro
- **`logs/errors.log`** - Stack traces completos dos erros
- **`logs/application.log`** - Logs técnicos detalhados

### Console
- Mensagens amigáveis para usuário final

## 🎯 **Exemplos de Resposta**

### Sucesso (200)
```
Teste de logging executado! Verifique os arquivos:
- logs/simple.log (logs limpos)
- logs/operations.md (logs estruturados)
- logs/application.log (logs técnicos)
```

### Erro Simulado (200)
```
Erro de conexão simulado! Verifique os logs:
- logs/errors.log (erro completo)
- logs/simple.log (erro simplificado)
- Console (mensagem amigável)
```

## 🔍 **Tags no Swagger**

Os endpoints estão organizados nas seguintes tags:

1. **Testes de Logging** - Endpoints para testar o sistema de logging
2. **Testes de Erro** - Endpoints para simular diferentes tipos de erro
3. **Contratos Fiscais** - Endpoints para dados de contratos
4. **Unidade Gestora** - Endpoints para dados de unidades gestoras

## 📝 **Notas Importantes**

1. **Ambiente de Teste**: Estes endpoints são para teste e desenvolvimento
2. **Logs Reais**: Os erros simulados geram logs reais no sistema
3. **Performance**: Os testes não afetam dados de produção
4. **Monitoramento**: Use os logs para verificar o funcionamento correto

## 🚀 **Próximos Passos**

1. Acesse o Swagger UI: http://localhost:8083/swagger-ui.html
2. Execute os testes através da interface
3. Verifique os logs gerados
4. Analise o formato simplificado vs. anterior
