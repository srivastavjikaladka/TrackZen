package com.example.trackzen.ui.navigation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackzen.ui.navigation.Screen
import kotlin.random.Random
import androidx.navigation.NavOptionsBuilder



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    val quotes = listOf(
        "Congrats, you're still running from commitment — now with cardio 😮‍💨",
        "Sweat now so your crush regrets later",
        "You're not running from your past... you're just logging it in kilometers 🤷‍♂️",
        "Workout now, or forever hold your breath when stairs appear 🫁",
        "Glow up loading… if you don’t quit halfway again 💅",
        "Pain is temporary. Regret is data-tracked 💀",
        "Chasing goals? Or just pretending life isn’t a simulation 🤖🏃‍♂️",
        "No shortcuts, just sweat and delulu ╰(*°▽°*)╯"
    )
    val randomQuote by remember { mutableStateOf(quotes.random()) }

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TrackZen 🏃‍♂️",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.primary
        )

        Text(
            text = "Welcome! Please enter your name and weight.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Name", color = colorScheme.onSurface)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Your Name", color = colorScheme.outline) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = colorScheme.background,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        cursorColor = colorScheme.primary
                    )
                )

                Text("Weight", color = colorScheme.onSurface)
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    placeholder = { Text("Your Weight", color = colorScheme.outline) },
                    trailingIcon = { Text("kg", color = colorScheme.outline) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = colorScheme.background,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        cursorColor = colorScheme.primary
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer)
        ) {
            Text(
                text = "\"$randomQuote\"",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                navController.navigate(Screen.Run.route, builder = {
                    popUpTo(Screen.Setup.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Continue", style = MaterialTheme.typography.bodyLarge)
        }


        Spacer(modifier = Modifier.height(24.dp))
    }
}
