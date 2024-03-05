package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.databinding.ActivityProductDetailsBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.BannerViewPagerAdapter
import com.instaapp.clothingtemplate1.ui.adapter.ColorDataAdapter
import com.instaapp.clothingtemplate1.ui.adapter.SizeDataAdapter
import com.instaapp.clothingtemplate1.ui.adapter.SpecificationDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailsActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {

    override val kodein by kodein()
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    lateinit var progressDialogBinding: ProgressDialogBinding
    private var productID = 0
    private var productName = ""
    private var productPrice = ""
    private var productFinalPrice = ""
    private var brandName = ""
    private var cancellationPolicy = ""

    // Declare variables for the timer and timer task
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private val delay: Long = 10000 // Change this value as needed
    var colorData: ArrayList<Color2> = arrayListOf()

    var sizeID = 0
    var colorID = 0
    var currency = ""
    var productImageUrl = ""
    private val TAG = "ProductDetailsActivity"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        currency =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!
        toolbar = binding.toolbar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        colorData = arrayListOf()

        productID = intent.getIntExtra("product_id", 0)
        productName = intent.getStringExtra("productName")!!
        productPrice = intent.getStringExtra("productPrice")!!
        productFinalPrice = intent.getStringExtra("productPrice")!!
        brandName = intent.getStringExtra("brandName")!!
        cancellationPolicy = intent.getStringExtra("cancellation_Policy")!!

        binding.txtBrandName.text = brandName
        binding.txtProductName.text = productName
        toolbar.txtTopHeading.text = brandName
        binding.txtMainPrice.text =
            "${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} $productFinalPrice"
        binding.txtPrice.text =
            "${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} $productPrice"

        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        val layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        binding.recyclerColor.layoutManager = layoutManager
        binding.recyclerColor.setHasFixedSize(true)

        binding.txtAdd.setOnClickListener { addQty() }
        binding.txtSub.setOnClickListener { subQty() }
        binding.layoutAddToCart.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                if (colorID != 0) {
                    addToCart()
                } else
                    toast("Please select available size and color")
            else loginFirst(this, "Other")
        }
        log(
            TAG,
            " Login Status ${PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)}"
        )
        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
            checkCart()
        getSizeData()
        getSpecification()
        binding.txtCheckLocation.setOnClickListener { checkDeliverStatus() }

        toolbar.cartLayout.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                callMainActivity(this)
            else
                loginFirst(this, "Other")
        }
    }


    private fun addQty() {
        var qty = binding.txtQty.text.toString().trim().toInt()
        if (qty < 9) {
            qty += 1
            binding.txtQty.text = qty.toString()
            val tempFinalPrice = (productFinalPrice.toDouble()) * qty
            binding.txtMainPrice.text = tempFinalPrice.toString()
            binding.txtPrice.text = (productPrice.toDouble() * qty).toString()
        }
    }

    private fun subQty() {
        var qty = binding.txtQty.text.toString().trim().toInt()
        if (qty > 1) {
            qty -= 1
            binding.txtQty.text = qty.toString()
            val tempFinalPrice = (productFinalPrice.toDouble()) * qty
            binding.txtMainPrice.text = tempFinalPrice.toString()
            binding.txtPrice.text = (productPrice.toDouble() * qty).toString()
        }
    }

    private fun showAllImages(imageURl: String) {
        val imageList = imageURl.trimEnd('@').split("@")
        bannerViewPagerAdapter =
            BannerViewPagerAdapter(this, imageList, "Details", imageURl)
        binding.bannerKocMarket.adapter = bannerViewPagerAdapter
        binding.tabDots.setupWithViewPager(binding.bannerKocMarket, true)
        startAutoSlide()
    }

    private fun startAutoSlide() {
        // Cancel any existing timer and task
        stopAutoSlide()
        // Create a new timer and task
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                // Perform slide change logic here
                runOnUiThread {
                    val currentItem = binding.bannerKocMarket.currentItem
                    val nextItem = (currentItem + 1) % binding.bannerKocMarket.adapter?.count!!
                    binding.bannerKocMarket.setCurrentItem(nextItem, true)
                }
            }
        }
        // Schedule the task with the desired delay
        timer?.schedule(timerTask, delay, delay)
    }

    private fun stopAutoSlide() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null
    }

    private fun getSizeData() {
        viewModel.getProductSize(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productID, "Size"
        )

    }

    private fun getColorData(sizeID: Int) {
        colorData = arrayListOf()
        colorID = 0
        binding.recyclerColor.adapter =
            ColorDataAdapter(colorData, this, applicationContext)
        viewModel.getProductColor(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productID, "Color", sizeID
        )
    }

    private fun getSpecification() {
        viewModel.getProductSpecificationId(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productID
        )
    }

    private fun checkCart() {
        log("$TAG checkCart ", " Called ")
        viewModel.checkCartAvailable(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            StaticValue.REST_ID,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString(),
        )
    }

    private fun checkDeliverStatus() {
        if (binding.etPincode.text!!.isNotEmpty()) {
            viewModel.checkDeliverStatus(
                "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                binding.etPincode.text!!.toString()
            )
        } else {
            toast("Please enter your delivery location pincode")
        }
    }

    private fun createCart() {
        log("$TAG createCart ", " CreateCart Called ")
        viewModel.createCart(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            CreateCartRequest(
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                StaticValue.REST_ID
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
        progressDialogBinding.progressLayout.visibility = View.GONE
        log(TAG, "onSuccess $type ")
        if (type == "getProductSpecificationId") {
            val data: CarSpecificationDataModel = dataG as CarSpecificationDataModel
            val layoutManager =
                GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
            binding.recyclerProductDetails.layoutManager = layoutManager
            binding.recyclerProductDetails.setHasFixedSize(true)
            binding.recyclerProductDetails.adapter =
                SpecificationDataAdapter(data.specification, this, applicationContext)
        } else if (type == "getProductSize") {
            val data: ProductSizeDataModel = dataG as ProductSizeDataModel
            if (data.size.isNotEmpty()) {
                val layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
                binding.recyclerSize.layoutManager = layoutManager
                binding.recyclerSize.setHasFixedSize(true)
                binding.recyclerSize.adapter =
                    SizeDataAdapter(data.size, this, applicationContext)
                getColorData(data.size[0].size_id)
                sizeID = data.size[0].size_id

            }
        } else if (type == "getProductColor") {
            val data: ProductWithColors = dataG as ProductWithColors
            if (data.colors.isNotEmpty()) {
                binding.recyclerColor.visibility = View.VISIBLE
                binding.txtColorDataNotAvailable.visibility = View.GONE
                binding.recyclerColor.adapter =
                    ColorDataAdapter(data.colors, this, applicationContext)
                productFinalPrice = data.colors[0].price!!.toString()
                colorID = data.colors[0].color_id
                val tempPrice =
                    (productFinalPrice.toDouble() * (binding.txtQty.text.toString()
                        .toInt())).toString()
                binding.txtMainPrice.text = tempPrice
                binding.txtPrice.text = ((productPrice).toDouble() * (binding.txtQty.text.toString()
                    .toInt())).toString()

                productImageUrl = data.product_url
                if (data.colors[0].image != null && data.colors[0].image!!.length > 5)
                    showAllImages(data.colors[0].image!!)
                else
                    showAllImages(data.product_url)
            } else {
                binding.txtColorDataNotAvailable.visibility = View.VISIBLE
                binding.recyclerColor.visibility = View.GONE
            }
        } else if (type == "checkCart") {
            val data: CheckCartResponse = dataG as CheckCartResponse
            if (data.results!!.isNotEmpty()) {
                PreferenceProvider(applicationContext).setIntValue(
                    data.results[0].id.toString().toInt(),
                    PreferenceKey.CART_ID
                )
            } else createCart()
        } else if (type == "createCart") {
            val data: CreateCartResponse = dataG as CreateCartResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id.toString().toInt(),
                PreferenceKey.CART_ID
            )
        } else if (type == "addToCart") {
            val data: AddToCartResponse = dataG as AddToCartResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.count!!,
                PreferenceKey.CART_COUNT
            )
            toolbar.txtCartItemCount.text = data.count.toString()
            toast("Product successfully added in your cart")
        } else if (type == "checkDeliverStatus") {
            val data: DeliveryAPIResponse = dataG as DeliveryAPIResponse
            log("checkDeliverStatus", " $data")
            if (data.delivery_codes != null && data.delivery_codes.size > 0) {
                binding.checkboxDeliveryStatus.setImageResource(R.drawable.ic_right)
                binding.txtLocationErrorMsg.text = ""
            } else {
                binding.checkboxDeliveryStatus.setImageResource(R.drawable.ic_wrong)
                binding.txtLocationErrorMsg.text =
                    "Sorry, we do not ship to this pincode, Try another one."
            }
        }
    }

    private fun addToCart() {
        val b: Double = java.lang.Double.valueOf(
            binding.txtMainPrice.text.toString()
                .replace(
                    currency,
                    ""
                )
        )
        val qty = binding.txtQty.text.toString().trim().toDouble()
        val jsonArray1 = JsonArray()
        val jsonObject = JsonObject()

        jsonObject.addProperty("price", b)
        jsonObject.addProperty("extra", "")
        jsonObject.addProperty("quantity", qty.toInt())
        jsonObject.addProperty(
            "cart_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
        )
        jsonObject.addProperty("product_id", productID)
        jsonObject.addProperty("size_id", "")
        jsonObject.addProperty("color_id", colorID)
        jsonObject.add("addon_content_list", jsonArray1)

        Log.d(TAG, " addToCart: $jsonObject")

        viewModel.addToCart(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            jsonObject
        )
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        log("onRecyclerItemClick ", " status =$status  && $dataG")
        if (status == "Size") {
            sizeID = dataG.toString().toInt()
            getColorData(dataG.toString().toInt())
        } else if (status == "Color") {
            val data: Color2 = dataG as Color2
            colorID = data.color_id
            productFinalPrice = data.price.toString()
            val tempPrice =
                (productFinalPrice.toDouble() * (binding.txtQty.text.toString().toInt())).toString()
            binding.txtMainPrice.text = tempPrice
            binding.txtPrice.text = ((productPrice).toDouble() * (binding.txtQty.text.toString()
                .toInt())).toString()
            log("onRecyclerItemClick ", " status =${data.image}")
            if (data.image != null && data.image.length > 5)
                showAllImages(data.image)
            else
                showAllImages(productImageUrl)
        }
    }
}