package com.example.lupath.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lupath.ui.screen.getstarted.GetStartedScreen
import com.example.lupath.ui.screen.getstarted.GetStartedScreen
import com.example.lupath.ui.screen.home.HomeScreen
import com.example.lupath.ui.screen.mountainDetails.MountainDetailScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "get_started") {
        composable("get_started") {
            GetStartedScreen(onNavigateToHome = {
                navController.navigate("home") {
                    popUpTo("get_started") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable(
            route = "mountainDetail/{mountainName}",
            arguments = listOf(navArgument("mountainName") { type = NavType.StringType })
        ) { backStackEntry ->
            val mountainName = backStackEntry.arguments?.getString("mountainName") ?: "Unknown Mountain"
            MountainDetailScreen(
                mountainName = mountainName,
                navController = navController
            )
        }
    }
}


