import itertools
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

# Implementação do Branch and Bound para dois veículos

def calculate_route_distance(route, distance_matrix):
    distance = 0
    for i in range(len(route) - 1):
        distance += distance_matrix[route[i]][route[i + 1]]
    return distance

def branch_and_bound_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity):
    num_clients = len(demands) - 1  # Ignorar o depósito
    clients = list(range(1, num_clients + 1))
    depot_index = 0

    best_routes = [[], []]  # Rotas para os dois veículos
    best_distance = float('inf')

    # Gerar todas as combinações possíveis de divisão entre dois veículos
    for split in itertools.combinations(clients, len(clients) // 2):
        vehicle_1_clients = list(split)
        vehicle_2_clients = [c for c in clients if c not in vehicle_1_clients]

        for perm1 in itertools.permutations(vehicle_1_clients):
            for perm2 in itertools.permutations(vehicle_2_clients):
                routes = [[], []]
                total_distance = 0

                # Construir rota para o veículo 1
                current_route = [depot_index]
                current_capacity = vehicle_capacity[0]
                current_battery = battery_capacity[0]

                for client in perm1:
                    demand = demands[list(locations.keys())[client]]
                    distance_to_client = distance_matrix[current_route[-1]][client]
                    distance_back_to_depot = distance_matrix[client][depot_index]

                    if current_capacity >= demand and current_battery >= (distance_to_client + distance_back_to_depot):
                        current_route.append(client)
                        current_capacity -= demand
                        current_battery -= distance_to_client
                    else:
                        current_route.append(depot_index)
                        routes[0].append(current_route)
                        total_distance += calculate_route_distance(current_route, distance_matrix)
                        current_route = [depot_index, client]
                        current_capacity = vehicle_capacity[0] - demand
                        current_battery = battery_capacity[0] - distance_to_client

                current_route.append(depot_index)
                routes[0].append(current_route)
                total_distance += calculate_route_distance(current_route, distance_matrix)

                # Construir rota para o veículo 2
                current_route = [depot_index]
                current_capacity = vehicle_capacity[1]
                current_battery = battery_capacity[1]

                for client in perm2:
                    demand = demands[list(locations.keys())[client]]
                    distance_to_client = distance_matrix[current_route[-1]][client]
                    distance_back_to_depot = distance_matrix[client][depot_index]

                    if current_capacity >= demand and current_battery >= (distance_to_client + distance_back_to_depot):
                        current_route.append(client)
                        current_capacity -= demand
                        current_battery -= distance_to_client
                    else:
                        current_route.append(depot_index)
                        routes[1].append(current_route)
                        total_distance += calculate_route_distance(current_route, distance_matrix)
                        current_route = [depot_index, client]
                        current_capacity = vehicle_capacity[1] - demand
                        current_battery = battery_capacity[1] - distance_to_client

                current_route.append(depot_index)
                routes[1].append(current_route)
                total_distance += calculate_route_distance(current_route, distance_matrix)

                # Verificar se a solução atual é melhor
                if total_distance < best_distance:
                    best_distance = total_distance
                    best_routes = routes

    return best_routes, best_distance

# Resolver o problema
best_routes, best_distance = branch_and_bound_two_vehicles(distance_matrix, demands, vehicle_capacity, battery_capacity)

# Mostrar solução
for vehicle_id, vehicle_routes in enumerate(best_routes):
    full_route = []
    vehicle_distance = 0
    depot_visits = len(vehicle_routes) - 1

    for route in vehicle_routes:
        full_route.extend(route[:-1])  # Adicionar rota sem o último depósito
        vehicle_distance += calculate_route_distance(route, distance_matrix)

    full_route.append(0)  # Adicionar o depósito ao final
    route_names = [list(locations.keys())[node] for node in full_route]
    print(f"Branch and Bound")
    print(f"Rota do veículo {vehicle_id + 1}: {' -> '.join(route_names)}")
    print(f"Distância percorrida pelo veículo {vehicle_id + 1}: {vehicle_distance} km")
    print(f"Número de visitas ao depósito pelo veículo {vehicle_id + 1}: {depot_visits}")

print(f"Distância total percorrida por todos os veículos: {best_distance} km")