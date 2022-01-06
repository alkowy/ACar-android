package com.example.acar.common

import android.content.Context
import android.widget.Toast
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

object GlobalToast{
    var toast: Toast? = null

    fun showShort(context: Context?, msg: String) {

        if (context != null) {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, validateString(msg), Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }
    fun showLong(context: Context?, msg: String) {

        if (context != null) {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, validateString(msg), Toast.LENGTH_LONG)
            toast!!.show()
        }
    }

    private fun validateString(msg: String?): String {
        return msg ?: "null"
    }

}