#!/usr/bin/env python3
"""interface_algoritmos.py – versão 2.2
================================================

🔑 NOVO • Suporte pleno a campos opcionais no Excel/JSON
-------------------------------------------------------
Esta revisão resolve o problema "Nenhum depósito encontrado" no
`savings.py` porque passa agora o campo **is_depot** (e outros)
directamente do Excel para o JSON.

✔ Detecta depósito mesmo que a coluna *is_depot* não exista – assume
  True quando `id == 0 and demand == 0`.
✔ Copia colunas opcionais *is_depot*, *is_charging_station* e
  *charger_power_kw* para cada nó, caso existam na folha *nodes*.
✔ Restante funcionalidade (auto‑build, menu, batch) mantém‑se.
"""
from __future__ import annotations

import json
import os
import re
import subprocess
from pathlib import Path
from typing import Dict, List, Tuple

import pandas as pd

# -----------------------------------------------------------------------------
# Configuração
# -----------------------------------------------------------------------------
EXCEL_FILE = "cenarios.xlsx"
SHEET_CENARIOS = "cenarios"
SHEET_NODES = "nodes"
OUTPUT_DIR = Path("JSONS")
OUTPUT_DIR.mkdir(exist_ok=True)

# Association: algoritmo  ➜  script Python
ALGORITMOS: Dict[str, str] = {
    "tabu_search": "algoritmos/tabu_search.py",
    "grasp": "algoritmos/grasp.py",
    "dijkstra": "algoritmos/dijkstra.py",
    "savings": "algoritmos/savings.py",
    "branch_and_bound": "algoritmos/branch_and_bound.py",
    "nearest_neighbor": "algoritmos/nearest_neighbor.py",
}

# -----------------------------------------------------------------------------
# Utilitários
# -----------------------------------------------------------------------------

def carregar_dados() -> Tuple[pd.DataFrame, pd.DataFrame]:
    cenarios_df = pd.read_excel(EXCEL_FILE, sheet_name=SHEET_CENARIOS)
    nodes_df = pd.read_excel(EXCEL_FILE, sheet_name=SHEET_NODES)
    return cenarios_df, nodes_df


def guarda_json(json_data: dict, path: Path) -> None:
    path.write_text(json.dumps(json_data, indent=4, ensure_ascii=False), encoding="utf-8")


def _build_node_dict(row: pd.Series, extra_cols: List[str]) -> dict:
    d = {
        "id": int(row["id"]),
        "x": float(row["x"]),
        "y": float(row["y"]),
        "demand": int(row["demand"]),
        "janela_inicio": str(row.get("janela_inicio", "")) or "",
        "janela_fim": str(row.get("janela_fim", "")) or "",
    }
    for col in extra_cols:
        if col in row and pd.notna(row[col]):
            # Mantém tipos correctos: bool para *_depot / *_station, float para kW
            val = row[col]
            if isinstance(val, str) and val.upper() in {"TRUE", "FALSE"}:
                val = val.upper() == "TRUE"
            d[col] = val
    return d


def gerar_json(cenario_row: pd.Series, nodes_df: pd.DataFrame) -> Path:
    cid = int(cenario_row["id"])
    nodes = nodes_df[nodes_df["scenario_id"] == cid]

    # Quais colunas extra existem?
    extra_cols = [c for c in ["is_depot", "is_charging_station", "charger_power_kw"] if c in nodes.columns]

    node_dicts = [_build_node_dict(r, extra_cols) for _, r in nodes.iterrows()]

    # Se nenhum nó tiver is_depot=True, marca o (id==0 & demand==0) como depósito implícito
    if not any(n.get("is_depot", False) for n in node_dicts):
        for n in node_dicts:
            if n["id"] == 0 and n["demand"] == 0:
                n["is_depot"] = True
                break

    json_data = {
        "algoritmo": str(cenario_row["algoritmo"]),
        "vehicles": [
            {
                "capacity": int(cenario_row["capacidade"]),
                "battery_kwh": float(cenario_row["bateria_kwh"]),
            }
        ],
        "nodes": node_dicts,
    }

    out_path = OUTPUT_DIR / f"cenario{cid}.json"
    guarda_json(json_data, out_path)
    return out_path


def construir_todos_jsons() -> List[Path]:
    cenarios_df, nodes_df = carregar_dados()
    paths: List[Path] = []
    for _, c in cenarios_df.iterrows():
        paths.append(gerar_json(c, nodes_df))
    return paths


def encontrar_script(path_json: Path) -> str | None:
    try:
        algoritmo = json.loads(path_json.read_text("utf-8"))["algoritmo"]
    except Exception as exc:
        print(f"⚠️  Erro a ler {path_json.name}: {exc}")
        return None
    script = ALGORITMOS.get(algoritmo)
    if script and Path(script).exists():
        return script
    print(f"❌ Script para algoritmo '{algoritmo}' não encontrado.")
    return None


def correr_algoritmo(script: str, file_json: Path) -> None:
    print(f"🚀  {script}  ←  {file_json.name}")
    subprocess.run(["python", script, str(file_json)], check=False)

# -----------------------------------------------------------------------------
# Menu interactivo
# -----------------------------------------------------------------------------

def _extrair_id_do_nome(stem: str) -> int | None:
    m = re.search(r"(\d+)", stem)
    return int(m.group(1)) if m else None


def menu() -> None:
    cenarios_df, nodes_df = carregar_dados()
    while True:
        print("\n=== Menu – Escolha o cenário a correr ===")
        for _, row in cenarios_df.iterrows():
            print(f"{row['id']:>2}  –  {row.get('nome', 'SemNome')}  [{row['algoritmo']}]")

        extra_jsons: List[Path] = []
        for p in OUTPUT_DIR.glob("*.json"):
            cid = _extrair_id_do_nome(p.stem)
            if cid is not None and (cenarios_df[cenarios_df['id'] == cid]).any().any():
                continue
            extra_jsons.append(p)

        if extra_jsons:
            print("-- Ficheiros JSON independentes --")
            for j in extra_jsons:
                print(f"{j.stem}")

        print("0  –  Sair")
        escolha = input("Digite o ID ou nome do ficheiro: ").strip()
        if escolha == "0":
            break

        if escolha.isdigit() and (cenarios_df['id'] == int(escolha)).any():
            row = cenarios_df[cenarios_df['id'] == int(escolha)].iloc[0]
            json_path = gerar_json(row, nodes_df)
            script = encontrar_script(json_path)
            if script:
                correr_algoritmo(script, json_path)
            continue

        file_candidate = OUTPUT_DIR / f"{escolha}.json"
        if file_candidate.exists():
            script = encontrar_script(file_candidate)
            if script:
                correr_algoritmo(script, file_candidate)
        else:
            print("❌ Entrada não reconhecida.")

# -----------------------------------------------------------------------------
# Execução directa
# -----------------------------------------------------------------------------
if __name__ == "__main__":
    #construir_todos_jsons()  # garante que JSONs estão actualizados
    menu()
