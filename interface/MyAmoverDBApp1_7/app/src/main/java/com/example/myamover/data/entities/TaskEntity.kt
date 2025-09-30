package com.example.myamover.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

// Define a entidade "tasks" para o Room.
// Essa classe representa a tabela no banco de dados SQLite.
@Entity(tableName = "tasks")


data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val creation_date: Long,
    val deadline: Long,
    val time_window: String,
    val description: String,
    val status: String,
    val pickup_location: String,
    val delivery_location: String,
    val longitude: Double,
    val latitude: Double,
    val priority: Priority = Priority.MEDIUM,

    val client_id: Long = 0,  //   demo, deve conter um maneira mais adequada como nif,


)
// Enum que define níveis de prioridade da tarefa.
enum class Priority {
    LOW, MEDIUM, HIGH
}


// Conversor necessário para o Room salvar/encontrar enums.
// O Room só entende tipos primitivos (String, Int, etc), então
// convertemos o enum Priority para String e vice-versa.
class PriorityConverter {
    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

}
