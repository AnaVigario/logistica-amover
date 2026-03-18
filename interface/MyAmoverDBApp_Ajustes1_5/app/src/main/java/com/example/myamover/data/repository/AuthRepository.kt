package com.example.myamover.data.repository

import com.example.myamover.data.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
**
 * AuthRepository
 *
 * Repositório responsável por TODA a lógica de autenticação.
 *
 * Este repositório:
 * - comunica diretamente com o Supabase Auth
 * - encapsula a lógica de login/logout
 * - devolve resultados normalizados à app (DTO próprio)
 *
 * O ViewModel NUNCA deve falar diretamente com o Supabase,
 * apenas através deste repositório.
 */

class AuthRepository {

    //Cliente Supabase partilhado em toda app
    private val client = SupabaseClientProvider.client

    // Login com email/password usando Supabase Auth (v3.2.5)
    /**
     * Login com email e palavra-passe usando Supabase Auth.
     *
     * Usa Result<T> para:
     * - representar sucesso (Result.success)
     * - representar falha (Result.failure)
     *
     * Isto permite ao ViewModel tratar o resultado
     * sem usar try/catch diretamente.
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<SupabaseUserData> {

        // Garante que a chamada é feita numa thread separada
        return withContext(Dispatchers.IO) {
            try {
                // 1. tenta autenticar
                /* Se as credenciais estiverem erradas,esta chamada lança uma exceção.
                 */
                client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // 2. depois do signInWith, o utilizador autenticado
                // fica disponível em currentUserOrNull()
                val currentUser: UserInfo? = client.auth.currentUserOrNull()

                // Situação anómala: login feito mas sessão não criada
                if (currentUser == null) {
                    return@withContext Result.failure(
                        IllegalStateException("Sessão não criada (currentUserOrNull == null)")
                    )
                }

                // 3. map para o nosso DTO externo
                //Converte o UserInfo do Supabase para o DTO simples usado pela app
                Result.success(
                    currentUser.toSupabaseUserData()
                )

            } catch (e: Exception) {
                // Normalizar a mensagem de erro - Evita mostrar mensagens técnicas ao utilizador
                val friendlyException = if (
                    e.message?.contains("invalid_credentials", ignoreCase = true) == true
                ) {
                    // credenciais erradas
                    Exception("Email ou palavra-passe incorretos.")
                } else {
                    // qualquer outro erro (rede, servidor, etc.)
                    Exception("Não foi possível iniciar sessão. Tente novamente.")
                }

                Result.failure(friendlyException)
            }
        }
    }

    // obter user atual (por ex. auto-login no arranque da app)
    /**
     * Obtém o utilizador atualmente autenticado.
     *
     * Usado para:
     * - auto-login ao abrir a app
     * - restaurar sessão existente
     *
     * Retorna null se não existir sessão válida.
     */
    suspend fun getCurrentUser(): SupabaseUserData? {
        return withContext(Dispatchers.IO) {
            client.auth.currentUserOrNull()?.toSupabaseUserData()
        }
    }

    suspend fun getCurrentUserUuid(): String? {
        return withContext(Dispatchers.IO) {
            client.auth.currentUserOrNull()?.id
        }
    }

    suspend fun requireCurrentUserUuid(): String {
        return getCurrentUserUuid()
            ?: throw IllegalStateException("Utilizador não autenticado")
    }

    // logout
    /**
     * Termina a sessão do utilizador (logout).
     *
     * Remove:
     * - sessão local
     * - token de autenticação
     */
    suspend fun logout() {
            client.auth.signOut()
    }

    // conversor interno -> externo
    /**
     * Função de extensão privada.
     *
     * Converte o UserInfo interno do Supabase
     * para o DTO simples usado pela aplicação.
     */
    private fun UserInfo.toSupabaseUserData(): SupabaseUserData {
        return SupabaseUserData(
            id = this.id,
            email = this.email ?: ""
        )
    }

}

// o DTO simples que a app realmente usa
/**
 * SupabaseUserData
 *
 * DTO simples que a app realmente usa.
 *
 * Evita expor diretamente o UserInfo do Supabase
 * ao resto da aplicação.
 */
data class SupabaseUserData(
    val id: String,
    val email: String
)

/*
Separação correta Repository ↔ ViewModel
✔ Uso adequado de Result<T> para sucesso/erro
✔ Normalização de mensagens para o utilizador
✔ Proteção contra erros de thread (Dispatchers.IO)
✔ Conversão limpa entre modelo Supabase e modelo da app
Sugestões futuras (opcionais)

1.Adicionar refresh de sessão
client.auth.refreshCurrentSession()

2.Suporte a outros providers
Google
GitHub
OTP

3️.Mapear tipos de erro mais específicos
utilizador bloqueado
email não confirmado
 */