package com.instaapp.clothingtemplate1.dataModel

data class UserDataModel(
    val user: String,
    var restaurant: String
)

data class UserRestaurant(
    val id: Int?,
    val user: Int?,
    val restaurant: Int?,
)
