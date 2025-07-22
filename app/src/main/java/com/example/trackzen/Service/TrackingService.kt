package com.example.trackzen.Service

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.trackzen.MainActivity
import android.Manifest
import com.example.trackzen.R
import com.example.trackzen.other.Constants.ACTION_PAUSE_SERVICE
import com.example.trackzen.other.Constants.ACTION_SHOW_TRACKING_SCREEN
import com.example.trackzen.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.trackzen.other.Constants.ACTION_STOP_SERVICE
import com.example.trackzen.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.trackzen.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.trackzen.other.Constants.NOTIFICATION_ID
import com.example.trackzen.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.trackzen.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.trackzen.other.TrackingUtility
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import com.google.android.gms.location.Priority


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private var serviceKilled = false
    private var timeStarted = 0L
    private var timeRun = 0L
    private var timerJob: Job? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var baseNotificationBuilder: NotificationCompat.Builder? = null
    private var curNotificationBuilder: NotificationCompat.Builder? = null

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPointsFlow = MutableStateFlow<Polylines>(mutableListOf())
        val timeRunInMillis = MutableLiveData<Long>()
        val currentLocationFlow = MutableStateFlow<Location?>(null)
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        postInitialValues()
    }

    private fun getBaseNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_pause_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
    }
    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_SHOW_TRACKING_SCREEN
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            FLAG_UPDATE_CURRENT
        )
    }


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPointsFlow.value = mutableListOf()
        timeRunInMillis.postValue(0L)
        currentLocationFlow.value = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                        resumeService()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pausing service...")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopping service...")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        CoroutineScope(Dispatchers.Main).launch {
            startTimer()
            isTracking.postValue(true)
            addEmptyPolyline()
            startLocationUpdates()

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            baseNotificationBuilder = getBaseNotificationBuilder()
            curNotificationBuilder = baseNotificationBuilder
            baseNotificationBuilder?.let { builder ->
                startForeground(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun resumeService() {
        startTimer()
        isTracking.postValue(true)
        startLocationUpdates()
    }

    private fun pauseService() {
        isTracking.postValue(false)
        timerJob?.cancel()
        stopLocationUpdates()
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    private fun addEmptyPolyline() {
        val updatedPolylines = pathPointsFlow.value.toMutableList()
        updatedPolylines.add(mutableListOf())
        pathPointsFlow.value = updatedPolylines
    }


    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            val updatedPolylines = pathPointsFlow.value.toMutableList()
            if (updatedPolylines.isNotEmpty()) {
                updatedPolylines.last().add(pos)
                pathPointsFlow.value = updatedPolylines
            }
            currentLocationFlow.value = location
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value == true) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (TrackingUtility.hasLocationPermissions(this)) {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()

        timerJob = CoroutineScope(Dispatchers.Main).launch {

            while (isTracking.value == true) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    updateNotificationTrackingState(isTracking.value ?: false)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private var lapTime = 0L
    private var lastSecondTimestamp = 0L

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(
                this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder?.let { builder ->
            // Clear existing actions
            try {
                val mActionsField = builder.javaClass.getDeclaredField("mActions")
                mActionsField.isAccessible = true
                mActionsField.set(builder, ArrayList<NotificationCompat.Action>())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            }
        if (!serviceKilled) {
            val updatedBuilder = baseNotificationBuilder
                ?.addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            curNotificationBuilder = updatedBuilder
            updatedBuilder?.let {
                notificationManager.notify(NOTIFICATION_ID, it.build())
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }



    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        stopLocationUpdates()
    }
}

private const val TIMER_UPDATE_INTERVAL = 50L
private val FLAG_UPDATE_CURRENT: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    else
        PendingIntent.FLAG_UPDATE_CURRENT
