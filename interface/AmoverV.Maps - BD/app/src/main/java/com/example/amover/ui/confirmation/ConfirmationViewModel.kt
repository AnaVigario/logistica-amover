package com.example.amover.ui.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmationViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "This is confirmation Fragment"
    }
    val text: LiveData<String> = _text
}