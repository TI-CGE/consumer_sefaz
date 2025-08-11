-- Script para recriar a tabela base_despesa_licitacao com tamanhos corretos
-- Execute este script se a tabela já existe e você quer recriá-la

-- Remover a tabela existente (se existir)
DROP TABLE IF EXISTS consumer_sefaz.base_despesa_licitacao;

-- Criar a tabela com os tamanhos corretos
CREATE TABLE consumer_sefaz.base_despesa_licitacao (
  id SERIAL PRIMARY KEY,
  nu_processo VARCHAR(50),
  si_licitacao VARCHAR(50),
  nu_documento VARCHAR(30),
  nm_razao_social_ug VARCHAR(200),
  nm_razao_social_fornecedor VARCHAR(200),
  cd_licitacao VARCHAR(50),
  vl_licitacao NUMERIC(18,2),
  sg_unidade_gestora VARCHAR(30),
  cd_unidade_gestora VARCHAR(30),
  nm_modalidade VARCHAR(100),
  dt_homologacao DATE,
  vl_estimado NUMERIC(18,2),
  ds_objeto TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

-- Criar índices para melhorar performance
CREATE INDEX idx_base_despesa_licitacao_cd_unidade_gestora ON consumer_sefaz.base_despesa_licitacao(cd_unidade_gestora);
CREATE INDEX idx_base_despesa_licitacao_cd_licitacao ON consumer_sefaz.base_despesa_licitacao(cd_licitacao);
CREATE INDEX idx_base_despesa_licitacao_dt_homologacao ON consumer_sefaz.base_despesa_licitacao(dt_homologacao);
CREATE INDEX idx_base_despesa_licitacao_created_at ON consumer_sefaz.base_despesa_licitacao(created_at);

-- Verificar a estrutura da tabela
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_schema = 'consumer_sefaz' 
  AND table_name = 'base_despesa_licitacao'
ORDER BY ordinal_position;
