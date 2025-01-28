import numpy as np
import random

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

# Branch and Bound Algorithm for Multiple Deposits
def branch_and_bound(depot, clients, locations, vehicles):
    capacity = {vehicle: vehicles[vehicle]["capacity"] for vehicle in vehicles}
    battery = {vehicle: vehicles[vehicle]["battery"] for vehicle in vehicles}

    best_routes = []
    best_total_distance = float('inf')

    def bnb_recursive(route, remaining_clients, current_capacity, current_battery, total_distance):
        nonlocal best_routes, best_total_distance

        # Se todos os clientes foram atendidos
        if not remaining_clients:
            # Retornar ao depósito
            distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
            route.append(depot)
            total_distance += distance_to_depot

            if total_distance < best_total_distance:
                best_routes = [(route.copy(), total_distance)]
                best_total_distance = total_distance
            return

        for client in remaining_clients:
            distance_to_client = calculate_distance(locations[route[-1]], locations[client])

            if demands[client] <= current_capacity and distance_to_client <= current_battery:
                # Explorar rota
                route.append(client)
                new_remaining_clients = remaining_clients[:]
                new_remaining_clients.remove(client)
                bnb_recursive(
                    route,
                    new_remaining_clients,
                    current_capacity - demands[client],
                    current_battery - distance_to_client,
                    total_distance + distance_to_client
                )
                route.pop()

    for vehicle in vehicles:
        if not clients:
            break

        current_capacity = capacity[vehicle]
        current_battery = battery[vehicle]
        for client in clients:
            # Começar rota com cada cliente
            route = [depot]
            bnb_recursive(
                route + [client],
                [c for c in clients if c != client],
                current_capacity - demands[client],
                current_battery - calculate_distance(locations[depot], locations[client]),
                calculate_distance(locations[depot], locations[client])
            )

    return best_routes

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
    routes = branch_and_bound(depot, assigned_clients, locations, depot_vehicles)
    final_routes[depot] = routes

# Exibir resultados
for depot in depots:
    print(f"Depósito: {depot}")
    for route, total_distance in final_routes[depot]:
        print(f"Rota Final: {' -> '.join(route)}")
        print(f"Distância Total: {total_distance:.2f} km\n")
