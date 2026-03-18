package com.example.myamover.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myamover.R
import com.example.myamover.data.model.RouteJson
import com.example.myamover.model.TaskRemoteViewModel
import com.example.myamover.route.utils.opendWazeFromCurrentTo


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    vm: TaskRemoteViewModel,
    onOpenMapRoute: () -> Unit,
    onTaskDetails: (Int) -> Unit,
    onBackClick: () -> Unit,
    onToggleExpand: () -> Unit,
    expanded: Boolean = false,
    onOpenMapToStop: (Double, Double) -> Unit,

    ) {
    // ViewModel que fala com Supabase
    val uiState by vm.ui.collectAsState()
    val context = LocalContext.current
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }

    // garante que tenta carregar a rota quando a screen é mostrada
    LaunchedEffect(Unit) {
        if (uiState.route == null) {
            vm.loadTodayRoute()
        }
    }


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        contentColor = MaterialTheme.colorScheme.background,

    ) { padding ->


        when {

            uiState.loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.tasks.isEmpty() && uiState.route == null -> {
                Box(
                    modifier = modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.no_tasks), color = MaterialTheme.colorScheme.onBackground)
                }
            }

            else -> {

                val route: RouteJson? = uiState.route

                Spacer(modifier = Modifier.width(24.dp))


                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(colorScheme.background)
                ) {





                    if (route != null) {

                        Text(
                            text = stringResource(id = R.string.today_route) + ":",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(48.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val stops = route.nodes.filter { it.type == "delivery" || it.type == "pickup" }

                            items(stops, key = { it.id }) { node ->
                                val expanded = expandedId?.toInt() == node.id

                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth(0.95f)
                                        .animateContentSize()
                                        .clickable {
                                            expandedId = if (expanded) null else node.id
                                        },
                                    //(onClick = onToggleExpand),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (expanded)
                                            colorScheme.surfaceContainerHighest
                                        else
                                            colorScheme.surfaceContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp),

                                    ) {
                                    Column(Modifier.padding(12.dp)) {
                                        val noAddress = stringResource(id = R.string.no_address)
                                        Text("${node.id} - ${node.type}",
                                            color = colorScheme.onSurface,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium)

                                        Text(node.addressTask ?: noAddress,
                                            color = colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodyMedium)

//                                        if (!node.notes.isNullOrBlank()) {
//                                            Spacer(Modifier.height(4.dp))
//                                            Text("Notes: ${node.notes}")
//                                        }
                                        AnimatedVisibility(visible = expanded) {
                                            Column (
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(),
                                                horizontalAlignment = Alignment.Start,
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(stringResource(id = R.string.time_delivery) + ": ",
                                                    color = colorScheme.onSurface,
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                                Text("${node.timeWindowStart} - ${node.timeWindowStart}",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Row (
                                                    horizontalArrangement = Arrangement.End,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()

                                                ) {
                                                    Button(
                                                        onClick = {
                                                            node.idTask?.let { id ->
                                                                onTaskDetails(id)
                                                            }
                                                        },
                                                        modifier = Modifier.padding(end = 4.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorScheme.onSecondary,
                                                            contentColor = colorScheme.secondary
                                                        )

                                                    ) {
                                                        Text(stringResource(id = R.string.view_details))
                                                    }
                                                    Button(
                                                        onClick = {
                                                            // ✅ precisa de coords no node (x/y ou lat/lng)
                                                            opendWazeFromCurrentTo(
                                                                context = context,
                                                                destlat = node.y,
                                                                destlng = node.x
                                                            )
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = colorScheme.secondary,
                                                            contentColor = colorScheme.onSecondary
                                                        )
                                                    ) { Text(stringResource(id = R.string.view_on_map)) }
                                                }

                                            }
                                        }

                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = R.string.no_route),
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    // botão para navegar a rota completa (usando o JSON inteiro)
                    Button(
                        onClick = {
                            if (uiState.route != null) {
                                onOpenMapRoute()     // ✅ navega para MapScreen
                            } else {
                                vm.loadTodayRoute()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp)
                            .padding(bottom = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onBackground
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null)
                        Text(stringResource(id = R.string.start_navigation))
                    }
                }
            }
        }
    }
}
