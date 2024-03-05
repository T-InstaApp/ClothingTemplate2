package com.instaapp.clothingtemplate1.dataModel

data class OrderStatusDataModel(
    val id: Int?,
    val date: String?,
    val time: String?,
    val details: String?,
    val status: String?,
    val drawStatus: Boolean = false,
    val orderDeliveryStatus: Boolean = false,
)
