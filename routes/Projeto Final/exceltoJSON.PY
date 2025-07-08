import os
import json
import pandas as pd

# ---------- Função que converte UM cenário ----------
def formato_json(cenario_row, nodes_df):
    """Recebe uma linha da sheet 'cenarios' e devolve o dicionário JSON."""
    cid = cenario_row["id"]
    nodes = nodes_df[nodes_df["scenario_id"] == cid]

    return {
        "algoritmo":      cenario_row["algoritmo"],
        "vehicles": [{
            "capacity":     int(cenario_row["capacidade"]),
            "battery_kwh":  float(cenario_row["bateria_kwh"])
        }],
        "nodes": [
            {
                "id":            int(row["id"]),
                "x":             float(row["x"]),
                "y":             float(row["y"]),
                "demand":        int(row["demand"]),
                # Usa string vazia se não houver janela
                "janela_inicio": str(row.get("janela_inicio", "")) or "",
                "janela_fim":    str(row.get("janela_fim", ""))    or ""
            }
            for _, row in nodes.iterrows()
        ]
    }

# ---------- Programa principal ----------
if __name__ == "__main__":
    EXCEL_FILE  = "cenarios.xlsx"
    OUTPUT_DIR  = "JSONS"

    # Cria a pasta de saída se não existir
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Lê as duas sheets
    cenarios_df = pd.read_excel(EXCEL_FILE, sheet_name="cenarios")
    nodes_df    = pd.read_excel(EXCEL_FILE, sheet_name="nodes")

    # Loop por todos os cenários
    for _, cenario in cenarios_df.iterrows():
        cid        = int(cenario["id"])
        json_data  = formato_json(cenario, nodes_df)
        out_path   = os.path.join(OUTPUT_DIR, f"cenario{cid}.json")

        with open(out_path, "w", encoding="utf-8") as f:
            json.dump(json_data, f, indent=4, ensure_ascii=False)

        print(f"✔ Gerado: {out_path}")

    print("\n✅ Todos os cenários convertidos com sucesso.")
