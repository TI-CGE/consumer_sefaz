# Deploy automático (merge na main)

## Runner auto-hospedado + compose geral

A máquina de produção instala um **runner auto-hospedado** do GitHub (conexão de saída; não precisa de IP exposto). Em push na `main`, o workflow **deploy.yml** roda no runner: faz checkout, sincroniza o código para a pasta do app no servidor e dispara **build e up** no **compose geral** do servidor. A aplicação roda dentro do container gerenciado por esse compose.

Detalhes do fluxo, variáveis e exemplo de como o compose geral deve estar: **COMPOSE-GERAL.md**.

Resumo do workflow `.github/workflows/deploy.yml`:

- **Quando:** push na branch `main`.
- **Onde:** runner `self-hosted` (máquina de produção).
- **O que faz:** checkout → rsync para `APP_PATH` (ex.: `/srv/apps/consumer_sefaz`) → no `COMPOSE_DIR` executa `docker compose build SERVICE_NAME` e `docker compose up -d SERVICE_NAME`.

Não é necessário configurar secrets para esse modo. Ajuste `COMPOSE_DIR`, `APP_PATH` e `SERVICE_NAME` no próprio workflow conforme o servidor.

---

## 1. Verificação em PR

Ao abrir um **Pull Request para a branch `main`**, o GitHub Actions executa (nos runners do GitHub, na nuvem):

- Checkout do código
- Java 21 (Eclipse Temurin)
- `mvn clean verify -DskipTests`

O PR só deve ser mergeado se esse job passar.

---

## (Opcional) Deploy via webhook

Os scripts `scripts/webhook_listener.py` e `scripts/deploy.sh` existem no repositório para uso futuro com webhook (ex.: servidor com IP exposto ou túnel). O deploy atual usa apenas o runner auto-hospedado.
