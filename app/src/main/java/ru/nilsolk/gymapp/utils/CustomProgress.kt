package ru.nilsolk.gymapp.utils

import android.app.Dialog
import android.content.Context
import ru.nilsolk.gymapp.R

class CustomProgress (context: Context) {

    private var progressDialog =  Dialog(context)

    fun show() {
        progressDialog.setContentView(R.layout.custom_progress)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.show()
    }

    fun dismiss() {
        progressDialog.dismiss()
    }

}