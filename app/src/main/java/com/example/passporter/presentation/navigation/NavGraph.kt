package com.example.passporter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.passporter.presentation.feature.add.AddBorderPointScreen
import com.example.passporter.presentation.feature.auth.AuthScreen
import com.example.passporter.presentation.feature.detail.BorderDetailsScreen
import com.example.passporter.presentation.feature.map.MapScreen

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object BorderDetails : Screen("border_details/{borderId}") {
        fun createRoute(borderId: String) = "border_details/$borderId"
    }
    object Auth : Screen("auth")
    object EditBorderPoint : Screen("edit_border_point/{borderId}?initialSection={initialSection}") {
        fun createRouteWithSection(borderId: String, initialSection: Int): String {
            return "edit_border_point/$borderId?initialSection=$initialSection"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToBorderDetail = { borderId ->
                    navController.navigate(Screen.BorderDetails.createRoute(borderId))
                },
                onNavigateToAdd = { location ->
                    navController.navigate("add_border_point/${location.latitude}/${location.longitude}")
                }
            )
        }

        composable(
            route = Screen.BorderDetails.route,
            arguments = listOf(
                navArgument("borderId") { type = NavType.StringType }
            )
        ) {
            val shouldRefresh = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<Boolean>("shouldRefresh") ?: false

            if (shouldRefresh) {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("shouldRefresh", false)
            }

            BorderDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = "add_border_point/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) {
            AddBorderPointScreen(
                onNavigateBack = { navController.popBackStack() },
                onUpdateSuccess = {
                    // This will ensure the details screen refreshes its data
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("shouldRefresh", true)
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditBorderPoint.route,
            arguments = listOf(
                navArgument("borderId") { type = NavType.StringType },
                navArgument("initialSection") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val initialSection = backStackEntry.arguments?.getInt("initialSection") ?: 0

            AddBorderPointScreen(
                initialSection = initialSection,
                onNavigateBack = { navController.popBackStack() },
                onDeleteSuccess = {
                    navController.navigate(Screen.Map.route) {
                        // Pop up to the map screen, removing all screens in between
                        popUpTo(Screen.Map.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}