package com.mouritech.notification

data class PushNotification(
    val data: NotificationData,
    val to: String? = ""
)
