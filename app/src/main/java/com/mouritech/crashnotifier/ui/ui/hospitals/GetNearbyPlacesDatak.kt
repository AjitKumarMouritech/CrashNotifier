package com.mouritech.crashnotifier.ui.ui.hospitals

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class GetNearbyPlacesDatak: AsyncTask<Object, String, String>() {

    var mMap: GoogleMap? = null
    var url: String? = null
    var googlePlacesData: String? = null
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(s: String?) {
        val nearbyPlaceList: List<HashMap<String, String>>
        val parser = DataParser()
        nearbyPlaceList = parser.parse(s)
        Log.d("nearbyplacesdata", "called parse method")
        showNearbyPlaces(nearbyPlaceList)
    }

    open fun showNearbyPlaces(nearbyPlaceList: List<HashMap<String, String>>) {
        //todo remove sout
        println("IN show nearby places , listSize = " + nearbyPlaceList.size)
        for (i in nearbyPlaceList.indices) {
            val markerOptions = MarkerOptions()
            val googlePlace = nearbyPlaceList[i]
            val placeName = googlePlace["place_name"]

            //todo remove sout
            println("place name = $placeName")
            val vicinity = googlePlace["vicinity"]
            val lat = googlePlace["lat"]!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title("$placeName : $vicinity")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
        }
    }

    
    override fun doInBackground(vararg objects: Object?): String? {
        mMap = objects[0] as GoogleMap?
        mMap = objects[0] as GoogleMap?
        url = objects[1] as String?
        val downloadURL = DownloadURL()
        try {
            googlePlacesData = downloadURL.readUrl(url)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return googlePlacesData
    }

    fun execute(dataTransfer: Array<Any?>): String? {
        mMap = dataTransfer[0] as GoogleMap?
        mMap = dataTransfer[0] as GoogleMap?
        url = dataTransfer[1] as String?
        val downloadURL = DownloadURL()
        try {
            googlePlacesData = downloadURL.readUrl(url)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return googlePlacesData
    }
}
