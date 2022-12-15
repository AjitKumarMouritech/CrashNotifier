package com.mouritech.crashnotifier.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.ui.adapters.ContactDetails
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.databinding.ActivityAddEmergencyContactBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddEmergencyContact : AppCompatActivity() {
    lateinit var binding: ActivityAddEmergencyContactBinding
    lateinit var mLastLocation: Location
    lateinit var lat: String
    lateinit var long: String
    private lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EmergencyContactViewModel::class.java]
        viewModel.emergencyContactList = ArrayList()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_emergency_contact)

        binding.contactsRV.layoutManager = LinearLayoutManager(this)
        adapter = ContactDetails(data,"signup_add_emergency_contacts")
        binding.contactsRV.adapter = adapter
        viewModel._emergencyContacts.value = ArrayList()



        mLocationRequest = LocationRequest()
        if (checkPermissionForLocation(this)) {
            mLocationRequest = LocationRequest()

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            startLocationUpdates()
        }


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
                valid = false
                Toast.makeText(this,"Enter valid mobile Number",Toast.LENGTH_SHORT).show()
            }
            else if (viewModel.name.value.toString().isEmpty()){
                valid = false
                Toast.makeText(this,"Enter user name",Toast.LENGTH_SHORT).show()
            }

            if (valid){
                viewModel.emergencyContactList = ArrayList()
                binding.mobileNumber.setText("")
                binding.contactName.setText("")
                viewModel.addData("", "0", "0")
            }
        }

        binding.submit.setOnClickListener {
            if (data.isEmpty()){
                Toast.makeText(this,"Please add atleast one or two emergency contacts",Toast.LENGTH_SHORT).show()
            }
            else{
                //startActivity(Intent(applicationContext,SignupActivity::class.java))
                super.onBackPressed()
            }
        }

    }

    private fun startLocationUpdates() {
        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here

            locationResult.lastLocation
            locationResult.lastLocation?.let {
                onLocationChanged(it)
                lat= it?.latitude.toString()
                long= it?.longitude.toString()
                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val myEdit = sharedPreferences.edit()
                myEdit.putString("lat",lat)
                myEdit.putString("lon",long)
                myEdit.commit()
            }
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        //  txtTime.text = "Updated at : " + sdf.format(date)
        // txtLat.text = "LATITUDE : " + mLastLocation.latitude
        //  txtLong.text = "LONGITUDE : " + mLastLocation.longitude
        // You can now create a LatLng Object for use with maps
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                //  btnStartupdate.isEnabled = false
                //  btnStopUpdates.isEnabled = true
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        /*if (requestCode == REQUEST_PERMISSION_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //sendSMS()
                //  btnStartupdate.isEnabled = false
                //  btnStopUpdates.isEnabled = true
            } else {
                Toast.makeText(this@Main2Activity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    private fun setDataToAdapter(it: List<EmergencyContacts>?) {
        data.add(
            EmergencyContacts(
                it!![0].emergency_contact_number,
                it[0].emergency_contact_name,
                it[0].lat,
                it[0].lon,
                it[0].fcm_token
            )
        )
        val adapter = ContactDetails(data,"signup_add_emergency_contacts")
        binding.contactsRV.adapter = adapter
    }

    companion object{
        var data = ArrayList<EmergencyContacts>()
        lateinit var viewModel: EmergencyContactViewModel
        lateinit var adapter:ContactDetails

        @SuppressLint("NotifyDataSetChanged")
        fun remove (position:Int){
            data.removeAt(position)
            adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(position)
            adapter.notifyItemRemoved(position)
        }
    }
}