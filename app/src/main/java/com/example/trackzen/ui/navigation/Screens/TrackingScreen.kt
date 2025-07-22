package com.example.trackzen.ui.navigation.Screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.trackzen.db.Run
import com.example.trackzen.other.Constants.POLYLINE_COLOR
import com.example.trackzen.other.Constants.POLYLINE_WIDTH
import com.example.trackzen.other.Constants.MAP_ZOOM
import com.example.trackzen.other.TrackingUtility
import com.example.trackzen.ui.navigation.Screen
import com.example.trackzen.ui.viewModels.TrackingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.TextUnit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*
import androidx.compose.material3.MaterialTheme



@OptIn(ExperimentalMaterial3Api::class,
ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingViewModel = hiltViewModel()

) {
    val context = LocalContext.current
    val pathPoints by viewModel.pathPoints.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.registerTrackingReceiver(context)
    }

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

        // Start foreground service
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
    val isTracking by viewModel.isTracking.collectAsState()
    val isMapLoaded = remember { mutableStateOf(false) }


    val cameraPositionState = rememberCameraPositionState()

    // Update camera position when location changes

    LaunchedEffect(locationState, pathPoints, isMapLoaded.value) {
        locationState?.let { location ->
            val newPosition = LatLng(location.latitude, location.longitude)

            // If we have path points, fit them in view, otherwise follow current location
            if (pathPoints.isNotEmpty() && pathPoints.any { it.isNotEmpty() }) {
                val allPoints = pathPoints.flatten()
                if (allPoints.size > 1) {
                    val boundsBuilder = LatLngBounds.builder()
                    allPoints.forEach { point ->
                        boundsBuilder.include(point)
                    }
                    val bounds = boundsBuilder.build()
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 100),
                        1000
                    )
                } else {
                    // Single point or just started
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(newPosition, MAP_ZOOM),
                        1000
                    )
                }
            } else {
                // No path points yet, just follow current location
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(newPosition, MAP_ZOOM),
                    1000
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tracking Run", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Stop tracking and go back
                        viewModel.finishRun()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black,
        floatingActionButton = {
            if (!isCountingDown) {
                FloatingActionButton(
                    onClick = { showDialog.value = true },
                    containerColor = Color(0xFFEF5350)
                ) {
                    Text("Stop", color = Color.White)
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
                modifier = Modifier.fillMaxSize()
            ) {
                // 🗺️ ENHANCED MAP VIEW
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                ) {
                    GoogleMap(

                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = true,
                            mapType = MapType.NORMAL ,

                        ),
                        onMapLoaded = {
                            isMapLoaded.value = true
                        },
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false, // We'll handle this manually
                            compassEnabled = true,
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false
                        )
                    ) {
                        // Draw current location marker with custom icon
                        locationState?.let { location ->
                            Marker(
                                state = MarkerState(LatLng(location.latitude, location.longitude)),
                                title = "You are here",
                                snippet = "Current location"
                            )
                        }

                        // Draw polylines for the path with better styling
                        pathPoints.forEachIndexed { index, polyline ->
                            if (polyline.size > 1) {
                                Polyline(
                                    points = polyline,
                                    color = Color(0xFF00E676), // Bright green
                                    width = POLYLINE_WIDTH + 2f,
                                    pattern = null,
                                    geodesic = true
                                )
                            }
                        }

                        // Start point marker
                        if (pathPoints.isNotEmpty() && pathPoints[0].isNotEmpty()) {
                            val startPoint = pathPoints[0][0]
                            Marker(
                                state = MarkerState(startPoint),
                                title = "Start",
                                snippet = "Run started here"
                            )
                        }
                    }

                    // My Location Button
                    FloatingActionButton(
                        onClick = {
                            locationState?.let { location ->
                                val newPosition = LatLng(location.latitude, location.longitude)
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, MAP_ZOOM)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Text("📍", fontSize = 20.sp)
                    }
                }

                // 📊 Enhanced Stats
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Time display
                    StatCard(
                        title = "⏱ Duration",
                        value = viewModel.getFormattedTime(timeRunInMillis),
                        cardColor = Color(0xFF1C1C1E),
                        valueColor = Color.White,
                        modifier = Modifier.height(80.dp),
                        fontSize = 32.sp
                    )

                    // Other stats in a row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "🏃 Distance",
                            value = String.format("%.2f km", distanceRunInMeters / 1000f),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFF00E676),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "⚡ Speed",
                            value = String.format("%.1f", avgSpeedInKmh),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFF80D8FF),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "🔥 Calories",
                            value = String.format("%.0f", caloriesBurned),
                            cardColor = Color(0xFF1C1C1E),
                            valueColor = Color(0xFFFFAB40),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Enhanced Pause/Resume button
                    PauseResumeButton(
                        isTracking = isTracking,
                        onPause = { viewModel.pauseTracking() },
                        onResume = { viewModel.startTracking() }
                    )
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (countdownNumber == 0) "GO!" else countdownNumber.toString(),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (countdownNumber == 0) Color(0xFF00E676) else Color.White
                        )
                        if (countdownNumber > 0) {
                            Text(
                                text = "Get Ready!",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Stop confirmation dialog
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Stop Run?", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("Are you sure you want to stop the run? This will save your current progress.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog.value = false

                                // Create run object to save - FIX: Handle null img properly
                                val run = Run(
                                    img = null, // This should be handled properly in the database
                                    timestamp = Calendar.getInstance().timeInMillis,
                                    avgSpeedInKMH = avgSpeedInKmh,
                                    distanceInMeters = distanceRunInMeters,
                                    timeInMillis = timeRunInMillis,
                                    caloriesBurned = caloriesBurned
                                )

                                viewModel.insertRun(run)
                                viewModel.finishRun()
                                navController.navigate(Screen.RunSummary.route) {
                                    popUpTo(Screen.Tracking.route) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFEF5350)
                            )
                        ) {
                            Text("Stop Run", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("Continue")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    cardColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = fontSize,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PauseResumeButton(
    isTracking: Boolean,
    modifier: Modifier = Modifier,
    onPause: () -> Unit,
    onResume: () -> Unit
) {
    Button(
        onClick = { if (isTracking) onPause() else onResume() },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isTracking) Color.Yellow else Color.Green,
            contentColor = Color.Black
        ),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = if (isTracking) "Pause" else "Resume",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}