package com.example.myamover.screens.maps

sealed class MapMode {
    data class ToSingleStop(val lat: Double, val lng: Double) : MapMode()
    data object FullRoute : MapMode()
}