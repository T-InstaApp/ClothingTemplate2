package com.instaapp.clothingtemplate1.repositories


import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.network.MyApi
import com.instaapp.clothingtemplate1.network.SafeApiRequest
import com.instaapp.clothingtemplate1.utils.log

class HomeRepository(private val api: MyApi) : SafeApiRequest() {


    suspend fun getProduct(token: String, type: String, restID: String, catId: String,masterCAT: String,subCategory:String) =

        apiRequest {   log("getProduct"," $type, $restID,$catId, $masterCAT, $subCategory")
            api.getProductData(token, type, restID, catId,masterCAT,subCategory) }

    suspend fun getAppDetails(restorID: Int, type: String) =
        apiRequest { api.getAppDetails(restorID, type) }

    suspend fun getSearchKey(token: String, type: String, restID: Int) =
        apiRequest { api.getSearchKey(token, type, restID) }

    suspend fun getMasterCat(token: String, restroID: String) =
        apiRequest { api.getMasterCat(token, restroID) }

    suspend fun getProductSpecificationId(token: String, id: Int) =
        apiRequest { api.getProductSpecificationId(token, id) }

    suspend fun getProductFilterData(
        token: String, restID: String, brand: String?, minPrice: String?, maxPrice: String?,
        color: String?, size: String?
    ) =
        apiRequest {
            log(
                "getProductFilterData",
                " $restID = $token = $brand = $minPrice = $maxPrice = $color =$size"
            )
            api.getProductFilterData(
                token, restID, brand, minPrice, maxPrice, color, size, "ecommerce"
            )
        }

    suspend fun getProductSize(token: String, productID: Int, type: String) =
        apiRequest { api.getProductSize(token, productID, type) }

    suspend fun getProductColor(token: String, productID: Int, type: String, sizeID: Int) =
        apiRequest { api.getProductColor(token, productID, type, sizeID) }

    suspend fun addToCart(token: String, data: JsonObject) =
        apiRequest { api.addToCart(token, data) }

    suspend fun login(data: LoginDataModel) = apiRequest { api.login(data) }

    suspend fun getUserID(token: String, user: String) =
        apiRequest { api.getUserID(token, user) }

    suspend fun userLogin(user: String, pass: String, restID: String) =
        apiRequest { api.mainLogin(LoginDataModel(user, pass, restID)) }

    suspend fun insertUser(token: String, userId: String, restroID: String) =
        apiRequest { api.insertUser(token, UserDataModel(userId, restroID)) }

    suspend fun resetUserName(token: String, email: String) =
        apiRequest { api.resetUserName(token, ResetUserName(email, "", "")) }

    suspend fun resetPassword(token: String, userName: String) =
        apiRequest { api.resetPassword(token, ResetUserName("", "", userName)) }

    suspend fun registerUser(token: String, userData: UserRegisterRequest) =
        apiRequest { api.userRegistration(token, userData) }

    suspend fun createUser(token: String, userData: UserRegisterRequest) =
        apiRequest { api.createUser(token, userData) }

    suspend fun getOTP(mobile: String) =
        apiRequest { api.getOTP(MobileVerificationRequest(mobile, "")) }

    suspend fun verifyOTP(mobile: String, otp: String) =
        apiRequest { api.verifyOtp(MobileVerificationRequest(mobile, otp)) }

    suspend fun checkCart(token: String, restID: String, custId: String): CheckCartResponse? {
        return apiRequest { api.checkCart(token, restID, custId) }
    }

    suspend fun getCart(token: String, cartID: String, restID: String): CartListResponse? {
        return apiRequest { api.getCartList(token, cartID, restID) }
    }

    suspend fun createCart(token: String, data: CreateCartRequest) = apiRequest {
        api.createCart(token, data)
    }

    suspend fun getCartListData(token: String, cartID: String, restroID: String) =
        apiRequest {
            api.getCartList(token, cartID, restroID)
        }

    suspend fun updateCart(token: String, cartItemID: String, data: JsonObject) =
        apiRequest {
            api.updateCart(token, cartItemID, data)
        }

    suspend fun deleteCartItem(token: String, cartItemID: String) =
        apiRequest {
            api.deleteCartItem(token, cartItemID)
        }

    suspend fun checkDeliverStatus(token: String, pincode: String) =
        apiRequest {
            api.checkDeliverStatus(token, pincode)
        }

    suspend fun getOrderListData(token: String, restID: String, userID: String) =
        apiRequest {
            api.getOrderList(token, restID, userID, "ecommerce")
        }

    suspend fun getProfile(token: String, userID: String): ProfileResponse? {
        return apiRequest { api.getProfile(token, userID) }
    }

    suspend fun updateProfile(
        token: String, userID: String, data: ProfileDataRequest
    ): UpdateProfileResponse? {
        return apiRequest { api.updateProfile(token, userID, data) }
    }

    suspend fun updateCustomerProfile(
        token: String, userID: String, data: ProfileDataRequest
    ): UpdateProfileResponse? {
        return apiRequest { api.updateCustomerProfile(token, userID, data) }
    }

    suspend fun changePassword(token: String, data: ChangePassword): ChangePasswordResponse? {
        return apiRequest { api.changePassword(token, data) }
    }

    suspend fun getSubCategoryData(token: String, masterCatID: Int) =
        apiRequest { api.getSubCategoryData(token, masterCatID) }

    suspend fun getAboutUS(token: String, restID: String) =
        apiRequest { api.getAboutUs(token, restID) }

    suspend fun cancelOrder(token: String, data: JsonObject) =
        apiRequest { api.cancelOrder(token, data) }

}