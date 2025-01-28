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

# Nearest Neighbor Algorithm para otimizar rotas
def nearest_neighbor(depot, clients, locations, vehicles):
    capacity = {vehicle: vehicles[vehicle]["capacity"] for vehicle in vehicles}
    battery = {vehicle: vehicles[vehicle]["battery"] for vehicle in vehicles}

    final_routes = []

    while clients:
        for vehicle in vehicles:
            if not clients:
                break

            current_capacity = capacity[vehicle]
            current_battery = battery[vehicle]
            route = [depot]
            total_distance = 0
            remaining_clients = clients[:]

            while remaining_clients:
                closest_client = None
                closest_distance = float('inf')

                for client in remaining_clients:
                    distance = calculate_distance(locations[route[-1]], locations[client])
                    if distance < closest_distance:
                        closest_client = client
                        closest_distance = distance

                if closest_client is None:
                    break

                if demands[closest_client] > current_capacity or closest_distance > current_battery:
                    break

                # Adicionar cliente à rota
                route.append(closest_client)
                total_distance += closest_distance
                current_capacity -= demands[closest_client]
                current_battery -= closest_distance
                remaining_clients.remove(closest_client)
                clients.remove(closest_client)

            # Retornar ao depósito
            if route[-1] != depot:
                distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
                route.append(depot)
                total_distance += distance_to_depot

            final_routes.append((vehicle, route, total_distance))

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
    routes = nearest_neighbor(depot, assigned_clients, locations, depot_vehicles)
    final_routes[depot] = routes

# Exibir resultados
for depot in depots:
    print(f"Depósito: {depot}")
    for vehicle, route, total_distance in final_routes[depot]:
        print(f"Veículo: {vehicle}")
        print(f"Rota Final: {' -> '.join(route)}")
        print(f"Distância Total: {total_distance:.2f} km\n")

