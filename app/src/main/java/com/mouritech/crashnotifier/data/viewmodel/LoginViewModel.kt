package com.mouritech.crashnotifier.data.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LoginViewModel: ViewModel() {

    var mobileNumber: MutableLiveData<String> = MutableLiveData()
    var otp: MutableLiveData<String> = MutableLiveData()

    fun isMobileNumberValid(): Boolean {
        var number = this.mobileNumber.value.toString()
        return number.isNotEmpty() && number.length == 10
    }

    fun isOTPValid() : Boolean{
        val otp = this.otp.value.toString()
        return otp.isNotEmpty()
    }

}