package com.example.myamover.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.remote.ClientRemote
import com.example.myamover.data.repository.ClientRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * UI State do ecrã/lista de clientes.
 *
 * - loading: indica se estamos a fazer pedido à API (para mostrar progress/loader)
 * - clients: lista final de clientes (resultado do GET)
 * - error: mensagem de erro para mostrar na UI (Snackbar/Text)
 */

data class ClientRemoteUiState(
    val loading: Boolean = false,
    val clients: List<ClientRemote> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel responsável por:
 * 1) Ir procurar clientes à API (via repositório)
 * 2) Guardar o resultado num StateFlow (ui) para a UI observar e atualizar automaticamente
 *
 * Fluxo típico:
 * UI abre -> init { loadClients() } -> repo.getAllClient() -> atualiza _ui -> Compose redesenha
 */


class ClientRemoteViewModel(
    // Repositório faz a ponte com a camada de rede (Retrofit / API)
    private val repo: ClientRemoteRepository = ClientRemoteRepository()
) : ViewModel() {
    // State interno mutável (só o ViewModel pode alterar)
    private val _ui = MutableStateFlow(ClientRemoteUiState())

    // State exposto à UI como "read-only" (a UI só observa, não altera)
    val ui: StateFlow<ClientRemoteUiState> = _ui

    init {
        // Assim que o ViewModel é criado (ecrã abre), carregamos logo os clientes
        loadClients()
    }

    /**
     * Faz o pedido à API para obter a lista de clientes.
     *
     * - Define loading = true para a UI mostrar "a carregar"
     * - Faz a chamada em coroutine (viewModelScope)
     * - Em sucesso: guarda lista em clients e remove loading
     * - Em erro: guarda mensagem em error e remove loading
     *
     */

    fun loadClients() {
        // Atualiza o estado imediatamente (antes de começar a chamada remota)
        _ui.value = _ui.value.copy(loading = true, error = null)

        // viewModelScope: coroutines que são canceladas automaticamente quando o VM é destruído
        viewModelScope.launch {
            try {
                // Pedido ao repositório (normalmente faz um GET à API)
                val all = repo.getAllClient()

                // Sucesso: guardamos a lista e desligamos o loading
                _ui.value = _ui.value.copy(
                    loading = false,
                    clients = all,
                    error = null
                )
            } catch (e: Exception) {
                // Falha: guardamos mensagem de erro e desligamos o loading
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Erro ao carregar clientes"
                )
            }
        }

    }

}