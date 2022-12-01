package com.mouritech.crashnotifier.utils

import android.app.ProgressDialog
import com.mouritech.crashnotifier.ui.LoginActivity

class Utils {
    companion object{
        fun displayProgressBar(loginActivity: LoginActivity) {
            var progress : ProgressDialog
            progress = ProgressDialog(loginActivity);
            progress.setTitle("Sending OTP")
            progress.setMessage("Wait!!")
            progress.setCancelable(true)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show()
        }
    }
}