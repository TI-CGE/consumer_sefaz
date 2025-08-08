-- Script para corrigir a tabela contrato_empenho existente
-- Remove a restrição NOT NULL dos campos de auditoria

-- Conectar ao banco de dados sefaz_transparency_consumer
-- \c sefaz_transparency_consumer;

-- Corrigir campos de auditoria para permitir NULL
ALTER TABLE consumer_sefaz.contrato_empenho 
ALTER COLUMN created_at DROP NOT NULL;

ALTER TABLE consumer_sefaz.contrato_empenho 
ALTER COLUMN updated_at DROP NOT NULL;

-- Verificar se as alterações foram aplicadas
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'consumer_sefaz' 
AND table_name = 'contrato_empenho'
AND column_name IN ('created_at', 'updated_at')
ORDER BY column_name;

-- Verificar estrutura completa da tabela
\d consumer_sefaz.contrato_empenho;

-- Mensagem de sucesso
SELECT 'Tabela contrato_empenho corrigida com sucesso!' AS status;
