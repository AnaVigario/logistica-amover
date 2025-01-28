import numpy as np
from itertools import permutations

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

def savings_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km):
    depot = "Depósito"
    clients = list(demands.keys())

    current_time = 8 * 60  # Começa às 8h
    current_battery = battery_capacity_km
    current_capacity = vehicle_capacity

    route = [depot]
    remaining_clients = set(clients)
    total_distance = 0

    while remaining_clients:
        available_clients = [client for client in remaining_clients if time_of_request[client] <= current_time]

        if not available_clients:
            next_time = min(time_of_request[client] for client in remaining_clients)
            current_time = next_time
            continue

        closest_client = None
        closest_distance = float('inf')

        for client in available_clients:
            distance = calculate_distance(locations[route[-1]], locations[client])
            if distance < closest_distance:
                closest_client = client
                closest_distance = distance

        if closest_client is None:
            break

        if demands[closest_client] > current_capacity:
            distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
            if current_battery < distance_to_depot:
                recharge_distance = calculate_distance(locations[route[-1]], locations["Estação"])
                route.append("Estação")
                current_time += recharge_distance + battery_recharge_time
                current_battery = battery_capacity_km

            route.append(depot)
            current_time += distance_to_depot
            current_battery -= distance_to_depot
            current_capacity = vehicle_capacity
            continue

        distance_to_client = calculate_distance(locations[route[-1]], locations[closest_client])
        if current_battery < distance_to_client:
            recharge_distance = calculate_distance(locations[route[-1]], locations["Estação"])
            route.append("Estação")
            current_time += recharge_distance + battery_recharge_time
            current_battery = battery_capacity_km

        route.append(closest_client)
        current_time += distance_to_client
        current_battery -= distance_to_client
        current_capacity -= demands[closest_client]
        total_distance += distance_to_client
        remaining_clients.remove(closest_client)

    if route[-1] != depot:
        distance_to_depot = calculate_distance(locations[route[-1]], locations[depot])
        route.append(depot)
        total_distance += distance_to_depot

    return route, total_distance, current_time

# Resolver o problema
final_route, total_distance, final_time = savings_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km)

# Exibir resultados
print("Rota Final:", " -> ".join(final_route))
print(f"Distância Total: {total_distance:.2f} km")
print(f"Tempo Total: {int(final_time // 60)}h {int(final_time % 60)}m")