package com.instaapp.clothingtemplate1.listener

import com.instaapp.clothingtemplate1.dataModel.CartItem


interface RecyclerViewCartClickListener {
    fun addSubDeleteItem(
        cartData: CartItem,
        product_id: String,
        position: Int,
        sequence: String,
        price: Double,
        count: String,
        check: String,
        cartItemId: String
    )
}