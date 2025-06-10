package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val lastLogin = prefs.getLong("lastLoginTime", 0L)
        val isLogged = prefs.getBoolean("isLoggedIn", false)
        val userId = prefs.getString("userId", "")
        val currentTime = System.currentTimeMillis()

        val maxAllowedTime = 3 * 24 * 60 * 60 * 1000L

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        Log.d("User logged:", "$isLogged")

        if (isLogged) {

            showBottomNavigationView()

            navController.navigate(R.id.navigation_home)

        } else {

            hideBottomNavigationView()

            // Session ended
            prefs.edit { clear() }

            // Go to LoginFragment in order to login again
            navController.navigate(R.id.navigation_register)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_schedule, R.id.navigation_attendees, R.id.navigation_profile
            )
        )

        navView.setupWithNavController(navController)

    }

    fun showBottomNavigationView() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigationView() {
        binding.navView.visibility = View.GONE
    }
}