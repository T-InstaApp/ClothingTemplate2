package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.MainActivity
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.databinding.ActivityPlaceOrderBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewCartClickListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.OrderCartDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.OrderViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PlaceOrderActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewCartClickListener, RecyclerViewClickListener,
    PaymentResultWithDataListener {
    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var binding: ActivityPlaceOrderBinding
    private lateinit var toolbar: ToolbarBinding
    var shipCountry = ""
    var shipCity = ""
    var shipState = ""
    var shipPincode = ""
    var shipXaddress = ""
    var shipHouseNo = ""
    var shipName = ""
    var shipTax = ""
    var shippingAddress = ""
    var billingAddress = ""
    private var latitude = "0"
    private var longitude = "0"
    private var userEmail = ""
    private var userMobile = ""
    private var totalPrice = ""
    private lateinit var freeResponse: FreeResponse
    private var isCouponApplied: Boolean = false
    private lateinit var orderDetailResponse: OrderDetailResponse
    lateinit var progressDialogBinding: ProgressDialogBinding
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private var productWeight = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolBar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        toolbar.txtTopHeading.text = "Place Order"
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()
        loadCartData()

        totalPrice =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.TOTAL_PRICE)!!

        shipCountry = intent.getStringExtra("country").toString()
        shipCity = intent.getStringExtra("city").toString()
        shipState = intent.getStringExtra("state").toString()
        shipPincode = intent.getStringExtra("zip").toString()
        shipXaddress = intent.getStringExtra("address").toString()
        shipHouseNo = intent.getStringExtra("house_no").toString()
        shipName = intent.getStringExtra("name").toString()
        shipTax = intent.getStringExtra("tax").toString()
        latitude = intent.getStringExtra("latitude")!!
        longitude = intent.getStringExtra("longitude")!!

        billingAddress = intent.getStringExtra("billing_address")!!
        shippingAddress = intent.getStringExtra("shipping_address")!!

        binding.txtAddress.text = shippingAddress
        productWeight =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.TOTAL_WEIGHT)!!

        viewModel.getUserProfile(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString(),
        )
        getFees()

        binding.btnAppliedCoupon.setOnClickListener {
            applyCoupon()
        }

        binding.btnPlaceOrder.setOnClickListener {
            getOrderDetails()
        }
        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }

        Checkout.preload(applicationContext)


    }

    private fun applyCoupon() {
        log(
            "applyCoupon = ", " D= ${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
                    .toString()
            } Cart= ${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString()
            } User= ${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString()
            } p= $totalPrice"
        )
        if (binding.etCouponCode.text.toString().trim().isEmpty()) {
            toast("Please enter coupon code!")
        } else {
            if (isCouponApplied) {
                viewModel.getFees(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )!!,
                    AddFreeRequest(
                        0,
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
                            .toString(),
                        StaticValue.REST_ID.toInt(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        totalPrice, 0,
                        0, "",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                            .toString(),
                        "ecommerce", shipPincode, productWeight
                    ),
                    "ApplyCoupon",
                )
            } else {
                viewModel.getFees(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )!!,
                    AddFreeRequest(
                        0,
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
                            .toString(),
                        StaticValue.REST_ID.toInt(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        totalPrice, 0, 0, binding.etCouponCode.text.toString().trim(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                            .toString(), "ecommerce", shipPincode, productWeight
                    ), "ApplyCoupon"
                )
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadCartData() {

        viewModel.getCartListData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID).toString(),
            StaticValue.REST_ID,
        )

        viewModel.cartListData.observe(this) { cartData ->
            binding.recyclerCartItem.also {
                it.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = OrderCartDataAdapter(cartData.results!!, this, applicationContext)
            }
        }

    }

    private fun getOrderDetails() {
        val da = JsonObject()
        da.addProperty("name", shipName)
        da.addProperty("add", shippingAddress)
        da.addProperty("pin", shipPincode)
        da.addProperty("city", shipCity)
        da.addProperty("state", shipState)
        da.addProperty("country", shipCountry)
        da.addProperty("phone", userMobile)

        viewModel.updateOrder(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            UpdateOrderRequest(
                "",
                freeResponse.tip!!,
                freeResponse.shipping_fee!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString(),
                "active",
                freeResponse.total!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                freeResponse.tax!!,
                freeResponse.sub_total!!,
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                "0",
                freeResponse.discount!!,
                shippingAddress,
                billingAddress, da.toString()
            )
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

    override fun <T> onSuccess(dataG: T, type: String) {
        log("getUserProfile ", " onSuccess $type $dataG")
        if (type == "getUserProfile") {
            val data: ProfileResponse = dataG as ProfileResponse
            userEmail = data.results!![0].customer!!.email!!
            userMobile = data.results!![0].phone
        } else if (type == "getFees" || type == "ApplyCoupon") {
            progressDialogBinding.progressLayout.visibility = View.GONE
            freeResponse = dataG as FreeResponse
            binding.feeDetails = freeResponse
            binding.btnPlaceOrder.visibility = View.VISIBLE
            if (type == "ApplyCoupon") {
                if (isCouponApplied) {
                    isCouponApplied = false
                    binding.btnAppliedCoupon.text = getString(R.string.apply_coupon)
                    binding.etCouponCode.setText("")
                    binding.etCouponCode.isEnabled = true
                } else {
                    isCouponApplied = true
                    binding.btnAppliedCoupon.text = getString(R.string.cancel_coupon)
                    binding.etCouponCode.isEnabled = false
                }
            }
        } else if (type == "updateOrder") {
            orderDetailResponse = dataG as OrderDetailResponse
            Log.d("PaymentActivity ", "placeOrderID: ${orderDetailResponse.order_id}")
            startPayment(freeResponse.total!!)
            //placeOrder()
        } else if (type == "placeOrder") {
            progressDialogBinding.progressLayout.visibility = View.GONE
            toast("Congratulations! Your order has been placed successfully.")
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("cart", false)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun placeOrder(transactionID: String, paymentMode: String) {
        val reqObj = JsonObject()
        val cardData = JsonObject()
        val metaData = JsonObject()
        val address = JsonObject()
        val billingDetails = JsonObject()

        metaData.addProperty("order_id", orderDetailResponse.order_id)
        metaData.addProperty(
            "shippingmethod_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
        )
        metaData.addProperty("restaurant_id", StaticValue.REST_ID)
        metaData.addProperty("phone", userMobile)
        metaData.addProperty(
            "customer_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)!!.toInt()
        )
        metaData.addProperty("special_instruction", "")

        metaData.addProperty("name", shippingAddress.split(",")[0])
        address.addProperty("city", shippingAddress.split(",")[3])
        address.addProperty("state", shippingAddress.split(",")[4])
        address.addProperty("line1", shippingAddress.split(",")[2])
        address.addProperty("line2", "")
        address.addProperty("house_no", shippingAddress.split(",")[1])
        address.addProperty("postal_code", shippingAddress.split(",")[6])

        billingDetails.add("address", address)
        //Card/Payment
        cardData.addProperty("number", "")
        cardData.addProperty("exp_month", "")
        cardData.addProperty("exp_year", "")
        cardData.addProperty("cvc", "")
        cardData.addProperty("name", "")
        reqObj.addProperty("type", "Online")

        reqObj.addProperty("amount", freeResponse.total!!.toDouble())
        reqObj.addProperty(
            "currency",
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)
        )
        reqObj.addProperty("receipt_email", userEmail)
        reqObj.addProperty("source", "mobile")
        reqObj.add("card", cardData)
        reqObj.add("metadata", metaData)
        reqObj.add("billing_details", billingDetails)
        reqObj.add("borzo_order", getDeliveryData())
        reqObj.addProperty("o_type", "ecommerce")
        reqObj.addProperty("transaction_id", transactionID)
        reqObj.addProperty("payment_method", paymentMode)

        log("PlaceOrder", " $reqObj")

        viewModel.placeOrder(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            reqObj
        )
    }

    private fun getDeliveryData(): JsonObject {
        val data = JsonObject()
        data.addProperty("matter", "")
        data.addProperty("address", "")
        data.addProperty("apartment_number", "")
        data.addProperty("username", "")

        data.addProperty("address", shippingAddress)
        data.addProperty("apartment_number", "")
        data.addProperty("username", shippingAddress.split(",")[0])

        data.addProperty("total_weight_kg", "1")
        data.addProperty("phone", userMobile)
        data.addProperty("latitude", latitude)
        data.addProperty("longitude", longitude)
        data.addProperty("note", "")
        data.addProperty("building_number", "")
        data.addProperty("entrance_number", "")
        data.addProperty("intercom_code", "")
        data.addProperty("floor_number", "")
        return data
    }

    private fun getFees() {
        log(
            "getFees = ", " ${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
                    .toString()
            }"
        )
        log("getFees = ", " ${StaticValue.REST_ID.toInt()}")
        log(
            "getFees = ", "${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString()
            }"
        )
        log("getFees = ", "${totalPrice}")
        log(
            "getFees = ", "${
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString()
            }"
        )
        log("getFees = ", " $shipPincode")
        log("getFees = ", " $productWeight")

        viewModel.getFees(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            AddFreeRequest(
                0,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)
                    .toString(),
                StaticValue.REST_ID.toInt(),
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                totalPrice, 0, 0, "",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString(), "ecommerce", shipPincode, productWeight
            ), "getFees"
        )
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {

    }

    override fun addSubDeleteItem(
        cartData: CartItem,
        product_id: String,
        position: Int,
        sequence: String,
        price: Double,
        count: String,
        check: String,
        cartItemId: String
    ) {

    }

    private fun startPayment2(amount: String) {
        val activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_28buO0s2MeSXiz") // For Testing Mode
        //co.setKeyID("rzp_live_XTK1F5JiP55Dww") // For Live Mode
        try {
            val options = JSONObject()
            options.put("currency", "INR")
            options.put("amount", amount.toDouble() * 100) // Convert amount to double
            options.put("name", getString(R.string.app_name))
            val preFill = JSONObject()
            preFill.put("name", shipName)
            preFill.put("contact", userMobile)
            preFill.put("email", userEmail)
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: Exception) {
            toast("Payment failed , ${e.toString()}")
            log("PlaceOrderActivity", "onPaymentFailed2:  ${e.message}")
        }
    }


    private fun startPayment(amount: String) {
        val activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_28buO0s2MeSXiz") // For Testing Mode
        try {
            val options = JSONObject()
            options.put("currency", "INR")
            options.put("amount", amount.toDouble() * 100) // Convert amount to double
            options.put("name", getString(R.string.app_name))
            val preFill = JSONObject()
            preFill.put("name", shipName)
            preFill.put("contact", userMobile)
            preFill.put("email", userEmail)
            options.put("prefill", preFill)

            val transfer1 = Transfer(
                account = "acc_NYjsh3aImYsrzP",
                amount = 10000,
                currency = "INR",
                notes = Notes(
                    branch = "Niraj Test",
                    name = "Niraj Kumar"
                ),
                linked_account_notes = listOf("branch"),
            )
            options.put("transfers", transfer1)

            log("PlaceOrderActivity", "RequestData:  $options")

            co.open(activity, options)
        } catch (e: Exception) {
            toast("Payment failed , ${e.toString()}")
            log("PlaceOrderActivity", "onPaymentFailed2:  ${e.message}")
        }
    }


    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData) {
        //placeOrder(paymentData.paymentId, "Card")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  $paymentData")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.paymentId}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.orderId}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.data}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.externalWallet}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.signature}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.userContact}")
        log("PlaceOrderActivity ", "onPaymentSuccess 22:  ${paymentData.userEmail}")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            log("PlaceOrderActivity", "onPaymentFailed: sendSpaceSubscriptionData $p2")
            log("PlaceOrderActivity", "onPaymentFailed: sendSpaceSubscriptionData $p1")
            log("PlaceOrderActivity", "onPaymentFailed: sendSpaceSubscriptionData $p0")
            toast("Payment failed, Please try after some time")
        } catch (e: Exception) {
            log("PlaceOrderActivity", "Exception in onPaymentError  $e")
        }
    }


}

data class Transfer(
    val account: String,
    val amount: Int,
    val currency: String,
    val notes: Notes,
    val linked_account_notes: List<String>
)

data class Notes(
    val branch: String,
    val name: String
)