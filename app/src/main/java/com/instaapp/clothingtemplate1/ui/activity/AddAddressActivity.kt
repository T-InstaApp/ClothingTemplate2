package com.instaapp.clothingtemplate1.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.AddBillingShippingAddressRequest
import com.instaapp.clothingtemplate1.databinding.ActivityAddAddressBinding
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

class AddAddressActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var toolbar: ToolbarBinding
    private var isShippingAddressAvailable = false
    private var isBillingAddressAvailable = false
    private var shippingAddressID = 0
    private var billingAddressID = 0
    private var tax = "0"
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolBar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        toolbar.txtTopHeading.text = "Add Shipping Address"
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        isShippingAddressAvailable = intent.getBooleanExtra("isShippingAddressAvailable", false)
        isBillingAddressAvailable = intent.getBooleanExtra("isBillingAddressAvailable", false)
        shippingAddressID = intent.getIntExtra("shippingAddressID", 0)
        billingAddressID = intent.getIntExtra("billingAddressID", 0)
        tax = intent.getStringExtra("tax")!!

        binding.btnAddNewAddress.setOnClickListener {
            validation()
        }
        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }

    }

    private fun validation() {
        if (binding.etName.text!!.isEmpty()) {
            toast("Please enter name")
        } else if (binding.etMobile.text!!.isEmpty()) {
            toast("Please enter mobile no.")
        } else if (binding.etMobile.text!!.length < 9) {
            toast("Please enter valid mobile no.")
        } else if (binding.etPincode.text!!.isEmpty()) {
            toast("Please enter pincode")
        } else if (binding.etPincode.text!!.length < 5) {
            toast("Please enter valid pincode")
        } else if (binding.etHouseNo.text!!.isEmpty()) {
            toast("Please enter house no.")
        } else if (binding.etAddress.text!!.isEmpty()) {
            toast("Please enter address")
        } else if (binding.etLandmark.text!!.isEmpty()) {
            toast("Please enter landmark")
        } else if (binding.etCity.text!!.isEmpty()) {
            toast("Please enter city")
        } else if (binding.etState.text!!.isEmpty()) {
            toast("Please enter state")
        } else if (binding.etCountry.text!!.isEmpty()) {
            toast("Please enter country")
        } else {
            if (isShippingAddressAvailable) {
                viewModel.updateShippingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    shippingAddressID.toString(),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etHouseNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etPincode.text.toString().trim(),
                        "${
                            binding.etAddress.text.toString().trim()
                        }, Near - ${binding.etLandmark.text.toString().trim()}",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        "",
                        ""
                    )
                )
            } else {
                viewModel.addShippingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etHouseNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etPincode.text.toString().trim(),
                        "${
                            binding.etAddress.text.toString().trim()
                        }, Near - ${binding.etLandmark.text.toString().trim()}",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        "",
                        ""
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

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        if (type == "updateShippingAddress" || type == "addShippingAddress") {
            if (binding.checkboxUseAsBilling.isChecked) {
                if (isBillingAddressAvailable) {
                    viewModel.updateBillingAddress(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        billingAddressID.toString(),
                        AddBillingShippingAddressRequest(
                            "",
                            binding.etCountry.text.toString().trim(),
                            binding.etHouseNo.text.toString().trim(),
                            binding.etCity.text.toString().trim(),
                            binding.etPincode.text.toString().trim(),
                            "${
                                binding.etAddress.text.toString().trim()
                            }, Near - ${binding.etLandmark.text.toString().trim()}",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            binding.etState.text.toString().trim(),
                            1,
                            binding.etName.text.toString().trim(),
                            "",
                            ""
                        )
                    )
                } else {
                    viewModel.addBillingAddress(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        AddBillingShippingAddressRequest(
                            "",
                            binding.etCountry.text.toString().trim(),
                            binding.etHouseNo.text.toString().trim(),
                            binding.etCity.text.toString().trim(),
                            binding.etPincode.text.toString().trim(),
                            "${
                                binding.etAddress.text.toString().trim()
                            }, Near - ${binding.etLandmark.text.toString().trim()}",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            binding.etState.text.toString().trim(),
                            1,
                            binding.etName.text.toString().trim(),
                            "",
                            ""
                        )
                    )
                }
            } else {
                val intent = Intent(applicationContext, AddBillingAddressActivity::class.java)
                intent.putExtra("country", binding.etCountry.text.toString().trim())
                intent.putExtra("city", binding.etCity.text.toString().trim())
                intent.putExtra("state", binding.etState.text.toString().trim())
                intent.putExtra("zip", binding.etPincode.text.toString().trim())
                intent.putExtra(
                    "address", "${
                        binding.etAddress.text.toString().trim()
                    }, Near - ${binding.etLandmark.text.toString().trim()}"
                )
                intent.putExtra("house_no", binding.etHouseNo.text.toString().trim())
                intent.putExtra("name", binding.etName.text.toString().trim())
                intent.putExtra("tax", tax)
                intent.putExtra("latitude", "")
                intent.putExtra("longitude", "")
                startActivity(intent)
                finish()
            }
        } else if (type == "updateBillingAddress" || type == "addBillingAddress") {
            val intent = Intent(applicationContext, PlaceOrderActivity::class.java)
            intent.putExtra("country", binding.etCountry.text.toString().trim())
            intent.putExtra("city", binding.etCity.text.toString().trim())
            intent.putExtra("state", binding.etState.text.toString().trim())
            intent.putExtra("zip", binding.etPincode.text.toString().trim())
            intent.putExtra(
                "address",
                "${
                    binding.etAddress.text.toString().trim()
                }, Near - ${binding.etLandmark.text.toString().trim()}"
            )
            intent.putExtra("house_no", binding.etHouseNo.text.toString().trim())
            intent.putExtra("name", binding.etName.text.toString().trim())
            intent.putExtra("tax", tax)
            intent.putExtra("latitude", "")
            intent.putExtra("longitude", "")
            val billingAddress =
                "${binding.etName.text.toString()},${binding.etHouseNo.text.toString()}, ${binding.etAddress.text.toString()}, ${binding.etCity.text.toString()}, ${binding.etState.text.toString()}, ${binding.etCountry.text.toString()}, ${binding.etPincode.text.toString()}"
            val shippingAddressAddress =
                "${binding.etName.text.toString()} (${binding.etMobile.text.toString()}),${binding.etHouseNo.text.toString()}, ${binding.etAddress.text.toString()}, Near - ${
                    binding.etLandmark.text.toString().trim()
                }, ${binding.etCity.text.toString()}, ${binding.etState.text.toString()}, ${binding.etCountry.text.toString()}, ${binding.etPincode.text.toString()}"
            intent.putExtra("billing_address", billingAddress)
            intent.putExtra("shipping_address", shippingAddressAddress)
            startActivity(intent)
            finish()
        }
    }
}