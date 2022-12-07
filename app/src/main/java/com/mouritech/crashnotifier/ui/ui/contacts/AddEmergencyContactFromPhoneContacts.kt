package com.mouritech.crashnotifier.ui.ui.contacts

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.ui.adapters.ContactDetails
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.databinding.ActivityAddEmergencyContactBinding
import com.mouritech.crashnotifier.ui.SignupActivity
import com.mouritech.crashnotifier.utils.Utils


class AddEmergencyContactFromPhoneContacts : AppCompatActivity() {
    lateinit var binding: ActivityAddEmergencyContactBinding
    lateinit var viewModel: EmergencyContactViewModel
    private var namesArrayList: ArrayList<String>? = null
    private var numbersArrayList: ArrayList<String>? = null
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    lateinit var contactName1 : AutoCompleteTextView
    lateinit var contactNameAdapter: ArrayAdapter<String>
    var usersList: ArrayList<String> = ArrayList()
    lateinit var fcmToken : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EmergencyContactViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_emergency_contact)

        binding.contactsRV.layoutManager = LinearLayoutManager(this)
        binding.contactNameInput1.visibility = View.VISIBLE
        binding.contactNameInput.visibility = View.INVISIBLE
        binding.mobileNumberInput.visibility = View.INVISIBLE

        contactName1 = findViewById<AutoCompleteTextView>(R.id.contactName1)
        viewModel.emergencyContactList = ArrayList()

        checkContactPermission()

        binding.addButton.setOnClickListener {
            val index: Int = namesArrayList!!.indexOf(contactName1.text.toString())
            if (index == -1){
                Toast.makeText(this,"Invalid contact number,please try again with valid one",Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.mobileNumber.value = numbersArrayList!![index]
                viewModel.name.value = contactName1.text.toString()
                checkDuplication()

            }

        }

        viewModel._emergencyContacts.observe(this, Observer {data->
            val adapter = ContactDetails(data)
            binding.contactsRV.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.contactName1.setText("")
        })

        binding.submit.setOnClickListener {
            if (viewModel.emergencyContactList.isNullOrEmpty()){
                Toast.makeText(this,"Add atleast one contact to save to db",Toast.LENGTH_SHORT).show()
            }
            else{
                progress  = ProgressDialog(this)
                Utils.displayProgressBar(progress,"Adding emergency contacts")
                val preferences = this.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
                viewModel.updateEmergencyContacts(viewModel.emergencyContactList,Utils.mobileNumber(preferences),
                    Utils.getUserID(preferences),this)

            }
        }

    }

    private fun checkDuplication() {
        var mAuth: FirebaseAuth?=null
        usersList= ArrayList()
        mAuth= FirebaseAuth.getInstance()

        var database: DatabaseReference = FirebaseDatabase.getInstance().reference
        database.child("signup").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (each_item_snapshot in snapshot.children) {
                    usersList.add(each_item_snapshot.child("mobile_number").value.toString())
                    var number = viewModel.mobileNumber.value?.replace(" ","")
                    if (each_item_snapshot.child("mobile_number").value.toString() == number){
                        fcmToken = each_item_snapshot.child("fcm_token").value.toString()
                        break
                        //Toast.makeText(this,viewModel.mobileNumber.value,Toast.LENGTH_SHORT).show()
                    }
                    else{
                        fcmToken = "NA"
                    }
                }

                viewModel.addData(fcmToken)
            }
            override fun onCancelled(error: DatabaseError) {
               // Toast.makeText(AddEmergencyContactFromPhoneContacts::class.java, "Failed to fetch data $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }
        else{
            getContacts()
        }
    }

    private fun getContacts() {

        namesArrayList = ArrayList()
        numbersArrayList = ArrayList()
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        while (phones!!.moveToNext()) {
            val name = phones?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                ?.let { phones?.getString(it) }
            val phoneNumber = phones?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                ?.let { phones?.getString(it) }

            namesArrayList!!.add(name.toString())
            numbersArrayList!!.add(phoneNumber.toString())

            Log.d("name>>", name + "  " + phoneNumber)
        }
        phones.close()

        contactNameAdapter= ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, namesArrayList!!.toList())
        contactName1.threshold = 1
        contactName1.setAdapter(contactNameAdapter)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts()
            } else {
                Toast.makeText(
                    this,
                    "Please give permissions to add emergency contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    companion object{
        lateinit var progress : ProgressDialog
    }
}
