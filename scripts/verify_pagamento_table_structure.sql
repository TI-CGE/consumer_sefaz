-- Verifica e adiciona colunas ausentes na tabela consumer_sefaz.pagamento
-- Campos retorno API Pagamento:
-- nu_conta_bancaria_recebedora, nu_conta_bancaria_pagadora,
-- cd_banco_agente_arrec_pagadora, cd_banco_agente_arrec_recebedor,
-- cd_ponto_atendimento_pagadora, cd_ponto_atendimento_recebedora

CREATE SCHEMA IF NOT EXISTS consumer_sefaz;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'pagamento'
    ) THEN
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS nu_conta_bancaria_recebedora BIGINT';
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS nu_conta_bancaria_pagadora BIGINT';
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS cd_banco_agente_arrec_pagadora INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS cd_banco_agente_arrec_recebedor INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS cd_ponto_atendimento_pagadora INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.pagamento ADD COLUMN IF NOT EXISTS cd_ponto_atendimento_recebedora INTEGER';
    ELSE
        RAISE NOTICE 'Tabela consumer_sefaz.pagamento não encontrada. Nenhuma alteração aplicada.';
    END IF;
END$$;
