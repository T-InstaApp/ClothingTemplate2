package com.instaapp.clothingtemplate1.dataModel

data class ProductSizeDataModel(
    val product_id: Int,
    val size: ArrayList<Size>,
    val product_name: String,
    val product_url: String,
    val price: Double?,
    val media: String,
    val caption: String,
    val extra: Any?,
    val status: String,
    val tax_exempt: Boolean,
    val source_id: Any?,
    val optional: Boolean,
    val sequence: Any?,
    val in_stock: Boolean,
    val available_time: String,
    val delivery_type: String?,
    val pos_id: Int?,
    val tax_rate: Double?,
    val suspended_until: String?,
    val brand: String,
    val model: String,
    val sub_category: String,
    val created_at: String,
    val MRP: Double?,
    val restaurant: Int,
    val category: Int
)

data class Size(
    val size_id: Int,
    val size: String,
    val is_color_available: Boolean,
    val price: Double?,
    val quantity: Int?,
    val product: Int
)

data class ProductWithColors(
    val product_id: Int,
    val colors: ArrayList<Color2>,
    val product_name: String,
    val product_url: String,
    val price: Double?,
    val media: String,
    val caption: String,
    val extra: Any?,
    val status: String,
    val tax_exempt: Boolean,
    val source_id: Any?,
    val optional: Boolean,
    val sequence: Any?,
    val in_stock: Boolean,
    val available_time: String,
    val delivery_type: String?,
    val pos_id: Int?,
    val tax_rate: Double?,
    val suspended_until: String?,
    val brand: String,
    val model: String,
    val sub_category: String,
    val created_at: String,
    val MRP: Double?,
    val restaurant: Int,
    val category: Int
)

data class Color2(
    val color_id: Int,
    val color: String,
    val title: String?,
    val size: String,
    val price: Double?,
    val quantity: Int?,
    val product: Int,
    val image: String?
)

data class Color(
    val color_id: Int,
    val color: String,
    val title: String?,
    val size: ColorSize,
    val price: Double?,
    val quantity: Int?,
    val product: Int,
    val image: String?
)

data class ColorSize(
    val name: String?,
    val id: Int?
)
