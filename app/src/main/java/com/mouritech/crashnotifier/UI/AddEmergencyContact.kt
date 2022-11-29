package com.mouritech.crashnotifier.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.UI.adapters.ContactDetails
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.data.viewmodel.SignupViewModel
import com.mouritech.crashnotifier.databinding.ActivityAddEmergencyContactBinding

class AddEmergencyContact : AppCompatActivity() {
    lateinit var binding: ActivityAddEmergencyContactBinding
    lateinit var viewModel: EmergencyContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EmergencyContactViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_emergency_contact)

        binding.contactsRV.layoutManager = LinearLayoutManager(this)
        val adapter = ContactDetails(data)
        binding.contactsRV.adapter = adapter
        viewModel._emergencyContacts.value = ArrayList()


        viewModel._emergencyContacts.observe(this, Observer {

            if (it.isNotEmpty()){
                setDataToAdapter(it)
            }

        })
        binding.addButton.setOnClickListener {
            viewModel.mobileNumber.value = binding.mobileNumber.text.toString()
            viewModel.name.value = binding.contactName.text.toString()

            var valid : Boolean = true

            if (viewModel.mobileNumber.value.toString().isEmpty() &&
                    viewModel.mobileNumber.value.toString().length<10){
                valid = false;
                Toast.makeText(this,"Enter valid mobile Number",Toast.LENGTH_SHORT).show()
            }
            else if (viewModel.name.value.toString().isEmpty()){
                valid = false;
                Toast.makeText(this,"Enter user name",Toast.LENGTH_SHORT).show()
            }

            if (valid){
                binding.mobileNumber.setText("")
                binding.contactName.setText("")
               viewModel.addData()
            }
        }

        binding.submit.setOnClickListener {
            if (data.isEmpty()){
                Toast.makeText(this,"Please add atleast one or two emergency contacts",Toast.LENGTH_SHORT).show()
            }
            else{
                //startActivity(Intent(applicationContext,SignupActivity::class.java))
                super.onBackPressed();
            }
        }

    }

    private fun setDataToAdapter(it: List<EmergencyContacts>?) {
        data.add(
            EmergencyContacts(
                it!![0].mobile,
                it!![0].name,
                it!![0].lat,
                it!![0].lon,
            )
        )
        val adapter = ContactDetails(data)
        binding.contactsRV.adapter = adapter
    }

    companion object{
        val data = ArrayList<EmergencyContacts>()
    }
}