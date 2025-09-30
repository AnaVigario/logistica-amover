package com.example.myamover.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.join.TaskWithClient
import com.example.myamover.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,

    ) : ViewModel() {

    // Flow<List<TaskEntity>> -> StateFlow<List<TaskEntity>> observado pela UI
    val tasks: StateFlow<List<TaskEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    class Factory(private val repo: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(repo) as T
    }

    //observa TaskWithClient
    val tasksWithClient: StateFlow<List<TaskWithClient>> =
        repository.observeAllWithClient()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayTasks: StateFlow<List<TaskWithClient>> =
        repository.tasksForTodayWithClient()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



}
