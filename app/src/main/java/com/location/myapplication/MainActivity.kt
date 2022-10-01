package com.location.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
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
import com.google.maps.android.SphericalUtil
import com.location.myapplication.databinding.ActivityMainBinding
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.model.TimeLocationData
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding
    private val  viewModel: LocationViewModel by viewModels()
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
   /* private lateinit var sensorManager: SensorManager
    private lateinit var sensorMagnetic: Sensor
    private lateinit var sensorAccelerometer: Sensor
    private var gravity = FloatArray(3)
    private var geoMagnetic = FloatArray(3)
    private var orientation = FloatArray(3)
    private var rotationMatrix = FloatArray(9)
    private var degree1 = -50f
    private var degree2 = 0f
    var differOfDegrees = 0.0*/

    private var first = LatLng(0.0, 0.0)
    private var type = 1




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getFromUpdates()
        //setUpSensor()
    }
  /*  private fun setUpSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val sensorEventListener = object: SensorEventListener{
            override fun onSensorChanged(p0: SensorEvent?) {
                p0?.let {
                    if (p0.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        gravity = p0.values
                        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geoMagnetic)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                    }
                    if (p0.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        geoMagnetic = p0.values
                        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geoMagnetic)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                    }
                    degree1 = orientation[0]
                    if(abs(degree1 * (180 / 3.14159) - degree2 * (180 / 3.14159)) > 45) {
                        differOfDegrees = abs(degree1 * (180 / 3.14159) - degree2 * (180 / 3.14159))
                        degree2 = degree1
                    }
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                return
            }

        }
        sensorManager.registerListener(sensorEventListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorEventListener, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL)

    }*/
    private fun getFromUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
            maxWaitTime = 1500
            fastestInterval = 1000
            smallestDisplacement = 15f
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
                    val end = LatLng(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
                    if(SphericalUtil.computeDistanceBetween(first, end) >= 120) {
                        first = end
                        type = 2
                    } else {
                        type = 1
                    }
                    val lastPosition = LatLng(locationResult.lastLocation?.latitude!!,locationResult.lastLocation?.longitude!!)
                    if (Build.VERSION.SDK_INT > 25 && locationResult.lastLocation!!.accuracy < 60f) {
                        val now = LocalDateTime.now()
                        viewModel.insertDate(
                            TimeLocationData(
                            date = now.toString(),
                            locationLongitude = lastPosition.longitude.toString(),
                            locationLatitude = lastPosition.latitude.toString(),
                            type = type.toString()
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