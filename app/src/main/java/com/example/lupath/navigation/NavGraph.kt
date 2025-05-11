package com.example.lupath.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lupath.ui.screen.datePicker.DatePickerScreen
import com.example.lupath.ui.screen.getstarted.GetStartedScreen
import com.example.lupath.ui.screen.home.HomeScreen
import com.example.lupath.ui.screen.lupathList.LuPathListScreen
import com.example.lupath.ui.screen.mountainDetails.MountainDetailScreen
import com.example.lupath.ui.screen.settings.AboutScreen
import com.example.lupath.ui.screen.settings.SettingsScreen
import com.example.lupath.ui.screen.checkList.CheckListScreen

@Composable
fun AppNavGraph(navController: NavHostController, exitApp: () -> Unit) {
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
            val mountainName =
                backStackEntry.arguments?.getString("mountainName") ?: "Unknown Mountain"
            MountainDetailScreen(
                mountainName = mountainName,
                navController = navController
            )
        }

        composable("datepicker") {
            DatePickerScreen(navController)
        }

        composable(route = "lupath_list") {
            // LuPathListScreen gets the ViewModel automatically
            LuPathListScreen(navController = navController)
        }

        composable("check_list") {
            CheckListScreen(navController = navController)
        }

        composable("settings"){
            SettingsScreen(
                navController = navController,
                onAboutPress = {
                    navController.navigate("about")
                },

                onExitApp = exitApp
            )
        }

        composable("about") {
            AboutScreen(navController = navController)
        }

    }
}