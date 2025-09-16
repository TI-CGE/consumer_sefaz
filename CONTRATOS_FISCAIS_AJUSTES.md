# Ajustes no Mapeamento de Contratos Fiscais

## Resumo das Alterações

### 1. Análise da API vs Documentação

**Resposta Real da API:**
```json
{
    "dtFimVigenciaContrato": "2022-06-30",
    "sgUnidadeGestora": "ADEMA", 
    "cdLicitacao": "3220112019000061",
    "cdUnidadeGestora": "322011",
    "nmFiscal": "GILVAN DIAS DOS SANTOS",
    "nuDocumentoContratado": "12487586000140",
    "nmContratado": "MED E SERVI OS LTDA",
    "dsQualificador": "FISCAL DE CONTRATO",
    "dtAnoExercicio": 2021,
    "cdContrato": "3220112024000014",
    "dtInicioVigenciaContrato": "2021-07-01"
}
```

**Campos Mapeados:**
- ✅ sgUnidadeGestora
- ✅ cdUnidadeGestora  
- ✅ dtAnoExercicio
- ✅ cdContrato
- ✅ cdLicitacao
- ✅ dtInicioVigenciaContrato
- ✅ dtFimVigenciaContrato
- ✅ nmContratado
- ✅ nuDocumentoContratado
- ✅ nmFiscal
- ✅ dsQualificador

### 2. Scripts SQL Criados

#### `scripts/ajustar_contratos_fiscais.sql`
- Cria o schema `consumer_sefaz` se não existir
- Cria a tabela `contratos_fiscais` com todos os campos necessários
- Verifica e adiciona colunas que estão faltando
- Configura todas as colunas como nullable (opcionais)
- Cria índices para melhorar performance
- Adiciona campos de auditoria (created_at, updated_at)

#### `scripts/verificar_contratos_fiscais.sql`
- Verifica se a tabela existe
- Lista a estrutura atual da tabela
- Verifica se todas as colunas necessárias existem
- Lista índices existentes
- Mostra registros de exemplo
- Verifica se as colunas são nullable

### 3. Alterações na Entidade

#### `src/main/java/br/gov/se/setc/consumer/entity/ContratosFiscais.java`

**Alterações realizadas:**
- ✅ Adicionadas anotações `@Column` com `nullable = true` para todos os campos
- ✅ Especificados tamanhos apropriados para campos VARCHAR
- ✅ Adicionados campos de auditoria (`createdAt`, `updatedAt`)
- ✅ Adicionadas anotações `@CreationTimestamp` e `@UpdateTimestamp`
- ✅ Criados getters e setters para os novos campos

**Estrutura da tabela:**
```sql
CREATE TABLE consumer_sefaz.contratos_fiscais (
    id BIGSERIAL PRIMARY KEY,
    sg_unidade_gestora VARCHAR(100),     -- nullable
    cd_unidade_gestora VARCHAR(20),      -- nullable  
    dt_ano_exercicio INTEGER,            -- nullable
    cd_contrato VARCHAR(50),             -- nullable
    cd_licitacao VARCHAR(50),            -- nullable
    dt_inicio_vigencia_contrato DATE,    -- nullable
    dt_fim_vigencia_contrato DATE,       -- nullable
    nm_contratado VARCHAR(255),          -- nullable
    nu_documento_contratado VARCHAR(20), -- nullable
    nm_fiscal VARCHAR(255),              -- nullable
    ds_qualificador VARCHAR(255),        -- nullable
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Alterações no DTO

#### `src/main/java/br/gov/se/setc/consumer/dto/ContratosFiscaisDTO.java`

**Alterações realizadas:**
- ✅ Removido campo `cdQualificador` (Integer) que não existe na API real
- ✅ Removidas referências ao `cdQualificador` no método `mapearParametros()`
- ✅ Mantidos todos os campos que existem na resposta real da API

**Campos mantidos:**
- sgUnidadeGestora
- cdUnidadeGestora
- dtAnoExercicio
- cdContrato
- cdLicitacao
- dtInicioVigenciaContrato
- dtFimVigenciaContrato
- nmContratado
- nuDocumentoContratado
- nmFiscal
- dsQualificador

### 5. Repository

#### `src/main/java/br/gov/se/setc/consumer/repository/EndpontSefazRepository.java`

**Status:** ✅ Não necessita alterações
- O método `persistContratosFiscais()` já está correto
- O SQL de INSERT mapeia todos os campos necessários
- A ordem dos parâmetros está correta

### 6. Próximos Passos

1. **Executar o script de ajuste:**
   ```bash
   # Execute o script no banco de dados
   psql -h 172.28.65.26 -p 5432 -U gideon -d "setc@bd" -f scripts/ajustar_contratos_fiscais.sql
   ```

2. **Verificar a estrutura:**
   ```bash
   # Execute o script de verificação
   psql -h 172.28.65.26 -p 5432 -U gideon -d "setc@bd" -f scripts/verificar_contratos_fiscais.sql
   ```

3. **Testar a aplicação:**
   - Compilar o projeto
   - Executar testes
   - Testar o endpoint `/contratos-fiscais`

### 7. Índices Criados

Para melhorar a performance, foram criados os seguintes índices:
- `idx_contratos_fiscais_cd_unidade_gestora`
- `idx_contratos_fiscais_dt_ano_exercicio`
- `idx_contratos_fiscais_cd_contrato`
- `idx_contratos_fiscais_dt_inicio_vigencia`
- `idx_contratos_fiscais_dt_fim_vigencia`

### 8. Validações

✅ **Todos os campos são opcionais** - Configurados como nullable
✅ **Mapeamento alinhado com a API real** - Removido campo inexistente
✅ **Estrutura do banco atualizada** - Scripts SQL criados
✅ **Entidade JPA atualizada** - Anotações e campos de auditoria
✅ **DTO limpo** - Removidas referências desnecessárias
