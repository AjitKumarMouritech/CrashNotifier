package com.mouritech.crashnotifier.data.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mouritech.crashnotifier.ui.AddEmergencyContact

import com.mouritech.crashnotifier.utils.Utils
import com.mouritech.crashnotifier.ui.LoginActivity
import com.mouritech.crashnotifier.ui.SignupActivity


class SignupViewModel : ViewModel() {

    var userName: MutableLiveData<String> = MutableLiveData()
    var mobileNumber: MutableLiveData<String> = MutableLiveData()
    var gender: MutableLiveData<String> = MutableLiveData()
    var dob: MutableLiveData<String> = MutableLiveData()
    var bloodGroup: MutableLiveData<String> = MutableLiveData()
    var healthData: MutableLiveData<String> = MutableLiveData()

    private var mAuth: FirebaseAuth?=null
    private lateinit var database: DatabaseReference
    var dataAdded: Boolean = false
    lateinit var usersList: ArrayList<String>

    fun checkElement(
        usersList: ArrayList<String>,
        signupActivity: SignupActivity,
        usersNumberfromDb: Long
    ) {
       if (usersList.isNotEmpty() &&usersList.contains(mobileNumber.value)){
           Utils.stopProgressBar(SignupActivity.progress)
           Toast.makeText(signupActivity, "Entered mobile number is already in use, please try with another one", Toast.LENGTH_SHORT).show()
       }
        else{
            addData(signupActivity,usersNumberfromDb)
       }
    }

    fun addData(signupActivity: SignupActivity, usersNumberfromDb: Long) {
        dataAdded = true
        val newUserDataMap:HashMap<String,String> = HashMap<String,String>()
        newUserDataMap["user_name"] = this.userName.value.toString()
        newUserDataMap["mobile_number"] = this.mobileNumber.value.toString()
        newUserDataMap["gender"] = this.gender.value.toString()
        newUserDataMap["dob"] = this.dob.value.toString()
        newUserDataMap["blood_group"] = this.bloodGroup.value.toString()
        newUserDataMap["health_data"] = this.healthData.value.toString()

        addDataToFirebase(signupActivity,newUserDataMap,usersNumberfromDb)
    }

    private fun addDataToFirebase(
        signupActivity: SignupActivity,
        newUserDataMap: HashMap<String, String>,
        usersNumberfromDb: Long
    ) {
        mAuth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        try{
            dataAdded = true
            database.child("signup").push().setValue(newUserDataMap)

            addUserIDToDb(mobileNumber.value.toString(),signupActivity)


            var uid = "uid_$usersNumberfromDb"

        }catch (error : java.lang.Exception){
            Utils.stopProgressBar(SignupActivity.progress)
            Toast.makeText(signupActivity, "Failed to add data $error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addUserIDToDb(mobileNumber: String, signupActivity: SignupActivity) {
        FirebaseAuth.getInstance()
       // val database = FirebaseDatabase.getInstance()
        val myRef = database.child("signup")
        myRef.orderByChild("mobile_number").equalTo(mobileNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (each_item_snapshot in dataSnapshot.children) {
                        val addUidMap:HashMap<String,String> = HashMap<String,String>()
                        addUidMap["uid"] = each_item_snapshot.key.toString()
                        addUidMap["mobile_number"] = mobileNumber
                        database.child("users").push().setValue(addUidMap)
                        addEmergencyContactsToFirebaseDB(each_item_snapshot.key.toString(),signupActivity)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Update view model", error.toString())
                }

            })
    }

    private fun addEmergencyContactsToFirebaseDB(uid: String, signupActivity: SignupActivity, ) {
        try{

            for (emergency_contact in AddEmergencyContact.data) {
                val addContactsMap:HashMap<String,String> = HashMap<String,String>()
                addContactsMap["uid"] = uid
                addContactsMap["user_mobile_number"] = this.mobileNumber.value.toString()
                addContactsMap["emergency_contact_name"] =  emergency_contact.name
                addContactsMap["emergency_contact_number"] = emergency_contact.mobile
                addContactsMap["lat"] =  "70"
                addContactsMap["long"] = "50"
                database.child("emergency_contact_details").push().setValue(addContactsMap)
            }

            AddEmergencyContact.data = ArrayList()
            Toast.makeText(signupActivity, "Signup is success, please login with registered mobile number", Toast.LENGTH_SHORT).show()
            signupActivity.startActivity(Intent(signupActivity , LoginActivity::class.java))
            signupActivity.finish()

        }catch (exception:Exception){
            Utils.stopProgressBar(SignupActivity.progress)
            Toast.makeText(signupActivity, "exception while adding emergency contacts $exception", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun checkDuplication(signupActivity: SignupActivity) {
        dataAdded = false
        usersList = ArrayList()
        mAuth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        database.child("signup").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!dataAdded){
                    for (each_item_snapshot in snapshot.children) {
                        usersList.add(each_item_snapshot.child("mobile_number").value.toString())
                    }

                    checkElement(usersList,signupActivity,snapshot.childrenCount+1)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Utils.stopProgressBar(SignupActivity.progress)
                Toast.makeText(signupActivity, "Failed to fetch data $error", Toast.LENGTH_SHORT).show()
            }
        })
    }


}