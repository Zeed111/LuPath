package com.example.lupath

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.lupath.data.model.GetStartedViewModel
import com.example.lupath.navigation.AppNavGraph
import com.example.lupath.ui.theme.LuPathTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasCompletedGetStarted = prefs.getBoolean(GetStartedViewModel.KEY_GET_STARTED_COMPLETED, false)
        val startDestination = if (hasCompletedGetStarted) "home" else "get_started"

        setContent {
            LuPathTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    exitApp = ::exitApp
                )
            }
        }
    }

    private fun exitApp() {
        finishAffinity()
    }
}