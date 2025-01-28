import numpy as np
import heapq

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

def dijkstra_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km):
    current_time = 8 * 60  # Começa às 8h
    current_battery = battery_capacity_km
    current_capacity = vehicle_capacity
    route = ["Depósito"]
    completed_deliveries = []
    remaining_clients = set(demands.keys())

    while remaining_clients:
        # Filtrar pedidos disponíveis no tempo atual
        available_clients = [client for client in remaining_clients if time_of_request[client] <= current_time]

        if not available_clients:
            # Avançar no tempo para o próximo pedido
            next_request_time = min(time_of_request[client] for client in remaining_clients)
            current_time = next_request_time
            continue

        # Utilizar Dijkstra para encontrar o cliente mais próximo
        pq = []  # Fila de prioridade
        last_location = route[-1]

        for client in available_clients:
            distance = calculate_distance(locations[last_location], locations[client])
            heapq.heappush(pq, (distance, client))

        if not pq:
            break

        closest_distance, closest_client = heapq.heappop(pq)

        # Verificar se há capacidade para atender o cliente
        if demands[closest_client] > current_capacity:
            # Retornar ao depósito para descarregar
            distance_to_depot = calculate_distance(locations[last_location], locations["Depósito"])
            if current_battery < distance_to_depot:
                # Recarga necessária
                distance_to_station = calculate_distance(locations[last_location], locations["Estação"])
                route.append("Estação")
                current_time += distance_to_station + battery_recharge_time
                current_battery = battery_capacity_km

            route.append("Depósito")
            current_time += distance_to_depot
            current_battery -= distance_to_depot
            current_capacity = vehicle_capacity
            continue

        # Verificar bateria suficiente para chegar ao cliente
        if current_battery < closest_distance:
            # Ir para estação recarregar
            distance_to_station = calculate_distance(locations[last_location], locations["Estação"])
            route.append("Estação")
            current_time += distance_to_station + battery_recharge_time
            current_battery = battery_capacity_km

        # Atender o cliente
        route.append(closest_client)
        current_time += closest_distance
        current_battery -= closest_distance
        current_capacity -= demands[closest_client]
        remaining_clients.remove(closest_client)
        completed_deliveries.append(closest_client)

    # Retornar ao depósito
    if route[-1] != "Depósito":
        distance_to_depot = calculate_distance(locations[route[-1]], locations["Depósito"])
        route.append("Depósito")
        current_time += distance_to_depot

    return route, completed_deliveries, current_time

# Resolver o problema
final_route, completed_deliveries, final_time = dijkstra_with_recharge(locations, demands, time_of_request, vehicle_capacity, battery_capacity_km)

# Exibir resultados
print("Rota Final:", " -> ".join(final_route))
print("Entregas Realizadas:", completed_deliveries)
print(f"Tempo Final: {int(final_time // 60)}h {int(final_time % 60)}m")
