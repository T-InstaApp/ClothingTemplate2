package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.AddBillingShippingAddressRequest
import com.instaapp.clothingtemplate1.dataModel.AddressListResponse
import com.instaapp.clothingtemplate1.dataModel.AddressListResultResponse
import com.instaapp.clothingtemplate1.databinding.ActivityAddAddressBinding
import com.instaapp.clothingtemplate1.databinding.ActivityAddBillingAddressBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.OrderViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddBillingAddressActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var binding: ActivityAddBillingAddressBinding
    private lateinit var toolbar: ToolbarBinding
    lateinit var progressDialogBinding: ProgressDialogBinding
    var shipCountry = ""
    var shipCity = ""
    var shipState = ""
    var shipPastalcode = ""
    var shipXaddress = ""
    var shipHouseNo = ""
    var shipName = ""
    var shipTax = ""
    private var latitude = "0"
    private var longitude = "0"
    private var addressid: String = ""
    var isBillAddressAvail = false

    private lateinit var addresslist: List<AddressListResultResponse?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolBar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        toolbar.txtTopHeading.text = "Billing Address"
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        shipCountry = intent.getStringExtra("country").toString()
        shipCity = intent.getStringExtra("city").toString()
        shipState = intent.getStringExtra("state").toString()
        shipPastalcode = intent.getStringExtra("zip").toString()
        shipXaddress = intent.getStringExtra("address").toString()
        shipHouseNo = intent.getStringExtra("house_no").toString()
        shipName = intent.getStringExtra("name").toString()
        shipTax = intent.getStringExtra("tax").toString()
        latitude = intent.getStringExtra("latitude")!!
        longitude = intent.getStringExtra("longitude")!!

        getBillingAddress()

        binding.btnAddNewAddress.setOnClickListener {
            validation()
        }

        binding.checkboxCurrentAddress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutAddress.visibility = View.GONE
                isBillAddressAvail = true
                binding.btnNext.visibility = View.VISIBLE
            } else {
                binding.layoutAddress.visibility = View.VISIBLE
                isBillAddressAvail = false
                binding.btnNext.visibility = View.GONE
            }
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(applicationContext, PlaceOrderActivity::class.java)
            intent.putExtra("tax", shipTax)
            intent.putExtra("country", shipCountry)
            intent.putExtra("city", shipCity)
            intent.putExtra("state", shipState)
            intent.putExtra("zip", shipPastalcode)
            intent.putExtra("address", shipXaddress)
            intent.putExtra("name", shipName)
            intent.putExtra("house_no", shipHouseNo)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)

            val billingAddress =
                "${addresslist[0]!!.name},${addresslist[0]!!.house_number}, ${addresslist[0]!!.address}, ${addresslist[0]!!.city}, ${addresslist[0]!!.state}, ${addresslist[0]!!.country}, ${addresslist[0]!!.zip}"
            val shippingAddress =
                "$shipName,$shipHouseNo, $shipXaddress, $shipCity, $shipState, $shipCountry, $shipPastalcode"
            intent.putExtra("billing_address", billingAddress)
            intent.putExtra("shipping_address", shippingAddress)
            startActivity(intent)
            finish()
        }

        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }
    }


    private fun getBillingAddress() {
        viewModel.getBillingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun validation() {
        if (binding.etName.text!!.isEmpty()) {
            toast("Please enter name")
        } else if (binding.etPincode.text!!.isEmpty()) {
            toast("Please enter pincode")
        } else if (binding.etPincode.text!!.length < 5) {
            toast("Please enter valid pincode")
        } else if (binding.etHouseNo.text!!.isEmpty()) {
            toast("Please enter house no.")
        } else if (binding.etAddress.text!!.isEmpty()) {
            toast("Please enter address")
        } else if (binding.etCountry.text!!.isEmpty()) {
            toast("Please enter locality/landmark")
        } else if (binding.etState.text!!.isEmpty()) {
            toast("Please enter state")
        } else if (binding.etCountry.text!!.isEmpty()) {
            toast("Please enter country")
        } else {
            if (addresslist.isNotEmpty()) {
                viewModel.updateBillingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    addressid,
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etHouseNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etPincode.text.toString().trim(),
                        binding.etAddress.text.toString().trim(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        "0.0",
                        "0.0"
                    )
                )
            } else {
                viewModel.addBillingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etHouseNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etPincode.text.toString().trim(),
                        binding.etAddress.text.toString().trim(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        "0.0",
                        "0.0"
                    )
                )
            }
        }

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
        if (type == "getBillingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            addresslist = data.results
            if (addresslist.isNotEmpty()) {
                log("addresslist ", " addresslist Not Empty ${addresslist[0]!!.address!!} ")
                binding.layout2.visibility = View.VISIBLE
                binding.layoutCurrentShippingAddress.visibility = View.VISIBLE
                binding.txtNote.visibility = View.VISIBLE
                binding.checkboxCurrentAddress.isChecked = true
                addressid = addresslist[0]!!.id.toString()
                isBillAddressAvail = true
                binding.txtExistAddress.text =
                    "${addresslist[0]!!.name!!}, ${addresslist[0]!!.house_number} ${addresslist[0]!!.address!!} " +
                            "${addresslist[0]!!.city!!} ${addresslist[0]!!.state!!} ${addresslist[0]!!.zip!!} ${addresslist[0]!!.country!!}"
            } else {
                binding.layoutCurrentShippingAddress.visibility = View.GONE
                binding.layoutAddress.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
                binding.txtNote.text = "Please provide billing address"
                binding.txtNote.visibility = View.VISIBLE
            }
        } else if (type == "addBillingAddress" || type == "updateBillingAddress") {
            val intent = Intent(applicationContext, PlaceOrderActivity::class.java)
            intent.putExtra("tax", shipTax)
            intent.putExtra("country", shipCountry)
            intent.putExtra("city", shipCity)
            intent.putExtra("state", shipState)
            intent.putExtra("zip", shipPastalcode)
            intent.putExtra("address", shipXaddress)
            intent.putExtra("name", shipName)
            intent.putExtra("house_no", shipHouseNo)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            val billingAddress =
                "${binding.etName.text.toString()},${binding.etHouseNo.text.toString()}, ${binding.etAddress.text.toString()}, ${binding.etCity.text.toString()}, ${binding.etState.text.toString()}, ${binding.etCountry.text.toString()}, ${binding.etPincode.text.toString()}"
            val shippingAddress =
                "$shipName,$shipHouseNo, $shipXaddress, $shipCity, $shipState, $shipCountry, $shipPastalcode"
            intent.putExtra("billing_address", billingAddress)
            intent.putExtra("shipping_address", shippingAddress)
            startActivity(intent)
            finish()
        }
    }
}