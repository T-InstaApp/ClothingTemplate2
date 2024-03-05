package com.instaapp.clothingtemplate1.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.AboutUs
import com.instaapp.clothingtemplate1.databinding.ActivityAbutUsBinding
import com.instaapp.clothingtemplate1.databinding.ActivityContactUsBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.utils.PreferenceKey
import com.instaapp.clothingtemplate1.utils.PreferenceProvider
import com.instaapp.clothingtemplate1.utils.StaticValue
import com.instaapp.clothingtemplate1.utils.showAlert
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AbutUsActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var toolbarBinding: ToolbarBinding
    lateinit var progressDialogBinding: ProgressDialogBinding
    private lateinit var binding: ActivityAbutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_abut_us)
        toolbarBinding = binding.toolbar
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        viewModel.getAboutUS(
            "Token " + PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.APP_TOKEN
            )!!, StaticValue.REST_ID
        )
        toolbarBinding.txtTopHeading.text = getString(R.string.about_us)
        toolbarBinding.icMenu.setImageResource(R.drawable.back_drawer)
        toolbarBinding.icMenu.setOnClickListener { finish() }
    }

    override fun onStarted() {
    }

    override fun onFailure(message: String, type: String) {
        showAlert(this, getString(R.string.alert), message)
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        val data: AboutUs = dataG as AboutUs
        binding.contactUSData = data

    }
}