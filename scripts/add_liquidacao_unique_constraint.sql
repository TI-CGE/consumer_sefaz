DELETE FROM consumer_sefaz.liquidacao a
USING consumer_sefaz.liquidacao b
WHERE a.dt_ano_exercicio_ctb = b.dt_ano_exercicio_ctb
  AND a.cd_unidade_gestora = b.cd_unidade_gestora
  AND a.cd_gestao = b.cd_gestao
  AND a.sq_empenho = b.sq_empenho
  AND a.sq_liquidacao = b.sq_liquidacao
  AND a.ctid < b.ctid;

CREATE UNIQUE INDEX IF NOT EXISTS uq_liquidacao
ON consumer_sefaz.liquidacao (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho, sq_liquidacao);
