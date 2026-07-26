"""
ortools_vrptw.py — Motor principal (Google OR-Tools).

VRP com janelas de tempo (deadlines), prioridades e energia. Copiado do
projeto original (API_Amover_fixed) — já era uma função importável, por isso
mantém-se praticamente intacto.

build_ortools_vrptw_route(...) -> (route_tasks, dropped_task_ids, phase)
  phase = "hard"  -> todas as ALTA cumprem a deadline
  phase = "soft"  -> impossível cumprir todas; atrasos minimizados
"""

import math

from ortools.constraint_solver import pywrapcp, routing_enums_pb2

ROAD_CIRCUITY_FACTOR = 1.3

# Penalidades de atraso por MINUTO (convertidas para segundos internamente).
LATE_PENALTY_PER_MIN = {
    "ALTA": 100_000,
    "MÉDIA": 600,
    "BAIXA": 180,
}

# Penalidade por DROPAR uma tarefa (disjunção).
DROP_PENALTY = {
    "ALTA": 10**9,
    "MÉDIA": 500_000,
    "BAIXA": 200_000,
}


def _normalize_priority(priority):
    p = str(priority or "").strip().upper()
    if p == "ALTA":
        return "ALTA"
    if p in ("MÉDIA", "MEDIA"):
        return "MÉDIA"
    return "BAIXA"


def _haversine_km(lat1, lon1, lat2, lon2):
    R = 6371.0
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlmb = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlmb / 2) ** 2
    return 2 * R * math.asin(math.sqrt(a))


ENERGY_WEIGHT_CUTOFF_PCT = 70.0
ENERGY_WEIGHT_MAX = 10.0


def _energy_weight_for_charge(current_charge_kwh, battery_capacity_kwh):
    if current_charge_kwh is None or not battery_capacity_kwh:
        return 0.0
    pct = (float(current_charge_kwh) / float(battery_capacity_kwh)) * 100.0
    if pct >= ENERGY_WEIGHT_CUTOFF_PCT:
        return 0.0
    fraction_below = (ENERGY_WEIGHT_CUTOFF_PCT - pct) / ENERGY_WEIGHT_CUTOFF_PCT
    return ENERGY_WEIGHT_MAX * fraction_below


def build_ortools_vrptw_route(
    task_ids,
    task_meta,
    depot_lat,
    depot_lon,
    speed_kph,
    route_start_utc,
    deadline_to_utc,
    service_stop_minutes=1.0,
    time_limit_s=5,
    horizon_hours=24,
    consumption_kwh_per_km=0.05,
    available_energy_kwh=None,
    energy_safety_margin=0.10,
    battery_capacity_kwh=None,
    use_energy_weighting=False,
):
    task_ids = [int(t) for t in task_ids if int(t) in task_meta]
    if not task_ids:
        return [], [], "hard"

    locations = [(float(depot_lat), float(depot_lon))]
    node_to_tid = {}
    for idx, tid in enumerate(task_ids, start=1):
        m = task_meta[tid]
        locations.append((float(m["lat"]), float(m["lon"])))
        node_to_tid[idx] = tid

    n = len(locations)
    service_sec = int(service_stop_minutes * 60)

    time_matrix = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if i == j:
                continue
            km = _haversine_km(*locations[i], *locations[j]) * ROAD_CIRCUITY_FACTOR
            sec = int((km / speed_kph) * 3600) if speed_kph > 0 else 0
            time_matrix[i][j] = max(1, sec)

    energy_matrix = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if i == j:
                continue
            km = _haversine_km(*locations[i], *locations[j]) * ROAD_CIRCUITY_FACTOR
            wh = int(km * consumption_kwh_per_km * 1000)
            energy_matrix[i][j] = max(0, wh)

    deadline_sec = {}
    for node, tid in node_to_tid.items():
        dl = task_meta[tid].get("deadline")
        if dl is None:
            continue
        dl_utc = deadline_to_utc(dl)
        deadline_sec[node] = int((dl_utc - route_start_utc).total_seconds())

    def _solve(hard_alta):
        manager = pywrapcp.RoutingIndexManager(n, 1, 0)
        routing = pywrapcp.RoutingModel(manager)

        def time_cb(fi, ti):
            f = manager.IndexToNode(fi)
            t = manager.IndexToNode(ti)
            svc = service_sec if f != 0 else 0
            return time_matrix[f][t] + svc

        cb = routing.RegisterTransitCallback(time_cb)

        if use_energy_weighting:
            energy_weight = _energy_weight_for_charge(
                available_energy_kwh, battery_capacity_kwh
            )
        else:
            energy_weight = 0.0

        if energy_weight > 0:
            def cost_cb(fi, ti):
                f = manager.IndexToNode(fi)
                t = manager.IndexToNode(ti)
                svc = service_sec if f != 0 else 0
                time_part = time_matrix[f][t] + svc
                energy_part = int(energy_matrix[f][t] * energy_weight)
                return time_part + energy_part

            cost_evaluator = routing.RegisterTransitCallback(cost_cb)
            routing.SetArcCostEvaluatorOfAllVehicles(cost_evaluator)
        else:
            routing.SetArcCostEvaluatorOfAllVehicles(cb)

        horizon = horizon_hours * 3600
        routing.AddDimension(cb, 0, horizon, True, "Time")
        tdim = routing.GetDimensionOrDie("Time")

        if available_energy_kwh is not None:
            def energy_cb(fi, ti):
                f = manager.IndexToNode(fi)
                t = manager.IndexToNode(ti)
                return energy_matrix[f][t]

            ecb = routing.RegisterTransitCallback(energy_cb)
            usable_kwh = available_energy_kwh * (1.0 - energy_safety_margin)
            energy_budget_wh = max(0, int(usable_kwh * 1000))
            routing.AddDimension(ecb, 0, energy_budget_wh, True, "Energy")

        infeasible_hard = False

        for node, tid in node_to_tid.items():
            prio = _normalize_priority(task_meta[tid].get("priority"))
            index = manager.NodeToIndex(node)
            routing.AddDisjunction([index], DROP_PENALTY[prio])

            if node not in deadline_sec:
                continue

            limit = deadline_sec[node]
            if hard_alta and prio == "ALTA":
                if limit <= 0:
                    infeasible_hard = True
                    break
                tdim.CumulVar(index).SetMax(limit)
            else:
                per_sec = max(1, LATE_PENALTY_PER_MIN[prio] // 60)
                tdim.SetCumulVarSoftUpperBound(index, max(0, limit), per_sec)

        if infeasible_hard:
            return None

        params = pywrapcp.DefaultRoutingSearchParameters()
        params.first_solution_strategy = (
            routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC
        )
        params.local_search_metaheuristic = (
            routing_enums_pb2.LocalSearchMetaheuristic.GUIDED_LOCAL_SEARCH
        )
        params.time_limit.seconds = int(max(1, time_limit_s))

        solution = routing.SolveWithParameters(params)
        if solution is None:
            return None

        route, dropped = [], []
        index = routing.Start(0)
        visited = set()
        while not routing.IsEnd(index):
            node = manager.IndexToNode(index)
            if node != 0:
                route.append(node_to_tid[node])
                visited.add(node)
            index = solution.Value(routing.NextVar(index))

        for node, tid in node_to_tid.items():
            if node not in visited:
                dropped.append(tid)

        return route, dropped

    result = _solve(hard_alta=True)
    if result is not None:
        route, dropped = result
        dropped_alta = [
            t for t in dropped
            if _normalize_priority(task_meta[t].get("priority")) == "ALTA"
        ]
        if not dropped_alta:
            return route, dropped, "hard"

    result = _solve(hard_alta=False)
    if result is None:
        return [], [], "soft"

    route, dropped = result
    return route, dropped, "soft"
