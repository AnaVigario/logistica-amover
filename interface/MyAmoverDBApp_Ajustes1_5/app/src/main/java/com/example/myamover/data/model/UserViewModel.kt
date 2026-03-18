package com.example.myamover.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.remote.UserRemote
import com.example.myamover.data.repository.UserRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val loading: Boolean = false,
    val users: List<UserRemote> = emptyList(),
    val error: String? = null,

    )


class UserViewModel(
    private val repo: UserRemoteRepository = UserRemoteRepository()

) : ViewModel() {


    private val _ui = MutableStateFlow(UserUiState())

    val ui: StateFlow<UserUiState> = _ui

    init {
        loadUsers()
    }

    fun loadUsers() {
        _ui.value = _ui.value.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                val all = repo.getAllUsers()

                _ui.value = _ui.value.copy(
                    loading = false,
                    users = all,
                    error = null
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Erro ao carregar utilizador"
                )
            }
        }

    }


}

