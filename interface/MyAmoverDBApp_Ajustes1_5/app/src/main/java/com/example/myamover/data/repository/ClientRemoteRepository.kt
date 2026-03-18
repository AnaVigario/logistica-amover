package com.example.myamover.data.repository

import com.example.myamover.data.network.SupabaseClientProvider
import com.example.myamover.data.remote.ClientRemote
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * ClientRemoteRepository
 *
 * Repositório responsável por todas as operações
 * relacionadas com CLIENTES no Supabase.
 *
 * Este repositório:
 * - fala diretamente com o Supabase (PostgREST)
 * - devolve modelos ClientRemote
 * - encapsula o acesso à base de dados
 *
 * Os ViewModels nunca devem aceder diretamente
 * ao Supabase, apenas através deste repositório.
 */
class ClientRemoteRepository {

    /**
     * Cliente Supabase partilhado na aplicação.
     *
     * Fornecido pelo SupabaseClientProvider.
     */
    private val client = SupabaseClientProvider.client

    /**
     * Obtém TODOS os clientes da base de dados.
     *
     * Tabela usada:
     * - "clients"
     *
     * Fluxo:
     * ViewModel → Repository → Supabase → Repository → ViewModel
     */
    suspend fun getAllClient():List<ClientRemote> = withContext(Dispatchers.IO){
        client.postgrest["clients"]
            .select()  //SELECT *FROM clients
            .decodeList<ClientRemote>() // converte JSON -> List<ClientRemote>
    }


    /*
    Obtém um cliente específico pelo ID.
     *
     * @param clientId identificador do cliente
     *
     * Usado normalmente no detalhe da task
     * para mostrar os dados do cliente associado.
     */
    suspend fun getClientById(clientId: Int): ClientRemote = withContext(Dispatchers.IO) {
        client.postgrest["clients"]
            // Filtro WHERE id = clientId
            // Confirma se o nome da coluna é "id" ou "ID" no Supabase
            .select { filter { eq("ID", clientId) } } // ou "id"
            .decodeSingle<ClientRemote>()
    }
}

/*

Porque existe um Repository entre ViewModel e Supabase

✔ Como funciona o acesso via PostgREST
✔ Onde alterar o nome da tabela/coluna
✔ Porque usar Dispatchers.IO para chamadas de rede
✔ Diferença entre decodeList e decodeSingle

Sugestões futuras (opcionais)

1️.Tratar erro de “cliente não encontrado”
runCatching { ... }.getOrElse { throw Exception("Cliente não encontrado") }


2️.Uniformizar nomes
eq("id", clientId) // preferível a "ID"


3️. Adicionar cache local (Room) se a lista crescer.

 */