CREATE SCHEMA IF NOT EXISTS consumer_sefaz;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'consulta_gerencial'
    ) THEN
        EXECUTE 'ALTER TABLE consumer_sefaz.consulta_gerencial ADD COLUMN IF NOT EXISTS cd_situacao_solicitacao INTEGER NULL';
    END IF;
END $$;
