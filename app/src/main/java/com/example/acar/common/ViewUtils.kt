package com.example.acar.common

import android.app.AlertDialog
import android.content.Context
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import com.example.acar.R
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

object GlobalToast {
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

fun showLogoutDialog(context: Context?, navController: NavController) {
    val dialog = AlertDialog.Builder(context).setCancelable(false).setView(R.layout.layout_logout_dialog).show()

    dialog.findViewById<Button>(R.id.dialogYesBtn).setOnClickListener {
        navController.navigate(R.id.action_global_loginFragment)
        dialog.dismiss()
    }
    dialog.findViewById<Button>(R.id.dialogNoBtn).setOnClickListener {
        dialog.dismiss()
    }
}
fun showEndOfRideDialog(context: Context?, navController: NavController){
    val dialog = AlertDialog.Builder(context).setCancelable(true).setView(R.layout.layout_end_of_ride_dialog)
    dialog.setOnCancelListener {
        navController.navigate(R.id.action_postOrderFragment_to_orderFragment)
    }
    dialog.show()

}