package com.mouritech.crashnotifier.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.databinding.ActivityAddEmergencyContactBinding

class AddEmergencyContact : AppCompatActivity() {
    lateinit var binding: ActivityAddEmergencyContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_emergency_contact)
    }
}