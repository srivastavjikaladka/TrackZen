package com.example.trackzen.ui.navigation.Screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.trackzen.db.Run
import com.example.trackzen.other.TrackingUtility
import com.example.trackzen.ui.navigation.Screen
import com.example.trackzen.ui.viewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RunItem(run: Run) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = "Run",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00C853), Color(0xFF69F0AE))
                        )
                    )
                    .padding(12.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatDate(run.timestamp),
                    color = Color.White,
                    fontSize = 14.sp)
                Text("${run.distanceInMeters / 1000f} Km",
                    color = Color(0xFF00E676),
                    fontSize = 20.sp
                )
                Row {
                    Text("${run.timeInMillis / 1000f} min",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("${String.format("%.2f", run.avgSpeedInKMH)} min/km",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Text("${run.caloriesBurned} Kcal",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val runs by viewModel.runs.collectAsState()
    var selectedSort by remember { mutableStateOf("Date") }
    val sortOptions = listOf("Date", "Distance", "Calories", "Time")
    var expanded by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val activity = context as? Activity

    var hasForegroundPermission by remember { mutableStateOf(false) }
    var hasBackgroundPermission by remember { mutableStateOf(false) }

    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activity?.let {
            if (!TrackingUtility.hasForegroundLocationPermission(it)) {
                TrackingUtility.requestForegroundPermissions(it)
            }
        }
    }

// ✅ Re-check permissions after possible user interaction
    LaunchedEffect(hasForegroundPermission, hasBackgroundPermission) {
        activity?.let {
            hasForegroundPermission = TrackingUtility.hasForegroundLocationPermission(it)
            hasBackgroundPermission = TrackingUtility.hasBackgroundLocationPermission(it)

            if (hasForegroundPermission && !hasBackgroundPermission) {
                showPermissionDialog = true
            }
        }
    }


    //  AlertDialog when permission not granted
    if (showPermissionDialog && (!hasForegroundPermission || !hasBackgroundPermission)) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = {
                Text("TrackZen requires location permissions to track your runs. Please allow access.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    activity?.let {
                        TrackingUtility.requestBackgroundPermission(it)
                    }
                }) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    //  Block screen if permission still not granted
    if (!hasForegroundPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1976D2)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Location permissions are required to track your runs.",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    activity?.let {
                        TrackingUtility.requestForegroundPermissions(it)
                        hasForegroundPermission = TrackingUtility.hasForegroundLocationPermission(it)
                    }
                }
            ) {
                Text("Grant Permissions")
            }
        }
        return@RunScreen
    }







    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TrackZen", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
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
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Sorting dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                Text("Sort by:",
                    color = Color.White)

                Spacer(
                    modifier = Modifier.width(8.dp))
                Box {
                    TextButton(
                        onClick = {
                            expanded = true }) {
                        Text(
                            selectedSort,
                            color = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false }) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSort = option
                                    viewModel.sortRuns(option)
                                    expanded = false
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
                    Text("No runs yet!",
                        color = Color.White)
                }
            } else {
                LazyColumn {
                    items(runs) { run -> RunItem(run) }
                }
            }
        }
    }
}
