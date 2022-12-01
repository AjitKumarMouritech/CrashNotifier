package com.mouritech.crashnotifier.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.data.model.EmergencyContacts


class EmergencyContactRepositoryIMP: EmergencyContactRepository {
    override fun getEmergencyContact(): List<EmergencyContacts> {
        //Get data from Firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        var  emergencyContactList: List<EmergencyContacts>? = null

        // Read from the database
        myRef.child("emergency_contact").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (item_snapshot in dataSnapshot.children) {
                    val emergencyContacts:EmergencyContacts= EmergencyContacts(item_snapshot.child("mobile").toString(), item_snapshot.child("name").toString(),item_snapshot.child("lat").toString(),item_snapshot.child("lon").toString())
                    emergencyContactList.apply {
                        emergencyContacts
                    }
                    Log.d("item id ", emergencyContacts.toString())
                }
                // val emergencyContacts: EmergencyContacts? =dataSnapshot.getValue(EmergencyContacts::class.java)

                //val value = dataSnapshot.getValue(String::class.java)

                //  Log.d("TAG", "Value is: $emergencyContacts")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
        return arrayListOf()
    }

}