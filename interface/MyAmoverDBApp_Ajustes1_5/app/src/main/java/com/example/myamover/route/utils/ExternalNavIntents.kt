package com.example.myamover.route.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.myamover.data.model.RouteJson
import com.example.myamover.data.model.RouteNode


/**
 * Abre Google Maps externo com rota COMPLETA:
 * origin + destination + waypoints (intermédios).
 *
 * Nota: com muitos waypoints o Google pode ignorar/limitar.
 * Mesmo assim abre e podes navegar.
 */

fun openGoogleMapsToStop(context: Context, lat: Double, lng: Double) {
    val navUri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
    val intent = Intent(Intent.ACTION_VIEW, navUri).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // fallback browser
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=driving")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}


/**
 * Waze: mostra apenas 1 destino (próxima paragem).
 * Fallback: abre link universal no browser se Waze não estiver instalado.
 */


fun openWazeToStop(context: Context, lat: Double, lng: Double) {

    val wazeAppUri = Uri.parse("waze://?ll=lat,lng&navigate=yes")
    val wazeIntent = Intent(Intent.ACTION_VIEW, wazeAppUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (wazeIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(wazeIntent)
    } else {
        val web = Uri.parse("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
        context.startActivity(
            Intent(Intent.ACTION_VIEW, web).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

/** Próxima paragem (ignora depot). */

fun nextStop(route: RouteJson, currentStopIndex: Int): RouteNode? {
    if (route.nodes.isEmpty()) return null


    val start = currentStopIndex.coerceAtLeast(1).coerceAtMost(route.nodes.lastIndex)

    for(i in start..route.nodes.lastIndex){
        val n = route.nodes[i]
        val isDepot = (n.isDepot == true) || (n.type == "depot")
        if(!isDepot)
            return n
    }
    return null
}