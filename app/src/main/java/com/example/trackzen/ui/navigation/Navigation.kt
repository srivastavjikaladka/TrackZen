package com.example.trackzen.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trackzen.ui.navigation.Screens.RunScreen
import com.example.trackzen.ui.navigation.Screens.SetUpScreen
import com.example.trackzen.ui.navigation.Screens.SettingsScreen
import com.example.trackzen.ui.navigation.Screens.StatisticsScreen
import com.example.trackzen.ui.navigation.Screens.TrackingScreen
import com.example.trackzen.ui.navigation.Screen
import androidx.compose.ui.Modifier
import com.example.trackzen.ui.navigation.Screens.StartScreen


@Composable
fun TrackZenNavHost(navController: NavHostController,
                    modifier: Modifier = Modifier
) {
    NavHost(navController,
        startDestination = Screen.Setup.route,
        modifier = Modifier
    ) {
        composable(Screen.Run.route) { RunScreen(navController) }
        composable(Screen.Start.route) { StartScreen(navController) }
        composable(Screen.Tracking.route) { TrackingScreen(navController) }
        composable(Screen.Statistics.route) { StatisticsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.Setup.route) { SetUpScreen(navController) }
    }
}