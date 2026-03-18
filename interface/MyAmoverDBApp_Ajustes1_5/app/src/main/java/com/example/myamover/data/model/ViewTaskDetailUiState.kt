package com.example.myamover.data.model

import com.example.myamover.data.remote.ClientRemote
import com.example.myamover.data.remote.TaskRemote


/**
 * Estado da UI do ecrã de detalhe de uma tarefa (Task Detail).
 *
 * Este UiState é observado pela UI (Compose).
 * Sempre que algum campo muda, o ecrã é redesenhado automaticamente.
 *
 * É usado normalmente num ViewModel associado ao detalhe da task.
 */
data class TaskDetailUiState(

    /**
     * Indica se os dados da task/cliente estão a ser carregados.
     *
     * Exemplo de uso:
     * - true  -> mostrar loading spinner
     * - false -> mostrar conteúdo
     */
    val loading: Boolean = false,


    /**
     * Mensagem de erro a apresentar no ecrã.
     *
     * Exemplos:
     * - erro ao carregar dados da task
     * - erro de rede
     */
    val error: String? = null,

    /**
     * Tarefa selecionada.
     *
     * Contém toda a informação da task:
     * - id
     * - estado (pending / completed)
     * - notas
     * - janelas temporais
     */
    val task: TaskRemote? = null,

    /**
     * Cliente associado à tarefa.
     *
     * Usado para mostrar:
     * - nome
     * - morada
     * - contactos
     */
    val client: ClientRemote? = null
)



/*Quando este UiState é usado
Ao abrir o ecrã TaskDetailScreen
Enquanto carrega:
loading = true

Quando termina:
task e client preenchidos

Em erro:
error preenchido

Este padrão mantém a UI previsível, simples e fácil de manter.*/