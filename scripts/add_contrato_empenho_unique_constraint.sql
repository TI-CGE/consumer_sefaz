DELETE FROM consumer_sefaz.contrato_empenho a
USING consumer_sefaz.contrato_empenho b
WHERE a.cd_solicitacao_compra = b.cd_solicitacao_compra
  AND a.ug_cd = b.ug_cd
  AND a.dt_ano_exercicio = b.dt_ano_exercicio
  AND a.ctid < b.ctid;

CREATE UNIQUE INDEX IF NOT EXISTS uq_contrato_empenho
ON consumer_sefaz.contrato_empenho (cd_solicitacao_compra, ug_cd, dt_ano_exercicio);
