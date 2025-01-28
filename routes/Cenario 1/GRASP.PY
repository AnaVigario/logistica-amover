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

# Construção adaptativa para GRASP
def construct_solution(distance_matrix, demands, vehicle_capacity, battery_capacity, alpha=0.5):
    depot_index = 0
    num_clients = len(demands) - 1  # Ignorar o depósito
    clients = list(range(1, num_clients + 1))

    # Inicializar rotas para os dois veículos
    vehicle_routes = [[], []]
    vehicle_loads = [0, 0]
    remaining_clients = set(clients)

    for v in range(2):
        current_route = [depot_index]
        current_capacity = vehicle_capacity[v]
        current_battery = battery_capacity[v]

        while remaining_clients:
            current_location = current_route[-1]

            # Calcular candidatos e lista restrita de candidatos (RCL)
            candidates = []
            for client in remaining_clients:
                distance_to_client = distance_matrix[current_location][client]
                if demands[list(locations.keys())[client]] <= current_capacity:
                    candidates.append((client, distance_to_client))

            if not candidates:
                break

            candidates.sort(key=lambda x: x[1])
            min_distance = candidates[0][1]
            max_distance = candidates[-1][1]

            threshold = min_distance + alpha * (max_distance - min_distance)
            rcl = [client for client, distance in candidates if distance <= threshold]

            # Selecionar cliente aleatoriamente da RCL
            chosen_client = random.choice(rcl)

            # Atualizar rota e capacidade
            current_route.append(chosen_client)
            current_capacity -= demands[list(locations.keys())[chosen_client]]
            current_battery -= distance_matrix[current_location][chosen_client]
            remaining_clients.remove(chosen_client)

        current_route.append(depot_index)
        vehicle_routes[v] = current_route

    return vehicle_routes

# Busca local para otimização
def local_search(solution, distance_matrix):
    best_solution = solution[:]
    best_distance = sum(calculate_route_distance(route, distance_matrix) for route in solution)

    improved = True
    while improved:
        improved = False
        for v in range(len(solution)):
            for i in range(1, len(solution[v]) - 2):
                for j in range(i + 1, len(solution[v]) - 1):
                    new_solution = [route[:] for route in solution]
                    new_solution[v][i], new_solution[v][j] = new_solution[v][j], new_solution[v][i]

                    new_distance = sum(calculate_route_distance(route, distance_matrix) for route in new_solution)
                    if new_distance < best_distance:
                        best_solution = new_solution[:]
                        best_distance = new_distance
                        improved = True

    return best_solution

# Implementação do GRASP
def grasp(distance_matrix, demands, vehicle_capacity, battery_capacity, max_iterations=100, alpha=0.5):
    best_solution = None
    best_distance = float('inf')

    for iteration in range(max_iterations):
        # Construir solução inicial
        initial_solution = construct_solution(distance_matrix, demands, vehicle_capacity, battery_capacity, alpha)

        # Melhorar solução com busca local
        improved_solution = local_search(initial_solution, distance_matrix)

        # Avaliar solução
        total_distance = sum(calculate_route_distance(route, distance_matrix) for route in improved_solution)
        if total_distance < best_distance:
            best_solution = improved_solution[:]
            best_distance = total_distance

        print(f"Iteração {iteration + 1}: Melhor distância encontrada: {best_distance} km")

    return best_solution, best_distance

# Resolver o problema
best_solution, best_distance = grasp(distance_matrix, demands, vehicle_capacity, battery_capacity)

# Mostrar solução
for vehicle_id, route in enumerate(best_solution):
    route_distance = calculate_route_distance(route, distance_matrix)
    route_names = [list(locations.keys())[node] for node in route]
    print(f"Rota do veículo {vehicle_id + 1}: {' -> '.join(route_names)}")
    print(f"Distância percorrida pelo veículo {vehicle_id + 1}: {route_distance} km")
    print(f"Número de visitas ao depósito pelo veículo {vehicle_id + 1}: {route.count(0) - 1}")

print(f"Distância total percorrida por todos os veículos: {best_distance} km")
