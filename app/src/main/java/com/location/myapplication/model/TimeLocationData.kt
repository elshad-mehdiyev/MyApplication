package com.location.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeLocationData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val date: String,
    val locationLatitude: String,
    val locationLongitude: String,
    val type: String
)