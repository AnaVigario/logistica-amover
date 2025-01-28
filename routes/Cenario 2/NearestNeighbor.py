import numpy as np

# Dados do problema
locations = {
    "Depósito": (0, 0),
    "A": (2, 3),
    "B": (5, 8),
    "C": (6, 1),
    "D": (8, 6),
    "E": (1, 9)
}

demands = {
    "Depósito": 0,
    "A": 3,
    "B": 5,
    "C": 4,
    "D": 6,
    "E": 2
}

# Janelas de tempo (em minutos, convertidas de horas para consistência)
time_windows = {
    "Depósito": (0, 720),  # Aberto o dia todo
    "A": (480, 600),  # 8h - 10h
    "B": (540, 660),  # 9h - 11h
    "C": (600, 720),  # 10h - 12h
    "D": (420, 540),  # 7h - 9h
    "E": (480, 720)   # 8h - 12h
}

# Matriz de distância e tempo (em minutos)
distance_matrix = {
    ("Depósito", "A"): (4.0, 6),
    ("A", "B"): (6.0, 9),
    ("B", "C"): (5.0, 7.5),
    ("C", "D"): (3.5, 5.25),
    ("D", "E"): (7.0, 10.5),
    ("E", "Depósito"): (6.5, 9.74),
    ("A", "Depósito"): (4.0, 6),
    ("B", "A"): (6.0, 9),
    ("C", "B"): (5.0, 7.5),
    ("D", "C"): (3.5, 5.25),
    ("E", "D"): (7.0, 10.5),
    ("Depósito", "E"): (6.5, 9.74)
}

vehicle_capacity = 10
vehicle_speed = 40  # Velocidade média em km/h

def calculate_total_time(route, distance_matrix, time_windows):
    current_time = 0
    for i in range(len(route) - 1):
        origin, destination = route[i], route[i + 1]
        _, travel_time = distance_matrix.get((origin, destination), (0, 0))

        current_time += travel_time
        start_window, end_window = time_windows[destination]

        if current_time < start_window:
            current_time = start_window  # Espera até o início da janela de tempo
        elif current_time > end_window:
            return float('inf')  # Penalidade por violar janela de tempo

    return current_time

def convert_time_to_hms(minutes):
    hours = int(minutes // 60)
    minutes = int(minutes % 60)
    seconds = int((minutes % 1) * 60)
    return hours, minutes, seconds

def nearest_neighbor_with_time(locations, demands, distance_matrix, time_windows, vehicle_capacity):
    depot = "Depósito"
    clients = list(locations.keys())
    clients.remove(depot)

    # Inicializar a rota
    route = [depot]
    current_capacity = vehicle_capacity
    current_time = 0

    while clients:
        next_client = None
        best_distance = float('inf')

        for client in clients:
            if demands[client] <= current_capacity:
                dist, travel_time = distance_matrix.get((route[-1], client), (0, 0))
                arrival_time = current_time + travel_time

                start_window, end_window = time_windows[client]
                if arrival_time < start_window:
                    arrival_time = start_window

                if arrival_time <= end_window and dist < best_distance:
                    best_distance = dist
                    next_client = client

        if next_client is None:
            # Retornar ao depósito se não puder atender mais clientes
            _, travel_time = distance_matrix.get((route[-1], depot), (0, 0))
            current_time += travel_time
            route.append(depot)
            current_capacity = vehicle_capacity
        else:
            # Visitar o próximo cliente
            _, travel_time = distance_matrix.get((route[-1], next_client), (0, 0))
            current_time += travel_time
            route.append(next_client)
            current_capacity -= demands[next_client]
            clients.remove(next_client)

    # Retornar ao depósito no final
    if route[-1] != depot:
        _, travel_time = distance_matrix.get((route[-1], depot), (0, 0))
        current_time += travel_time
        route.append(depot)

    # Calcular distância total
    total_distance = 0
    for i in range(len(route) - 1):
        dist, _ = distance_matrix.get((route[i], route[i + 1]), (0, 0))
        total_distance += dist

    return route, total_distance, current_time, route.count(depot) - 1

# Resolver o problema
final_route, total_distance, total_time, visits_to_depot = nearest_neighbor_with_time(locations, demands, distance_matrix, time_windows, vehicle_capacity)

# Converter tempo para horas, minutos e segundos
hours, minutes, seconds = convert_time_to_hms(total_time)

# Exibir resultados
print(f"Rota do Veículo: {' -> '.join(final_route)}")
print(f"Distância Total: {total_distance:.2f} km")
print(f"Tempo Total de Viagem: {hours}h {minutes}m {seconds}s")
print(f"Número de Visitas ao Depósito: {visits_to_depot}")
