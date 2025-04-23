package com.example.lupath.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lupath.ui.screen.datePicker.DatePickerScreen
import com.example.lupath.ui.screen.getstarted.GetStartedScreen
import com.example.lupath.ui.screen.getstarted.GetStartedScreen
import com.example.lupath.ui.screen.home.HomeScreen
import com.example.lupath.ui.screen.lupathList.LuPathListScreen
import com.example.lupath.ui.screen.mountainDetails.MountainDetailScreen
import com.example.lupath.ui.screen.toBringList.CheckListScreen


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

//        composable(
//            route =  "lupath_list",
//            arguments = listOf(
//                navArgument("mountainName") { type = NavType.StringType; nullable = true  },
//                navArgument("selectedDate") { type = NavType.StringType; nullable = true }
//            )
//        ) { backStackEntry ->
//
//            val mountain = backStackEntry.arguments?.getString("mountainName")
//            val dateStr = backStackEntry.arguments?.getString("selectedDate")
//
//            LuPathListScreen(
//                navController = navController, // Pass if needed by the simplified screen
//                mountainName = mountain,
//                selectedDate = dateStr
//            )
//        }
    }
}


