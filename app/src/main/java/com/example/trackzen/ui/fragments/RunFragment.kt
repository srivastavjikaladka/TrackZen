package com.example.trackzen.ui.fragments



import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.example.trackzen.R
import com.example.trackzen.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    private val viewModel: MainViewModel by viewModels()

}