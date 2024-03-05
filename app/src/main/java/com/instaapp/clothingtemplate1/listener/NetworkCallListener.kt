package com.instaapp.clothingtemplate1.listener

interface NetworkCallListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}