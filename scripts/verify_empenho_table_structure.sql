-- Verifica e adiciona colunas ausentes na tabela consumer_sefaz.empenho
-- Campos solicitados:
-- 1) dt_emissao_empenho (texto para data vinda da API)
-- 2) dt_lancamento_empenho (texto para data vinda da API)
-- 3) vl_total_anulado_empenho (valor decimal)

CREATE SCHEMA IF NOT EXISTS consumer_sefaz;

-- Assegura que as colunas existam (não cria a tabela inteira, apenas as colunas se a tabela já existir)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'empenho'
    ) THEN
        -- Datas são tratadas como texto no modelo atual (String)
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS dt_emissao_empenho VARCHAR(30)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS dt_lancamento_empenho VARCHAR(30)';

        -- Valores monetários com precisão 15,2
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS vl_total_anulado_empenho NUMERIC(15,2)';
    ELSE
        RAISE NOTICE 'Tabela consumer_sefaz.empenho não encontrada. Nenhuma alteração aplicada.';
    END IF;
END$$;
