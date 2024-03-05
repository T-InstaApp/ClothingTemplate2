package com.instaapp.clothingtemplate1.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.instaapp.clothingtemplate1.MainActivity
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.ui.activity.LoginActivity
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ceil


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun Context.successToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun log(tag: String, message: String) {
    Log.d(tag, message)
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun CircularProgressIndicator.show() {
    visibility = View.VISIBLE
}

fun CircularProgressIndicator.hide() {
    visibility = View.GONE
}

fun RelativeLayout.visible() {
    visibility = View.VISIBLE
}

fun RelativeLayout.notVisible() {
    visibility = View.GONE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snakeBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun showAlert(activity: Activity, title: String, message: String) {
    AlertDialog.Builder(activity)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            "Ok"
        ) { dialog, _ -> dialog?.dismiss() }.show()
}

fun createAlertDialogObjectForCar(activity: Activity): Dialog {
    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.custom_dialog)
    dialog.setCancelable(false)
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
    imgCancel.setOnClickListener { dialog.dismiss() }
    dialog.window!!.attributes = lp
    return dialog
}

fun currentTimeStamp(): String {
    //Date object
    val date = Date()
    //getTime() returns current time in milliseconds
    val time = date.time
    //Passed the milliseconds to constructor of Timestamp class
    val ts = Timestamp(time)
    return ts.toString()
}

fun getScreenWidthSize(activity: Activity): Double {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val height = windowMetrics.bounds.width() - insets.right - insets.left
        height.toDouble()
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.widthPixels
        height.toDouble()
    }
}

fun CurrentTimeStamp(): String {
    //Date object
    val date = Date()
    //getTime() returns current time in milliseconds
    val time = date.time
    //Passed the milliseconds to constructor of Timestamp class
    val ts = Timestamp(time)
    return ts.toString()
}

fun callMainActivity(activity: Activity) {
    val intent = Intent(activity.applicationContext, MainActivity::class.java)
    intent.putExtra("cart", true)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    activity.applicationContext.startActivity(intent)
    activity.finish()
}

fun loginFirst(activity: Activity, callFrom: String) {
    val dialog = createAlertDialogObjectForCar(activity)
    val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
    val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnOk)
    val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
    val txtMessage = dialog.findViewById<TextView>(R.id.txtMsg)
    txtMessage.text = "Let's Get Started – Please Log In!"
    txtHeading.text = activity.getString(R.string.alert)
    btnOk.text = activity.getString(R.string.login)
    imgCancel.setOnClickListener { dialog.dismiss() }
    btnOk.setOnClickListener {
        dialog.dismiss()
        val intent = Intent(activity.applicationContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("callFrom", "Details")
        activity.applicationContext.startActivity(intent)
    }
    dialog.show()
}

fun info(activity: Activity, message: String) {
    val dialog = createAlertDialogObjectForCar(activity)
    val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
    val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnOk)
    val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
    val txtMessage = dialog.findViewById<TextView>(R.id.txtMsg)
    txtMessage.text = message
    txtHeading.text = "Note"
    btnOk.text = "Ok"
    imgCancel.visibility = View.GONE
    imgCancel.setOnClickListener { dialog.dismiss() }
    btnOk.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

fun customRounding(amount: Double): Int {
    val decimalPart = amount % 1.0

    return if (decimalPart >= 0.5) {
        ceil(amount).toInt()
    } else {
        amount.toInt()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun separateDateAndTime(dateTimeString: String): Pair<String, String>? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return try {
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)
        val date = dateTime.toLocalDate().toString()
        val time = dateTime.toLocalTime().toString()
        Pair(date, time)
    } catch (e: Exception) {
        // Handle parsing exceptions (invalid date format, etc.)
        null
    }
}






