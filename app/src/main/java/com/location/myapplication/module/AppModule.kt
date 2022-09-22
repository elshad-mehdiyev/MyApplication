package com.location.myapplication.module

import android.content.Context
import androidx.room.Room
import com.location.myapplication.room.LocationDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context
    ): LocationDB = Room.databaseBuilder(
        context,
        LocationDB::class.java,
        "DB_Location1"
    ).build()

    @Singleton
    @Provides
    fun provideDao(
        locationDB: LocationDB
    ) = locationDB.getLocationDao()
}