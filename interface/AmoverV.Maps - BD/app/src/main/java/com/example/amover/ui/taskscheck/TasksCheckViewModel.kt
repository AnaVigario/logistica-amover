package com.example.amover.ui.taskscheck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TasksCheckViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Historic"
    }
    val text: LiveData<String> = _text
}