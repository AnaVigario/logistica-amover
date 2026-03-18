package com.example.myamover.screens.maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.model.RouteJson
import com.example.myamover.data.network.RoutesApiClient
import com.example.myamover.data.network.computePolylineChunked
import com.example.myamover.location.getLastKnownLatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class MapUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val myLocation: LatLng? = null,
    val routePolyline: List<LatLng> = emptyList(),
    val markers: List<LatLng> = emptyList()
)

class MapRouteViewModel(
    app: Application,
    private val routesApi: RoutesApiClient
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(MapUiState())
    val ui: StateFlow<MapUiState> = _ui

//    fun ensureMyLocation() {
//        viewModelScope.launch {
//            if (_ui.value.myLocation != null) return@launch
//            val me = getLastKnownLatLng(getApplication())
//            _ui.value = _ui.value.copy(myLocation = me)
//        }
//    }

    private fun distanceMeters(a: LatLng, b: LatLng): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)

        val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        return 2 * R * asin(sqrt(h))
    }

    fun routeToSingleStop(dest: LatLng) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null, routePolyline = emptyList())

            val me = _ui.value.myLocation ?: getLastKnownLatLng(getApplication())
            if (me == null) {
                _ui.value = _ui.value.copy(loading = false, error = "Sem localização atual (liga o GPS).")
                return@launch
            }
            _ui.value = _ui.value.copy(myLocation = me)

            runCatching {
                computePolylineChunked(routesApi, listOf(me, dest))
            }.onSuccess { poly ->
                _ui.value = _ui.value.copy(
                    loading = false,
                    routePolyline = poly,
                    markers = listOf(me, dest)
                )
            }.onFailure { e ->
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Erro a calcular rota")
            }
        }
    }

    fun routeFullFromJson(route: RouteJson) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null, routePolyline = emptyList())
//            val me = _ui.value.myLocation ?: getLastKnownLatLng(getApplication())
//            if (me == null) {
//                _ui.value = _ui.value.copy(loading = false, error = "Sem localização atual (liga o GPS).")
//                return@launch
//            }
//            _ui.value = _ui.value.copy(myLocation = me)

            runCatching {
                val pts = route.nodes.map { n ->
                    // ✅ Ajusta aqui se o teu RouteJson.Node não tiver x/y
                    LatLng(n.y, n.x) // y=lat, x=lng
                }
                val poly = computePolylineChunked(routesApi, pts)
                Pair(poly, pts)
            }.onSuccess { (poly, pts) ->
                _ui.value = _ui.value.copy(
                    loading = false,
                    routePolyline = poly,
                    markers = pts
                )
            }.onFailure { e ->
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Erro a calcular rota")
            }
        }
    }


    fun routeFullFromJsonStartingAt(route: RouteJson, start: LatLng) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null, routePolyline = emptyList())
            val me = _ui.value.myLocation ?: getLastKnownLatLng(getApplication())
            if (me == null) {
                _ui.value = _ui.value.copy(loading = false, error = "Sem localização atual (liga o GPS).")
                return@launch
            }
            _ui.value = _ui.value.copy(myLocation = me)

            runCatching {
                // 1) converter nós do JSON para LatLng
                val stops = route.nodes.map { n ->
                    LatLng(n.y, n.x) // y=lat, x=lng
                }

                if (stops.isEmpty()) {
                    return@runCatching Triple(emptyList<LatLng>(), listOf(start), start)
                }

                // 2) montar lista que começa SEMPRE no "start"
                // (se o primeiro stop for praticamente igual ao start, não duplica)
                val points = if (distanceMeters(start, stops.first()) < 30.0) {
                    listOf(start) + stops.drop(1)
                } else {
                    listOf(start) + stops
                }

                // 3) calcular polyline (rota em estradas) em chunks
                val poly = computePolylineChunked(routesApi, points)

                // 4) devolver polyline + markers
                Triple(poly, points, start)
            }.onSuccess { (poly, points, startUsed) ->
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = null,
                    myLocation = startUsed,
                    routePolyline = poly,
                    markers = points
                )
            }.onFailure { e ->
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Erro a calcular rota"
                )
            }
        }
    }

}