# Ajustes na Entidade Consulta Gerencial

## Resumo das Alterações

### 1. Análise da Resposta da API vs Entidade

**Resposta da API (exemplo):**
```json
{
    "dtRetornoSolicitacaoDiaria": "2025-06-10",
    "dtSaidaSolicitacaoDiaria": "2025-06-10",
    "sqOB": 454,
    "vlTotalValorPagoAtualizado": 25,
    "tpViagemSolicitacaoDiaria": "M",
    "destinoViagemMunicipioSolicitacaoDiaria": "ARAUA",
    "destinoViagemPaisSolicitacaoDiaria": "BRASIL",
    "nmRazaoSocialPessoa": "MOACIR SENA RIBEIRO",
    "qtdDiariaSolicitacaoDiaria": 1,
    "tpTransporteViagemSolicitacaoDiaria": "C",
    "cdUnidadeGestora": "241101",
    "dsQualificacaoVinculo": "DEMAIS CARGOS, FUNÇÕES OU EMPREGOS.",
    "dtAnoExercicioCTB": 2025,
    "destinoViagemUFSolicitacaoDiaria": "SERGIPE",
    "cdGestao": "00001",
    "nuDocumento": "96437634553",
    "tpDocumento": 3,
    "txMotivoSolicitacao": "FISCALIZACAO EM ARAUA/SE, NO DIA 10/06/2025, C.I. 1067/2025.",
    "sqSolicEmpenho": 320,
    "sqEmpenho": 320,
    "sgUnidadeGestora": "SUPDEC",
    "sqSolicitacaoDiaria": 140,
    "sqPrevisaoDesembolso": 466,
    "vlTotalSolicitacaoDiaria": 25
}
```

### 2. Mapeamento de Campos

Todos os campos da resposta da API estão corretamente mapeados na entidade:

| Campo da API | Campo da Entidade | Tipo | Status |
|--------------|-------------------|------|--------|
| cdUnidadeGestora | cd_unidade_gestora | String | ✅ OK |
| sgUnidadeGestora | sg_unidade_gestora | String | ✅ OK |
| dtAnoExercicioCTB | dt_ano_exercicio_ctb | Integer | ✅ OK |
| cdGestao | cd_gestao | String | ✅ OK |
| txMotivoSolicitacao | tx_motivo_solicitacao | String (TEXT) | ✅ OK |
| dtSaidaSolicitacaoDiaria | dt_saida_solicitacao_diaria | LocalDate | ✅ OK |
| dtRetornoSolicitacaoDiaria | dt_retorno_solicitacao_diaria | LocalDate | ✅ OK |
| qtdDiariaSolicitacaoDiaria | qtd_diaria_solicitacao_diaria | Integer | ✅ OK |
| vlTotalSolicitacaoDiaria | vl_total_solicitacao_diaria | BigDecimal(18,2) | ✅ OK |
| vlDescontoSolicitacaoDiaria | vl_desconto_solicitacao_diaria | BigDecimal(18,2) | ✅ OK |
| vlValorMoeda | vl_valor_moeda | BigDecimal(18,2) | ✅ OK |
| vlTotalValorPagoAtualizado | vl_total_valor_pago_atualizado | BigDecimal(18,2) | ✅ OK |
| sqSolicEmpenho | sq_solic_empenho | Long | ✅ OK |
| sqEmpenho | sq_empenho | Long | ✅ OK |
| sqSolicitacaoDiaria | sq_solicitacao_diaria | Long | ✅ OK |
| sqOB | sq_ob | Long | ✅ OK |
| sqPrevisaoDesembolso | sq_previsao_desembolso | Long | ✅ OK |
| tpDocumento | tp_documento | Integer | ✅ OK |
| nuDocumento | nu_documento | String | ✅ OK |
| nmRazaoSocialPessoa | nm_razao_social_pessoa | String | ✅ OK |
| dsQualificacaoVinculo | ds_qualificacao_vinculo | String | ✅ OK |
| destinoViagemPaisSolicitacaoDiaria | destino_viagem_pais_solicitacao_diaria | String | ✅ OK |
| destinoViagemUFSolicitacaoDiaria | destino_viagem_uf_solicitacao_diaria | String | ✅ OK |
| destinoViagemMunicipioSolicitacaoDiaria | destino_viagem_municipio_solicitacao_diaria | String | ✅ OK |
| tpTransporteViagemSolicitacaoDiaria | tp_transporte_viagem_solicitacao_diaria | String | ✅ OK |
| tpViagemSolicitacaoDiaria | tp_viagem_solicitacao_diaria | String | ✅ OK |
| nmCargo | nm_cargo | String | ✅ OK |

### 3. Alterações Realizadas no DTO

**Problema identificado:** Alguns campos de valor vêm como números na API, mas o DTO estava esperando apenas strings.

**Solução:** Adicionados setters que aceitam `Number` para os campos de valor:

```java
// Adicionado para vlTotalValorPagoAtualizado
public void setVlTotalValorPagoAtualizado(Number vlTotalValorPagoAtualizado) {
    if (vlTotalValorPagoAtualizado != null) {
        setVlTotalValorPagoAtualizadoStr(vlTotalValorPagoAtualizado.toString());
    }
}

// Adicionado para vlTotalSolicitacaoDiaria
public void setVlTotalSolicitacaoDiaria(Number vlTotalSolicitacaoDiaria) {
    if (vlTotalSolicitacaoDiaria != null) {
        setVlTotalSolicitacaoDiariaStr(vlTotalSolicitacaoDiaria.toString());
    }
}

// Adicionado para vlDescontoSolicitacaoDiaria
public void setVlDescontoSolicitacaoDiaria(Number vlDescontoSolicitacaoDiaria) {
    if (vlDescontoSolicitacaoDiaria != null) {
        setVlDescontoSolicitacaoDiariaStr(vlDescontoSolicitacaoDiaria.toString());
    }
}

// Adicionado para vlValorMoeda
public void setVlValorMoeda(Number vlValorMoeda) {
    if (vlValorMoeda != null) {
        setVlValorMoedaStr(vlValorMoeda.toString());
    }
}
```

### 4. Campos Opcionais

✅ **Todos os campos são opcionais** conforme solicitado. Nenhum campo tem `nullable = false` na entidade.

### 5. Scripts SQL Criados

- `scripts/verificar_consulta_gerencial.sql` - Para verificar estrutura da tabela
- `scripts/ajustar_consulta_gerencial.sql` - Para criar/ajustar a tabela no banco

### 6. Estrutura da Tabela

```sql
CREATE TABLE consumer_sefaz.consulta_gerencial (
    id BIGSERIAL PRIMARY KEY,
    cd_unidade_gestora VARCHAR(20),
    sg_unidade_gestora VARCHAR(100),
    dt_ano_exercicio_ctb INTEGER,
    cd_gestao VARCHAR(20),
    tx_motivo_solicitacao TEXT,
    dt_saida_solicitacao_diaria DATE,
    dt_retorno_solicitacao_diaria DATE,
    qtd_diaria_solicitacao_diaria INTEGER,
    vl_total_solicitacao_diaria NUMERIC(18,2),
    vl_desconto_solicitacao_diaria NUMERIC(18,2),
    vl_valor_moeda NUMERIC(18,2),
    vl_total_valor_pago_atualizado NUMERIC(18,2),
    sq_solic_empenho BIGINT,
    sq_empenho BIGINT,
    sq_solicitacao_diaria BIGINT,
    sq_ob BIGINT,
    sq_previsao_desembolso BIGINT,
    tp_documento INTEGER,
    nu_documento VARCHAR(50),
    nm_razao_social_pessoa VARCHAR(255),
    ds_qualificacao_vinculo VARCHAR(255),
    destino_viagem_pais_solicitacao_diaria VARCHAR(100),
    destino_viagem_uf_solicitacao_diaria VARCHAR(100),
    destino_viagem_municipio_solicitacao_diaria VARCHAR(100),
    tp_transporte_viagem_solicitacao_diaria VARCHAR(10),
    tp_viagem_solicitacao_diaria VARCHAR(10),
    nm_cargo VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7. Testes Criados

**Testes para ConsultaGerencialDTO:**
- `src/test/java/br/gov/se/setc/consumer/dto/ConsultaGerencialDTOTest.java`
- Valida os novos setters que aceitam valores numéricos
- Testa conversões de Integer, Double, String e valores nulos
- Verifica inicialização e mapeamento de campos

**Testes para ConsultaGerencialMapper:**
- `src/test/java/br/gov/se/setc/consumer/mapper/ConsultaGerencialMapperTest.java`
- Valida conversão de DTO para Entity
- Testa cenários com valores nulos, zero e decimais
- Verifica mapeamento completo dos campos

### 8. Próximos Passos

1. ✅ Executar o script SQL para ajustar a tabela no banco
2. ✅ Executar os testes criados para validar as alterações
3. ✅ Testar o consumo da API com os novos setters
4. ✅ Verificar se todos os campos estão sendo persistidos corretamente

### 9. Scripts SQL Disponíveis

- `scripts/verificar_consulta_gerencial.sql` - Verificar estrutura da tabela
- `scripts/ajustar_consulta_gerencial.sql` - Criar/ajustar a tabela no banco

### 10. Comandos para Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar apenas os testes da ConsultaGerencial
mvn test -Dtest=ConsultaGerencialDTOTest,ConsultaGerencialMapperTest
```

### 11. Observações

- A entidade já estava bem estruturada e com todos os campos necessários
- O principal ajuste foi adicionar flexibilidade no DTO para aceitar valores numéricos
- Todos os campos seguem o padrão de nomenclatura snake_case no banco
- O mapeamento JPA está correto e consistente
- Testes criados garantem que as alterações funcionem corretamente
