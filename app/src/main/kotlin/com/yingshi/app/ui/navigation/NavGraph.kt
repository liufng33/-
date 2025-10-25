package com.yingshi.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yingshi.app.ui.screens.home.HomeScreen
import com.yingshi.app.ui.screens.player.PlayerScreen
import com.yingshi.app.ui.screens.sources.SourcesScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Player : Screen("player/{videoUrl}") {
        fun createRoute(videoUrl: String) = "player/$videoUrl"
    }
    object Sources : Screen("sources")
}

@Composable
fun YingshiNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPlayer = { videoUrl ->
                    navController.navigate(Screen.Player.createRoute(videoUrl))
                },
                onNavigateToSources = {
                    navController.navigate(Screen.Sources.route)
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            PlayerScreen(
                videoUrl = videoUrl,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Sources.route) {
            SourcesScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
