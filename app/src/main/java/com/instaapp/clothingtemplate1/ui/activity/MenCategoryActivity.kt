package com.instaapp.clothingtemplate1.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.dataModel.SubCategoryDataModel
import com.instaapp.clothingtemplate1.databinding.ActivityMenCategoryBinding
import com.instaapp.clothingtemplate1.databinding.ProgressDialogBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.listener.NetworkCallListener
import com.instaapp.clothingtemplate1.listener.RecyclerViewClickListener
import com.instaapp.clothingtemplate1.provider.HomeViewModelFactory
import com.instaapp.clothingtemplate1.ui.adapter.CategoryDataAdapter
import com.instaapp.clothingtemplate1.utils.*
import com.instaapp.clothingtemplate1.viewModel.HomeViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MenCategoryActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {
    override val kodein by kodein()
    private lateinit var binding: ActivityMenCategoryBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private var masterCatID = 0
    private var masterCatName = ""
    lateinit var progressDialogBinding: ProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_men_category)
        binding = ActivityMenCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.networkCallListener = this
        toolbar = binding.toolbar
        progressDialogBinding = binding.progressDialog
        toolbar.cartLayout.visibility = View.VISIBLE
        toolbar.txtCartItemCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()

        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }

        masterCatID = intent.getIntExtra("masterCatID", 0)
        masterCatName = intent.getStringExtra("masterCatName")!!
        toolbar.txtTopHeading.text = masterCatName

        toolbar.cartLayout.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                callMainActivity(this)
            else
                loginFirst(this, "Other")
        }
        getSubCategoryData()

    }

    private fun getSubCategoryData() {
        viewModel.getSubCategoryData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            masterCatID
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
        log("getSubCategoryData", " data $dataG")
        if (type == "getSubCategoryData") {
            val data: ArrayList<SubCategoryDataModel> = dataG as ArrayList<SubCategoryDataModel>
            log("getSubCategoryData", " data $data")
            if (data.size > 0) {
                val layoutManager = GridLayoutManager(this, 2)
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position % 3 == 2) 2 else 1
                    }
                }
                binding.menCategoryRecyclerView.layoutManager = layoutManager
                binding.menCategoryRecyclerView.setHasFixedSize(true)
                binding.menCategoryRecyclerView.adapter =
                    CategoryDataAdapter(data, this, applicationContext)
            } else
                toast("We're sorry, but there are no results to show")
        }
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        val data = dataG.toString()
        val intent = Intent(applicationContext, ShowAllProductActivity::class.java)
        intent.putExtra("catID", masterCatID)//Master_Cat ID
        intent.putExtra("type", "SubCategory")
        intent.putExtra("name", data)
        startActivity(intent)
    }
}