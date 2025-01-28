import heapq
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

# Implementação do algoritmo de Dijkstra
def dijkstra(graph, start):
    num_nodes = len(graph)
    distances = [float('inf')] * num_nodes
    distances[start] = 0
    visited = [False] * num_nodes
    pq = [(0, start)]  # (distância acumulada, nó atual)

    while pq:
        current_distance, current_node = heapq.heappop(pq)

        if visited[current_node]:
            continue

        visited[current_node] = True

        for neighbor in range(num_nodes):
            if graph[current_node][neighbor] > 0 and not visited[neighbor]:
                new_distance = current_distance + graph[current_node][neighbor]
                if new_distance < distances[neighbor]:
                    distances[neighbor] = new_distance
                    heapq.heappush(pq, (new_distance, neighbor))

    return distances

# Divisão inicial dos clientes entre os veículos
def assign_clients_to_vehicles(locations, demands):
    vehicle_clients = [[], []]
    sorted_clients = sorted(demands.items(), key=lambda x: x[1], reverse=True)  # Ordenar por demanda

    for client, demand in sorted_clients:
        if client == "Depósito":
            continue
        if sum(demands[c] for c in vehicle_clients[0]) + demand <= vehicle_capacity[0]:
            vehicle_clients[0].append(client)
        else:
            vehicle_clients[1].append(client)

    return vehicle_clients

# Resolvendo o problema para dois veículos

def solve_dijkstra_multi_vehicle(distance_matrix, depot_index, vehicle_clients, battery_capacity):
    total_distance = [0, 0]
    remaining_demands = demands.copy()
    all_routes = []
    depot_visits = [0, 0]

    for vehicle_id in range(len(vehicle_clients)):
        current_capacity = vehicle_capacity[vehicle_id]
        current_battery = battery_capacity[vehicle_id]
        route = ["Depósito"]
        current_location = depot_index

        while any(remaining_demands[client] > 0 for client in vehicle_clients[vehicle_id]):
            distances = dijkstra(distance_matrix, current_location)
            next_stop = None
            min_distance = float('inf')

            for i, distance in enumerate(distances):
                client = list(locations.keys())[i]
                if client in vehicle_clients[vehicle_id] and remaining_demands[client] > 0 and distance < min_distance:
                    if distance <= current_battery and remaining_demands[client] <= current_capacity:
                        next_stop = i
                        min_distance = distance

            if next_stop is None:
                depot_visits[vehicle_id] += 1
                route.append("Depósito")
                current_capacity = vehicle_capacity[vehicle_id]
                current_battery = battery_capacity[vehicle_id]
                current_location = depot_index
                continue

            route.append(list(locations.keys())[next_stop])
            current_capacity -= remaining_demands[list(locations.keys())[next_stop]]
            current_battery -= int(min_distance)
            total_distance[vehicle_id] += int(min_distance)
            remaining_demands[list(locations.keys())[next_stop]] = 0
            current_location = next_stop

        route.append("Depósito")
        depot_visits[vehicle_id] += 1
        all_routes.append(route)

    return all_routes, total_distance, depot_visits

# Divisão dos clientes
vehicle_clients = assign_clients_to_vehicles(locations, demands)

# Executando a solução
routes, total_distances, depot_visits = solve_dijkstra_multi_vehicle(distance_matrix, 0, vehicle_clients, battery_capacity)

# Mostrar solução
for i, route in enumerate(routes):
    print(f"Dijkstra")
    print(f"Rota do veículo {i + 1}: {' -> '.join(route)}")
    print(f"Distância percorrida pelo veículo {i + 1}: {total_distances[i]} km")
    print(f"Número de visitas ao depósito pelo veículo {i + 1}: {depot_visits[i]}")
print(f"Distância total percorrida por todos os veículos: {sum(total_distances)} km")
