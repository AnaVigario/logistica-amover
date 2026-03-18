package com.example.myamover.route.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.myamover.route.VrptwNode
import com.example.myamover.route.VrptwPayload
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


/**
 * Função utilitária para codificar strings para URL.
 *
 * Necessário para:
 * - coordenadas
 * - waypoints
 * - evitar erros com caracteres especiais
 */
// encoding URL
private fun enc(s: String): String =
    URLEncoder.encode(s, StandardCharsets.UTF_8.name())


/**
 * Converte um nó da rota num formato "lat,lng"
 * exigido pelo Google Maps.
 *
 * Atenção:
 * - n.y = latitude
 * - n.x = longitude
 */
private fun latLng(n: VrptwNode): String = "${n.y},${n.x}" // y = lat, x = lon

/**
 * Abre a navegação do Google Maps para um único ponto (lat, lng).
 *
 * * Usado normalmente quando:
 *  * - o utilizador quer navegar diretamente para um cliente
 *  * - ou abrir apenas uma paragem isolada
 *  *
 *  * @param context contexto da aplicação
 *  * @param lat latitude do destino
 *  * @param lng longitude do destino
 */
fun startNavigationTo(
    context: Context,
    lat: Double,
    lng: Double
) {
    try {
        // URI de navegação direta do Google Maps
        val uri = Uri.parse("google.navigation:q=$lat,$lng")

        // Intent explícita para o Google Maps
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        //Se o Google Maps não estiver instalado,
        // remove o package para deixar o Android escolher
        if (intent.resolveActivity(context.packageManager) == null) {
            // se não houver app do Maps, tenta sem package
            intent.setPackage(null)
        }

        // Abre a navegação
        context.startActivity(intent)
    } catch (t: Throwable) {
        // Mostra erro ao utilizador
        Toast.makeText(
            context,
            "Falha ao abrir navegação: ${t.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Lê um JSON no formato VrptwPayload e abre a rota no Google Maps:
 * - origem: primeiro nó com is_depot = true (ou o primeiro nó caso não exista)
 * - paragens: nós que não são depósito; podes escolher se queres incluir estações de carregamento
 * - destino: última paragem (ou depósito, se returnToDepot = true)
 *
 *
 *  * Abre uma rota completa no Google Maps a partir de um JSON VrptwPayload.
 *  *
 *  * Este método:
 *  * - lê o JSON da rota
 *  * - identifica o depósito (origem)
 *  * - define paragens intermédias (waypoints)
 *  * - abre a rota no Google Maps
 *  *
 *  * @param context contexto da aplicação
 *  * @param json JSON da rota (VrptwPayload)
 *  * @param includeChargingStationsAsStops
 *  *        true  -> inclui estações de carregamento como paragens
 *  *        false -> ignora estações de carregamento
 *  * @param returnToDepot
 *  *        true  -> volta ao depósito no final
 *  *        false -> termina na última paragem
 *  */

fun startRouteFromNodesJson(
    context: Context,
    json: String,
    includeChargingStationsAsStops: Boolean = true,
    returnToDepot: Boolean = true
) {
    try {

        // Disserializa o JSON para o modelo VrptwPayload
        val payload = Gson().fromJson(json, VrptwPayload::class.java)

        // Lista de nós da rota
        val nodes = payload?.nodes ?: emptyList()

        //Se não houver nós,não há rota
        if (nodes.isEmpty()) {
            Toast.makeText(context, "Rota vazia.", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * 2️.Determinar o depósito (origem).
         *
         * - Usa o primeiro nó com is_depot = true
         * - Caso não exista, usa o primeiro nó da lista
         */
        val depot = nodes.firstOrNull { it.is_depot == true } ?: nodes.first()

        // filtrar paragens
        /**
         * 3️. Filtrar paragens intermédias.
         *
         * Regras:
         * - ignora o depósito
         * - ignora estações de carregamento se configurado
         */
        val stops = nodes.filter { node ->
            when {
                node.is_depot == true -> false
                node.is_charging_station == true && !includeChargingStationsAsStops -> false
                else -> true
            }
        }.toMutableList()

        // se for para voltar ao depósito, adiciona-o no fim como destino
        /**
         * 4️. Se for para voltar ao depósito,
         * adiciona-o como última paragem.
         */
        if (returnToDepot && stops.isNotEmpty()) {
            stops.add(depot)
        }

        //5️. Define origem, destino e waypoints.
        val origin = depot
        val destination = stops.lastOrNull() ?: depot


        //Waypoints são todas as paragens intermédias, exceto o destino final
        val waypoints = if (stops.size > 1) stops.dropLast(1) else emptyList()

        // 6. Codificação das coordenadas para URL
        val originStr = enc(latLng(origin))
        val destStr = enc(latLng(destination))

        //Google Maps permite no máximo 23 waypoints
        val waypointsStr = waypoints
            .take(23) // limite de waypoints do Maps
            .joinToString("|") { latLng(it) }
            .let { if (it.isNotEmpty()) enc(it) else "" }

        //7. Construção da URL do Google Maps
        val url = StringBuilder("https://www.google.com/maps/dir/?api=1")
            .append("&origin=").append(originStr)
            .append("&destination=").append(destStr)
            .append("&travelmode=driving")

        if (waypointsStr.isNotEmpty()) {
            url.append("&waypoints=").append(waypointsStr)
        }

        // 8. Cria intent para abrir a rota no Google Maps
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())).apply {
            setPackage("com.google.android.apps.maps")
        }

        //Fallback se o Maps não estiver instalado
        if (intent.resolveActivity(context.packageManager) == null) {
            intent.setPackage(null)
        }

        // Abre  o Google Maps com a rota
        context.startActivity(intent)

    } catch (t: Throwable) {
        //Erro genérico ao abrir rota
        Toast.makeText(
            context,
            "Falha ao abrir rota: ${t.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/*
Diferença entre origem, paragens e destino

✔ Como o JSON da rota é transformado em navegação real
✔ Limite de 23 waypoints do Google Maps
✔ Como lidar com ausência da app Google Maps
✔ Onde controlar:
estações de carregamento
retorno ao depósito


 */
