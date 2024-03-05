package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.Scan
import com.instaapp.clothingtemplate1.databinding.TrackingLayoutStatusListItemBinding
import com.instaapp.clothingtemplate1.utils.separateDateAndTime

class ClothingOrderStatusDataAdapter(
    private val dataList: ArrayList<Scan>,
    private val context: Context,
    private val textViewStatus: TextView
) :
    RecyclerView.Adapter<ClothingOrderStatusDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tracking_layout_status_list_item, parent, false
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        holder.trackingLayoutStatusListItemBinding.txtDetails.text =
            dataList[position].ScanDetail.Instructions
        val result = separateDateAndTime(dataList[position].ScanDetail.ScanDateTime)

        if (dataList[position].ScanDetail.Scan == "Manifested")
            holder.trackingLayoutStatusListItemBinding.txtStatus.text = "Prepared for Dispatch"


        if (result != null) {
            val (date, time) = result
            holder.trackingLayoutStatusListItemBinding.txtDate.text = date
            holder.trackingLayoutStatusListItemBinding.txtTime.text = time
        }

        if (dataList[position].ScanDetail.Scan == "Manifested")
            holder.trackingLayoutStatusListItemBinding.view2.visibility = View.VISIBLE

        if ((position + 1) == dataList.size)
            if (dataList[position].ScanDetail.Scan == "Manifested")
                textViewStatus.text = "Status - Prepared for Dispatch"

    }

    inner class OrderViewHolder(
        val trackingLayoutStatusListItemBinding: TrackingLayoutStatusListItemBinding
    ) : RecyclerView.ViewHolder(trackingLayoutStatusListItemBinding.root)
}