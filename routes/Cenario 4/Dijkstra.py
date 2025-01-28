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

# Implementação do Dijkstra para um depósito
def dijkstra_for_depot(depot, clients, locations, vehicle):
    capacity = vehicle["capacity"]
    battery = vehicle["battery"]

    current_battery = battery
    current_capacity = capacity
    route = [depot]
    total_distance = 0

    while clients:
        closest_client = None
        closest_distance = float('inf')

        for client in clients:
            distance = calculate_distance(locations[route[-1]], locations[client])
            if distance < closest_distance:
                closest_client = client
                closest_distance = distance

        if closest_client is None:
            break

        # Verificar capacidade e bateria
        if demands[closest_client] > current_capacity:
            # Retornar ao depósito para descarregar
            distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
            route.append(depot)
            total_distance += distance_to_depot
            current_capacity = capacity
            current_battery -= distance_to_depot
            continue

        if closest_distance > current_battery:
            # Retornar ao depósito para recarregar
            distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
            route.append(depot)
            total_distance += distance_to_depot
            current_battery = battery
            continue

        # Atender cliente
        route.append(closest_client)
        total_distance += closest_distance
        current_battery -= closest_distance
        current_capacity -= demands[closest_client]
        clients.remove(closest_client)

    # Retornar ao depósito
    if route[-1] != depot:
        distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
        route.append(depot)
        total_distance += distance_to_depot

    return route, total_distance

# Resolver o problema
clients = list(demands.keys())
depots = ["Depósito 1", "Depósito 2"]
assignments = assign_clients_to_depots(locations, clients, depots)

final_routes = {}
final_distances = {}

for depot, assigned_clients in assignments.items():
    depot_vehicles = ["Moto1", "Moto2"] if depot == "Depósito 1" else ["Moto3", "Moto4"]
    routes = []
    distances = []

    for vehicle_name in depot_vehicles:
        if not assigned_clients:
            break

        vehicle = vehicles[vehicle_name]
        route, distance = dijkstra_for_depot(depot, assigned_clients[:], locations, vehicle)

        for client in route:
            if client in assigned_clients:
                assigned_clients.remove(client)

        routes.append((vehicle_name, route))
        distances.append((vehicle_name, distance))

    final_routes[depot] = routes
    final_distances[depot] = distances

# Exibir resultados
for depot in depots:
    print(f"Depósito: {depot}")
    for vehicle_name, route in final_routes[depot]:
        print(f"Veículo: {vehicle_name}")
        print(f"Rota Final: {' -> '.join(route)}")

    total_distance = sum(distance for _, distance in final_distances[depot])
    print(f"Distância Total para {depot}: {total_distance:.2f} km\n")
