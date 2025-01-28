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

# Implementação do Savings Algorithm com dois veículos

def savings_algorithm_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity):
    depot_index = 0
    num_clients = len(demands) - 1  # Ignorar o depósito
    clients = list(range(1, num_clients + 1))

    # Calcular economias
    savings = []
    for i in clients:
        for j in clients:
            if i != j:
                save = distance_matrix[depot_index][i] + distance_matrix[depot_index][j] - distance_matrix[i][j]
                savings.append((save, i, j))

    # Ordenar economias em ordem decrescente
    savings.sort(reverse=True, key=lambda x: x[0])

    # Inicializar rotas para os dois veículos
    vehicle_routes = [[], []]
    vehicle_loads = [0, 0]

    # Atribuir clientes às rotas
    assigned_clients = set()

    for save, i, j in savings:
        if i in assigned_clients or j in assigned_clients:
            continue

        for v in range(2):
            if vehicle_loads[v] + demands[list(locations.keys())[i]] + demands[list(locations.keys())[j]] <= vehicle_capacity[v]:
                vehicle_routes[v].append(i)
                vehicle_routes[v].append(j)
                vehicle_loads[v] += demands[list(locations.keys())[i]] + demands[list(locations.keys())[j]]
                assigned_clients.update([i, j])
                break

    # Verificar clientes restantes
    for client in clients:
        if client not in assigned_clients:
            for v in range(2):
                if vehicle_loads[v] + demands[list(locations.keys())[client]] <= vehicle_capacity[v]:
                    vehicle_routes[v].append(client)
                    vehicle_loads[v] += demands[list(locations.keys())[client]]
                    assigned_clients.add(client)
                    break

    # Ajustar rotas para incluir o depósito e respeitar a capacidade de bateria
    final_routes = []
    total_distance = 0

    for v in range(2):
        current_route = [depot_index]
        current_battery = battery_capacity[v]

        for client in vehicle_routes[v]:
            distance_to_client = distance_matrix[current_route[-1]][client]
            if current_battery >= distance_to_client:
                current_route.append(client)
                current_battery -= distance_to_client
            else:
                current_route.append(depot_index)
                total_distance += calculate_route_distance(current_route, distance_matrix)
                final_routes.append(current_route)
                current_route = [depot_index, client]
                current_battery = battery_capacity[v] - distance_to_client

        current_route.append(depot_index)
        total_distance += calculate_route_distance(current_route, distance_matrix)
        final_routes.append(current_route)

    return final_routes, total_distance

# Resolver o problema
final_routes, total_distance = savings_algorithm_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity)

# Mostrar solução
for vehicle_id, route in enumerate(final_routes):
    route_distance = calculate_route_distance(route, distance_matrix)
    route_names = [list(locations.keys())[node] for node in route]
    print(f"Rota do veículo {vehicle_id + 1}: {' -> '.join(route_names)}")
    print(f"Distância percorrida pelo veículo {vehicle_id + 1}: {route_distance} km")
    print(f"Número de visitas ao depósito pelo veículo {vehicle_id + 1}: {route.count(0) - 1}")

print(f"Distância total percorrida por todos os veículos: {total_distance} km")
