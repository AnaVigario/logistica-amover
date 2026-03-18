package com.example.myamover.data.network

import com.example.myamover.data.remote.TaskApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * RetrofitProvider
 *
 * Singleton responsável por:
 * - configurar o Retrofit
 * - configurar o OkHttp
 * - definir a URL base da API
 * - criar a instância do TaskApiService
 *
 * Toda a app usa este provider para fazer chamadas HTTP.
 */
object RetrofitProvider {

    /**
     * URL base da API backend.

     * Em produção, deve ser um domínio HTTPS.
     */
    private const val BASE_URL = "http:///" //colocar porta




    /**
     * Interceptor de autenticação .
     *
     * Usado para enviar automaticamente o token do Supabase
     * em todas as requests para a API.
     *
     * Atualmente está comentado porque a API não exige autenticação.
     */
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()

        // se precisares do token do Supabase:
        // val token = SupabaseClientProvider.client.auth.currentSessionOrNull()?.accessToken

        val token = SupabaseClientProvider.client
            .auth
            .currentSessionOrNull()
            ?.accessToken

        val request = original.newBuilder().apply {
            // Se  ativar autenticação:
            if (token != null) header("Authorization", "Bearer $token")
        }.build()

        chain.proceed(request)
    }


    /**
     * Interceptor de logging HTTP.
     *
     * Level.BODY mostra:
     * - URL
     * - headers
     * - body do request
     * - body do response
     *
     * Ideal para desenvolvimento/debug.
     * Evitar em produção (dados sensíveis)!!!!!.
     */
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    /**
     * Cliente OkHttp usado pelo Retrofit.
     *
     * Interceptors adicionados:
     * - authInterceptor → autenticação (opcional)
     * - logging → debug das requests
     */
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()


    /**
     * Configuração do parser JSON (kotlinx.serialization).
     *
     * - ignoreUnknownKeys → ignora campos extra do backend
     * - isLenient → aceita JSON menos estrito
     * - coerceInputValues → valores inválidos passam para default
     *
     * Isto torna a app mais robusta a mudanças no backend.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }


    /**
     * Instância do Retrofit.
     *
     * Combina:
     * - baseUrl
     * - OkHttp configurado
     * - conversor JSON
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()


    /**
     * Instância final do serviço da API.
     *
     * Este objeto é usado pelos repositórios
     * para chamar os endpoints (GET, PATCH, POST, etc.).
     */
    val taskApi: TaskApiService =
        retrofit.create(TaskApiService::class.java)

    data class RegisterTokenBody(
        val user_id: String,
        val fcm_token: String,
        val platform: String = "android"
    )

    interface PushApi {
        @POST("push/register-token")
        suspend fun register(@Body body: RegisterTokenBody): Response
    }


}


/*
O que este ficheiro explica:

Onde alterar a URL da API
Como ativar autenticação por token no futuro
Porque usar ignoreUnknownKeys, isLenient, coerceInputValues
Onde adicionar novos interceptors (timeout, cache, etc.)
 */