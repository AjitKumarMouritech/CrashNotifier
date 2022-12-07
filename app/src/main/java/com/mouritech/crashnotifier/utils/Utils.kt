package com.mouritech.crashnotifier.utils

import android.app.ProgressDialog
import android.content.SharedPreferences

class Utils {
    companion object{
        fun displayProgressBar(progress: ProgressDialog, title: String) {
            progress.setTitle(title)
            progress.setMessage("Wait!!")
            progress.setCancelable(true)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.show()
        }

        fun stopProgressBar(progress: ProgressDialog){
           progress.hide()
        }

        fun mobileNumber(preferences: SharedPreferences): String {
            return preferences.getString("login_mobile_number", "").toString()
        }

        fun getUserID(preferences: SharedPreferences): String {
            return preferences.getString("uid", "").toString()
        }

        fun getUserName(preferences: SharedPreferences): String {
            return preferences.getString("user_name", "").toString()
        }
        fun getUserLat(preferences: SharedPreferences): String {
            return preferences.getString("lat", "").toString()
        }
        fun getUserLon(preferences: SharedPreferences): String {
            return preferences.getString("lon", "").toString()
        }
        fun getFcmToken(preferences: SharedPreferences): String {
            return preferences.getString("fcm_token", "").toString()
        }
    }
}