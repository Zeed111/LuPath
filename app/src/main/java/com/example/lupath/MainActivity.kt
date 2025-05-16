package com.example.lupath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.lupath.navigation.AppNavGraph
import com.example.lupath.ui.theme.LuPathTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            LuPathTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    exitApp = ::exitApp
                )
            }
        }
    }

    private fun exitApp() {
        finishAffinity()
    }
}