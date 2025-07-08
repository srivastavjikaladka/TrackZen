package com.example.trackzen.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Map

import androidx.navigation.compose.currentBackStackEntryAsState


data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

    @Composable
    fun BottomBar(navController: NavController) {
        val items = listOf(
            Screen.Run,
            Screen.Statistics,
            Screen.Settings
        )

        NavigationBar {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = when (screen) {
                                is Screen.Run -> Icons.Default.DirectionsRun
                                is Screen.Statistics -> Icons.Default.BarChart
                                is Screen.Settings -> Icons.Default.Settings
                                else -> Icons.Default.Home
                            },
                            contentDescription = screen.route
                        )
                    },
                    label = { Text(screen.route.capitalize()) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Setup.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

