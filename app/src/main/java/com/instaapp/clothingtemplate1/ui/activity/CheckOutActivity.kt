package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.AddressListResponse
import com.instaapp.clothingtemplate1.dataModel.AddressListResultResponse
import com.instaapp.clothingtemplate1.databinding.ActivityCheckOutBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.OrderViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CheckOutActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {


    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var addresslist: List<AddressListResultResponse?>

    private var isShipAddressAvail = false
    private var isBillAddressAvail = false
    private var shippingAddressID: Int = 0
    private var billingAddressID: Int = 0
    val TAG = "CheckOutActivity"
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolbar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        toolbar.txtTopHeading.text = "Shipping Address"
        addresslist = arrayListOf()


        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }


        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        getBillingAddress()
        getShippingAddress()
        binding.checkboxCurrentAddress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnAddNewAddress.visibility = View.GONE
            } else {
                binding.btnNext.visibility = View.GONE
                binding.btnAddNewAddress.visibility = View.VISIBLE
            }
        }

        binding.btnAddNewAddress.setOnClickListener {
            val intent = Intent(applicationContext, AddAddressActivity::class.java)
            intent.putExtra("isShippingAddressAvailable", isShipAddressAvail)
            intent.putExtra("isBillingAddressAvailable", isBillAddressAvail)
            intent.putExtra("shippingAddressID", shippingAddressID)
            intent.putExtra("billingAddressID", billingAddressID)
            intent.putExtra("tax", "0")
            startActivity(intent)
        }
        binding.btnNext.setOnClickListener { onSubmitButtonClick() }
    }

    private fun getShippingAddress() {
        viewModel.getShippingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun getBillingAddress() {
        viewModel.getBillingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
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

    @SuppressLint("SetTextI18n")
    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        log(TAG, " Success $type = $dataG")
        if (type == "getShippingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            addresslist = data.results
            if (addresslist.isNotEmpty()) {
                shippingAddressID = addresslist[0]!!.id!!
                isShipAddressAvail = true
                binding.txtExistAddress.text =
                    "${addresslist[0]!!.name!!}, ${addresslist[0]!!.house_number} ${addresslist[0]!!.address!!} " +
                            "${addresslist[0]!!.city!!} ${addresslist[0]!!.state!!} ${addresslist[0]!!.zip!!} ${addresslist[0]!!.country!!}"
                binding.layoutCurrentShippingAddress.visibility = View.VISIBLE
            } else {
                binding.btnAddNewAddress.visibility = View.VISIBLE
            }
        } else if (type == "getBillingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            if (addresslist.isNotEmpty()) {
                billingAddressID = data.results[0]!!.id!!
                isBillAddressAvail = true
            }
        }
    }

    private fun onSubmitButtonClick() {
        val intent = Intent(applicationContext, AddBillingAddressActivity::class.java)
        intent.putExtra("country", addresslist[0]!!.country)
        intent.putExtra("city", addresslist[0]!!.city)
        intent.putExtra("state", addresslist[0]!!.state)
        intent.putExtra("zip", addresslist[0]!!.zip)
        intent.putExtra("address", addresslist[0]!!.address)
        intent.putExtra("house_no", addresslist[0]!!.house_number)
        intent.putExtra("name", addresslist[0]!!.name)
        intent.putExtra("tax", "0")
        intent.putExtra("latitude", addresslist[0]!!.latitude)
        intent.putExtra("longitude", addresslist[0]!!.longitude)
        startActivity(intent)
    }
}