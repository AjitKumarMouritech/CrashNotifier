package com.mouritech.crashnotifier.utils

import android.app.ProgressDialog
import com.mouritech.crashnotifier.UI.ui.health_details.UpdateHealthDetails

class Utils {
    companion object{
        fun displayProgressBar(progress: ProgressDialog, title: String) {
            progress.setTitle(title)
            progress.setMessage("Wait!!")
            progress.setCancelable(true)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.show()
        }

        fun stopProgressBar(progress: ProgressDialog){
           progress.hide()
        }
    }
}