package com.cleanarch.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Player : Screen("player")
    object Settings : Screen("settings")
}
