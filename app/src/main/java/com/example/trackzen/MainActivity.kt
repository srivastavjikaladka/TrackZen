package com.example.trackzen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.trackzen.ui.theme.TrackZenTheme
import android.util.Log
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackzen.db.RunDao
import com.example.trackzen.ui.navigation.BottomBar
import com.example.trackzen.ui.navigation.Screen
import com.example.trackzen.ui.navigation.TrackZenNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var runDao: RunDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TrackZen", "RUNDAO: ${runDao.hashCode()}")

        setContent {
            TrackZenTheme {
                val navController = rememberNavController()
                enableEdgeToEdge()
                val currentDestination = navController.currentBackStackEntry?.destination?.route
                val showBottomBar = currentDestination != Screen.Setup.route


                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(navController)
                        }
                    }
                ) { padding ->
                    TrackZenNavHost(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }

        }
    }

}







