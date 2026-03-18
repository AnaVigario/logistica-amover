package com.example.myamover.route.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun opendWazeFromCurrentTo(context: Context, destlat: Double, destlng: Double) {
    //waze usa a localização atual como origem
    val wazeAppUri = Uri.parse("waze://?ll=lat,lng&navigate=yes")
    val wazeIntent = Intent(Intent.ACTION_VIEW, wazeAppUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (wazeIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(wazeIntent)
    } else {
        val web = Uri.parse("https://waze.com/ul?ll=$destlat,$destlng&navigate=yes")
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                web
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

}