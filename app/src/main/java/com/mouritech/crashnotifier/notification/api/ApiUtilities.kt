package com.mouritech.crashnotifier.notification.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    lateinit var retrofit:Retrofit

    fun getInstance(): ApiInterface {
     return Retrofit.Builder()
         .baseUrl("https://fcm.googleapis.com")
         .addConverterFactory(GsonConverterFactory.create())
         .build()
         .create(ApiInterface::class.java)
 }
}