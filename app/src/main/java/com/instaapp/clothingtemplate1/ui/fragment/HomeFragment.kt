package com.instaapp.clothingtemplate1.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.databinding.FragmentHomeBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.activity.MenCategoryActivity
import com.instaapp.clothingtemplate1.ui.activity.ProductDetailsActivity
import com.instaapp.clothingtemplate1.ui.activity.ShowAllProductActivity
import com.instaapp.clothingtemplate1.ui.adapter.HomeBestSellerDataAdapter
import com.instaapp.clothingtemplate1.ui.adapter.HomeProductDataAdapter
import com.instaapp.clothingtemplate1.ui.adapter.MasterCategoryAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), NetworkCallListener, KodeinAware, RecyclerViewClickListener {
    override val kodein by kodein()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private val TAG = "HomeFragment"
    var badge: BadgeDrawable? = null
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = _binding!!.progressDialog

        getMasterCatData()
        loadTopCarData()
        loadHotDeals()
        if (PreferenceProvider(requireContext()).getBooleanValue(PreferenceKey.LOGIN_STATUS))
            checkCart()

        _binding!!.txtViewAllFeatured.setOnClickListener {
            val intent = Intent(requireContext(), ShowAllProductActivity::class.java)
            intent.putExtra("catID", 0)
            intent.putExtra("type", "Top")
            intent.putExtra("name", "Featured Products")
            startActivity(intent)
        }

        _binding!!.txtViewAllBestSeller.setOnClickListener {
            val intent = Intent(requireContext(), ShowAllProductActivity::class.java)
            intent.putExtra("catID", 0)
            intent.putExtra("type", "new_arrievals")
            intent.putExtra("name", "Best Sellers")
            startActivity(intent)
        }




        return _binding!!.root
    }

    private fun checkCart() {
        viewModel.checkCart(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID,
            PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun getCartCount(cartID: String) {
        PreferenceProvider(requireContext()).setIntValue(cartID.toInt(), PreferenceKey.CART_ID)
        viewModel.getCartCount(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN),
            cartID,
            StaticValue.REST_ID,
        )
    }

    private fun getMasterCatData() {
        viewModel.getMasterCat(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID
        )
        viewModel.masterCategoryData.observe(viewLifecycleOwner) { catData ->
            _binding!!.recyclerMasterCategory.also {

                log(TAG, " Data ${catData.size} & ${StaticValue.REST_ID}")
                it.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                it.setHasFixedSize(true)
                it.adapter = MasterCategoryAdapter(catData, requireContext(), this)
            }
        }
    }

    private fun loadTopCarData() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "Top", "", "", ""
        )
    }

    private fun loadHotDeals() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "new_arrievals", "", "", ""
        )
    }

    override fun onStarted() {
        progressDialogBinding.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        showAlert(
            requireActivity(),
            getString(R.string.alert),
            message
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        log(TAG, " Success   $type  == $dataG ")
        when (type) {
            "Top" -> {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                val layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                _binding!!.recyclerFeaturedData.layoutManager = layoutManager
                _binding!!.recyclerFeaturedData.setHasFixedSize(true)
                _binding!!.recyclerFeaturedData.adapter =
                    HomeProductDataAdapter(data, this, requireContext())
            }
            "new_arrievals" -> {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                val layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                _binding!!.recyclerBestSeller.layoutManager = layoutManager
                _binding!!.recyclerBestSeller.setHasFixedSize(true)
                _binding!!.recyclerBestSeller.adapter =
                    HomeBestSellerDataAdapter(data, this, requireContext())
            }
            "checkCart" -> {
                PreferenceProvider(requireContext()).setIntValue(
                    dataG.toString().toInt(),
                    PreferenceKey.CART_ID
                )
                getCartCount(dataG.toString())
            }
            "getCartCount" -> {
                try {
                    initView(dataG.toString().toInt())
                } catch (ignore: Exception) {
                }
            }
        }
    }

    private fun initView(count: Int) {
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.navigation)
        val menuItemId = bottomNavigationView.menu.getItem(1).itemId
        badge = bottomNavigationView.getOrCreateBadge(menuItemId)
        if (PreferenceProvider(requireContext()).getStringValue(PreferenceKey.USER_PROFILE) !== ("0")
        ) {
            PreferenceProvider(requireContext()).setIntValue(count, PreferenceKey.CART_COUNT)
            badge?.number = count
        }
    }

    override fun onResume() {
        if (PreferenceProvider(requireContext()).getBooleanValue(PreferenceKey.LOGIN_STATUS) && PreferenceProvider(
                requireContext()
            ).getIntValue(PreferenceKey.CART_ID) != 0
        )
            getCartCount(
                PreferenceProvider(
                    requireContext()
                ).getIntValue(PreferenceKey.CART_ID).toString()
            )
        super.onResume()
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        if (status == "Category") {
            val intent = Intent(requireContext(), MenCategoryActivity::class.java)
            intent.putExtra("masterCatID", dataG.toString().split("@")[0].toInt())
            intent.putExtra("masterCatName", dataG.toString().split("@")[1])
            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
            val data = dataG.toString().split("@")
            intent.putExtra("product_id", data[0].toInt())
            intent.putExtra("productName", data[1])
            intent.putExtra("productPrice", data[2])
            intent.putExtra("productFinalPrice", data[3])
            intent.putExtra("brandName", data[4])
            intent.putExtra("cancellation_Policy", data[5])
            startActivity(intent)
        }
    }
}