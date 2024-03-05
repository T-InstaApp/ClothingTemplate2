package com.instaapp.clothingtemplate1.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.dataModel.*
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.repositories.CarRepository
import com.instaapp.clothingtemplate1.utils.ApiException
import com.instaapp.clothingtemplate1.utils.CommonKey
import com.instaapp.clothingtemplate1.utils.Coroutines
import com.instaapp.clothingtemplate1.utils.NoInternetException
import kotlinx.coroutines.Job

class OrderViewModel(private val repository: CarRepository) : ViewModel() {

    var networkCallListener: NetworkCallListener? = null
    private lateinit var job: Job

    fun getShippingAddressData(token: String, userID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getShippingAddress(token, userID)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "getShippingAddressData")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getBillingAddressData(token: String, userID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getBillingAddress(token, userID)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "getBillingAddressData")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateShippingAddress(token: String, id: String, data: AddBillingShippingAddressRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateShippingAddress(token, id, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "updateShippingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun addShippingAddress(token: String, data: AddBillingShippingAddressRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addShippingAddress(token, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "addShippingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun addBillingAddress(token: String, data: AddBillingShippingAddressRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addBillingAddress(token, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "addBillingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateBillingAddress(token: String, id: String, data: AddBillingShippingAddressRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateBillingAddress(token, id, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "updateBillingAddress")
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


    fun getUserProfile(token: String, custID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getUserProfile(token, custID)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "getUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getFees(token: String, data: AddFreeRequest, operationType: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getFees(token, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, operationType)
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1] + "," + e.message!!.split("@")[2]
                )
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateOrder(token: String, data: UpdateOrderRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateOrderDetails(token, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "updateOrder")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun placeOrder(token: String, data: JsonObject) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.placeOrder(token, data)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "placeOrder")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                )
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun orderTracking(token: String, data: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.trackOrder(token, data)
                response.let {
                    networkCallListener?.onSuccess(response, "orderTracking")
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