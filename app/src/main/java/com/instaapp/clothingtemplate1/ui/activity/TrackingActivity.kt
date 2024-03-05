package com.instaapp.clothingtemplate1.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.OrderStatusDataModel
import com.instaapp.clothingtemplate1.dataModel.ShipmentData
import com.instaapp.clothingtemplate1.databinding.ActivityAddAddressBinding
import com.instaapp.clothingtemplate1.databinding.ActivityTrackinBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.provider.OrderViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.ClothingOrderStatusDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.OrderViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class TrackingActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var binding: ActivityTrackinBinding
    private lateinit var toolbar: ToolbarBinding
    val TAG = "TrackingActivity"
    private var orderID = ""
    lateinit var progressDialogBinding: ProgressDialogBinding
    private var orderStatus = ""
    private var waybillID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolbar
        toolbar.imgInfo.visibility = View.VISIBLE
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }
        toolbar.txtTopHeading.text = "Track Order"
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()
        orderID = intent.getStringExtra("order_id").toString()
        orderStatus = intent.getStringExtra("status")!!
        waybillID = intent.getStringExtra("waybill")!!
        binding.orderNumber.text = "Order ID- $orderID"

        if (waybillID.length > 5) {
            binding.txtMessage.visibility = View.GONE
            getTrackingData()
            binding.txtTrackingID.text = "Tracking ID - $waybillID (AWB)"
        } else
            binding.orderStatus.text = orderStatus.replace("_", " ")

        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }

        toolbar.imgInfo.setOnClickListener {
            info(
                this,
                "For order tracking or delivery status queries, contact our delivery partner at +91 8069856101 or reach out to our support team at +91 8770577785"
            )
        }
    }

    private fun getTrackingData() {
        viewModel.orderTracking(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            waybillID
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
        if (type == "orderTracking") {
            val data: ShipmentData = dataG as ShipmentData
            val layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            binding.recyclerView.layoutManager = layoutManager
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = ClothingOrderStatusDataAdapter(
                data.ShipmentData[0].Shipment.Scans,
                applicationContext, binding.orderStatus
            )
        }
    }
}