-- Verifica e adiciona colunas ausentes na tabela consumer_sefaz.empenho
-- Campos: dt_emissao_empenho, dt_lancamento_empenho, vl_total_*,
-- cd_funcao_plo, nm_funcao_plo, cd_programa_governo, nm_programa_governo,
-- cd_categoria_economica, nm_categoria_economica, cd_modalidade_aplicacao,
-- nm_modalidade_aplicacao, nm_fonte_recurso, cd_grupo_despesa, nm_grupo_despesa (retorno API Empenho)

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

        -- Natureza da despesa completa
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_natureza_despesa_completa VARCHAR(255)';

        -- Valores monetários com precisão 15,2
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS vl_total_anulado_empenho NUMERIC(15,2)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS vl_total_estorno_liqd_empenho NUMERIC(15,2)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS vl_total_devolvido_ob_empenho NUMERIC(15,2)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS vl_total_devolvido_gr_empenho NUMERIC(15,2)';

        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_funcao_plo INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_funcao_plo VARCHAR(255)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_programa_governo INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_programa_governo VARCHAR(255)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_categoria_economica INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_categoria_economica VARCHAR(255)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_modalidade_aplicacao INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_modalidade_aplicacao VARCHAR(255)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_fonte_recurso VARCHAR(255)';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS cd_grupo_despesa INTEGER';
        EXECUTE 'ALTER TABLE consumer_sefaz.empenho ADD COLUMN IF NOT EXISTS nm_grupo_despesa VARCHAR(255)';
    ELSE
        RAISE NOTICE 'Tabela consumer_sefaz.empenho não encontrada. Nenhuma alteração aplicada.';
    END IF;
END$$;
