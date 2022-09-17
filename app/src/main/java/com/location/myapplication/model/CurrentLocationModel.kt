package com.location.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentLocationModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val latitude: String,
    val longitude: String,
    val accuracy: String
)
