package com.mouritech.crashnotifier.data.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.repository.EmergencyContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EmergencyContactViewModel: ViewModel() {

    var mobileNumber: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()

    lateinit var emergencyContactList: ArrayList<EmergencyContacts>
    var _emergencyContacts= MutableLiveData<List<EmergencyContacts>>()
    val emergencyContacts: MutableLiveData<List<EmergencyContacts>>
            get()=_emergencyContacts

    fun getEmergencyContact(){
        //_emergencyContacts.value= repository.getEmergencyContact()
        emergencyContactList= ArrayList()

        val job= CoroutineScope(Dispatchers.IO).async {
             getEmergencyContactList()
        }

    }
    private suspend fun getEmergencyContactList():List<EmergencyContacts> {
        // Read from the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        myRef.child("emergency_contact").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (item_snapshot in dataSnapshot.children) {
                    val emergencyContacts: EmergencyContacts = EmergencyContacts(
                        item_snapshot.child("mobile").value.toString(),
                        item_snapshot.child("name").value.toString(),
                        item_snapshot.child("lat").value.toString(),
                        item_snapshot.child("lon").value.toString()
                    )
                    emergencyContactList.add(emergencyContacts)
                    Log.d("item id ", emergencyContacts.toString())
                }
                _emergencyContacts.value=emergencyContactList
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
        return emergencyContactList

    }

    fun addData(){
        emergencyContactList = ArrayList()
        val emergencyContacts: EmergencyContacts = EmergencyContacts(
            mobileNumber.value.toString(),name.value.toString(),"50","70"
        )
        emergencyContactList.add(emergencyContacts)
        _emergencyContacts.value=emergencyContactList
    }

}