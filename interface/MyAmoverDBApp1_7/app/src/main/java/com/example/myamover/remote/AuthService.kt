package com.example.myamover.remote

import com.example.myamover.data.repository.UserRepository
import com.example.myamover.di.PasswordHasher

class AuthService(private val userRepository: UserRepository) {

    sealed interface Result{
        data class Success(val userId: Long, val email: String): Result
        data class Error(val message: String): Result
    }

    suspend fun login(email: String, password: String): Result {
        val user = userRepository.findByemail(email)
            ?: return Result.Error("User not found")

        val computed = PasswordHasher.hashBase64(password, user.salt)
        return if (computed == user.passwordHash){
            Result.Success(user.uid, user.email)
        } else {
            Result.Error("Invalid password")
        }

    }
}