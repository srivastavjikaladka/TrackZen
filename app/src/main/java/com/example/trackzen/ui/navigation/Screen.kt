package com.example.trackzen.ui.navigation

sealed class Screen(val route: String) {
    object Run : Screen("run")
    object Start : Screen("start")
    object Settings : Screen("settings")
    object Setup : Screen("setup")
    object Statistics : Screen("statistics")
    object Tracking : Screen("tracking")
}
