"""
geocode_tasks.py — Converte as moradas das tarefas em coordenadas REAIS.

Problema que resolve: o backend, quando uma tarefa não tem LocationNode, mete
coordenadas aleatórias (Random()*10). Este script lê a morada de cada tarefa
(street, door_number, postal_code, city), geocodifica-a com o Nominatim
(OpenStreetMap, gratuito) e atualiza a LocationNode com lat/lon reais. Depois de
correres isto, a rota do otimizador passa a fazer sentido geográfico.

Uso (na tua máquina):
    pip install psycopg2-binary requests
    python geocode_tasks.py

Ligação à BD local (overridável por variáveis de ambiente):
    DB_HOST (default localhost)   DB_PORT (default 5435)
    DB_NAME (default amover-data) DB_USER (default postgres)
    DB_PASSWORD (default Fulgora2000)

Nota: o Nominatim permite ~1 pedido/segundo e exige um User-Agent. Para muitos
endereços, considera um serviço dedicado (Google/Mapbox) ou um Nominatim próprio.
"""

import os
import time

import psycopg2
import requests

DB = dict(
    host=os.environ.get("DB_HOST", "localhost"),
    port=int(os.environ.get("DB_PORT", "5435")),
    dbname=os.environ.get("DB_NAME", "amover-data"),
    user=os.environ.get("DB_USER", "postgres"),
    password=os.environ.get("DB_PASSWORD", "Fulgora2000"),
)

NOMINATIM_URL = "https://nominatim.openstreetmap.org/search"
HEADERS = {"User-Agent": "amover-geocoder/1.0 (teste local A-MoVeR)"}
COUNTRY = "Portugal"
SLEEP_S = 1.1  # respeita o limite de ~1 pedido/segundo do Nominatim


def _query_nominatim(q):
    r = requests.get(
        NOMINATIM_URL,
        params={"q": q, "format": "json", "limit": 1, "countrycodes": "pt"},
        headers=HEADERS,
        timeout=15,
    )
    r.raise_for_status()
    data = r.json()
    if data:
        return float(data[0]["lat"]), float(data[0]["lon"])
    return None


def geocode(street, door, postal, city):
    """Tenta a morada completa; se falhar, cai para código postal + cidade."""
    full = " ".join(p for p in [(street or "").strip(), (door or "").strip()] if p)
    attempts = [
        ", ".join(p for p in [full, postal, city, COUNTRY] if p),
        ", ".join(p for p in [street, city, COUNTRY] if p),
        ", ".join(p for p in [postal, city, COUNTRY] if p),
    ]
    seen = set()
    for q in attempts:
        if not q or q in seen:
            continue
        seen.add(q)
        res = _query_nominatim(q)
        if res:
            return res[0], res[1], q
        time.sleep(SLEEP_S)
    return None, None, attempts[0]


def main():
    conn = psycopg2.connect(**DB)
    conn.autocommit = False
    cur = conn.cursor()

    # Tarefas + a LocationNode que o algoritmo usa (lat/lon a corrigir).
    cur.execute(
        '''
        SELECT t."ID", t.street, t.door_number, t.postal_code, t.city, ln."ID"
        FROM tasks t
        JOIN "LocationNodeTask" lnt ON lnt."TaskID" = t."ID"
        JOIN "LocationNode" ln ON ln."ID" = lnt."NodeID"
        ORDER BY t."ID"
        '''
    )
    rows = cur.fetchall()
    if not rows:
        print("Sem tarefas com LocationNode. Gera/otimiza uma rota primeiro "
              "(é o que cria os nós).")
        return

    updated = 0
    for tid, street, door, postal, city, node_id in rows:
        try:
            lat, lon, query = geocode(street, door, postal, city)
        except Exception as e:
            print(f"[ERRO]  tarefa {tid}: {e}")
            continue

        if lat is None:
            print(f"[SEM RESULTADO] tarefa {tid}: '{query}'")
        else:
            cur.execute(
                'UPDATE "LocationNode" SET latitude = %s, longintude = %s WHERE "ID" = %s',
                (lat, lon, node_id),
            )
            updated += 1
            print(f"[OK]    tarefa {tid}: {query}  ->  {lat:.6f}, {lon:.6f}")
        time.sleep(SLEEP_S)

    conn.commit()
    cur.close()
    conn.close()
    print(f"\nConcluído: {updated}/{len(rows)} nós atualizados com coordenadas reais.")
    print("Agora volta a otimizar a rota (POST /api/route/optimize-for-vehicle) "
          "para a ordem passar a basear-se nas moradas reais.")


if __name__ == "__main__":
    main()
