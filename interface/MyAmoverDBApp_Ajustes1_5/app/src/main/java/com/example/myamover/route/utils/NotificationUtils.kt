package com.example.myamover.route.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val CHANNEL_ID = "amover_channel"

const val CHANNEL_MESSAGES = "messages_high"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "A-MoVeR Notificações",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

fun ensureMessageChannel(context: Context){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(
            CHANNEL_MESSAGES,
            "Messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Messages from A-MoVeR"
        }
        nm.createNotificationChannel(ch)

    }
}
