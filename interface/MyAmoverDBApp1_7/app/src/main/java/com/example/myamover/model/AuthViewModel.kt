package com.example.myamover.model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.di.SessionManager
import kotlinx.coroutines.launch

// ViewModel responsável pela lógica de autenticação/sessão do utilizador.
class AuthViewModel(

    // O SessionManager é injetado aqui, permitindo aceder ao DataStore para gerir a sessão.

    private val sessionManager: SessionManager,

    ): ViewModel() {

    // Função para terminar a sessão do utilizador.
    // Lança uma corrotina no scope da ViewModel e chama o clear() do SessionManager,
    // que apaga todos os dados de sessão guardados (ex.: token).
    fun logout() = viewModelScope.launch {
        sessionManager.clear()
    }
}