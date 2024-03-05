package com.instaapp.clothingtemplate1.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.instaapp.clothingtemplate1.dataModel.LoginDataModel
import com.instaapp.clothingtemplate1.dataModel.MainLoginDataModel
import com.instaapp.clothingtemplate1.dataModel.UserIDResponse
import com.instaapp.clothingtemplate1.databinding.ActivityLoginBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: HomeViewModel
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog

        binding.txtForgotPassword.setOnClickListener { forgotUserNameAnaPassword("Password") }
        binding.txtForgotUserName.setOnClickListener { forgotUserNameAnaPassword("User") }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
            finish()
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.btnSignin.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty())
                toast("Please enter username")
            else if (binding.etPassword.text.toString().isEmpty())
                toast("Please enter password")
            else if (binding.etPassword.text.toString().trim().length < 8)
                toast("please enter correct password")
            else
                viewModel.login(
                    LoginDataModel(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString(),
                        StaticValue.REST_ID
                    )
                )
        }
    }

    private fun forgotUserNameAnaPassword(type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_input_field)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<TextInputEditText>(R.id.etInputData)
        val txtInputHeading = dialog.findViewById<TextInputLayout>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)

        if (type == "User") {
            txtInputHeading.hint = getString(R.string.email)
            txtHeading.text = "Reset your username"
        } else {
            txtInputHeading.hint = getString(R.string.user_name)
            txtHeading.text = "Reset your password"
        }
        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener {
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                if (type == "User")
                    toast(getString(R.string.enter_your_email))
                else
                    toast(getString(R.string.enter_your_user_name))
            } else {
                dialog.dismiss()
                if (type == "User")
                    viewModel.resetUserName(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
                    )
                else if (type == "Password")
                    viewModel.resetPassword(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
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
        when (type) {
            CommonKey.NO_INTERNET -> {
                showAlert(
                    this,
                    getString(R.string.h_no_internet),
                    getString(R.string.no_internet)
                )
            }
            CommonKey.ERROR_CODE_401 -> {
                showAlert(
                    this,
                    getString(R.string.h_login_401),
                    getString(R.string.login_401)
                )
            }
            CommonKey.ERROR_CODE_403 -> {
                if (message.contains(getString(R.string.no_restro_access))) {
                    restroAccess()
                } else
                    showAlert(
                        this,
                        getString(R.string.h_login_403),
                        message
                    )
            }
            else -> showAlert(
                this,
                getString(R.string.alert),
                message

            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        log("RegistrationActivity", " Success $type")
        if (type == "login") {
            val data: MainLoginDataModel = dataG as MainLoginDataModel
            PreferenceProvider(applicationContext).setStringValue(
                data.token,
                PreferenceKey.APP_TOKEN
            )
            PreferenceProvider(applicationContext).setIntValue(
                data.id.toInt(),
                PreferenceKey.USER_ID
            )
            PreferenceProvider(applicationContext).setBooleanValue(
                true,
                PreferenceKey.LOGIN_STATUS
            )
            PreferenceProvider(applicationContext).setStringValue(
                "REGISTERED",
                PreferenceKey.LOGIN_TYPE
            )
            PreferenceProvider(applicationContext).setStringValue(
                "1",
                PreferenceKey.USER_PROFILE
            )
            if (intent.getStringExtra("callFrom").equals("Details"))
                finish()
            else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("cart", false)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        } else if (type == "mainLogin") {
            val data: MainLoginDataModel = dataG as MainLoginDataModel
            PreferenceProvider(applicationContext).setStringValue(
                data.token, PreferenceKey.APP_TOKEN
            )
            viewModel.getUserID(data.token, binding.etUsername.text.toString())
        } else if (type == "getUserID") {
            val data: UserIDResponse = dataG as UserIDResponse
            viewModel.insertUser(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
                data.results?.get(0)?.id.toString(),
                PreferenceProvider(applicationContext).getStringValue(StaticValue.REST_ID)!!
            )
        } else if (type == "insertUser") {
            viewModel.checkLogin(
                StaticValue.REST_ID,
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        } else if (type == "resetUserName") {
            showAlert(
                this,
                getString(R.string.congratulations),
                getString(R.string.username_reset_success)
            )
        } else if (type == "resetPassword") {
            showAlert(
                this,
                getString(R.string.congratulations),
                getString(R.string.password_reset_success)
            )
        }
    }

    private fun restroAccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val edtEmail = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_login))
            .into(imgAppLogo)
        button.text = getString(R.string.ok)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        edtEmail.text = resources.getString(R.string.confirm_user)
        (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
            viewModel.getUserID(
                "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                binding.etUsername.text.toString()
            )
        }
        button.setOnClickListener { dialog.dismiss() }
        imgAppLogo.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }
}