
package com.example.trackzen.Repository

import com.example.trackzen.db.Run
import com.example.trackzen.db.RunDao
import javax.inject.Inject
class MainRepository @Inject constructor(
    private val runDao: RunDao
){
    suspend fun insertRun(run: Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)
    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedBydistanceInMeters()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedBytimeinMillis()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByavgSpeedInKMH()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedBycaloriesBurned()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeedInKMH()
    fun getTotalDistance() = runDao.getTotalDistanceInMeters()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getRunsByDateRange(startDate: Long, endDate: Long) = runDao.getRunsByDateRange(startDate, endDate)
    fun getRunsByTimeInMillisRange(startDate: Long, endDate: Long) = runDao.getRunsByTimeInMillisRange(startDate, endDate)
    fun getRunsByAvgSpeedRange(startDate: Long, endDate: Long) = runDao.getRunsByAvgSpeedInKMHRange(startDate, endDate)
    fun getRunsByDistanceRange(startDate: Long, endDate: Long) = runDao.getRunsByDistanceInMetersRange(startDate, endDate)
    fun getRunsByCaloriesBurnedRange(startDate: Long, endDate: Long) = runDao.getRunsByCaloriesBurnedRange(startDate, endDate)
    fun getRunsBySpeedRange(startDate: Long, endDate: Long) = runDao.getRunsBySpeedRange(startDate, endDate)








}