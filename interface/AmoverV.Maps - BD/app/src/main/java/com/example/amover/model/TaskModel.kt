package com.example.amover.model

data class TaskModel(
    val id: Int= 0,
    val type: String = "",
    val address: String = "",
    val name: String = "",
    val contacto: String = "",
    val status: String = "",
    val timewindow: String = "",
    val timerTask: String = "",
    val dateTask: String = "", //formato: yyyy-MM-dd
    val note: String = "",
    val image: String? = null,
    val latitude: Double = 0.0,
    val longitude:  Double = 0.0
) {
    override fun toString(): String {
        return "TaskModel(id=$id, type='$type', address='$address', name='$name', status='$status', timewindow='$timewindow', timerTask='$timerTask', dateTask='$dateTask', note='$note', image='$image', latitude='$latitude', longitude='$longitude')"

    }
}