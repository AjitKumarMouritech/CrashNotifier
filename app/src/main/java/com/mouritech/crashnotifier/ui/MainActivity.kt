package com.mouritech.crashnotifier.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: EmergencyContactViewModel by viewModels()
   private lateinit var database: DatabaseReference
    private val currentFragment: Fragment? = null

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
        val f = DashboardFragment()
        val t: FragmentTransaction = supportFragmentManager.beginTransaction()
        t.replace(binding.container.id, f).commit()

    }


    private fun addEmergencyContact(){
        val emergencyContactMap:HashMap<String,String> = HashMap<String,String>() //define empty hashmap
        emergencyContactMap.put("name","Alekhya SANAPALA")
        emergencyContactMap.put("mobile","6305786778")
        emergencyContactMap.put("lat","25.6241")
        emergencyContactMap.put("lon","85.0414")
        // FirebaseDatabase.getInstance().getReference().child("emergency_contact").push().setValue(emergencyContactMap)

        database = FirebaseDatabase.getInstance().getReference()
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


