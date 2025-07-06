package com.example.trackzen.ui.viewModels


import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trackzen.R
import com.example.trackzen.ui.viewModels.StatisticViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistic) {
    private val viewModel: StatisticViewModel by viewModels()
}