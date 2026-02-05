CREATE UNIQUE INDEX IF NOT EXISTS uq_empenho
ON consumer_sefaz.empenho (dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao, sq_empenho);
