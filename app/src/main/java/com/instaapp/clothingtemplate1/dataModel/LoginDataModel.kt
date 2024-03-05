package com.instaapp.clothingtemplate1.dataModel

data class LoginDataModel(val username: String, val password: String, val restaurant_id: String)

data class ResetUserName(
    val email: String,
    val password: String,
    val username: String
)


data class MobileVerificationRequest(val phone_number: String, val verification_code: String)
