CREATE UNIQUE INDEX IF NOT EXISTS uq_pagamento
ON consumer_sefaz.pagamento (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho, sq_ob);

CREATE UNIQUE INDEX IF NOT EXISTS uq_liquidacao
ON consumer_sefaz.liquidacao (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho, sq_liquidacao);

CREATE UNIQUE INDEX IF NOT EXISTS uq_restos_a_pagar
ON consumer_sefaz.restos_a_pagar (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho);

CREATE UNIQUE INDEX IF NOT EXISTS uq_contrato_empenho
ON consumer_sefaz.contrato_empenho (cd_solicitacao_compra, ug_cd, dt_ano_exercicio);

CREATE UNIQUE INDEX IF NOT EXISTS uq_ordem_fornecimento
ON consumer_sefaz.ordem_fornecimento (cd_unidade_gestora, cd_gestao, dt_ano_exercicio_emp, sq_empenho, sq_ordem_fornecimento);

CREATE UNIQUE INDEX IF NOT EXISTS uq_receita
ON consumer_sefaz.receita (cd_convenio);
