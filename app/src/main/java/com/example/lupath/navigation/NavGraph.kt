package com.example.lupath.navigation

import androidx.compose.material.Text
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
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String, exitApp: () -> Unit) {
    NavHost(navController = navController, startDestination = startDestination) {
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
            route = "mountainDetail/{mountainId}",
            arguments = listOf(navArgument("mountainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mountainIdFromRoute = backStackEntry.arguments?.getString("mountainId")
            if (mountainIdFromRoute != null && mountainIdFromRoute.isNotBlank()) {
                MountainDetailScreen(
                    mountainIdFromNav = mountainIdFromRoute, // Pass the String ID
                    navController = navController
                )
            } else {
                Text("Error: Mountain ID is missing in navigation route.")
            }
        }

//        composable(
//            route = "datepicker/{mountainId}", // Add mountainId as argument
//            arguments = listOf(navArgument("mountainId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val mountainIdArg = backStackEntry.arguments?.getString("mountainId")
//            if (mountainIdArg != null) {
//                DatePickerScreen(navController = navController, mountainId = mountainIdArg)
//            } else {
//                // Handle error: mountainId not found, maybe navigate back or show error
//                Text("Error: Mountain ID missing.")
//            }
//        }

        composable(
            // Route for DatePickerScreen, now with optional parameters for editing
            route = "datepicker/{mountainId}?hikePlanId={hikePlanId}&initialSelectedDateEpochDay={initialSelectedDateEpochDay}&notes={notes}",
            arguments = listOf(
                navArgument("mountainId") { type = NavType.StringType },
                navArgument("hikePlanId") {
                    type = NavType.StringType
                    nullable = true // This ID is optional, only present when editing
                    defaultValue = null
                },
                navArgument("initialSelectedDateEpochDay") {
                    type = NavType.LongType
                    defaultValue = -1L // Use -1L or another sentinel to indicate no date passed
                },
                navArgument("notes") {
                    type = NavType.StringType
                    nullable = true // This is optional
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val mountainIdArg = backStackEntry.arguments?.getString("mountainId")
            val hikePlanIdArg = backStackEntry.arguments?.getString("hikePlanId") // Can be null
            val initialDateEpochDayArg = backStackEntry.arguments?.getLong("initialSelectedDateEpochDay")
            val notesArg = backStackEntry.arguments?.getString("notes")?.let { encodedNotes ->
                try {
                    URLDecoder.decode(encodedNotes, StandardCharsets.UTF_8.name())
                } catch (e: Exception) {
                    encodedNotes
                }
            }

            if (mountainIdArg != null) {
                DatePickerScreen(
                    navController = navController,
                    mountainId = mountainIdArg,
                    hikePlanIdForEdit = hikePlanIdArg, // Pass to DatePickerScreen
                    initialSelectedDateEpochDay = initialDateEpochDayArg ?: -1L,
                    initialNotes = notesArg // Pass to DatePickerScreen
                )
            } else {
                Text("Error: Mountain ID missing for DatePicker.")
            }
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