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

def branch_and_bound_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km):
    depot = "Depósito"
    clients = list(demands.keys())

    best_route = None
    best_distance = float('inf')
    best_time = float('inf')

    def calculate_total_time(route):
        current_time = 8 * 60  # Início às 8h
        current_battery = battery_capacity_km
        total_distance = 0

        for i in range(len(route) - 1):
            origin, destination = route[i], route[i + 1]
            distance = calculate_distance(locations[origin], locations[destination])

            if destination == "Estação":
                current_time += battery_recharge_time
                current_battery = battery_capacity_km
            else:
                total_distance += distance
                if current_battery < distance:
                    return float('inf'), float('inf')

                current_battery -= distance
                current_time += distance

                if destination in time_of_request and current_time < time_of_request[destination]:
                    current_time = time_of_request[destination]

        return current_time, total_distance

    for perm in permutations(clients):
        route = [depot] + list(perm) + [depot]
        current_capacity = vehicle_capacity
        valid = True

        for client in perm:
            if demands[client] > current_capacity:
                valid = False
                break
            current_capacity -= demands[client]

        if valid:
            total_time, total_distance = calculate_total_time(route)
            if total_distance < best_distance and total_time < best_time:
                best_route = route
                best_distance = total_distance
                best_time = total_time

    if best_route is None:
        best_route = [depot] + clients + [depot]
        best_time, best_distance = calculate_total_time(best_route)

    # Garantir que valores inválidos não sejam retornados
    if best_time == float('inf') or best_distance == float('inf'):
        best_time, best_distance = 0, 0

    return best_route, best_distance, best_time

# Resolver o problema
final_route, total_distance, total_time = branch_and_bound_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km)

# Exibir resultados
print("Rota Final:", " -> ".join(final_route))
print(f"Distância Total: {total_distance:.2f} km")
if total_time != 0:
    print(f"Tempo Total: {int(total_time // 60)}h {int(total_time % 60)}m")
else:
    print("Nenhuma solução viável encontrada.")