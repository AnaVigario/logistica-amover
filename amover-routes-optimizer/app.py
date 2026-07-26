"""
app.py - Microservico Flask: o Motor de Otimizacao de Rotas do A-MoVeR.
Stateless. O backend .NET chama POST /optimize com as coordenadas das tarefas
do dia + restricoes da mota; este servico devolve a ordem otima e as metricas.
Tambem expoe POST /geocode: converte codigo postal/morada em lat/lon reais.
Nao acede a base de dados - quem grava a rota e notifica a app e o .NET.
"""
import os
import logging
from flask import Flask, request, jsonify
from optimizer import run_optimization, ValidationError
from geocoding import geocode
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
log = logging.getLogger("amover-routes")
app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify({"status": "ok", "service": "amover-routes-optimizer"})


@app.post("/optimize")
def optimize():
    data = request.get_json(silent=True)
    if data is None:
        return jsonify({"ok": False, "error": "Corpo JSON invalido ou em falta."}), 400
    try:
        result = run_optimization(data)
        return jsonify(result), 200
    except ValidationError as e:
        return jsonify({"ok": False, "error": str(e)}), 400
    except Exception:
        log.exception("Erro inesperado no /optimize")
        return jsonify({"ok": False, "error": "Erro interno ao otimizar a rota."}), 500


@app.post("/geocode")
def geocode_endpoint():
    """Converte codigo postal / morada em coordenadas (lat/lon)."""
    data = request.get_json(silent=True) or {}
    result = geocode(
        postal_code=data.get("postal_code"),
        street=data.get("street"),
        city=data.get("city"),
        door_number=data.get("door_number"),
    )
    return jsonify(result), (200 if result.get("ok") else 404)


if __name__ == "__main__":
    port = int(os.environ.get("PORT", "5000"))
    app.run(host="0.0.0.0", port=port, debug=False)
