package com.example.covid19tracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.covid19tracker.repository.CovidTrackerRepository
import com.example.covid19tracker.ui.CovidTrackerViewModel
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 18-09-2021.
 */

class CovidViewModelFactory @Inject constructor(private val repository: CovidTrackerRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CovidTrackerViewModel::class.java)) {
            CovidTrackerViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}