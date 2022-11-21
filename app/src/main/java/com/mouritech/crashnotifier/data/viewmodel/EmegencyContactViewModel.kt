package com.mouritech.crashnotifier.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouritech.crashnotifier.data.model.EmergencyContacts
import com.mouritech.crashnotifier.data.repository.EmergencyContactRepository

class EmegencyContactViewModel(val repository: EmergencyContactRepository): ViewModel() {
   val _emergencyContacts= MutableLiveData<List<EmergencyContacts>>()
    val emergencyContacts: MutableLiveData<List<EmergencyContacts>>
            get()=_emergencyContacts

    fun getEmergencyContact(){
        //_emergencyContacts.value= repository.getEmergencyContact()
        _emergencyContacts.value= repository.getEmergencyContact()

    }

}