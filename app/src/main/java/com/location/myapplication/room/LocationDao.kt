package com.location.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.location.myapplication.model.CurrentLocationModel

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(locationModel: CurrentLocationModel)
    @Query("SELECT * FROM CurrentLocationModel")
    fun getAllLocation(): LiveData<List<CurrentLocationModel>>
}