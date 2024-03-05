package com.instaapp.clothingtemplate1.viewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.repositories.HomeRepository
import com.instaapp.clothingtemplate1.utils.*
import kotlinx.coroutines.Job

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    var networkCallListener: NetworkCallListener? = null
    private lateinit var job: Job

    private val _categoryData = MutableLiveData<List<CategoryResponse>>()
    val categoryData: LiveData<List<CategoryResponse>>
        get() = _categoryData

    private val _masterCategoryData = MutableLiveData<List<MasterCategoryDataModel>>()
    val masterCategoryData: LiveData<List<MasterCategoryDataModel>>
        get() = _masterCategoryData

    fun getProductData(
        token: String, restID: String, type: String, catID: String,
        masterCAT: String, subCategory: String
    ) {
        log("HomeFragment", " test $token $restID $type")
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response =
                    repository.getProduct(token.trim(), type, restID, catID, masterCAT, subCategory)
                response.let {
                    networkCallListener?.onSuccess(it, type)
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun getAppDetails(type: String, restroID: Int) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getAppDetails(restroID, type)
                response.let {
                    networkCallListener?.onSuccess(it, "getAppDetails")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getSearchKey(token: String, type: String, restID: Int) {
        log("getSearchKey", " $restID")
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getSearchKey(token, type, restID)
                response.let {
                    networkCallListener?.onSuccess(it, "getSearchKey")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun getProductFilterData(token: String, restID: String, filterData: HashMap<String, String>) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                var maxPrice = ""
                var minPrice = ""
                if (filterData["Price"] != null) {
                    minPrice = filterData["Price"]!!.split(" - ")[0]
                    maxPrice = filterData["Price"]!!.split(" - ")[1]
                }
                val response = repository.getProductFilterData(
                    token.trim(),
                    restID,
                    filterData["Brand"],
                    minPrice,
                    maxPrice,
                    filterData["Color"],
                    filterData["Size"],
                )
                response.let {
                    networkCallListener?.onSuccess(it, "getProductFilterData")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun getMasterCat(token: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getMasterCat(token.trim(), restID)
                response.let {
                    _masterCategoryData.value = it
                    networkCallListener?.onSuccess(it, "getMasterCat")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getProductSpecificationId(token: String, id: Int) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductSpecificationId(token, id)
                response.let {
                    networkCallListener?.onSuccess(response, "getProductSpecificationId")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getProductSize(token: String, id: Int, type: String) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductSize(token, id, type)
                response.let {
                    networkCallListener?.onSuccess(response, "getProductSize")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getProductColor(token: String, id: Int, type: String, sizeID: Int) {
        Log.d("TAG", "getProductColor: Product $id, Type=$type, Sise=$sizeID")
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductColor(token, id, type, sizeID)
                response.let {
                    networkCallListener?.onSuccess(response, "getProductColor")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun addToCart(token: String, data: JsonObject) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addToCart(token, data)
                mainLoginResponse.let {
                    log("addToCart ", "  DetailScreen2 " + it.toString())
                    networkCallListener?.onSuccess(it, "addToCart")
                    return@main
                }
            } catch (e: ApiException) {
                log(
                    "DetailScreen2 ",
                    " ex ${e.message!!.split("@")[0]}  99 ${e.message!!.split("@")[1]}"
                )
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun login(data: LoginDataModel) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.login(data)
                response.let {
                    networkCallListener?.onSuccess(response, "login")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getUserID(token: String, userName: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getUserID(token, userName)
                response.let {
                    networkCallListener?.onSuccess(response, "getUserID")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkLogin(restID: String, userName: String, password: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse =
                    repository.userLogin(userName, password, restID)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "Login")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun insertUser(token: String, userId: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.insertUser(token, userId, restID)
                response.let {
                    networkCallListener?.onSuccess(response, "insertUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun registerUser(token: String, data: UserRegisterRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse = repository.registerUser(token, data)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "registerUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun createUser(token: String, data: UserRegisterRequest) {
        Coroutines.main {
            try {
                val authResponse = repository.createUser(token, data)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "createUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetUserName(token: String, email: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetUserName(token, email)
                response.let {
                    networkCallListener?.onSuccess(response, "resetUserName")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetPassword(token: String, username: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetPassword(token, username)
                response.let {
                    networkCallListener?.onSuccess(response, "resetPassword")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun generateOtp(mobile: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getOTP(mobile)
                response.let {
                    networkCallListener?.onSuccess(response, "generateOtp")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun verifyOTP(otp: String, code: String, mobile: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.verifyOTP(code + mobile, otp)
                response.let {
                    networkCallListener?.onSuccess(response, "verifyOTP")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkCart(token: String, restID: String, custID: String) {
        log("ProductDetailsActivity checkCart ", " Called 222 ")
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkCart(token, restID, custID)
                mainLoginResponse.let {
                    if (it!!.count!! > 0) {
                        networkCallListener?.onSuccess(
                            it.results?.get(0)?.id.toString(),
                            "checkCart"
                        )
                    } else
                        networkCallListener?.onSuccess(
                            0,
                            "checkCart"
                        )
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkCartAvailable(token: String, restID: String, custID: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkCart(token, restID, custID)
                mainLoginResponse.let {
                    //   if (it!!.count!! > 0) {
                    networkCallListener?.onSuccess(it, "checkCart")
                    //  }
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getCartCount(token: String, cartID: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getCart(token, cartID, restID)
                mainLoginResponse.let {
                    if (it!!.count!! > 0) {
                        networkCallListener?.onSuccess(it.count.toString(), "getCartCount")
                    } else {
                        networkCallListener?.onSuccess("", "getCartCount")
                    }
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun createCart(token: String, data: CreateCartRequest) {
        log("ProductDetailsActivity createCart ", "CreateCart Called 222")
        Coroutines.main {
            try {
                val mainLoginResponse = repository.createCart(token, data)
                mainLoginResponse.let {
                    log("ProductDetailsActivity createCart ", "CreateCart Called 222")
                    networkCallListener?.onSuccess(it, "createCart")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    private val _cartListData = MutableLiveData<CartListResponse>()
    val cartListData: LiveData<CartListResponse>
        get() = _cartListData

    fun getCartListData(token: String, cartID: String, restID: String) {
        job = Coroutines.ioThenMain({ repository.getCartListData(token, cartID, restID) },
            { _cartListData.value = it })
    }

    fun updateCart(token: String, cartItemID: String, data: JsonObject) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateCart(token, cartItemID, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "updateCart")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun deleteCartItem(token: String, cartItemID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.deleteCartItem(token, cartItemID)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "deleteCartItem")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkDeliverStatus(token: String, pincode: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkDeliverStatus(token, pincode)
                mainLoginResponse.let {
                    //   if (it!!.count!! > 0) {
                    networkCallListener?.onSuccess(it, "checkDeliverStatus")
                    //  }
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    private val _orderListData = MutableLiveData<ArrayList<OrderListResponse>>()
    val orderListData: LiveData<ArrayList<OrderListResponse>>
        get() = _orderListData

    fun getOrderListData(token: String, restID: String, userID: String) {
        job = Coroutines.ioThenMain({ repository.getOrderListData(token, restID, userID) },
            { _orderListData.value = it })
    }

    fun getUserProfile(token: String, userId: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProfile(token, userId)
                response.let {
                    networkCallListener?.onSuccess(response, "getUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun onUpdateUserProfile(token: String, userId: String, data: ProfileDataRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.updateProfile(token, userId, data)
                response.let {
                    networkCallListener?.onSuccess(response, "onUpdateUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateCustomerProfile(token: String, userId: String, data: ProfileDataRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.updateCustomerProfile(token, userId, data)
                response.let {
                    networkCallListener?.onSuccess(response, "updateCustomerProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun changePassword(token: String, data: ChangePassword) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.changePassword(
                    token, data
                )
                response.let {
                    networkCallListener?.onSuccess(response, "changePassword")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getSubCategoryData(token: String, masterCatID: Int) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getSubCategoryData(token, masterCatID)
                response.let {
                    networkCallListener?.onSuccess(response, "getSubCategoryData")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getAboutUS(token: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getAboutUS(token, restID)
                response.let {
                    networkCallListener?.onSuccess(response, "getAboutUS")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }

    }

    fun cancelOrder(token: String, data: JsonObject) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.cancelOrder(token, data)
                response.let {
                    networkCallListener?.onSuccess(response, "cancelOrder")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

}