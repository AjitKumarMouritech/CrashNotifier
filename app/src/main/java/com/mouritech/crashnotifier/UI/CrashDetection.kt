package com.mouritech.crashnotifier.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.viewmodel.CrashDetectionNotificationViewModel
import com.mouritech.crashnotifier.databinding.ActivityCrashDetectionBinding


class CrashDetection : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var newBinding: ActivityCrashDetectionBinding
    private lateinit var viewModel: CrashDetectionNotificationViewModel
    private var countdown_timer: CountDownTimer? = null
    private var time_in_milliseconds = 5000L
    private var pauseOffSet = 0L
    private var crashCount:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newBinding = DataBindingUtil.setContentView(this, R.layout.activity_crash_detection)
        //newBinding = DataBindingUtil.setContentView(this, R.layout.activity_crash_detection);
        getFCMToken()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        if(sensorManager != null){
            val acceleroSensor:Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if(acceleroSensor !=null){
               sensorManager.registerListener(this, acceleroSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        else{
            Toast.makeText(this,"Sensor service not detected",Toast.LENGTH_SHORT).show()
        }
        newBinding.btnNo.setOnClickListener {
            pauseTimer()
            resetTimer()
            crashCount=0;

        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.d("Token", token)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            Log.e("","x: "+ event.values[0] +" y:"+ event.values[1] +" z:"+ event.values[2] )
            crashDetection(event.values[0].toDouble(),event.values[1].toDouble(),event.values[2].toDouble())

        }
    }

    private fun crashDetection(xValue: Double, yValue: Double, zValue: Double) {

        val fallThreshold = Math.sqrt((xValue * xValue + yValue * yValue + zValue * zValue) as Double)
        if (fallThreshold < 1.0f) {
            if (crashCount == 0) {
                crashCount = 1;
                //val direction: String = if (mXAxis < x) VALUE_FRONT else VALUE_BACK

                starTimer(pauseOffSet)
                crashCount = 2;
            }
        }
        var rootSquare = Math.sqrt(Math.pow(xValue, 2.0) + Math.pow(yValue, 2.0) + Math.pow(zValue, 2.0
        ));
        if(rootSquare<2.0) {
            if (crashCount == 0) {
                starTimer(pauseOffSet)
                crashCount = 2;

            }
        }

    }
    private fun starTimer(pauseOffSetL : Long){
        countdown_timer = object : CountDownTimer(time_in_milliseconds - pauseOffSetL, 1000){
            override fun onTick(millisUntilFinished: Long) {
                pauseOffSet = time_in_milliseconds - millisUntilFinished
               // tv_timer.text= (millisUntilFinished/1000).toString()
                Toast.makeText(this@CrashDetection, pauseOffSet.toString(), Toast.LENGTH_SHORT).show();

            }

            override fun onFinish() {
                    val viewModel =
                        ViewModelProvider(this@CrashDetection).get(
                            CrashDetectionNotificationViewModel::class.java
                        )

                    Toast.makeText(this@CrashDetection, "Fall detected", Toast.LENGTH_SHORT).show();
                    //viewModel = ViewModelProvider(CrashDetection@this).get(CrashDetectionNotificationViewModel::class.java)
                    viewModel.sendNotification("New Msg Body")
                    viewModel._notificationResponse.observe(this@CrashDetection, Observer {
                        Toast.makeText(this@CrashDetection, it, Toast.LENGTH_SHORT).show();
                        crashCount = 0;
                    })
                pauseOffSet =0

            }
        }.start()
    }
    private fun pauseTimer(){
        if (countdown_timer!= null){
            countdown_timer!!.cancel()
        }
    }

    private fun resetTimer(){
        if (countdown_timer!= null){
            countdown_timer!!.cancel()
            countdown_timer = null
            pauseOffSet =0
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}
