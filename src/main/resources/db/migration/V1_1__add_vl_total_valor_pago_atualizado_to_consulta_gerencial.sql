-- Adicionar coluna vl_total_valor_pago_atualizado na tabela consulta_gerencial
-- Esta coluna armazena o valor total pago atualizado das diárias

ALTER TABLE consumer_sefaz.consulta_gerencial 
ADD COLUMN vl_total_valor_pago_atualizado DECIMAL(18,2) DEFAULT 0.00;

-- Adicionar comentário na coluna
COMMENT ON COLUMN consumer_sefaz.consulta_gerencial.vl_total_valor_pago_atualizado 
IS 'Valor total pago atualizado da solicitação de diária';
