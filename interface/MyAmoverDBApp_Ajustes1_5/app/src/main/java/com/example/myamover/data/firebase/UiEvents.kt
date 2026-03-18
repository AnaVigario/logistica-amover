package com.example.myamover.data.firebase


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class UiFcmMessage(val title: String, val message: String)

object UiEvents {
    private val _fcmEvents = MutableSharedFlow<UiFcmMessage>(extraBufferCapacity = 20)
    val fcmEvents = _fcmEvents.asSharedFlow()

    fun emit(msg: UiFcmMessage) {
        _fcmEvents.tryEmit(msg)
    }
}
