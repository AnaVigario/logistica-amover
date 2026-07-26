# Documentação — Motor de Otimização de Rotas (A-MoVeR)

Equipa de **logística e otimização de rotas**. Este documento descreve **o que foi
criado de novo** no âmbito desta peça do projeto — o motor de rotas e tudo o que o
suporta — e o estado da integração com o resto do ecossistema A-MoVeR.

---

## 1. Objetivo

Transformar a otimização de rotas num **microserviço próprio, limpo e isolado**,
alinhado com a arquitetura do A-MoVeR (Python + Flask + Google OR-Tools, *stateless*,
chamado pelo backend .NET), substituindo a abordagem anterior em que o código de
rotas estava misturado com gestão de dados (a antiga `API_Amover_fixed`, agora mantida
apenas como salvaguarda).

Princípio central: **o motor é stateless** — recebe coordenadas e restrições, devolve a
ordem ótima + métricas, e **não** acede à base de dados nem gere motas/pessoas/empresas
(isso é do backend .NET).

---

## 2. Novo microserviço: `amover-routes-optimizer`

Projeto novo, criado de raiz. Estrutura:

```
amover-routes-optimizer/
├── app.py                     # Flask: endpoints /optimize e /health
├── optimizer.py               # Validação + orquestração + métricas (núcleo)
├── algorithms/
│   ├── geo.py                 # Distâncias reais (Haversine) e métricas
│   ├── ortools_vrptw.py       # Motor principal (OR-Tools: deadlines/prioridades/energia)
│   └── heuristics.py          # nearest_neighbor, tabu_search, grasp, savings, branch_and_bound
├── tests/sample_request.json  # Pedido de exemplo
├── requirements.txt           # Flask, ortools, gunicorn
├── Dockerfile / Dockerfile.python   # Imagem do serviço (porta 5000, gunicorn)
├── .env.example / .gitignore
└── README.md                  # Contrato, como correr, integração
```

### 2.1 Endpoints

| Método | Rota        | Função                                         |
|--------|-------------|------------------------------------------------|
| GET    | `/health`   | Estado do serviço (healthcheck)                |
| POST   | `/optimize` | Calcula a ordem ótima das paragens             |

### 2.2 Algoritmos disponíveis

| `algorithm`        | Tipo                  | Notas                                          |
|--------------------|-----------------------|------------------------------------------------|
| `auto`             | —                     | Corre vários e escolhe o melhor (completo + viável, menor distância) |
| `ortools_vrptw`    | OR-Tools              | **Principal**. Trata deadlines, prioridades e energia |
| `nearest_neighbor` | Heurística construtiva| Com melhoria 2-opt                             |
| `tabu_search`      | Metaheurística        | Vizinhança por troca de pares                  |
| `grasp`            | Metaheurística        | Construção com RCL + 2-opt                      |
| `savings`          | Clarke & Wright       | Depósito único                                 |
| `branch_and_bound` | Exato                 | Só instâncias pequenas (≤ 9 paragens)          |

**Refatoração importante:** os algoritmos heurísticos existiam como *scripts* que liam
JSON e faziam `print` do resultado, e eram chamados por **subprocesso**. Foram
convertidos em **funções puras importáveis**, eliminando os subprocessos (mais rápido e
robusto). O `ortools_vrptw` foi reaproveitado do projeto antigo (já era uma função).

As heurísticas ordenam num plano; as **métricas finais (km/kWh/min) são sempre
recalculadas por Haversine** (`algorithms/geo.py`), por isso são comparáveis entre
algoritmos.

### 2.3 Contrato `POST /optimize`

O serviço aceita **dois formatos** no mesmo endpoint (deteta automaticamente qual):

**Formato legado** — compatível com o que o backend .NET (`RouteServices.cs`) já envia.
Não exige qualquer alteração no backend:
```jsonc
// Pedido
{ "nodes": [ { "id": 0, "x": <lat>, "y": <lon>, "demand": 0 }, ... ],
  "vehicles": [ { "capacity": 9999, "battery_kwh": 9999 } ] }
// Resposta
{ "route": [0, 2, 1, 3, 0], "distance_km": 7.2, "energy_kwh": 0.36 }
```

**Formato rico** — para quando se quiser usar prioridades, deadlines e energia:
```jsonc
{ "algorithm": "auto",
  "depot":   { "lat": ..., "lon": ... },
  "vehicle": { "capacity": ..., "battery_kwh": ..., "current_charge_kwh": ..., "speed_kph": ..., "consumption_kwh_per_km": ... },
  "tasks":   [ { "id": 101, "lat": ..., "lon": ..., "demand": 20, "priority": "ALTA", "deadline": "2026-06-30T17:00:00Z" } ],
  "options": { "mode": "balanced", "time_limit_s": 5 } }
```

### 2.4 Melhorias face ao serviço antigo

- Corre **todos os algoritmos** (e modo `auto`), não apenas o nearest neighbor.
- **Validação de input** a sério: devolve `400` com mensagem clara em vez de `500`.
- **Drop-in** do serviço atual: mesmo container (`amover-routes-optimizer:5000`) e
  mesmo contrato, sem o backend mudar nada.
- Import "lazy" do OR-Tools e registo de pedidos (logs de acesso).

---

## 3. Kit de teste de integração local (`integration-local/`)

Conjunto de ferramentas criado para validar a integração na máquina, de forma isolada,
sem tocar na base de dados partilhada nem no GitHub:

| Ficheiro                       | Função                                                                 |
|--------------------------------|------------------------------------------------------------------------|
| `docker-compose.override.yml`  | Faz o stack usar o **novo motor**, sem alterar o repositório           |
| `run_local.ps1`                | Script único: levanta o stack, (opcional) clona dados e corre os testes|
| `test_integration.sh`          | Teste rápido do fluxo completo (token → backend → otimizador → BD)     |
| `geocode_tasks.py`             | Geocoding das moradas → coordenadas reais (ver secção 4)               |
| `README.md`                    | Runbook passo-a-passo                                                   |

---

## 4. Script de geocoding (`integration-local/geocode_tasks.py`)

Resolve um problema identificado: quando uma tarefa não tem coordenadas, o backend
inventa-as com `Random()`, pelo que a rota não tinha sentido geográfico. O script:

1. Lê a morada de cada tarefa (`street`, `door_number`, `postal_code`, `city`).
2. Geocodifica com o **Nominatim** (OpenStreetMap, gratuito).
3. Atualiza a `LocationNode` com **lat/lon reais**.

Depois de o correr, a rota passa a basear-se nas moradas verdadeiras.

---

## 5. Resultados validados

A integração foi testada **de ponta a ponta** com o ecossistema completo a correr
localmente (Postgres + Keycloak + backend .NET + frontend + o novo motor):

- **Autenticação (Keycloak) → backend (.NET) → motor de rotas → base de dados**: a
  cadeia completa funciona. O `POST /api/route/optimize-for-vehicle` devolveu
  *"Rota optimizada com sucesso"* e a ordem ficou gravada na BD (`stopOrder`).
- **OR-Tools confirmado**: a resposta do motor traz `algorithm_used: ortools_vrptw` —
  prova de que o algoritmo principal está instalado e a ser usado.
- **Geocoding confirmado**: as 3 tarefas de teste (moradas em **Vila Real**) passaram
  de coordenadas aleatórias para coordenadas reais (~41.30, -7.74), e a rota foi
  recalculada com base nelas (a ordem mudou de `2 → 1 → 3` para `1 → 2 → 3`).

---

## 6. O que ainda falta (próximos passos)

1. **Prioridade e deadline na otimização.** Os campos `priority` (ALTA/MÉDIA/BAIXA) e
   `deadline` já existem na base, mas o backend .NET **ainda não os envia** ao motor —
   por isso a rota é hoje otimizada só por distância. Falta: o `RouteServices.cs` incluir
   `priority`/`deadline` no payload, e o motor passar a usá-los (o `ortools_vrptw` já sabe).
2. **Geocoding no backend.** Mover a conversão morada→coordenadas para o backend (ao
   criar a `LocationNode`), em vez do `Random()` e do script manual.
3. **Disponibilizar aos colegas.** Quando a equipa estiver de acordo, mover o motor para
   a pasta `routes/Projeto Final` do repositório e abrir o pull request.

---

*Estado atual: motor novo construído, integração validada localmente com dados e moradas
reais. Pronto para evoluir (prioridades/deadlines) e, depois, para partilhar.*
