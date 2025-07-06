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
import com.example.trackzen.db.RunDao
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
                Scaffold { padding ->
                    Text(
                        modifier = Modifier.padding(padding),
                        text = "TrackZen is running 🚀")

            }
        }

        }
    }
}


