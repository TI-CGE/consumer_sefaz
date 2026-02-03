# Documentação Completa de Consumo de Entidades - API SEFAZ

Este documento descreve detalhadamente o consumo de cada entidade da API SEFAZ, incluindo formato, parâmetros, iterações e origem dos dados.

---

## Índice

1. [Unidade Gestora](#1-unidade-gestora)
2. [Receita](#2-receita)
3. [Empenho](#3-empenho)
4. [Empenho Mensal](#4-empenho-mensal)
5. [Pagamento](#5-pagamento)
6. [Liquidação](#6-liquidação)
7. [Consulta Gerencial](#7-consulta-gerencial)
8. [Totalizadores Execução](#8-totalizadores-execução)
9. [Base Despesa Credor](#9-base-despesa-credor)
10. [Base Despesa Licitação](#10-base-despesa-licitação)
11. [Contrato](#11-contrato)
12. [Contrato Empenho](#12-contrato-empenho)
13. [Contratos Fiscais](#13-contratos-fiscais)
14. [Termo](#14-termo)
15. [Despesa Convenio](#15-despesa-convenio)
16. [Ordem Fornecimento](#16-ordem-fornecimento)
17. [Previsão Realização Receita](#17-previsão-realização-receita)
18. [Despesa Detalhada](#18-despesa-detalhada)
19. [Dados Orçamentários](#19-dados-orçamentários)

---

## 1. Unidade Gestora

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.unidade_gestora`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora?sgTipoUnidadeGestora=E`
- **Tipo de Consumo**: Independente de UG e Data
- **Carga Completa**: Sim (busca todas as UGs ativas)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Filtro Fixo**: `sgTipoUnidadeGestora=E` (apenas unidades executivas)
- **Iterações**: Nenhuma - busca única sem parâmetros

### Parâmetros Utilizados
- **Parâmetros na URL**: `sgTipoUnidadeGestora=E` (fixo na URL base)
- **Parâmetros Query**: Nenhum
- **Origem dos Parâmetros**: Hardcoded na URL base

### Campos Mapeados
- `nm_unidade_gestora` ← `nmUnidadeGestora`
- `sg_unidade_gestora` ← `sgUnidadeGestora`
- `cd_unidade_gestora` ← `cdUnidadeGestora`
- `sg_tipo_unidade_gestora` ← `sgTipoUnidadeGestora`

### Observações
- Esta entidade é usada como base para obter a lista de UGs para outras consultas
- Não requer iteração sobre anos ou UGs
- Retorna todas as unidades gestoras ativas do tipo executivo

---

## 2. Receita

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.receita`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/receita`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs da tabela `unidade_gestora`)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs da tabela `unidade_gestora`)
- `nuAnoCelebracao`: Ano do exercício (origem: iteração de ano atual até ano atual - 5)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `nuAnoCelebracao`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuMesCelebracao`: Mês atual (origem: `ValidacaoUtil.getMesAtual()`)
- `inVigente`: "S" (fixo)

### Campos Mapeados
- `dt_fim_vigencia_convenio`, `cd_convenio`, `cd_unidade_gestora`, `nm_concedente`, `in_convenio_ficha_ingresso`, `dt_publicacao_convenio`, `ds_objeto_convenio`, `vl_concedente_convenio`, `cd_gestao`, `tx_ident_original_convenio`, `cd_concedente_pessoa`, `cd_efetivacao_usuario`, `tx_observacao_convenio`, `cd_beneficiario_pessoa`, `cd_convenio_situacao`, `cd_area_atuacao`, `dt_lancamento_convenio`, `dt_prazo_prest_contas_convenio`, `nm_beneficiario`, `dt_celebracao_convenio`, `dt_inicio_vigencia_convenio`, `sg_unidade_gestora`, `nm_convenio`, `in_local_publicacao_convenio`, `vl_contrapartida_convenio`, `in_convenio_empenho_ingresso`, `sq_unidade_gestora_gestao`

---

## 3. Empenho

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.empenho`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho`
- **Tipo de Consumo**: Por UG, Ano e cdGestao (com iteração)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Iteração cdGestao**: Sim

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por cdGestao (códigos de gestão obtidos da tabela `empenho` ou seed)
  - Por Mês (1 a 12) - iteração adicional

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoSeed()` ou `cdGestaoPorUgAno()`)
- `nuMes`: Número do mês 1-12 (origem: iteração de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Mês atual ou filtro específico (origem: `ValidacaoUtil.getMesAtual()` ou parâmetro)

### Campos Mapeados
- `dt_ano_exercicio_ctb`, `cd_unidade_gestora`, `cd_gestao`, `sg_unidade_gestora`, `id_orgao`, `sg_orgao`, `id_orgao_supervisor`, `sg_orgao_supervisor`, `sq_solic_empenho`, `sq_empenho`, `cd_natureza_despesa_completa`, `dt_geracao_empenho`, `dt_emissao_empenho`, `dt_lancamento_empenho`, `nm_razao_social_pessoa`, `nu_documento`, `vl_solic_empenho`, `vl_original_empenho`, `vl_total_reforcado_empenho`, `vl_total_anulado_empenho`, `vl_total_liquidado_empenho`, `vl_total_pago_empenho`, `vl_total_executado`, `cd_funcao`, `nm_funcao`, `cd_sub_funcao`, `nm_sub_funcao`, `cd_elemento_despesa`, `cd_fonte_recurso`, `cd_licitacao`, `ds_objeto_licitacao`, `nu_processo_licitacao`, `nm_modalidade_licitacao`

### Observações
- Requer iteração sobre cdGestao porque a API não retorna todos os empenhos de uma vez
- Para cada cdGestao, itera sobre os 12 meses
- Os códigos de gestão são obtidos da própria tabela `empenho` ou de um seed inicial

---

## 4. Empenho Mensal

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.empenho` (mesma tabela do Empenho)
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho`
- **Tipo de Consumo**: Por UG, Ano e Mês específico
- **Carga Completa**: Não (consulta específica por mês/ano)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: Nenhuma - consulta direta com mês e ano específicos

### Parâmetros Utilizados
- `cdUnidadeGestora`: Código da UG (origem: parâmetro do controller)
- `dtAnoExercicioCTB`: Ano do exercício (origem: parâmetro do controller)
- `nuMes`: Número do mês 1-12 (origem: parâmetro do controller)

### Observações
- Extensão do EmpenhoDTO para consultas específicas por mês
- Usado pelo `SwaggerEmpenhoMensalController` para consultas pontuais
- Não faz iteração automática, apenas consulta o mês/ano especificado

---

## 5. Pagamento

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.pagamento`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/pagamento`
- **Tipo de Consumo**: Por UG, Ano e cdGestao (com iteração)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Iteração cdGestao**: Sim

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por cdGestao (códigos de gestão)
  - Por Mês (1 a 12) - iteração adicional

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `nuAnoLancamento`: Ano do lançamento (origem: mesmo ano do exercício)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Número do mês 1-12 (origem: iteração de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuAnoLancamento`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Mês atual ou filtro específico (origem: `ValidacaoUtil.getMesAtual()` ou parâmetro)

### Campos Mapeados
- `dt_ano_exercicio_ctb`, `cd_unidade_gestora`, `cd_gestao`, `sg_unidade_gestora`, `id_orgao`, `sg_orgao`, `id_orgao_supervisor`, `sg_orgao_supervisor`, `sq_previsao_desembolso`, `sq_empenho`, `sq_ob`, `cd_natureza_despesa_completa`, `nu_documento`, `tp_documento`, `nm_razao_social_pessoa`, `vl_bruto_pd`, `vl_retido_pd`, `vl_ob`, `dt_ano_exercicio_ctb_referencia`, `dt_previsao_desembolso`, `dt_lancamento_ob`, `in_situacao_pagamento`, `situacao_ob`, `cd_funcao`, `nm_funcao`, `cd_sub_funcao`, `nm_sub_funcao`, `cd_elemento_despesa`, `cd_fonte_recurso`, `cd_licitacao`, `ds_objeto_licitacao`, `nu_processo_licitacao`, `nm_modalidade_licitacao`

---

## 6. Liquidação

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.liquidacao`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/liquidacao`
- **Tipo de Consumo**: Por UG, Ano e cdGestao (com iteração)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Iteração cdGestao**: Sim

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por cdGestao (códigos de gestão)
  - Por Mês (1 a 12) - iteração adicional

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `nuAnoLancamento`: Ano do lançamento (origem: mesmo ano do exercício)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Número do mês 1-12 (origem: iteração de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuAnoLancamento`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Mês atual ou filtro específico (origem: `ValidacaoUtil.getMesAtual()` ou parâmetro)

### Campos Mapeados
- `sq_empenho`, `sq_liquidacao`, `dt_liquidacao`, `cd_unidade_gestora`, `sg_unidade_gestora`, `id_orgao`, `sg_orgao`, `id_orgao_supervisor`, `sg_orgao_supervisor`, `vl_bruto_liquidacao`, `nu_documento`, `tp_documento`, `dt_ano_exercicio_ctb`, `cd_gestao`, `nm_razao_social_pessoa`, `cd_natureza_despesa`, `vl_estornado_liquidacao`, `cd_funcao`, `nm_funcao`, `cd_sub_funcao`, `nm_sub_funcao`, `cd_elemento_despesa`, `cd_fonte_recurso`, `cd_licitacao`, `ds_objeto_licitacao`, `nu_processo_licitacao`, `nm_modalidade_licitacao`

---

## 7. Consulta Gerencial

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.consulta_gerencial`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/diarias/consulta-gerencial`
- **Tipo de Consumo**: Por UG, Ano e cdGestao (com iteração)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Iteração cdGestao**: Sim

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por cdGestao (códigos de gestão)
  - Por Mês (1 a 12) - iteração adicional

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Número do mês 1-12 (origem: iteração de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `cdGestao`: Código de gestão (origem: `ValidacaoUtil.cdGestaoPorUgAno()`)
- `nuMes`: Mês atual ou filtro específico (origem: `ValidacaoUtil.getMesAtual()` ou parâmetro)

### Campos Mapeados
- `cd_unidade_gestora`, `sg_unidade_gestora`, `dt_ano_exercicio_ctb`, `cd_gestao`, `tx_motivo_solicitacao`, `dt_saida_solicitacao_diaria`, `dt_retorno_solicitacao_diaria`, `qtd_diaria_solicitacao_diaria`, `vl_total_solicitacao_diaria`, `vl_desconto_solicitacao_diaria`, `vl_valor_moeda`, `vl_total_valor_pago_atualizado`, `sq_solic_empenho`, `sq_empenho`, `sq_solicitacao_diaria`, `sq_ob`, `sq_previsao_desembolso`, `tp_documento`, `nu_documento`, `nm_razao_social_pessoa`, `ds_qualificacao_vinculo`, `destino_viagem_pais_solicitacao_diaria`, `destino_viagem_uf_solicitacao_diaria`, `destino_viagem_municipio_solicitacao_diaria`, `tp_transporte_viagem_solicitacao_diaria`, `tp_viagem_solicitacao_diaria`, `nm_cargo`

### Observações
- Consulta gerencial de diárias
- Requer conversão de campos String para tipos apropriados (datas, valores monetários)

---

## 8. Totalizadores Execução

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.totalizadores_execucao`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/totalizadores-execucao`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs ou parâmetro do controller)
- `dtAnoExercicioCTB`: Ano atual fixo em 2025 (hardcoded)

### Campos Mapeados
- `cd_programa_governo`, `cd_natureza_despesa_elemento_despesa`, `nm_natureza_despesa_elemento_despesa`, `nm_programa_governo`, `vl_total_pago`, `vl_total_liquidado`, `cd_unidade_orcamentaria`, `vl_total_empenhado`, `nm_fonte_recurso`, `nm_complemento_exec_orc`, `vl_total_dotacao_atualizada`, `cd_fonte_recurso_reduzida`, `cd_sub_acao`, `nm_sub_acao`, `sg_unidade_gestora`, `cd_unidade_gestora`, `dh_ultima_alteracao`, `nm_acao`, `dt_ano_exercicio_ctb`, `cd_acao`, `cd_gestao`, `cd_complemento_exec_orc`

### Observações
- Permite filtros opcionais via controller: `cdUnidadeGestora` e `dtAnoExercicioCTB`
- No modo atual, o ano é fixo em 2025 (hardcoded)

---

## 9. Base Despesa Credor

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.base_despesa_credor`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-credor`
- **Tipo de Consumo**: Por UG e Ano (com paginação)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Estrutura Especial**: Resposta aninhada com paginação

### Formato do Consumo
- **Estrutura de Resposta**: JSON aninhado `result > dados > colecao[]`
- **Paginação**: Controlada por `nuFaixaPaginacao` e `qtTotalFaixasPaginacao`
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por Faixa de Paginação (se necessário)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)
- `nuFaixaPaginacao`: 1 (inicial, pode ser iterado se houver múltiplas faixas)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano atual (origem: `Year.now().getValue()`)
- `nuFaixaPaginacao`: 1 (inicial)

### Campos Mapeados
- `cd_gestao`, `ug_cd`, `dt_ano_exercicio`, `sq_empenho`, `nu_documento_pessoa`, `cd_tipo_documento`, `nm_razao_social_pessoa`, `dh_lancamento_empenho`, `dt_geracao_empenho`, `vl_original_empenho`, `vl_total_liquidado_empenho`, `vl_total_pago_empenho`, `nm_modalidade_licitacao`, `cd_licitacao`, `nm_item_material_servico`, `qt_item_solicitacao_empenho`, `vl_unitario_item_solicitacao_empenho`, `created_at`, `updated_at`

### Observações
- Estrutura de resposta especial com paginação
- A resposta inclui metadados: `nuFaixaPaginacao`, `qtTotalFaixasPaginacao`, `msgUsuario`, `msgTecnica`, `cdRetorno`
- Processamento especial no `ConsumoApiService.processarRespostaBaseDespesaCredor()`

---

## 10. Base Despesa Licitação

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.base_despesa_licitacao`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/base-despesa-licitacao`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano atual (origem: `Year.now().getValue()`)

### Campos Mapeados
- `nu_processo`, `si_licitacao`, `nu_documento`, `nm_razao_social_ug`, `nm_razao_social_fornecedor`, `cd_licitacao`, `vl_licitacao`, `sg_unidade_gestora`, `cd_unidade_gestora`, `nm_modalidade`, `dt_homologacao`, `vl_estimado`, `ds_objeto`, `created_at`, `updated_at`

---

## 11. Contrato

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.contrato`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano fixo em 2025 (hardcoded)

### Campos Mapeados
- `sg_unidade_gestora`, `cd_unidade_gestora`, `dt_ano_exercicio`, `cd_contrato`, `cd_aditivo`, `dt_inicio_vigencia`, `dt_fim_vigencia`, `nm_categoria`, `nm_fornecedor`, `nu_documento`, `ds_objeto_contrato`, `vl_contrato`, `tp_contrato`

### Observações
- Conversão de datas de String para LocalDate no setter

---

## 12. Contrato Empenho

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.contrato_empenho`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-empenho`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestoraContrato`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestoraContrato`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano atual (origem: `Year.now().getValue()`)

### Campos Mapeados
- `cd_solicitacao_compra`, `ug_cd`, `ds_resumida_solicitacao_compra`, `cd_licitacao`, `dt_ano_exercicio`, `situacao`, `nm_modalidade_licitacao`, `crit_julg_licitacao`, `natureza_objeto_licitacao`, `ug_se`, `doc_se`, `valor_se`, `ug_ne`, `doc_ne`, `doc_credor_ne`, `nm_credor_ne`, `tipo_empenho`, `vl_original_ne`, `vltotal_reforco_ne`, `vltotal_anulado_ne`, `vltotal_liquidado_ne`, `vl_total_pago_ne`, `cd_contrato`, `tipo_contrato`, `vl_contrato`, `dt_inicio_vigencia_contrato`, `dt_fim_vigencia_contrato`, `created_at`, `updated_at`

### Observações
- Parâmetro `cdUnidadeGestoraContrato` (diferente do padrão `cdUnidadeGestora`)

---

## 13. Contratos Fiscais

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.contratos_fiscais`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)
- `cdQualificador`: 133 (fixo)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuMes`: Mês atual (origem: `ValidacaoUtil.getMesAtual()`)
- `cdQualificador`: 133 (fixo)

### Campos Mapeados
- `dt_fim_vigencia_contrato`, `sg_unidade_gestora`, `cd_licitacao`, `cd_unidade_gestora`, `nm_fiscal`, `nu_documento_contratado`, `nm_contratado`, `dt_ano_exercicio`, `cd_contrato`, `dt_inicio_vigencia_contrato`, `ds_qualificador`

### Observações
- Parâmetro `cdQualificador` fixo em 133

---

## 14. Termo

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.termo`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/termo`
- **Tipo de Consumo**: Por UG
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)

### Campos Mapeados
- `cd_convenio`, `cd_unidade_gestora`, `cd_gestao`, `sg_unidade_gestora`, `nm_convenio`, `ds_objeto_convenio`, `dt_celebracao_convenio`, `dt_inicio_vigencia_conv`, `dt_fim_vigencia_conv`, `dt_publicacao_convenio`, `nu_doc_oficial_convenio`, `tx_ident_original_conv`, `tx_observacao_convenio`, `cd_efetivacao_usuario`, `cd_convenio_situacao`, `cd_area_atuacao`, `in_local_publicacao_conv`

### Observações
- Aplica deduplicação baseada em `cdConvenio` antes de persistir
- Não requer parâmetros de ano na URL (mas itera por anos internamente)

---

## 15. Despesa Convenio

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.convenio_despesa`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/aco/v1/convenio/despesa`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `nuAnoLancamento`: Ano do lançamento (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `nuAnoLancamento`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuMesLancamento`: Mês atual (origem: `ValidacaoUtil.getMesAtual()`)
- `inVigente`: "S" (fixo)

### Campos Mapeados
- `dt_fim_vigencia_convenio`, `cd_convenio`, `cd_unidade_gestora`, `nm_concedente`, `in_convenio_ficha_ingresso`, `dt_publicacao_convenio`, `ds_objeto_convenio`, `vl_concedente_convenio`, `cd_gestao`, `tx_ident_original_convenio`, `cd_concedente_pessoa`, `cd_efetivacao_usuario`, `tx_observacao_convenio`, `cd_beneficiario_pessoa`, `cd_convenio_situacao`, `cd_area_atuacao`, `dt_lancamento_convenio`, `dt_prazo_prest_contas_convenio`, `nm_beneficiario`, `dt_celebracao_convenio`, `dt_inicio_vigencia_convenio`, `sg_unidade_gestora`, `nm_convenio`, `in_local_publicacao_convenio`, `vl_contrapartida_convenio`, `in_convenio_empenho_ingresso`, `sq_unidade_gestora_gestao`, `created_at`, `updated_at`

---

## 16. Ordem Fornecimento

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.ordem_fornecimento`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/ordem-fornecimento`
- **Tipo de Consumo**: Por UG e Ano
- **Carga Completa**: Sim (todos os anos: 2020-2025)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioEmp`: Ano do exercício do empenho (origem: iteração de anos)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioEmp`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `nuMesRecebimento`: Mês atual (origem: `ValidacaoUtil.getMesAtual()`)

### Campos Mapeados
- `cd_unidade_gestora`, `cd_unidade_administrativa`, `cd_gestao`, `dt_ano_exercicio_emp`, `dt_ano_exercicio_of`, `sq_empenho`, `sq_ordem_fornecimento`, `dt_recebimento`, `vl_ordem_fornecimento`, `nu_danfe`, `dt_emissao`, `nu_documento_destinatario`, `nm_destinatario`, `nu_documento_emitente`, `nm_remetente`, `vl_total_desconto`, `vl_total_nfe`, `tp_nfe`, `vl_total_prod_serv`, `vl_total_frete`, `vl_total_seguro`, `vl_base_calc_icms`, `vl_base_calc_icms_st`, `vl_total_icms_st`, `vl_total_icms`, `vl_ipi`

---

## 17. Previsão Realização Receita

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.previsao_realizacao_receita`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita`
- **Tipo de Consumo**: Por UG, Ano e Mês (múltiplas requisições)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Múltiplas Requisições**: Sim (12 meses)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por Mês (1 a 12) - obrigatório

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `nuMes`: Número do mês 1-12 (origem: iteração obrigatória de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano fixo em 2025 (hardcoded)
- `nuMes`: Mês 12 (padrão) ou filtro específico

### Campos Mapeados
- `cd_unidade_gestora`, `dt_ano_exercicio_ctb`, `nu_mes`, `cd_categoria_economica`, `nm_categoria_economica`, `cd_origem`, `nm_origem`, `cd_especie`, `nm_especie`, `cd_desdobramento`, `nm_desdobramento`, `cd_tipo`, `nm_tipo`, `vl_previsto`, `vl_atualizado`, `vl_realizado`

### Observações
- **Obrigatório**: Parâmetro `nuMes` (1-12)
- Requer 12 requisições por UG/Ano (uma para cada mês)
- Método `getCamposParametrosTodosMeses()` retorna lista com 12 mapas de parâmetros
- Pausa de 500ms entre requisições (configurável)

---

## 18. Despesa Detalhada

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.despesa_detalhada`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/despesa-detalhada`
- **Tipo de Consumo**: Por UG, Ano e Mês (múltiplas requisições)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Múltiplas Requisições**: Sim (12 meses)

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por Mês (1 a 12) - obrigatório

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano do exercício (origem: iteração de anos)
- `nuMes`: Número do mês 1-12 (origem: iteração obrigatória de 1 a 12)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicio`: Ano fixo em 2025 (hardcoded)
- `nuMes`: Mês 12 (padrão) ou filtro específico

### Campos Mapeados
- `cd_unidade_gestora`, `dt_ano_exercicio_ctb`, `nu_mes`, `cd_orgao`, `nm_orgao`, `cd_unid_orc`, `nm_unid_orc`, `cd_funcao`, `nm_funcao`, `cd_sub_funcao`, `nm_sub_funcao`, `cd_programa_governo`, `nm_programa_governo`, `cd_ppa_acao`, `nm_ppa_acao`, `cd_sub_acao`, `nm_subacao`, `cd_categoria_economica`, `nm_categoria_economica`, `cd_natureza_despesa`, `nm_natureza_despesa`, `vl_dotacao_inicial`, `vl_credito_adicional`, `vl_dotacao_atualizada`, `vl_total_empenhado`, `vl_total_liquidado`, `vl_total_pago`, `created_at`, `updated_at`

### Observações
- **Obrigatório**: Parâmetro `nuMes` (1-12)
- Requer 12 requisições por UG/Ano (uma para cada mês)
- Método `getCamposParametrosTodosMeses()` retorna lista com 12 mapas de parâmetros
- Aplica deduplicação baseada em chave composta antes de persistir
- Método `definirCamposDerivados()` preenche campos obrigatórios a partir dos parâmetros

---

## 19. Dados Orçamentários

### Informações Gerais
- **Tabela Banco**: `consumer_sefaz.dados_orcamentarios`
- **URL Base**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v1/empenho/dados-orcamentarios`
- **Tipo de Consumo**: Por UG, Ano e Empenho (iteração sobre empenhos)
- **Carga Completa**: Sim (todos os anos: 2020-2025)
- **Requer Iteração Empenhos**: Sim

### Formato do Consumo
- **Estrutura de Resposta**: Array JSON direto
- **Iterações**: 
  - Por UG (todas as UGs)
  - Por Ano (ano atual até ano atual - 5)
  - Por Empenho (todos os empenhos da UG/Ano)

### Parâmetros Utilizados

#### Modo Carga Completa (Todos os Anos)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano do exercício (origem: iteração de anos)
- `cdGestao`: Código de gestão (origem: tabela `empenho` - DISTINCT cd_gestao, sq_empenho)
- `sqEmpenho`: Sequencial do empenho (origem: tabela `empenho`)

#### Modo Atualização Incremental (Ano Atual)
- `cdUnidadeGestora`: Código da UG (origem: lista de UGs)
- `dtAnoExercicioCTB`: Ano atual (origem: `ValidacaoUtil.getAnoAtual()`)
- `cdGestao`: Código de gestão (origem: tabela `empenho`)
- `sqEmpenho`: Sequencial do empenho (origem: tabela `empenho`)

### Campos Mapeados
- `cd_funcao_plo`, `nm_funcao_plo`, `cd_sub_funcao`, `nm_sub_funcao`, `cd_programa_governo`, `nm_programa_governo`, `cd_categoria_economica`, `nm_categoria_economica`, `cd_modalidade_aplicacao`, `nm_modalidade_aplicacao`, `cd_fonte_recurso`, `nm_fonte_recurso`, `cd_grupo_despesa`, `nm_grupo_despesa`, `cd_licitacao`, `ds_objeto_licitacao`, `nu_processo_licitacao`, `cd_elemento_despesa`, `nm_elemento_despesa`, `cd_tipo_despesa`, `ds_tipo_despesa`

### Observações
- **Todos os parâmetros são obrigatórios**: cdUnidadeGestora, dtAnoExercicioCTB, cdGestao, sqEmpenho
- Os empenhos são obtidos da tabela `consumer_sefaz.empenho` com a query:
  ```sql
  SELECT DISTINCT cd_gestao, sq_empenho
  FROM consumer_sefaz.empenho
  WHERE cd_unidade_gestora = ? AND dt_ano_exercicio_ctb = ?
    AND cd_gestao IS NOT NULL AND sq_empenho IS NOT NULL
  ```
- Método `criarParametrosComEmpenho()` cria parâmetros específicos para cada empenho
- Processamento especial no `ConsumoApiService.processarIterandoEmpenhos()`

---

## Resumo das Estratégias de Consumo

### 1. Consumo Independente de UG e Data
- **Entidades**: Unidade Gestora
- **Características**: Uma única requisição sem parâmetros de UG ou ano

### 2. Consumo por UG e Ano (Simples)
- **Entidades**: Receita, Totalizadores Execução, Base Despesa Licitação, Contrato, Contrato Empenho, Contratos Fiscais, Termo, Despesa Convenio, Ordem Fornecimento
- **Características**: Itera sobre UGs e anos, uma requisição por combinação

### 3. Consumo por UG, Ano e cdGestao (com Mês)
- **Entidades**: Empenho, Pagamento, Liquidação, Consulta Gerencial
- **Características**: Itera sobre UGs, anos, códigos de gestão e meses (1-12)

### 4. Consumo por UG, Ano e Mês (Múltiplas Requisições)
- **Entidades**: Previsão Realização Receita, Despesa Detalhada
- **Características**: Requer 12 requisições obrigatórias (uma por mês) por UG/Ano

### 5. Consumo por UG, Ano e Empenho (Iteração sobre Empenhos)
- **Entidades**: Dados Orçamentários
- **Características**: Itera sobre todos os empenhos da UG/Ano para obter dados orçamentários

### 6. Consumo com Paginação
- **Entidades**: Base Despesa Credor
- **Características**: Resposta aninhada com controle de paginação por faixas

---

## Origem dos Parâmetros

### Parâmetros Fixos/Hardcoded
- `sgTipoUnidadeGestora=E` (Unidade Gestora)
- `cdQualificador=133` (Contratos Fiscais)
- `inVigente=S` (Receita, Despesa Convenio)
- `dtAnoExercicioCTB=2025` (Totalizadores Execução, Contrato, Previsão Realização Receita, Despesa Detalhada - modo atual)

### Parâmetros do Sistema
- `ValidacaoUtil.getAnoAtual()`: Ano atual do sistema
- `ValidacaoUtil.getMesAtual()`: Mês atual do sistema
- `ValidacaoUtil.getUgs()`: Lista de UGs da tabela `unidade_gestora`

### Parâmetros de Iteração
- **Anos**: De ano atual até (ano atual - 5) = 6 anos total
- **Meses**: De 1 a 12 (para entidades que requerem)
- **cdGestao**: Obtido de `ValidacaoUtil.cdGestaoPorUgAno()` ou `cdGestaoSeed()`
- **Empenhos**: Obtidos da tabela `empenho` via query SQL

### Parâmetros do Controller
- Alguns controllers permitem filtros opcionais via parâmetros HTTP (ex: Totalizadores Execução)

---

## Observações Gerais

1. **Modo de Operação**: O sistema detecta automaticamente se há dados no banco:
   - **Sem dados**: Modo "Carga Completa" (todos os anos: 2020-2025)
   - **Com dados**: Modo "Atualização Incremental" (apenas ano atual)

2. **Deduplicação**: Aplicada antes da persistência para:
   - Termo (baseada em `cdConvenio`)
   - Despesa Detalhada (baseada em chave composta)

3. **Estruturas Especiais**:
   - Base Despesa Credor: Resposta aninhada com paginação
   - Dados Orçamentários: Requer iteração sobre empenhos

4. **Persistência**: 
   - JPA para entidades com suporte
   - JDBC Template para outras entidades

5. **Logging**: Sistema completo de logs com:
   - MarkdownLogger
   - UnifiedLogger
   - UserFriendlyLogger
   - SimpleLogger

---

**Data de Geração**: 2025-01-18
**Versão do Documento**: 1.0

