package com.example.trackzen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.trackzen.ui.theme.TrackZenTheme
import android.util.Log

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackzen.db.RunDao
import com.example.trackzen.ui.navigation.Screens.Screen
import com.example.trackzen.ui.navigation.Screens.RunScreen
import com.example.trackzen.ui.navigation.Screens.SetUpScreen
import com.example.trackzen.ui.navigation.Screens.SettingsScreen
import com.example.trackzen.ui.navigation.Screens.StatisticsScreen
import com.example.trackzen.ui.navigation.Screens.TrackingScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var runDao: RunDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Trackzen", "RUNDAO: ${runDao.hashCode()}")

        setContent {
            TrackZenTheme {
                val navController = rememberNavController()
                enableEdgeToEdge()

                Scaffold { innerPadding ->
                    TrackZenNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

        }
    }
    @Composable
    fun TrackZenNavHost(navController: NavHostController = rememberNavController()
    ,modifier: Modifier = Modifier) {
        NavHost(navController = navController, startDestination = Screen.Run.route) {
            composable(Screen.Run.route) { RunScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.Setup.route) { SetUpScreen(navController) }
            composable(Screen.Statistics.route) { StatisticsScreen(navController) }
            composable(Screen.Tracking.route) { TrackingScreen(navController) }
        }
    }

}





