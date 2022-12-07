package com.mouritech.crashnotifier.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.ui.CrashDetection
import com.mouritech.crashnotifier.ui.Main2Activity

class CrashDetectionService : Service(), SensorEventListener {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    private lateinit var sensorManager: SensorManager

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, CrashDetectionService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, CrashDetectionService::class.java)
            context.stopService(stopIntent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        if(sensorManager != null){
            val acceleroSensor:Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if(acceleroSensor !=null){
                sensorManager.registerListener(this, acceleroSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, Main2Activity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            //   Log.e("","x: "+ event.values[0] +" y:"+ event.values[1] +" z:"+ event.values[2] )
            crashDetection(event.values[0].toDouble(),event.values[1].toDouble(),event.values[2].toDouble())

        }
    }
    private fun crashDetection(xValue: Double, yValue: Double, zValue: Double) {

        val fallThreshold = Math.sqrt((xValue * xValue + yValue * yValue + zValue * zValue) as Double)
        if (fallThreshold < 1.0f) {
            crashDetected()
        }
        var rootSquare = Math.sqrt(Math.pow(xValue, 2.0) + Math.pow(yValue, 2.0) + Math.pow(zValue, 2.0
        ));
        if(rootSquare<2.0) {
            crashDetected()
        }

    }

    private fun crashDetected() {
        val intent = Intent(this, CrashDetection::class.java)
        // start your next activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

        startActivity(intent)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}