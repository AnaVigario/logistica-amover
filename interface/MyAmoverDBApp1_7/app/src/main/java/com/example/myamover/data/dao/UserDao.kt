package com.example.myamover.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myamover.data.entities.UserEntity


@Dao
interface UserDao {

    //Procura um utilizador pelo email
    //Retorna null caso não exista
    // Como o email deve ser único, usamos LIMIT 1 para garantir apenas um resultado.
    // Como é `suspend`, roda de forma assíncrona.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByemail(email: String): UserEntity?


    // Insere um novo utilizador.
    // Se já existir um utilizados com o mesmo ID (chave primária), lança erro (ABORT).
    // Retorna o id da linha inserida.
    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    // Conta o número de utilizadores
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int


    //Procura um utilizador pelo ID
    @Query("SELECT * FROM users WHERE uid = :id LIMIT 1")
    fun getUserById(id: Long): UserEntity?

}