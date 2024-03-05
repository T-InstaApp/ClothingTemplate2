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
import com.instaapp.clothingtemplate1.dataModel.MasterCategoryDataModel
import com.instaapp.clothingtemplate1.databinding.MasterCategoryListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener

var selectedItem = 0

class MasterCategoryAdapter(
    private val catData: List<MasterCategoryDataModel>, private val context: Context,
    private val listener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<MasterCategoryAdapter.CategoryViewHolder>() {

    override fun getItemCount() = catData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.master_category_list_item, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryListItemBinding.categoryData = catData[position]
        holder.categoryListItemBinding.cardLayout.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
            listener.onRecyclerItemClick(
                holder.categoryListItemBinding.cardLayout,
                "${catData[position].master_category_id}@${catData[position].master_category_name}",
                "Category"
            )
        }

    }

    inner class CategoryViewHolder(
        val categoryListItemBinding: MasterCategoryListItemBinding
    ) : RecyclerView.ViewHolder(categoryListItemBinding.root)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}