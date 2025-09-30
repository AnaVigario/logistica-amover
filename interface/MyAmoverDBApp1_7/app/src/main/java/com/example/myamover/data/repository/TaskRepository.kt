package com.example.myamover.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myamover.data.dao.TaskDao
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.join.TaskWithClient
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

// Repositório que encapsula o acesso aos dados de Task.
// Ele faz a ponte entre o DAO (acesso ao banco) e a ViewModel/UseCases.
class TaskRepository(private val taskDao: TaskDao)  {

    // Observa todas as tarefas em tempo real.
    // Retorna um Flow<List<TaskEntity>>, ou seja,
    // sempre que a base de dados for alterada, a UI que estiver coletando esse flow será atualizada automaticamente.
    fun observeAll(): Flow<List<TaskEntity>> = taskDao.getAll()

    // Procura uma tarefa específica pelo ID.
    // Como é suspend, deve ser chamada dentro de uma coroutine.
    suspend fun findById(taskId: Long): TaskEntity? =
        taskDao.findById(taskId)

    // Procura uma tarefa específica pelo ID.
    // Como é suspend, tem de ser chamada dentro de uma corrotina.
    suspend fun add(task: TaskEntity): Long =
        taskDao.insert(task)
    fun tasksByDate(date: String) = taskDao.getByDate(date)
    fun allOrderedByDateDesc() = taskDao.getAllOrderByCreationDateDesc()


    //procura data
    @RequiresApi(Build.VERSION_CODES.O)
    fun tasksForTodayWithClient(): Flow<List<TaskWithClient>> {
        val calendar = java.util.Calendar.getInstance()

        // início do dia (00:00:00.000)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // fim do dia (23:59:59.999)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return taskDao.getTasksForDayWithClient(startOfDay, endOfDay)
//        val today = LocalDate.now()
//        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//        val endOfDay =
//            today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
//        return taskDao.getTasksForDayWithClient(startOfDay, endOfDay)

    }

    //////////////////////////////////////////

    //Cliente

    fun observeAllWithClient(): Flow<List<TaskWithClient>> =
        taskDao.getAllWithClient()

    suspend fun findByIdWithClient(taskId: Long): TaskWithClient? =
        taskDao.findByIdWithClient(taskId)

    fun tasksByDateWithClient(date: String): Flow<List<TaskWithClient>> =
        taskDao.getByDateWithClient(date)

    fun allOrderedByDateDescWithClient(): Flow<List<TaskWithClient>> =
        taskDao.getAllOrderByCreationDateDescWithClient()
}

/*o Repository pode também:
Juntar dados de várias tabelas (ou até de API remota + base local).
Aplicar regras de negócio (ex.: validar dados antes de guardar, converter datas, etc).*/