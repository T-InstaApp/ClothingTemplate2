package com.instaapp.clothingtemplate1.ui.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.CartDataModel
import com.instaapp.clothingtemplate1.dataModel.CartItem
import com.instaapp.clothingtemplate1.databinding.FragmentCartBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewCartClickListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.activity.CheckOutActivity
import com.instaapp.clothingtemplate1.ui.adapter.CartDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CartFragment : Fragment(), NetworkCallListener, KodeinAware, RecyclerViewCartClickListener {
    override val kodein by kodein()
    private var _binding: FragmentCartBinding? = null
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private val TAG = "CartFragment"
    private var cartTotal: Double = 0.0
    private var cartCount: Int = 0
    var badge: BadgeDrawable? = null
    lateinit var progressDialogBinding: ProgressDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = _binding!!.progressDialog


        loadCartData()

        _binding!!.txtCheckOut.setOnClickListener {
            if (cartTotal > 1) {
                PreferenceProvider(requireContext()).setStringValue(
                    cartTotal.toString(), PreferenceKey.TOTAL_PRICE
                )

                startActivity(Intent(requireContext(), CheckOutActivity::class.java))
            } else
                requireContext().toast("Nothing in your cart.")
        }

        return _binding!!.root
    }


    @SuppressLint("SetTextI18n")
    private fun loadCartData() {
        viewModel.getCartListData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(requireContext()).getIntValue(PreferenceKey.CART_ID).toString(),
            StaticValue.REST_ID,
        )

        viewModel.cartListData.observe(viewLifecycleOwner) { cartData ->
            _binding!!.recyclerCartItem.also {
                it.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = CartDataAdapter(cartData.results!!, this, requireContext())
                cartCount = cartData.count!!
                cartTotal = cartData.total_cost!!.toDouble()
                _binding!!.txtFinalPrice.text = PreferenceProvider(
                    requireContext()
                ).getStringValue(PreferenceKey.CURRENCY_SYMBOL)!! + " " + cartData.total_cost
                updateCartCount(cartData.count)
                progressDialogBinding.progressLayout.visibility = View.GONE
                calculateProductWeight(cartData.results)
            }
        }
    }

    private fun calculateProductWeight(results: ArrayList<CartItem>) {
        var weightTotal = 0
        for (d in results) {
            if (d.product!!.weight != null) {
                weightTotal += d.product.weight!!.toInt()
            }
        }
        log("TotalWeight ", " = $weightTotal")
        PreferenceProvider(requireContext()).setStringValue(
            weightTotal.toString(), PreferenceKey.TOTAL_WEIGHT
        )
    }

    private fun updateCartCount(count: Int) {
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
        loadCartData()
    }

    override fun addSubDeleteItem(
        cartData: CartItem, product_id: String, position: Int,
        sequence: String, price: Double, count: String, check: String, cartItemId: String
    ) {
        if (check != "Delete") {
            val jsonArray1 = JsonArray()
            val jsonObject = JsonObject()
            jsonObject.addProperty("price", price)
            jsonObject.addProperty("extra", "")
            jsonObject.addProperty("quantity", count.toInt())
            jsonObject.addProperty("cart_id", cartItemId.toInt())
            jsonObject.addProperty("product_id", product_id.toInt())
            jsonObject.addProperty("size_id", "")
            jsonObject.addProperty("color_id", cartData.color!!.color_id)
            jsonObject.add("addon_content_list", jsonArray1)
            log(" UpdateOrder "," $jsonObject")
            viewModel.updateCart(
                "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN),
                cartItemId, jsonObject
            )
        } else {
            deleteWarning(cartItemId)
        }
    }

    private fun deleteWarning(cartItemId: String) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtMsg = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        button.text = getString(R.string.delete)
        txtHeading.text = getString(R.string.warning2)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        txtMsg.text = resources.getString(R.string.dialog_delete_item_txt)

        (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener { view: View? ->
            viewModel.deleteCartItem(
                "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN),
                cartItemId
            )
            dialog.dismiss()
        }

        imgCancel.setOnClickListener { dialog.dismiss() }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }


}