# Teste de integração local — A-MoVeR (stack completa, isolada)

Objetivo: correr **todo o ecossistema na tua máquina** (Postgres local + Keycloak +
backend .NET + frontend + o **teu** motor de rotas), validar a integração
ponta-a-ponta e **só depois** disponibilizar aos colegas. Nada toca na base de
dados partilhada — o Postgres é um contentor local.

## Caminho rápido (one-click)

Em vez de fazeres os passos à mão, podes correr o script que automatiza tudo
(levanta a stack, carrega os dados e testa):

```powershell
cd D:\amover-routes-optimizer\integration-local

# RECOMENDADO: clonar a BD a partir do .sql que o colega te enviou.
# Não te ligas à máquina dele — só carregas no Postgres local.
.\run_local.ps1 -DumpFile "D:\amover_data.sql" -KcUser testuser -KcPass "MinhaPass123"

# Só levantar e testar (sem dados externos; usa os que o backend/EF cria):
.\run_local.ps1 -KcUser testuser -KcPass "MinhaPass123"
```

### Como obter o `.sql` do colega (ele corre isto, uma vez)
Se ele usa o stack em Docker:
```bash
docker exec amover-postgres pg_dump --data-only --no-owner --disable-triggers `
  -U postgres -d amover-data > amover_data.sql
```
Se ele corre o Postgres nativo:
```bash
pg_dump --data-only --no-owner --disable-triggers -h localhost -p 5432 -U postgres -d amover-data -f amover_data.sql
```
Depois envia-te o `amover_data.sql` e tu usas `-DumpFile`.

O script **só escreve no Postgres local**, **não toca em nenhuma BD remota** e
**não toca no GitHub**. Os passos manuais abaixo continuam válidos se preferires
controlar cada etapa.

## O que descobri no repositório (resumo)

- **Keycloak**: `kc-export/amover-realm-realm.json` traz o realm `amover-realm`,
  os roles `admin` e `motorista`, o client público `amover-api` e ~8 utilizadores.
  O compose importa-o sozinho (`start-dev --import-realm`).
- **Base de dados**: o backend corre `db.Database.Migrate()` no arranque, ou seja
  **o EF Core cria o schema automaticamente**. Não precisas do `02_schema.sql.bak`
  (que aliás seria ignorado pelo Postgres, por ser `.bak`). O teu "dump fresco"
  passa a ser só **dados** (carregados por cima do schema do EF).
- **Chamada da integração**: o backend faz `POST http://amover-routes-optimizer:5000/optimize`
  e lê só o campo `route`. O teu motor já é compatível com esse formato.

## Pré-requisitos

- Docker Desktop a correr.
- `git` para clonar o repo.
- Acesso à BD atual (para fazer o `pg_dump` dos dados frescos).
- Portas livres: `3000`, `5029`, `5000`, `5435`, `8080`, `8443`.

---

## Passo 1 — Clonar o repo como vizinho do teu motor

```bash
# Recomendado: clonar AO LADO da pasta amover-routes-optimizer
cd D:\
git clone https://github.com/AnaVigario/logistica-amover.git
```

Fica assim:
```
D:\amover-routes-optimizer   <- o teu motor (já tens)
D:\logistica-amover          <- repo clonado
```

## Passo 2 — Ativar o teu motor no stack (sem mexer no repo)

Copia o override para a raiz do repo clonado:
```bash
copy D:\amover-routes-optimizer\integration-local\docker-compose.override.yml  D:\logistica-amover\
```
Isto faz o serviço `routes-optimizer` ser construído a partir de
`../amover-routes-optimizer` (o teu motor, com todos os algoritmos), mantendo o
container `amover-routes-optimizer:5000` que o backend já chama.

## Passo 3 — Levantar a stack

```bash
cd D:\logistica-amover
docker-compose up -d --build
```
Primeira vez demora uns minutos. No arranque o backend aplica as migrações EF
(cria as tabelas). Verifica:
```bash
docker ps
docker logs amover-backend --tail 30      # deve dizer "Migrações verificadas/aplicadas com sucesso"
docker logs amover-routes-optimizer --tail 20
```
Acessos: frontend `http://localhost:3000` · Swagger `http://localhost:5029/swagger` ·
Keycloak `http://localhost:8080` (admin/admin).

## Passo 4 — Carregar os DADOS frescos da BD atual

Como o schema já é criado pelo EF, usa um **dump só de dados**.

1. Exporta da BD atual (ajusta host/porta/user/db da BD em uso):
```bash
pg_dump --data-only --no-owner --disable-triggers \
  -h <HOST_BD_ATUAL> -p <PORTA> -U <USER> -d <DB_ATUAL> \
  -f amover_data.sql
```
2. Carrega no Postgres local (o backend já criou as tabelas no Passo 3):
```bash
docker cp amover_data.sql amover-postgres:/tmp/amover_data.sql
docker exec -it amover-postgres psql -U postgres -d amover-data -f /tmp/amover_data.sql
```

> Se houver conflitos de chaves/sequências, normalmente é por o EF já ter inserido
> linhas-semente. Solução limpa: `docker-compose down -v` (apaga o volume),
> `up -d --build` outra vez e carrega o dump **antes** de usares a app.
> Se o `--disable-triggers` der erro de permissões, tira essa flag e garante a
> ordem das tabelas (ou usa `--data-only --column-inserts`).

## Passo 5 — Definir uma password de teste no Keycloak

O export traz utilizadores, mas pode não trazer passwords. Para teres um login:
1. `http://localhost:8080` → admin/admin → realm **amover-realm**.
2. **Users** → escolhe um (ex.: `testuser`) → **Credentials** → **Set password**
   → desliga **Temporary** → guarda.
3. (Garante que esse user tem o role certo: **Role mapping** → `admin` ou `motorista`.)

## Passo 6 — Testar a integração

Opção A — script automático (Git Bash / WSL):
```bash
cd D:\amover-routes-optimizer\integration-local
bash test_integration.sh testuser <password> <vehicleId> 1,2,3
```
Faz: health do otimizador → `/optimize` direto → token Keycloak → backend
`optimize-for-vehicle` → lê a rota gravada.

Opção B — manual pelo Swagger:
1. `http://localhost:5029/swagger` → **Authorize** (cola o token do Keycloak).
2. `POST /api/route/optimize-for-vehicle` com `{ "vehicleId":1, "date":"2026-06-30", "taskIds":[1,2,3] }`.
3. `GET /api/route?vehicleId=1&date=2026-06-30` → confirma `stopOrder` preenchido.

Sinais de sucesso:
- `docker logs amover-routes-optimizer` mostra o pedido `/optimize` (200).
- A resposta do backend é "Rota optimizada com sucesso" e o GET devolve as paragens ordenadas.

## Passo 7 — Quando estiver tudo verde → disponibilizar aos colegas

Aí sim avançamos para o repositório: meter o motor em `routes/Projeto Final` e
abrir um pull request. (Posso preparar essa pasta + a descrição do PR quando quiseres.)

---

## Troubleshooting rápido

- **Backend não liga à BD**: ele tenta 5x com 5s de espera; confirma `docker logs amover-backend`.
- **401 nas chamadas**: token expirado ou user sem role; repete o Passo 5/6.
- **Otimizador não responde**: `docker logs amover-routes-optimizer`; testa direto `curl http://localhost:5000/health`.
- **Reset total da BD**: `docker-compose down -v && docker-compose up -d --build`.
- **Mudaste o motor**: `docker-compose up -d --build routes-optimizer` para reconstruir só esse serviço.
