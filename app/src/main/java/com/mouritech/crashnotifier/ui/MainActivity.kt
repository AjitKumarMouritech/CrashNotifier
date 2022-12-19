package com.mouritech.crashnotifier.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.Signup
import com.mouritech.crashnotifier.data.viewmodel.EmergencyUserViewModel
import com.mouritech.crashnotifier.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: EmergencyUserViewModel by viewModels()
   private lateinit var database: DatabaseReference
    private lateinit var uid:String
    private lateinit var mobile: String
    lateinit var emergencyUserList: ArrayList<Signup>
    private val RECORD_REQUEST_CODE = 101
    private var lat:String=""
    private var lon:String=""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    /*    val viewModel = ViewModelProvider(this).get(EmergencyContactViewModel::class.java)
        // calling start counter methods which is in our viewmodel class
        viewModel.getEmergencyContact()
        // observing the second value of our view model class
        viewModel._emergencyContacts.observe(this, Observer {
            // setting textview value
            binding.quotes = it.toString()
        })*/
       // addEmergencyContact()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askNotificationPermission()
        }
        if(intent != null)
        {
            uid= intent.extras?.getString("uid").toString()
            mobile= intent.extras?.getString("mobile").toString()
            lat= intent.extras?.getString("lat").toString()
            lon= intent.extras?.getString("lon").toString()


            val viewModel = ViewModelProvider(this).get(EmergencyUserViewModel::class.java)
            // calling start counter methods which is in our viewmodel class
            viewModel.getEmergencyUserDetails(mobile)
            // observing the second value of our view model class
            viewModel._emergencyUser.observe(this, Observer {
                // setting textview value
                for(element in it){
                    binding.tvUName.text= element.userName
                    binding.tvBloodGroup.text= element.bloodGroup
                    binding.tvDOB.text= element.dob
                    binding.tvHealthDetails.text= element.healthData
                    binding.tvMobile.text= element.mobileNumber
                    val strUri = "http://maps.google.com/maps?q=loc:$lat,$lon"
                    //binding.tvLatLon.text= strUri
                    //binding.tvLatLon.movementMethod = LinkMovementMethod.getInstance()
                    binding.tvLatLon.text = Html.fromHtml(strUri, Html.FROM_HTML_MODE_COMPACT);
                }
            })



        }
        binding.btnCallNow.setOnClickListener(View.OnClickListener {
            if(intent != null) {
                val permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Log.i("call Permission request", "Permission to record denied")
                    makeCallRequest()
                }
                else{
                    val intent = Intent(Intent.ACTION_CALL)

                    intent.data = Uri.parse("tel:" + mobile)
                    startActivity(intent)
                }
            }
        })
        binding.btnCall.setOnClickListener(View.OnClickListener {
            val strUri =
                "http://maps.google.com/maps?q=loc:$lat,$lon"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
            startActivity(intent)
        })
       /* val f = DashboardFragment()
        val t: FragmentTransaction = supportFragmentManager.beginTransaction()
        t.replace(binding.container.id, f).commit()*/

    }

    private fun makeCallRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CALL_PHONE),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG", "Permission has been denied by user")
                } else {
                    val intent = Intent(Intent.ACTION_CALL)

                    intent.data = Uri.parse("tel:" + mobile)
                    startActivity(intent)
                    Log.i("TAG", "Permission has been granted by user")

                }
            }
        }
    }

    private fun addEmergencyContact(){
        val emergencyContactMap:HashMap<String,String> = HashMap<String,String>() //define empty hashmap
        emergencyContactMap.put("name","Alekhya SANAPALA")
        emergencyContactMap.put("mobile","6305786778")
        emergencyContactMap.put("lat","25.6241")
        emergencyContactMap.put("lon","85.0414")
        // FirebaseDatabase.getInstance().getReference().child("emergency_contact").push().setValue(emergencyContactMap)

        database = FirebaseDatabase.getInstance().reference
        database.child("emergency_contact").push().setValue(emergencyContactMap)
    }
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}


