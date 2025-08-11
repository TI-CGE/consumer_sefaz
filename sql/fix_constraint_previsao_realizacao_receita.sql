-- Script para corrigir a constraint única da tabela previsao_realizacao_receita
-- Problema: A constraint não inclui nu_mes, causando violações de chave única
-- Solução: Recriar a constraint incluindo nu_mes

-- Verificar a estrutura atual da tabela
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'consumer_sefaz' 
AND table_name = 'previsao_realizacao_receita'
ORDER BY ordinal_position;

-- Verificar constraints existentes
SELECT 
    conname as constraint_name,
    contype as constraint_type,
    pg_get_constraintdef(oid) as constraint_definition
FROM pg_constraint 
WHERE conrelid = 'consumer_sefaz.previsao_realizacao_receita'::regclass;

-- Verificar índices existentes
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'consumer_sefaz' 
  AND tablename = 'previsao_realizacao_receita'
ORDER BY indexname;

-- Adicionar coluna nu_mes se não existir
ALTER TABLE consumer_sefaz.previsao_realizacao_receita 
ADD COLUMN IF NOT EXISTS nu_mes INTEGER;

-- Atualizar registros existentes com nu_mes = 12 (dezembro) como padrão
-- Isso é necessário porque a API sempre requer o parâmetro mês
UPDATE consumer_sefaz.previsao_realizacao_receita 
SET nu_mes = 12 
WHERE nu_mes IS NULL;

-- Tornar a coluna nu_mes NOT NULL após atualizar os dados existentes
ALTER TABLE consumer_sefaz.previsao_realizacao_receita 
ALTER COLUMN nu_mes SET NOT NULL;

-- Remover a constraint única existente (se existir)
ALTER TABLE consumer_sefaz.previsao_realizacao_receita
DROP CONSTRAINT IF EXISTS uq_previsao_realizacao_receita;

-- Remover o índice único existente (se existir)
DROP INDEX IF EXISTS consumer_sefaz.uq_previsao_realizacao_receita;

-- Recriar o índice único incluindo nu_mes
CREATE UNIQUE INDEX uq_previsao_realizacao_receita
ON consumer_sefaz.previsao_realizacao_receita
(
  cd_unidade_gestora,
  dt_ano_exercicio_ctb,
  nu_mes,
  cd_categoria_economica,
  cd_origem,
  cd_especie,
  cd_desdobramento,
  cd_tipo
);

-- Permitir valores NULL nos campos monetários (conforme API pode retornar)
ALTER TABLE consumer_sefaz.previsao_realizacao_receita 
ALTER COLUMN vl_previsto DROP NOT NULL;

ALTER TABLE consumer_sefaz.previsao_realizacao_receita 
ALTER COLUMN vl_atualizado DROP NOT NULL;

ALTER TABLE consumer_sefaz.previsao_realizacao_receita 
ALTER COLUMN vl_realizado DROP NOT NULL;

-- Adicionar comentário na coluna nu_mes
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nu_mes IS 'Número do mês (1-12) - parâmetro obrigatório da API';

-- Verificar se as alterações foram aplicadas
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'consumer_sefaz' 
AND table_name = 'previsao_realizacao_receita'
AND column_name IN ('vl_previsto', 'vl_atualizado', 'vl_realizado', 'nu_mes')
ORDER BY column_name;

-- Verificar o novo índice único
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'consumer_sefaz' 
  AND tablename = 'previsao_realizacao_receita'
  AND indexname = 'uq_previsao_realizacao_receita';

-- Estatísticas da tabela após correção
SELECT 
    COUNT(*) as total_registros,
    COUNT(DISTINCT cd_unidade_gestora) as total_ugs,
    COUNT(DISTINCT dt_ano_exercicio_ctb) as total_anos,
    COUNT(DISTINCT nu_mes) as total_meses,
    COUNT(CASE WHEN vl_previsto IS NULL THEN 1 END) as registros_vl_previsto_null,
    COUNT(CASE WHEN vl_atualizado IS NULL THEN 1 END) as registros_vl_atualizado_null,
    COUNT(CASE WHEN vl_realizado IS NULL THEN 1 END) as registros_vl_realizado_null
FROM consumer_sefaz.previsao_realizacao_receita;

-- Verificar se há registros duplicados que precisam ser limpos
SELECT 
    cd_unidade_gestora,
    dt_ano_exercicio_ctb,
    nu_mes,
    cd_categoria_economica,
    cd_origem,
    cd_especie,
    cd_desdobramento,
    cd_tipo,
    COUNT(*) as duplicates
FROM consumer_sefaz.previsao_realizacao_receita
GROUP BY 
    cd_unidade_gestora,
    dt_ano_exercicio_ctb,
    nu_mes,
    cd_categoria_economica,
    cd_origem,
    cd_especie,
    cd_desdobramento,
    cd_tipo
HAVING COUNT(*) > 1;
