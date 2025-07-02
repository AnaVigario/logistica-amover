
import os
import subprocess

# Diretório com todos os ficheiros JSON
json_dir = "json_algoritmos_completos"
output_dir = "resultados"
os.makedirs(output_dir, exist_ok=True)

# Mapeamento de algoritmos para ficheiros python
algos_scripts = {
    "dijkstra": "dijkstra.py",
    "nearest_neighbor": "nearest_neighbor.py",
    "savings": "savings.py",
    "tabu_search": "tabu_search.py",
    "branch_and_bound": "branch_and_bound.py",
    "grasp": "grasp.py"
}

# Lista todos os ficheiros JSON
for file in os.listdir(json_dir):
    if file.endswith(".json"):
        algo = file.split("_")[-1].replace(".json", "")
        script = algos_scripts.get(algo)
        if script:
            print(f"Executando {file} com {script}...")
            try:
                result = subprocess.check_output(["python", script, os.path.join(json_dir, file)], text=True)
                output_file = os.path.join(output_dir, file.replace(".json", ".txt"))
                with open(output_file, "w") as f:
                    f.write(result)
            except subprocess.CalledProcessError as e:
                print(f"Erro ao executar {file}: {e}")
