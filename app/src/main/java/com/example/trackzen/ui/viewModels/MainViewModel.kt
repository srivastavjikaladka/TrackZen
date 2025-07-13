package com.example.trackzen.ui.viewModels
import com.example.trackzen.Repository.MainRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.trackzen.db.Run
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlinx.coroutines.flow.first



@HiltViewModel
class MainViewModel @Inject constructor(
   private val mainRepository: MainRepository
) : ViewModel() {


    private val _runs = MutableStateFlow<List<Run>>(emptyList())
    val runs: StateFlow<List<Run>> = _runs

    init {
        viewModelScope.launch {
            val run = Run(
                timestamp = System.currentTimeMillis(),
                avgSpeedInKMH = 10f,
                distanceInMeters = 1500,
                timeInMillis = 900000,
                caloriesBurned = 120,

                )
            mainRepository.insertRun(run)
            sortRuns("Date")
        }
    }

    fun sortRuns(option: String) {
        viewModelScope.launch {
            val sortedRuns = when (option) {
                "Date" -> mainRepository.getAllRunsSortedByDate()
                "Distance" -> mainRepository.getAllRunsSortedByDistance()
                "Calories" -> mainRepository.getAllRunsSortedByCaloriesBurned()
                "Time" -> mainRepository.getAllRunsSortedByTimeInMillis()
                else -> null
            }
            sortedRuns?.collect { runsList ->
                _runs.value = runsList
            }
        }
    }
}


