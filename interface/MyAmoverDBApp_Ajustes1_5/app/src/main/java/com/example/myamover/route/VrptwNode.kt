package com.example.myamover.route


/* Representa um nó da rede VRPTW (Vehicle Routing Problem with Time Windows)
 Cada nó pode ser um depósito, um cliente ou uma estação de carregamento.
 Este modelo é usado para:
 * - cálculo de rotas
 * - visualização no mapa
 * - navegação (Google Maps)
 */
data class VrptwNode(

    //Identificador único do nó
    val id: Long,
    val x: Double, // longitude
    val y: Double, // latitude

    /**
     * Indica se o nó é o depósito.
     *
     * true  -> depósito
     * false -> não é depósito
     * null  -> informação não fornecida
     */
    val is_depot: Boolean? = null,

    /**
     * Indica se o nó é uma estação de carregamento.
     *
     * true  -> estação de carregamento
     * false -> outro tipo de nó
     */
    val is_charging_station: Boolean? = null,



    // ─────────────── CLIENTES ───────────────

    /**
     * Quantidade de carga associada ao cliente.
     *
     * Usado em algoritmos de:
     * - capacidade do veículo
     * - otimização logística
     */
    val demand: Int? = null,                  // Quantidade de carga a ser entregue/recolhida neste nó
    val time_window: List<Int>? = null,       // Janela de tempo [início, fim] em segundos desde 00:00
    val service_time: Int? = null,            // Tempo de serviço no nó (em segundos)

    // Atributo específico para estação de carregamento:
    val charger_power_kw: Double? = null      // Potência do carregador (se aplicável)
)

/* Representa a estrutura raiz do JSON vindo da API.
 Contém a lista de todos os nós (depósito + clientes + estações).
  Este objeto é usado para:
 * - parsing do JSON
 * - cálculo e visualização da rota
 */
data class VrptwPayload(
    //Lista ordenada de nós da rota
    val nodes: List<VrptwNode>  // Lista de nós a serem usados na rota
)

/*
Conceito de VRPTW

✔ Diferença entre depósito, cliente e estação
✔ Uso de janelas temporais
✔ Separação clara de atributos por tipo de nó
✔ Preparação para algoritmos avançados de otimização

Sugestões futuras (opcionais)

1️ Enums para tipos de nó

enum class NodeType { DEPOT, CLIENT, CHARGER }

2️ Converter time_window para LocalTime

LocalTime.ofSecondOfDay(...)

3️Validação de dados

require(x in -180.0..180.0)
require(y in -90.0..90.0)
 */

