package com.instaapp.clothingtemplate1.network


import com.instaapp.clothingtemplate1.utils.ApiException
import com.instaapp.clothingtemplate1.utils.log
import org.json.JSONObject
import retrofit2.Response


abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T? {
        val response = call.invoke()
        if (response.isSuccessful) {
            val resData = response.body()
            return resData
        } else {
            val error = response.errorBody()?.string()
            var message = "NA"
            error?.let {
                message = try {
                    val jObjError = JSONObject(error)
                    jObjError.get("msg").toString()
                } catch (e: Exception) {
                    "The system is temporarily unavailable. Please try again later"
                }
            }
            throw ApiException(message + "@" + response.code() + "@" + error)
        }
    }
}