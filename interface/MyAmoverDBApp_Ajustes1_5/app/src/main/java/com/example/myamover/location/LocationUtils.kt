package com.example.myamover.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
suspend fun getLastKnownLatLng(context: Context): LatLng? =
    suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { loc ->
                if (!cont.isActive) return@addOnSuccessListener
                cont.resume(loc?.let { LatLng(it.latitude, it.longitude) })
            }
            .addOnFailureListener {
                if (!cont.isActive) return@addOnFailureListener
                cont.resume(null)
            }
    }
