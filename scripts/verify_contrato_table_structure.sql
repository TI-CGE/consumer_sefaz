-- Script para verificar e ajustar a estrutura da tabela contrato
-- Autor: Sistema de Ajuste de Mapeamento
-- Data: 2025-09-15
-- Descrição: Verifica se todas as colunas necessárias existem na tabela contrato

-- Verificar se a tabela existe
DO $$
BEGIN
    -- Verificar se a tabela contrato existe
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.tables 
        WHERE table_schema = 'consumer_sefaz' 
        AND table_name = 'contrato'
    ) THEN
        -- Criar a tabela se não existir
        CREATE TABLE consumer_sefaz.contrato (
            id BIGSERIAL PRIMARY KEY,
            sg_unidade_gestora VARCHAR(100),
            cd_unidade_gestora VARCHAR(20),
            dt_ano_exercicio INTEGER,
            cd_contrato VARCHAR(30),
            cd_aditivo VARCHAR(30),
            dt_inicio_vigencia DATE,
            dt_fim_vigencia DATE,
            nm_categoria VARCHAR(200),
            nm_fornecedor VARCHAR(200),
            nu_documento VARCHAR(20),
            ds_objeto_contrato TEXT,
            vl_contrato DECIMAL(18,2),
            tp_contrato VARCHAR(10),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        
        RAISE NOTICE 'Tabela consumer_sefaz.contrato criada com sucesso';
    ELSE
        RAISE NOTICE 'Tabela consumer_sefaz.contrato já existe';
    END IF;
END $$;

-- Verificar e adicionar colunas que podem estar faltando
DO $$
BEGIN
    -- Verificar sg_unidade_gestora
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'sg_unidade_gestora'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN sg_unidade_gestora VARCHAR(100);
        RAISE NOTICE 'Coluna sg_unidade_gestora adicionada';
    END IF;

    -- Verificar cd_unidade_gestora
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'cd_unidade_gestora'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN cd_unidade_gestora VARCHAR(20);
        RAISE NOTICE 'Coluna cd_unidade_gestora adicionada';
    END IF;

    -- Verificar dt_ano_exercicio
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'dt_ano_exercicio'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN dt_ano_exercicio INTEGER;
        RAISE NOTICE 'Coluna dt_ano_exercicio adicionada';
    END IF;

    -- Verificar cd_contrato
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'cd_contrato'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN cd_contrato VARCHAR(30);
        RAISE NOTICE 'Coluna cd_contrato adicionada';
    END IF;

    -- Verificar cd_aditivo
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'cd_aditivo'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN cd_aditivo VARCHAR(30);
        RAISE NOTICE 'Coluna cd_aditivo adicionada';
    END IF;

    -- Verificar dt_inicio_vigencia
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'dt_inicio_vigencia'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN dt_inicio_vigencia DATE;
        RAISE NOTICE 'Coluna dt_inicio_vigencia adicionada';
    END IF;

    -- Verificar dt_fim_vigencia
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'dt_fim_vigencia'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN dt_fim_vigencia DATE;
        RAISE NOTICE 'Coluna dt_fim_vigencia adicionada';
    END IF;

    -- Verificar nm_categoria
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'nm_categoria'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN nm_categoria VARCHAR(200);
        RAISE NOTICE 'Coluna nm_categoria adicionada';
    END IF;

    -- Verificar nm_fornecedor
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'nm_fornecedor'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN nm_fornecedor VARCHAR(200);
        RAISE NOTICE 'Coluna nm_fornecedor adicionada';
    END IF;

    -- Verificar nu_documento
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'nu_documento'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN nu_documento VARCHAR(20);
        RAISE NOTICE 'Coluna nu_documento adicionada';
    END IF;

    -- Verificar ds_objeto_contrato
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'ds_objeto_contrato'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN ds_objeto_contrato TEXT;
        RAISE NOTICE 'Coluna ds_objeto_contrato adicionada';
    END IF;

    -- Verificar vl_contrato
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'vl_contrato'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN vl_contrato DECIMAL(18,2);
        RAISE NOTICE 'Coluna vl_contrato adicionada';
    END IF;

    -- Verificar tp_contrato
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'tp_contrato'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN tp_contrato VARCHAR(10);
        RAISE NOTICE 'Coluna tp_contrato adicionada';
    END IF;

    -- Verificar created_at
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'created_at'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE 'Coluna created_at adicionada';
    END IF;

    -- Verificar updated_at
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'consumer_sefaz' AND table_name = 'contrato' AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE consumer_sefaz.contrato ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE 'Coluna updated_at adicionada';
    END IF;

END $$;

-- Adicionar comentários nas colunas
COMMENT ON TABLE consumer_sefaz.contrato IS 'Tabela de contratos do SEFAZ';
COMMENT ON COLUMN consumer_sefaz.contrato.sg_unidade_gestora IS 'Sigla da Unidade Gestora';
COMMENT ON COLUMN consumer_sefaz.contrato.cd_unidade_gestora IS 'Código da Unidade Gestora';
COMMENT ON COLUMN consumer_sefaz.contrato.dt_ano_exercicio IS 'Ano do Exercício';
COMMENT ON COLUMN consumer_sefaz.contrato.cd_contrato IS 'Código do Contrato';
COMMENT ON COLUMN consumer_sefaz.contrato.cd_aditivo IS 'Código do Aditivo (da documentação)';
COMMENT ON COLUMN consumer_sefaz.contrato.dt_inicio_vigencia IS 'Data de Início da Vigência';
COMMENT ON COLUMN consumer_sefaz.contrato.dt_fim_vigencia IS 'Data de Fim da Vigência';
COMMENT ON COLUMN consumer_sefaz.contrato.nm_categoria IS 'Nome da Categoria (da documentação)';
COMMENT ON COLUMN consumer_sefaz.contrato.nm_fornecedor IS 'Nome do Fornecedor';
COMMENT ON COLUMN consumer_sefaz.contrato.nu_documento IS 'Número do Documento';
COMMENT ON COLUMN consumer_sefaz.contrato.ds_objeto_contrato IS 'Descrição do Objeto do Contrato';
COMMENT ON COLUMN consumer_sefaz.contrato.vl_contrato IS 'Valor do Contrato';
COMMENT ON COLUMN consumer_sefaz.contrato.tp_contrato IS 'Tipo do Contrato';
