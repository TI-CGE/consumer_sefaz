# 🔄 Solução para Rotação Automática de Logs

## 📋 Problema Identificado

O arquivo `operations.md` estava crescendo muito rapidamente (53.041 linhas) e ficando difícil de ler na IDE antes da rotação automática acontecer.

## ✅ Soluções Implementadas

### 1. **Rotação Mais Frequente**
- **Antes**: Rotação aos 50MB
- **Agora**: Rotação aos 5MB
- **Resultado**: Arquivos menores e mais gerenciáveis

### 2. **Monitoramento Ativo**
- Novo serviço `LogRotationService` monitora o tamanho a cada 30 minutos
- Força rotação quando atinge 3MB (antes do limite de 5MB)
- Compactação automática dos arquivos rotacionados

### 3. **Endpoints para Controle Manual**

#### 📊 Verificar Status
```
GET /api/logs/operations/info
```
Retorna informações detalhadas sobre o `operations.md`:
- Tamanho atual em MB e bytes
- Número estimado de linhas
- Se precisa de rotação
- Recomendações

#### 🔄 Forçar Rotação
```
POST /api/logs/rotate
```
Força rotação imediata do `operations.md`:
- Move arquivo atual para arquivo timestampado
- Compacta o arquivo rotacionado (.gz)
- Cria novo `operations.md` vazio
- Retorna estatísticas de compressão

#### 🧹 Limpeza Geral
```
POST /api/logs/cleanup
```
Executa limpeza completa:
- Verifica se `operations.md` precisa rotação
- Remove arquivos antigos
- Remove arquivos vazios
- Verifica tamanho total

### 4. **Melhorias na Limpeza Automática**
- Integração com o serviço de rotação
- Verificação automática do `operations.md` durante limpeza
- Rotação preventiva incluída no processo

## 🚀 Como Usar

### Resolver o Problema Atual
1. **Verificar status atual**:
   ```bash
   curl http://localhost:8080/api/logs/operations/info
   ```

2. **Forçar rotação imediata**:
   ```bash
   curl -X POST http://localhost:8080/api/logs/rotate
   ```

3. **Verificar resultado**:
   - Novo `operations.md` vazio criado
   - Arquivo antigo compactado e salvo
   - Logs voltam a ser legíveis na IDE

### Monitoramento Contínuo
- O sistema agora monitora automaticamente a cada 30 minutos
- Rotação automática quando atingir 3MB
- Limpeza diária às 2:00 AM inclui verificação de rotação

## 📁 Arquivos Modificados

1. **`logback-spring.xml`**
   - Reduzido `maxFileSize` de 50MB para 5MB
   - Reduzido `totalSizeCap` de 1GB para 500MB

2. **`LogRotationService.java`** (NOVO)
   - Monitoramento ativo do tamanho
   - Rotação forçada com compactação
   - Informações detalhadas do arquivo

3. **`LogManagementController.java`**
   - Novos endpoints para rotação e informações
   - Integração com serviços de rotação e limpeza

4. **`LogCleanupService.java`**
   - Integração com rotação automática
   - Verificação preventiva durante limpeza

5. **`application.properties`**
   - Configurações para rotação e limpeza

## ⚙️ Configurações

```properties
# Rotação ativa
logging.rotation.enabled=true
logging.rotation.max-size-mb=3

# Limpeza automática
logging.cleanup.enabled=true
logging.cleanup.max-age-days=7
logging.cleanup.max-size-mb=500
```

## 📊 Benefícios

1. **Arquivos Menores**: Máximo 5MB antes da rotação
2. **Monitoramento Ativo**: Verificação a cada 30 minutos
3. **Controle Manual**: Endpoints para resolver problemas imediatos
4. **Compactação**: Arquivos antigos compactados automaticamente
5. **Prevenção**: Rotação preventiva aos 3MB

## 🔧 Testando a Solução

1. **Verificar configuração**:
   ```bash
   curl http://localhost:8080/api/logs/status
   ```

2. **Informações detalhadas**:
   ```bash
   curl http://localhost:8080/api/logs/operations/info
   ```

3. **Forçar rotação (se necessário)**:
   ```bash
   curl -X POST http://localhost:8080/api/logs/rotate
   ```

4. **Executar limpeza**:
   ```bash
   curl -X POST http://localhost:8080/api/logs/cleanup
   ```

## 🎯 Resultado Esperado

- ✅ Arquivo `operations.md` sempre menor que 5MB
- ✅ Rotação automática preventiva
- ✅ Arquivos antigos compactados
- ✅ Controle manual quando necessário
- ✅ Logs legíveis na IDE
- ✅ Histórico preservado em arquivos compactados

## 📝 Próximos Passos

1. **Testar a solução** executando a rotação manual
2. **Monitorar** o comportamento automático
3. **Ajustar configurações** se necessário (tamanhos, intervalos)
4. **Documentar** qualquer comportamento específico do ambiente
