package com.example.myamover.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users", // cria tabela no banco de dados
    indices = [Index(value = ["email"], unique = true)] // cria índice único para email
)
data class UserEntity( // é o "modelo" que o Room mapeia para linhas da tabela
    @PrimaryKey(autoGenerate = true) val uid: Long = 0, // chave primária
    @ColumnInfo(collate = ColumnInfo.NOCASE) val email: String,//guarda o email e aplica NOCASE: comparação por email( e a verificação de unidade)ficam case-insensitive
    val passwordHash: String,  // hash PBKDF2 base64, 64 caracteres
    val salt: String,          // salt base64
    val createdAt: Long = System.currentTimeMillis(), //Timestamp (epoch millis) com valor padrão no momento da criação do objeto; mapeia para INTEGER no SQLite
    @ColumnInfo(name = "display_name", defaultValue = "Amover")  // valor padrão vazio)
    val name: String

)