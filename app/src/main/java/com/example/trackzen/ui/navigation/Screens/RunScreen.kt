package com.example.trackzen.ui.navigation.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trackzen.ui.navigation.Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.trackzen.ui.viewModels.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackzen.db.Run
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunItemCard(run: Run) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text("Date: ${run.timestamp}")
        Text("Distance: ${run.distanceInMeters} m")
        Text("Calories: ${run.caloriesBurned}")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel() // ✅ auto-inject ViewModel
) {
    val runs by viewModel.runs.collectAsState() // ✅ collect list of runs
    var selectedSort by remember { mutableStateOf("Date") }
    val sortOptions = listOf("Date", "Distance", "Calories", "Time")
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(runs) {

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TrackZen") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF1976D2), // Material blue 700
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Tracking.route) },
                containerColor = Color.Yellow
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start Run")
            }
        },
        containerColor = Color(0xFF2196F3)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text("Sort by: ")
                Spacer(modifier = Modifier.width(8.dp))



                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(selectedSort, color = Color.White)
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSort = option
                                    expanded = false
                                    viewModel.sortRuns(option)
                                    // optional if sorting needed
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (runs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No runs yet!", color = MaterialTheme.colorScheme.onPrimary)
                }
            } else {
                LazyColumn {

                    items(runs) { run ->
                        RunItemCard(run)

                    }
                }
            }
        }
    }
}
