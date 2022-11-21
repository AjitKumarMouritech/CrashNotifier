package com.mouritech.crashnotifier.data.repository

import com.mouritech.crashnotifier.data.model.EmergencyContacts

interface EmergencyContactRepository {
    fun getEmergencyContact(): List<EmergencyContacts>
}