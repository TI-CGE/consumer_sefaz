# Endpoint para Cadastro de Cidadãos Externos

## Visão Geral

Este documento descreve o novo endpoint implementado para cadastrar cidadãos externos no sistema, ou seja, cidadãos que não possuem credenciais de login (sem usuário associado).

## Endpoint

**URL:** `POST /usuarios/externos`

**Descrição:** Cadastra um novo cidadão sem criar um usuário associado.

## Estrutura da Requisição

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
  "nome": "João Silva Santos",
  "email": "joao.silva@email.com",
  "cpf": "12345678901",
  "tipo": true,
  "rg": "1234567",
  "celular": "(79) 99999-9999",
  "orgaoexp": "SSP/SE",
  "datanasc": "1990-01-15",
  "sexo": "M",
  "escolaridade": 3,
  "profissao": "Engenheiro",
  "endereco": "Rua das Flores, 123",
  "estado": "Sergipe",
  "cidade": "Aracaju",
  "cep": "49000-000",
  "bairro": "Centro",
  "complemento": "Apt 101",
  "telefone": "(79) 3333-3333",
  "renda": 5,
  "numero": 123
}
```

### Campos Obrigatórios
- `nome`: Nome completo do cidadão
- `email`: E-mail único do cidadão

### Campos Opcionais
Todos os demais campos são opcionais, incluindo o CPF.

## Estrutura da Resposta

### Sucesso (201 Created)
```json
{
  "idCidadao": 1,
  "nome": "João Silva Santos",
  "email": "joao.silva@email.com",
  "cpf": "12345678901",
  "tipo": true,
  "rg": "1234567",
  "celular": "(79) 99999-9999",
  "orgaexp": "SSP/SE",
  "dataNasc": "1990-01-15",
  "sexo": "M",
  "escolaridade": 3,
  "profissao": "Engenheiro",
  "endereco": "Rua das Flores, 123",
  "estado": "Sergipe",
  "cidade": "Aracaju",
  "cep": "49000-000",
  "bairro": "Centro",
  "complemento": "Apt 101",
  "telefone": "(79) 3333-3333",
  "renda": 5,
  "numero": 123,
  "idUsuario": null
}
```

**Nota:** O campo `idUsuario` será sempre `null` para cidadãos externos.

### Erro (400 Bad Request)
```json
"CPF já cadastrado"
```
ou
```json
"E-mail já cadastrado"
```

## Validações Implementadas

1. **E-mail único**: Verifica se o e-mail já está cadastrado no sistema
2. **CPF único**: Se fornecido, verifica se o CPF já está cadastrado
3. **Campos obrigatórios**: Nome e e-mail são obrigatórios
4. **Formato de dados**: Validação automática de tipos de dados

## Diferenças do Endpoint Principal

| Aspecto | `/usuarios` (Principal) | `/usuarios/externos` (Novo) |
|---------|------------------------|----------------------------|
| **Cria Usuario** | ✅ Sim | ❌ Não |
| **Cria Cidadao** | ✅ Sim | ✅ Sim |
| **Requer credenciais** | ✅ Nick/Senha | ❌ Não |
| **Permite login** | ✅ Sim | ❌ Não |
| **Retorna dados** | ❌ Vazio | ✅ Dados do cidadão |

## Casos de Uso

### 1. Cidadão que não acessa o sistema
```bash
curl -X POST http://localhost:8080/usuarios/externos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria.santos@email.com",
    "cpf": "98765432100",
    "celular": "(79) 88888-8888"
  }'
```

### 2. Cidadão sem CPF
```bash
curl -X POST http://localhost:8080/usuarios/externos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "José Silva",
    "email": "jose.silva@email.com",
    "celular": "(79) 77777-7777"
  }'
```

### 3. Cidadão com dados completos
```bash
curl -X POST http://localhost:8080/usuarios/externos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ana Paula Costa",
    "email": "ana.costa@email.com",
    "cpf": "11122233344",
    "rg": "9876543",
    "celular": "(79) 66666-6666",
    "orgaoexp": "SSP/SE",
    "datanasc": "1985-03-20",
    "sexo": "F",
    "escolaridade": 4,
    "profissao": "Professora",
    "endereco": "Av. Principal, 456",
    "estado": "Sergipe",
    "cidade": "Aracaju",
    "cep": "49001-000",
    "bairro": "Jardins",
    "telefone": "(79) 2222-2222",
    "renda": 3
  }'
```

## Alterações no Banco de Dados

Foi criada a migration `V10__allow_null_id_usuario_in_cidadao.sql` que:

1. Altera a coluna `id_usuario` para permitir valores `NULL`
2. Recria a foreign key permitindo valores nulos
3. Preserva todos os dados existentes
4. Inclui verificações de integridade

## Arquivos Modificados/Criados

### Novos Arquivos
- `src/main/resources/db/migration/V10__allow_null_id_usuario_in_cidadao.sql`
- `src/main/java/br/gov/se/setc/cidadao/dto/response/CidadaoResponseDTO.java`
- `src/main/java/br/gov/se/setc/cidadao/mapper/CidadaoMapper.java`
- `src/test/java/br/gov/se/setc/cidadao/controller/CidadaoExternoControllerTest.java`
- `src/test/java/br/gov/se/setc/cidadao/service/CidadaoServiceTest.java`

### Arquivos Modificados
- `src/main/java/br/gov/se/setc/cidadao/entity/Cidadao.java`
- `src/main/java/br/gov/se/setc/cidadao/service/CidadaoService.java`
- `src/main/java/br/gov/se/setc/usuario/controller/UsuarioController.java`

## Testes

A implementação inclui testes unitários e de integração que cobrem:

- ✅ Cadastro bem-sucedido de cidadão externo
- ✅ Validação de e-mail único
- ✅ Validação de CPF único
- ✅ Tratamento de CPF nulo ou vazio
- ✅ Validação de campos obrigatórios
- ✅ Retorno correto dos dados

## Próximos Passos

1. **Executar a migration**: Aplicar a migration V10 no banco de dados
2. **Executar testes**: Rodar os testes para verificar a implementação
3. **Testar endpoint**: Fazer chamadas reais para o endpoint
4. **Documentar API**: Atualizar documentação da API se necessário
