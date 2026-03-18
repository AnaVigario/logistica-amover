package com.example.myamover.screens.maps

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myamover.data.network.RoutesApiClient

class MapRouteVmFactory(
    private val app: Application,
    private val apiKey: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val client = RoutesApiClient(apiKey)
        @Suppress("UNCHECKED_CAST")
        return MapRouteViewModel(app, client) as T
    }
}