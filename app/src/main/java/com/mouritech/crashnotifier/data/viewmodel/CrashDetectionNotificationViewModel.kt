package com.mouritech.crashnotifier.data.viewmodel

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

    fun sendNotification(msg: String) {
        val notificationData = PushNotification(NotificationData("New message", msg),"cntMj1gpTESlaS2fvI2oCT:APA91bEdUZX_LzCKa5W1yfZO6thTEKXpo6F3EGSgrHl4IffRRCmsHSE_WvRrs7fXup_bl4W0gC-kXsCh33wPvlvUrs66nvrzXsurTVOpSvkGTd6_D8yB-Z19KegJkWvVV-1UjKvtT2_e")
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