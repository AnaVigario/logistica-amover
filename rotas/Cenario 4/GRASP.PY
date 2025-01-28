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

# GRASP Algorithm

def grasp(depot, clients, locations, vehicles, alpha=0.3, max_iterations=100):
    capacity = {vehicle: vehicles[vehicle]["capacity"] for vehicle in vehicles}
    battery = {vehicle: vehicles[vehicle]["battery"] for vehicle in vehicles}

    best_routes = []
    best_total_distance = float('inf')

    for _ in range(max_iterations):
        current_routes = []
        total_distance = 0
        remaining_clients = clients[:]

        for vehicle in vehicles:
            if not remaining_clients:
                break

            current_capacity = capacity[vehicle]
            current_battery = battery[vehicle]
            route = [depot]

            while remaining_clients:
                # Construir lista restrita de candidatos (RCL)
                candidates = []
                for client in remaining_clients:
                    distance = calculate_distance(locations[route[-1]], locations[client])
                    if demands[client] <= current_capacity and distance <= current_battery:
                        candidates.append((client, distance))

                if not candidates:
                    break

                candidates.sort(key=lambda x: x[1])
                rcl = candidates[:max(1, int(len(candidates) * alpha))]
                next_client, distance_to_client = random.choice(rcl)

                # Atualizar rota e estado do veículo
                route.append(next_client)
                current_capacity -= demands[next_client]
                current_battery -= distance_to_client
                total_distance += distance_to_client
                remaining_clients.remove(next_client)

            # Retornar ao depósito
            if route[-1] != depot:
                distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
                route.append(depot)
                total_distance += distance_to_depot

            current_routes.append((vehicle, route))

        if total_distance < best_total_distance:
            best_routes = current_routes
            best_total_distance = total_distance

    return best_routes, best_total_distance

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
    routes, total_distance = grasp(depot, assigned_clients, locations, depot_vehicles)
    final_routes[depot] = (routes, total_distance)

# Exibir resultados
for depot in depots:
    print(f"Depósito: {depot}")
    routes, total_distance = final_routes[depot]
    for vehicle, route in routes:
        print(f"Veículo: {vehicle}")
        print(f"Rota Final: {' -> '.join(route)}")
    print(f"Distância Total para {depot}: {total_distance:.2f} km\n")
