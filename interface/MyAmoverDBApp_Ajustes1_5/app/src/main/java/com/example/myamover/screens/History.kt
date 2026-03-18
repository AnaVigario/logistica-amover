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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myamover.R
import com.example.myamover.data.remote.TaskRemote
import com.example.myamover.model.TaskRemoteViewModel
import com.example.myamover.navigation.NavRoutes
import com.example.myamover.route.utils.formatIsoDateTime
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * HISTORY SCREEN
 * - Lista tarefas ordenadas (por prazo ou criação)
 * - Seletor de intervalo de datas
 * - Filtro por campo de data (Criação vs Prazo)
 *
 */


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onOpenMapRoute: (() -> Unit)? = null,
    vm: TaskRemoteViewModel = viewModel(),
    navigationIcon: @Composable () -> Unit = {},
    //windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val uiState by vm.ui.collectAsState()
    val context = LocalContext.current

    // Estado do intervalo de datas
    var showDateDialog by remember { mutableStateOf(false) }
    var startDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var endDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }

    // Critério de ordenação e de filtro
    var orderBy by rememberSaveable { mutableStateOf(OrderBy.DEADLINE) }
    var filterBy by rememberSaveable { mutableStateOf(FilterBy.DEADLINE) }
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }

    // Lista tratada (ordenada + filtrada)
    val tasksProcessed = remember(uiState.tasks, startDate, endDate, orderBy, filterBy) {
        uiState.tasks
            .filter { task ->
                if (startDate == null && endDate == null) return@filter true
                val date = when (filterBy) {
                    FilterBy.DEADLINE -> task.timeWindowEnd?.toLocalDateOrNull()
                    FilterBy.CREATED -> task.planned_time?.toLocalDateOrNull()
                }
                val s = startDate ?: LocalDate.MIN
                val e = endDate ?: LocalDate.MAX
                if (date != null) (date >= s && date <= e) else false
            }
            .sortedWith(
                when (orderBy) {
                    OrderBy.DEADLINE -> compareBy(nullsLast(LocalDate::compareTo)) { it.timeWindowEnd?.toLocalDateOrNull() }
                    OrderBy.CREATED -> compareBy(nullsLast(LocalDate::compareTo)) { it.planned_time?.toLocalDateOrNull() }
                    OrderBy.STATUS -> compareBy(nullsLast(String.CASE_INSENSITIVE_ORDER)) { it.status }
                }
            )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
//
    ) { padding ->

        when {
            uiState.loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding), contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.error_prefix)+ ": ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }

            uiState.tasks.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding), contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.no_tasks))
                }
            }


            else -> {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Cabeçalho com datas selecionadas
                    HistoryHeaderRow(
                        navController = navController,
                        start = startDate,
                        end = endDate,
                        onOpenDateDialog = { showDateDialog = true },
                        onClearDates = { startDate = null; endDate = null },
                        orderBy = orderBy,
                        onOrderChange = { orderBy = it },
                        filterBy = filterBy,
                        onFilterChange = { filterBy = it }
                    )


                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(tasksProcessed, key = { it.id }) { task ->
                            val expanded = expandedId?.toInt() == task.id
                            HistoryTaskCard(
                                task = task,
                                expanded = expanded,
                                onToggleExpand = {
                                    expandedId = if (expanded) null else task.id
                                },
                                onHistoryTaskDetails = { taskId ->
                                    navController.navigate(NavRoutes.HistoryTaskDetail(taskId))
                                },
                                onBackClick = { /* volta */ }
                            )
                        }
                    }

                }
            }
        }
    }

    if (showDateDialog) {
        DateRangePickerDialog(
            onDismissRequest = { showDateDialog = false },
            onConfirm = { s, e ->
                startDate = s
                endDate = e
                showDateDialog = false
            }
        )
    }
}

// -- Menus de ordenação e filtro ----------------------------------------------------

enum class OrderBy { DEADLINE, CREATED, STATUS }
enum class FilterBy { DEADLINE, CREATED }

@Composable
private fun HistoryMenus(
    orderBy: OrderBy,
    onOrderChange: (OrderBy) -> Unit,
    filterBy: FilterBy,
    onFilterChange: (FilterBy) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.FilterList, contentDescription = "Order/Filter")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Text(
                stringResource(id = R.string.sort_by),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.deadline)) },
                onClick = { onOrderChange(OrderBy.DEADLINE); expanded = false })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.creation)) },
                onClick = { onOrderChange(OrderBy.CREATED); expanded = false })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.status)) },
                onClick = { onOrderChange(OrderBy.STATUS); expanded = false })
            Divider()
            Text(
                stringResource(id = R.string.filter_by_date),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.deadline)) },
                onClick = { onFilterChange(FilterBy.DEADLINE); expanded = false })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.creation)) },
                onClick = { onFilterChange(FilterBy.CREATED); expanded = false })
        }
    }
}

// -- Cabeçalho com datas selecionadas ----------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryHeaderRow(
    navController: NavHostController,
    start: LocalDate?,
    end: LocalDate?,
    onOpenDateDialog: () -> Unit,
    onClearDates: () -> Unit,
    orderBy: OrderBy,
    onOrderChange: (OrderBy) -> Unit,
    filterBy: FilterBy,
    onFilterChange: (FilterBy) -> Unit,
) {
    val label = when {
        start == null && end == null -> stringResource(id = R.string.all_dates)
        start != null && end != null -> "${start.formatPt()} → ${end.formatPt()}"
        start != null -> stringResource(id = R.string.from_date) + " ${start.formatPt()}"
        else ->  stringResource(id = R.string.until_date) + " ${end!!.formatPt()}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ESQUERDA: back + chip/limpar
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AssistChip(
                onClick = onOpenDateDialog,
                label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )

            Spacer(Modifier.width(8.dp))

            if (start != null || end != null) {
                TextButton(onClick = onClearDates) { Text(stringResource(id = R.string.clear)) }
            }
        }

        // DIREITA: actions
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onOpenDateDialog) {
                Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(id = R.string.select_dates))
            }

            HistoryMenus(
                orderBy = orderBy,
                onOrderChange = onOrderChange,
                filterBy = filterBy,
                onFilterChange = onFilterChange
            )
        }
    }
}


// -- Card de tarefa para histórico --------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryTaskCard(
    task: TaskRemote,
    onHistoryTaskDetails: (Int) -> Unit,
    onBackClick: () -> Unit,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    val startDate = formatIsoDateTime(task.timeWindowStart, "dd-MM-yyyy")
    val startTime = formatIsoDateTime(task.timeWindowStart, "HH:mm")

    val endDate = formatIsoDateTime(task.timeWindowEnd, "dd-MM-yyyy")
    val endTime = formatIsoDateTime(task.timeWindowEnd, "HH:mm")
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onToggleExpand),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded)
                colorScheme.surfaceContainerHighest
            else
                colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(13.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            // topo do card (sempre visível)
            Surface(
                color = colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = task.status ?: "--",
                    color = colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("${task.address}", fontWeight = FontWeight.Bold)
            Text(stringResource(id = R.string.date_label) + "$endDate")
            if (!task.notes.isNullOrBlank()) Text(task.notes!!)


            AnimatedVisibility(visible = expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // exemplo de ícone/ação extra à esquerda
//                    IconButton(onClick = { /* outra ação */ }) {
//                        Icon(Icons.Default.Map, contentDescription = "Open map")
//                    }
                    Button(
                        onClick = { onHistoryTaskDetails(task.id) },
                        modifier = Modifier.padding(end = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.onSecondary,
                            contentColor = colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(id = R.string.view_details))
                    }
                }
            }
        }
    }
}


// -- DateRangePickerDialog ----------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (start: LocalDate?, end: LocalDate?) -> Unit,
) {
    val state = rememberDateRangePickerState()
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val s = state.selectedStartDateMillis?.toLocalDate()
                    val e = state.selectedEndDateMillis?.toLocalDate()
                    onConfirm(s, e)
                }
            ) { Text(stringResource(id = R.string.apply)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(id = R.string.cancel)) }
        }
    ) {
        DateRangePicker(state = state, title = { Text(stringResource(id = R.string.select_date)) })
    }
}


// -- Utils de datas -----------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
private fun String.toLocalDateOrNull(): LocalDate? =
    runCatching {
        // tenta ISO OffsetDateTime ou LocalDate
        when {
            contains("T", ignoreCase = false) -> OffsetDateTime.parse(this).toLocalDate()
            else -> LocalDate.parse(this)
        }
    }.getOrNull()

@RequiresApi(Build.VERSION_CODES.O)
private fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate =
    java.time.Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDate.formatPt(): String =
    this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))


//@Composable
//fun TaskCardWithStatusBadge(
//    status: String,
//    content: @Composable () -> Unit
//) {
//    Box {
//        // Card principal
//        ElevatedCard(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            content()
//        }
//
//        // Badge PENDING
//        Surface(
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .offset(x = 12.dp, y = 12.dp),
//            shape = RoundedCornerShape(12.dp),
//            color = MaterialTheme.colorScheme.primary
//        ) {
//            Text(
//                text = status.uppercase(),
//                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
//                color = MaterialTheme.colorScheme.onPrimary,
//                style = MaterialTheme.typography.labelSmall
//            )
//        }
//    }
//}
