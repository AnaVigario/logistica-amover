import subprocess
import os
import time

# Caminho base onde os scripts estão localizados
base_path = os.path.dirname(os.path.abspath(__file__))

# Lista de scripts de algoritmos
scripts = [
    "Dijkstra.py",
    "BranchandBound.py",
    "Savings.py",
    "NearestNeighbor.py",
    "TabuSearch.py",
    "GRASP.PY"
]

# Função para executar um script e capturar a saída e tempo de execução
def run_script(script_name):
    script_path = os.path.join(base_path, script_name)
    print(f"\nExecutando {script_name}...")
    start_time = time.time()
    try:
        result = subprocess.run(["python", script_path], capture_output=True, text=True)
        end_time = time.time()
        execution_time = end_time - start_time
        print(result.stdout)
        if result.stderr:
            print(f"Erros em {script_name}:\n{result.stderr}")
        print(f"Tempo de execução de {script_name}: {execution_time:.2f} segundos")
    except Exception as e:
        print(f"Erro ao executar {script_name}: {e}")

# Executar todos os scripts
for script in scripts:
    run_script(script)

print("\nTodos os algoritmos foram executados.")
