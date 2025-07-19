package com.example.trackzen.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.trackzen.MainActivity
import com.example.trackzen.R
import com.example.trackzen.other.Constants.ACTION_PAUSE_SERVICE
import com.example.trackzen.other.Constants.ACTION_SHOW_TRACKING_SCREEN
import com.example.trackzen.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.trackzen.other.Constants.ACTION_STOP_SERVICE
import com.example.trackzen.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.trackzen.other.Constants.NOTIFICATION_CHANNEL_NAME
import android.Manifest.permission.ACCESS_FINE_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import android.Manifest
import com.google.android.gms.location.Priority


class TrackingService : LifecycleService() {

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        private const val NOTIFICATION_ID = 1

    }

    private var isFirstRun = true
    private var isTimerEnabled = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

                 // Lifecycle Methods (onCreate, onStartCommand) //

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        postInitialValues()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {

            ACTION_START_OR_RESUME_SERVICE -> {
                if (isFirstRun) {
                    startForegroundService()
                    isFirstRun = false
                } else {
                    Timber.d("Started or resumed services")
                    startTimer()
                }
            }
            ACTION_PAUSE_SERVICE -> {
                pauseService()
                Timber.d("Paused service")
            }
            ACTION_STOP_SERVICE -> {
                killService()
                Timber.d("Stopped service")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

                               // CORE TRACKING LOGIC //

    private fun startTimer() {
        isTracking.postValue(true)
        isTimerEnabled = true
        broadcastTrackingState(true)
        startLocationUpdates()

    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
        broadcastTrackingState(false)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun killService() {
        isTracking.postValue(false)
        isFirstRun = true
        isTimerEnabled = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        broadcastTrackingState(false)

    }

    private fun broadcastTrackingState(isTracking: Boolean) {
        val intent = Intent("com.example.trackzen.TRACKING_STATUS_UPDATE")
        intent.putExtra("isTracking", isTracking)
        sendBroadcast(intent)
    }

                              // LOCATION HANDLING //

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            /* intervalMillis = */ 5000L
        )
            .setMinUpdateIntervalMillis(2000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            for (location in result.locations) {

            }
        }
    }

                             // Notification Setup //

    private fun startForegroundService() {
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotficationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // your custom icon
            .setContentTitle("TrackZen Running Tracker")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        startTimer()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotficationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_SHOW_TRACKING_SCREEN
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}


