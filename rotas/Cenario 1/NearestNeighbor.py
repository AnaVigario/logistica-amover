import numpy as np

# Dados do problema
locations = {
    "Depósito": (0, 0),
    "A": (2, 3),
    "B": (5, 8),
    "C": (6, 1),
    "D": (8, 6),
    "E": (1, 9),
    "F": (7, 4)
}
demands = {
    "Depósito": 0,
    "A": 4,
    "B": 6,
    "C": 3,
    "D": 5,
    "E": 2,
    "F": 8
}
vehicle_capacity = [10, 10]  # Capacidade máxima por viagem para os dois veículos
battery_capacity = [30, 30]  # Distância máxima por viagem para os dois veículos

# Função para calcular a matriz de distância
def calculate_distance_matrix(locations):
    points = list(locations.values())
    size = len(points)
    distance_matrix = np.zeros((size, size))
    for i in range(size):
        for j in range(size):
            distance_matrix[i][j] = np.linalg.norm(np.array(points[i]) - np.array(points[j]))
    return distance_matrix

distance_matrix = calculate_distance_matrix(locations)

# Função para calcular a distância de uma rota
def calculate_route_distance(route, distance_matrix):
    distance = 0
    for i in range(len(route) - 1):
        distance += distance_matrix[route[i]][route[i + 1]]
    return distance

# Implementação do Nearest Neighbor

def nearest_neighbor_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity):
    depot_index = 0
    num_clients = len(demands) - 1  # Ignorar o depósito
    clients = list(range(1, num_clients + 1))

    # Inicializar rotas para os dois veículos
    vehicle_routes = [[], []]
    vehicle_loads = [0, 0]
    remaining_clients = set(clients)

    # Construir rotas para cada veículo
    for v in range(2):
        current_route = [depot_index]
        current_capacity = vehicle_capacity[v]
        current_battery = battery_capacity[v]

        while remaining_clients:
            current_location = current_route[-1]
            nearest_client = None
            nearest_distance = float('inf')

            for client in remaining_clients:
                distance_to_client = distance_matrix[current_location][client]
                if distance_to_client < nearest_distance and demands[list(locations.keys())[client]] <= current_capacity:
                    nearest_client = client
                    nearest_distance = distance_to_client

            if nearest_client is None:
                # Nenhum cliente restante pode ser atendido sem retornar ao depósito
                current_route.append(depot_index)
                vehicle_routes[v].append(current_route)
                break

            # Adicionar cliente à rota
            current_route.append(nearest_client)
            current_capacity -= demands[list(locations.keys())[nearest_client]]
            current_battery -= nearest_distance
            remaining_clients.remove(nearest_client)

            # Verificar se precisa retornar ao depósito
            if current_battery < min(distance_matrix[nearest_client][depot_index], nearest_distance):
                current_route.append(depot_index)
                vehicle_routes[v].append(current_route)
                current_route = [depot_index]
                current_capacity = vehicle_capacity[v]
                current_battery = battery_capacity[v]

        # Finalizar rota do veículo atual
        if current_route[-1] != depot_index:
            current_route.append(depot_index)
            vehicle_routes[v].append(current_route)

    # Calcular distância total
    total_distance = 0
    final_routes = []
    for v in range(2):
        for route in vehicle_routes[v]:
            final_routes.append(route)
            total_distance += calculate_route_distance(route, distance_matrix)

    return final_routes, total_distance

# Resolver o problema
final_routes, total_distance = nearest_neighbor_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity)

# Mostrar solução
for vehicle_id, route in enumerate(final_routes):
    route_distance = calculate_route_distance(route, distance_matrix)
    route_names = [list(locations.keys())[node] for node in route]
    print(f"Rota do veículo {vehicle_id + 1}: {' -> '.join(route_names)}")
    print(f"Distância percorrida pelo veículo {vehicle_id + 1}: {route_distance} km")
    print(f"Número de visitas ao depósito pelo veículo {vehicle_id + 1}: {route.count(0) - 1}")

print(f"Distância total percorrida por todos os veículos: {total_distance} km")
