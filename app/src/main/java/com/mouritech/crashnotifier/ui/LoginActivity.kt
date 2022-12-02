package com.mouritech.crashnotifier.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.UI.Main2Activity
import com.mouritech.crashnotifier.UI.ui.health_details.UpdateHealthDetails
import com.mouritech.crashnotifier.data.viewmodel.LoginViewModel
import com.mouritech.crashnotifier.data.viewmodel.SignupViewModel
import com.mouritech.crashnotifier.databinding.ActivityLoginBinding
import com.mouritech.crashnotifier.utils.Utils
import io.grpc.okhttp.internal.Util


class LoginActivity  : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var loginViewModel: LoginViewModel
    lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        auth=FirebaseAuth.getInstance()
        binding.enterOTP.visibility =  View.GONE
        binding.submit.visibility =  View.GONE

        binding.newUser.setOnClickListener {
            startActivity(Intent(applicationContext, SignupActivity::class.java))
            finish()
            Log.d("login" , "clicked newUser")
        }

        binding.sendOTP.setOnClickListener {
            progress = ProgressDialog(this@LoginActivity)
            Utils.displayProgressBar(progress,"Sending OTP")
            loginViewModel.mobileNumber.value = binding.mobileNumber.text?.trim().toString()

            if (loginViewModel.isMobileNumberValid()){

               loginViewModel.checkDuplication(this@LoginActivity,)
            }
            else{
                Utils.stopProgressBar(progress)
                binding.mobileNumber.setText("")
                Toast.makeText(this,"Enter valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.submit.setOnClickListener {
            progress = ProgressDialog(this)
            Utils.displayProgressBar(progress,"Logging in")
            loginViewModel.otp.value =binding.enterOTP.text?.trim().toString()

            if(loginViewModel.isOTPValid()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), loginViewModel.otp.value.toString())
                signInWithPhoneAuthCredential(credential)
            }else{
                Utils.stopProgressBar(progress)
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, Main2Activity::class.java))
                finish()
                Log.d("dashboard" , "onVerificationCompleted Success")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Utils.stopProgressBar(progress)
                Log.d("login" , "onVerificationFailed $e")
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("login","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

                binding.enterOTP.visibility =  View.VISIBLE
                binding.submit.visibility =  View.VISIBLE
                Utils.stopProgressBar(progress)
                //binding.sendOTP.text = "Resend"
                setTimerToResendOtp()
            }
        }
    }

    private fun setTimerToResendOtp() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.sendOTP.text = "You can request otp in " + millisUntilFinished / 1000+" seconds"
                binding.sendOTP.isClickable = false
            }
            override fun onFinish() {
                binding.sendOTP.isClickable = true
                binding.sendOTP.text = "Resend"
            }
        }.start()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setUserID()
                    setUserName()
                    successfullyLoggedIn("LoggedIn",true)
                    Utils.stopProgressBar(progress)
                   val intent = Intent(this , Main2Activity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Utils.stopProgressBar(progress)
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    private fun setUserName() {
        FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("signup")
        myRef.orderByChild("mobile_number").equalTo(loginViewModel.mobileNumber.value.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (each_item_snapshot in dataSnapshot.children) {
                            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                            val myEdit = sharedPreferences.edit()
                            myEdit.putString("user_name",each_item_snapshot.child("user_name").value.toString())
                            myEdit.commit()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("User", error.toString())
                }

            })
    }

    private fun setUserID() {

        FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("emergency_contact_details")
        myRef.orderByChild("user_mobile_number").equalTo(loginViewModel.mobileNumber.value.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (each_item_snapshot in dataSnapshot.children) {
                            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                            val myEdit = sharedPreferences.edit()
                            myEdit.putString("uid",each_item_snapshot.child("uid").value.toString())
                            myEdit.commit()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("User", error.toString())
                }

            })
    }

    private fun successfullyLoggedIn(key: String, value: Boolean) {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("login", 2)
        myEdit.putString("login_mobile_number",loginViewModel.mobileNumber.value.toString())
        myEdit.commit()
    }
    companion object{
        lateinit var auth: FirebaseAuth
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        lateinit var progress : ProgressDialog
    }
}