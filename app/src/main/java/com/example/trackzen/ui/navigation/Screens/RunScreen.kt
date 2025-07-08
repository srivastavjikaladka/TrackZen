package com.example.trackzen.ui.navigation.Screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun RunScreen(navController: NavController) {
    Text(text = "Run Screen")
    // Add your run-related UI elements here
    Button(onClick = { navController.navigate(Screen.Run.route) }) {
        Text("Go to Run")
    }


}

