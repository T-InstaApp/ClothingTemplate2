package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.CategoryResponse
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.dataModel.SubCategoryDataModel
import com.instaapp.clothingtemplate1.databinding.CategoryDataListItemBinding
import com.instaapp.clothingtemplate1.databinding.HomeFeaturedListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener

class CategoryDataAdapter(
    var data: ArrayList<SubCategoryDataModel>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<CategoryDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.category_data_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.categoryDataListItemBinding.menuData = data[position]

        if (position % 3 == 2)
            holder.categoryDataListItemBinding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        holder.categoryDataListItemBinding.layout.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.categoryDataListItemBinding.layout,
                "${data[position].sub_category}",
                "SubCategory"
            )
        }
    }


    inner class OrderViewHolder(
        val categoryDataListItemBinding: CategoryDataListItemBinding
    ) : RecyclerView.ViewHolder(categoryDataListItemBinding.root)
}