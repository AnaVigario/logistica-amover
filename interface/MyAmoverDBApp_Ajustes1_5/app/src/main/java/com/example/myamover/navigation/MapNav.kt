package com.example.myamover.navigation

object MapNav {
    // 1 destino: rota da minha localização até ao serviço
    const val ToStop = "map/toStop/{lat}/{lng}"
    fun toStop(lat: Double, lng: Double) = "map/toStop/$lat/$lng"

    // rota completa: busca no VM partilhado (não passamos lista gigante no route)
    const val FullRoute = "map/fullRoute"
}