package com.example.myprojectapplication.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
fun showAlertDialog(title: String, message: String, btnMessage: String, context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(btnMessage) { dialog, _ ->
        dialog.dismiss()
    }
    builder.create().show()
}