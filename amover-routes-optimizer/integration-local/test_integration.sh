#!/usr/bin/env bash
# =====================================================================
# Teste rapido da integracao local (stack tem de estar a correr).
#
# Uso:
#   ./test_integration.sh <user> <password> [vehicleId] [taskIds_csv]
# Exemplo:
#   ./test_integration.sh testuser MinhaPass123 1 1,2,3
#
# Faz, por ordem:
#   1. /health do teu otimizador (chamada direta, sem passar pelo backend)
#   2. /optimize do teu otimizador no formato legado (= o que o .NET envia)
#   3. Pede um token ao Keycloak (client publico amover-api)
#   4. Chama o backend .NET POST /api/route/optimize-for-vehicle
#   5. Le a rota gravada via GET /api/route
# =====================================================================
set -euo pipefail

KC=http://localhost:8080
API=http://localhost:5029
OPT=http://localhost:5000
REALM=amover-realm
CLIENT=amover-api

USER="${1:?Uso: ./test_integration.sh <user> <password> [vehicleId] [taskIds_csv]}"
PASS="${2:?Falta a password}"
VEHICLE="${3:-1}"
TASKS="${4:-1,2,3}"
TODAY="$(date +%Y-%m-%d)"
TASK_JSON="[${TASKS}]"

echo "==> 1) Health do otimizador"
curl -fsS "$OPT/health"; echo

echo "==> 2) /optimize direto (formato legado .NET: nodes/vehicles)"
curl -fsS -X POST "$OPT/optimize" -H "Content-Type: application/json" -d '{
  "nodes": [
    {"id":0,"x":41.5454,"y":-8.4265,"demand":0},
    {"id":101,"x":41.5510,"y":-8.4200,"demand":1},
    {"id":102,"x":41.5380,"y":-8.4310,"demand":1}
  ],
  "vehicles":[{"capacity":9999,"battery_kwh":9999.0}]
}' | python3 -m json.tool
echo

echo "==> 3) Token Keycloak (grant=password, client publico $CLIENT)"
TOKEN=$(curl -fsS -X POST "$KC/realms/$REALM/protocol/openid-connect/token" \
  -d "client_id=$CLIENT" -d "grant_type=password" \
  -d "username=$USER" -d "password=$PASS" \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['access_token'])")
echo "token OK (${#TOKEN} chars)"

echo "==> 4) Backend: POST /api/route/optimize-for-vehicle (vehicle=$VEHICLE tasks=$TASK_JSON)"
curl -fsS -X POST "$API/api/route/optimize-for-vehicle" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"vehicleId\":$VEHICLE,\"date\":\"$TODAY\",\"taskIds\":$TASK_JSON}"; echo

echo "==> 5) Backend: GET /api/route (rota gravada)"
curl -fsS "$API/api/route?vehicleId=$VEHICLE&date=$TODAY" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

echo
echo "Se chegaste aqui sem erros, a integracao .NET -> otimizador -> BD esta a funcionar."
