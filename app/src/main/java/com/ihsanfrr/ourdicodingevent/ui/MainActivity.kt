package com.ihsanfrr.ourdicodingevent.ui

import android.os.Bundle
import android.text.Html
import com.ihsanfrr.ourdicodingevent.R
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ihsanfrr.ourdicodingevent.databinding.ActivityMainBinding
import com.ihsanfrr.ourdicodingevent.helpers.ConnectionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        ConnectionHelper.verifyInternet(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailFragment -> {
                    toggleBottomNavigation(true)
                    supportActionBar?.show()
                    supportActionBar?.title = "Dicoding Event Detail"
                    supportActionBar?.setTitle(Html.fromHtml("<font color='#FFFFFF'>${supportActionBar?.title}</font>"))
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
                }
                else -> {
                    toggleBottomNavigation(false)
                    supportActionBar?.hide()
                }
            }
        }
    }

    private fun toggleBottomNavigation(isVisible: Boolean) {
        binding.navView.apply {
            visibility = if (isVisible) {
                BottomNavigationView.GONE
            } else {
                BottomNavigationView.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.detailFragment) {
            navController.popBackStack(R.id.navigation_home, false)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }
}