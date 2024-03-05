package com.instaapp.clothingtemplate1.dataModel


data class CartListResponse(
    val count: Int?,
    val results: ArrayList<CartItem>?,
    val total_cost: String?
)

data class CartItem(
    val cart_item_id: Int?,
    val cart: Int?,
    val product: CartProduct?,
    val size: String?,
    val product_price: String?,
    val category_available_time: String?,
    val category_status: String?,
    val quantity: Int?,
    val price: String?,
    val extra: String?,
    val color: Color?,
    val product_size: Size?,
    val master_category_open_hour: Boolean?,
    val cart_item_in_stock: Boolean?
)

data class CartProduct(
    val product_id: Int?,
    val product_name: String?,
    val product_url: String?,
    val price: String?,
    val media: String?,
    val caption: String?,
    val extra: String?,
    val image: String?,
    val status: String?,
    val tax_exempt: Boolean?,
    val source_id: String?,
    val optional: Boolean?,
    val sequence: String?,
    val in_stock: Boolean?,
    val available_time: String?,
    val delivery_type: String?,
    val pos_id: String?,
    val tax_rate: String?,
    val suspended_until: String?,
    val brand: String?,
    val model: String?,
    val sub_category: String?,
    val created_at: String?,
    val MRP: String? = "0.0",
    val restaurant: Int?,
    val category: Int?,
    val weight: String?,
)








