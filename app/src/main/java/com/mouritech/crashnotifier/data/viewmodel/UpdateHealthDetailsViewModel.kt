package com.mouritech.crashnotifier.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.ui.ui.health_details.UpdateHealthDetails
import com.mouritech.crashnotifier.utils.Utils


class UpdateHealthDetailsViewModel  : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Update health details"
    }
    val text: LiveData<String> = _text

    val _bloodGroup = MutableLiveData<String>()
    val bloodGroup : LiveData<String> = _bloodGroup

    val _healthData = MutableLiveData<String>()
    val healthData : LiveData<String> = _healthData

    fun getDetails(mobileNumber: String?) {

        FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference().child("signup")
        myRef.orderByChild("mobile_number").equalTo(mobileNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (each_item_snapshot in dataSnapshot.children) {
                        _bloodGroup.value = each_item_snapshot.child("blood_group").value.toString()
                        _healthData.value = each_item_snapshot.child("health_data").value.toString()
                    }
                   // UpdateHealthDetails.stopProgressBar()
                    Utils.stopProgressBar(UpdateHealthDetails.progress)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Update view model", error.toString())
                   // UpdateHealthDetails.stopProgressBar()
                    Utils.stopProgressBar(UpdateHealthDetails.progress)

                }

            })
    }

    fun editData(mobileNumber: String) {

        FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("signup")
        myRef.orderByChild("mobile_number").equalTo(mobileNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if(dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {
                            val map = HashMap<String, Any>()
                            map["blood_group"] = bloodGroup.value.toString()
                            map["health_data"] = healthData.value.toString()
                            ds.ref.updateChildren(map)
                        }
                    }
                   // UpdateHealthDetails.stopProgressBar()
                    Utils.stopProgressBar(UpdateHealthDetails.progress)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("User", error.toString())
                    //UpdateHealthDetails.stopProgressBar()
                    Utils.stopProgressBar(UpdateHealthDetails.progress)

                }

            })

    }
}