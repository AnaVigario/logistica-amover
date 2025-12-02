package com.example.amover.ui.detailtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailTasksViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is detail tasks Fragment"
    }
    val text: LiveData<String> = _text

}