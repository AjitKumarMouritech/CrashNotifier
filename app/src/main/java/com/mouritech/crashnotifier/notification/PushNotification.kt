package com.mouritech.crashnotifier.notification

import com.mouritech.crashnotifier.notification.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String? = ""
)
