package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.CartDataModel
import com.instaapp.clothingtemplate1.dataModel.OrderListResponse
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.databinding.CartListItemBinding
import com.instaapp.clothingtemplate1.databinding.HomeBestSellerListItemBinding
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.databinding.OrderListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.ui.activity.TrackingActivity
import com.instaapp.clothingtemplate1.utils.log

class OrderDataAdapter(
    var data: ArrayList<OrderListResponse>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<OrderDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.orderListItemBinding.menuData = data[position]

        // Obtain the LayoutInflater from the context
        val layoutInflater = LayoutInflater.from(context)
        val customLayout: View = layoutInflater.inflate(R.layout.order_item, null)

        holder.orderListItemBinding.layoutItem.removeAllViews()

        holder.orderListItemBinding.txtStatus.text = data[position].status!!.replace("_", " ")

        holder.orderListItemBinding.txtTrackOrder.setOnClickListener {
            val intent = Intent(context, TrackingActivity::class.java)
            intent.putExtra("order_id", data[position].order!!.order_id.toString())
            intent.putExtra("status", data[position].status)
            intent.putExtra("waybill", " ")
            if (data[position].delhivery_obj != null)
                intent.putExtra("waybill", data[position].delhivery_obj!!.waybill)
            context.startActivity(intent)
        }
        holder.orderListItemBinding.layoutCancelOrder.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.orderListItemBinding.layout, data[position], "Cancel"
            )
        }
        if ((data[position].delhivery_obj != null && data[position].delhivery_obj!!.waybill!!.length > 5) || data[position].status.equals(
                "Canceled"
            )
        )
            holder.orderListItemBinding.layoutCancelOrder.visibility = View.GONE


        for (n in data[position].product_data!!) {
            // Inflate a new customLayout for each item
            val customLayout: View = layoutInflater.inflate(R.layout.order_item, null)

            val txtItemName = customLayout.findViewById<TextView>(R.id.txtItemName)
            val txtQTY = customLayout.findViewById<TextView>(R.id.txtQty)
            val txtPrice = customLayout.findViewById<TextView>(R.id.txtPrice)

            txtItemName.text = n.product_name
            txtQTY.text = (n.quantity).toString()
            txtPrice.text = n.price

            // Add the customLayout to layoutItem
            holder.orderListItemBinding.layoutItem.addView(customLayout)
            // Make sure to set layout parameters, for example, if you want to set the width and height of the customLayout.
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            customLayout.layoutParams = params
        }

    }

    inner class OrderViewHolder(
        val orderListItemBinding: OrderListItemBinding
    ) : RecyclerView.ViewHolder(orderListItemBinding.root)
}