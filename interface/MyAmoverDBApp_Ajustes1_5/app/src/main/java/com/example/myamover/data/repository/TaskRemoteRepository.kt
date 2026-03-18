package com.example.myamover.data.repository

import android.net.Uri
import com.example.myamover.data.model.RouteJson
import com.example.myamover.data.network.RetrofitProvider
import com.example.myamover.data.remote.TaskApiService
import com.example.myamover.data.remote.TaskRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRemoteRepository(
    private val api: TaskApiService = RetrofitProvider.taskApi
) {

    suspend fun getAllTasks(courierId: Int): List<TaskRemote> =
        withContext(Dispatchers.IO) {
        api.getAllTasks(courierId)
    }


    suspend fun getAllTasksByCourier(courierId: Int): List<TaskRemote> =
        withContext(Dispatchers.IO) {
            api.getTasksByCourier(courierId)
        }

    suspend fun getTodayRoute(courierId: Int): RouteJson =
        withContext(Dispatchers.IO) {
            api.getTodayRoute(courierId)
        }



    suspend fun completeTask(
        taskId: Int,
        status: String,
        notes: String,
        photos: List<Uri>,
        signature: Uri?
    ): RouteJson? = withContext(Dispatchers.IO) {
        val res = api.updateTask(
            taskId,
            TaskApiService.TaskUpdateRequest(status = status)
        )
        res.route
    }


}