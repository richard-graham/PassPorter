package com.example.passporter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.passporter.presentation.feature.add.AddBorderPointScreen
import com.example.passporter.presentation.feature.detail.BorderDetailsScreen
import com.example.passporter.presentation.feature.map.MapScreen

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object BorderDetails : Screen("border_details/{borderId}") {
        fun createRoute(borderId: String) = "border_details/$borderId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route,
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
            BorderDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}