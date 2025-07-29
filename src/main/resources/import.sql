INSERT INTO se_ouv.entidade
(id_entidade, "oid", ativa, aturalizado_em, endereco, horario, nome, orgao, sigla, site, telefone, atulizador_por)
VALUES(1, '', true, CURRENT_TIMESTAMP, 'Endereço Teste', '8h às 17h', 'Entidade Teste', true, 'ET', 'www.teste.com', '(79) 1234-5678', null);


INSERT INTO se_ouv.responsavel
(id_responsavel, ativo, email, "oid", telefone1, telefone2, id_entidade)
VALUES(1, true, 'responsavel@teste.com', '', '', '', 1);

INSERT INTO se_ouv.perfil_usuario
(id_perfil, ativo, nome_perfil)
VALUES(1, true, 'Cidadão');

INSERT INTO se_ouv.perfil_usuario
(id_perfil, ativo, nome_perfil)
VALUES(2, true, 'Administrador');

INSERT INTO se_ouv.usuario
(id_usuario, last_logged, nick, nome, "password", session_id, id_perfil)
VALUES(1, null, 'admin', 'Administrador do Sistema', 'senha123', '', 2);

INSERT INTO se_ouv.tema
(id_tema, descricao, status, titulo)
VALUES(1, 'Tema de teste para solicitações', 'ATIVO', 'Tema Teste');

INSERT INTO se_ouv.competencia
(id_competencia, ativa, aturalizado_em, descricao, id_responsavel, id_entidade, id_tema)
VALUES(1, true, CURRENT_TIMESTAMP, 'Competência de Teste', 1, 1, 1);

INSERT INTO se_ouv.cidadao
(id_cidadao, bairro, celular, cep, cidade, complemento, cpf, datanasc, email, endereco, escolaridade, estado, numero, orgaexp, profissao, renda, rg, sexo, telefone, tipo, id_usuario)
VALUES(1, 'Centro', '79999999999', '49000000', 'Aracaju', 'Sala 101', '12345678901', '1980-01-01', 'admin@governo.se.gov.br', 'Rua da Administração', 3, 'SE', 100, 'SSP/SE', 'Administrador', 5, '1234567', 'M', '7932222222', false, 1);


INSERT INTO se_ouv.solicitacao
(id_solicitacao, avaliacao, canal_entrada, data_fim, data_ini, data_limite, encaminhada, forma_recebimento, instancia, libera_denuncia, "oid", protocolo, protocolo_agrese, sigilo, status, tipo, titulo, visualizada, id_cidadao, id_competencia, id_responsavel_atendente)
VALUES(1, 1, 1, null, CURRENT_TIMESTAMP, null, 1, 1, 1, 1, '', 'PROT123456789', '', 0, 'ABERTA', 'RECLAMACAO', 'Teste de Solicitação', 1, 1, 1, 1);


INSERT INTO se_ouv.mensagem
(id_mensagem, "data", nota, texto, tipo, id_responsavel, id_solicitacao)
VALUES(1, null, 1, '', 1, 1, 1);



INSERT INTO se_ouv.anexo
(id_anexo, conteudo, "local", nome, tamanho, tipo, id_mensagem)
VALUES(1, 'VGVzdCBjb250ZW50', '', 'test-file.txt', 12, 'text/plain', 1);



INSERT INTO se_ouv.query (nome, query, tipo, tabela_Principal) VALUES
('SELECT_OBJECT_MENSAGEM', 'SELECT id_mensagem, data, nota, texto, tipo, id_responsavel, id_solicitacao FROM se_ouv.mensagem WHERE id_mensagem = ?', 'SELECT', 'mensagem'),
('SELECT_LIST_MENSAGEM_MANIFESTACAO', 'SELECT id_mensagem, data, nota, texto, tipo, id_responsavel, id_solicitacao FROM se_ouv.mensagem msg WHERE id_solicitacao = ? AND (msg.tipo = 1 OR msg.tipo = 2 OR msg.tipo = 6 OR msg.tipo = 7) ORDER BY msg.data ASC', 'SELECT', 'mensagem'),
('SELECT_LIST_MENSAGENS_HISTORICO', 'SELECT id_mensagem, data, nota, texto, tipo, id_responsavel, id_solicitacao FROM se_ouv.mensagem msg WHERE id_solicitacao = ? AND (msg.tipo = 3 OR msg.tipo = 4 OR msg.tipo = 8) ORDER BY msg.data ASC', 'SELECT', 'mensagem'),
('SELECT_LIST_MENSAGENS_TRAMITE', 'SELECT id_mensagem, data, nota, texto, tipo, id_responsavel, id_solicitacao FROM se_ouv.mensagem msg WHERE id_solicitacao = ? AND (msg.tipo = 5) ORDER BY msg.data ASC', 'SELECT', 'mensagem'),
('DELETE_MESSAGEM', 'DELETE FROM se_ouv.mensagem WHERE id_mensagem = ?', 'DELETE', 'mensagem');


INSERT INTO se_ouv.query (nome, query, tipo, tabela_Principal) VALUES
('SELECT_OBJECT_SOLICITACAO_BY_PROTOCOLO', 'SELECT id_solicitacao, avaliacao, canal_entrada, data_fim, data_ini, data_limite, encaminhada, forma_recebimento, instancia, libera_denuncia, "oid", protocolo, protocolo_agrese, sigilo, status, tipo, titulo, visualizada, id_cidadao, id_competencias, id_responsavel_atendente
FROM se_ouv.solicitacao where protocolo = ?;', 'SELECT', 'solicitacao');


INSERT INTO se_ouv.query (nome, query, tipo, tabela_Principal) VALUES
('SELECT_LIST_SOLICITACAO_BY_PROTOCOLO', 'SELECT id_solicitacao, avaliacao, canal_entrada, data_fim, data_ini, data_limite, encaminhada, forma_recebimento, instancia, libera_denuncia, "oid", protocolo, protocolo_agrese, sigilo, status, tipo, titulo, visualizada, id_cidadao, id_competencias, id_responsavel_atendente
FROM se_ouv.solicitacao where protocolo ilike ?;', 'SELECT', 'solicitacao');

INSERT INTO se_ouv.query
(id_query, nome, query, tipo, tabela_principal)
VALUES(23, 'SELECT_LIST_SOLICITACAO_TRAMITANDO_POR_ORGAO', 'select  id_solicitacao, avaliacao, canal_entrada, data_fim, data_ini, 
        data_limite, encaminhada, forma_recebimento, instancia, libera_denuncia,
        oid, protocolo, protocolo_agrese, sigilo, status, tipo, titulo, visualizada, 
        id_cidadao, id_competencia, id_responsavel_atendente 
from se_ouv.solicitacao s 
join se_ouv.competencia c  using(id_competencia) 
where  s.status not in (''FINALIZADA'',''SEM_RESPOTA'')  and c.id_Entidade = ?;', 'SELECT', 'solicitacao');