package com.instaapp.clothingtemplate1.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.instaapp.clothingtemplate1.MainActivity
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.MobileVerifyResponse
import com.instaapp.clothingtemplate1.databinding.ActivityGuestLoginBinding
import com.instaapp.clothingtemplate1.databinding.ActivityLoginBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.utils.PreferenceKey
import com.instaapp.clothingtemplate1.utils.PreferenceProvider
import com.instaapp.clothingtemplate1.utils.showAlert
import com.instaapp.clothingtemplate1.utils.toast
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class GuestLoginActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityGuestLoginBinding
    private lateinit var viewModel: HomeViewModel
    lateinit var progressDialogBinding: ProgressDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuestLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog

        binding.imgBack.setOnClickListener { finish() }

        binding.btnGenerateOtp.setOnClickListener {
            if (binding.etCode.text.toString().length <= 2) {
                toast("Please enter country code")
                return@setOnClickListener
            } else if (binding.etMobile.text.toString().length < 9) {
                toast("Please enter mobile no.")
                return@setOnClickListener
            }
            viewModel.generateOtp(
                binding.etCode.text.toString().trim() + binding.etMobile.text.toString().trim()
            )
        }
    }

    private fun verifyOTP() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_input_field)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<TextInputEditText>(R.id.etInputData)
        val txtInputHeading = dialog.findViewById<TextInputLayout>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)

        edtText.hint = getString(R.string.enter_otp)
        txtInputHeading.hint = getString(R.string.enter_otp)
        edtText.inputType = InputType.TYPE_CLASS_NUMBER

        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener {
            dialog.dismiss()
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                toast(getString(R.string.enter_otp))
            } else {
                viewModel.verifyOTP(
                    edtText.text.toString(),
                    binding.etCode.text.toString().trim(), binding.etMobile.text.toString().trim()
                )
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    override fun onStarted() {
        progressDialogBinding.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        if (type == "generateOtp") {
            verifyOTP()
        } else if (type == "verifyOTP") {
            val data: MobileVerifyResponse = dataG as MobileVerifyResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id!!,
                PreferenceKey.USER_ID
            )
            PreferenceProvider(applicationContext).setStringValue(
                data.token!!,
                PreferenceKey.APP_TOKEN
            )
            PreferenceProvider(applicationContext).setBooleanValue(true, PreferenceKey.LOGIN_STATUS)
            PreferenceProvider(applicationContext).setStringValue("2", PreferenceKey.USER_PROFILE)

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("cart", false)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}