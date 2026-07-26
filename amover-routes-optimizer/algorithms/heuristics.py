"""
heuristics.py — Heurísticas de roteamento (refatoradas do projeto original).

Cada algoritmo do API_Amover_fixed era um script CLI que lia JSON e fazia
`print` do resultado. Aqui passam a ser funções puras e importáveis: recebem
coordenadas + restrições e DEVOLVEM a ordem (lista de índices de nós), sem
imprimir nada e sem subprocessos.

Convenções:
- `coords`: lista de (x, y) = (lon, lat). O índice 0 é sempre o depósito.
- `demands`: lista alinhada com `coords` (procura de cada nó; depósito = 0).
- A ordem devolvida é uma lista de índices [0, ..., 0] (circular no depósito).

A distância usada aqui é euclidiana no plano (lon, lat) — serve para *ordenar*
as paragens. As métricas reais (km/kWh/min) são recalculadas por Haversine no
orquestrador (ver algorithms/geo.py).
"""

import math
import time
import random
from itertools import combinations, permutations


# --------------------------------------------------------------------------- #
# Helpers partilhados
# --------------------------------------------------------------------------- #
def _euclid_matrix(coords):
    return [[math.hypot(a[0] - b[0], a[1] - b[1]) for b in coords] for a in coords]


def _route_cost(route, mat):
    return sum(mat[route[k]][route[k + 1]] for k in range(len(route) - 1))


def _two_opt(route, mat):
    """Busca local 2-opt: inverte segmentos enquanto melhorar."""
    improved = True
    while improved:
        improved = False
        for i in range(1, len(route) - 2):
            for j in range(i + 1, len(route) - 1):
                if j - i == 1:
                    continue
                delta = (mat[route[i - 1]][route[j]] + mat[route[i]][route[j + 1]]
                         - mat[route[i - 1]][route[i]] - mat[route[j]][route[j + 1]])
                if delta < -1e-6:
                    route[i:j + 1] = list(reversed(route[i:j + 1]))
                    improved = True
    return route


# --------------------------------------------------------------------------- #
# 1. Nearest Neighbor (+ 2-opt)
# --------------------------------------------------------------------------- #
def nearest_neighbor(coords, demands, vehicle, consumption, two_opt=True):
    n = len(coords)
    depot = 0
    mat = _euclid_matrix(coords)
    unvisited = set(range(1, n))
    cap = vehicle.get("capacity", math.inf)
    battery = vehicle.get("battery_kwh", math.inf)

    route = [depot]
    load = 0.0
    energy = 0.0
    cur = depot

    while unvisited:
        nxt = min(unvisited, key=lambda j: mat[cur][j])
        next_demand = demands[nxt]
        next_energy = mat[cur][nxt] * consumption
        if load + next_demand > cap:
            break
        if energy + next_energy > battery:
            break
        route.append(nxt)
        load += next_demand
        energy += next_energy
        cur = nxt
        unvisited.discard(nxt)

    route.append(depot)
    if two_opt and len(route) > 3:
        route = _two_opt(route, mat)
    return route


# --------------------------------------------------------------------------- #
# 2. Tabu Search
# --------------------------------------------------------------------------- #
def tabu_search(coords, demands, vehicle, consumption, time_limit=2.0, tabu_tenure=10):
    n = len(coords)
    depot = 0
    mat = _euclid_matrix(coords)
    clients = list(range(1, n))
    if not clients:
        return [depot, depot]

    best_route = [depot] + clients + [depot]
    best_cost = _route_cost(best_route, mat)
    current = best_route[:]
    tabu = []
    battery = vehicle.get("battery_kwh", math.inf)

    start = time.time()
    while time.time() - start < time_limit:
        neighbors = []
        for i in range(1, len(current) - 2):
            for j in range(i + 1, len(current) - 1):
                if (i, j) in tabu:
                    continue
                neighbor = current[:]
                neighbor[i], neighbor[j] = neighbor[j], neighbor[i]
                cost = _route_cost(neighbor, mat)
                if cost * consumption <= battery:
                    neighbors.append((cost, neighbor, (i, j)))

        if not neighbors:
            break

        neighbors.sort(key=lambda t: t[0])
        best_neighbor_cost, best_neighbor_route, move = neighbors[0]
        if best_neighbor_cost < best_cost:
            best_cost = best_neighbor_cost
            best_route = best_neighbor_route
        current = best_neighbor_route
        tabu.append(move)
        if len(tabu) > tabu_tenure:
            tabu.pop(0)

    return best_route


# --------------------------------------------------------------------------- #
# 3. GRASP (construção com RCL + 2-opt)
# --------------------------------------------------------------------------- #
def _grasp_build(coords, mat, alpha):
    depot = 0
    unvisited = set(range(1, len(coords)))
    route = [depot]
    while unvisited:
        dists = sorted((mat[route[-1]][j], j) for j in unvisited)
        if not dists:
            break
        max_idx = max(1, int(alpha * len(dists)))
        _, chosen = random.choice(dists[:max_idx])
        route.append(chosen)
        unvisited.discard(chosen)
    route.append(depot)
    return route


def grasp(coords, vehicle, alpha=0.3, iters=300, time_limit=2.0):
    mat = _euclid_matrix(coords)
    best = None
    best_cost = math.inf
    start = time.time()
    for _ in range(iters):
        if time.time() - start > time_limit:
            break
        r = _grasp_build(coords, mat, alpha)
        r = _two_opt(r, mat)
        c = _route_cost(r, mat)
        if c < best_cost:
            best_cost = c
            best = r
    return best if best else [0, 0]


# --------------------------------------------------------------------------- #
# 4. Clarke & Wright Savings (depósito único)
# --------------------------------------------------------------------------- #
def savings(coords, demands, vehicle, consumption):
    n = len(coords)
    depot = 0
    mat = _euclid_matrix(coords)
    clients = [i for i in range(1, n)]
    if not clients:
        return [depot, depot]

    routes = {i: [depot, i, depot] for i in clients}
    loads = {i: demands[i] for i in clients}
    cap = vehicle.get("capacity", math.inf)
    battery = vehicle.get("battery_kwh", math.inf)

    savings_list = [
        (mat[i][depot] + mat[depot][j] - mat[i][j], i, j)
        for i, j in combinations(clients, 2)
    ]
    savings_list.sort(key=lambda t: t[0], reverse=True)

    for _, i, j in savings_list:
        try:
            ri = next(r for r in routes.values() if r[1] == i or r[-2] == i)
            rj = next(r for r in routes.values() if r[1] == j or r[-2] == j)
        except StopIteration:
            continue
        if ri is rj:
            continue
        if ri[-2] == i and rj[1] == j:
            new_r = ri[:-1] + rj[1:]
            new_load = loads[ri[1]] + loads[rj[1]]
            if new_load > cap or _route_cost(new_r, mat) * consumption > battery:
                continue
            routes[ri[1]] = new_r
            loads[ri[1]] = new_load
            routes.pop(rj[1])
            loads.pop(rj[1])

    # Achata as (possíveis) várias sub-rotas numa única sequência circular.
    seq = [depot]
    for r in routes.values():
        seq.extend(r[1:-1])
    seq.append(depot)
    return seq


# --------------------------------------------------------------------------- #
# 5. Branch & Bound (exato, só para instâncias pequenas)
# --------------------------------------------------------------------------- #
def branch_and_bound(coords, max_clients=9):
    n = len(coords)
    depot = 0
    clients = list(range(1, n))
    if not clients:
        return [depot, depot]
    if len(clients) > max_clients:
        return None  # explode combinatorialmente; o orquestrador ignora.

    mat = _euclid_matrix(coords)
    best_cost = math.inf
    best_route = None
    for perm in permutations(clients):
        route = [depot] + list(perm) + [depot]
        cost = _route_cost(route, mat)
        if cost < best_cost:
            best_cost = cost
            best_route = route
    return best_route
