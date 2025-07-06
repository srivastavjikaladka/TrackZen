package com.example.trackzen.ui.fragments


import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trackzen.R
import com.example.trackzen.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

}