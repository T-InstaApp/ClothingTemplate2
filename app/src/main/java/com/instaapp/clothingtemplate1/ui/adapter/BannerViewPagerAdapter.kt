package com.instaapp.clothingtemplate1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.instaapp.clothingtemplate1.R
import com.instaapp.clothingtemplate1.ui.activity.ZoomImageActivity

class BannerViewPagerAdapter(
    private val context: Context,
    val images: List<String>,
    val type: String,
    val stringUrl: String,
) :
    PagerAdapter() {

    private var inflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.imageb_anner, null)
        val bannerItem = view.findViewById<ImageView>(R.id.bannerItem)
        if (type == "Zoom" || type == "InspectionReport") {
            val photoView =
                view.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photo_view)
            Glide.with(context).load(images[position]).into(photoView)
            photoView.visibility = View.VISIBLE
            bannerItem.visibility = View.GONE
        } else {
            Glide.with(context).load(images[position]).into(bannerItem)
        }

        bannerItem.setOnClickListener {
            val intent = Intent(context, ZoomImageActivity::class.java)
            intent.putExtra("imageUrl", stringUrl)
            context.startActivity(intent)
        }

        val vp = container as ViewPager
        vp.addView(view, 0)


        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}