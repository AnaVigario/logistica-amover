package com.example.myamover.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("MissingPermission")
fun locationUpdatesFlow(
    context: Context,
    intervalMs: Long = 3000L
) = callbackFlow<LatLng> {

    val client = LocationServices.getFusedLocationProviderClient(context)

    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
        .setMinUpdateIntervalMillis(1500L)
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            trySend(LatLng(loc.latitude, loc.longitude))
        }
    }

    client.requestLocationUpdates(request, callback, Looper.getMainLooper())

    awaitClose { client.removeLocationUpdates(callback) }
}
