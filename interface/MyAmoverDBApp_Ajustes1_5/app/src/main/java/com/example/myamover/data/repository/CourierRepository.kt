package com.example.myamover.data.repository

import com.example.myamover.data.network.SupabaseClientProvider
import com.example.myamover.data.remote.CourierRemote
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourierRepository(
    private val authRepository: AuthRepository,

) {
    private val client = SupabaseClientProvider.client

    suspend fun getCourierByCurrentUser(): CourierRemote {
        return withContext(Dispatchers.IO) {
            val userUuid = authRepository.requireCurrentUserUuid()
            client
                .from("couriers")
                .select (Columns.list("id", "name", "UUID")){
                    filter {
                        eq("UUID", userUuid)
                    }
                }
                .decodeSingle<CourierRemote>()
        }
    }

    suspend fun getCourierIdByCurrentUser(): Int {
        val courier = getCourierByCurrentUser()
        return requireNotNull(courier.id) { "Courier sem id" }
    }
}