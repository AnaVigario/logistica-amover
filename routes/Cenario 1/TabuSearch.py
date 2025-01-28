import numpy as np
import random

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

# Geração de solução inicial usando Nearest Neighbor
def nearest_neighbor_initial_solution(distance_matrix, demands, vehicle_capacity, battery_capacity):
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
                break

            # Adicionar cliente à rota
            current_route.append(nearest_client)
            current_capacity -= demands[list(locations.keys())[nearest_client]]
            current_battery -= nearest_distance
            remaining_clients.remove(nearest_client)

        current_route.append(depot_index)
        vehicle_routes[v] = current_route

    return vehicle_routes

# Implementação do Tabu Search
def tabu_search(distance_matrix, demands, vehicle_capacity, battery_capacity, max_iterations=100, tabu_tenure=10):
    # Geração de solução inicial
    current_solution = nearest_neighbor_initial_solution(distance_matrix, demands, vehicle_capacity, battery_capacity)
    best_solution = current_solution[:]
    best_distance = sum(calculate_route_distance(route, distance_matrix) for route in current_solution)

    tabu_list = []
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        neighborhood = []

        # Gerar vizinhança trocando clientes entre rotas
        for i in range(1, len(current_solution[0]) - 1):
            for j in range(1, len(current_solution[1]) - 1):
                neighbor = [current_solution[0][:], current_solution[1][:]]
                neighbor[0][i], neighbor[1][j] = neighbor[1][j], neighbor[0][i]
                neighborhood.append(neighbor)

        # Avaliar vizinhança
        best_neighbor = None
        best_neighbor_distance = float('inf')

        for neighbor in neighborhood:
            neighbor_distance = sum(calculate_route_distance(route, distance_matrix) for route in neighbor)

            if neighbor_distance < best_neighbor_distance and neighbor not in tabu_list:
                best_neighbor = neighbor
                best_neighbor_distance = neighbor_distance

        if best_neighbor is not None:
            current_solution = best_neighbor[:]

            if best_neighbor_distance < best_distance:
                best_solution = best_neighbor[:]
                best_distance = best_neighbor_distance

            tabu_list.append(best_neighbor)

            if len(tabu_list) > tabu_tenure:
                tabu_list.pop(0)

        # Log de progresso
        print(f"Iteração {iteration}: Melhor distância encontrada: {best_distance} km")

    return best_solution, best_distance

# Resolver o problema
best_solution, best_distance = tabu_search(distance_matrix, demands, vehicle_capacity, battery_capacity)

# Mostrar solução
for vehicle_id, route in enumerate(best_solution):
    route_distance = calculate_route_distance(route, distance_matrix)
    route_names = [list(locations.keys())[node] for node in route]
    print(f"Rota do veículo {vehicle_id + 1}: {' -> '.join(route_names)}")
    print(f"Distância percorrida pelo veículo {vehicle_id + 1}: {route_distance} km")
    print(f"Número de visitas ao depósito pelo veículo {vehicle_id + 1}: {route.count(0) - 1}")

print(f"Distância total percorrida por todos os veículos: {best_distance} km")