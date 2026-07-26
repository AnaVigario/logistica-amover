"""
optimizer.py - Orquestracao do motor de rotas.
Responsabilidades:
1. Validar e normalizar o pedido (POST /optimize).
2. Executar o algoritmo pedido (ou comparar varios em modo "auto").
3. Recalcular metricas reais (km/kWh/min), iguais para todos.
4. Construir a resposta segundo o contrato.
Stateless: nada toca na base de dados. Recebe coordenadas, devolve a ordem.

Modos de coordenadas (options.coordinates):
  - "geographic" (default): lat/lon reais -> Haversine (fluxo real do .NET).
  - "cartesian": pontos num plano x,y -> euclidiana (cenarios de teste).
O formato legado (nodes/vehicles, benchmarks) usa "cartesian" automaticamente.
"""
import logging
from datetime import datetime, timezone
from algorithms import geo, heuristics
log = logging.getLogger("amover-routes")
ALLOWED_ALGORITHMS = {
    "auto",
    "ortools_vrptw",
    "nearest_neighbor",
    "tabu_search",
    "grasp",
    "savings",
    "branch_and_bound",
}
AUTO_ALGORITHMS = [
    "ortools_vrptw",
    "nearest_neighbor",
    "tabu_search",
    "grasp",
    "savings",
    "branch_and_bound",
]
VALID_PRIORITIES = {"ALTA", "MEDIA", "BAIXA"}


class ValidationError(ValueError):
    """Erro de input do cliente -> resposta 400."""


def _parse_dt(value):
    if isinstance(value, datetime):
        dt = value
    else:
        s = str(value).strip()
        if s.endswith("Z"):
            s = s[:-1] + "+00:00"
        try:
            dt = datetime.fromisoformat(s)
        except ValueError:
            raise ValidationError(f"Data invalida: {value!r} (usa ISO 8601, ex. 2026-06-30T17:00:00Z)")
    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=timezone.utc)
    return dt.astimezone(timezone.utc)


def _num(value, name, *, minimum=None, allow_none=False):
    if value is None:
        if allow_none:
            return None
        raise ValidationError(f"Campo obrigatorio em falta: {name}")
    try:
        out = float(value)
    except (TypeError, ValueError):
        raise ValidationError(f"{name} tem de ser numerico (recebido: {value!r})")
    if minimum is not None and out < minimum:
        raise ValidationError(f"{name} tem de ser >= {minimum} (recebido: {out})")
    return out


def validate_request(data):
    """Valida e normaliza o corpo do pedido. Levanta ValidationError em caso de erro."""
    if not isinstance(data, dict):
        raise ValidationError("O corpo do pedido tem de ser um objeto JSON.")
    algorithm = str(data.get("algorithm") or "auto").strip()
    if algorithm not in ALLOWED_ALGORITHMS:
        raise ValidationError(
            f"Algoritmo invalido: {algorithm!r}. Validos: {sorted(ALLOWED_ALGORITHMS)}"
        )
    depot = data.get("depot") or {}
    if not isinstance(depot, dict):
        raise ValidationError("'depot' tem de ser um objeto com lat/lon.")
    depot_lat = _num(depot.get("lat"), "depot.lat")
    depot_lon = _num(depot.get("lon"), "depot.lon")
    vehicle_in = data.get("vehicle") or {}
    if not isinstance(vehicle_in, dict):
        raise ValidationError("'vehicle' tem de ser um objeto.")
    vehicle = {
        "capacity": _num(vehicle_in.get("capacity", 0), "vehicle.capacity", minimum=0),
        "battery_kwh": _num(vehicle_in.get("battery_kwh"), "vehicle.battery_kwh", minimum=0.0001),
        "current_charge_kwh": _num(
            vehicle_in.get("current_charge_kwh"), "vehicle.current_charge_kwh",
            minimum=0, allow_none=True,
        ),
        "speed_kph": _num(vehicle_in.get("speed_kph", 40), "vehicle.speed_kph", minimum=0.0001),
        "consumption_kwh_per_km": _num(
            vehicle_in.get("consumption_kwh_per_km", 0.05),
            "vehicle.consumption_kwh_per_km", minimum=0,
        ),
    }
    tasks_in = data.get("tasks")
    if not isinstance(tasks_in, list) or not tasks_in:
        raise ValidationError("'tasks' tem de ser uma lista nao vazia.")
    tasks = []
    seen_ids = set()
    for i, t in enumerate(tasks_in):
        if not isinstance(t, dict):
            raise ValidationError(f"tasks[{i}] tem de ser um objeto.")
        if t.get("id") is None:
            raise ValidationError(f"tasks[{i}].id e obrigatorio.")
        tid = int(t["id"])
        if tid in seen_ids:
            raise ValidationError(f"id de tarefa duplicado: {tid}")
        seen_ids.add(tid)
        priority = t.get("priority")
        if priority is not None and str(priority).strip().upper() not in VALID_PRIORITIES:
            raise ValidationError(
                f"tasks[{i}].priority invalida: {priority!r} (usa ALTA/MEDIA/BAIXA)"
            )
        deadline = t.get("deadline")
        if deadline is not None:
            _parse_dt(deadline)
        tasks.append({
            "id": tid,
            "lat": _num(t.get("lat"), f"tasks[{i}].lat"),
            "lon": _num(t.get("lon"), f"tasks[{i}].lon"),
            "demand": _num(t.get("demand", 0), f"tasks[{i}].demand", minimum=0),
            "priority": (str(priority).strip().upper() if priority is not None else None),
            "deadline": deadline,
            "service_minutes": _num(t.get("service_minutes", 1), f"tasks[{i}].service_minutes", minimum=0),
        })
    options_in = data.get("options") or {}
    if not isinstance(options_in, dict):
        raise ValidationError("'options' tem de ser um objeto.")
    route_start = options_in.get("route_start")
    route_start_utc = _parse_dt(route_start) if route_start else datetime.now(timezone.utc)
    time_limit_s = _num(options_in.get("time_limit_s", 5), "options.time_limit_s", minimum=1)
    time_limit_s = int(min(time_limit_s, 30))
    mode = str(options_in.get("mode") or "balanced").strip().lower()
    # Modo de coordenadas: geografico (default, fluxo real) ou cartesiano (teste).
    coordinates = str(options_in.get("coordinates") or "geographic").strip().lower()
    if coordinates not in ("geographic", "cartesian"):
        raise ValidationError("options.coordinates deve ser 'geographic' ou 'cartesian'.")
    return {
        "algorithm": algorithm,
        "depot_lat": depot_lat,
        "depot_lon": depot_lon,
        "vehicle": vehicle,
        "tasks": tasks,
        "route_start_utc": route_start_utc,
        "time_limit_s": time_limit_s,
        "mode": mode,
        "coordinates": coordinates,
    }


def _build_context(req):
    tasks = req["tasks"]
    depot_lat, depot_lon = req["depot_lat"], req["depot_lon"]
    task_ids = [t["id"] for t in tasks]
    task_meta = {
        t["id"]: {"lat": t["lat"], "lon": t["lon"], "priority": t["priority"], "deadline": t["deadline"]}
        for t in tasks
    }
    latlon = {t["id"]: (t["lat"], t["lon"]) for t in tasks}
    demand_by_id = {t["id"]: t["demand"] for t in tasks}
    service_by_id = {t["id"]: t["service_minutes"] for t in tasks}
    coords = [(depot_lon, depot_lat)] + [(t["lon"], t["lat"]) for t in tasks]
    demands = [0.0] + [t["demand"] for t in tasks]
    node_to_tid = {i + 1: t["id"] for i, t in enumerate(tasks)}
    mean_service = sum(t["service_minutes"] for t in tasks) / len(tasks)
    return {
        "task_ids": task_ids,
        "task_meta": task_meta,
        "latlon": latlon,
        "demand_by_id": demand_by_id,
        "service_by_id": service_by_id,
        "coords": coords,
        "demands": demands,
        "node_to_tid": node_to_tid,
        "depot_lat": depot_lat,
        "depot_lon": depot_lon,
        "mean_service": mean_service,
        "coordinates": req.get("coordinates", "geographic"),
    }


def _route_indices_to_ids(route_indices, node_to_tid):
    order, seen = [], set()
    for idx in route_indices:
        if idx == 0:
            continue
        tid = node_to_tid.get(idx)
        if tid is not None and tid not in seen:
            order.append(tid)
            seen.add(tid)
    return order


def _evaluate(order, ctx, req):
    """Calcula metricas + viabilidade de uma dada ordem de tarefas."""
    vehicle = req["vehicle"]
    depot_pt = (ctx["depot_lat"], ctx["depot_lon"])
    ordered_points = [depot_pt] + [ctx["latlon"][tid] for tid in order] + [depot_pt]
    metrics = geo.route_metrics(
        ordered_points,
        consumption_kwh_per_km=vehicle["consumption_kwh_per_km"],
        speed_kph=vehicle["speed_kph"],
        service_minutes=ctx["mean_service"],
        mode=ctx.get("coordinates", "geographic"),
    )
    load = sum(ctx["demand_by_id"][tid] for tid in order)
    available_energy = (
        vehicle["current_charge_kwh"]
        if vehicle["current_charge_kwh"] is not None
        else vehicle["battery_kwh"]
    )
    cap_ok = load <= vehicle["capacity"]
    energy_ok = metrics["total_energy_kwh"] <= available_energy
    feasible = cap_ok and energy_ok
    metrics["feasible"] = feasible
    return metrics, load


def _run_one(name, ctx, req):
    """Corre UM algoritmo e devolve um candidato (ou None se nao aplicavel)."""
    vehicle = req["vehicle"]
    consumption = vehicle["consumption_kwh_per_km"]
    time_limit = req["time_limit_s"]
    heur_time = min(2.0, float(time_limit))
    phase = None
    if name == "ortools_vrptw":
        from algorithms.ortools_vrptw import build_ortools_vrptw_route
        available = vehicle["current_charge_kwh"]
        order, dropped, phase = build_ortools_vrptw_route(
            ctx["task_ids"],
            ctx["task_meta"],
            ctx["depot_lat"],
            ctx["depot_lon"],
            speed_kph=vehicle["speed_kph"],
            route_start_utc=req["route_start_utc"],
            deadline_to_utc=_parse_dt,
            service_stop_minutes=ctx["mean_service"],
            time_limit_s=time_limit,
            consumption_kwh_per_km=consumption,
            available_energy_kwh=available,
            battery_capacity_kwh=vehicle["battery_kwh"],
            use_energy_weighting=available is not None,
        )
    elif name == "nearest_neighbor":
        idx = heuristics.nearest_neighbor(ctx["coords"], ctx["demands"], vehicle, consumption)
        order = _route_indices_to_ids(idx, ctx["node_to_tid"])
    elif name == "tabu_search":
        idx = heuristics.tabu_search(ctx["coords"], ctx["demands"], vehicle, consumption, time_limit=heur_time)
        order = _route_indices_to_ids(idx, ctx["node_to_tid"])
    elif name == "grasp":
        idx = heuristics.grasp(ctx["coords"], vehicle, time_limit=heur_time)
        order = _route_indices_to_ids(idx, ctx["node_to_tid"])
    elif name == "savings":
        idx = heuristics.savings(ctx["coords"], ctx["demands"], vehicle, consumption)
        order = _route_indices_to_ids(idx, ctx["node_to_tid"])
    elif name == "branch_and_bound":
        idx = heuristics.branch_and_bound(ctx["coords"])
        if idx is None:
            return None
        order = _route_indices_to_ids(idx, ctx["node_to_tid"])
    else:
        return None
    all_ids = set(ctx["task_ids"])
    dropped = [tid for tid in ctx["task_ids"] if tid not in set(order)]
    metrics, _ = _evaluate(order, ctx, req)
    return {
        "algorithm": name,
        "order": order,
        "dropped": dropped,
        "phase": phase,
        "metrics": metrics,
        "complete": len(order) == len(all_ids),
    }


def _pick_best(candidates):
    """Melhor = completo e viavel primeiro, depois menor distancia."""
    def key(c):
        return (
            not c["complete"],
            not c["metrics"]["feasible"],
            c["metrics"]["total_distance_km"],
        )
    return min(candidates, key=key)


def _select(ctx, req):
    """Escolhe o algoritmo (ou o melhor, em modo auto) e devolve o candidato."""
    if req["algorithm"] == "auto":
        candidates = []
        for n in AUTO_ALGORITHMS:
            try:
                c = _run_one(n, ctx, req)
            except Exception:
                log.warning("Algoritmo %s falhou em modo auto", n, exc_info=True)
                continue
            if c:
                candidates.append(c)
        if not candidates:
            raise ValidationError("Nenhum algoritmo produziu uma rota.")
        return _pick_best(candidates)
    chosen = _run_one(req["algorithm"], ctx, req)
    if chosen is None:
        raise ValidationError(
            f"O algoritmo {req['algorithm']!r} nao e aplicavel a esta instancia "
            f"(ex.: branch_and_bound so suporta instancias pequenas)."
        )
    return chosen


def _normalize_legacy(data):
    nodes = data.get("nodes")
    if not isinstance(nodes, list) or not nodes:
        raise ValidationError("'nodes' tem de ser uma lista nao vazia.")
    vehicles = data.get("vehicles") or [{}]
    if not isinstance(vehicles, list) or not vehicles or not isinstance(vehicles[0], dict):
        raise ValidationError("'vehicles' tem de ser uma lista com pelo menos um objeto.")
    v0 = vehicles[0]
    depot = next((n for n in nodes if isinstance(n, dict) and n.get("is_depot") is True), None)
    if depot is None:
        depot = next((n for n in nodes if isinstance(n, dict) and n.get("id") == 0), None)
    if depot is None:
        depot = nodes[0]
    depot_id = depot.get("id", 0)
    algorithm = str(data.get("algorithm") or data.get("algoritmo") or "auto").strip()
    if algorithm not in ALLOWED_ALGORITHMS:
        raise ValidationError(
            f"Algoritmo invalido: {algorithm!r}. Validos: {sorted(ALLOWED_ALGORITHMS)}"
        )
    tasks, seen = [], set()
    for i, n in enumerate(nodes):
        if n is depot or not isinstance(n, dict):
            continue
        if n.get("id") is None:
            raise ValidationError(f"nodes[{i}].id e obrigatorio.")
        tid = int(n["id"])
        if tid == depot_id or tid in seen:
            continue
        seen.add(tid)
        deadline = n.get("janela_fim") or None
        if deadline:
            try:
                _parse_dt(deadline)
            except ValidationError:
                deadline = None
        tasks.append({
            "id": tid,
            "lat": _num(n.get("x"), f"nodes[{i}].x"),
            "lon": _num(n.get("y"), f"nodes[{i}].y"),
            "demand": _num(n.get("demand", 0), f"nodes[{i}].demand", minimum=0),
            "priority": None,
            "deadline": deadline,
            "service_minutes": 1.0,
        })
    if not tasks:
        raise ValidationError("Nenhuma tarefa (alem do deposito) em 'nodes'.")
    vehicle = {
        "capacity": _num(v0.get("capacity", 1e9), "vehicles[0].capacity", minimum=0),
        "battery_kwh": _num(v0.get("battery_kwh", 1e9), "vehicles[0].battery_kwh", minimum=0.0001),
        "current_charge_kwh": _num(
            v0.get("current_charge_kwh"), "vehicles[0].current_charge_kwh",
            minimum=0, allow_none=True,
        ),
        "speed_kph": _num(v0.get("speed_kph", 40), "vehicles[0].speed_kph", minimum=0.0001),
        "consumption_kwh_per_km": _num(
            v0.get("consumption_kwh_per_km", 0.05),
            "vehicles[0].consumption_kwh_per_km", minimum=0,
        ),
    }
    return {
        "algorithm": algorithm,
        "depot_lat": _num(depot.get("x"), "depot.x"),
        "depot_lon": _num(depot.get("y"), "depot.y"),
        "depot_id": depot_id,
        "vehicle": vehicle,
        "tasks": tasks,
        "route_start_utc": datetime.now(timezone.utc),
        "time_limit_s": int(_num(data.get("time_limit_s", 5), "time_limit_s", minimum=1)),
        "mode": "balanced",
        "coordinates": "cartesian",
    }


def _run_legacy(data):
    req = _normalize_legacy(data)
    ctx = _build_context(req)
    chosen = _select(ctx, req)
    depot_id = req["depot_id"]
    route_ids = [depot_id] + list(chosen["order"]) + [depot_id]
    return {
        "route": route_ids,
        "distance_km": round(chosen["metrics"]["total_distance_km"], 2),
        "energy_kwh": round(chosen["metrics"]["total_energy_kwh"], 2),
        "algorithm_used": chosen["algorithm"],
    }


def run_optimization(data):
    """Aceita o formato novo (depot/tasks) e o legado (nodes/vehicles)."""
    if isinstance(data, dict) and data.get("nodes") is not None and data.get("tasks") is None:
        return _run_legacy(data)
    req = validate_request(data)
    ctx = _build_context(req)
    chosen = _select(ctx, req)
    route = [
        {"sequence": i + 1, "task_id": tid, "lat": ctx["latlon"][tid][0], "lon": ctx["latlon"][tid][1]}
        for i, tid in enumerate(chosen["order"])
    ]
    return {
        "ok": True,
        "algorithm_used": chosen["algorithm"],
        "phase": chosen["phase"],
        "route": route,
        "dropped_task_ids": chosen["dropped"],
        "metrics": chosen["metrics"],
    }
