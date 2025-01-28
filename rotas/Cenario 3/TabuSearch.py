import numpy as np
import random

# Dados do problema
locations = {
    "Depósito": (0, 0),
    "A": (3, 2),
    "B": (7, 5),
    "C": (2, 8),
    "D": (5, 7),
    "Estação": (4, 4)
}

demands = {
    "A": 4,
    "B": 6,
    "C": 3,
    "D": 5
}

time_of_request = {
    "A": 8 * 60,      # 8h
    "B": 8.5 * 60,    # 8h30
    "C": 9 * 60,      # 9h
    "D": 10 * 60      # 10h
}

# Configuração do veículo
vehicle_capacity = 12  # kg
battery_capacity_km = 20  # km
battery_recharge_time = 30  # minutos para recarregar completamente
recharge_rate = 2  # bateria recarregada por minuto

def calculate_distance(coord1, coord2):
    return np.sqrt((coord1[0] - coord2[0])**2 + (coord1[1] - coord2[1])**2)

def tabu_search_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km, max_iterations=100, tabu_tenure=10):
    depot = "Depósito"
    current_time = 8 * 60  # Início às 8h
    current_battery = battery_capacity_km
    current_capacity = vehicle_capacity
    route = [depot]
    remaining_clients = set(demands.keys())
    total_distance = 0

    best_route = None
    best_distance = float('inf')
    tabu_list = []

    def calculate_solution_distance(solution):
        distance = 0
        for i in range(len(solution) - 1):
            distance += calculate_distance(locations[solution[i]], locations[solution[i + 1]])
        return distance

    while remaining_clients:
        available_clients = [client for client in remaining_clients if time_of_request[client] <= current_time]

        if not available_clients:
            next_request_time = min(time_of_request[client] for client in remaining_clients)
            current_time = next_request_time
            continue

        # Gerar vizinhança
        neighbors = []
        for client in available_clients:
            distance = calculate_distance(locations[route[-1]], locations[client])
            if client not in tabu_list:
                neighbors.append((client, distance))

        if not neighbors:
            break

        # Selecionar o melhor vizinho
        neighbors.sort(key=lambda x: x[1])
        next_client, closest_distance = neighbors[0]

        if demands[next_client] > current_capacity:
            # Retornar ao depósito para descarregar
            distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
            if current_battery < distance_to_depot:
                # Ir para a estação de recarga
                recharge_distance = calculate_distance(locations[route[-1]], locations["Estação"])
                route.append("Estação")
                current_time += recharge_distance + battery_recharge_time
                current_battery = battery_capacity_km

            route.append(depot)
            current_time += distance_to_depot
            current_battery -= distance_to_depot
            current_capacity = vehicle_capacity
            continue

        # Verificar se há bateria suficiente para alcançar o cliente
        distance_to_client = calculate_distance(locations[route[-1]], locations[next_client])
        if current_battery < distance_to_client:
            # Ir para a estação de recarga
            recharge_distance = calculate_distance(locations[route[-1]], locations["Estação"])
            route.append("Estação")
            current_time += recharge_distance + battery_recharge_time
            current_battery = battery_capacity_km

        # Atender o cliente
        route.append(next_client)
        current_time += distance_to_client
        current_battery -= distance_to_client
        current_capacity -= demands[next_client]
        total_distance += distance_to_client
        remaining_clients.remove(next_client)

        # Atualizar lista tabu
        tabu_list.append(next_client)
        if len(tabu_list) > tabu_tenure:
            tabu_list.pop(0)

    # Retornar ao depósito
    if route[-1] != depot:
        distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
        route.append(depot)
        total_distance += distance_to_depot

    # Verificar se a solução é melhor
    if total_distance < best_distance:
        best_route = route
        best_distance = total_distance

    return best_route, best_distance, current_time

# Resolver o problema
final_route, total_distance, final_time = tabu_search_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km)

# Exibir resultados
print("Rota Final:", " -> ".join(final_route))
print(f"Distância Total: {total_distance:.2f} km")
print(f"Tempo Total: {int(final_time // 60)}h {int(final_time % 60)}m")
