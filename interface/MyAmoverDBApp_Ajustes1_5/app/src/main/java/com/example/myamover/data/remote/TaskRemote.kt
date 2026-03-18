package com.example.myamover.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myamover.data.model.RouteJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * TaskRemote
 *
 * DTO (Data Transfer Object) remoto.
 * Representa **exatamente** o formato de uma tarefa
 * tal como vem do Supabase / backend.
 *
 * IMPORTANTE:
 * - Este modelo NÃO deve conter lógica de negócio
 * - Serve apenas para transportar dados (JSON → Kotlin)
 */
@Serializable
data class TaskRemote @RequiresApi(Build.VERSION_CODES.O) constructor(

    //Idenitficar único da task
    @SerialName("id")
    val id: Int,

    //Identificador único da rota a que a tarefa pertence
    val route_id: Int,

    //Identificador único do cliente a que a tarefa pertence
    @SerialName("client_id")
    val client_id: Int?,

    // Tipo de tarefa (entrega/serviço)
    val type: String? = null,

    // Estado da tarefa (pending/in_progress/completed)
    val status: String? = null,

    // Data de criação da tarefa
    val created_at: String? = null,

    // Data de atualização da tarefa
    @SerialName("updated_at")
    val updatedAt: String? = null,



    // Campos adicionais
    // Notas associadas à tarefa
    val notes: String? = null,

    /**
     * Data/hora planeada para execução da tarefa.
     *
     * Normalmente em formato ISO:
     * "2025-12-29 09:00:00"
     */
    val planned_time: String? = null,

    //instruções específicos para a tarefa
    val instructions: String? = null,

    //janela temporal da tarefa - inicio
    @SerialName("time_window_start")
    val timeWindowStart: String? = null,

    //janela temporal da tarefa - fim
    @SerialName("time_window_end")
    val timeWindowEnd: String? = null,


    /**
     * Latitude da localização da tarefa.
     *
     *  Está como String porque vem assim do Supabase.
     * Idealmente poderia ser Double no futuro.
     */
    val lat: String? = null,
    val lng: String? = null,

    //Morada da tarefa
    val address: String? = null,

    //-------------- Futura implementação - confirmação de entrega


    // Fotografias associadas à tarefa - Lista de urls ou path
    val photo: List<String>? = null,
    val signature: String? = null,
    val anotation: String? = null,

    //Identificador do utilizador associado
    val userID: Int? = null,

    //nome do utilizador
    val name: String? = null,

    //email associado ao cliente
    val email: String? = null,

    // Rota completa associada à tarefa.
    //   Normalmente este campo NÃO vem preenchido
    //  no GET /tasks. É usado apenas em casos específicos.
    val route: RouteJson? = null,






)

/*
Usa @Serializable → compatível com Retrofit + Supabase

✔ Campos opcionais evitam crashes se o backend não enviar dados
✔ Separação clara entre dados remotos e estado da UI
✔ Permite evoluir o backend sem quebrar a app

Sugestões futuras (opcionais)

1️⃣ Converter lat/lng para Double

val lat: Double?
val lng: Double?


2️⃣ Criar um modelo de domínio
TaskDomain(...)

para separar ainda mais:
DTO remoto
modelo usado pela UI
 */
