import numpy as np

# Dados do problema
locations = {
    "Depósito 1": (1, 1),
    "Depósito 2": (8, 8),
    "A": (3, 5),
    "B": (7, 9),
    "C": (6, 2),
    "D": (2, 8)
}

demands = {
    "A": 6,
    "B": 4,
    "C": 3,
    "D": 5
}

vehicles = {
    "Moto1": {"capacity": 10, "battery": 30},
    "Moto2": {"capacity": 15, "battery": 25},
    "Moto3": {"capacity": 10, "battery": 35},
    "Moto4": {"capacity": 15, "battery": 20}
}

# Atribuir clientes ao depósito mais próximo
def assign_clients_to_depots(locations, clients, depots):
    assignments = {depot: [] for depot in depots}
    for client in clients:
        closest_depot = min(
            depots,
            key=lambda depot: np.sqrt(
                (locations[client][0] - locations[depot][0]) ** 2 +
                (locations[client][1] - locations[depot][1]) ** 2
            )
        )
        assignments[closest_depot].append(client)
    return assignments

def calculate_distance(coord1, coord2):
    return np.sqrt((coord1[0] - coord2[0])**2 + (coord1[1] - coord2[1])**2)

# Savings Algorithm para otimizar rotas
def savings_algorithm(depot, clients, locations, vehicles):
    capacity = {vehicle: vehicles[vehicle]["capacity"] for vehicle in vehicles}
    battery = {vehicle: vehicles[vehicle]["battery"] for vehicle in vehicles}

    # Inicializar rotas individuais para cada cliente
    routes = {client: [depot, client, depot] for client in clients}
    route_distances = {
        client: 2 * calculate_distance(locations[depot], locations[client])
        for client in clients
    }

    # Calcular economias
    savings = []
    for i in clients:
        for j in clients:
            if i != j:
                saving = (
                    calculate_distance(locations[depot], locations[i]) +
                    calculate_distance(locations[depot], locations[j]) -
                    calculate_distance(locations[i], locations[j])
                )
                savings.append((saving, i, j))

    # Ordenar economias em ordem decrescente
    savings.sort(reverse=True, key=lambda x: x[0])

    # Combinar rotas com base nas economias
    for saving, i, j in savings:
        if i in routes and j in routes and i != j:
            route_i = routes[i]
            route_j = routes[j]

            # Verificar capacidade
            demand_i = sum(demands[client] for client in route_i if client != depot)
            demand_j = sum(demands[client] for client in route_j if client != depot)

            if demand_i + demand_j <= max(capacity.values()):
                # Combinar rotas
                if route_i[-2] == i and route_j[1] == j:
                    new_route = route_i[:-1] + route_j[1:]
                    new_distance = (
                        route_distances[i] + route_distances[j] -
                        calculate_distance(locations[i], locations[j])
                    )

                    # Atualizar rotas e distâncias
                    routes[i] = new_route
                    route_distances[i] = new_distance
                    del routes[j]
                    del route_distances[j]

    # Atribuir rotas aos veículos
    final_routes = []
    for route in routes.values():
        for vehicle in vehicles:
            if capacity[vehicle] >= sum(demands[client] for client in route if client != depot):
                final_routes.append((vehicle, route))
                break

    return final_routes

# Resolver o problema
clients = list(demands.keys())
depots = ["Depósito 1", "Depósito 2"]
assignments = assign_clients_to_depots(locations, clients, depots)

final_routes = {}

for depot, assigned_clients in assignments.items():
    depot_vehicles = (
        {"Moto1": vehicles["Moto1"], "Moto2": vehicles["Moto2"]}
        if depot == "Depósito 1" else
        {"Moto3": vehicles["Moto3"], "Moto4": vehicles["Moto4"]}
    )
    routes = savings_algorithm(depot, assigned_clients, locations, depot_vehicles)
    final_routes[depot] = routes

# Exibir resultados
for depot in depots:
    print(f"Depósito: {depot}")
    for vehicle, route in final_routes[depot]:
        print(f"Veículo: {vehicle}")
        print(f"Rota Final: {' -> '.join(route)}")

    total_distance = sum(
        calculate_distance(locations[route[i]], locations[route[i + 1]])
        for _, route in final_routes[depot]
        for i in range(len(route) - 1)
    )
    print(f"Distância Total para {depot}: {total_distance:.2f} km\n")
