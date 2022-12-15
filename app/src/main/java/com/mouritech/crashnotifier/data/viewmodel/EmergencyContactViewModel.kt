package com.mouritech.crashnotifier.data.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.ui.Main2Activity
import com.mouritech.crashnotifier.ui.ui.contacts.AddEmergencyContactFromPhoneContacts
import com.mouritech.crashnotifier.ui.ui.contacts.UpdateEmergencyContacts
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.utils.Utils
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
    fun getEmergencyContact2(userID: String) {

        emergencyContactList= ArrayList()
        val job= CoroutineScope(Dispatchers.IO).async {
             getEmergencyContactList2(userID)
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
                        item_snapshot.child("lon").value.toString(),
                        item_snapshot.child("fcm_token").value.toString()
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


    private suspend fun getEmergencyContactList2(userID: String):List<EmergencyContacts> {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("emergency_contact_details")
        myRef.orderByChild("uid").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (item_snapshot in dataSnapshot.children) {
                        val emergencyContacts: EmergencyContacts = EmergencyContacts(
                            item_snapshot.child("emergency_contact_number").value.toString(),
                            item_snapshot.child("emergency_contact_name").value.toString(),
                            item_snapshot.child("lat").value.toString(),
                            item_snapshot.child("long").value.toString(),
                            item_snapshot.child("fcm_token").value.toString()

                        )
                        emergencyContactList.add(emergencyContacts)
                        Log.d("item id ", emergencyContacts.toString())
                    }
                    _emergencyContacts.value=emergencyContactList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Update view model", error.toString())
                    Utils.stopProgressBar(UpdateEmergencyContacts.progress)
                }

            })
        return emergencyContactList
    }

    fun addData(fcm: String, lat: String, long: String){
        //emergencyContactList = ArrayList()
        val emergencyContacts: EmergencyContacts = EmergencyContacts(
            mobileNumber.value.toString(),name.value.toString(),lat,long, fcm
        )
        emergencyContactList.add(emergencyContacts)
        _emergencyContacts.value=emergencyContactList
    }



    fun updateEmergencyContacts(
        emergencyContactList: ArrayList<EmergencyContacts>,
        mobileNumber: String,
        userID: String,
        context: AddEmergencyContactFromPhoneContacts
    ) {
        FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("emergency_contact_details")
        try{

            for (emergency_contact in emergencyContactList) {

                val addContactsMap:HashMap<String,Any> = HashMap<String,Any>()
                addContactsMap["uid"] = userID
                addContactsMap["user_mobile_number"] = mobileNumber
                addContactsMap["emergency_contact_name"] =  emergency_contact.emergency_contact_name
                addContactsMap["emergency_contact_number"] = emergency_contact.emergency_contact_number
                addContactsMap["lat"] =  emergency_contact.lat
                addContactsMap["long"] = emergency_contact.lon
                addContactsMap["fcm_token"] = emergency_contact.fcm_token
                myRef.push().updateChildren(addContactsMap)
            }
            Toast.makeText(context, "Contacts added successfully", Toast.LENGTH_SHORT)
                .show()
            Utils.stopProgressBar(AddEmergencyContactFromPhoneContacts.progress)
           context.startActivity(Intent(context,Main2Activity::class.java))

        }catch (exception:Exception){
            Utils.stopProgressBar(AddEmergencyContactFromPhoneContacts.progress)
            Toast.makeText(context, "exception while adding emergency contacts $exception", Toast.LENGTH_SHORT)
                .show()
        }

    }

}