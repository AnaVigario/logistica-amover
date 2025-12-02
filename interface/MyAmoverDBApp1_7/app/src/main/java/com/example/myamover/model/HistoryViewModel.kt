package com.example.myamover.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// ViewModel responsável por gerir o histórico de tarefas, filtradas por data ou listadas na totalidade.
class HistoryViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    // Data actualmente seleccionada (por defeito o dia de hoje no formato dd-MM-yyyy).
    // MutableStateFlow permite que a UI reaja sempre que o valor mudar.
    @RequiresApi(Build.VERSION_CODES.O)
    private val selectedDate = MutableStateFlow(todayDdMMyyyy())



    // Tarefas do dia seleccionado.
    // Sempre que `selectedDate` muda, faz-se uma nova query ao repositório (`tasksByDate`).
    // flatMapLatest cancela a pesquisa anterior e lança a nova.
    // stateIn converte o Flow em StateFlow para ser facilmente observado no Compose.
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForDay: StateFlow<List<TaskEntity>> = selectedDate
        .flatMapLatest { repository.tasksByDate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // (opcional) lista completa ordenada por data
    val allTasksOrdered: StateFlow<List<TaskEntity>> = repository.allOrderedByDateDesc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Actualiza a data seleccionada, o que automaticamente refaz a pesquisa em tasksForDay.
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(date: String) {
        selectedDate.value = date
    }

    // Factory para poder instanciar o HistoryViewModel manualmente, injectando o repositório.
    // Útil em contextos onde não se usa Hilt ou Koin.
    class Factory(private val repo: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistoryViewModel(repo) as T
    }

}

// Utils simples p/ datas dd-MM-yyyy
// Formatter configurado para o padrão "dia-mês-ano"
// Usa o fuso horário do sistema (ZoneId.systemDefault()).
@RequiresApi(Build.VERSION_CODES.O)
private val ddMMyyyy: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())


// Devolve a data actual no formato dd-MM-yyyy.
// Exemplo: se hoje for 27 de Setembro de 2025 → "27-09-2025"
@RequiresApi(Build.VERSION_CODES.O)
fun todayDdMMyyyy(): String = ddMMyyyy.format(Instant.now())


// Converte um valor em milissegundos desde epoch (Unix time) para o formato dd-MM-yyyy.
// Exemplo: millis correspondente a 10 de Março de 2025 → "10-03-2025"
@RequiresApi(Build.VERSION_CODES.O)
fun millisToDdMMyyyy(millis: Long): String = ddMMyyyy.format(Instant.ofEpochMilli(millis))