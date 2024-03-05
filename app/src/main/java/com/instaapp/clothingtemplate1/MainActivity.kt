package com.instaapp.clothingtemplate1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.instaapp.clothingtemplate1.dataModel.AboutUs
import com.instaapp.clothingtemplate1.databinding.*
import com.instaapp.clothingtemplate1.ui.activity.*
import com.instaapp.clothingtemplate1.ui.fragment.CartFragment
import com.instaapp.clothingtemplate1.ui.fragment.HomeFragment
import com.instaapp.clothingtemplate1.ui.fragment.OrderFragment
import com.instaapp.clothingtemplate1.utils.*

class MainActivity : AppCompatActivity(), View.OnClickListener, UpdateUi {

    var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavBar: AppBarMainBinding
    private lateinit var navigationDrawerBinding: DrawerLayoutBinding
    private lateinit var toolbarBinding: ToolbarBinding
    private var functionalityType = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavBar = binding.appBarMain
        navigationDrawerBinding = binding.drawer
        toolbarBinding = bottomNavBar.toolbar

        findViewById<ImageView>(R.id.img_back).setOnClickListener(this)

        findViewById<TextView>(R.id.txtAppName).text =
            PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.COMPANY_NAME
            )

        navigationDrawerBinding.navPro.setOnClickListener(this)
        navigationDrawerBinding.navAbout.setOnClickListener(this)
        navigationDrawerBinding.navContact.setOnClickListener(this)
        navigationDrawerBinding.navLogout.setOnClickListener(this)
        navigationDrawerBinding.navLogin.setOnClickListener(this)
        toolbarBinding.icMenu.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.openDrawer(
                GravityCompat.START
            ) else binding.drawerLayout.closeDrawer(
                GravityCompat.START
            )
        }

        toolbarBinding.txtTopHeading.text = PreferenceProvider(applicationContext).getStringValue(
            PreferenceKey.COMPANY_NAME
        )

        functionalityType = intent.getBooleanExtra("cart", false)
        init()


        toolbarBinding.imgInfo.setOnClickListener {
            info(
                this,
                "Once the order is confirmed or shipped, cancellation is not possible. If you wish to cancel, please contact our customer support"
            )
        }


    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.nav_login -> {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.putExtra("callFrom", "MainActivity")
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_contact -> {
                val intent = Intent(this@MainActivity, ContactUsActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_about -> {
                val intent = Intent(this@MainActivity, AbutUsActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_pro -> {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.nav_logout -> {
                binding.drawerLayout.closeDrawers()
                PreferenceProvider(applicationContext).setBooleanValue(
                    false,
                    PreferenceKey.LOGIN_STATUS
                )
                PreferenceProvider(applicationContext).setStringValue(
                    "0",
                    PreferenceKey.USER_PROFILE
                )
                PreferenceProvider(applicationContext).setIntValue(
                    0,
                    PreferenceKey.CART_COUNT
                )
                // PreferenceProvider(applicationContext).clear()

                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.putExtra("cart", false)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            R.id.img_back -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.openDrawer(
                    GravityCompat.START
                ) else binding.drawerLayout.closeDrawer(
                    GravityCompat.START
                )
            }
        }
    }

    override fun updateIMG(url: Boolean) {

    }

    private fun init() {

        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("0")
        ) {
            navigationDrawerBinding.navPro.visibility = View.GONE
            navigationDrawerBinding.navAbout.visibility = View.VISIBLE
            navigationDrawerBinding.navContact.visibility = View.VISIBLE
            navigationDrawerBinding.navLogout.visibility = View.GONE
            navigationDrawerBinding.navLogin.visibility = View.VISIBLE
        } else if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("2")
        ) {
            navigationDrawerBinding.navPro.visibility = View.GONE
            navigationDrawerBinding.navAbout.visibility = View.VISIBLE
            navigationDrawerBinding.navContact.visibility = View.VISIBLE
            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        } else {
            navigationDrawerBinding.navPro.visibility = View.VISIBLE
            navigationDrawerBinding.navAbout.visibility = View.VISIBLE
            navigationDrawerBinding.navContact.visibility = View.VISIBLE
            navigationDrawerBinding.navLogout.visibility = View.VISIBLE
            navigationDrawerBinding.navLogin.visibility = View.GONE
        }
        bottomNav()
    }

    private fun bottomNav() {
        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)
                .equals("0")
        ) {
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_order).visibility = View.VISIBLE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_cart).visibility = View.VISIBLE
        } else {
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_order).visibility = View.VISIBLE
            bottomNavBar.navigation.findViewById<View>(R.id.m_item_cart).visibility = View.VISIBLE
        }
        toolbarBinding.imgInfo.visibility = View.GONE

        bottomNavBar.navigation.setOnItemSelectedListener { menuItem ->
            toolbarBinding.imgInfo.visibility = View.GONE
            var selectedFragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.m_item_home -> {
                    selectedFragment = HomeFragment()
                    toolbarBinding.txtTopHeading.text =
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.COMPANY_NAME
                        )
                }

                R.id.m_item_cart -> if (PreferenceProvider(applicationContext).getBooleanValue(
                        PreferenceKey.LOGIN_STATUS
                    )
                ) {
                    selectedFragment = CartFragment()
                    toolbarBinding.txtTopHeading.text = "Shopping Bag"
                } else {
                    loginFirst(this, "Home")
                    return@setOnItemSelectedListener true
                }
                R.id.m_item_order -> {
                    if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                        selectedFragment = OrderFragment()
                        toolbarBinding.txtTopHeading.text = "Order List"
                        toolbarBinding.imgInfo.visibility = View.VISIBLE
                    } else {
                        loginFirst(this, "Home")
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.m_item_more -> {
                    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.openDrawer(
                        GravityCompat.START
                    ) else binding.drawerLayout.closeDrawer(
                        GravityCompat.START
                    )
                    return@setOnItemSelectedListener false
                }
            }
            replaceFragment(selectedFragment!!)
            true
        }

        if (functionalityType) {
            toolbarBinding.txtTopHeading.text = "Shopping Bag"
            replaceFragment(CartFragment())
        } else
            replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        //String backStateName = fragment.getClass().getName();
        val backStateName = fragment.javaClass.name
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.replace(R.id.parent_foter, fragment)
            ft.commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finish()
                return
            } else {
                this.doubleBackToExitPressedOnce = true
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        } else {
            super.onBackPressed()
        }
    }

}