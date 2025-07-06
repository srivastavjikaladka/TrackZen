package com.example.trackzen.ui.viewModels
import com.example.trackzen.Repository.MainRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class StatisticViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

}
