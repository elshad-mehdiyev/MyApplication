package com.location.myapplication.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.model.TimeLocationData
import com.location.myapplication.repo.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repo: LocationRepo
): ViewModel() {

    val allLocation = repo.getAllSavedLocation()

    fun saveLocation(location: CurrentLocationModel) {
        viewModelScope.launch {
            repo.saveCurrentLocation(location)
        }
    }
    val allDate = repo.getAllDate()

    fun insertDate(timeLocationData: TimeLocationData) {
        viewModelScope.launch {
            repo.insertDate(timeLocationData)
        }
    }
}