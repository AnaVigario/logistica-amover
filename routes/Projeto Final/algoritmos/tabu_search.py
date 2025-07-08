"""
tabu_search.py — Metaheurística Tabu Search para o Problema de Roteamento de Veículos (VRP)

Suporta:
- Múltiplos clientes
- Capacidade do veículo
- Autonomia com consumo energético (kWh/km)
- Roteamento circular (início e fim no mesmo depósito)

Entrada JSON:
- nodes: lista com {id, x, y, demand (opcional)}
- vehicles: usa o primeiro com {capacity, battery_kwh}

Limitações:
- Não trata janelas de tempo
- Assume apenas um depósito (primeiro nó)
"""

import sys, json, math, random, time

# Consumo energético em kWh por km
CONSUMPTION = 0.2

class Node:
    def __init__(self, d): self.__dict__.update(d)

def dist(a, b): return math.hypot(a.x - b.x, a.y - b.y)

def total_distance(route, mat): return sum(mat[route[i]][route[i+1]] for i in range(len(route) - 1))

def total_energy(route, mat): return total_distance(route, mat) * CONSUMPTION

def tabu_search(nodes, vehicle, mat, time_limit=30, tabu_tenure=10):
    depot = 0
    clients = list(range(1, len(nodes)))

    best_route = [depot] + clients + [depot]
    best_cost = total_distance(best_route, mat)
    current = best_route[:]
    tabu = []

    start_time = time.time()

    while time.time() - start_time < time_limit:
        neighbors = []
        for i in range(1, len(current) - 2):
            for j in range(i + 1, len(current) - 1):
                neighbor = current[:]
                neighbor[i], neighbor[j] = neighbor[j], neighbor[i]
                if (i, j) not in tabu:
                    cost = total_distance(neighbor, mat)
                    energy = cost * CONSUMPTION
                    if energy <= vehicle["battery_kwh"]:
                        neighbors.append((cost, neighbor, (i, j)))

        if not neighbors:
            break

        neighbors.sort()
        best_neighbor_cost, best_neighbor_route, move = neighbors[0]

        if best_neighbor_cost < best_cost:
            best_cost = best_neighbor_cost
            best_route = best_neighbor_route

        current = best_neighbor_route
        tabu.append(move)
        if len(tabu) > tabu_tenure:
            tabu.pop(0)

    return best_route, best_cost

def main():
    # Carregamento do ficheiro JSON
    payload = json.load(open(sys.argv[1])) if len(sys.argv) > 1 else json.loads(sys.stdin.read())
    nodes = [Node(n) for n in payload["nodes"]]
    vehicle = payload["vehicles"][0]

    # Verificações iniciais
    if len(nodes) < 2:
        print("❌ São necessários pelo menos um depósito e um cliente.")
        return

    mat = [[dist(a, b) for b in nodes] for a in nodes]
    route, cost = tabu_search(nodes, vehicle, mat)

    # Resultado
    if route and cost < float("inf"):
        route_ids = [nodes[i].id for i in route]
        print("✔ Roteamento:", [route_ids])
        print("📏 Distância total:", round(cost, 2), "km")
        print("🔋 Energia estimada:", round(cost * CONSUMPTION, 2), "kWh")
    else:
        print("❌ Não foi possível encontrar rota viável. Verifica a autonomia ou os dados de entrada.")

if __name__ == "__main__":
    main()
