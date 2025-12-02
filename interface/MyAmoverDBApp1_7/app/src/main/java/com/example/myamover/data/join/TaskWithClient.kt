package com.example.myamover.data.join

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myamover.data.entities.ClientEntity
import com.example.myamover.data.entities.TaskEntity

data class TaskWithClient(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "client_id",
        entityColumn = "id"
    )
    val client: ClientEntity?
)