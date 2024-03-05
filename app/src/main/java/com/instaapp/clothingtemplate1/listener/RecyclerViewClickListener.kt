package com.instaapp.clothingtemplate1.listener

import android.view.View

interface RecyclerViewClickListener {
    //fun onRecyclerItemClick(view: View, catID: Int)
    fun <T> onRecyclerItemClick(view: View, dataG: T,status: String)
}