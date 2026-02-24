DELETE FROM consumer_sefaz.pagamento a
USING consumer_sefaz.pagamento b
WHERE a.dt_ano_exercicio_ctb = b.dt_ano_exercicio_ctb
  AND a.cd_unidade_gestora = b.cd_unidade_gestora
  AND a.cd_gestao = b.cd_gestao
  AND a.sq_empenho = b.sq_empenho
  AND a.sq_ob = b.sq_ob
  AND a.ctid < b.ctid;

CREATE UNIQUE INDEX IF NOT EXISTS uq_pagamento
ON consumer_sefaz.pagamento (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho, sq_ob);
