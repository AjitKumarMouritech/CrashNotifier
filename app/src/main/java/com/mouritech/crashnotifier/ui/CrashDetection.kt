package com.mouritech.crashnotifier.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.CrashDetectionNotificationViewModel
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.databinding.ActivityCrashDetectionBinding
import com.mouritech.crashnotifier.utils.Utils
import java.util.*

class CrashDetection : AppCompatActivity() /*, SensorEventListener*/ {
    private lateinit var sensorManager: SensorManager
    private lateinit var newBinding: ActivityCrashDetectionBinding
    private lateinit var viewModel: CrashDetectionNotificationViewModel
    private var countdown_timer: CountDownTimer? = null
    private var time_in_milliseconds = 10000L
    private var pauseOffSet = 0L
    private var crashCount:Int = 0
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
    private var lat:String= ""
    private var lon:String= ""

    private var tts: TextToSpeech? = null
    private val REQUEST_PERMISSION_SMS = 20

    private lateinit var emergencyContactViewModel: EmergencyContactViewModel
    private lateinit var emergencyContactList: List<EmergencyContacts>
    private var emergencyNumber:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newBinding = DataBindingUtil.setContentView(this, R.layout.activity_crash_detection)
        //newBinding = DataBindingUtil.setContentView(this, R.layout.activity_crash_detection);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //speakOut()
        getLastLocation()

        getFCMTokenƒromfirebaseDB()
       // sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
      /*  if(sensorManager != null){
            val acceleroSensor:Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if(acceleroSensor !=null){
               sensorManager.registerListener(this, acceleroSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        else{
            Toast.makeText(this,"Sensor service not detected",Toast.LENGTH_SHORT).show()
        }*/


        newBinding.btnNo.setOnClickListener {
            pauseTimer()
            resetTimer()
            crashCount=0;
            finish()

        }
        tts = TextToSpeech(
            this@CrashDetection
        ) { status -> // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e("error", "This Language is not supported")
                } else {
                    convertTextToSpeech()
                }
            } else Log.e("error", "Initilization Failed!")
        }


    }
    private fun convertTextToSpeech() {
        // TODO Auto-generated method stub
        var text = "Are you in Trouble, If not please press No button"
        if (text == null || "" == text) {
            text = "Content not available"
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        } else tts!!.speak(text + "is saved", TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun getFCMTokenƒromfirebaseDB() {
        emergencyContactViewModel = ViewModelProvider(this@CrashDetection).get(EmergencyContactViewModel::class.java)
        val preferences = this.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        emergencyContactViewModel.getEmergencyContact2( Utils.getUserID(preferences))
        emergencyContactViewModel._emergencyContacts.observe(this@CrashDetection, Observer {
            emergencyContactList=it
            starTimer(pauseOffSet)
        })
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
           // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

  /*  override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            Log.e("","x: "+ event.values[0] +" y:"+ event.values[1] +" z:"+ event.values[2] )
            crashDetection(event.values[0].toDouble(),event.values[1].toDouble(),event.values[2].toDouble())
        }
    }*/

 /*   private fun crashDetection(xValue: Double, yValue: Double, zValue: Double) {

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

    }*/
    private fun starTimer(pauseOffSetL : Long){
        countdown_timer = object : CountDownTimer(time_in_milliseconds - pauseOffSetL, 1000){
            override fun onTick(millisUntilFinished: Long) {
                pauseOffSet = time_in_milliseconds - millisUntilFinished
               // tv_timer.text= (millisUntilFinished/1000).toString()
              //  Toast.makeText(this@CrashDetection, pauseOffSet.toString(), Toast.LENGTH_SHORT).show();

            }

            override fun onFinish() {
                    val viewModel = ViewModelProvider(this@CrashDetection).get(CrashDetectionNotificationViewModel::class.java)
                for (element in emergencyContactList){
                    val emergencyContact:EmergencyContacts= element
                    val preferences = getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)

                    //Toast.makeText(this@CrashDetection, "Fall detected", Toast.LENGTH_SHORT).show();
                    //viewModel = ViewModelProvider(CrashDetection@this).get(CrashDetectionNotificationViewModel::class.java)

                    if (element.fcm_token=="NA"){
                        viewModel.sendNotification("Crash happening with your friend","Some Emergency", Utils.getUserID(preferences), Utils.mobileNumber(preferences),
                            lat, lon, emergencyContact.fcm_token)
                    }else{
                        if (checkPermissionForSMS(this@CrashDetection)) {
                            emergencyNumber = emergencyContact.emergency_contact_number
                            sendSMS(lat,lon, emergencyContact.emergency_contact_number)
                        }
                    }
                    viewModel._notificationResponse.observe(this@CrashDetection, Observer {
                       // Toast.makeText(this@CrashDetection, it, Toast.LENGTH_SHORT).show();
                        crashCount = 0;
                    })
                    pauseOffSet =0
                }


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

    override fun onBackPressed() {
        super.onBackPressed()
        pauseTimer()
        resetTimer()
        countdown_timer?.cancel()
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lat= location.latitude.toString()
                        lon= location.longitude.toString()
                        //findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                       // findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location? = locationResult.lastLocation
            lat= mLastLocation?.latitude.toString()
            lon= mLastLocation?.longitude.toString()
           // findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
           // findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
        if (requestCode == REQUEST_PERMISSION_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(lat,lon, emergencyNumber)
                //  btnStartupdate.isEnabled = false
                //  btnStopUpdates.isEnabled = true
            } else {
                Toast.makeText(this@CrashDetection, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSMS(lat: String, lon: String, emergencyNumber: String) {
        val preferences = getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        val strUri =
            "http://maps.google.com/maps?q=loc:$lat,$lon"
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(emergencyNumber, null, "Emergency Message\nCrash happening with your friend, Please call on " +
                "- ${Utils.mobileNumber(preferences)}\nAccident Location is:- $strUri", null, null)

    }
    private fun checkPermissionForSMS(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS),
                    REQUEST_PERMISSION_SMS)
                false
            }
        } else {
            true
        }
    }
}
