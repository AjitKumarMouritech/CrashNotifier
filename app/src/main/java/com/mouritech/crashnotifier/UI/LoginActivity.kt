package com.mouritech.crashnotifier.UI

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.viewmodel.LoginViewModel
import com.mouritech.crashnotifier.data.viewmodel.SignupViewModel
import com.mouritech.crashnotifier.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity  : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    var number : String =""
    lateinit var auth: FirebaseAuth
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
            displayProgressBar()
            loginViewModel!!.mobileNumber.value = binding.mobileNumber.text?.trim().toString()

            if (loginViewModel!!.isMobileNumberValid()){

               loginViewModel.checkDuplication(this@LoginActivity,)
            }
            else{
                progress.hide()
                binding.mobileNumber.setText("")
                Toast.makeText(this,"Enter valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.submit.setOnClickListener {
            loginViewModel!!.otp.value =binding.enterOTP.text?.trim().toString()

            if(loginViewModel!!.isOTPValid()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), loginViewModel!!.otp.value.toString())
                signInWithPhoneAuthCredential(credential)
            }else{
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
                progress.hide()
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
                    successfullyLoggedIn("LoggedIn",true)
                   //val intent = Intent(this , MainActivity::class.java)
                   val intent = Intent(this , Main2Activity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    private fun successfullyLoggedIn(key: String, value: Boolean) {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("login", 2)
        myEdit.commit()
    }

    private fun displayProgressBar() {
        progress = ProgressDialog(this);
        progress.setTitle("Sending OTP")
        progress.setMessage("Wait!!")
        progress.setCancelable(true)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show()
    }
    companion object{
        lateinit var auth: FirebaseAuth
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        lateinit var progress : ProgressDialog
    }
}