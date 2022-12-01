package com.mouritech.crashnotifier.data.viewmodel


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.mouritech.crashnotifier.UI.LoginActivity
import com.mouritech.crashnotifier.utils.Utils
import com.mouritech.crashnotifier.ui.LoginActivity
import java.util.concurrent.TimeUnit


class LoginViewModel: ViewModel() {

    var mobileNumber: MutableLiveData<String> = MutableLiveData()
    var otp: MutableLiveData<String> = MutableLiveData()
    private var mAuth: FirebaseAuth?=null
    private lateinit var database: DatabaseReference
    lateinit var usersList: ArrayList<String>
    lateinit var auth: FirebaseAuth

    fun isMobileNumberValid(): Boolean {
        val number = this.mobileNumber.value.toString()
        return number.isNotEmpty() && number.length == 10
    }

    fun isOTPValid() : Boolean{
        val otp = this.otp.value.toString()
        return otp.isNotEmpty()
    }

     fun  checkDuplication(context: LoginActivity) {
        usersList = ArrayList()
        mAuth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        database.child("signup").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (each_item_snapshot in snapshot.children) {
                    usersList.add(each_item_snapshot.child("mobile_number").value.toString())
                }
                sendVerificationCode(usersList,context)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch data $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendVerificationCode(usersList: ArrayList<String>, context: LoginActivity) {

        if (usersList.isNotEmpty() && usersList.contains(mobileNumber.value)){
            var number = mobileNumber.value.toString()
            number = "+91$number"
            val options = PhoneAuthOptions.newBuilder(LoginActivity.auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context)
                .setCallbacks(LoginActivity.callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d("login" , "Auth started")
        }

        else{
            Utils.stopProgressBar(LoginActivity.progress)
            Toast.makeText(context,"Entered mobile number is not registered, please Signup to continue",Toast.LENGTH_SHORT).show()
        }
    }
}