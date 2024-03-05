package com.instaapp.clothingtemplate1.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.databinding.ActivityZoomImageBinding
import com.instaapp.clothingtemplate1.databinding.ToolbarBinding
import com.instaapp.clothingtemplate1.ui.adapter.BannerViewPagerAdapter
import java.util.*

class ZoomImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityZoomImageBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    private var productID = 0
    private var productName = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbar = binding.toolBar
        toolbar.txtTopHeading.text = "View Image"
        toolbar.icMenu.setImageResource(R.drawable.back_drawer)
        toolbar.icMenu.setOnClickListener { finish() }

        showAllGroupImages(intent.getStringExtra("imageUrl")!!)
    }


    private fun showAllGroupImages(gName: String) {
        // Load Default Image
        val imageList = gName.trimEnd('@').split("@")
        bannerViewPagerAdapter =
            BannerViewPagerAdapter(this, imageList, "Zoom","")
        binding.bannerKocMarket.adapter = bannerViewPagerAdapter
        binding.tabDots.setupWithViewPager(binding.bannerKocMarket, true)
    }


}