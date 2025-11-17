-- Script para atualizar a tabela error_log
-- Remove coluna affected_operation e adiciona api_url_completa

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' 
        AND table_name = 'error_log'
        AND column_name = 'affected_operation'
    ) THEN
        ALTER TABLE consumer_sefaz.error_log DROP COLUMN affected_operation;
        RAISE NOTICE 'Coluna affected_operation removida';
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' 
        AND table_name = 'error_log'
        AND column_name = 'api_url_completa'
    ) THEN
        ALTER TABLE consumer_sefaz.error_log ADD COLUMN api_url_completa TEXT;
        RAISE NOTICE 'Coluna api_url_completa adicionada';
    END IF;
END $$;

COMMENT ON COLUMN consumer_sefaz.error_log.api_url_completa IS 'URL completa da API com todos os parâmetros (se aplicável)';

