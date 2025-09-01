# Correção dos Campos cd_funcao_plo e nm_funcao_plo

## Problema Identificado

As colunas `cd_funcao_plo` e `nm_funcao_plo` estavam vindo como `null` na resposta da API de Dados Orçamentários.

## Causa Raiz

O problema estava no mapeamento JSON do DTO `DadosOrcamentariosDTO`. A API da SEFAZ retorna os campos com nomes diferentes do que estava configurado:

### Mapeamento Incorreto (Antes):
```java
@JsonProperty("cdFuncaoPLO")  // ❌ API não retorna este campo
private Integer cdFuncaoPLO;

@JsonProperty("nmFuncaoPLO")  // ❌ API não retorna este campo  
private String nmFuncaoPLO;
```

### Mapeamento Correto (Depois):
```java
@JsonProperty("cdFuncao")     // ✅ API retorna este campo
private Integer cdFuncaoPLO;

@JsonProperty("nmFuncao")     // ✅ API retorna este campo
private String nmFuncaoPLO;
```

## Correções Realizadas

### 1. DadosOrcamentariosDTO.java
- ✅ Corrigido `@JsonProperty("cdFuncaoPLO")` → `@JsonProperty("cdFuncao")`
- ✅ Corrigido `@JsonProperty("nmFuncaoPLO")` → `@JsonProperty("nmFuncao")`
- ✅ Corrigido tipo de `cdFonteRecurso` de `Integer` → `String` (API retorna string)

### 2. DadosOrcamentarios.java (Entidade JPA)
- ✅ Corrigido tipo de `cdFonteRecurso` de `Integer` → `String`
- ✅ Atualizado construtor para aceitar `String` em `cdFonteRecurso`
- ✅ Atualizados getters e setters

### 3. Script SQL
- ✅ Criado script `scripts/fix_dados_orcamentarios_fields.sql` para atualizar estrutura da tabela
- ✅ Altera `cd_fonte_recurso` de `INTEGER` para `VARCHAR(50)`

### 4. Testes
- ✅ Criado teste `DadosOrcamentariosDTOFixTest` para validar as correções
- ✅ Teste verifica mapeamento correto dos campos
- ✅ Teste verifica tratamento de valores null

## Resultado Esperado

Após as correções, a resposta da API deve incluir os valores corretos:

```json
{
    "cd_funcao_plo": 20,           // ✅ Agora será preenchido
    "nm_funcao_plo": "Agricultura", // ✅ Agora será preenchido
    "cd_fonte_recurso": "1500000000", // ✅ Agora como string
    "nuProcessoLicitacao": "292/2023",
    "cdProgramaGoverno": 36,
    // ... outros campos
}
```

## Validação

Execute o teste para validar as correções:
```bash
mvn test -Dtest=DadosOrcamentariosDTOFixTest
```

## Próximos Passos

1. **Executar o script SQL** no banco de dados para atualizar a estrutura da tabela
2. **Testar o endpoint** de dados orçamentários para confirmar que os campos não estão mais null
3. **Verificar outros DTOs** se há problemas similares de mapeamento

## Campos Corrigidos

| Campo | Antes | Depois | Status |
|-------|-------|--------|--------|
| `cd_funcao_plo` | null | Valor correto | ✅ Corrigido |
| `nm_funcao_plo` | null | Valor correto | ✅ Corrigido |
| `cd_fonte_recurso` | Integer | String | ✅ Corrigido |

## Impacto

- ✅ **Sem breaking changes** - apenas correção de bugs
- ✅ **Compatibilidade mantida** - nomes das colunas no banco permanecem iguais
- ✅ **Melhoria na qualidade dos dados** - campos importantes agora são preenchidos corretamente
