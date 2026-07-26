#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
============================================================================
 SIMULADOR DE ROTA - A-Mover
============================================================================
 Simula uma mota a percorrer uma rota ja gravada na base de dados.
 - "Anda" entre as paragens com o tempo acelerado (configuravel).
 - A cada intervalo reporta: posicao, tarefas concluidas, (bateria).
 - Mostra no terminal E grava num CSV para analise posterior.

 DOIS MODOS:
 -----------
 1. MODO COM ENERGIA (default) - o comportamento original.
    Le um Plan/PlanStop da Supabase, mostra a bateria a decair, atualiza
    current_charge_kwh na BD. Usa a estrutura do sistema proprio (Django).

 2. MODO SEM ENERGIA (--sem-energia) - para a BD do ecossistema da equipa.
    Le a rota da LocationNodeTask (por RouteGroupId), NAO le nem mostra
    bateria (a BD da equipa nao tem current_charge_kwh), NAO escreve carga.
    So anima a rota: posicao, tempo, km, entregas.

 USO (modo com energia, Supabase):
   python simular_rota.py --plan 49
   python simular_rota.py --plan 49 --aceleracao 60 --intervalo 5

 USO (modo sem energia, BD da equipa):
   python simular_rota.py --sem-energia --route-group 7ca3eb28-90fd-4bab-8db3-ab28011e9eb0
   python simular_rota.py --sem-energia --route-group <id> --aceleracao 120

 Ligacao a BD:
   - Modo com energia: SUPABASE_DSN ou SUPABASE_HOST/USER/PASSWORD (como antes).
   - Modo sem energia: por omissao liga a BD da equipa em localhost:5435
     (amover-data / postgres / Fulgora2000). Pode ser sobreposto por
     AMOVER_DSN ou pelos argumentos --db-host/--db-port/--db-name/--db-user/--db-pass.
============================================================================
"""
import os
import sys
import csv
import math
import time
import argparse
from datetime import datetime
import psycopg2

try:
    from dotenv import load_dotenv
    load_dotenv()
except ImportError:
    pass

# ----------------------------------------------------------------------------
# CONSTANTES FISICAS - alinhadas com o otimizador
# ----------------------------------------------------------------------------
CONSUMPTION_KWH_PER_KM = 0.05
ROAD_CIRCUITY_FACTOR = 1.3
SPEED_KPH = 30.0


# ----------------------------------------------------------------------------
# LIGACOES A BASE DE DADOS
# ----------------------------------------------------------------------------
def get_connection_supabase():
    """Ligacao ao sistema proprio (Supabase) - modo com energia."""
    dsn = os.environ.get("SUPABASE_DSN")
    if dsn:
        return psycopg2.connect(dsn)
    host = os.environ.get("SUPABASE_HOST")
    user = os.environ.get("SUPABASE_USER")
    password = os.environ.get("SUPABASE_PASSWORD")
    dbname = os.environ.get("SUPABASE_DB", "postgres")
    port = os.environ.get("SUPABASE_PORT", "5432")
    if not (host and user and password):
        print("ERRO: credenciais Supabase nao encontradas nas variaveis de ambiente.")
        print("Define SUPABASE_DSN ou SUPABASE_HOST/SUPABASE_USER/SUPABASE_PASSWORD.")
        sys.exit(1)
    return psycopg2.connect(
        host=host, user=user, password=password,
        dbname=dbname, port=port, sslmode="require",
    )


def get_connection_equipa(args):
    """Ligacao a BD do ecossistema da equipa - modo sem energia.
    Por omissao localhost:5435 / amover-data. Sobreposta por AMOVER_DSN
    ou pelos argumentos --db-*."""
    dsn = os.environ.get("AMOVER_DSN")
    if dsn:
        return psycopg2.connect(dsn)
    return psycopg2.connect(
        host=args.db_host, port=args.db_port, dbname=args.db_name,
        user=args.db_user, password=args.db_pass,
    )


# ----------------------------------------------------------------------------
# GEOMETRIA
# ----------------------------------------------------------------------------
def haversine_km(lat1, lon1, lat2, lon2):
    R = 6371.0
    p1, p2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlmb = math.radians(lon2 - lon1)
    a = (math.sin(dphi / 2) ** 2
         + math.cos(p1) * math.cos(p2) * math.sin(dlmb / 2) ** 2)
    return 2 * R * math.asin(math.sqrt(a))


def road_distance_km(lat1, lon1, lat2, lon2):
    return haversine_km(lat1, lon1, lat2, lon2) * ROAD_CIRCUITY_FACTOR


# ----------------------------------------------------------------------------
# LEITURA DA ROTA - MODO EQUIPA (LocationNodeTask por RouteGroupId)
# ----------------------------------------------------------------------------
def carregar_rota_equipa(conn, route_group_id):
    """Le a rota da BD da equipa a partir do RouteGroupId.
    Devolve (info_mota, paragens) - paragens ordenadas por stopOrder,
    cada uma com lat/lon vindas da LocationNode."""
    cur = conn.cursor()
    cur.execute("""
        SELECT lnt."stopOrder", lnt."TaskID", ln.latitude, ln.longintude, ln.address
        FROM public."LocationNodeTask" lnt
        JOIN public."LocationNode" ln ON ln."ID" = lnt."NodeID"
        WHERE lnt."RouteGroupId" = %s
        ORDER BY lnt."stopOrder"
    """, (route_group_id,))
    rows = cur.fetchall()
    if not rows:
        print(f"ERRO: nenhuma rota encontrada com RouteGroupId {route_group_id!r}.")
        sys.exit(1)

    paragens = []
    for stop_order, task_id, lat, lon, address in rows:
        if lat is None or lon is None:
            print(f"AVISO: tarefa {task_id} (stopOrder {stop_order}) sem coordenadas - ignorada.")
            continue
        paragens.append({
            "sequence": stop_order,
            "task_id": task_id,
            "lat": float(lat),
            "lon": float(lon),
            "address": address or "",
        })

    # Tentar descobrir a mota associada (via tasks.vehicleID da primeira tarefa)
    nome_mota = "(desconhecida)"
    if paragens:
        cur.execute("""
            SELECT v.name
            FROM tasks t
            JOIN vehicles v ON v."ID" = t."vehicleID"
            WHERE t."ID" = %s
            LIMIT 1
        """, (paragens[0]["task_id"],))
        r = cur.fetchone()
        if r:
            nome_mota = r[0]
    cur.close()

    info_mota = {"name": nome_mota}
    return info_mota, paragens


# ----------------------------------------------------------------------------
# LEITURA DA ROTA - MODO SUPABASE (Plan/PlanStop, com energia)
# ----------------------------------------------------------------------------
def carregar_plano_supabase(conn, plan_id):
    """Le um plano da Supabase (estrutura do sistema proprio, com energia)."""
    cur = conn.cursor()
    cur.execute("""
        SELECT p."ID", p."vehicleID", v."name",
               v."batteryCapacity", v."current_charge_kwh", v."status"
        FROM public."Plan" p
        JOIN public."vehicles" v ON v."ID" = p."vehicleID"
        WHERE p."ID" = %s
    """, (plan_id,))
    row = cur.fetchone()
    if not row:
        print(f"ERRO: plano {plan_id} nao encontrado (ou sem veiculo associado).")
        sys.exit(1)
    veiculo = {
        "plan_id": row[0], "vehicle_id": row[1], "name": row[2],
        "battery_capacity": float(row[3]) if row[3] is not None else None,
        "current_charge": float(row[4]) if row[4] is not None else None,
        "status": row[5],
    }
    cur.execute("""
        SELECT ps."Sequence", ps."TaskId", ln.latitude, ln.longintude
        FROM public."PlanStop" ps
        JOIN public."LocationNodeTask" lnt ON lnt."tasksID" = ps."TaskId"
        JOIN public."LocationNode" ln ON ln."ID" = lnt."NodesID"
        WHERE ps."PlanId" = %s
        ORDER BY ps."Sequence"
    """, (plan_id,))
    paragens = []
    for seq, task_id, lat, lon in cur.fetchall():
        if lat is None or lon is None:
            print(f"AVISO: tarefa {task_id} (seq {seq}) sem coordenadas - ignorada.")
            continue
        paragens.append({"sequence": seq, "task_id": task_id,
                         "lat": float(lat), "lon": float(lon)})
    cur.close()
    return veiculo, paragens


def carregar_deposito(conn, deposito_node_id):
    cur = conn.cursor()
    cur.execute(
        'SELECT latitude, longintude, address FROM public."LocationNode" WHERE "ID" = %s',
        (deposito_node_id,),
    )
    row = cur.fetchone()
    cur.close()
    if not row or row[0] is None or row[1] is None:
        return None
    return {"lat": float(row[0]), "lon": float(row[1]), "address": row[2] or "Deposito"}


def atualizar_carga(conn, vehicle_id, nova_carga):
    cur = conn.cursor()
    cur.execute('UPDATE public."vehicles" SET "current_charge_kwh" = %s WHERE "ID" = %s',
                (round(nova_carga, 4), vehicle_id))
    conn.commit()
    cur.close()


def atualizar_status(conn, vehicle_id, status):
    cur = conn.cursor()
    cur.execute('UPDATE public."vehicles" SET "status" = %s WHERE "ID" = %s',
                (status, vehicle_id))
    conn.commit()
    cur.close()


# ----------------------------------------------------------------------------
# SIMULACAO (unificada - com ou sem energia)
# ----------------------------------------------------------------------------
def simular(conn, paragens, aceleracao, intervalo_s, csv_path,
            *, com_energia, veiculo=None, escrever_bd=False, deposito=None,
            titulo=""):
    """Anima a rota. Se com_energia=False, ignora tudo o que e bateria."""
    if len(paragens) < 1:
        print("ERRO: rota sem paragens com coordenadas. Nada a simular.")
        sys.exit(1)

    # Depot no inicio e no fim (se fornecido)
    if deposito:
        ponto_dep = {"sequence": 0, "task_id": "DEPOSITO",
                     "lat": deposito["lat"], "lon": deposito["lon"]}
        paragens = [ponto_dep] + paragens + [dict(ponto_dep, sequence=999)]

    # Energia (so no modo com energia)
    capacidade = carga = None
    if com_energia:
        capacidade = veiculo["battery_capacity"]
        carga = veiculo["current_charge"]
        if carga is None:
            carga = capacidade
            print(f"AVISO: carga atual a NULL - assumida cheia ({capacidade:.2f} kWh).")

    total_tarefas = sum(1 for p in paragens if p["task_id"] != "DEPOSITO")
    inclui_dep = any(p["task_id"] == "DEPOSITO" for p in paragens)

    print("=" * 68)
    print(f" SIMULACAO DE ROTA {titulo}")
    if com_energia:
        print(f" Mota: {veiculo['name']}  |  Capacidade: {capacidade:.2f} kWh"
              f"  |  Carga inicial: {carga:.2f} kWh ({100*carga/capacidade:.1f}%)")
    else:
        print(f" Mota: {veiculo['name'] if veiculo else '(n/d)'}  |  MODO SEM ENERGIA")
    print(f" Entregas: {total_tarefas}  |  Aceleracao: {aceleracao}x"
          f"  |  Reporte cada {intervalo_s}s  |  Deposito: {'sim' if inclui_dep else 'nao'}")
    if com_energia:
        print(f" Escrita na BD: {'SIM' if escrever_bd else 'NAO (modo seco)'}")
    print("=" * 68)

    # CSV - cabecalho depende do modo
    csv_file = open(csv_path, "w", newline="", encoding="utf-8")
    writer = csv.writer(csv_file)
    if com_energia:
        writer.writerow(["timestamp", "tempo_sim_s", "troco", "tarefas_concluidas",
                         "lat", "lon", "bateria_kwh", "bateria_pct", "km_percorridos"])
    else:
        writer.writerow(["timestamp", "tempo_sim_s", "troco", "tarefas_concluidas",
                         "lat", "lon", "km_percorridos"])

    estado = {"carga": carga, "km_total": 0.0, "concluidas": 0, "tempo_sim": 0.0}

    def reportar(troco_label, lat, lon, km_total):
        if com_energia:
            pct = 100 * estado["carga"] / capacidade if capacidade else 0
            print(f"[t+{estado['tempo_sim']:6.1f}s] {troco_label:<26} | "
                  f"entregas: {estado['concluidas']:2d}/{total_tarefas} | "
                  f"pos: ({lat:.5f}, {lon:.5f}) | "
                  f"bateria: {estado['carga']:5.2f} kWh ({pct:5.1f}%) | {km_total:5.2f} km")
            writer.writerow([datetime.now().isoformat(timespec="seconds"),
                            round(estado["tempo_sim"], 1), troco_label, estado["concluidas"],
                            round(lat, 6), round(lon, 6),
                            round(estado["carga"], 4), round(pct, 1), round(km_total, 3)])
        else:
            print(f"[t+{estado['tempo_sim']:6.1f}s] {troco_label:<26} | "
                  f"entregas: {estado['concluidas']:2d}/{total_tarefas} | "
                  f"pos: ({lat:.5f}, {lon:.5f}) | {km_total:5.2f} km")
            writer.writerow([datetime.now().isoformat(timespec="seconds"),
                            round(estado["tempo_sim"], 1), troco_label, estado["concluidas"],
                            round(lat, 6), round(lon, 6), round(km_total, 3)])
        csv_file.flush()

    if com_energia and escrever_bd:
        atualizar_status(conn, veiculo["vehicle_id"], "busy")

    pos = paragens[0]
    label_inicio = "SAIDA DO DEPOSITO" if pos["task_id"] == "DEPOSITO" else "INICIO"
    reportar(label_inicio, pos["lat"], pos["lon"], estado["km_total"])
    if pos["task_id"] != "DEPOSITO":
        estado["concluidas"] = 1

    # Percorre os trocos
    for i in range(len(paragens) - 1):
        origem, destino = paragens[i], paragens[i + 1]
        dist_km = road_distance_km(origem["lat"], origem["lon"],
                                   destino["lat"], destino["lon"])
        tempo_real_s = (dist_km / SPEED_KPH) * 60.0 * 60.0
        tempo_sim_troco = tempo_real_s / aceleracao
        o_label = "Deposito" if origem["task_id"] == "DEPOSITO" else str(origem["task_id"])
        d_label = "Deposito" if destino["task_id"] == "DEPOSITO" else str(destino["task_id"])
        troco_label = f"{o_label} -> {d_label}"
        energia_troco = dist_km * CONSUMPTION_KWH_PER_KM if com_energia else 0.0

        decorrido = 0.0
        while decorrido < tempo_sim_troco:
            passo = min(intervalo_s, tempo_sim_troco - decorrido)
            time.sleep(passo)
            decorrido += passo
            estado["tempo_sim"] += passo
            frac = decorrido / tempo_sim_troco if tempo_sim_troco > 0 else 1.0
            lat = origem["lat"] + (destino["lat"] - origem["lat"]) * frac
            lon = origem["lon"] + (destino["lon"] - origem["lon"]) * frac
            if com_energia:
                estado["carga"] = max(0.0, estado["carga"]
                                      - energia_troco * (passo / tempo_sim_troco)) \
                    if tempo_sim_troco > 0 else estado["carga"] - energia_troco
            km_atual = estado["km_total"] + dist_km * frac
            reportar(f"{troco_label} ({frac*100:3.0f}%)", lat, lon, km_atual)
            if com_energia and escrever_bd:
                atualizar_carga(conn, veiculo["vehicle_id"], estado["carga"])

        estado["km_total"] += dist_km
        if destino["task_id"] == "DEPOSITO":
            reportar("REGRESSO AO DEPOSITO", destino["lat"], destino["lon"], estado["km_total"])
        else:
            estado["concluidas"] += 1
            reportar(f"ENTREGUE: tarefa {destino['task_id']}",
                     destino["lat"], destino["lon"], estado["km_total"])

    if com_energia and escrever_bd:
        atualizar_status(conn, veiculo["vehicle_id"], "available")
        atualizar_carga(conn, veiculo["vehicle_id"], estado["carga"])

    print("=" * 68)
    print(" ROTA CONCLUIDA")
    print(f" Entregas: {estado['concluidas']}/{total_tarefas}  |  "
          f"Distancia total: {estado['km_total']:.2f} km")
    if com_energia:
        print(f" Bateria final: {estado['carga']:.2f} kWh "
              f"({100*estado['carga']/capacidade:.1f}%)  |  "
              f"Consumido: {capacidade - estado['carga']:.2f} kWh")
    print(f" CSV guardado em: {csv_path}")
    print("=" * 68)
    csv_file.close()


# ----------------------------------------------------------------------------
# MAIN
# ----------------------------------------------------------------------------
def main():
    parser = argparse.ArgumentParser(
        description="Simula uma mota a percorrer uma rota gravada na BD.")
    # Comuns
    parser.add_argument("--aceleracao", type=float, default=60.0,
                        help="Fator de aceleracao do tempo. Default: 60")
    parser.add_argument("--intervalo", type=float, default=5.0,
                        help="Intervalo de reporte em segundos. Default: 5")
    parser.add_argument("--csv", type=str, default=None, help="Caminho do CSV de saida.")
    parser.add_argument("--deposito", type=int, default=None,
                        help="Node ID do deposito (saida/regresso). Opcional.")
    # Modo com energia (Supabase)
    parser.add_argument("--plan", type=int, default=None,
                        help="[modo com energia] ID do plano na Supabase.")
    parser.add_argument("--sem-escrita-bd", action="store_true",
                        help="[modo com energia] nao escreve carga na BD.")
    # Modo sem energia (equipa)
    parser.add_argument("--sem-energia", action="store_true",
                        help="Modo sem energia: le a rota da LocationNodeTask (BD da equipa).")
    parser.add_argument("--route-group", type=str, default=None,
                        help="[modo sem energia] RouteGroupId da rota a simular.")
    parser.add_argument("--db-host", default="localhost")
    parser.add_argument("--db-port", default="5435")
    parser.add_argument("--db-name", default="amover-data")
    parser.add_argument("--db-user", default="postgres")
    parser.add_argument("--db-pass", default="Fulgora2000")
    args = parser.parse_args()

    if args.sem_energia:
        # ---- MODO SEM ENERGIA (BD da equipa) ----
        if not args.route_group:
            print("ERRO: no modo --sem-energia tens de indicar --route-group <id>.")
            sys.exit(1)
        conn = get_connection_equipa(args)
        try:
            info_mota, paragens = carregar_rota_equipa(conn, args.route_group)
            deposito = carregar_deposito(conn, args.deposito) if args.deposito else None
            csv_path = args.csv or f"simulacao_rota_{args.route_group[:8]}.csv"
            simular(conn, paragens, args.aceleracao, args.intervalo, csv_path,
                    com_energia=False, veiculo=info_mota, deposito=deposito,
                    titulo=f"(sem energia) - grupo {args.route_group[:8]}")
        finally:
            conn.close()
    else:
        # ---- MODO COM ENERGIA (Supabase) ----
        if not args.plan:
            print("ERRO: no modo com energia tens de indicar --plan <id>.")
            print("(ou usa --sem-energia --route-group <id> para a BD da equipa.)")
            sys.exit(1)
        conn = get_connection_supabase()
        try:
            veiculo, paragens = carregar_plano_supabase(conn, args.plan)
            dep_id = args.deposito if args.deposito is not None else 8
            deposito = carregar_deposito(conn, dep_id)
            csv_path = args.csv or f"simulacao_plano_{args.plan}.csv"
            simular(conn, paragens, args.aceleracao, args.intervalo, csv_path,
                    com_energia=True, veiculo=veiculo,
                    escrever_bd=not args.sem_escrita_bd, deposito=deposito,
                    titulo=f"- Plano #{args.plan}")
        finally:
            conn.close()


if __name__ == "__main__":
    main()
