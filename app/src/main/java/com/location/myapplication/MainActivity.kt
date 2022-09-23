package com.location.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.location.myapplication.databinding.ActivityMainBinding
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.model.TimeLocationData
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val  viewModel: LocationViewModel by viewModels()
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getFromUpdates()
    }
    private fun getFromUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            maxWaitTime = 8000
            fastestInterval = 5000
            smallestDisplacement = 120f
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val model = CurrentLocationModel(
                    latitude = locationResult.lastLocation?.latitude.toString(),
                    longitude = locationResult.lastLocation?.longitude.toString(),
                    accuracy = locationResult.lastLocation?.accuracy.toString()
                )
                viewModel.saveLocation(model)
                val lastPosition = LatLng(locationResult.lastLocation?.latitude!!,locationResult.lastLocation?.longitude!!)
                if (Build.VERSION.SDK_INT > 25) {
                    val now = LocalDateTime.now()
                    viewModel.insertDate(
                        TimeLocationData(
                        date = now.toString(),
                        locationLongitude = lastPosition.longitude.toString(),
                        locationLatitude = lastPosition.latitude.toString(),
                    )
                    )
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }
}