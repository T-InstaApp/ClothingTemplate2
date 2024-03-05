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
import com.instaapp.clothingtemplate1.databinding.ColorListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener


class ColorDataAdapter(
    var data: ArrayList<com.instaapp.clothingtemplate1.dataModel.Color2>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<ColorDataAdapter.OrderViewHolder>() {
    var selectedItem = 0
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.color_list_item, parent, false
        )
    )

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.colorListItemBinding.colorData = data[position]

        try {
            holder.colorListItemBinding.txtSize.setBackgroundColor(
                Color.parseColor(data[position].color.split(",")[1])
            )
        } catch (ignore: Exception) {
        }

        if (position == selectedItem) {
            holder.colorListItemBinding.layout.outlineSpotShadowColor =
                (context.getColor(R.color.prime_dark))

            holder.colorListItemBinding.layout.strokeColor =
                (context.getColor(R.color.prime_dark))

            holder.colorListItemBinding.layout.strokeWidth = 3
        } else {
            holder.colorListItemBinding.layout.strokeColor =
                (context.getColor(R.color.semi_transparent))
            holder.colorListItemBinding.layout.strokeWidth = 1
        }
        holder.colorListItemBinding.layout.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
            listener.onRecyclerItemClick(
                holder.colorListItemBinding.layout,
                data[position],
                "Color"
            )
        }

    }

    inner class OrderViewHolder(
        val colorListItemBinding: ColorListItemBinding
    ) : RecyclerView.ViewHolder(colorListItemBinding.root)
}