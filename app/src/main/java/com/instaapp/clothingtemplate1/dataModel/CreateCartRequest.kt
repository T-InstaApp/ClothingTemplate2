package com.instaapp.clothingtemplate1.dataModel

class CreateCartRequest(customer_id: String, restaurant: String) {
    val restaurant: String?
    val customer_id: String?

    init {
        this.restaurant = restaurant
        this.customer_id = customer_id
    }
}