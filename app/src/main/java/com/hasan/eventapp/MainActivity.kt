package com.hasan.eventapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hasan.eventapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ===========================
    // Properties
    // ===========================
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // ===========================
    // Properties
    // ===========================
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()

        setupNavigation()
    }

    // ===========================
    // UI Configuration Methods
    // ===========================
    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupSystemBars() {
        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)

        // Configure system bar appearance
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    // ===========================
    // Navigation Methods
    // ===========================
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    // ===========================
    // Override Methods
    // ===========================
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
