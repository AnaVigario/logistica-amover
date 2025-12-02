package com.example.myamover.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myamover.data.entities.ClientEntity

@Dao //contém os métodos responsáveis por acessar e manipular dados no banco.
interface ClientDao { //tem os métodos para interagir com a tabela de clientes.

    //insere um objeto no banco
    @Insert
    suspend fun insertClient(client: ClientEntity): Long

    //recebe uma lista de clientes.
    @Insert
    suspend fun insertAll(clients: List<ClientEntity>): List<Long>

    //consulta SQL que seleciona todos os registos da tabela clients.
    @Query("SELECT * FROM clients")
    fun getAllClients(): List<ClientEntity>
}