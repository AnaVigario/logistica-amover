package com.example.myamover.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nif: String,
    val address: String,
    val phone: String,
    val email: String,
) {
}