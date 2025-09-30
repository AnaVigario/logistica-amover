package com.example.myamover.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myamover.data.database.AppDataBase
import com.example.myamover.data.repository.UserRepository
import com.example.myamover.remote.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class LoginUiState(
    val loading: Boolean = false,
    val passwordError: String = "",
    val message: String? = null, //sucesso/erro
    val loggedInUser: String? = null,
    val usermameError: String = "",
    val emailError: String?
)

class LoginViewModel (app: Application): AndroidViewModel(app){

    private val db = AppDataBase.getInstance(app)

    private val auth = AuthService(UserRepository(db.userDao()))


    private val _ui = MutableStateFlow(LoginUiState(emailError = null))
    val ui: StateFlow<LoginUiState> = _ui

    fun login(email: String, password: String) {
        //validaç~~oes simples do VM
        val uErr = if (email.isBlank()) "email is required" else ""
        val pErr = if (password.isBlank()) "Password is required" else ""

        if (uErr.isNotEmpty() || pErr.isNotEmpty()) {
            _ui.value = _ui.value.copy(emailError = uErr, passwordError = pErr, message = null)
            return

        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(
                loading = true,
                message = null,
                emailError = "",
                passwordError = ""
            )
            val result = auth.login(email, password)
            _ui.value = when (result) {
                is AuthService.Result.Error ->
                    _ui.value.copy(loading = false, message = result.message)

                is AuthService.Result.Success ->
                    _ui.value.copy(loading = false, loggedInUser = result.email, message = "Login successful")

            }
        }
    }



    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            throw IllegalStateException("Use provideFactory(application)")

        }
        fun provideFactory(app: Application)= object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return LoginViewModel(app) as T
            }
        }
    }

}