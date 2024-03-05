package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.dataModel.Size
import com.instaapp.clothingtemplate1.dataModel.SizeDataModel
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.databinding.SizeListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener


class SizeDataAdapter(
    var data: ArrayList<Size>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<SizeDataAdapter.OrderViewHolder>() {
    var selectedItem = 0
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.size_list_item, parent, false
        )
    )

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.sizeListItemBinding.sizeData = data[position]
        if (position == selectedItem) {
            holder.sizeListItemBinding.layout.outlineSpotShadowColor =
                (context.getColor(R.color.prime_dark))
            holder.sizeListItemBinding.layout.outlineAmbientShadowColor =
                (context.getColor(R.color.prime_dark))
            holder.sizeListItemBinding.layout.strokeWidth = 1
        } else {
            holder.sizeListItemBinding.layout.outlineSpotShadowColor =
                (context.getColor(R.color.semi_background))
            holder.sizeListItemBinding.layout.outlineAmbientShadowColor =
                (context.getColor(R.color.semi_background))
            holder.sizeListItemBinding.layout.strokeWidth = 0
        }
        holder.sizeListItemBinding.layout.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
            listener.onRecyclerItemClick(
                holder.sizeListItemBinding.layout,
                "${data[position].size_id}",
                "Size"
            )
        }

    }

    inner class OrderViewHolder(
        val sizeListItemBinding: SizeListItemBinding
    ) : RecyclerView.ViewHolder(sizeListItemBinding.root)
}