package com.mouritech.notification.api

import com.mouritech.notification.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Content-Type:application/json",
        "Authorization:key=AAAAuDXgXl8:APA91bF7zXCZmMNGcKHbw4LXlMyjO7AeYS6Bnjt5r5Fi1ZVQurPFzDhPktr60bJFFLBQKeDWlNX_ffEfb5zh39BM65ci3US2B9vkunTQ5jiLrkdOTNsPrAJ0usLA78du4wgqnxRsYuZx")
    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification)
            : Call<PushNotification>
}