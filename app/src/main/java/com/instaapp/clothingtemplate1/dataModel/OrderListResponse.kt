package com.instaapp.clothingtemplate1.dataModel

data class OrderListResponse(
    val payment_id: Int?,
    val amount: String?,
    val order: Order?,
    val shippingmethod: ShippingMode?,
    val product_data: ArrayList<ProductDataList>?,
    val delhivery_obj: DelhiveryObj?,
    val transaction_id: String?,
    val status: String?,
    val created_at: String?,
    val updated_at: String?,
    val payment_method: String?,
    val payout_status: String?,
    val payout_message: String?,
    val payout_id: String?,
    val correlation_id: String?,
    val signature: Any?,
)

data class DelhiveryObj(
    val delhivery_order_id: Int?,
    val waybill: String?
)

data class Order(
    val order_id: Int?,
    val subtotal: String?,
    val tip: String?,
    val service_fee: String?,
    val tax: String?,
    val shipping_fee: String?,
    val discount: String?,
    val total: String?,
    val shipping_address_text: String?,
    val billing_address_text: String?,

    )

data class ShippingMode(
    val id: Int?,
    val name: String?,
)

data class ProductDataList(
    val product_id: Int?,
    val product_name: String?,
    val product_url: String?,
    val brand: String?,
    val quantity: Int = 0,
    val color_id: String?,
    val color: String?,
    val price: String?,
    val size_id: Int?,
    val size: String?,
)

