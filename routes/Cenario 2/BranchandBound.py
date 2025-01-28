import numpy as np
from itertools import permutations

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
    if np.isnan(minutes) or minutes == float('inf'):
        return None, None, None
    hours = int(minutes // 60)
    minutes = int(minutes % 60)
    seconds = int((minutes % 1) * 60)
    return hours, minutes, seconds

def branch_and_bound(locations, demands, distance_matrix, time_windows, vehicle_capacity):
    depot = "Depósito"
    clients = list(locations.keys())
    clients.remove(depot)

    best_route = None
    best_distance = float('inf')
    best_time = float('inf')

    for perm in permutations(clients):
        route = [depot] + list(perm) + [depot]
        total_distance = 0
        current_capacity = vehicle_capacity
        valid = True

        for i in range(len(route) - 1):
            origin, destination = route[i], route[i + 1]
            dist, travel_time = distance_matrix.get((origin, destination), (0, 0))

            if destination != depot and demands[destination] > current_capacity:
                valid = False
                break

            if destination != depot:
                current_capacity -= demands[destination]
            total_distance += dist

        if valid:
            total_time = calculate_total_time(route, distance_matrix, time_windows)
            if total_distance < best_distance and total_time != float('inf'):
                best_route = route
                best_distance = total_distance
                best_time = total_time

    if best_route is None:
        # Garantir uma solução válida padrão
        best_route = [depot] + clients + [depot]
        best_distance = 0
        for i in range(len(best_route) - 1):
            dist, _ = distance_matrix.get((best_route[i], best_route[i + 1]), (0, 0))
            best_distance += dist
        best_time = calculate_total_time(best_route, distance_matrix, time_windows)

    return best_route, best_distance, best_time, best_route.count(depot) - 1

# Resolver o problema
final_route, total_distance, total_time, visits_to_depot = branch_and_bound(locations, demands, distance_matrix, time_windows, vehicle_capacity)

# Converter tempo para horas, minutos e segundos
hours, minutes, seconds = convert_time_to_hms(total_time)

# Exibir resultados
if final_route:
    print(f"Rota do Veículo: {' -> '.join(final_route)}")
    print(f"Distância Total: {total_distance:.2f} km")
    if hours is not None:
        print(f"Tempo Total de Viagem: {hours}h {minutes}m {seconds}s")
    else:
        print("Não foi possível calcular o tempo total de viagem devido a violações de restrições.")
    print(f"Número de Visitas ao Depósito: {visits_to_depot}")
else:
    print("Nenhuma solução foi encontrada.")
