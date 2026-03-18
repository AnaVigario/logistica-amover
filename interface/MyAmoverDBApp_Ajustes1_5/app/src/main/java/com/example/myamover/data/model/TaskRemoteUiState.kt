package com.example.myamover.data.model

import com.example.myamover.data.remote.TaskRemote


/**
 * Estado da UI para o ecrã de Tasks e Rota diária.
 *
 * Este UiState é observado pela UI (Compose) através de StateFlow.
 * Sempre que algum campo muda, a UI é redesenhada automaticamente.
 */
data class TaskRemoteUiState(

    /**
     * Indica se a lista de tarefas está a ser carregada.
     *
     * Normalmente usado para:
     * - mostrar um CircularProgressIndicator
     * - bloquear ações enquanto o GET /tasks está a decorrer
     */
    val loading: Boolean = false,


/**
 * Indica se a rota diária está a ser carregada/atualizada.
 *
 * Usado quando:
 * - a app chama GET /routes/*
 * - ou quando está a trocar a rota após "Complete Task"
*/
 * */
    val loadingRoute: Boolean = false,


    /**
     * Lista de tarefas recebidas do backend.
     *
     * Representa o resultado do GET /tasks.
     * A UI usa esta lista para:
     * - mostrar cards de tarefas
     * - associar tarefas aos nós da rota
     */
    val tasks: List<TaskRemote> = emptyList(),

    /**
     * Mensagem de erro a apresentar na UI.
     *
     * Exemplos:
     * - erro de rede
     * - erro do backend
     * - falha ao carregar tarefas ou rota
     */
    val error: String? = null,


    /**
     * JSON bruto da rota diária (string).
     *
     * Campo legado / opcional.
     * Era usado quando a rota vinha como String e precisava de parsing manual.
     *
     * Atualmente pode ser removido se estiveres a usar apenas `route: RouteJson`.
     */
    val todayRouteJson: String? = null,

    /**
     * Rota diária já desserializada.
     *
     * Este é o campo principal usado pela UI para:
     * - desenhar o mapa
     * - mostrar a sequência de paragens
     * - navegar para o detalhe da task
     *
     * É atualizado:
     * - no load inicial da rota
     * - ou diretamente após completar uma task (PATCH /tasks/{id})
     */
    val route: RouteJson? = null
)
