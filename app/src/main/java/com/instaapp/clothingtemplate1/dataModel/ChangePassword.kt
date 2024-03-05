package com.instaapp.clothingtemplate1.dataModel

data class ChangePassword(
    var old_password: String,
    var username: String,
    var new_password1: String,
    var new_password2: String
)

