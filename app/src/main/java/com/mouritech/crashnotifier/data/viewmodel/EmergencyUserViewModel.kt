package com.mouritech.crashnotifier.data.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.model.Signup
import com.mouritech.crashnotifier.ui.ui.contacts.UpdateEmergencyContacts
import com.mouritech.crashnotifier.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EmergencyUserViewModel : ViewModel()  {
    lateinit var emergencyUserList: ArrayList<Signup>
    var _emergencyUser= MutableLiveData<List<Signup>>()
    val emergencyUser: MutableLiveData<List<Signup>>
        get()=_emergencyUser
    fun getEmergencyUserDetails(mobile: String){
        //_emergencyContacts.value= repository.getEmergencyContact()
        emergencyUserList= ArrayList()

        val job= CoroutineScope(Dispatchers.IO).async {
            getEmergencyUserDetailsList(mobile)
        }
    }
    private suspend fun getEmergencyUserDetailsList(mobile:String):List<Signup> {
        //val database = FirebaseDatabase.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("signup")
        myRef.orderByChild("mobile_number").equalTo(mobile).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (item_snapshot in dataSnapshot.children) {
                        val signup: Signup = Signup(
                            item_snapshot.child("user_name").value.toString(),
                            item_snapshot.child("mobile_number").value.toString(),
                            item_snapshot.child("gender").value.toString(),
                            item_snapshot.child("dob").value.toString(),
                            item_snapshot.child("blood_group").value.toString(),
                            item_snapshot.child("health_data").value.toString()
                        )
                        emergencyUserList.add(signup)
                        Log.d("item id ", signup.toString())
                    }
                    _emergencyUser.value=emergencyUserList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Update view model", error.toString())
                    Utils.stopProgressBar(UpdateEmergencyContacts.progress)
                }

            })
        return emergencyUserList

    }

}