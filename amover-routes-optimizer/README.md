# A-MoVeR · Motor de Otimização de Rotas

Microserviço **stateless** em **Python + Flask + Google OR-Tools** que calcula a
ordem ótima de visita das tarefas de uma mota elétrica. É a peça da equipa de
**logística e otimização de rotas** dentro do ecossistema A-MoVeR.

> **Stateless por design.** Este serviço **não** acede à base de dados nem gere
> motas/pessoas/empresas. O backend (.NET) reúne as coordenadas das tarefas do
> dia, chama `POST /optimize`, recebe a ordem ótima + métricas, e é o .NET que
> grava a rota e notifica a app do condutor. Isto segue exatamente o fluxo
> descrito em `Estrutura_e_Ecossistema.md`.

Este projeto substitui, **apenas na parte das rotas**, o antigo `API_Amover_fixed`
(Django + acesso direto ao Supabase). O `API_Amover_fixed` deve ser mantido como
**salvaguarda/backup** e já não é o caminho ativo.

---

## Endpoints

| Método | Rota         | Descrição                                  |
|--------|--------------|--------------------------------------------|
| GET    | `/health`    | Estado do serviço (para healthchecks).     |
| POST   | `/optimize`  | Calcula a rota ótima a partir das tarefas. |

### `POST /optimize` — Pedido

```jsonc
{
  "algorithm": "auto",            // auto | ortools_vrptw | tabu_search | nearest_neighbor | grasp | savings | branch_and_bound
  "depot":   { "lat": 41.5454, "lon": -8.4265 },
  "vehicle": {
    "capacity": 500,              // capacidade (mesma unidade do "demand")
    "battery_kwh": 70,            // capacidade total da bateria
    "current_charge_kwh": 55,     // opcional → ativa otimização sensível à energia
    "speed_kph": 40,
    "consumption_kwh_per_km": 0.05
  },
  "tasks": [
    { "id": 101, "lat": 41.55, "lon": -8.42, "demand": 20,
      "priority": "ALTA", "deadline": "2026-06-30T17:00:00Z", "service_minutes": 1 }
  ],
  "options": {
    "route_start": "2026-06-30T09:00:00Z",  // opcional (default: agora, UTC)
    "mode": "balanced",
    "time_limit_s": 5
  }
}
```

Campos por tarefa: `id`, `lat`, `lon` são obrigatórios; `demand` (default 0),
`priority` (ALTA/MÉDIA/BAIXA), `deadline` (ISO 8601) e `service_minutes`
(default 1) são opcionais.

### `POST /optimize` — Resposta

```jsonc
{
  "ok": true,
  "algorithm_used": "ortools_vrptw",
  "phase": "hard",                 // hard = todas as ALTA cumprem a deadline | soft = atrasos minimizados | null nas heurísticas
  "route": [
    { "sequence": 1, "task_id": 104, "lat": 41.5290, "lon": -8.4400 },
    { "sequence": 2, "task_id": 101, "lat": 41.5510, "lon": -8.4200 }
  ],
  "dropped_task_ids": [],          // tarefas não incluídas (capacidade/energia/deadline)
  "metrics": {
    "total_distance_km": 12.34,
    "total_energy_kwh": 0.62,
    "total_time_min": 35.2,
    "feasible": true
  }
}
```

Erros de input devolvem `400` com `{ "ok": false, "error": "..." }`.

---

## Algoritmos

| `algorithm`        | Tipo                       | Notas                                                |
|--------------------|----------------------------|------------------------------------------------------|
| `auto`             | —                          | Corre vários e escolhe o melhor (completo+viável, menor distância). |
| `ortools_vrptw`    | Exato/meta (OR-Tools)      | **Principal.** Trata deadlines, prioridades e energia. |
| `nearest_neighbor` | Heurística construtiva     | Rápido; com melhoria 2-opt.                          |
| `tabu_search`      | Metaheurística             | Vizinhança por troca de pares.                       |
| `grasp`            | Metaheurística             | Construção com RCL + 2-opt.                          |
| `savings`          | Heurística (Clarke-Wright) | Poupanças; depósito único.                           |
| `branch_and_bound` | Exato                      | Só instâncias pequenas (≤ 9 paragens).               |

> `dijkstra` (caminho ponto-a-ponto com energia/carregamento) existe no projeto
> original mas **não** é um otimizador de tour, por isso não faz parte de
> `/optimize`. Pode vir a ser um endpoint próprio (`/shortest-path`) no futuro.

As heurísticas ordenam num plano (lon, lat); as **métricas finais (km/kWh/min)
são sempre recalculadas por Haversine** (`algorithms/geo.py`), por isso são
comparáveis entre algoritmos.

---

## Compatibilidade com o repositório `logistica-amover` (backend .NET)

O backend .NET (`backend/Services/RouteServices.cs`) chama
`POST http://amover-routes-optimizer:5000/optimize` e **lê apenas o campo `route`**
(lista de IDs). O payload que envia é o **formato legado**:

```jsonc
{
  "nodes": [
    { "id": 0,   "x": <lat>, "y": <lng>, "demand": 0 },   // depósito = id 0
    { "id": 101, "x": <lat>, "y": <lng>, "demand": 1 }     // x=latitude, y=longitude
  ],
  "vehicles": [ { "capacity": 9999, "battery_kwh": 9999.0 } ]
}
```

Resposta legada (a que o .NET espera):

```jsonc
{ "route": [0, 101, 104, ...], "distance_km": 12.5, "energy_kwh": 0.63 }
```

**Este serviço aceita os DOIS formatos** no mesmo `/optimize` (deteta `nodes` vs
`tasks`), por isso é um **drop-in** do atual `routes/Projeto Final` — mas em vez
de correr só o `nearest_neighbor`, corre **todos os algoritmos** (e `auto`
escolhe o melhor). O .NET não precisa de mudar nada.

### Como integrar (sem partir o que existe)
1. Substituir o conteúdo de `routes/Projeto Final` por este projeto (já inclui
   `Dockerfile.python`, e o serviço mantém-se `amover-routes-optimizer:5000`).
2. `docker-compose up -d --build routes-optimizer`. Fim — passa a haver `auto` +
   todos os algoritmos, com a mesma chamada do .NET.

### Próximo passo (desbloquear o OR-Tools a sério)
O modelo `Task` do .NET já tem `priority` e `deadline`. Quando o backend os
passar no payload (ou migrar para o formato rico `depot`/`tasks`), o
`ortools_vrptw` passa a respeitar prioridades e janelas de tempo — algo que o
`nearest_neighbor` atual não faz.

---

## Correr localmente

```bash
python -m venv .venv
source .venv/bin/activate        # Windows: .\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python app.py                    # http://localhost:5000

# Testar:
curl -s -X POST http://localhost:5000/optimize \
  -H "Content-Type: application/json" \
  --data @tests/sample_request.json | python -m json.tool
```

## Correr com Docker

```bash
docker build -t amover-routes-optimizer .
docker run --rm -p 5000:5000 amover-routes-optimizer
```

### Integração no `docker-compose.yml` do A-MoVeR

```yaml
  amover-routes-optimizer:
    build: ./routes            # pasta deste serviço no monorepo
    container_name: amover-routes-optimizer
    ports:
      - "5000:5000"
    networks:
      - amover-net
```

O backend .NET fala com ele pela rede interna, ex.: `POST http://amover-routes-optimizer:5000/optimize`.

---

## Estrutura

```
amover-routes-optimizer/
├── app.py                     # Flask: /optimize, /health
├── optimizer.py               # Validação + orquestração + métricas
├── algorithms/
│   ├── geo.py                 # Haversine + métricas reais
│   ├── ortools_vrptw.py       # Motor principal (OR-Tools)
│   └── heuristics.py          # nearest_neighbor, tabu, grasp, savings, branch&bound
├── tests/sample_request.json
├── requirements.txt
├── Dockerfile
└── .env.example
```
