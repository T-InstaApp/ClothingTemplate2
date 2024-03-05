package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.UserRegisterRequest
import com.instaapp.clothingtemplate1.dataModel.UserRegistrationResponse
import com.instaapp.clothingtemplate1.databinding.ActivityRegistrationBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.utils.StaticValue.SALUTATION_DATA
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegistrationActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: HomeViewModel
    val TAG = "RegistrationActivity"
    lateinit var progressDialogBinding: ProgressDialogBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog

        binding.txtSignIn.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            intent.putExtra("callFrom", "Registration")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.etSalutation.setAdapter<ArrayAdapter<*>>(
            ArrayAdapter<Any?>(this, R.layout.select_salutation, SALUTATION_DATA)
        )
        binding.etSalutation.onFocusChangeListener =
            View.OnFocusChangeListener { _: View?, hasFocus: Boolean -> if (hasFocus) binding.etSalutation.showDropDown() }
        binding.etSalutation.setOnTouchListener { _: View?, _: MotionEvent? ->
            binding.etSalutation.showDropDown()
            false
        }

        binding.btnSignUp.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        if (binding.etSalutation.text.toString().length <= 2) {
            toast("Please select salutation")
            return
        } else if (binding.etUsername.text.toString().length <= 2) {
            toast("Please enter user name")
            return
        } else if (binding.etFirstName.text.toString().length <= 2) {
            toast("Please enter first name")
            return
        } else if (binding.lastLastName.text.toString().length <= 2) {
            toast("Please enter last name")
            return
        } else if (binding.etEmail.text.toString().length <= 2) {
            toast("Please enter email")
            return
        } else if (binding.etCode.text.toString().length <= 1) {
            toast("Please enter country code")
            return
        } else if (binding.etMobile.text.toString().length < 9) {
            toast("Please enter mobile number")
            return
        } else if (binding.etPassword.text.toString().length < 7) {
            toast("Please enter your password")
            return
        } else if (binding.etConfirmPassword.text.toString().length < 7) {
            toast("Please enter your confirm password")
            return
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            toast("Password and confirm password does not match")
            return
        } else {
            viewModel.registerUser(
                "Token ${
                    PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!
                        .trim()
                }",
                UserRegisterRequest(
                    binding.etUsername.text.toString().trim(),
                    binding.etEmail.text.toString().trim(),
                    binding.etFirstName.text.toString().trim(),
                    binding.lastLastName.text.toString().trim(),
                    binding.etPassword.text.toString().trim(),
                    StaticValue.REST_ID,
                    "N",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
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
            CommonKey.ERROR_CODE_400 -> {
                showAlert(
                    this,
                    getString(R.string.warning),
                    getString(R.string.login_400)
                )
            }
            else -> {
                showAlert(
                    this,
                    getString(R.string.alert),
                    getString(R.string.no_response)
                )
            }
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        log(TAG, " onSuccess $type")
        when (type) {
            "registerUser" -> {
                val data: UserRegistrationResponse = dataG as UserRegistrationResponse
                viewModel.createUser(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!
                        .trim(),
                    UserRegisterRequest(
                        "",
                        binding.etEmail.text.toString().trim(),
                        binding.etFirstName.text.toString().trim(),
                        binding.lastLastName.text.toString().trim(),
                        "",
                        "",
                        "",
                        data.id.toString(),
                        currentTimeStamp(),
                        "extra",
                        binding.etSalutation.text.toString().trim(),
                        binding.etCode.text.toString().trim() + binding.etMobile.text.toString()
                            .trim(),
                    )
                )
            }
            "createUser" -> {
                val dialog = createAlertDialogObjectForCar(this)
                val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnOk)
                val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
                val txtMessage = dialog.findViewById<TextView>(R.id.txtMsg)
                txtMessage.text = getString(R.string.registration_success)
                txtHeading.text = getString(R.string.congratulations)
                btnOk.text = getString(R.string.ok)
                btnOk.setOnClickListener {
                    dialog.dismiss()
                    startActivity(
                        Intent(
                            applicationContext,
                            LoginActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
                dialog.show()
            }
        }
    }
}