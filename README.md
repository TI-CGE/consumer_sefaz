# SEFAZ Transparency Consumer

## 📋 Descrição do Projeto

O **SEFAZ Transparency Consumer** é uma aplicação Spring Boot desenvolvida para consumir dados das APIs de transparência da Secretaria da Fazenda do Estado de Sergipe (SEFAZ-SE) e armazená-los em um banco de dados PostgreSQL local para consulta e análise.

### 🎯 Propósito da Aplicação

Esta aplicação funciona como um **consumidor de dados** que:
- Conecta-se às APIs públicas de transparência da SEFAZ-SE
- Autentica-se automaticamente usando OAuth2 (client credentials)
- Consome dados de diferentes endpoints (contratos, receitas, despesas, etc.)
- Processa e valida os dados recebidos
- Armazena os dados em um banco PostgreSQL 
- Disponibiliza APIs REST para consulta dos dados armazenados
- Oferece interface Swagger para documentação e testes

## 🏗️ Arquitetura do Sistema

### Tecnologias Utilizadas
- **Framework**: Spring Boot 3.3.3
- **Linguagem**: Java 21
- **Banco de Dados**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Documentação API**: OpenAPI 3.0 (Swagger)
- **Logging**: Logback com múltiplos appenders
- **Autenticação**: OAuth2 Client Credentials

### Estrutura em Camadas

```
┌─────────────────────────────────────────┐
│           CAMADA DE APRESENTAÇÃO        │
│  Controllers REST + Swagger UI          │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│           CAMADA DE SERVIÇO             │
│  Lógica de Negócio + Consumo APIs       │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│         CAMADA DE PERSISTÊNCIA          │
│  Entidades JPA + Repositórios           │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│           CAMADA DE DADOS               │
│        PostgreSQL Database              │
└─────────────────────────────────────────┘
```

## 🔧 Classes Principais do Sistema

### 📡 Controllers (Camada de Apresentação)
- **`HealthController`**: Health checks e status da aplicação
- **`LogManagementController`**: Gerenciamento e visualização de logs
- **`SchedulerController`**: Controle manual de execução de tarefas
- **`SwaggerContratoController`**: API para dados de contratos
- **`SwaggerReceitaController`**: API para dados de receitas
- **`SwaggerPagamentoController`**: API para dados de pagamentos
- **`SwaggerEmpenhoController`**: API para dados de empenhos
- **`SwaggerLiquidacaoController`**: API para dados de liquidações

### ⚙️ Services (Camada de Negócio)
- **`ConsumoApiService<T>`**: Serviço principal para consumo de APIs externas
- **`JpaPersistenceService`**: Serviço de persistência usando JPA
- **`AcessoTokenService`**: Gerenciamento de tokens OAuth2
- **`DespesaDetalhadaMultiMesService`**: Consumo de dados multi-mês
- **`PrevisaoRealizacaoReceitaMultiMesService`**: Consumo de receitas multi-mês

### 🗄️ Entities (Camada de Persistência)
- **`Contrato`**: Dados de contratos fiscais
- **`Receita`**: Receitas de convênios
- **`Pagamento`**: Dados de pagamentos
- **`Empenho`**: Dados de empenhos
- **`Liquidacao`**: Dados de liquidações
- **`ConsultaGerencial`**: Dados de consulta gerencial
- **`DadosOrcamentarios`**: Dados orçamentários

### 🔄 DTOs (Transferência de Dados)
- **`ContratoDTO`**: Transfer object para contratos
- **`ReceitaDTO`**: Transfer object para receitas
- **`PagamentoDTO`**: Transfer object para pagamentos
- **`EmpenhoDTO`**: Transfer object para empenhos

### 📅 Schedulers
- **`ContractConsumptionScheduler`**: Agendamento automático de consumo de dados

## 🌊 Fluxo Completo do Sistema

### 1. **Inicialização**
```
Aplicação Inicia → Configuração de Beans → Conexão com BD → Pronto para Consumo
```

### 2. **Autenticação**
```
Solicitação de Token → OAuth2 SEFAZ → Token Válido → Cache do Token
```

### 3. **Consumo de Dados**
```
Trigger (Manual/Agendado) → Obter Token → Chamar API SEFAZ → Processar Resposta → Validar Dados → Persistir no BD
```

### 4. **Disponibilização**
```
Cliente Consulta → Controller REST → Service → Repository → Retorna Dados
```

### Fluxo Detalhado de Consumo

1. **Trigger de Execução**: Manual via endpoint ou automático via scheduler
2. **Autenticação**: `AcessoTokenService` obtém token OAuth2 da SEFAZ
3. **Consumo**: `ConsumoApiService` faz requisições para APIs da SEFAZ
4. **Processamento**: Dados são convertidos de DTO para Entity
5. **Validação**: `TypeConverter` valida e converte tipos de dados
6. **Persistência**: `JpaPersistenceService` salva no PostgreSQL
7. **Logging**: Sistema registra todas as operações
8. **Disponibilização**: Dados ficam disponíveis via APIs REST

## 🚀 Instruções de Execução

### Pré-requisitos
- Java 21+
- PostgreSQL 12+
- Maven 3.8+

### Configuração do Banco de Dados
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sefaz_transparency
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### Execução
```bash
# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run

# Ou executar o JAR
java -jar target/sefaz-transparency-consumer-0.0.1-SNAPSHOT.jar
```

### Acesso às APIs
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **Health Check**: http://localhost:8083/health
- **Logs**: http://localhost:8083/logs/status

### Endpoints Principais
- `GET /contrato` - Consultar contratos
- `GET /receita` - Consultar receitas  
- `GET /pagamento` - Consultar pagamentos
- `POST /scheduler/execute/{entity}` - Executar consumo manual

## 📊 Monitoramento e Logs

O sistema possui um robusto sistema de logging com:
- **Logs de Operação**: Registram todas as operações de consumo
- **Logs de Erro**: Capturam e detalham erros
- **Logs de Performance**: Monitoram tempo de execução
- **Rotação Automática**: Gerenciam tamanho e idade dos logs

## 🔒 Segurança

- Autenticação OAuth2 com a SEFAZ
- Tokens com renovação automática
- Logs de segurança para auditoria
- Validação de dados de entrada

## 📈 Performance

- Cache de tokens para reduzir chamadas de autenticação
- Processamento em lotes para grandes volumes
- Conexões de banco otimizadas
- Retry automático em caso de falhas temporárias
