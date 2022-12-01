package com.mouritech.crashnotifier.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NearHospitalsListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "hospitals list"
    }
    val text: LiveData<String> = _text
}