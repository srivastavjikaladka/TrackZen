package com.example.trackzen.ui.viewModels

import android.app.Application
import android.content.*
import android.location.Location
import androidx.lifecycle.*
import com.example.trackzen.Repository.MainRepository
import com.example.trackzen.db.Run
import com.example.trackzen.other.Constants.ACTION_PAUSE_SERVICE
import com.example.trackzen.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.trackzen.other.Constants.ACTION_STOP_SERVICE
import com.example.trackzen.other.TrackingUtility
import com.example.trackzen.Service.Polyline
import com.example.trackzen.Service.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val application: Application,
    private val repository: MainRepository
) : AndroidViewModel(application) {

    // ------------------------------------------
    // Service data flows
    // ------------------------------------------

    val isTracking = TrackingService.isTracking.asFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val pathPoints = TrackingService.pathPointsFlow.asStateFlow()

    val timeRunInMillis = TrackingService.timeRunInMillis.asFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    val currentLocation: StateFlow<Location?> = TrackingService.currentLocationFlow.asStateFlow()

    // ------------------------------------------
    // Calculated stats
    // ------------------------------------------

    val distanceRunInMeters = pathPoints.map { polylines ->
        var totalDistance = 0f
        for (polyline in polylines) {
            totalDistance += TrackingUtility.calculatePolylineLength(polyline)
        }
        totalDistance
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val avgSpeedInKmh = combine(
        distanceRunInMeters,
        timeRunInMillis
    ) { distance, time ->
        if (time > 0) {
            val distanceInKm = distance / 1000f
            val timeInHours = time / 1000f / 3600f
            if (timeInHours > 0) distanceInKm / timeInHours else 0f
        } else 0f
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val caloriesBurned = combine(
        distanceRunInMeters,
        repository.userWeight
    ) { distance, weight ->
        // Approximate calculation: 0.75 calories per kg per km
        val distanceInKm = distance / 1000f
        distanceInKm * weight * 0.75f
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )


    // Broadcasting receiver for service updates

    private var trackingReceiver: BroadcastReceiver? = null

    fun registerTrackingReceiver(context: Context) {
        if (trackingReceiver == null) {
            trackingReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val isTracking = intent?.getBooleanExtra("isTracking", false) ?: false
                    // Handle tracking state if needed
                }
            }
            val filter = IntentFilter("com.example.trackzen.TRACKING_STATUS_UPDATE")
            context.registerReceiver(trackingReceiver, filter)
        }
    }


    // Service commands


    fun sendCommandToService(action: String) {
        Intent(application, TrackingService::class.java).also {
            it.action = action
            application.startService(it)
        }
    }

    fun startTracking() {
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    fun pauseTracking() {
        sendCommandToService(ACTION_PAUSE_SERVICE)
    }

    fun finishRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
    }


    // Database operations


    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRun(run)
    }


    // Utility functions


    fun getFormattedTime(timeInMillis: Long): String {
        return TrackingUtility.getFormattedStopWatchTime(timeInMillis, false)
    }


    // Repository data

    val runsSortedByDate = repository.getAllRunsSortedByDate()
    val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis()
    val runsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    val runsSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeed()
    val runsSortedByDistance = repository.getAllRunsSortedByDistance()

    override fun onCleared() {
        super.onCleared()
        trackingReceiver?.let {
            try {
                application.unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver not registered
            }
        }
    }
}