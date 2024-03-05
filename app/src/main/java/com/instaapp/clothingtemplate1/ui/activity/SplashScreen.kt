package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.MainActivity
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.CompanyData
import com.instaapp.clothingtemplate1.databinding.ActivitySplashScreenBinding

import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity(), KodeinAware, NetworkCallListener {

    private lateinit var zoom: Animation
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    override val kodein by kodein()

    /*Test Shared Project*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this

        zoom = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom)
        binding.image.startAnimation(zoom)


        val handler = Handler(Looper.getMainLooper())

        val delayedRunnable = Runnable {
            if (!PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                log("SplashActivity", " REST ID ${StaticValue.REST_ID.toInt()}")
                viewModel.getAppDetails("User", StaticValue.REST_ID.toInt())
            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("cart", false)
                startActivity(intent)
                finish()
            }
        }

        handler.postDelayed(delayedRunnable, 3000) // 5000 milliseconds = 5 seconds


    }

    override fun onStarted() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progressBar.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progressBar.visibility = View.GONE
        log("SplashActivity", " onSuccess $dataG")

        val data: CompanyData = dataG as CompanyData

        PreferenceProvider(applicationContext).setStringValue(
            data.compony_name!!, PreferenceKey.COMPANY_NAME
        )
        log("SplashActivity ", " ${data.Token}")
        PreferenceProvider(applicationContext).setStringValue(
            data.Token!!, PreferenceKey.APP_TOKEN
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.compony_image!!, PreferenceKey.IMAGE_LOGO
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.currency!!, PreferenceKey.CURRENCY_TYPE
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.currency_symbol!!, PreferenceKey.CURRENCY_SYMBOL
        )
        PreferenceProvider(applicationContext).setIntValue(
            data.REST_DELIVERY_ID!!.toInt(), PreferenceKey.REST_DELIVERY_ID
        )
        PreferenceProvider(applicationContext).setIntValue(
            data.REST_PICKUP_ID!!.toInt(), PreferenceKey.REST_PICKUP_ID
        )
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("cart", false)
        startActivity(intent)
        finish()

    }
}