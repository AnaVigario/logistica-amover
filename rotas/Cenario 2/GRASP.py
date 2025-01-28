import numpy as np
import random

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

def grasp_with_time(locations, demands, distance_matrix, time_windows, vehicle_capacity, max_iterations=100, alpha=0.3):
    depot = "Depósito"
    clients = list(locations.keys())
    clients.remove(depot)

    best_solution = None
    best_distance = float('inf')
    best_time = float('inf')

    for iteration in range(max_iterations):
        # Construção da solução inicial com lista restrita de candidatos (RCL)
        solution = [depot]
        current_capacity = vehicle_capacity
        current_time = 0
        remaining_clients = clients[:]

        while remaining_clients:
            candidates = []

            for client in remaining_clients:
                if demands[client] <= current_capacity:
                    dist, travel_time = distance_matrix.get((solution[-1], client), (0, 0))
                    arrival_time = current_time + travel_time

                    start_window, end_window = time_windows[client]
                    if arrival_time < start_window:
                        arrival_time = start_window

                    if arrival_time <= end_window:
                        candidates.append((client, dist))

            if not candidates:
                # Retornar ao depósito se não puder atender mais clientes
                _, travel_time = distance_matrix.get((solution[-1], depot), (0, 0))
                current_time += travel_time
                solution.append(depot)
                current_capacity = vehicle_capacity
                continue

            # Ordenar candidatos por distância e selecionar da RCL
            candidates.sort(key=lambda x: x[1])
            rcl = candidates[:max(1, int(len(candidates) * alpha))]
            next_client = random.choice(rcl)[0]

            # Atualizar solução
            _, travel_time = distance_matrix.get((solution[-1], next_client), (0, 0))
            current_time += travel_time
            solution.append(next_client)
            current_capacity -= demands[next_client]
            remaining_clients.remove(next_client)

        if solution[-1] != depot:
            _, travel_time = distance_matrix.get((solution[-1], depot), (0, 0))
            current_time += travel_time
            solution.append(depot)

        # Calcular distância total
        total_distance = 0
        for i in range(len(solution) - 1):
            dist, _ = distance_matrix.get((solution[i], solution[i + 1]), (0, 0))
            total_distance += dist

        # Verificar se a solução é melhor
        total_time = calculate_total_time(solution, distance_matrix, time_windows)
        if total_distance < best_distance and total_time != float('inf'):
            best_solution = solution
            best_distance = total_distance
            best_time = total_time

    if best_solution is None:
        # Garante uma solução válida se nenhuma foi encontrada
        best_solution = [depot] + clients + [depot]
        best_distance = 0
        best_time = calculate_total_time(best_solution, distance_matrix, time_windows)
        for i in range(len(best_solution) - 1):
            dist, _ = distance_matrix.get((best_solution[i], best_solution[i + 1]), (0, 0))
            best_distance += dist

    return best_solution, best_distance, best_time, best_solution.count(depot) - 1

# Resolver o problema
final_route, total_distance, total_time, visits_to_depot = grasp_with_time(locations, demands, distance_matrix, time_windows, vehicle_capacity)

# Converter tempo para horas, minutos e segundos
hours, minutes, seconds = convert_time_to_hms(total_time)

# Exibir resultados
print(f"Rota do Veículo: {' -> '.join(final_route)}")
print(f"Distância Total: {total_distance:.2f} km")
if hours is not None:
    print(f"Tempo Total de Viagem: {hours}h {minutes}m {seconds}s")
else:
    print("Não foi possível calcular o tempo total de viagem devido a violações de restrições.")
print(f"Número de Visitas ao Depósito: {visits_to_depot}")
