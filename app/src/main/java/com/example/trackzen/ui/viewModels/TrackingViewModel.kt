package com.example.trackzen.ui.viewModels

import android.app.Application
import android.content.Intent
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackzen.Repository.MainRepository
import com.example.trackzen.Service.TrackingService
import com.example.trackzen.db.Run
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    application: Application,
    private val repository : MainRepository
) : AndroidViewModel(application) {


    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val _timeRunInMillis = MutableStateFlow(0L)
    val timeRunInMillis: StateFlow<Long> = _timeRunInMillis

    private val _distanceRunInMeters = MutableStateFlow(0f)
    val distanceRunInMeters: StateFlow<Float> = _distanceRunInMeters

    private val _caloriesBurned = MutableStateFlow(0f)
    val caloriesBurned: StateFlow<Float> = _caloriesBurned

    private val _avgSpeedInKmh = MutableStateFlow(0f)
    val avgSpeedInKmh: StateFlow<Float> = _avgSpeedInKmh

    private val _runStats = MutableStateFlow<Run?>(null)
    val runStats: StateFlow<Run?> = _runStats


    private var timerJob: Job? = null
    private var startTime = 0L

    fun startTracking() {
        startTime = System.currentTimeMillis()

        // Timer logic
        timerJob = viewModelScope.launch {
            while (true) {
                _timeRunInMillis.value = System.currentTimeMillis() - startTime
                delay(1000L)
            }
        }

        // Dummy tracking logic (replace with real location updates from FusedLocationProviderClient or foreground service)
        simulateTracking()
    }

    fun stopTracking() {
        timerJob?.cancel()
    }

    private fun simulateTracking() {
        // Dummy values for now; replace with actual GPS updates
        viewModelScope.launch {
            repeat(60) {
                delay(1000L)
                val distance = _distanceRunInMeters.value + 2.5f // e.g., 2.5 meters/second
                _distanceRunInMeters.value = distance

                _caloriesBurned.value = (distance / 1000f) * 60f // ~60kcal/km
                val minutes = (_timeRunInMillis.value / 60000f).coerceAtLeast(1f)
                _avgSpeedInKmh.value = (distance / 1000f) / (minutes / 60f)
            }
        }
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    fun getFormattedTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60 % 60
        val hours = (ms / 1000) / 3600
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    fun finishRun() {
        stopTracking() // stop the timer

        val run = Run(
            timestamp = System.currentTimeMillis(),
            avgSpeedInKMH = avgSpeedInKmh.value,
            distanceInMeters = distanceRunInMeters.value,
            timeInMillis = timeRunInMillis.value,
            caloriesBurned = caloriesBurned.value,

        )

        viewModelScope.launch {
            repository.insertRun(run)
            _runStats.value = run
        }
    }

    //called in tracking screen afteer thec ontdown ends
    fun sendCommandToService(action: String) {
        val intent = Intent(getApplication(), TrackingService::class.java).apply {
            this.action = action
        }
        getApplication<Application>().startService(intent)
    }


}
