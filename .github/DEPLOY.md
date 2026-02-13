# Deploy automático (merge na main)

## Sem IP exposto: runner auto-hospedado

A máquina de produção **não precisa ter IP ou porta expostos**. Ela instala um **runner auto-hospedado** do GitHub: o runner **sai** para o GitHub (conexão de saída). Quando alguém dá merge na `main`, o workflow roda **na própria máquina de produção** e executa `docker compose down` e `docker compose up -d --build` ali.

### 1. Na máquina de produção

Requisitos: **Docker**, **Docker Compose** e **Git** instalados; o usuário que roda o runner deve poder usar `docker` (ex.: no grupo `docker`).

1. No repositório no GitHub: **Settings → Actions → Runners → New self-hosted runner**.
2. Escolha o sistema (Linux, Windows, etc.) e siga os comandos que o GitHub mostrar (baixar, configurar, rodar). Ao pedir **labels**, pode usar `self-hosted` (já vem) ou adicionar `production`.
3. Deixe o runner rodando como serviço (o assistente do GitHub oferece comandos para systemd/svc).
4. O runner usa uma pasta de trabalho (ex.: `_work`). O workflow faz **checkout** do repositório nessa pasta e depois roda `docker compose` nesse diretório. Se você precisar de **variáveis de ambiente** (banco, senhas), crie um arquivo **`.env`** na **pasta do repositório que o runner usa** (a pasta onde o checkout é feito, algo como `_work/consumer_sefaz/consumer_sefaz` no runner). Ou configure as env vars no sistema para o usuário do runner.

### 2. No GitHub

Nada além do workflow que já está em `.github/workflows/deploy.yml`:

- **Quando:** push na branch `main` (ex.: merge de PR).
- **Onde:** no runner com label `self-hosted` (a sua máquina de produção).
- **O que faz:** checkout do código da `main`, `docker compose down`, `docker compose up -d --build`.

Não é necessário configurar **nenhum secret** (como `DEPLOY_WEBHOOK_URL`) para esse modo.

### 3. Resumo

| Você | Ação |
|------|------|
| Máquina atrás de firewall / sem IP exposto | Instala o runner auto-hospedado; a máquina só precisa de saída para a internet (GitHub). |
| Merge na `main` | O GitHub dispara o job no seu runner; o job roda na produção e sobe os containers. |

Os arquivos `scripts/webhook_listener.py` e `scripts/deploy.sh` continuam no repositório caso você queira usar **webhook** no futuro (por exemplo, com túnel ou em outro servidor com IP exposto).

---

## 1. Verificação em PR

Ao abrir um **Pull Request para a branch `main`**, o GitHub Actions executa (nos runners do GitHub, na nuvem):

- Checkout do código
- Java 21 (Eclipse Temurin)
- `mvn clean verify -DskipTests`

O PR só deve ser mergeado se esse job passar.

---

## 2. (Opcional) Deploy via webhook

Se um dia você tiver uma URL acessível (IP exposto ou túnel), pode usar o webhook em vez do runner:

- Secret **DEPLOY_WEBHOOK_URL**: URL que recebe o POST (ex.: `https://servidor:9000/?token=SEU_TOKEN`).
- Na máquina: rodar `scripts/webhook_listener.py` (com `WEBHOOK_SECRET` igual ao token da URL); o listener chama `scripts/deploy.sh` (git pull + docker compose down/up).

O workflow atual **não** usa webhook; ele usa apenas o runner auto-hospedado.
