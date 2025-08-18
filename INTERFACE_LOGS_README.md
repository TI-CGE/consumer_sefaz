# Interface de Monitoramento de Logs em Tempo Real

## 📊 Visão Geral

Foi implementada uma interface web completa para monitoramento de logs em tempo real no projeto SEFAZ Transparency Consumer. A interface permite visualizar e monitorar 4 arquivos de log simultaneamente com atualizações automáticas.

## 🚀 Funcionalidades Implementadas

### ✅ Funcionalidades Principais
- **Visualização em Tempo Real**: 4 painéis para monitoramento simultâneo dos logs
- **Server-Sent Events (SSE)**: Atualizações automáticas sem necessidade de refresh
- **Interface Responsiva**: Compatível com desktop, tablet e mobile
- **Design Moderno**: Tema claro/escuro com animações suaves
- **Filtros Avançados**: Busca e filtro por conteúdo em cada log
- **Download de Logs**: Funcionalidade para baixar logs individuais
- **Controles de Visualização**: Pausar/retomar, limpar, auto-scroll

### 📁 Arquivos de Log Monitorados
1. **simple.log** - Logs simplificados com timestamps
2. **application.log** - Logs detalhados da aplicação
3. **errors.log** - Logs de erros
4. **operations.md** - Logs de operações em formato Markdown

## 🏗️ Arquitetura Implementada

### Backend (Spring Boot)
- **Controller**: `LogMonitorController.java`
  - Endpoints REST para leitura de logs
  - Server-Sent Events para tempo real
  - Monitoramento automático de mudanças nos arquivos

### Frontend (Web)
- **HTML**: `index.html` - Interface principal
- **CSS**: `styles.css` - Design responsivo e moderno
- **JavaScript**: `app.js` - Lógica de tempo real e interações

## 🌐 Endpoints da API

### Principais Endpoints
```
GET /                    - Página principal da interface
GET /logs/info          - Informações dos arquivos de log
GET /logs/{fileName}    - Conteúdo de um log específico
GET /logs/stream        - Server-Sent Events para tempo real
```

### Exemplos de Uso da API
```bash
# Obter informações dos logs
curl http://localhost:8083/logs/info

# Obter conteúdo do simple.log
curl http://localhost:8083/logs/simple.log

# Conectar ao stream de tempo real
curl -N http://localhost:8083/logs/stream
```

## 🎨 Interface do Usuário

### Características do Design
- **Header**: Logo, título e controles principais
- **Stats Bar**: Estatísticas de conexão e configurações
- **Grid de Logs**: 4 painéis organizados responsivamente
- **Footer**: Links úteis e informações do sistema

### Controles Disponíveis
- **🌙/☀️ Tema**: Alternar entre modo claro e escuro
- **⏸️/▶️ Pausar**: Pausar/retomar atualizações em tempo real
- **🗑️ Limpar**: Limpar visualização dos logs
- **🔄 Atualizar**: Recarregar todos os logs
- **🔍 Filtrar**: Buscar conteúdo específico em cada log
- **💾 Download**: Baixar logs individuais

### Funcionalidades por Painel
Cada painel de log possui:
- **Contador de linhas** em tempo real
- **Filtro individual** com highlight
- **Auto-scroll** configurável
- **Download** do conteúdo atual

## 🚀 Como Usar

### 1. Iniciar a Aplicação
```bash
# Usando Maven
mvn spring-boot:run

# Ou usando JAR
java -jar target/sefaz-transparency-consumer-0.0.1-SNAPSHOT.jar
```

### 2. Acessar a Interface
Abra o navegador e acesse:
```
http://localhost:8083
```

### 3. Monitoramento em Tempo Real
- A interface conecta automaticamente ao stream de logs
- Indicador de status mostra se está conectado (verde) ou desconectado (vermelho)
- Logs são atualizados automaticamente conforme novos dados chegam

### 4. Usar Filtros
1. Clique no ícone 🔍 em qualquer painel
2. Digite o termo de busca
3. Linhas correspondentes serão destacadas
4. Use o botão ✕ para limpar o filtro

### 5. Controlar Atualizações
- **Pausar**: Clique em ⏸️ para pausar atualizações
- **Auto-scroll**: Toggle no stats bar para controlar scroll automático
- **Limpar**: Clique em 🗑️ para limpar a visualização

## 🔧 Configuração

### Variáveis de Ambiente
```properties
# Caminho dos logs (padrão: ./logs)
logging.path=./logs

# Porta do servidor (padrão: 8083)
server.port=8083
```

### Arquivos de Log Suportados
A interface monitora automaticamente:
- `./logs/simple.log`
- `./logs/application.log`
- `./logs/errors.log`
- `./logs/operations.md`

## 📱 Responsividade

### Desktop (>1200px)
- Grid 2x2 com painéis grandes
- Todos os controles visíveis
- Máxima funcionalidade

### Tablet (768px-1200px)
- Grid adaptativo
- Controles reorganizados
- Interface otimizada

### Mobile (<768px)
- Layout em coluna única
- Controles compactos
- Painéis redimensionados

## 🎯 Recursos Avançados

### Server-Sent Events (SSE)
- Conexão persistente para atualizações em tempo real
- Reconexão automática em caso de falha
- Detecção de mudanças nos arquivos de log

### Monitoramento de Arquivos
- Verificação a cada segundo
- Detecção de rotação de logs
- Leitura incremental para performance

### Gestão de Estado
- Posições de arquivo mantidas em memória
- Filtros persistentes durante a sessão
- Configurações salvas no localStorage

## 🔍 Troubleshooting

### Problemas Comuns

1. **Interface não carrega**
   - Verificar se a aplicação está rodando na porta 8083
   - Verificar logs de erro no console do navegador

2. **Logs não atualizam**
   - Verificar indicador de conexão (deve estar verde)
   - Verificar se os arquivos de log existem em `./logs/`

3. **Performance lenta**
   - Pausar atualizações se necessário
   - Limpar logs antigos da visualização

### Logs de Debug
Para debug, verificar:
- Console do navegador (F12)
- Logs da aplicação Spring Boot
- Network tab para verificar conexões SSE

## 📋 Próximos Passos

### Melhorias Futuras
- [ ] Configuração de intervalos de atualização
- [ ] Exportação de logs filtrados
- [ ] Alertas para erros críticos
- [ ] Histórico de logs arquivados
- [ ] Métricas de performance

### Integração
A interface está totalmente integrada ao projeto existente e:
- Não interfere com funcionalidades existentes
- Usa a mesma configuração de logging
- Compartilha a porta e contexto da aplicação principal

## 📞 Suporte

Para questões ou problemas:
1. Verificar logs da aplicação
2. Consultar documentação do Spring Boot
3. Verificar configurações de rede/firewall

---

**Desenvolvido para SETC - Secretaria de Estado da Transparência e Controle**  
**Versão**: 1.0.0  
**Data**: Agosto 2025
