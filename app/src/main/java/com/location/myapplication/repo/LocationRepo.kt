package com.location.myapplication.repo

import androidx.sqlite.db.SimpleSQLiteQuery
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.room.LocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepo @Inject constructor(
    private val dao: LocationDao
) {
    suspend fun saveCurrentLocation(locationModel: CurrentLocationModel) {
        dao.insertLocation(locationModel)
    }
    fun getAllSavedLocation() = dao.getAllLocation()
}