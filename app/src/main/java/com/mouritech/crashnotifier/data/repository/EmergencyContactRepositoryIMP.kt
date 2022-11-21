package com.mouritech.crashnotifier.data.repository

import com.mouritech.crashnotifier.data.model.EmergencyContacts

class EmergencyContactRepositoryIMP: EmergencyContactRepository {
    override fun getEmergencyContact(): List<EmergencyContacts> {
        //Get data from Firebase
        return arrayListOf()
    }
}