package com.example.myamover.route.utils

import com.example.myamover.route.VrptwPayload
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


/**
 * Converte um JSON de rota (VrptwPayload)
 * numa lista ordenada de pontos LatLng para o Google Maps.
 *
 * Esta função é usada para:
 * - desenhar a rota no mapa
 * - criar polylines
 * - visualizar a sequência de paragens
 *
 * @param json JSON da rota (string)
 * @param includeStations
 *        true  -> inclui estações de carregamento como paragens
 *        false -> ignora estações de carregamento
 * @param returnDepot
 *        true  -> adiciona o depósito no final da rota
 *        false -> termina na última paragem
 *
 * @return Lista ordenada de LatLng (latitude, longitude)
 */
fun parseRoutePoints(
    json: String,
    includeStations: Boolean = false,
    returnDepot: Boolean = false
): List<LatLng> {

    //Desserializa o JSON para o modelo VrptwPayload
    val payload = Gson().fromJson(json, VrptwPayload::class.java)

    // Lista de nós da rota. Se for null, usa lista vazia para evitar crashes
    val nodes = payload?.nodes ?: emptyList()

    // Se não existirem nós, não há rota para desenhar
    if (nodes.isEmpty()) return emptyList()


    /**
     * 2️. Identificar o depósito (origem da rota).
     *
     * Regra:
     * - usa o primeiro nó com is_depot = true
     * - se não existir, usa o primeiro nó da lista
     */

    val depot =
        nodes.firstOrNull { it.is_depot == true }
            ?: nodes.first()


    /**
     * 3️. Filtrar paragens intermédias.
     *
     * Regras:
     * - ignora o depósito
     * - ignora estações de carregamento se includeStations = false
     */
    val stops = nodes.filter {
        when {
            it.is_depot == true -> false
            it.is_charging_station == true && !includeStations -> false
            else -> true
        }
    }.toMutableList()


    /**
     * 4️. Se configurado, adiciona o depósito no final
     * para simular regresso ao ponto inicial.
     */
    if (returnDepot) stops.add(depot)


    /**
     * 5️. Ordem final da rota:
     * - começa sempre no depósito
     * - seguido das paragens filtradas
     */
    val ordered = listOf(depot) + stops


    /**
     * 6️ Conversão final para LatLng (Google Maps).
     *
     * - it.y = latitude
     * - it.x = longitude
     */
    return ordered.map { LatLng(it.y, it.x) } // y=lat, x=lon
}

/*+
Sugestões futuras (opcionais)

1️. Evitar Gson repetido
val gson = Gson() // singleton


2️. Validar coordenadas
if (it.x == null || it.y == null) skip

 */