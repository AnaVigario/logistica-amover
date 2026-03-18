package com.example.myamover.data.firebase



import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myamover.MainActivity
import com.example.myamover.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


const val TAG = "FCM"
class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        //notification
        message.notification?.let {
            showNotification(message)
        }


        //check if message containins a data payload
        if (message.data.isNotEmpty())

            handleDataMessage(message.data)

    }

    private fun handleDataMessage(data: MutableMap<String, String>) {

        Log.d(TAG, "handleDataMessage: $data")
    }

    fun showNotification(message: RemoteMessage){

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "Default"


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "Firebase Messging"


        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(Random.nextInt(), notificationBuilder)


    }



    override fun onNewToken(token: String) {
        Log.d("FCM", "NEW TOKEN=$token")
        // (opcional) enviar token ao teu backend aqui
    }
}


