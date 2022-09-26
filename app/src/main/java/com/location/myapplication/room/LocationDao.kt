package com.location.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.model.TimeLocationData

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(locationModel: CurrentLocationModel)
    @Query("SELECT * FROM CurrentLocationModel")
    fun getAllLocation(): LiveData<List<CurrentLocationModel>>
    @Insert
    suspend fun insertDate(timeLocationData: TimeLocationData)
    @Query("SELECT * FROM TimeLocationData")
    fun getAllDate(): LiveData<List<TimeLocationData>>
    @Query("SELECT * FROM TimeLocationData WHERE type = 2")
    fun getMarkerLocations(): LiveData<List<TimeLocationData>>

}