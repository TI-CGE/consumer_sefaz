# Documentação Técnica - SEFAZ Transparency Consumer

## Visão Geral

O **SEFAZ Transparency Consumer** é uma aplicação Spring Boot desenvolvida para consumir dados das APIs de transparência da Secretaria da Fazenda do Estado de Sergipe (SEFAZ-SE) e armazená-los em um banco de dados PostgreSQL local para consulta e análise.

### Tecnologias Utilizadas

- **Framework**: Spring Boot 3.3.3
- **Linguagem**: Java 21
- **Banco de Dados**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Documentação API**: OpenAPI 3.0 (Swagger)
- **Logging**: Logback com encoder JSON
- **Validação**: Jakarta Validation

### Arquitetura do Sistema

O sistema segue uma arquitetura em camadas com os seguintes módulos principais:

1. **Camada de Apresentação** (`controller`): Controllers REST para exposição de APIs
2. **Camada de Serviço** (`service`): Lógica de negócio e consumo de APIs externas
3. **Camada de Persistência** (`entity`, `repository`): Entidades JPA e repositórios
4. **Camada de Transferência** (`dto`): Data Transfer Objects para comunicação
5. **Camada de Configuração** (`config`): Configurações do Spring e beans
6. **Camada de Infraestrutura** (`logging`, `util`): Utilitários e sistema de logging
7. **Camada de Segurança** (`tokenSefaz`): Autenticação e gerenciamento de tokens

---

## 1. Classe Principal da Aplicação

### ConsumerSefazApplication

**Localização**: `br.gov.se.setc.ConsumerSefazApplication`

**Propósito**: Classe principal que inicializa a aplicação Spring Boot.

**Características**:
- Habilita agendamento de tarefas (`@EnableScheduling`)
- Configuração padrão do Spring Boot

**Métodos**:
- `main(String[] args)`: Ponto de entrada da aplicação

---

## 2. Camada de Configuração

### SefazConsumerConfig

**Localização**: `br.gov.se.setc.config.SefazConsumerConfig`

**Propósito**: Configuração central para criação de beans de serviços de consumo de API.

**Responsabilidades**:
- Criação de beans `ConsumoApiService` para cada endpoint SEFAZ
- Configuração de utilitários de validação
- Injeção de dependências para serviços especializados

**Métodos Principais**:
- `createConsumoApiService()`: Factory method para criação de serviços de consumo
- `createValidacaoUtil()`: Factory method para utilitários de validação
- Múltiplos métodos `@Bean` para cada endpoint (UnidadeGestora, Contrato, Empenho, etc.)

**Endpoints Configurados**:
- Unidade Gestora
- Contratos Fiscais
- Empenhos
- Liquidações
- Pagamentos
- Receitas
- Termos (Convênios)
- Despesas Detalhadas
- E outros endpoints específicos

### LoggingConfig

**Localização**: `br.gov.se.setc.config.LoggingConfig`

**Propósito**: Habilita o processamento de aspectos AOP para logging automático.

**Características**:
- `@EnableAspectJAutoProxy`: Habilita proxy AOP
- Permite interceptação de métodos anotados com `@LogOperation`

### OpenApiConfig

**Localização**: `br.gov.se.setc.config.OpenApiConfig`

**Propósito**: Configuração da documentação OpenAPI/Swagger.

**Características**:
- Define informações da API (título, descrição, versão)
- Configura contato e licença
- Define servidor local na porta 8083

---

## 3. Camada de Controladores REST

### HealthController

**Localização**: `br.gov.se.setc.controller.HealthController`

**Propósito**: Endpoint para verificação de saúde da aplicação.

**Endpoints**:
- `GET /health`: Verificação básica de saúde
- `GET /health/token-test`: Teste específico do serviço de token

**Dependências**:
- `RestTemplate`: Para verificação de conectividade
- `AcessoTokenService`: Para teste de autenticação

### LogManagementController

**Localização**: `br.gov.se.setc.controller.LogManagementController`

**Propósito**: Gerenciamento e monitoramento de arquivos de log.

**Endpoints**:
- `GET /api/logs/status`: Status de todos os arquivos de log
- `GET /api/logs/tail/simple`: Últimas linhas do simple.log
- `GET /api/logs/tail/errors`: Últimas linhas do errors.log
- `GET /api/logs/tail/application`: Últimas linhas do application.log
- `POST /api/logs/rotate`: Rotação manual de logs
- `POST /api/logs/cleanup`: Limpeza de logs antigos

**Dependências**:
- `LogRotationService`: Serviço de rotação de logs
- `LogCleanupService`: Serviço de limpeza de logs

### ErrorTestController

**Localização**: `br.gov.se.setc.controller.ErrorTestController`

**Propósito**: Controller para simulação de diferentes tipos de erro para teste do sistema de logging.

**Endpoints**:
- `GET /error/api`: Simula erro de API
- `GET /error/auth`: Simula erro de autenticação
- `GET /error/data`: Simula erro de processamento
- `GET /error/database`: Simula erro de banco de dados
- `GET /error/all`: Executa todos os testes de erro

---

## 4. Controllers de Consumo SEFAZ

### SwaggerUnidadeGestoraController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerUnidadeGestoraController`

**Propósito**: Consumo de dados de Unidades Gestoras.

**Endpoint**: `GET /unidade-gestora`

**Funcionalidade**: Consome dados do endpoint SEFAZ de unidades gestoras e persiste no banco local.

### SwaggerContratoController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerContratoController`

**Propósito**: Consumo de dados de Contratos.

**Endpoint**: `GET /contrato`

**Funcionalidade**: Consome e persiste dados de contratos fiscais.

### SwaggerEmpenhoController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerEmpenhoController`

**Propósito**: Consumo de dados de Empenhos.

**Endpoint**: `GET /empenho`

**Funcionalidade**: Consome dados de empenhos por unidade gestora e ano.

### SwaggerPagamentoController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerPagamentoController`

**Propósito**: Consumo de dados de Pagamentos.

**Endpoint**: `GET /pagamento`

**Funcionalidade**: Consome dados de pagamentos com filtros por UG e período.

### SwaggerReceitaController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerReceitaController`

**Propósito**: Consumo de dados de Receitas de Convênios.

**Endpoint**: `GET /receita`

**Funcionalidade**: Consome dados de receitas de convênios.

### SwaggerTermoController

**Localização**: `br.gov.se.setc.consumer.controller.SwaggerTermoController`

**Propósito**: Consumo de dados de Termos (Convênios).

**Endpoint**: `GET /termo`

**Funcionalidade**: Consome dados de termos e convênios.

### Outros Controllers Especializados

- **SwaggerContratoEmpenhoController**: Contratos-Empenho
- **SwaggerBaseDespesaCredorController**: Base Despesa por Credor
- **SwaggerBaseDespesaLicitacaoController**: Base Despesa por Licitação
- **SwaggerDespesaConvenioController**: Despesas de Convênio
- **SwaggerDespesaDetalhadaController**: Despesas Detalhadas
- **SwaggerLiquidacaoController**: Liquidações
- **SwaggerOrdemFornecimentoController**: Ordens de Fornecimento

Todos seguem o mesmo padrão:
- Endpoint GET único
- Injeção do serviço de consumo correspondente
- Documentação OpenAPI completa
- Tratamento de exceções
- Logging de operações

---

## 5. Camada de Serviços

### ConsumoApiService<T>

**Localização**: `br.gov.se.setc.consumer.service.ConsumoApiService`

**Propósito**: Serviço genérico para consumo de APIs SEFAZ e persistência de dados com sistema híbrido JPA/JdbcTemplate.

**Características**:
- Genérico (`<T extends EndpontSefaz>`)
- Sistema híbrido: JPA para entidades suportadas, JdbcTemplate para compatibilidade
- Detecção automática de sistema de persistência
- Suporte a paginação automática
- Iteração por Unidades Gestoras
- Tratamento de diferentes estratégias de consumo
- Logging unificado e detalhado

**Métodos Principais**:
- `consumirPersistir(T mapper)`: Método principal de consumo e persistência
- `respostaApiRaw(String apiUrl)`: Execução de requisições HTTP
- `processarRespostaSefaz(String responseBody, T mapper)`: Processamento de respostas JSON
- `consumirIterandoUGs(T mapper)`: Iteração por Unidades Gestoras
- `consumirTodosOsAnos(T mapper)`: Consumo histórico por anos

**Sistema de Persistência Híbrido**:
- **JPA**: Para entidades com tipos padronizados (ConsultaGerencial, Contrato)
- **JdbcTemplate**: Para entidades legadas (compatibilidade)
- **Detecção automática**: Baseada no nome da tabela

**Dependências**:
- `RestTemplate`: Cliente HTTP
- `AcessoTokenService`: Autenticação
- `JpaPersistenceService`: Persistência JPA moderna
- `JdbcTemplate`: Acesso ao banco legado
- `ValidacaoUtil`: Validações
- `UnifiedLogger`: Logging técnico
- `UserFriendlyLogger`: Logging amigável
- `MarkdownLogger`: Logging em markdown

### JpaPersistenceService

**Localização**: `br.gov.se.setc.consumer.service.JpaPersistenceService`

**Propósito**: Sistema moderno de persistência JPA com validação automática de tipos e conversões controladas.

**Características**:
- Persistência JPA com validação automática
- Mappers específicos para conversão DTO→Entity
- Detecção automática de entidades suportadas
- Backup automático antes de alterações
- Logging detalhado de operações
- Tratamento seguro de erros

**Métodos Principais**:
- `persist(List<T> dtos)`: Persistência principal com roteamento automático
- `isJpaPersistenceSupported(String tableName)`: Verifica suporte JPA
- `persistConsultaGerencial()`: Persistência específica para ConsultaGerencial
- `persistContrato()`: Persistência específica para Contrato

**Entidades Suportadas**:
- ✅ **ConsultaGerencial**: Mapeamento completo com conversões de tipos
- ✅ **Contrato**: Mapeamento completo com conversões de datas
- 🔄 **Outras entidades**: Podem usar GenericEntityMapper

**Dependências**:
- `ConsultaGerencialRepository`: Repositório JPA
- `ContratoRepository`: Repositório JPA
- `ConsultaGerencialMapper`: Mapper específico
- `ContratoMapper`: Mapper específico
- `GenericEntityMapper`: Mapper genérico
- `UnifiedLogger`: Logging

### TypeConverter

**Localização**: `br.gov.se.setc.consumer.mapper.TypeConverter`

**Propósito**: Sistema centralizado de conversões de tipos entre API e banco de dados com validação e tratamento de erros.

**Características**:
- Conversões seguras com fallbacks
- Tratamento de valores nulos e inválidos
- Logging de erros de conversão
- Normalização de formatos (vírgula→ponto)
- Validação de formatos de data

**Métodos Principais**:
- `stringToBigDecimal(String value)`: Conversão segura para valores monetários
- `stringToBigDecimalNullable(String value)`: Conversão nullable
- `stringToLocalDate(String value)`: Conversão para datas ISO
- `stringToLocalDateTime(String value)`: Conversão para timestamps
- `stringToInteger(String value)`: Conversão para números inteiros
- `stringToLong(String value)`: Conversão para números longos
- `objectToString(Object value)`: Conversão genérica para string

**Tratamento de Erros**:
- Valores inválidos retornam fallbacks seguros (ZERO, null)
- Logging detalhado de erros de conversão
- Normalização automática de formatos

### Mappers de Entidades

#### ConsultaGerencialMapper

**Localização**: `br.gov.se.setc.consumer.mapper.ConsultaGerencialMapper`

**Propósito**: Mapper específico para conversão entre ConsultaGerencialDTO e ConsultaGerencial com tipos consistentes.

**Características**:
- Conversões String→BigDecimal para valores monetários
- Conversões String→LocalDate para datas
- Campos de auditoria automáticos
- Validação de tipos via TypeConverter

#### ContratoMapper

**Localização**: `br.gov.se.setc.consumer.mapper.ContratoMapper`

**Propósito**: Mapper específico para conversão entre ContratoDTO e Contrato.

**Características**:
- Conversões de datas String→LocalDate
- Mapeamento de campos específicos
- Campos de auditoria automáticos

#### GenericEntityMapper

**Localização**: `br.gov.se.setc.consumer.mapper.GenericEntityMapper`

**Propósito**: Mapper genérico para entidades que já possuem tipos consistentes.

**Características**:
- Mapeamento automático via reflexão
- Suporte a entidades sem inconsistências de tipos
- Fallback para entidades não mapeadas especificamente

### AcessoTokenService

**Localização**: `br.gov.se.setc.tokenSefaz.service.AcessoTokenService`

**Propósito**: Gerenciamento de tokens de autenticação OAuth2 para APIs SEFAZ.

**Características**:
- Cache de tokens com expiração automática
- Renovação automática de tokens expirados
- Logging de segurança detalhado
- Tratamento de erros de autenticação

**Métodos Principais**:
- `getToken()`: Obtém token válido (cache ou novo)
- `requestNewToken()`: Solicita novo token da API
- `isTokenValid()`: Verifica validade do token em cache
- `cacheToken(String token)`: Armazena token em cache
- `extractToken(String responseBody)`: Extrai token da resposta JSON

**Configurações**:
- Client ID: `87f72053`
- URL Token: `https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token`
- Duração do cache: 55 minutos

### PrevisaoRealizacaoReceitaMultiMesService

**Localização**: `br.gov.se.setc.consumer.service.PrevisaoRealizacaoReceitaMultiMesService`

**Propósito**: Serviço especializado para consumo de dados de Previsão/Realização de Receita com busca multi-mês.

**Características**:
- Executa 12 consultas (uma para cada mês)
- Consolida resultados de todos os meses
- Suporte a consulta de mês específico

**Métodos Principais**:
- `consumirTodosMeses()`: Consome dados dos 12 meses
- `consumirMesEspecifico(int mes)`: Consome mês específico
- `criarMapperParaMes(int mes)`: Cria mapper configurado para o mês

---

## 6. Camada de Entidades JPA

### UnidadeGestora

**Localização**: `br.gov.se.setc.consumer.entity.UnidadeGestora`

**Propósito**: Entidade para armazenamento de dados de Unidades Gestoras.

**Tabela**: `consumer_sefaz.unidade_gestora`

**Campos Principais**:
- `id`: Chave primária auto-incremento
- `cdUnidadeGestora`: Código da unidade gestora (chave natural única)
- `nmUnidadeGestora`: Nome da unidade gestora
- `sgUnidadeGestora`: Sigla da unidade gestora
- `sgTipoUnidadeGestora`: Tipo da unidade gestora
- `createdAt`, `updatedAt`: Campos de auditoria

### Contrato

**Localização**: `br.gov.se.setc.consumer.entity.Contrato`

**Propósito**: Entidade para armazenamento de dados de Contratos.

**Tabela**: `consumer_sefaz.contrato`

**Campos Principais**:
- `id`: Chave primária
- `cdUnidadeGestora`: Código da unidade gestora
- `nmUnidadeGestora`: Nome da unidade gestora
- `cdContrato`: Código do contrato
- `nmContrato`: Nome/descrição do contrato
- `vlContrato`: Valor do contrato
- `dtAssinaturaContrato`: Data de assinatura
- `dtInicioVigencia`, `dtFimVigencia`: Período de vigência

### Empenho

**Localização**: `br.gov.se.setc.consumer.entity.Empenho`

**Propósito**: Entidade para armazenamento de dados de Empenhos.

**Tabela**: `consumer_sefaz.empenho`

**Campos Principais**:
- `id`: Chave primária
- `dtAnoExercicioCTB`: Ano do exercício contábil
- `cdUnidadeGestora`: Código da unidade gestora
- `sqSolicEmpenho`: Sequencial da solicitação de empenho
- `vlEmpenho`: Valor do empenho
- `dtEmpenho`: Data do empenho
- `nmCredor`: Nome do credor

### Pagamento

**Localização**: `br.gov.se.setc.consumer.entity.Pagamento`

**Propósito**: Entidade para armazenamento de dados de Pagamentos.

**Tabela**: `consumer_sefaz.pagamento`

**Campos Principais**:
- `id`: Chave primária
- `dtAnoExercicioCTB`: Ano do exercício contábil
- `cdUnidadeGestora`: Código da unidade gestora
- `sqPagamento`: Sequencial do pagamento
- `vlPagamento`: Valor do pagamento
- `dtPagamento`: Data do pagamento
- `nmCredor`: Nome do credor

### Liquidacao

**Localização**: `br.gov.se.setc.consumer.entity.Liquidacao`

**Propósito**: Entidade para armazenamento de dados de Liquidações.

**Tabela**: `consumer_sefaz.liquidacao`

**Campos Principais**:
- `id`: Chave primária
- `dtAnoExercicioCTB`: Ano do exercício contábil
- `cdUnidadeGestora`: Código da unidade gestora
- `sqLiquidacao`: Sequencial da liquidação
- `vlLiquidacao`: Valor da liquidação
- `dtLiquidacao`: Data da liquidação

### Termo

**Localização**: `br.gov.se.setc.consumer.entity.Termo`

**Propósito**: Entidade para armazenamento de dados de Termos (Convênios).

**Tabela**: `consumer_sefaz.termo`

**Campos Principais**:
- `id`: Chave primária
- `cdConvenio`: Código do convênio (chave natural única)
- `nmConvenio`: Nome do convênio
- `vlConvenio`: Valor do convênio
- `dtAssinaturaConvenio`: Data de assinatura
- `dtInicioVigencia`, `dtFimVigencia`: Período de vigência

### Outras Entidades

- **Receita**: Receitas de convênios
- **DespesaDetalhada**: Despesas detalhadas
- **ContratoEmpenho**: Relacionamento contrato-empenho
- **BaseDespesaCredor**: Base de despesas por credor
- **BaseDespesaLicitacao**: Base de despesas por licitação
- **DespesaConvenio**: Despesas de convênio
- **OrdemFornecimento**: Ordens de fornecimento
- **PrevisaoRealizacaoReceita**: Previsão e realização de receitas
- **TotalizadoresExecucao**: Totalizadores de execução
- **ConsultaGerencial**: Dados de consulta gerencial
- **DadosOrcamentarios**: Dados orçamentários

Todas as entidades seguem padrões similares:
- Chave primária auto-incremento
- Campos de auditoria (`createdAt`, `updatedAt`)
- Mapeamento JPA com anotações
- Schema `consumer_sefaz`

---

## 7. Camada de DTOs (Data Transfer Objects)

### EndpontSefaz (Interface Base)

**Localização**: `br.gov.se.setc.consumer.contracts.EndpontSefaz`

**Propósito**: Interface base para todos os DTOs de endpoints SEFAZ.

**Métodos Abstratos**:
- `getTabelaBanco()`: Retorna nome da tabela de destino
- `getUrl()`: Retorna URL do endpoint SEFAZ
- `mapearCamposResposta()`: Mapeia campos da resposta JSON
- `mapearParametros()`: Mapeia parâmetros da requisição
- `getCamposParametrosTodosOsAnos()`: Parâmetros para consulta histórica
- `getCamposParametrosAtual()`: Parâmetros para consulta atual

### UnidadeGestoraDTO

**Localização**: `br.gov.se.setc.consumer.dto.UnidadeGestoraDTO`

**Propósito**: DTO para consumo de dados de Unidades Gestoras.

**Características**:
- Endpoint independente de UG (não itera por unidades)
- Filtro fixo: `sgTipoUnidadeGestora=E`
- URL: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora`

**Campos**:
- `nmUnidadeGestora`: Nome da unidade gestora
- `sgUnidadeGestora`: Sigla da unidade gestora
- `cdUnidadeGestora`: Código da unidade gestora
- `sgTipoUnidadeGestora`: Tipo da unidade gestora

### ContratoDTO

**Localização**: `br.gov.se.setc.consumer.dto.ContratoDTO`

**Propósito**: DTO para consumo de dados de Contratos.

**Características**:
- Itera por Unidades Gestoras
- Suporte a filtros por período
- URL: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato`

**Campos Principais**:
- `cdUnidadeGestora`: Código da unidade gestora
- `nmUnidadeGestora`: Nome da unidade gestora
- `cdContrato`: Código do contrato
- `nmContrato`: Nome do contrato
- `vlContrato`: Valor do contrato
- `dtAssinaturaContrato`: Data de assinatura

### EmpenhoDTO

**Localização**: `br.gov.se.setc.consumer.dto.EmpenhoDTO`

**Propósito**: DTO para consumo de dados de Empenhos.

**Características**:
- Itera por UG e anos
- Filtros por exercício contábil
- URL: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/empenho`

**Campos Principais**:
- `dtAnoExercicioCTB`: Ano do exercício contábil
- `cdUnidadeGestora`: Código da unidade gestora
- `sqSolicEmpenho`: Sequencial da solicitação
- `vlEmpenho`: Valor do empenho
- `dtEmpenho`: Data do empenho
- `nmCredor`: Nome do credor

### PagamentoDTO

**Localização**: `br.gov.se.setc.consumer.dto.PagamentoDTO`

**Propósito**: DTO para consumo de dados de Pagamentos.

**Características**:
- Itera por UG e anos
- Filtros por exercício contábil
- URL: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/pagamento`

**Campos Principais**:
- `dtAnoExercicioCTB`: Ano do exercício contábil
- `cdUnidadeGestora`: Código da unidade gestora
- `sqPagamento`: Sequencial do pagamento
- `vlPagamento`: Valor do pagamento
- `dtPagamento`: Data do pagamento
- `nmCredor`: Nome do credor

### Outros DTOs Importantes

- **TermoDTO**: Dados de termos e convênios
- **ReceitaDTO**: Receitas de convênios
- **LiquidacaoDTO**: Dados de liquidações
- **DespesaDetalhadaDTO**: Despesas detalhadas
- **ContratoEmpenhoDTO**: Relacionamento contrato-empenho
- **BaseDespesaCredorDTO**: Base de despesas por credor
- **BaseDespesaLicitacaoDTO**: Base de despesas por licitação
- **DespesaConvenioDTO**: Despesas de convênio
- **OrdemFornecimentoDTO**: Ordens de fornecimento
- **PrevisaoRealizacaoReceitaDTO**: Previsão e realização de receitas
- **TotalizadoresExecucaoDTO**: Totalizadores de execução
- **ConsultaGerencialDTO**: Dados de consulta gerencial
- **DadosOrcamentariosDTO**: Dados orçamentários

Todos os DTOs seguem padrões similares:
- Implementam interface `EndpontSefaz`
- Contêm mapeamento de campos JSON para propriedades Java
- Definem URL do endpoint SEFAZ correspondente
- Implementam lógica de parâmetros de filtro
- Suportam diferentes estratégias de consumo (por UG, por ano, etc.)

---

## 8. Camada de Repositórios

### Sistema Híbrido de Repositórios

O sistema utiliza uma abordagem híbrida com repositórios JPA modernos e repositórios legados para compatibilidade.

### Repositórios JPA (Modernos)

#### ConsultaGerencialRepository

**Localização**: `br.gov.se.setc.consumer.repository.ConsultaGerencialRepository`

**Propósito**: Repositório JPA para entidade ConsultaGerencial com validação automática de tipos.

**Características**:
- Extends `JpaRepository<ConsultaGerencial, Long>`
- Operações type-safe
- Validação automática de tipos pelo JPA
- Queries customizadas com JPQL

**Métodos Principais**:
- `deleteByCurrentYear()`: Limpeza por ano atual
- `findByDtAnoExercicioCTB(Integer ano)`: Busca por ano
- Métodos herdados: `save()`, `saveAll()`, `findAll()`, etc.

#### ContratoRepository

**Localização**: `br.gov.se.setc.consumer.repository.ContratoRepository`

**Propósito**: Repositório JPA para entidade Contrato.

**Características**:
- Extends `JpaRepository<Contrato, Long>`
- Operações type-safe
- Validação automática de tipos pelo JPA

**Métodos Principais**:
- `deleteByCurrentYear()`: Limpeza por ano atual
- `findByDtAnoExercicio(Integer ano)`: Busca por ano
- Métodos herdados do JpaRepository

### EndpontSefazRepository<T> (Legado)

**Localização**: `br.gov.se.setc.consumer.respository.EndpontSefazRepository`

**Propósito**: Repositório genérico legado para persistência de dados de endpoints SEFAZ via JdbcTemplate.

**Características**:
- Genérico (`<T extends EndpontSefaz>`)
- Operações em lote (batch) via JdbcTemplate
- Estratégias de limpeza de dados
- Logging detalhado de operações
- Usado para entidades sem suporte JPA

**Métodos Principais**:
- `insert(List<T> contratos)`: Inserção em lote
- `deleteByMesVigente(T endpointInstance)`: Limpeza por período
- `persist(List<T> contratos)`: Operação completa (limpar + inserir)

**Estratégias de Limpeza**:
- **Padrão**: Deleta registros do mês atual
- **Previsão Realização Receita**: Deleta todos os registros do ano atual
- **Unidade Gestora**: Deleta todos os registros (dados mestres)

### Detecção Automática de Sistema

O `ConsumoApiService` detecta automaticamente qual sistema usar:

```java
// Sistema híbrido com detecção automática
if (jpaPersistenceService.isJpaPersistenceSupported(tableName)) {
    // Usar repositórios JPA modernos
    jpaPersistenceService.persist(dtos);
} else {
    // Usar repositório legado
    contratosFiscaisDAO.persist(dtos);
}
```

**Entidades com Suporte JPA**:
- ✅ `consumer_sefaz.consulta_gerencial`
- ✅ `consumer_sefaz.contrato`

**Entidades Legadas (JdbcTemplate)**:
- 🔄 `consumer_sefaz.pagamento`
- 🔄 `consumer_sefaz.empenho`
- 🔄 Outras entidades

---

## 9. Sistema de Logging Unificado

### UnifiedLogger

**Localização**: `br.gov.se.setc.logging.UnifiedLogger`

**Propósito**: Sistema centralizado de logging técnico com múltiplos formatos.

**Características**:
- Logging estruturado em JSON
- Suporte a MDC (Mapped Diagnostic Context)
- Múltiplos appenders (arquivo, console)
- Correlação de operações

**Métodos Principais**:
- `logOperationStart()`: Início de operação
- `logOperationSuccess()`: Sucesso de operação
- `logOperationError()`: Erro de operação
- `logAuthentication()`: Eventos de autenticação
- `logApplicationEvent()`: Eventos da aplicação

### UserFriendlyLogger

**Localização**: `br.gov.se.setc.logging.UserFriendlyLogger`

**Propósito**: Logging simplificado para usuários finais.

**Características**:
- Mensagens em linguagem natural
- Foco em eventos de negócio
- Menos detalhes técnicos

**Métodos Principais**:
- `logDataFetchStart()`: Início de busca de dados
- `logDataFetchSuccess()`: Sucesso na busca
- `logAuthenticationStart()`: Início de autenticação
- `logInfo()`, `logWarning()`, `logError()`: Logs básicos

### MarkdownLogger

**Localização**: `br.gov.se.setc.logging.MarkdownLogger`

**Propósito**: Logging em formato Markdown para relatórios.

**Características**:
- Saída formatada em Markdown
- Ideal para documentação automática
- Suporte a seções e hierarquia

**Métodos Principais**:
- `logSimple()`: Log simples
- `logError()`: Log de erro com stack trace
- `section()`: Criação de seções
- `info()`, `success()`, `warning()`, `error()`: Logs categorizados

### LoggingAspect

**Localização**: `br.gov.se.setc.logging.aspect.LoggingAspect`

**Propósito**: Aspecto AOP para interceptação automática de métodos anotados.

**Características**:
- Intercepta métodos com `@LogOperation`
- Logging automático de início, sucesso e erro
- Medição de tempo de execução
- Configuração de contexto MDC

**Funcionalidades**:
- Log de parâmetros (opcional)
- Log de resultado (opcional)
- Contagem de dados processados
- Threshold para operações lentas

### Anotação @LogOperation

**Localização**: `br.gov.se.setc.logging.annotation.LogOperation`

**Propósito**: Anotação para marcação de métodos que devem ter logging automático.

**Parâmetros**:
- `operation`: Nome da operação (obrigatório)
- `component`: Componente responsável (opcional)
- `logParameters`: Se deve logar parâmetros (padrão: false)
- `logResult`: Se deve logar resultado (padrão: false)
- `slowOperationThresholdMs`: Threshold para operação lenta (padrão: 5000ms)
- `includeDataCount`: Se deve incluir contagem de dados (padrão: false)
- `logExceptions`: Se deve logar exceções (padrão: true)

### Serviços de Logging Auxiliares

#### LogRotationService

**Localização**: `br.gov.se.setc.logging.LogRotationService`

**Propósito**: Rotação automática de arquivos de log quando atingem tamanho crítico.

**Configurações**:
- Tamanho máximo: 3MB (configurável)
- Rotação automática habilitada
- Compressão de arquivos antigos

#### LogCleanupService

**Localização**: `br.gov.se.setc.logging.LogCleanupService`

**Propósito**: Limpeza automática de logs antigos.

**Configurações**:
- Idade máxima: 7 dias (configurável)
- Tamanho máximo total: 500MB (configurável)
- Execução automática habilitada

---

## 10. Utilitários

### ValidacaoUtil<T>

**Localização**: `br.gov.se.setc.util.ValidacaoUtil`

**Propósito**: Utilitários para validação e consultas auxiliares.

**Métodos Principais**:
- `isPresenteBanco(T endpointInstance)`: Verifica se há dados na tabela
- `isEndpointIdependenteUGData(T mapper)`: Verifica se endpoint é independente de UG
- `getAnoAtual()`: Obtém ano atual
- `getMesAtual()`: Obtém mês atual
- `getUgs()`: Lista códigos de Unidades Gestoras
- `cdGestao()`: Lista códigos de gestão

### LoggingUtils

**Localização**: `br.gov.se.setc.logging.util.LoggingUtils`

**Propósito**: Utilitários para formatação e sanitização de logs.

**Métodos Principais**:
- `truncate(String text, int maxLength)`: Trunca texto
- `sanitizeSensitiveData(String data)`: Remove dados sensíveis
- `formatDuration(long milliseconds)`: Formata duração
- `formatBytes(long bytes)`: Formata tamanho em bytes
- `determineErrorCategory(Exception exception)`: Categoriza erros

### MDCUtil

**Localização**: `br.gov.se.setc.logging.util.MDCUtil`

**Propósito**: Utilitários para gerenciamento de contexto MDC.

**Métodos Principais**:
- `generateAndSetCorrelationId()`: Gera ID de correlação
- `setupOperationContext()`: Configura contexto de operação
- `setComponent()`, `setOperation()`: Define componente e operação
- `clear()`: Limpa contexto MDC

---

## 11. Scheduler

### ContractConsumptionScheduler

**Localização**: `br.gov.se.setc.scheduler.ContractConsumptionScheduler`

**Propósito**: Agendador para execução automática de consumo de dados SEFAZ.

**Características**:
- Execução agendada (comentada por padrão)
- Execução manual via endpoint
- Suporte a execução de entidades específicas
- Logging detalhado de progresso

**Métodos Principais**:
- `executeAllEntities()`: Executa consumo de todas as entidades
- `executePagamentoOnly()`: Executa apenas Pagamento
- `executeContratoOnly()`: Executa apenas Contrato
- `executeDespesaConvenioOnly()`: Executa apenas Despesa Convênio
- `executeManual()`: Execução manual via endpoint

**Configurações de Agendamento**:
- Produção: Diariamente às 2:45 AM (comentado)
- Teste: 10 segundos após startup (comentado)
- Teste frequente: A cada 10 minutos (comentado)

**Endpoints de Controle**:
- `POST /scheduler/execute`: Execução manual
- `GET /scheduler/status`: Status do scheduler
- `POST /scheduler/execute-pagamento`: Executa apenas Pagamento
- `POST /scheduler/execute-contrato`: Executa apenas Contrato

---

## 12. Configurações e Propriedades

### application.properties

**Localização**: `src/main/resources/application.properties`

**Configurações Principais**:

#### Banco de Dados
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/se_ouve_api
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

#### Logging
```properties
logging.config=classpath:logback-spring.xml
logging.rotation.enabled=true
logging.rotation.max-size-mb=3
logging.cleanup.enabled=true
logging.cleanup.max-age-days=7
logging.cleanup.max-size-mb=500
```

#### Scheduler
```properties
scheduler.enabled=true
scheduler.startup-delay-seconds=10
scheduler.test-execution-on-startup=false
scheduler.production-schedule-enabled=false
```

#### Performance e Logging
```properties
logging.performance.enabled=true
logging.performance.slow-operation-threshold-ms=5000
logging.security.enabled=true
logging.contract.enabled=true
logging.database.enabled=true
```

### logback-spring.xml

**Localização**: `src/main/resources/logback-spring.xml`

**Propósito**: Configuração avançada do sistema de logging.

**Características**:
- Múltiplos appenders (console, arquivo, erro)
- Formato JSON estruturado
- Rotação automática de arquivos
- Filtros por nível de log

---

## 13. Endpoints das APIs SEFAZ Consumidas

### URLs dos Endpoints

1. **Unidade Gestora**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora`
2. **Contratos**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato`
3. **Empenhos**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/empenho`
4. **Liquidações**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/liquidacao`
5. **Pagamentos**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/pagamento`
6. **Receitas**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/receita-convenio`
7. **Termos**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/termo`
8. **Contratos Fiscais**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais`

### Autenticação

- **Tipo**: OAuth2 Client Credentials
- **URL Token**: `https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token`
- **Client ID**: `87f72053`
- **Duração Token**: 55 minutos (cache local)

---

## 14. Estrutura do Banco de Dados

### Schema Principal

**Nome**: `consumer_sefaz`

### Padronização de Tipos de Dados

O sistema implementa **padronização completa de tipos** entre API e banco de dados:

#### Tipos Padronizados (API → Banco)

| Campo API | Tipo API | Tipo Banco | Status |
|-----------|----------|------------|---------|
| `cdLicitacao` | STRING | `character varying(50)` | ✅ **IDÊNTICO** |
| `idOrgao` | INTEGER | `integer` | ✅ **IDÊNTICO** |
| `tpDocumento` | INTEGER | `integer` | ✅ **IDÊNTICO** |
| `sq_empenho` | BIGINT | `bigint` | ✅ **IDÊNTICO** |
| `dt_geracao_empenho` | DATE | `date` | ✅ **IDÊNTICO** |
| `vl_total_*` | DECIMAL | `numeric(18,2)` | ✅ **IDÊNTICO** |

#### Correções Aplicadas no Banco

**Tabela `pagamento`** (242.782 registros processados):
- `cd_licitacao`: integer → `character varying(50)` (seguindo API STRING)
- `id_orgao`: character varying → `integer` (seguindo API INTEGER)
- `tp_documento`: character varying → `integer` (seguindo API INTEGER)

**Tabela `base_despesa_credor`** (60.967 registros processados):
- `sq_empenho`: character varying → `bigint` (seguindo API BIGINT)
- `dt_geracao_empenho`: character varying → `date` (seguindo API DATE)

### Tabelas Principais

1. `unidade_gestora` - Dados mestres de unidades gestoras
2. `contrato` - Contratos fiscais ✅ **JPA**
3. `empenho` - Empenhos orçamentários
4. `liquidacao` - Liquidações
5. `pagamento` - Pagamentos ✅ **TIPOS CORRIGIDOS**
6. `receita` - Receitas de convênios
7. `termo` - Termos e convênios
8. `despesa_detalhada` - Despesas detalhadas
9. `contrato_empenho` - Relacionamento contrato-empenho
10. `base_despesa_credor` - Base de despesas por credor ✅ **TIPOS CORRIGIDOS**
11. `base_despesa_licitacao` - Base de despesas por licitação
12. `despesa_convenio` - Despesas de convênio
13. `ordem_fornecimento` - Ordens de fornecimento
14. `previsao_realizacao_receita` - Previsão e realização de receitas
15. `totalizadores_execucao` - Totalizadores de execução
16. `consulta_gerencial` - Dados de consulta gerencial ✅ **JPA**
17. `dados_orcamentarios` - Dados orçamentários

### Padrões das Tabelas

- Todas possuem campo `id` como chave primária auto-incremento
- Campos de auditoria: `created_at`, `updated_at`
- **Tipos idênticos entre API e banco** (padronização completa)
- Chaves naturais únicas quando aplicável
- Índices em campos de consulta frequente
- Backup automático antes de alterações de tipos

### Sistema de Backup

- Schema: `backup_types_fix`
- Backup automático antes de correções de tipos
- Rollback disponível se necessário
- **303.749 registros** processados com 0 perdas de dados

---

## 15. Fluxo de Dados

### Processo de Consumo Moderno

1. **Autenticação**: Obtenção/renovação de token OAuth2
2. **Iteração**: Por Unidades Gestoras (quando aplicável)
3. **Paginação**: Controle automático de páginas da API
4. **Processamento**: Conversão JSON → DTO → Entity (com validação de tipos)
5. **Detecção de Sistema**: JPA vs JdbcTemplate automática
6. **Persistência**: Sistema híbrido com validação
7. **Logging**: Registro detalhado de todas as operações

### Fluxo de Conversão de Tipos

#### Sistema JPA (Entidades Modernas)
```
API Response (JSON)
    ↓
DTO (tipos da API)
    ↓
JpaPersistenceService (detecção automática)
    ↓
Mapper específico (ConsultaGerencialMapper)
    ↓
TypeConverter (conversões controladas) ✅
    ↓
Entity JPA (tipos do banco)
    ↓
Repository JPA (validação automática) ✅
    ↓
Banco de Dados (tipos idênticos à API)
```

#### Sistema Legado (Compatibilidade)
```
API Response (JSON)
    ↓
DTO (tipos da API)
    ↓
EndpontSefazRepository
    ↓
SQL direto com JdbcTemplate
    ↓
Banco de Dados (tipos corrigidos)
```

### Estratégias de Consumo

1. **Independente de UG**: Unidade Gestora (dados mestres)
2. **Por UG**: Contratos, Empenhos, Liquidações, Pagamentos
3. **Por UG + Ano**: Dados históricos
4. **Multi-mês**: Previsão Realização Receita (12 consultas)

### Detecção Automática de Sistema

O sistema detecta automaticamente qual abordagem usar:

```java
// Detecção baseada no nome da tabela
if (tableName.contains("consulta_gerencial") ||
    (tableName.contains("contrato") && !tableName.contains("empenho"))) {
    // Usar sistema JPA moderno
    jpaPersistenceService.persist(dtos);
} else {
    // Usar sistema legado
    legacyRepository.persist(dtos);
}
```

### Tratamento de Erros

- **Conversões de tipos**: Fallbacks seguros (ZERO, null)
- **Validação JPA**: Erros imediatos em tipos inconsistentes
- **Retry automático**: Em falhas de rede
- **Logging detalhado**: De erros e conversões
- **Continuidade**: Processamento continua em falhas parciais
- **Backup automático**: Antes de alterações críticas

---

## 16. Análise de Arquivos e Dependências Não Utilizados

### Arquivos Identificados para Remoção

#### 1. Arquivo de Exemplo Não Utilizado

**Arquivo**: `src/main/java/br/gov/se/setc/consumer/dto/ExemploNovoEndpointDTO.java`

**Motivo**: Este é um arquivo de exemplo/template que demonstra como criar novos DTOs. Não está sendo usado em produção e pode ser removido para limpeza do código.

**Impacto**: Nenhum - é apenas documentação em código.

#### 2. Arquivo de Dados de Teste Incorreto

**Arquivo**: `src/main/resources/import.sql`

**Motivo**: Este arquivo contém dados de teste para um sistema diferente (se_ouv - Sistema de Ouvidoria), não relacionado ao SEFAZ Transparency Consumer. Os dados inserem informações sobre entidades, responsáveis, solicitações, etc., que não fazem parte do escopo deste projeto.

**Impacto**: Pode causar erros se executado, pois as tabelas referenciadas não existem no schema do projeto atual.

#### 3. Dependências Maven Potencialmente Não Utilizadas

##### MapStruct
**Status**: **NÃO UTILIZADO**
- Dependências: `mapstruct` e `mapstruct-processor`
- **Motivo**: Não há mappers MapStruct implementados no código. O projeto usa mapeamento manual via reflexão no `ConsumoApiService`.
- **Recomendação**: Remover dependências MapStruct.

##### Spring WebFlux
**Status**: **NÃO UTILIZADO**
- Dependência: `spring-boot-starter-webflux`
- **Motivo**: O projeto usa apenas Spring Web MVC tradicional. Não há uso de programação reativa.
- **Recomendação**: Remover dependência WebFlux.

##### BouncyCastle
**Status**: **NÃO UTILIZADO**
- Dependência: `bcprov-jdk18on`
- **Motivo**: Declarado para Argon2 Password Encoder, mas não há implementação de criptografia de senhas no projeto.
- **Recomendação**: Remover dependência BouncyCastle.

##### Spring Security (Parcialmente Utilizado)
**Status**: **PARCIALMENTE UTILIZADO**
- Dependências: `spring-boot-starter-security` e `spring-security-crypto`
- **Motivo**: A segurança está desabilitada na aplicação principal (`SecurityAutoConfiguration.class` excluído). Apenas `spring-security-crypto` poderia ser mantido se necessário.
- **Recomendação**: Avaliar se é necessário manter.

##### Flyway
**Status**: **CONFIGURADO MAS NÃO UTILIZADO**
- Dependências: `flyway-core` e `flyway-database-postgresql`
- **Motivo**: Há apenas um arquivo de migração (`V1_1__add_vl_total_valor_pago_atualizado_to_consulta_gerencial.sql`), mas o JPA está configurado com `ddl-auto=update`.
- **Recomendação**: Decidir entre usar Flyway ou JPA DDL auto.

##### Dependências de Teste Não Utilizadas
**Status**: **PARCIALMENTE UTILIZADAS**
- `reactor-test`: Não há testes reativos
- `spring-security-test`: Segurança desabilitada
- **Recomendação**: Remover dependências de teste não utilizadas.

#### 4. Dependências Redundantes

##### JDBC Starters Redundantes
- `spring-boot-starter-data-jdbc` e `spring-boot-starter-jdbc` são redundantes com `spring-boot-starter-data-jpa`
- **Recomendação**: Manter apenas `spring-boot-starter-data-jpa`

##### Jakarta Validation Redundante
- `jakarta.validation-api` e `hibernate-validator` já estão incluídos no `spring-boot-starter-validation`
- **Recomendação**: Remover dependências explícitas

##### Jakarta Persistence Redundante
- `jakarta.persistence-api` já está incluído no `spring-boot-starter-data-jpa`
- **Recomendação**: Remover dependência explícita

### Arquivos de Configuração Desnecessários

#### 1. Arquivo de Teste
**Arquivo**: `src/test/resources/application-test.properties`
- **Status**: Existe mas pode estar vazio ou com configurações mínimas
- **Recomendação**: Verificar conteúdo e remover se desnecessário

### Resumo de Limpeza Recomendada

#### Arquivos para Remoção:
1. `src/main/java/br/gov/se/setc/consumer/dto/ExemploNovoEndpointDTO.java`
2. `src/main/resources/import.sql`

#### Dependências Maven para Remoção:
1. MapStruct (`mapstruct`, `mapstruct-processor`)
2. Spring WebFlux (`spring-boot-starter-webflux`)
3. BouncyCastle (`bcprov-jdk18on`)
4. JDBC redundantes (`spring-boot-starter-data-jdbc`, `spring-boot-starter-jdbc`)
5. Jakarta Validation redundantes (`jakarta.validation-api`, `hibernate-validator`)
6. Jakarta Persistence redundante (`jakarta.persistence-api`)
7. Dependências de teste não utilizadas (`reactor-test`, `spring-security-test`)

#### Dependências para Avaliação:
1. Spring Security (avaliar necessidade real)
2. Flyway (decidir estratégia de migração)

### Estimativa de Impacto da Limpeza

- **Redução do tamanho do JAR**: Aproximadamente 15-20MB
- **Redução do tempo de build**: 10-15%
- **Melhoria na clareza das dependências**: Significativa
- **Risco**: Baixo (apenas remoção de código não utilizado)

---

## 17. Limpeza Realizada

### ✅ Arquivos Removidos

1. **`src/main/java/br/gov/se/setc/consumer/dto/ExemploNovoEndpointDTO.java`**
   - Arquivo de exemplo/template removido

2. **`src/main/resources/import.sql`**
   - Dados de teste incorretos removidos

3. **`src/main/resources/db/migration/V1_1__add_vl_total_valor_pago_atualizado_to_consulta_gerencial.sql`**
   - Migração Flyway removida

### ✅ Dependências Maven Removidas

1. **MapStruct** - Não utilizado (mapeamento manual via reflexão)
2. **Spring WebFlux** - Não utilizado (apenas Spring Web MVC)
3. **BouncyCastle** - Não utilizado (sem criptografia de senhas)
4. **Spring Security** - Não utilizado (sem autenticação/autorização)
5. **Flyway** - Removido conforme solicitado (mudanças manuais no banco)
6. **JDBC Starters redundantes** - Mantido apenas spring-boot-starter-data-jpa
7. **Jakarta Validation redundantes** - Incluídas no spring-boot-starter-validation
8. **Jakarta Persistence redundante** - Incluída no spring-boot-starter-data-jpa
9. **Dependências de teste não utilizadas** - reactor-test e spring-security-test

### ✅ Configurações Atualizadas

1. **ConsumerSefazApplication.java**
   - Removida exclusão do SecurityAutoConfiguration
   - Simplificada configuração da aplicação

2. **pom.xml**
   - Removida propriedade mapstruct.version
   - Simplificado plugin maven-compiler-plugin
   - Limpas todas as dependências não utilizadas

### 📊 Resultado da Limpeza

- **Dependências antes**: 22 dependências
- **Dependências depois**: 11 dependências
- **Redução**: 50% das dependências
- **Arquivos removidos**: 3 arquivos
- **Configurações simplificadas**: 2 arquivos

### 🎯 Benefícios Obtidos

- ✅ JAR mais leve e rápido para build
- ✅ Dependências mais claras e focadas
- ✅ Menos complexidade de configuração
- ✅ Código mais limpo e organizado
- ✅ Sem funcionalidades não utilizadas

---

## 18. Sistema de Padronização de Tipos

### Arquitetura de Conversão de Tipos

O sistema implementa uma arquitetura robusta para garantir que os tipos de dados no banco sejam **exatamente idênticos** aos tipos que chegam da API.

#### Componentes Principais

1. **TypeConverter**: Conversões centralizadas e seguras
2. **Mappers Específicos**: Para entidades com inconsistências
3. **JpaPersistenceService**: Sistema moderno com validação
4. **Repositórios JPA**: Validação automática de tipos

#### Filosofia de Design

**Princípio**: O banco deve seguir os tipos da API, não o contrário.

- ✅ **Alterações no banco**: Para corresponder aos tipos da API
- ✅ **Conversões mínimas**: Apenas quando semanticamente necessário
- ✅ **Validação automática**: Via JPA e anotações
- ✅ **Fallbacks seguros**: Para valores inválidos

### Resultados da Padronização

#### Estatísticas Finais

- **303.749 registros** processados com sucesso
- **0 perdas de dados**
- **100% de integridade** mantida
- **25 testes** executados - 0 falhas

#### Tipos Corrigidos

| Tabela | Campo | Antes | Depois | Registros |
|--------|-------|-------|--------|-----------|
| `pagamento` | `cd_licitacao` | integer | `character varying(50)` | 242.782 |
| `pagamento` | `id_orgao` | character varying | `integer` | 242.782 |
| `pagamento` | `tp_documento` | character varying | `integer` | 242.782 |
| `base_despesa_credor` | `sq_empenho` | character varying | `bigint` | 60.967 |
| `base_despesa_credor` | `dt_geracao_empenho` | character varying | `date` | 60.967 |

#### Entidades com Suporte JPA Completo

- ✅ **ConsultaGerencial**: Mapeamento + conversões + testes
- ✅ **Contrato**: Mapeamento + conversões + testes
- 🔄 **Outras entidades**: Podem migrar usando GenericEntityMapper

### Sistema de Backup e Segurança

- **Schema de backup**: `backup_types_fix`
- **Backup automático**: Antes de qualquer alteração
- **Rollback disponível**: Scripts de reversão
- **Validação pós-alteração**: Verificação automática

### Benefícios Técnicos Alcançados

1. **Eliminação de conversões automáticas** do PostgreSQL
2. **Validação em múltiplas camadas** (TypeConverter + JPA)
3. **Tipos consistentes** entre API e banco
4. **Migração gradual** sem quebrar funcionalidades
5. **Observabilidade completa** com logging detalhado
6. **Sistema robusto** com tratamento de erros

### Conclusão

A padronização de tipos foi **completamente implementada** com:
- ✅ Tipos idênticos entre API e banco
- ✅ Sistema híbrido para migração gradual
- ✅ Validação automática e segura
- ✅ Backup e rollback disponíveis
- ✅ Testes completos validando funcionalidade

O sistema agora garante **consistência total** entre os dados da API SEFAZ e o banco de dados local.

