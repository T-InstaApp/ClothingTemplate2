package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ChangePassword
import com.instaapp.clothingtemplate1.dataModel.ProfileDataRequest
import com.instaapp.clothingtemplate1.dataModel.ProfileResponse
import com.instaapp.clothingtemplate1.databinding.ActivityProfileBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ProfileActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var toolbarBinding: ToolbarBinding
    lateinit var progressDialogBinding: ProgressDialogBinding
    private lateinit var dialog: Dialog

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        progressDialogBinding = binding.progressDialog
        viewModel.networkCallListener = this

        toolbarBinding = binding.toolbar

        toolbarBinding.txtTopHeading.text = "Profile"
        toolbarBinding.icMenu.setImageResource(R.drawable.back_drawer)
        toolbarBinding.icMenu.setOnClickListener { finish() }

        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE) == "2") {
            binding.txtPasswordChange.visibility = View.GONE
        }

        viewModel.getUserProfile(
            "Token " + PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.APP_TOKEN
            ), PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
        binding.btnUpdateProfile.setOnClickListener {
            validation()
        }

        binding.etSalutation.setAdapter<ArrayAdapter<*>>(
            ArrayAdapter<Any?>(this, R.layout.select_salutation, StaticValue.SALUTATION_DATA)
        )
        binding.etSalutation.onFocusChangeListener =
            View.OnFocusChangeListener { _: View?, hasFocus: Boolean -> if (hasFocus) binding.etSalutation.showDropDown() }
        binding.etSalutation.setOnTouchListener { _: View?, _: MotionEvent? ->
            binding.etSalutation.showDropDown()
            false
        }

        binding.txtPasswordChange.setOnClickListener { changePassword() }

        toolbarBinding.cartLayout.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                callMainActivity(this)
            else
                loginFirst(this, "Other")
        }

    }

    private fun validation() {
        if (binding.etSalutation.text.toString().length <= 2) {
            toast("Please select salutation")
            return
        } else if (binding.etFirstName.text.toString().length <= 2) {
            toast("Please enter first name")
            return
        } else if (binding.etLastName.text.toString().length <= 2) {
            toast("Please enter last name")
            return
        } else if (binding.etCode.text.toString().length <= 1) {
            toast("Please enter country code")
            return
        } else if (binding.etMobile.text.toString().length < 9) {
            toast("Please enter mobile number")
            return
        } else {
            val data = ProfileDataRequest(
                binding.etSalutation.text.toString(), binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(), binding.etUsername.text.toString(),
                binding.etEmail.text.toString(),
                CurrentTimeStamp(),
                binding.etCode.text.toString() + binding.etMobile.text.toString()
            )
            viewModel.onUpdateUserProfile(
                "Token " + PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                data
            )
        }
    }

    override fun onStarted() {
        progressDialogBinding.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        showAlert(this, getString(R.string.alert), message)
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        when (type) {
            "onUpdateUserProfile" -> {
                val data = ProfileDataRequest(
                    binding.etSalutation.text.toString(), binding.etFirstName.text.toString(),
                    binding.etLastName.text.toString(), binding.etUsername.text.toString(),
                    binding.etEmail.text.toString(),
                    CurrentTimeStamp(),
                    binding.etCode.text.toString() + binding.etMobile.text.toString()
                )
                viewModel.updateCustomerProfile(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )!!, PreferenceProvider(applicationContext).getIntValue(
                        PreferenceKey.USER_ID
                    ).toString(), data
                )
                log("Update Profile", " onUpdateUserProfile")
            }
            "updateCustomerProfile" -> {
                toast("Profile updated successfully")
            }
            "changePassword" -> {
                dialog.dismiss()
                showAlert(
                    this,
                    getString(R.string.congratulations),
                    getString(R.string.password_change_successfully)
                )
            }
            else -> {
                val data: ProfileResponse = dataG as ProfileResponse
                binding.etSalutation.setText(data.results?.get(0)!!.salutation!!.toString())
                binding.etUsername.setText(data.results?.get(0)!!.customer!!.username)
                binding.etFirstName.setText(data.results?.get(0)!!.customer!!.first_name)
                binding.etLastName.setText(data.results?.get(0)!!.customer!!.last_name)
                binding.etEmail.setText(data.results?.get(0)!!.customer!!.email)
                binding.etCode.setText(data.results?.get(0)!!.code)
                binding.etMobile.setText(data.results?.get(0)!!.phone)
            }
        }
    }

    private fun changePassword() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_pasword)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val tool_bar = dialog.findViewById<RelativeLayout>(R.id.tool_bar)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val etOldPassword = dialog.findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = dialog.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialog.findViewById<EditText>(R.id.etConfirmPassword)
        val btnSubmit = dialog.findViewById<AppCompatButton>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            hideKeyboard(this)
            if (etOldPassword.text.isNullOrEmpty() || etOldPassword.text.toString().length < 5) {
                tool_bar.snakeBar("Please enter old password")
                return@setOnClickListener
            } else if (etNewPassword.text.isNullOrEmpty()) {
                tool_bar.snakeBar("Please enter new password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString().length < 7) {
                tool_bar.snakeBar("Password must be greater than 7 character")
                return@setOnClickListener
            } else if (etConfirmPassword.text.isNullOrEmpty() || etConfirmPassword.text.toString().length < 7) {
                tool_bar.snakeBar("Please enter confirm  password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
                // toast("New password and confirm password does not match")
                tool_bar.snakeBar("New password and confirm password does not match")
                return@setOnClickListener
            }
            viewModel.changePassword(
                "Token " + PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )!!, ChangePassword(
                    etOldPassword.text.toString(),
                    binding.etUsername.text.toString(),
                    etNewPassword.text.toString(),
                    etNewPassword.text.toString()
                )
            )
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp

    }
}