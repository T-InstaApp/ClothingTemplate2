package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.CartItem
import com.instaapp.clothingtemplate1.databinding.CartListItemBinding
import com.instaapp.clothingtemplate1.listener.RecyclerViewCartClickListener
import com.instaapp.clothingtemplate1.utils.PreferenceKey
import com.instaapp.clothingtemplate1.utils.PreferenceProvider
import com.instaapp.clothingtemplate1.utils.log
import com.instaapp.clothingtemplate1.utils.roundOffDecimal

class CartDataAdapter(
    var data: ArrayList<CartItem>,
    private val listener: RecyclerViewCartClickListener,
    val context: Context
) :
    RecyclerView.Adapter<CartDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.cartListItemBinding.menuData = data[position]

        try {
            holder.cartListItemBinding.txtColor.text = data[position].color!!.color.split(",")[0]
        }catch (e:java.lang.Exception){
            log("onBindViewHolder "," ${data[position].color!!.color}")
        }


        holder.cartListItemBinding.imgAdd.setOnClickListener {
            val qty: Int = holder.cartListItemBinding.txtQty.text.toString().toInt()
            if (qty > 0) {
                val count = (qty + 1)
                val b: Double = data[position].price!!.toDouble()
                val price: Double = roundOffDecimal((count * (b / qty)))
                holder.cartListItemBinding.txtPrice.text =
                    PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)!! + " " + price.toString()
                holder.cartListItemBinding.txtQty.text = count.toString()
                listener.addSubDeleteItem(
                    data[position],
                    data[position].product!!.product_id.toString(),
                    position,
                    "",
                    price,
                    count.toString(),
                    "add",
                    data[position].cart_item_id.toString()
                )
            }
        }

        holder.cartListItemBinding.imgSub.setOnClickListener {
            val qty: Int = holder.cartListItemBinding.txtQty.text.toString().toInt()
            if (qty > 1) {
                val count = (qty - 1)
                val b: Double = data[position].price!!.toDouble()
                val price: Double = roundOffDecimal((count * (b / qty)))

                holder.cartListItemBinding.txtPrice.text =
                    PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)!! + " " + price.toString()
                holder.cartListItemBinding.txtQty.text = count.toString()

                listener.addSubDeleteItem(
                    data[position],
                    data[position].product!!.product_id.toString(),
                    position,
                    "",
                    price,
                    count.toString(),
                    "sub",
                    data[position].cart_item_id.toString()
                )
            }
        }
        holder.cartListItemBinding.imgDelete.setOnClickListener {
            listener.addSubDeleteItem(
                data[position],
                "",
                position,
                "",
                0.0,
                "0",
                "Delete",
                data[position].cart_item_id.toString()
            )
        }


    }

    inner class OrderViewHolder(
        val cartListItemBinding: CartListItemBinding
    ) : RecyclerView.ViewHolder(cartListItemBinding.root)
}