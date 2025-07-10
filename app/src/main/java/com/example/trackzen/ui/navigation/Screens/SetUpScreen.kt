package com.example.trackzen.ui.navigation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackzen.ui.navigation.Screen
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val quotes = listOf(
        "Congrats, you're still running from commitment — now with cardio😮‍💨",
        "Sweat now so your crush regrets later",
        "You're not running from your past... you're just logging it in kilometers🤷‍♂️",
        "Workout now, or forever hold your breath when stairs appear🫁",
        "Glow up loading… if you don’t quit halfway again💅",
        "One more run before the identity crisis hits again\uD83D\uDE2E\u200D\uD83D\uDCA8",
        "Pain is temporary. Regret is data-tracked💀",
        "Chasing goals? Or just pretending life isn’t a simulation🤖🏃‍♂️",
        "No shortcuts, just sweat and delulu ╰(*°▽°*)╯"

    )
    val randomQuote by remember { mutableStateOf(quotes.random()) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF2196F3)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TrackZen🏃‍",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium

        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Welcome! Please enter your name and weight." ,
            color = Color.White, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Name", color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = { Text("Your Name") },
            textStyle = LocalTextStyle.current.copy(color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,

            )

            )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Weight", color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = {weight = it},
            label = { Text("Your Weight") },
            trailingIcon = { Text("kg",color = Color.White) },
            textStyle = LocalTextStyle.current.copy(color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,


            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "\"$randomQuote\"",
            color = Color.Yellow,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)

        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            //saving name and weight in datastore or DB
            navController.navigate(Screen.Run.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White
            )) {
                Text("Continue", color = Color.Black)
            }

    }
}
