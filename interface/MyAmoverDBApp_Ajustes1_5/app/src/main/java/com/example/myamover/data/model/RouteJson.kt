package com.example.myamover.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/*
*
* * Modelo principal de uma Rota.
 *
 * Este objeto representa o JSON completo da rota diária,
 * normalmente vindo da API /routes/* ou devolvido pelo PATCH /tasks/{id}.
 *
 * Exemplo (simplificado):
 * {
 *   "route_id": 3,
 *   "version": 2,
 *   "nodes": [ ... ]
 * }
 */
 */
@Serializable
data class RouteJson(
    /**
     * Lista ordenada de nós da rota.
     *
     * Cada nó representa:
     * - o armazém/depot (início/fim)
     * - ou uma paragem/tarefa de entrega
     *
     * A ordem da lista corresponde à ordem de visita.
     */
    val nodes: List<RouteNode>,
    /**
     * Versão da rota.
     *
     * Usada para distinguir diferentes cenários:
     * - 1, 2, 3… (rota alternativa A/B/C)
     *
     * Pode ser null se o backend não enviar versão.
     */
    val version: Int? = null,
    /**
     * Identificador da rota no backend.
     *
     * Mapeado a partir do campo "route_id" do JSON.
     * Nem sempre é necessário na UI, mas é útil para debug/logs.
     */
    @SerialName("route_id") val routeId: Int? = null
)

/**
 * Representa um nó individual da rota.
 *
 * Pode ser:
 * - um DEPOT (armazém)
 * - ou uma TASK (entrega/serviço ao cliente)
 */
@Serializable
data class RouteNode(
    /**
     * Identificador único do nó dentro da rota.
     * Normalmente sequencial.
     */
    val id: Int,
    /**
     * Coordenada X (longitude ou eixo X no mapa).
     */
    val x: Double,
    /**
     * Coordenada Y (latitude ou eixo Y no mapa).
     */
    val y: Double,

    /**
     * Tipo de nó.
     *
     * Exemplos:
     * - "depot"
     * - "delivery"
     */
    // pode existir nos nós de entrega
    val type: String? = null,
    /**
     * Notas associadas à tarefa (opcional).
     */
    val notes: String? = null,
    /**
     * Estado da tarefa neste nó.
     *
     * Exemplos:
     * - "pending"
     * - "in_progress"
     * - "completed"
     */
    val status: String? = null,
    /**
     * ID da tarefa associada a este nó.
     * Vem do campo "id_task" no JSON.
     */

    @SerialName("id_task")
    val idTask: Int? = null,
    /**
     * Nome do cliente associado à tarefa.
     */
    @SerialName("name_client")
    val nameClient: String? = null,
    /**
     * Endereço onde a tarefa deve ser realizada.
     */
    @SerialName("address_task")
    val addressTask: String? = null,
    /**
     * Instruções específicas para esta paragem.
     */
    val instructions: String? = null,

    /**
     * Início da janela temporal da tarefa.
     * Exemplo: "09:00"
     */
    @SerialName("time_window_start")
    val timeWindowStart: String? = null,


    /**
     * Fim da janela temporal da tarefa.
     * Exemplo: "12:00"
     */
    @SerialName("time_window_end")
    val timeWindowEnd: String? = null,

    /**
     * Indica se este nó corresponde ao armazém/depot.
     * true  -> depot
     * false -> paragem normal
     */
    @SerialName("is_depot")
    val isDepot: Boolean? = null
)
