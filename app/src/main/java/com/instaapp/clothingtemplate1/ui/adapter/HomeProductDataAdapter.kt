package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener

class HomeProductDataAdapter(
    var data: ArrayList<ProductDataMode>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<HomeProductDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_featured_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.homeFeaturedListItemBinding.menuData = data[position]

        holder.homeFeaturedListItemBinding.layout.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.homeFeaturedListItemBinding.layout,
                "${data[position].product_id}@${data[position].product_name}@${data[position].price}@${data[position].price}@${data[position].brand}@${data[position].cancellation_Policy}",
                "Featured Product"
            )
        }


    }

    inner class OrderViewHolder(
        val homeFeaturedListItemBinding: HomeFeaturedListItemBinding
    ) : RecyclerView.ViewHolder(homeFeaturedListItemBinding.root)
}