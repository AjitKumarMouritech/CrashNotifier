package com.mouritech.crashnotifier.UI

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.UI.AddEmergencyContact
import java.util.*


class SignupActivity : AppCompatActivity() {

    lateinit var dob: TextView
    lateinit var addEmergencyContact : Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dob = findViewById(R.id.dob)
        addEmergencyContact = findViewById<Button>(R.id.addEmergencyContact)
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


        addEmergencyContact.setOnClickListener {
            startActivity(Intent(applicationContext, AddEmergencyContact::class.java))
            finish()
            Log.d("signup" , "clicked AddEmergencyContact")
        }
    }
}
