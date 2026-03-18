package com.example.myamover.data.repository

import com.example.myamover.data.network.SupabaseClientProvider
import com.example.myamover.data.remote.UserRemote
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRemoteRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getAllUsers(): List<UserRemote> = withContext(Dispatchers.IO) {
    client.postgrest["users"]
        .select()
        .decodeList<UserRemote>()
    }

    suspend fun getUserbyId(userId: String): UserRemote = withContext(Dispatchers.IO) {
        client.postgrest["users"]
            .select { filter { eq("id", userId) } }
            .decodeSingle<UserRemote>()
    }


    }

