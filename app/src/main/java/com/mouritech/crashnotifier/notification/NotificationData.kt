package com.mouritech.crashnotifier.notification

data class NotificationData (
    val title : String? = "",
    val body: String?= "",
    val uid: String?="",
    val mobile: String?="",
    val lat: String?="",
    val lon: String?=""

    )