package com.example.myamover.data.repository

import com.example.myamover.data.netwok.SupabaseClientProvider
import com.example.myamover.data.remote.TaskRemote
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//decodeList vem do supabase-kt e faz a desserialização usando @Serializable.

class TaskRemoteRepository {

    private val client = SupabaseClientProvider.client

    // busca todas as tasks (ou mais tarde podemos filtrar por userID)
    suspend fun getAllTasks(): List<TaskRemote> = withContext(Dispatchers.IO) {
        client.postgrest["Task"]
            .select() // SELECT * FROM "Task"
            .decodeList<TaskRemote>()
    }

    // se quiseres só tasks do utilizador logado:
    suspend fun getTasksForUser(userId: Int): List<TaskRemote> = withContext(Dispatchers.IO) {
        client.postgrest["Task"]
            .select {
                filter {
                    eq("userID", userId)
                }
            }
            .decodeList<TaskRemote>()
    }
}
