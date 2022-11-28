package com.mouritech.crashnotifier.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

}


