package com.mouritech.crashnotifier.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdateHealthDetailsViewModel  : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Update health details"
    }
    val text: LiveData<String> = _text
}