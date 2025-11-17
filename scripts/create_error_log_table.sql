-- Script para criar a tabela error_log
-- Descrição: Tabela para armazenar logs de erro do sistema

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.tables 
        WHERE table_schema = 'consumer_sefaz' 
        AND table_name = 'error_log'
    ) THEN
        CREATE TABLE consumer_sefaz.error_log (
            id BIGSERIAL PRIMARY KEY,
            timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            correlation_id VARCHAR(50),
            component VARCHAR(100),
            operation VARCHAR(200),
            message TEXT,
            exception_type VARCHAR(200),
            exception_message TEXT,
            stack_trace TEXT,
            root_cause TEXT,
            error_code VARCHAR(50),
            error_category VARCHAR(100),
            severity VARCHAR(20),
            affected_operation VARCHAR(200),
            http_status_code INTEGER,
            request_id VARCHAR(100),
            api_endpoint VARCHAR(500),
            ug_code VARCHAR(50),
            contract_id VARCHAR(50)
        );
        
        CREATE INDEX idx_error_log_timestamp ON consumer_sefaz.error_log(timestamp DESC);
        CREATE INDEX idx_error_log_component ON consumer_sefaz.error_log(component);
        CREATE INDEX idx_error_log_exception_type ON consumer_sefaz.error_log(exception_type);
        CREATE INDEX idx_error_log_correlation_id ON consumer_sefaz.error_log(correlation_id);
        
        RAISE NOTICE 'Tabela consumer_sefaz.error_log criada com sucesso';
    ELSE
        RAISE NOTICE 'Tabela consumer_sefaz.error_log já existe';
    END IF;
END $$;

COMMENT ON TABLE consumer_sefaz.error_log IS 'Tabela para armazenar logs de erro do sistema';
COMMENT ON COLUMN consumer_sefaz.error_log.id IS 'Identificador único do log de erro';
COMMENT ON COLUMN consumer_sefaz.error_log.timestamp IS 'Data e hora do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.correlation_id IS 'ID de correlação para rastreamento';
COMMENT ON COLUMN consumer_sefaz.error_log.component IS 'Componente onde ocorreu o erro';
COMMENT ON COLUMN consumer_sefaz.error_log.operation IS 'Operação que estava sendo executada';
COMMENT ON COLUMN consumer_sefaz.error_log.message IS 'Mensagem de erro';
COMMENT ON COLUMN consumer_sefaz.error_log.exception_type IS 'Tipo da exceção';
COMMENT ON COLUMN consumer_sefaz.error_log.exception_message IS 'Mensagem da exceção';
COMMENT ON COLUMN consumer_sefaz.error_log.stack_trace IS 'Stack trace completo do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.root_cause IS 'Causa raiz do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.error_code IS 'Código do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.error_category IS 'Categoria do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.severity IS 'Severidade do erro';
COMMENT ON COLUMN consumer_sefaz.error_log.affected_operation IS 'Operação afetada pelo erro';
COMMENT ON COLUMN consumer_sefaz.error_log.http_status_code IS 'Código de status HTTP (se aplicável)';
COMMENT ON COLUMN consumer_sefaz.error_log.request_id IS 'ID da requisição (se aplicável)';
COMMENT ON COLUMN consumer_sefaz.error_log.api_endpoint IS 'Endpoint da API (se aplicável)';
COMMENT ON COLUMN consumer_sefaz.error_log.ug_code IS 'Código da unidade gestora (se aplicável)';
COMMENT ON COLUMN consumer_sefaz.error_log.contract_id IS 'ID do contrato (se aplicável)';

