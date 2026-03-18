package com.example.myamover.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CourierRemote(
    val id: Int?,
    val name: String,
    @SerialName("UUID")
    val uuid: String
)