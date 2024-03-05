package com.instaapp.clothingtemplate1.repositories



import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.dataModel.AddBillingShippingAddressRequest
import com.instaapp.clothingtemplate1.dataModel.AddFreeRequest
import com.instaapp.clothingtemplate1.dataModel.UpdateOrderRequest
import com.instaapp.clothingtemplate1.network.MyApi
import com.instaapp.clothingtemplate1.network.SafeApiRequest

class CarRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun getShippingAddress(token: String, userID: String) =
        apiRequest { api.getShippingAddress(token, userID) }

    suspend fun getBillingAddress(token: String, userID: String) =
        apiRequest { api.getBillingAddress(token, userID) }

    suspend fun addShippingAddress(token: String, data: AddBillingShippingAddressRequest) =
        apiRequest { api.addShippingAddress(token, data) }

    suspend fun updateShippingAddress(
        token: String, id: String, data: AddBillingShippingAddressRequest
    ) = apiRequest { api.updateShippingAddress(token, id, data) }

    suspend fun updateBillingAddress(
        token: String, id: String, data: AddBillingShippingAddressRequest
    ) = apiRequest { api.updateBillingAddress(token, id, data) }

    suspend fun addBillingAddress(token: String, data: AddBillingShippingAddressRequest) =
        apiRequest { api.addBillingAddress(token, data) }

    suspend fun getCartListData(token: String, cartID: String, restroID: String) =
        apiRequest {
            api.getCartList(token, cartID, restroID)
        }
    suspend fun getUserProfile(token: String, userID: String) = apiRequest {
        api.getProfile(token, userID)
    }

    suspend fun getFees(token: String, data: AddFreeRequest) = apiRequest {
        api.getFees(token, data)
    }
    suspend fun updateOrderDetails(token: String, data: UpdateOrderRequest) = apiRequest {
        api.updateOrderDetail(token, data)
    }
    suspend fun placeOrder(token: String, data: JsonObject) = apiRequest {
        api.placeOrder(token, data)
    }

    suspend fun trackOrder(token: String, id: String) =
        apiRequest { api.trackOrder(token, id) }

}