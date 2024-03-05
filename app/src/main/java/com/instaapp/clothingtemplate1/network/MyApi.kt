package com.instaapp.clothingtemplate1.network


import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.utils.StaticValue
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {
    // Not in Used
    @Headers("Content-Type: application/json")
    @GET("catalog/?status=ACTIVE")
    suspend fun getMenuList(
        @Header("Authorization") authorization: String,
        @Query("restaurant_id") rid: String,
        // @Query("category_id") cid: Int,
        @Query("page") page: Int
    ): MenuResponse


    @Headers("Content-Type: application/json")
    @GET("productdetails/{product}/")
    suspend fun getProductSpecificationId(
        @Header("Authorization") authorization: String?,
        @Path("product") id: Int?
    ): Response<CarSpecificationDataModel>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/login/v1/")
    suspend fun login(
        @Body login: LoginDataModel
    ): Response<MainLoginDataModel>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/login/v1/")
    suspend fun mainLogin(
        @Body login: LoginDataModel
    ): Response<MainLoginDataModel>


    @Headers("Content-Type: application/json")
    @GET("user/")
    suspend fun getUserID(
        @Header("Authorization") authorization: String?,
        @Query("username") username: String?
    ): Response<UserIDResponse>


    @POST("userrestaurant/")
    suspend fun insertUser(
        @Header("Authorization") authorization: String?,
        @Body login: UserDataModel?
    ): Response<UserRestaurant>

    @Headers("Content-Type: application/json")
    @POST("forgot/user/")
    suspend fun resetUserName(
        @Header("Authorization") authorization: String?,
        @Body login: ResetUserName
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/password/reset/v1/")
    suspend fun resetPassword(
        @Header("Authorization") authorization: String?,
        @Body login: ResetUserName
    ): Response<ResponseBody>

    @POST("guest/verify/")
    suspend fun verifyOtp(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<MobileVerifyResponse>

    @POST("send/code/")
    suspend fun getOTP(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("user/")
    suspend fun userRegistration(
        @Header("Authorization") authorization: String?,
        @Body login: UserRegisterRequest?
    ): Response<UserRegistrationResponse>

    @Headers("Content-Type: application/json")
    @POST("customer/")
    suspend fun createUser(
        @Header("Authorization") token: String?,
        @Body data: UserRegisterRequest
    ): Response<ResponseBody>

    @GET("customer/")
    suspend fun getProfile(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") customer_id: String?
    ): Response<ProfileResponse>

    @PUT("customer/{id}/")
    suspend fun updateCustomerProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest
    ): Response<UpdateProfileResponse>

    @POST("rest-auth/password/reset/confirm/v1/")
    suspend fun changePassword(
        @Header("Authorization") authorization: String?,
        @Body rid: ChangePassword?
    ): Response<ChangePasswordResponse>

    @PUT("user/{id}/")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest?
    ): Response<UpdateProfileResponse>


    @GET("product-details/")
    suspend fun getProductData(
        @Header("Authorization") authorization: String?,
        @Query("type") type: String?,
        @Query("restaurant_id") restID: String?,
        @Query("category_id") catID: String?,
        @Query("master_category_id") mscID: String,
        @Query("sub_category") subCategory: String
    ): Response<ArrayList<ProductDataMode>>


    @GET("restaurant-details/")
    suspend fun getAppDetails(
        @Query("restaurant_id") restroID: Int,
        @Query("type") type: String?
    ): Response<CompanyData>

    @Headers("Content-Type: application/json")
    @GET("search-key/")
    suspend fun getSearchKey(
        @Header("Authorization") authorization: String?,
        @Query("industry_type") industryType: String?,
        @Query("restaurant_id") restID: Int
    ): Response<ArrayList<OverviewsLabelData>>


    @Headers("Content-Type: application/json")
    @POST("shipping/")
    suspend fun addShippingAddress(
        @Header("Authorization") authorization: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @PUT("billing/{id}/")
    suspend fun updateBillingAddress(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @POST("billing/")
    suspend fun addBillingAddress(
        @Header("Authorization") authorization: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest?
    ): Response<AddressUpdateResponse>

    @Headers("Content-Type: application/json")
    @PUT("shipping/{id}/")
    suspend fun updateShippingAddress(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body addBillingAddress: AddBillingShippingAddressRequest
    ): Response<AddressUpdateResponse>


    @GET("product-filter/")
    suspend fun getProductFilterData(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") restID: String?,
        @Query("brand") brand: String?,
        @Query("min_price") minPrice: String?,
        @Query("max_price") maxPrice: String?,
        @Query("color") color: String?,
        @Query("size") size: String?,
        @Query("type") type: String,
    ): Response<ArrayList<ProductDataMode>>

    @Headers("Content-Type: application/json")
    @GET("get-master-category/{id}/")
    suspend fun getMasterCat(
        @Header("Authorization") authorization: String?,
        @Path("id") type: String?,
    ): Response<ArrayList<MasterCategoryDataModel>>


    @Headers("Content-Type: application/json")
    @GET("shipping/")
    suspend fun getShippingAddress(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") rid: String?
    ): Response<AddressListResponse>

    @Headers("Content-Type: application/json")
    @GET("billing/")
    suspend fun getBillingAddress(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") rid: String?
    ): Response<AddressListResponse>

    @Headers("Content-Type: application/json")
    @GET("get-product-size-color/")
    suspend fun getProductSize(
        @Header("Authorization") authorization: String?,
        @Query("product_id") pID: Int?,
        @Query("type") type: String,
    ): Response<ProductSizeDataModel>

    @Headers("Content-Type: application/json")
    @GET("get-product-size-color/")
    suspend fun getProductColor(
        @Header("Authorization") authorization: String?,
        @Query("product_id") pID: Int?,
        @Query("type") type: String,
        @Query("size_id") sizeID: Int?
    ): Response<ProductWithColors>

    @POST("cart-item-post/")
    suspend fun addToCart(
        @Header("Authorization") authorization: String?,
        @Body login: JsonObject?
    ): Response<AddToCartResponse>

    @Headers("Content-Type: application/json")
    @GET("cart/")
    suspend fun checkCart(
        @Header("Authorization") authorization: String?,
        @Query("restaurant") restaurant_id: String?,
        @Query("customer_id") customer_id: String?
    ): Response<CheckCartResponse>

    @Headers("Content-Type: application/json")
    @GET("cart-items-x/")
    suspend fun getCartList(
        @Header("Authorization") authorization: String?,
        @Query("cart_id") rid: String?,
        @Query("restaurant_id") retroid: String?
    ): Response<CartListResponse>

    @Headers("Content-Type: application/json")
    @POST("cart/")
    suspend fun createCart(
        @Header("Authorization") authorization: String?,
        @Body createCart: CreateCartRequest
    ): Response<CreateCartResponse>

    @PUT("cart-item-post/{id}/")
    suspend fun updateCart(
        @Header("Authorization") authorization: String?,
        @Path("id") cartItemID: String?,
        @Body cartUpdate: JsonObject?
    ): Response<UpdateCartResponse>

    @Headers("Content-Type: application/json")
    @DELETE("cart-item-x/{id}/")
    suspend fun deleteCartItem(
        @Header("Authorization") authorization: String?,
        @Path("id") rid: String?
    ): Response<HourResponse>


    @Headers("Content-Type: application/json")
    @GET("pincode_availability/")
    suspend fun checkDeliverStatus(
        @Header("Authorization") authorization: String?,
        @Query("filter_codes") pincode: String?
    ): Response<DeliveryAPIResponse>

    @POST("v2/fee-x/")
    suspend fun getFees(
        @Header("Authorization") authorization: String?,
        @Body query: AddFreeRequest?
    ): Response<FreeResponse>

    @POST("order-detail-x/")
    suspend fun updateOrderDetail(
        @Header("Authorization") authorization: String?,
        @Body query: UpdateOrderRequest
    ): Response<OrderDetailResponse>


    @POST("v2/payment/")
    suspend fun placeOrder(
        @Header("Authorization") authorization: String?,
        @Body cartUpdate: JsonObject
    ): Response<ResponseBody>

    @GET("customer-payment/")
    suspend fun getOrderList(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") restaurant_id: String?,
        @Query("customer_id") customer_id: String?,
        @Query("type") type: String?
    ): Response<ArrayList<OrderListResponse>>

    @GET("get-product-by-master-category/")
    suspend fun getSubCategoryData(
        @Header("Authorization") authorization: String?,
        @Query("master_category_id") id: Int?,
    ): Response<ArrayList<SubCategoryDataModel>>

    @GET("restaurant/{id}/")
    suspend fun getAboutUs(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?
    ): Response<AboutUs>

    @Headers("Content-Type: application/json")
    @GET("order_tracking/")
    suspend fun trackOrder(
        @Header("Authorization") authorization: String?,
        @Query("waybill") id: String,
    ): Response<ShipmentData>

    @POST("cancel_order/")
    suspend fun cancelOrder(
        @Header("Authorization") authorization: String?,
        @Body data: JsonObject,
    ): Response<ResponseBody>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): MyApi {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(StaticValue.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }


}