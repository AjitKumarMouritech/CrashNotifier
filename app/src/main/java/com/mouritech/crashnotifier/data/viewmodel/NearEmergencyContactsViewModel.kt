package com.mouritech.crashnotifier.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NearEmergencyContactsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Emergency contacts"
    }
    val text: LiveData<String> = _text
}