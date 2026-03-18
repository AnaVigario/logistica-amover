package com.example.myamover.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.model.TaskRemoteUiState
import com.example.myamover.data.repository.AuthRepository
import com.example.myamover.data.repository.CourierRepository
import com.example.myamover.data.repository.TaskRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskRemoteViewModel(
    private val repo: TaskRemoteRepository = TaskRemoteRepository()
) : ViewModel() {

    private val courierRepository = CourierRepository(
        authRepository = AuthRepository()
    )

    private val _ui = MutableStateFlow(TaskRemoteUiState())
    val ui: StateFlow<TaskRemoteUiState> = _ui

    init {
        loadTasks()
        loadTodayRoute()
    }

    private suspend fun resolveCourierId(): Int {
        return courierRepository.getCourierIdByCurrentUser()
            ?: throw IllegalStateException("Courier não encontrado para o utilizador autenticado")
    }

    fun loadTasks() {
        _ui.value = _ui.value.copy(loading = true, error = null)

        viewModelScope.launch {
            runCatching {
                val courierId = resolveCourierId()
                repo.getAllTasksByCourier(courierId)
            }.onSuccess { tasks ->
                _ui.update {
                    it.copy(
                        loading = false,
                        tasks = tasks,
                        error = null
                    )
                }
            }.onFailure { e ->
                _ui.update {
                    it.copy(
                        loading = false,
                        error = e.stackTraceToString()
                    )
                }
            }
        }
    }

    fun clearTasks() {
        _ui.value = _ui.value.copy(tasks = emptyList())
    }

    fun loadTodayRoute() {
        _ui.update { it.copy(loadingRoute = true, error = null) }

        viewModelScope.launch {
            runCatching {
                val courierId = resolveCourierId()
                repo.getTodayRoute(courierId)
            }.onSuccess { route ->
                _ui.update {
                    it.copy(
                        loadingRoute = false,
                        route = route,
                        todayRouteJson = null,
                        error = null
                    )
                }
            }.onFailure { e ->
                _ui.update {
                    it.copy(
                        loadingRoute = false,
                        route = null,
                        error = e.stackTraceToString()
                    )
                }
            }
        }
    }

    fun completeTaskAndRefresh(
        taskId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        status: String,
        notes: String,
        photos: List<Uri>,
        signature: Uri?
    ) {
        viewModelScope.launch {
            runCatching {
                repo.completeTask(
                    taskId = taskId,
                    status = status,
                    notes = notes,
                    photos = photos,
                    signature = signature
                )
            }.onSuccess { newRoute ->
                if (newRoute != null) {
                    _ui.update { it.copy(route = newRoute, loadingRoute = false) }
                } else {
                    loadTodayRoute()
                }

                loadTasks()
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Erro ao completar tarefa")
            }
        }
    }
}