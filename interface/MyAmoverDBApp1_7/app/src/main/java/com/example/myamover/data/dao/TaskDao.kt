package com.example.myamover.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.join.TaskWithClient
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Insere uma Task no banco.
    // Se já existir uma com o mesmo ID, substitui (REPLACE).
    // Retorna o id da linha inserida.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    // Procura uma Task específica pelo ID.
    // Retorna null caso não exista.
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun findById(taskId: Long): TaskEntity?

    // Retorna todas as Tasks em forma de Flow (lista reativa).
    // Assim, a UI pode observar mudanças automaticamente.
    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>


    //tarefas e os clientes relacionados de forma atômica, sem risco de inconsistência no meio da leitura.
    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllWithClient(): Flow<List<TaskWithClient>>

    //traz apenas uma tarefa específica, cujo id é igual ao parâmetro taskId.
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun findByIdWithClient(taskId: Long): TaskWithClient?

    //Consulta todas as tarefas de um dia específico, onde creation_date coincide com o parâmetro date
    @Transaction
    @Query("SELECT * FROM tasks WHERE creation_date = :date")
    fun getByDateWithClient(date: String): Flow<List<TaskWithClient>>

    //tarefas ordenadas por creation_date em ordem decrescente (mais recentes primeiro).
    @Transaction
    @Query("SELECT * FROM tasks ORDER BY creation_date DESC")
    fun getAllOrderByCreationDateDescWithClient(): Flow<List<TaskWithClient>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE creation_date BETWEEN :startOfDay AND :endOfDay")
    fun getTasksForDayWithClient(startOfDay: Long, endOfDay: Long): Flow<List<TaskWithClient>>


    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query("""
        SELECT * FROM tasks
        WHERE creation_date = :date
        ORDER BY id DESC
    """)
    fun getByDate(date: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        ORDER BY 
          substr(creation_date, 7, 4) ||  -- pega o ano (posições 7 a 10) 
          substr(creation_date, 4, 2) ||  -- pega o mês (posições 4 e 5)
          substr(creation_date, 1, 2) DESC, -- pega o dia (posições 1 e 2)
          id DESC
    """)
    fun getAllOrderByCreationDateDesc(): Flow<List<TaskEntity>>


}