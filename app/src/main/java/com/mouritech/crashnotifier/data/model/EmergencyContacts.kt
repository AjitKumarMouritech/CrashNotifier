package com.mouritech.crashnotifier.data.model

data class EmergencyContacts(
    val emergency_contact_number: String,
    val emergency_contact_name: String,
    val lat: String,
    val lon: String,
    val fcm_token: String
    )
