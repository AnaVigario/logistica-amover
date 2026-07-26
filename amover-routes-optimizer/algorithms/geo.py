"""
geo.py - Utilitarios geograficos partilhados pelo motor de rotas.
Suporta dois modos de coordenadas:
  - "geographic" (default): lat/lon reais -> Haversine * circuito (fluxo real).
  - "cartesian": pontos num plano (x,y) -> euclidiana direta (cenarios teste).
"""
import math

ROAD_CIRCUITY_FACTOR = 1.3


def haversine_km(lat1, lon1, lat2, lon2):
    """Distancia em km entre dois pontos GPS."""
    R = 6371.0
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlmb = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlmb / 2) ** 2
    return 2 * R * math.asin(math.sqrt(a))


def road_km(lat1, lon1, lat2, lon2):
    """Distancia de estrada estimada (Haversine com fator de circuito)."""
    return haversine_km(lat1, lon1, lat2, lon2) * ROAD_CIRCUITY_FACTOR


def euclid_km(y1, x1, y2, x2):
    """Distancia euclidiana direta num plano cartesiano (modo teste)."""
    return math.hypot(x2 - x1, y2 - y1)


def _leg_km(p1, p2, mode):
    """Distancia de um troco, conforme o modo de coordenadas."""
    (a1, b1), (a2, b2) = p1, p2
    if mode == "cartesian":
        return euclid_km(a1, b1, a2, b2)
    return road_km(a1, b1, a2, b2)


def route_metrics(ordered_points, consumption_kwh_per_km, speed_kph,
                  service_minutes=1.0, mode="geographic"):
    """
    Calcula metricas reais de uma rota.
    ordered_points: lista de (lat, lon) [ou (y, x) em cartesiano] JA ordenada,
                    com o deposito no inicio e no fim.
    mode: "geographic" (Haversine*circuito) ou "cartesian" (euclidiana).
    """
    total_km = 0.0
    for p1, p2 in zip(ordered_points, ordered_points[1:]):
        total_km += _leg_km(p1, p2, mode)
    energy_kwh = total_km * float(consumption_kwh_per_km)
    travel_min = (total_km / float(speed_kph) * 60.0) if speed_kph else 0.0
    n_stops = max(0, len(ordered_points) - 2)
    service_min = n_stops * float(service_minutes)
    return {
        "total_distance_km": round(total_km, 3),
        "total_energy_kwh": round(energy_kwh, 3),
        "total_time_min": round(travel_min + service_min, 1),
    }
