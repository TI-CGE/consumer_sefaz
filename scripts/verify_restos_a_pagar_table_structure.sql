-- Cria schema e tabela restos_a_pagar (retorno API restos a pagar)
-- PK: sq_empenho, dt_ano_exercicio_ctb, cd_unidade_gestora, cd_gestao

CREATE SCHEMA IF NOT EXISTS consumer_sefaz;

CREATE TABLE IF NOT EXISTS consumer_sefaz.restos_a_pagar (
    id BIGSERIAL PRIMARY KEY,
    vl_total_transferido_npp NUMERIC(15,2),
    vl_empenho_inscrito_np NUMERIC(15,2),
    vl_empenho_inscrito_p NUMERIC(15,2),
    vl_empenho_canc_rpnp_nao_exec NUMERIC(15,2),
    vl_empenho_cancelado_rp NUMERIC(15,2),
    vl_empenho_canc_divd_rpnp_exec NUMERIC(15,2),
    vl_empenho_total_pago_np NUMERIC(15,2),
    sq_empenho INTEGER,
    vl_empenho_cancelado_divida_rp NUMERIC(15,2),
    cd_unidade_gestora VARCHAR(20),
    vl_empenho_total_pago_p NUMERIC(15,2),
    vl_executado NUMERIC(15,2),
    dt_ano_exercicio_ctb INTEGER,
    vl_empenho_canc_rpnp_executado NUMERIC(15,2),
    sq_solicitacao_empenho INTEGER,
    cd_gestao VARCHAR(20)
);
