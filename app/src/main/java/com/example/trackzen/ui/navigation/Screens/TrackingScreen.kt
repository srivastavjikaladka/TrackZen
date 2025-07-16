package com.example.trackzen.ui.navigation.Screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.trackzen.ui.navigation.Screen
import com.example.trackzen.ui.viewModels.TrackingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    var countdownNumber by remember { mutableStateOf(3) }
    var isCountingDown by remember { mutableStateOf(true) }

    // Countdown logic
    LaunchedEffect(Unit) {
        for (i in 3 downTo 1) {
            countdownNumber = i
            delay(1000)
        }
        countdownNumber = 0
        delay(500)
        isCountingDown = false
        viewModel.startTracking()
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    val locationState by viewModel.currentLocation.collectAsState()
    val timeRunInMillis by viewModel.timeRunInMillis.collectAsState()
    val distanceRunInMeters by viewModel.distanceRunInMeters.collectAsState()
    val caloriesBurned by viewModel.caloriesBurned.collectAsState()
    val avgSpeedInKmh by viewModel.avgSpeedInKmh.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(28.6139, 77.2090), 12f)
    }

    Scaffold(
        containerColor = Color.Black,
        floatingActionButton = {
            if (!isCountingDown) {
                Box(modifier = Modifier.padding(bottom = 56.dp)) { // Adjust this value as needed
                    FloatingActionButton(
                        onClick = { showDialog.value = true },
                        containerColor = Color(0xFFEF5350)
                    ) {
                        Text("Stop", color = Color.White)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // 🗺️ MAP VIEW
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
                    ) {
                        locationState?.let {
                            Marker(
                                state = MarkerState(LatLng(it.latitude, it.longitude)),
                                title = "You"
                            )
                        }
                    }
                }

                // 📊 Stats
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "⏱ Time",
                        value = viewModel.getFormattedTime(timeRunInMillis),
                        cardColor = Color(0xFF1C1C1E),
                        valueColor = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Km",
                            value = String.format("%.2f", distanceRunInMeters / 1000f),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFF00E676),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Pace",
                            value = String.format("%.2f", avgSpeedInKmh),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFF80D8FF),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Kcal",
                            value = String.format("%.0f", caloriesBurned),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFFFFAB40),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Countdown overlay
            if (isCountingDown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (countdownNumber == 0) "GO!" else countdownNumber.toString(),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            // Stop confirmation dialog
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Stop Run?") },
                    text = { Text("Are you sure you want to stop the run?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog.value = false
                            viewModel.finishRun()
                            navController.navigate(Screen.RunSummary.route)
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun StatCard(title: String, value: String, cardColor: Color, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = valueColor)
            Text(text = value, fontSize = 24.sp, color = valueColor, fontWeight = FontWeight.Bold)
        }
    }
}
