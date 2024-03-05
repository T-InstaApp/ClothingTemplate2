package com.instaapp.clothingtemplate1.dataModel

import com.google.gson.JsonObject

class AddFreeRequest(
    no_tax_total: Int?, shipping_id: String?, restaurant_id: Int?,
    customer_id: String?, sub_total: String?, tip: Int?,
    custom_tip: Int?, coupon: String, cart_id: String, type: String, destination_pincode: String,
    weight: String
) {
    private var no_tax_total: Int?
    private var shipping_id: String?
    private var restaurant_id: Int?
    private var customer_id: String?
    private var sub_total: String?
    private var tip: Int?
    private var custom_tip: Int?
    private var coupon: String?
    private var cart_id: String
    private var destination_pincode: String?
    private var type: String?
    private var weight: String?

    init {
        this.no_tax_total = no_tax_total!!
        this.shipping_id = shipping_id!!
        this.restaurant_id = restaurant_id
        this.customer_id = customer_id
        this.sub_total = sub_total
        this.tip = tip
        this.custom_tip = custom_tip
        this.coupon = coupon
        this.cart_id = cart_id
        this.type = type
        this.destination_pincode = destination_pincode
        this.weight = weight
    }


}