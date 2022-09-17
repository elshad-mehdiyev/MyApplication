package com.location.myapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.location.myapplication.model.CurrentLocationModel

@Database(entities = [CurrentLocationModel::class], version = 1, exportSchema = false)
abstract class LocationDB: RoomDatabase() {
    abstract fun getLocationDao(): LocationDao
}