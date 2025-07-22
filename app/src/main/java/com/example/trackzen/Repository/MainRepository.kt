package com.example.trackzen.Repository

import com.example.trackzen.db.Run
import com.example.trackzen.db.RunDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val runDao: RunDao
) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedBytimeinMillis()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedBycaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByavgSpeedInKMH()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedBydistanceInMeters()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeedInKMH()

    fun getTotalDistance() = runDao.getTotalDistanceInMeters()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    val userWeight: Flow<Float> = flow {
        emit(70.0f)
    }
}
