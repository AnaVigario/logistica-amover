package com.example.myamover.data.network

import com.example.myamover.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * SupabaseClientProvider
 *
 * Singleton responsável por criar e fornecer
 * uma instância única do SupabaseClient em toda a app.
 *
 * Este client é usado para:
 * - autenticação (login/logout)
 * - acesso à base de dados (PostgREST)
 *
 *  Importante:
 * As credenciais NÃO estão hardcoded no código.
 * São lidas a partir do BuildConfig (gradle).
 */
object SupabaseClientProvider {

    @OptIn(SupabaseInternal::class)
    val client: SupabaseClient

        get() = createSupabaseClient(
            // URL do projeto Supabase (Settings → API)
            supabaseUrl = BuildConfig.SUPABASE_URL,


            // Anon public key do projeto Supabase
            // (Settings → API → anon public)
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY

        ) {
            /**
             * Plugin de autenticação (Supabase Auth).
             *
             * Necessário para:
             * - login com email/password
             * - obter sessão atual
             * - logout
             */
            install(Auth)
            /**
             * Plugin PostgREST.
             *
             * Necessário para:
             * - ler/escrever tabelas do Supabase
             * - executar selects, inserts, updates
             *
             * Exemplo de uso:
             * supabase.from("tasks").select()
             */
            install(Postgrest)
        }
    }




/*
Onde estão as credenciais do Supabase
✔ Porque não devem estar hardcoded
✔ Para que servem Auth e Postgrest
✔ Porque usar BuildConfig é a abordagem correta
✔ Como garantir uma única instância do client

Sugestões futuras (opcionais)
1️⃣ Persistência de sessão

install(Auth) {
    persistSession = true
}


2️⃣ Logs apenas em debug

if (BuildConfig.DEBUG) {
    // ativar logs
}


3️⃣ Separar clients (auth vs db) se a app crescer muito.
 */


