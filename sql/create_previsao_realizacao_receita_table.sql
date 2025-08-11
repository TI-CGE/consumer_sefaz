-- Script SQL para criação da tabela consumer_sefaz.previsao_realizacao_receita
-- Entidade: Previsão Realização Receita
-- Fonte: API Transparência SEFAZ/SE - CTB (Contabilidade)
-- Endpoint: https://api-transparencia.apps.sefaz.se.gov.br/ctb/v1/previsao-realizacao-receita

-- Criar schema se não existir
CREATE SCHEMA IF NOT EXISTS consumer_sefaz;

-- Criar tabela previsao_realizacao_receita
CREATE TABLE IF NOT EXISTS consumer_sefaz.previsao_realizacao_receita (
  -- Chave primária
  id SERIAL PRIMARY KEY,

  -- Campos de identificação
  cd_unidade_gestora        VARCHAR(20)  NOT NULL,
  dt_ano_exercicio_ctb      INTEGER      NOT NULL,

  -- Hierarquia de categorização da receita - Nível 1: Categoria Econômica
  cd_categoria_economica    VARCHAR(10)  NOT NULL,
  nm_categoria_economica    VARCHAR(200) NOT NULL,

  -- Nível 2: Origem
  cd_origem                 VARCHAR(10)  NOT NULL,
  nm_origem                 VARCHAR(200) NOT NULL,

  -- Nível 3: Espécie
  cd_especie                VARCHAR(10)  NOT NULL,
  nm_especie                VARCHAR(200) NOT NULL,

  -- Nível 4: Desdobramento
  cd_desdobramento          VARCHAR(10)  NOT NULL,
  nm_desdobramento          VARCHAR(200) NOT NULL,

  -- Nível 5: Tipo
  cd_tipo                   VARCHAR(10)  NOT NULL,
  nm_tipo                   VARCHAR(300) NOT NULL,

  -- Valores monetários
  vl_previsto               NUMERIC(18,2) NOT NULL,
  vl_atualizado             NUMERIC(18,2) NOT NULL,
  vl_realizado              NUMERIC(18,2) NOT NULL,

  -- Campos de auditoria
  created_at                TIMESTAMP DEFAULT now(),
  updated_at                TIMESTAMP DEFAULT now()
);

-- Comentários na tabela e colunas
COMMENT ON TABLE consumer_sefaz.previsao_realizacao_receita IS 'Dados de previsão e realização de receitas por unidade gestora, organizados em hierarquia de categorização';

COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_unidade_gestora IS 'Código da Unidade Gestora';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.dt_ano_exercicio_ctb IS 'Ano do exercício contábil';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_categoria_economica IS 'Código da Categoria Econômica (nível 1 da hierarquia)';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nm_categoria_economica IS 'Nome da Categoria Econômica';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_origem IS 'Código da Origem (nível 2 da hierarquia)';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nm_origem IS 'Nome da Origem';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_especie IS 'Código da Espécie (nível 3 da hierarquia)';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nm_especie IS 'Nome da Espécie';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_desdobramento IS 'Código do Desdobramento (nível 4 da hierarquia)';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nm_desdobramento IS 'Nome do Desdobramento';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.cd_tipo IS 'Código do Tipo (nível 5 da hierarquia)';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.nm_tipo IS 'Nome do Tipo';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.vl_previsto IS 'Valor previsto da receita';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.vl_atualizado IS 'Valor atualizado da receita';
COMMENT ON COLUMN consumer_sefaz.previsao_realizacao_receita.vl_realizado IS 'Valor realizado da receita';

-- Índice único lógico para evitar duplicidade do mesmo lançamento
-- Chave composta: UG + Ano + Hierarquia completa (categoria → origem → espécie → desdobramento → tipo)
CREATE UNIQUE INDEX IF NOT EXISTS uq_previsao_realizacao_receita
ON consumer_sefaz.previsao_realizacao_receita
(
  cd_unidade_gestora,
  dt_ano_exercicio_ctb,
  cd_categoria_economica,
  cd_origem,
  cd_especie,
  cd_desdobramento,
  cd_tipo
);

-- Índices auxiliares para consultas por UG e Ano
CREATE INDEX IF NOT EXISTS ix_prr_ug 
ON consumer_sefaz.previsao_realizacao_receita (cd_unidade_gestora);

CREATE INDEX IF NOT EXISTS ix_prr_ano 
ON consumer_sefaz.previsao_realizacao_receita (dt_ano_exercicio_ctb);

-- Índice composto para consultas por UG + Ano
CREATE INDEX IF NOT EXISTS ix_prr_ug_ano 
ON consumer_sefaz.previsao_realizacao_receita (cd_unidade_gestora, dt_ano_exercicio_ctb);

-- Índices para hierarquia de categorização (para consultas analíticas)
CREATE INDEX IF NOT EXISTS ix_prr_categoria 
ON consumer_sefaz.previsao_realizacao_receita (cd_categoria_economica);

CREATE INDEX IF NOT EXISTS ix_prr_origem 
ON consumer_sefaz.previsao_realizacao_receita (cd_categoria_economica, cd_origem);

CREATE INDEX IF NOT EXISTS ix_prr_especie 
ON consumer_sefaz.previsao_realizacao_receita (cd_categoria_economica, cd_origem, cd_especie);

-- Trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_previsao_realizacao_receita_updated_at 
    BEFORE UPDATE ON consumer_sefaz.previsao_realizacao_receita 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Verificar se a tabela foi criada com sucesso
SELECT 
    schemaname,
    tablename,
    tableowner
FROM pg_tables 
WHERE schemaname = 'consumer_sefaz' 
  AND tablename = 'previsao_realizacao_receita';

-- Verificar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'consumer_sefaz' 
  AND tablename = 'previsao_realizacao_receita'
ORDER BY indexname;

-- Estatísticas da tabela (será vazia inicialmente)
SELECT 
    COUNT(*) as total_registros,
    COUNT(DISTINCT cd_unidade_gestora) as total_ugs,
    COUNT(DISTINCT dt_ano_exercicio_ctb) as total_anos,
    MIN(dt_ano_exercicio_ctb) as ano_minimo,
    MAX(dt_ano_exercicio_ctb) as ano_maximo
FROM consumer_sefaz.previsao_realizacao_receita;

COMMIT;
