package com.example.myamover.data.repository

import com.example.myamover.data.dao.ClientDao
import com.example.myamover.data.entities.ClientEntity

class ClientRepository(private val clientDao: ClientDao)  {
    suspend fun add(client: ClientEntity): Long =
        clientDao.insertClient(client)

    fun getAllClients() = clientDao.getAllClients()



}