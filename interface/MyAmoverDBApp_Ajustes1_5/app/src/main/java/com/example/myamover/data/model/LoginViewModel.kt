package com.example.myamover.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.network.SupabaseClientProvider
import com.example.myamover.data.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


/**
 * Modelo simples de utilizador usado na app.
 * Este modelo é independente do Supabase e pode ser serializado.
 */
@Serializable
data class User(
    val id: String,
    val email: String
)

/**
 * Estado da UI do ecrã de Login.
 *
 * - loading: indica se está a decorrer uma operação de login/logout
 * - loggedInUser: utilizador autenticado (null se não estiver autenticado)
 * - message: mensagens de erro ou informação para mostrar na UI
 */
data class LoginUiState(
    val loading: Boolean = false,
    val loggedInUser: User? = null,
    val message: String? = null
)

/**
 * ViewModel responsável pela autenticação do utilizador.
 *
 * Funções principais:
 * - Verificar se já existe sessão guardada (auto-login)
 * - Efetuar login com email e password
 * - Efetuar logout
 *
 * Este ViewModel comunica com o Supabase Auth.
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // State interno mutável (só o ViewModel pode alterar)
    private val _ui = MutableStateFlow(LoginUiState())

    // State exposto à UI (read-only)
    val ui: StateFlow<LoginUiState> = _ui

    // Cliente Supabase partilhado na app
    private val supabase = SupabaseClientProvider.client

    init {
        /**
         * Ao criar o ViewModel, tentamos obter um utilizador já autenticado.
         * Isto permite auto-login se a sessão estiver guardada no dispositivo.
         */
        // tenta sessão guardada (auto-login)
        viewModelScope.launch {
            val current = supabase.auth.currentUserOrNull()
            if (current != null) {
                _ui.value = _ui.value.copy(
                    loggedInUser = current.toUser(),
                    message = null,
                    loading = false
                )
            }
        }
    }

    /**
     * Função auxiliar para converter o UserInfo do Supabase
     * para o modelo User usado pela aplicação.
     */
    private fun UserInfo.toUser(): User? {
        return User(
            id = this.id,
            email = this.email ?: ""
        )
    }

    /**
     * Efetua login com email e password.
     *
     * Fluxo:
     * 1) Coloca UI em loading
     * 2) Tenta autenticar no Supabase
     * 3) Se sucesso → guarda utilizador no estado
     * 4) Se falha → guarda mensagem de erro
     */

    fun login(email: String, password: String) {
        // coloca UI em estado de loading
        _ui.value = _ui.value.copy(
            loading = true,
            message = null
        )

        viewModelScope.launch {
            try {
                // 1. tenta autenticar no Supabase
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // 2. depois de fazer signInWith, a sessão fica guardada no client
                val currentUser = supabase.auth.currentUserOrNull()

                if (currentUser != null) {
                    //Login bem sucedido: guarda utilizador no estado
                    _ui.value = _ui.value.copy(
                        loading = false,
                        loggedInUser = currentUser.toUser(),
                        message = null
                    )
                } else {
                    // Login bem sucedido, mas não é o utilizador esperado
                    _ui.value = _ui.value.copy(
                        loading = false,
                        loggedInUser = null,
                        message = "Falha ao obter sessão após login (utilizador vazio)"
                    )
                }

            } catch (e: Exception) {
                // Login falhou: guarda mensagem de erro no estado
                _ui.value = _ui.value.copy(
                    loading = false,
                    loggedInUser = null,
                    message = "Falha no login: ${e.message ?: "credenciais inválidas?"}"
                )
            }
        }
    }

    /**
     * Termina a sessão do utilizador (logout).
     *
     * - Chama o AuthRepository para fazer signOut
     * - Limpa o utilizador do estado da UI
     */
    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.logout()   // aqui dentro deves chamar client.auth.signOut()
            }.onSuccess {
                _ui.value = _ui.value.copy(
                    loggedInUser = null,
                    message = null
                )
            }.onFailure { error ->
                // podes tratar o erro se quiseres
                _ui.value = _ui.value.copy(
                    message = "Erro ao terminar sessão"
                )
            }
        }
    }
}

/**
 * Função helper global:
 * Converte UserInfo (Supabase) para User (modelo da app).
 * Pode ser usada noutros ficheiros.
 */
// helper: converte o UserInfo (Supabase) no nosso User serializável
fun UserInfo.toUser(): User {
    return User(
        id = this.id,
        email = this.email ?: ""
    )
}