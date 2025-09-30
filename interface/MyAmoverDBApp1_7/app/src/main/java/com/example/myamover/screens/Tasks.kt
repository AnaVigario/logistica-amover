package com.example.myamover.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myamover.data.database.AppDataBase
import com.example.myamover.data.join.TaskWithClient
import com.example.myamover.data.repository.TaskRepository
import com.example.myamover.data.utils.formatDate
import com.example.myamover.model.TaskViewModel
import com.example.myamover.route.utils.loadJsonFromAssets
import com.example.myamover.route.utils.startRouteFromNodesJson


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    windowSize: androidx.compose.material3.windowsizeclass.WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    onOpenMapRoute: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDataBase.getInstance(context) }
    val repo = remember { TaskRepository(db.taskDao()) }
    val vm: TaskViewModel = viewModel(factory = TaskViewModel.Factory(repo))
    val tasks = vm.todayTasks.collectAsState().value

    Scaffold(

    ) { padding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem tarefas.")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.task.id }) { twc ->
                    TaskCard(twc)
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val json = loadJsonFromAssets(context, "route.json")
                    startRouteFromNodesJson(
                        context,
                        json,
                        includeChargingStationsAsStops = true)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Iniciar rota demo")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TaskCard(twc: TaskWithClient) {
    val t = twc.task
    val c = twc.client

    ElevatedCard(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${t.type} • ${t.status} • ${t.priority}", fontWeight = FontWeight.Bold)
            if (t.description.isNotBlank()) Text(t.description)

            Spacer(Modifier.height(8.dp))
            Text("Criado em: ${formatDate(t.creation_date)}")
            Text("Janela: ${t.time_window}  |  Prazo: ${formatDate(t.deadline)}")
            Text("Pickup: ${t.pickup_location}")
            Text("Entrega: ${t.delivery_location}")
            Text("Coords: ${"%.5f".format(t.latitude)}, ${"%.5f".format(t.longitude)}")

            Spacer(Modifier.height(8.dp))
            Text(
                c?.let { "Cliente: ${it.name} • NIF ${it.nif}" } ?: "Cliente: —",
                fontWeight = FontWeight.SemiBold
            )
            if (c != null) {
                Text(c.address)
                Text("${c.phone} • ${c.email}")
            }
        }
    }
}