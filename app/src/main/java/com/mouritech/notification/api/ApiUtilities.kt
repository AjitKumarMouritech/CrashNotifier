package com.mouritech.notification.api

import com.google.gson.Gson
import com.mouritech.notification.PushNotification
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object ApiUtilities {
    lateinit var retrofit:Retrofit

    fun getInstance(): ApiInterface{
     return Retrofit.Builder()
         .baseUrl("https://fcm.googleapis.com")
         .addConverterFactory(GsonConverterFactory.create())
         .build()
         .create(ApiInterface::class.java)
 }
}