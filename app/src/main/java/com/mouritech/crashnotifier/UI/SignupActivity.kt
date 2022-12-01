package com.mouritech.crashnotifier.UI

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.UI.adapters.ContactDetails
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.data.viewmodel.SignupViewModel
import com.mouritech.crashnotifier.databinding.ActivitySignupBinding
import com.mouritech.crashnotifier.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class SignupActivity : AppCompatActivity() {

    lateinit var dob: TextView
    lateinit var signupViewModel: SignupViewModel
    lateinit var binding: ActivitySignupBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signupViewModel= ViewModelProvider(this)[SignupViewModel::class.java]
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup)
        AddEmergencyContact.data = ArrayList<EmergencyContacts>()

        binding.contactsRV.layoutManager = LinearLayoutManager(this)

        dob = findViewById(R.id.dob)
        dob.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    dob.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        val bloodGroupSpinner = findViewById<Spinner>(R.id.bloodGroupSpinner)
        val bloodGroupsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.blood_groups,
            R.layout.dropdown_item
        )
        bloodGroupsAdapter.setDropDownViewResource(R.layout.dropdown_item)
        bloodGroupSpinner.adapter = bloodGroupsAdapter

        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender,
            R.layout.dropdown_item
        )
        genderSpinnerAdapter.setDropDownViewResource(R.layout.dropdown_item)
        genderSpinner.adapter = genderSpinnerAdapter


        binding.addEmergencyContact.setOnClickListener {
            startActivity(Intent(applicationContext, AddEmergencyContact::class.java))
            Log.d("signup" , "clicked AddEmergencyContact")
        }

        binding.submit.setOnClickListener {

            progress = ProgressDialog(this@SignupActivity)
            Utils.displayProgressBar(progress,"Creating user")
            var valid : Boolean = true
            signupViewModel.userName.value = binding.userName.text.toString()
            signupViewModel.mobileNumber.value = binding.mobileNumber.text.toString()
            signupViewModel.gender.value =  genderSpinner.selectedItem.toString()
            signupViewModel.dob.value = binding.dob.text.toString()
            signupViewModel.bloodGroup.value = bloodGroupSpinner.selectedItem.toString()
            signupViewModel.healthData.value = binding.longDiseaseDesc.text.toString()

            if (signupViewModel.userName.value.toString().isEmpty()){
                valid = false
                binding.userName.error = "Enter User name"
            }
            else if (signupViewModel.mobileNumber.value.toString().isEmpty() &&
                this.signupViewModel.mobileNumber.value.toString().length<10){
                valid = false
                binding.mobileNumber.error = "Enter valid mobile number"
            }
            else if (signupViewModel.gender.value.toString() == "Select Gender"){
                valid = false
                binding.genderInput.error = "Select gender"
            }
            else if (signupViewModel.dob.value.toString() == "DOB"){
                valid = false
                binding.genderInput.error = "Select DOB"
            }
            else if (signupViewModel.bloodGroup.value.toString() == ""){
                valid = false
                binding.bloodGroupInput.error = "Select blood group"

            }
            else if (AddEmergencyContact.data.size==0){
                valid = false
               Toast.makeText(this,"Please add at least one or two emergency contact details",Toast.LENGTH_SHORT).show()
            }

            if (valid){
                signupViewModel.checkDuplication(this@SignupActivity)
            }
            else{
                Utils.stopProgressBar(progress)
            }
        }

    }
    override fun onRestart() {
        if (AddEmergencyContact.data.isNotEmpty()){
            val adapter = ContactDetails(AddEmergencyContact.data)
            binding.contactsRV.adapter = adapter
        }
        super.onRestart()
    }
    companion object{
        lateinit var progress : ProgressDialog
    }
}
