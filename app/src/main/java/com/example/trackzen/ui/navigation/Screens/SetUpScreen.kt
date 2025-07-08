package com.example.trackzen.ui.navigation.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.trackzen.ui.navigation.Screen

@Composable
fun SetUpScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome! Please enter your name and weight.")
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Your Name") })
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Your Weight") })
        Button(onClick = { navController.navigate(Screen.Run.route) }) {
            Text("Continue")
        }
    }
}
