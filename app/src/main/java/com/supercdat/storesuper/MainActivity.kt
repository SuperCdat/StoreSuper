package com.supercdat.storesuper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.supercdat.storesuper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_installed, R.id.navigation_options
            )
        )

        navView.setupWithNavController(navController)

        for (i in 0 until navView.menu.size()) {
            val menuItem = navView.menu.getItem(i)
            val view = navView.findViewById<View>(menuItem.itemId)
            if (view != null) {
                view.setOnLongClickListener { true }
                view.setOnTouchListener { _: View?, _: MotionEvent? -> false }
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences("Mode", Context.MODE_PRIVATE)
            val nightMode: Boolean = sharedPreferences.getBoolean("nightMode", false)
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }


    }

}