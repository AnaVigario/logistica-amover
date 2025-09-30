package com.example.myamover.data.repository

import com.example.myamover.data.dao.UserDao
import com.example.myamover.data.entities.UserEntity

// Repositório que encapsula o acesso aos dados de utilizadores.
// Serve de camada intermédia entre o DAO e a lógica de negócio/ViewModel.
class UserRepository(private val userDao: UserDao)  {

    // Procura um utilizador pelo e-mail.
    // Como é suspend, deve ser chamado dentro de uma corrotina.
    suspend fun findByemail(email: String): UserEntity? =
        userDao.getByemail(email)

    // Regista um novo utilizador na base de dados.
    // Cria um UserEntity a partir dos parâmetros recebidos e insere-o.
    // Devolve o ID da linha inserida.
    suspend fun register(email: String, passwordHash: String, name: String, salt: String): Long =
        userDao.insert(UserEntity(
            email = email,
            passwordHash = passwordHash,
            name = name,
            salt = salt))
}

/*este repositório por enquanto só encaminha chamadas, mas pode evoluir para:
validar se o utilizador já existe antes de o registar;
aplicar regras de negócio (ex.: normalizar o e-mail para lowercase);
combinar dados locais com dados de uma API.*/