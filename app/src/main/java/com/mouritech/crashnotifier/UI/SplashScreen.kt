package com.mouritech.crashnotifier.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.databinding.ActivitySplashScreenBinding



class SplashScreen : AppCompatActivity() {

    lateinit var binding : ActivitySplashScreenBinding
    private val SPLASH_TIME_OUT = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        Handler().postDelayed(Runnable
        {

            if (checkLoginData()==2){
                val i = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(i)
                finish()
            }
            else{
                val i = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(i)
                finish()
            }

        }, SPLASH_TIME_OUT.toLong()
        )
    }

    private fun checkLoginData(): Int {
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sh.getInt("login", 0)
    }
}