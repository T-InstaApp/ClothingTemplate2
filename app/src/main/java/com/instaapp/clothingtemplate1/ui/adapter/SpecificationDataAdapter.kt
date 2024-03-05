package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ColorDataModel
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.dataModel.SizeDataModel
import com.instaapp.clothingtemplate1.dataModel.Specification
import com.instaapp.clothingtemplate1.databinding.ColorListItemBinding
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.databinding.ProductDetailsListItemBinding
import com.instaapp.clothingtemplate1.databinding.SizeListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener


class SpecificationDataAdapter(
    var data: ArrayList<Specification>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<SpecificationDataAdapter.OrderViewHolder>() {
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_details_list_item, parent, false
        )
    )

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.productDetailsListItemBinding.specificationData = data[position]
    }

    inner class OrderViewHolder(
        val productDetailsListItemBinding: ProductDetailsListItemBinding
    ) : RecyclerView.ViewHolder(productDetailsListItemBinding.root)
}