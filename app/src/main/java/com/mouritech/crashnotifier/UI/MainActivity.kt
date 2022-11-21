package com.mouritech.crashnotifier.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.viewmodel.EmegencyContactViewModel

import com.mouritech.crashnotifier.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
   // private val viewModel: EmegencyContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
       // val viewModel = ViewModelProvider(this).get(EmegencyContactViewModel::class.java)
        // calling start counter methods which is in our viewmodel class
/*        viewModel.getEmergencyContact()
        // observing the second value of our view model class
        viewModel._emergencyContacts.observe(this, Observer {
            // setting textview value
            binding.quotes = it.toString()
        })*/
    }
}