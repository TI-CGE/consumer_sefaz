# Instalar runner auto-hospedado no Linux

## 1. Obter o token e o link no GitHub

1. Abra o repositório no GitHub.
2. **Settings** → **Actions** → **Runners**.
3. Clique em **New self-hosted runner**.
4. Selecione **Linux** e a arquitetura (em geral **x64**).
5. Na página aparecem:
   - uma **URL** do repositório (ex.: `https://github.com/SEU_USUARIO/consumer_sefaz`);
   - um **token** (válido por pouco tempo, ~1 hora);
   - os comandos para baixar o runner.

Deixe essa página aberta; você vai usar o token nos próximos passos.

---

## 2. Na máquina Linux (produção)

Instale **Docker** e **Docker Compose** se ainda não tiver. O usuário que rodar o runner precisa poder usar `docker` (ex.: no grupo `docker`).

Crie um diretório e entre nele:

```bash
mkdir -p ~/actions-runner && cd ~/actions-runner
```

Baixe o runner (use a **versão** e o **link** que o GitHub mostrar na página do passo 1; o exemplo abaixo é para x64):

```bash
curl -o actions-runner-linux-x64-2.311.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.311.0/actions-runner-linux-x64-2.311.0.tar.gz
tar xzf actions-runner-linux-x64-2.311.0.tar.gz
```

Configure o runner (substitua `REPO_URL` e `TOKEN` pelos valores da página do GitHub):

```bash
./config.sh --url https://github.com/SEU_USUARIO/consumer_sefaz --token TOKEN_DA_PAGINA
```

Quando perguntar o **name** do runner, pode usar por exemplo `production` ou Enter para o padrão.  
Quando perguntar **labels**, pode deixar o padrão (`self-hosted`, `Linux`, `X64`) ou adicionar outras; o workflow usa `self-hosted`.

Instale e inicie o serviço (roda ao subir a máquina):

```bash
sudo ./svc.sh install
sudo ./svc.sh start
```

Para rodar como outro usuário (ex.: usuário que já usa Docker):

```bash
sudo ./svc.sh install NOME_DO_USUARIO
sudo ./svc.sh start
```

---

## 3. Conferir no GitHub

Em **Settings** → **Actions** → **Runners** deve aparecer o runner com status **Idle** (verde). Quando houver push na `main`, o job de deploy será executado nessa máquina.

---

## Comandos úteis

| Ação              | Comando               |
|-------------------|------------------------|
| Parar o serviço   | `sudo ./svc.sh stop`   |
| Iniciar o serviço | `sudo ./svc.sh start`  |
| Status            | `sudo ./svc.sh status` |
| Desinstalar       | `sudo ./svc.sh uninstall` |

---

## Versão do runner

Se a versão no GitHub for diferente de `2.311.0`, use a URL e o nome do arquivo que aparecem na página **New self-hosted runner** (seção “Download”).
