package com.instaapp.clothingtemplate1.dataModel


data class DeliveryAPIResponse(
    val delivery_codes: ArrayList<DeliveryCode>?
)

data class DeliveryCode(
    val postal_code: PostalCode?
)

data class PostalCode(
    val city: String?,
    val cod: String?,
    val inc: String?,
    val district: String?,
    val pin: Int?,
    val max_amount: Double?,
    val pre_paid: String?,
    val cash: String?,
    val max_weight: Double?,
    val pickup: String?,
    val repl: String?,
    val covid_zone: String?,
    val country_code: String?,
    val is_oda: String?,
    val remarks: String?,
    val sort_code: String?,
    val state_code: String?,
    val center: ArrayList<Center>?
)

data class Center(
    val code: String?,
    val e: String?,
    val cn: String?,
    val s: String?,
    val u: String?,
    val ud: String?,
    val sort_code: String?
)
