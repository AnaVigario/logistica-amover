package com.example.myamover.data.remote

import com.example.myamover.data.model.RouteJson
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * TaskApiService
 *
 * Interface Retrofit que define todos os endpoints
 * relacionados com:
 * - tarefas (tasks)
 * - rotas diárias (routes)
 *
 * O Retrofit gera automaticamente a implementação
 * desta interface em tempo de execução.
 */
interface TaskApiService {

    /**
     * Request body usado para atualizar uma tarefa.
     *
     * Atualmente usado apenas para alterar o estado (status),
     * por exemplo:
     * - "pending"
     * - "in_progress"
     * - "completed"
     */

    @Serializable
    data class TaskUpdateRequest(
        val status: String
    )

    /**
     * Response devolvida pelo PATCH /tasks/{id}.
     *
     * - status: indica sucesso da operação
     * - route: nova rota diária (opcional)
     *
     * A rota só vem preenchida quando o status passa a "completed" ou "not-completed.
     * Noutros casos, route = null.
     */
    @Serializable
    data class UpdateTaskResponse(
        val status: String,
        val route: RouteJson? = null
    )


    /**
     * Obtém todas as tarefas do backend.
     *
     * Endpoint:
     * GET /tasks
     *
     * Usado no ecrã principal (TasksScreen)
     * para mostrar a lista de tarefas do dia.
     */
    @GET("tasks")
    suspend fun getAllTasks(courierId: Int): List<TaskRemote>

    @GET("tasks")
    suspend fun getTasksByCourier(
        @Query("courier_id") courierId: Int
    ): List<TaskRemote>


    /**
     * Atualiza uma tarefa específica.
     *
     * Endpoint:
     * PATCH /tasks/{id}
     *
     * Usado principalmente para:
     * - marcar uma tarefa como "completed"
     *
     * Quando a tarefa é concluída, o backend:
     * - recalcula a rota diária
     * - devolve a nova rota no campo "route"
     */
    @PATCH("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Body body: TaskUpdateRequest
    ): UpdateTaskResponse


    /**
     * Obtém a rota diária atual do estafeta.
     *
     * Endpoint:
     * GET /routes/today?courier_id=...
     *
     * Usado:
     * - no load inicial da app
     * - como fallback caso o PATCH não devolva rota
     */
    @GET("routes/today")
    suspend fun getTodayRoute(
        @Query("courier_id") courierId: Int,
    ): RouteJson
}


/*
Diferença entre GET /tasks, PATCH /tasks/{id} e GET /routes/today

✔ Quando e porquê o backend devolve uma nova rota
✔ Porque route pode ser null
✔ Como o Retrofit faz o binding automático dos endpoints
 */
