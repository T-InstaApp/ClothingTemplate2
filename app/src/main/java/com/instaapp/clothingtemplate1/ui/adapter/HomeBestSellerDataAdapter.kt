package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.databinding.HomeBestSellerListItemBinding
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener

class HomeBestSellerDataAdapter(
    var data: ArrayList<ProductDataMode>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<HomeBestSellerDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_best_seller_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.homeBestSellerListItemBinding.menuData = data[position]

        holder.homeBestSellerListItemBinding.layout.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.homeBestSellerListItemBinding.layout,
                "${data[position].product_id}@${data[position].product_name}@${data[position].price}@${data[position].price}@${data[position].brand}@${data[position].cancellation_Policy}",
                "Best Seller"
            )
        }
    }

    inner class OrderViewHolder(
        val homeBestSellerListItemBinding: HomeBestSellerListItemBinding
    ) : RecyclerView.ViewHolder(homeBestSellerListItemBinding.root)
}