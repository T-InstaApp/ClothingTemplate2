package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.OverviewsLabelData
import com.instaapp.clothingtemplate1.dataModel.ProductDataMode
import com.instaapp.clothingtemplate1.databinding.ActivityShowAllProductBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.HomeProductDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ShowAllProductActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {
    override val kodein by kodein()
    private lateinit var binding: ActivityShowAllProductBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()

    private var catID = 12
    private var type = ""
    private var headingName = ""
    lateinit var progressDialogBinding: ProgressDialogBinding
    lateinit var searchKeyData: ArrayList<OverviewsLabelData>
    lateinit var dialog: Dialog
    var isOpenFirst = false
    private lateinit var chipBorderDrawable: Drawable
    private val selectedValue = HashMap<String, String>()

    //Size,color,Brand,price,

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_show_all_product)
        binding = ActivityShowAllProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        progressDialogBinding = binding.progressDialog
        toolbar = binding.toolbar
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }

        catID = intent.getIntExtra("catID", 0)
        type = intent.getStringExtra("type")!!
        headingName = intent.getStringExtra("name")!!
        loadAllProductData(type)

        toolbar.txtTopHeading.text = headingName
        chipBorderDrawable = ContextCompat.getDrawable(this, R.drawable.border_line)!!
        toolbar.cartLayout.setOnClickListener {
            callMainActivity(this)
        }

        searchKeyData = arrayListOf()

        viewModel.getSearchKey(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", "Ecommerce", StaticValue.REST_ID.toInt()
        )

        binding.fabFilter.setOnClickListener { openFilterDialog() }
    }

    private fun loadAllProductData(type: String) {
        log("getProductFilterData ", " type == $type ")
        viewModel.getProductData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            type, catID.toString(), catID.toString(), headingName
        )
    }

    override fun onStarted() {
        progressDialogBinding.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressDialogBinding.progressLayout.visibility = View.GONE
        when (type) {
            "Top", "new_arrievals","SubCategory" -> {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                val layoutManager =
                    GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                binding.allProductRecyclerView.layoutManager = layoutManager
                binding.allProductRecyclerView.setHasFixedSize(true)
                binding.allProductRecyclerView.adapter =
                    HomeProductDataAdapter(data, this, applicationContext)
            }
            "getSearchKey" -> {
                searchKeyData = dataG as ArrayList<OverviewsLabelData>
                log("getSearchKey", " $dataG")
            }
            "getProductFilterData" -> {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                if (data.isNotEmpty()) {
                    val layoutManager =
                        GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                    binding.allProductRecyclerView.layoutManager = layoutManager
                    binding.allProductRecyclerView.setHasFixedSize(true)
                    binding.allProductRecyclerView.adapter =
                        HomeProductDataAdapter(data, this, applicationContext)
                } else
                    toast("We're sorry, but there are no results to show")
            }
        }

    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        val intent = Intent(applicationContext, ProductDetailsActivity::class.java)
        val data = dataG.toString().split("@")
        intent.putExtra("product_id", data[0].toInt())
        intent.putExtra("productName", data[1])
        intent.putExtra("productPrice", data[2])
        intent.putExtra("productFinalPrice", data[3])
        intent.putExtra("brandName", data[4])
        intent.putExtra("cancellation_Policy", data[5])
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun openFilterDialog() {
        if (!isOpenFirst) {
            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.filter_screen)
            dialog.setCancelable(true)
            isOpenFirst = true
            val mainChipGroup = dialog.findViewById<ChipGroup>(R.id.selectedShipGroup)
            val selectableChipGroup = dialog.findViewById<ChipGroup>(R.id.selectableChipGroup)
            val txtBrand = dialog.findViewById<TextView>(R.id.txtBrand)
            val txtSize = dialog.findViewById<TextView>(R.id.txtSize)
            val txtColor = dialog.findViewById<TextView>(R.id.txtColor)
            val txtPrice = dialog.findViewById<TextView>(R.id.txtPrice)
            val txtSelectedText = dialog.findViewById<TextView>(R.id.txtSelectedText)
            val rangeSlider = dialog.findViewById<RangeSlider>(R.id.rangeSlider)
            val priceCheckBox = dialog.findViewById<CheckBox>(R.id.priceCheckBox)
            val priceFilterLayout = dialog.findViewById<LinearLayout>(R.id.priceFilterLayout)
            val txtSelectedPrice = dialog.findViewById<TextView>(R.id.txtSelectedPrice)
            rangeSlider.isEnabled = false
            priceCheckBox.setOnCheckedChangeListener { _, isChecked ->
                rangeSlider.isEnabled = isChecked
                if (!isChecked) {
                    updateSelectedChip(mainChipGroup, true, filterPrice)
                    txtSelectedPrice.text = ""
                    filterPrice = ""
                    selectedValue["Price"] = ""
                }
            }

            rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val values = rangeSlider.values
                    filterPrice = "${values[0].toInt()} - ${values[1].toInt()}"
                    selectedValue["Price"] = filterPrice
                    txtSelectedPrice.text =
                        PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL) + filterPrice
                    updateSelectedChip(mainChipGroup, false, null)
                }
            })

            dialog.findViewById<AppCompatButton>(R.id.btnApply).setOnClickListener {
                appliedFilter()
                dialog.hide()
            }

            txtBrand.setOnClickListener {
                txtSelectedText.text = "Brand"
                priceFilterLayout.visibility = View.GONE
                selectableChipGroup.visibility = View.VISIBLE
                txtBrand.setBackgroundResource(R.drawable.border_line)
                txtSize.setBackgroundResource(R.drawable.border_line_unselect)
                txtColor.setBackgroundResource(R.drawable.border_line_unselect)
                txtPrice.setBackgroundResource(R.drawable.border_line_unselect)
                setChipValue("Brand", selectableChipGroup, mainChipGroup)
            }
            txtSize.setOnClickListener {
                txtSelectedText.text = "Size"
                priceFilterLayout.visibility = View.GONE
                selectableChipGroup.visibility = View.VISIBLE
                txtBrand.setBackgroundResource(R.drawable.border_line_unselect)
                txtSize.setBackgroundResource(R.drawable.border_line)
                txtColor.setBackgroundResource(R.drawable.border_line_unselect)
                txtPrice.setBackgroundResource(R.drawable.border_line_unselect)
                setChipValue("Size", selectableChipGroup, mainChipGroup)
            }
            txtColor.setOnClickListener {
                txtSelectedText.text = "Color"
                priceFilterLayout.visibility = View.GONE
                selectableChipGroup.visibility = View.VISIBLE
                txtBrand.setBackgroundResource(R.drawable.border_line_unselect)
                txtSize.setBackgroundResource(R.drawable.border_line_unselect)
                txtColor.setBackgroundResource(R.drawable.border_line)
                txtPrice.setBackgroundResource(R.drawable.border_line_unselect)
                setChipValue("Color", selectableChipGroup, mainChipGroup)
            }
            txtPrice.setOnClickListener {
                txtSelectedText.text = "Price"
                priceFilterLayout.visibility = View.VISIBLE
                selectableChipGroup.visibility = View.GONE
                txtBrand.setBackgroundResource(R.drawable.border_line_unselect)
                txtSize.setBackgroundResource(R.drawable.border_line_unselect)
                txtColor.setBackgroundResource(R.drawable.border_line_unselect)
                txtPrice.setBackgroundResource(R.drawable.border_line)
                setChipValue("Price", selectableChipGroup, mainChipGroup)
            }
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = (getScreenWidthSize(this) * 0.95).toInt()
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = lp
        } else {
            dialog.show()
        }
    }

    var isPriceFilter = false
    var filterPrice = ""
    private fun setChipValue(type: String, chipGroup: ChipGroup, mainChipGroup: ChipGroup) {
        chipGroup.removeAllViews()
        var mainData: ArrayList<String> = arrayListOf()
        if (type == "Brand")
            for (n in searchKeyData[0].brand)
                mainData.add(n.brand)
        else if (type == "Color")
            for (n in searchKeyData[0].Color)
                mainData.add(n.color)
        else if (type == "Size")
            for (n in searchKeyData[0].Size)
                mainData.add(n.size)
        for (data in mainData) {
            val chip =
                LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
            chip.background = chipBorderDrawable
            chip.text = data.split(",#")[0]
            chip.setOnClickListener {
                for (j in 0 until chipGroup.childCount) {
                    val otherChip = chipGroup.getChildAt(j) as Chip
                    otherChip.isChecked = otherChip == chip
                }
                if (selectedValue[type] == chip.text.toString()) {
                    selectedValue[type] = ""
                    chip.isChecked = false
                    updateSelectedChip(mainChipGroup, true, chip.text.toString())
                } else {
                    selectedValue[type] = chip.text.toString()
                    updateSelectedChip(mainChipGroup, false, null)
                }
            }
            chipGroup.addView(chip)
        }

    }

    @SuppressLint("InflateParams")
    private fun updateSelectedChip(chipGroup: ChipGroup, isRemove: Boolean, text: String?) {
        if (!isRemove) {
            chipGroup.removeAllViews()
            for (map in selectedValue) {
                if (map.value.isNotEmpty()) {
                    val chip =
                        LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                    chip.background = chipBorderDrawable
                    chip.text = map.value
                    chip.isClickable = false
                    chipGroup.addView(chip)
                }
            }
        } else {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (text.equals(chip.text.toString(), ignoreCase = true)) {
                    chipGroup.removeViewAt(i)
                    return
                }
            }
        }
    }

    private fun appliedFilter() {
        log("appliedFilter ", " $selectedValue ==${selectedValue.size} == ${checkFilterValue()}")
        if (selectedValue.size > 0 && checkFilterValue())
            viewModel.getProductFilterData(
                "Token ${
                    PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )
                }", StaticValue.REST_ID, selectedValue
            )
        else
            loadAllProductData(type)
    }


    private fun checkFilterValue(): Boolean {
        if (selectedValue["Brand"] != null && selectedValue["Brand"]!!.isNotEmpty())
            return true
        else if (selectedValue["Color"] != null && selectedValue["Color"]!!.isNotEmpty())
            return true
        else if (selectedValue["Size"] != null && selectedValue["Size"]!!.isNotEmpty())
            return true
        else if (selectedValue["Price"] != null && selectedValue["Price"]!!.isNotEmpty())
            return true
        return false
    }

}