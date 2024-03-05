package com.instaapp.clothingtemplate1.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.*
import java.util.*
import kotlin.String

@BindingAdapter("image")
fun loadImage(view: ImageView, url: String) {
    if (url != null && url.length > 10)
        Glide.with(view).load(url).circleCrop().into(view)
}

@BindingAdapter("imageSquare")
fun loadImageSquare(view: ImageView, url: String?) {
    if (url != null && url.length > 10)
        Glide.with(view).load(url).into(view)
}


@BindingAdapter("dateFormat")
fun formatDate(view: TextView, dateTime: String) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    val parsedDate = inputFormat.parse(dateTime)
    view.text = outputFormat.format(parsedDate!!)
}

@BindingAdapter("SetEditTextValue")
fun editTextValue(view: EditText, title: String) {
    view.setText(title)
}


@SuppressLint("SetTextI18n")
@BindingAdapter("FormatCurrency")
fun formatCurrency(view: TextView, price: String?) {
    try {
        if (price != null) {
            val cleanString = price.replace(",", "")
            val parsed = BigDecimal(cleanString)
            val formatter = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.ENGLISH))
            val formattedText =
                "${PreferenceProvider(view.context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                    formatter.format(parsed)
                }"
            view.text = formattedText
        } else
            view.text = "0.0"
    } catch (ignore: Exception) {
        view.text = price
    }
}


fun formatNumberToWord(number: Long): String {
    return when {
        number >= 10000000 -> {
            val crore = number / 10000000
            val remainder = number % 10000000
            val formattedRemainder = String.format("%02d", remainder / 100000)
            "$crore.$formattedRemainder Crore"
        }
        number >= 100000 -> {
            val lakh = number / 100000
            val remainder = number % 100000
            val formattedRemainder = String.format("%02d", remainder / 1000)
            "$lakh.$formattedRemainder Lakh"
        }
        else -> {
            val thousand = number / 1000
            val remainder = number % 1000
            val formattedRemainder = String.format("%02d", remainder / 10)
            "$thousand.$formattedRemainder K"
        }
    }
}

fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}

fun hideKeyboard(activity: Activity) {
    try {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    } catch (e: Exception) {
    }
}
