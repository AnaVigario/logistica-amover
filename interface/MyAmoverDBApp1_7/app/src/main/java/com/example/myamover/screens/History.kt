package com.example.myamover.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myamover.data.database.AppDataBase
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.repository.TaskRepository
import com.example.myamover.data.utils.formatDate
import com.example.myamover.model.HistoryViewModel
import com.example.myamover.model.millisToDdMMyyyy

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    // se você usar DI/Hilt, troque pelo hiltViewModel()
    viewModel: HistoryViewModel = run {
        val ctx = LocalContext.current
        val db = remember { AppDataBase.getInstance(ctx) }
        val repo = remember { TaskRepository(db.taskDao()) }
        viewModel(factory = HistoryViewModel.Factory(repo))
    },
    windowSize: androidx.compose.material3.windowsizeclass.WindowWidthSizeClass,
) {
    val tasksForDay by viewModel.tasksForDay.collectAsState()
    val allTasks by viewModel.allTasksOrdered.collectAsState()

    var showPicker by remember { mutableStateOf(false) }

    Scaffold(

    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tarefas do dia selecionado", style = MaterialTheme.typography.titleMedium)
            if (tasksForDay.isEmpty()) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Sem tarefas para a data escolhida")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tasksForDay, key = { it.id }) { TaskHistoryCard(it) }
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Text("Todas (ordenadas por data)", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                items(allTasks, key = { it.id }) { TaskHistoryCard(it) }
            }
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = pickerState.selectedDateMillis ?: return@TextButton
                    viewModel.setDate(millisToDdMMyyyy(millis))
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = pickerState) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TaskHistoryCard(t: TaskEntity) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${formatDate(t.creation_date)} • ${t.type} • ${t.priority}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Status: ${t.status}")
            Text("Cliente: ${t.client_id}")
            Text("Pickup: ${t.pickup_location}")
            Text("Entrega: ${t.delivery_location}")
            Text("Coords: ${"%.5f".format(t.latitude)}, ${"%.5f".format(t.longitude)}")
            if (t.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(t.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// --- (Opcional) Preview estático com dados fake:
/*@Preview(showBackground = true, widthDp = 360, heightDp = 720, name = "History — Light")
@Composable
private fun HistoryScreenPreview() {
    val fake = listOf(
        TaskEntity(
            id = 1,
            type = "Recolha",
            creation_date= "09-03-2025",
            deadline = "9:00",
            time_window = "No specific time windows",
            description = "Envelopes",
            /* done = false, */
            status = "Concluído",
            pickup_location = "R. D. António Valente da Fonseca",
            delivery_location = "Quinta dos Prados, Chocas",
            longitude = 41.2994,
            latitude =-7.7496,
            priority = Priority.LOW,
            client_id = 1
        ),
        TaskEntity(
            id = 2,
            type = "Entrega",
            creation_date= "10-03-2025",
            deadline = "9:00",
            time_window = "No specific time windows",
            description = "Exames",
            status = "Em andamento",
            pickup_location = "Quinta dos Prados, ECT, Polo I",
            delivery_location = "Quinta dos Prados, Chocas",
            longitude = 41.2886,
            latitude =-7.7391,
            priority = Priority.HIGH,
            client_id = 0
        )
    )
    HistoryScreen(windowSize = androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact)

    MaterialTheme {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            fake.forEach { TaskHistoryCard(it) }
        }
    }
}*/
