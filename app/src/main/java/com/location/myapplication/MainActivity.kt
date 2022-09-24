package com.location.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
    private lateinit var sensorManager: SensorManager
    private var magneticFieldSensor: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
       /* sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (magneticFieldSensor != null) {
            sensorManager.registerListener(sensorListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI)
        }*/


        getFromUpdates()
    }
   /* private var sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                Log.v("tag", "${event.values[0]}")
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            TODO("Not yet implemented")
        }

    }*/
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
                if (locationResult.lastLocation != null) {
                    val model = CurrentLocationModel(
                        latitude = locationResult.lastLocation?.latitude.toString(),
                        longitude = locationResult.lastLocation?.longitude.toString(),
                        accuracy = locationResult.lastLocation?.accuracy.toString()
                    )
                    viewModel.saveLocation(model)
                    val lastPosition = LatLng(locationResult.lastLocation?.latitude!!,locationResult.lastLocation?.longitude!!)
                    if (Build.VERSION.SDK_INT > 25 && locationResult.lastLocation!!.accuracy < 60f) {
                        val now = LocalDateTime.now()
                        viewModel.insertDate(
                            TimeLocationData(
                            date = now.toString(),
                            locationLongitude = lastPosition.longitude.toString(),
                            locationLatitude = lastPosition.latitude.toString()
                            )
                        )
                    }
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