DELETE FROM consumer_sefaz.empenho a
USING consumer_sefaz.empenho b
WHERE a.id > b.id
  AND a.dt_ano_exercicio_ctb IS NOT DISTINCT FROM b.dt_ano_exercicio_ctb
  AND a.cd_unidade_gestora IS NOT DISTINCT FROM b.cd_unidade_gestora
  AND a.cd_gestao IS NOT DISTINCT FROM b.cd_gestao
  AND a.sq_empenho IS NOT DISTINCT FROM b.sq_empenho;

CREATE UNIQUE INDEX IF NOT EXISTS uq_empenho
ON consumer_sefaz.empenho (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho);
