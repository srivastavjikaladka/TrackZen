package com.example.trackzen.ui.navigation.Screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TrackingScreen(navController: NavController) {
    Text(text = "Tracking Screen")
    Button(onClick = { navController.navigate(Screen.Tracking.route) }) {
        Text("Go to Tracking")
    }


}

