package com.example.trackzen.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")

data class Run(
    val img: Bitmap? = null,
    val timestamp: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val distanceInMeters: Float = 0f,
    val timeInMillis: Long = 0L,
    val caloriesBurned: Float = 0f


) {
    @PrimaryKey(autoGenerate = true)
    var id :Int?=null


}