package com.instaapp.clothingtemplate1.ui.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.OrderListResponse
import com.instaapp.clothingtemplate1.databinding.FragmentOrderBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.OrderDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class OrderFragment : Fragment(), NetworkCallListener, KodeinAware, RecyclerViewClickListener {
    override val kodein by kodein()
    private lateinit var _binding: FragmentOrderBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    lateinit var progressDialogBinding: ProgressDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = _binding.progressDialog

        loadOrderData()

        return _binding.root
    }

    private fun loadOrderData() {
        viewModel.getOrderListData(
            "Token ${PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!}",
            StaticValue.REST_ID,
            PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID).toString()
        )
        viewModel.orderListData.observe(viewLifecycleOwner) { orderData ->
            _binding.recyclerCartItem.also {
                it.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = OrderDataAdapter(orderData, this, requireContext())
                progressDialogBinding.progressLayout.visibility = View.GONE
            }
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
        if (type == "cancelOrder") {
            requireContext().toast("Your order has been canceled successfully, and the refund process has been initiated.")
            loadOrderData()
        }
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        if (status == "Cancel") {
            val data: OrderListResponse = dataG as OrderListResponse
            deleteMenuWarningDialog(data.payment_id.toString())
        }
    }

    private fun deleteMenuWarningDialog(paymentID: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_input_field)
        dialog.setCancelable(false)

        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtInputHeading = dialog.findViewById<TextInputLayout>(R.id.txtInputHeading)
        val etInputData = dialog.findViewById<EditText>(R.id.etInputData)
        val button = dialog.findViewById<AppCompatButton>(R.id.btnSubmit)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)

        txtInputHeading.hint = "Enter cancellation reason"
        txtHeading.text = getString(R.string.cancel_order_warning_msg)

        Glide.with(requireContext())
            .load(PreferenceProvider(requireContext()).getStringValue(PreferenceKey.IMAGE_LOGO))
            .into(imgAppLogo)

        log(
            "deleteMenuWarningDialog ",
            "${PreferenceProvider(requireContext()).getStringValue(PreferenceKey.IMAGE_LOGO)}"
        )

        imgAppLogo.visibility = View.GONE
        button.text = "Cancel"

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = (getScreenWidthSize(requireActivity()) * 0.9).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        imgCancel.visibility = View.VISIBLE
        imgCancel.setOnClickListener {
            dialog.dismiss()
        }
        button.setOnClickListener {
            if (etInputData.text.toString().trim()
                    .isNotEmpty() || etInputData.text.trim().length > 2
            ) {
                val data = JsonObject()
                data.addProperty("is_delivery_cancel", "False")
                data.addProperty("waybill_id", "")
                data.addProperty("payment_id", paymentID)
                data.addProperty("reason_for_cancellation", etInputData.text.toString().trim())
                viewModel.cancelOrder(
                    "Token ${PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)}",
                    data
                )
                dialog.dismiss()
            } else
                requireContext().toast("Please enter cancellation reason")
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

}