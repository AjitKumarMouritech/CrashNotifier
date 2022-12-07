package com.mouritech.crashnotifier.data.viewmodel

import android.icu.text.CaseMap.Title
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouritech.crashnotifier.notification.NotificationData
import com.mouritech.crashnotifier.notification.PushNotification
import com.mouritech.crashnotifier.notification.api.ApiUtilities
import retrofit2.Call
import retrofit2.Response

class CrashDetectionNotificationViewModel : ViewModel() {

    var notificationResponse = MutableLiveData<String>()
    val _notificationResponse: MutableLiveData<String>
        get() = notificationResponse

    fun sendNotification(msg: String, title: String, uid:String, mobile: String, lat:String, lon:String, token:String) {
        val notificationData = PushNotification(NotificationData(title, msg, uid, mobile,lat,lon),token)
        ApiUtilities.getInstance().sendNotification(
            notificationData
        ).enqueue(object : retrofit2.Callback<PushNotification>{
            override fun onResponse(
                call: Call<PushNotification>,
                response: Response<PushNotification>
            ) {
                notificationResponse.value="Success"
                //Toast.makeText(baseContext,"New Msg", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                //Toast.makeText(baseContext,"Failed", Toast.LENGTH_LONG).show()
                notificationResponse.value="Fail"

            }

        })


    }

}