package com.mouritech.crashnotifier.UI

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity  : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    var number : String =""
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var progress : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            login()
        }

        binding.submit.setOnClickListener {
            val otp =binding.enterOTP.text?.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
                Log.d("dashboard" , "onVerificationCompleted Success")
            }

            override fun onVerificationFailed(e: FirebaseException) {
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
                progress.hide()
                binding.sendOTP.text = "Resend"
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                   val intent = Intent(this , MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    private fun login() {
        number = binding.mobileNumber.text?.trim().toString()
        if (number.isNotEmpty()){
            number = "+91$number"
            displayProgressBar()
            sendVerificationCode(number)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayProgressBar() {
        progress = ProgressDialog(this);
        progress.setTitle("Sending OTP")
        progress.setMessage("Wait!!")
        progress.setCancelable(true)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show()
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("login" , "Auth started")
    }
}