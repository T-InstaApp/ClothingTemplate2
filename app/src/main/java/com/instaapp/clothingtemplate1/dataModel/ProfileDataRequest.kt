package com.instaapp.clothingtemplate1.dataModel

data class ProfileDataRequest(
    var salutation: String,
    var first_name: String,
    var last_name: String,
    var username: String,
    var email: String,
    var last_access: String,
    var phone_number: String
)
