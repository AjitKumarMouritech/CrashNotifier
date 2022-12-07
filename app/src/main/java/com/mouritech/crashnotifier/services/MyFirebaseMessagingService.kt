package com.mouritech.crashnotifier.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.ui.Main2Activity
import com.mouritech.crashnotifier.ui.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val NOTIFICATION_ID = 100
    private var tts: TextToSpeech? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Token:",token)

    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("notification", "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("notification", "Message data payload: " + remoteMessage.data)
            sendNotification(remoteMessage)

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("uid", remoteMessage.data.get("uid"))
            intent.putExtra("mobile", remoteMessage.data.get("mobile"))
            intent.putExtra("lat", remoteMessage.data.get("lat"))
            intent.putExtra("lon", remoteMessage.data.get("lon"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)

        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("TAG", "Message Notification Body: ${it.body}")
            it.body?.let { body -> sendNotification(body) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", remoteMessage.data.get("uid"))
        intent.putExtra("mobile", remoteMessage.data.get("mobile"))
        intent.putExtra("lat", remoteMessage.data.get("lat"))
        intent.putExtra("lon", remoteMessage.data.get("lon"))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(remoteMessage.data.get("title"))
            .setContentText(remoteMessage.data.get("body"))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(140 /* ID of notification */, notificationBuilder.build())
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, Main2Activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("gvghcghcgh")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(140 /* ID of notification */, notificationBuilder.build())
    }

}