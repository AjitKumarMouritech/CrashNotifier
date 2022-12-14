package com.mouritech.crashnotifier.ui.ui.contacts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.viewmodel.EmergencyContactViewModel
import com.mouritech.crashnotifier.utils.Utils


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mMap : GoogleMap
    private var mapReady = false
    private lateinit var viewModel: EmergencyContactViewModel
    private lateinit var emergencyContactList: List<EmergencyContacts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val mapFragment =   childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap -> mMap = googleMap
            mapReady = true
           // updateMap()
        }
        return rootView
    }

    private fun updateMap() {
        val preferences = activity?.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        val userLat = preferences?.let { Utils.getUserLat(it) }
        val userLon = preferences?.let { Utils.getUserLon(it) }

        if (mapReady && emergencyContactList!=null){
            mMap.clear()

            emergencyContactList.forEach{

                val markerOptions = MarkerOptions()
                val lat = it.lat
                val lon = it.lon

                if (lat!="null" && lon!="null"){
                    markerOptions.position(LatLng( lat.toDouble(),lon.toDouble()))
                    markerOptions.title(it.emergency_contact_name)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.lat.toDouble(),it.lon.toDouble())))
                    mMap.addMarker(markerOptions)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        emergencyContactList= ArrayList()

        activity.let {
            viewModel = ViewModelProvider(it!!).get(EmergencyContactViewModel::class.java)
            //val preferences = requireActivity().getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
            //viewModel.getEmergencyContact2( Utils.getUserID(preferences),"")
            viewModel.getEmergencyContact()
            viewModel._emergencyContacts.observe(viewLifecycleOwner, Observer {
                    emergencyContactList = viewModel._emergencyContacts.value!!
                Log.d("first value ", emergencyContactList.get(0).toString())
                updateMap()
            })
          
        }
    }
}