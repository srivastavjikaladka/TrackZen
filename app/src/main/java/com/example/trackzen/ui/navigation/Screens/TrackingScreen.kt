package com.example.trackzen.ui.navigation.Screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.trackzen.ui.viewModels.TrackingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingViewModel = hiltViewModel()

) {
    val context = LocalContext.current

    // Launch permission request for location
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // Initial camera focus on India
    val india = LatLng(28.6139, 77.2090)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(india, 12f)
    }

    // Observe states from ViewModel
    val locationState by viewModel.currentLocation.collectAsState()
    val timeRunInMillis by viewModel.timeRunInMillis.collectAsState()
    val distanceRunInMeters by viewModel.distanceRunInMeters.collectAsState()
    val caloriesBurned by viewModel.caloriesBurned.collectAsState()
    val avgSpeedInKmh by viewModel.avgSpeedInKmh.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        //
        //  Google Map View
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                locationState?.let {
                    Marker(
                        state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                        title = "You"
                    )
                }
            }
        }

        // ⏱ Timer Text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = viewModel.getFormattedTime(timeRunInMillis),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
        }

        // 📊 Metrics Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = String.format("%.2f", distanceRunInMeters / 1000f), label = "Km")
            StatItem(value = String.format("%.2f", avgSpeedInKmh), label = "Pace (min/km)")
            StatItem(value = String.format("%.0f", caloriesBurned), label = "Kcal")
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
