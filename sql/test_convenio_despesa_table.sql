-- Script de teste para a tabela de Despesa de Convênio
-- Execute após criar a tabela para validar a estrutura

-- 1. Verificar se a tabela foi criada corretamente
SELECT 
    table_name,
    table_schema,
    table_type
FROM information_schema.tables 
WHERE table_schema = 'consumer_sefaz' 
  AND table_name = 'convenio_despesa';

-- 2. Verificar estrutura das colunas
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    numeric_precision,
    numeric_scale,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'consumer_sefaz' 
  AND table_name = 'convenio_despesa'
ORDER BY ordinal_position;

-- 3. Verificar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'consumer_sefaz' 
  AND tablename = 'convenio_despesa'
ORDER BY indexname;

-- 4. Verificar constraints
SELECT 
    constraint_name,
    constraint_type,
    table_name
FROM information_schema.table_constraints 
WHERE table_schema = 'consumer_sefaz' 
  AND table_name = 'convenio_despesa';

-- 5. Inserir dados de teste (opcional)
INSERT INTO consumer_sefaz.convenio_despesa (
    cd_convenio,
    cd_unidade_gestora,
    cd_gestao,
    nm_convenio,
    nm_concedente,
    nm_beneficiario,
    dt_celebracao_convenio,
    dt_inicio_vigencia_convenio,
    dt_fim_vigencia_convenio,
    dt_lancamento_convenio,
    vl_concedente_convenio,
    vl_contrapartida_convenio,
    cd_convenio_situacao,
    in_convenio_ficha_ingresso,
    in_convenio_empenho_ingresso
) VALUES (
    999999,
    '161011',
    '00001',
    'Convênio de Teste - DespesaConvenio',
    'Governo do Estado de Sergipe',
    'Município Teste',
    '2025-01-01',
    '2025-01-01',
    '2025-12-31',
    '2025-01-15',
    100000.00,
    25000.00,
    'ATIVO',
    'S',
    'S'
);

-- 6. Verificar se o insert funcionou
SELECT 
    cd_convenio,
    nm_convenio,
    nm_concedente,
    vl_concedente_convenio,
    dt_lancamento_convenio,
    created_at
FROM consumer_sefaz.convenio_despesa 
WHERE cd_convenio = 999999;

-- 7. Testar constraint de unicidade (deve falhar)
-- INSERT INTO consumer_sefaz.convenio_despesa (cd_convenio, nm_convenio) 
-- VALUES (999999, 'Teste Duplicado');

-- 8. Limpar dados de teste
DELETE FROM consumer_sefaz.convenio_despesa WHERE cd_convenio = 999999;

-- 9. Verificar se a limpeza funcionou
SELECT COUNT(*) as total_registros 
FROM consumer_sefaz.convenio_despesa;

-- 10. Verificar permissões (se aplicável)
SELECT 
    grantee,
    privilege_type,
    is_grantable
FROM information_schema.role_table_grants 
WHERE table_schema = 'consumer_sefaz' 
  AND table_name = 'convenio_despesa';

COMMIT;
