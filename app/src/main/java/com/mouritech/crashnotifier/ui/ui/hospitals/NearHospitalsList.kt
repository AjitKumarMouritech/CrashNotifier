package com.mouritech.crashnotifier.ui.ui.hospitals

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel

class NearHospitalsList : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mMap : GoogleMap
    private var mapReady = false
    private lateinit var viewModel: EmergencyContactViewModel
    private lateinit var emergencyContactList: List<EmergencyContacts>
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
    private var lat:String= ""
    private var lon:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_hospitals_list, container, false)
        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        getLastLocation()
        val mapFragment =   childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
                googleMap -> mMap = googleMap
            mapReady = true
            updateMap()
        }
        return rootView
    }

    private fun updateMap() {
        if (mapReady && emergencyContactList!=null){
            mMap.clear()

            emergencyContactList.forEach{

                // Creating a marker
                // Creating a marker
                val markerOptions = MarkerOptions()

                // Setting the position for the marker

                // Setting the position for the marker
                val lat = it.lat
                val lon = it.lon

                markerOptions.position(LatLng( lat.toDouble(),lon.toDouble()))

                // Setting the title for the marker.
                // This will be displayed on taping the marker

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(it.lat + " : " + it.lon)

                // Clears the previously touched position

                // Clears the previously touched position

                // Animating to the touched position

                // Animating to the touched position

                mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.lat.toDouble(),it.lon.toDouble())))

                // Placing a marker on the touched position
                val latlon:LatLng = LatLng(lat.toDouble(),lon.toDouble())
                // Placing a marker on the touched position
                mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlon))
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        emergencyContactList= ArrayList()

        activity.let {
            viewModel = ViewModelProvider(it!!).get(EmergencyContactViewModel::class.java)
            viewModel.getEmergencyContact()
            viewModel._emergencyContacts.observe(viewLifecycleOwner, Observer {
                emergencyContactList = viewModel._emergencyContacts.value!!
                Log.d("first value ", emergencyContactList.get(0).toString())
                updateMap()
            })

        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                activity?.let {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            lat= location.latitude.toString()
                            lon= location.longitude.toString()
                            //findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                            // findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location? = locationResult.lastLocation
            lat= mLastLocation?.latitude.toString()
            lon= mLastLocation?.longitude.toString()
            // findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            // findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
       // var locationManager: LocationManager = context.getSystemService(activity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}