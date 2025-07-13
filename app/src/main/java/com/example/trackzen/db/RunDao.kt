package com.example.trackzen.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): Flow<List<Run>>
    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedBytimeinMillis(): Flow<List<Run>>
    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByavgSpeedInKMH(): Flow<List<Run>>
    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedBydistanceInMeters(): Flow<List<Run>>
    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedBycaloriesBurned(): Flow<List<Run>>



    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>
    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Int>
    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistanceInMeters(): LiveData<Float>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeedInKMH(): LiveData<Float>

    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsByDateRange(startDate: Long, endDate: Long): Flow<List<Run>>
    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsByTimeInMillisRange(startDate: Long, endDate: Long): Flow<List<Run>>

    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsByAvgSpeedInKMHRange(startDate: Long, endDate: Long): Flow<List<Run>>
    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsByDistanceInMetersRange(startDate: Long, endDate: Long): Flow<List<Run>>

    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsByCaloriesBurnedRange(startDate: Long, endDate: Long): Flow<List<Run>>

    @Query("SELECT * FROM running_table WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getRunsBySpeedRange(startDate: Long, endDate: Long): Flow<List<Run>>




}