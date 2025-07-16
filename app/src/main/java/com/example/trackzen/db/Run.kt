package com.example.trackzen.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")

data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Float = 0f,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Float = 0f

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null


}