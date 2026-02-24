CREATE UNIQUE INDEX IF NOT EXISTS uq_ordem_fornecimento
ON consumer_sefaz.ordem_fornecimento (cd_unidade_gestora, cd_gestao, dt_ano_exercicio_emp, sq_empenho, sq_ordem_fornecimento);
