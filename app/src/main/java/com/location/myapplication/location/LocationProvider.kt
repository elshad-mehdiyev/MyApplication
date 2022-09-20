package com.location.myapplication.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

class LocationProvider(private val activity: Activity) {
    private val client
            by lazy { LocationServices.getFusedLocationProviderClient(activity) }
    private val locations = mutableListOf<LatLng>()
    val liveLocation = MutableLiveData<Location>()

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            locations.add(latLng)
            liveLocation.value = location
        }
    }
    private var distance = 0
    val liveLocations = MutableLiveData<List<LatLng>>()
    val liveDistance = MutableLiveData<Int>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val currentLocation = result.lastLocation!!
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            val lastLocation = locations.lastOrNull()
            if (lastLocation != null) {
                distance +=
                    SphericalUtil.computeDistanceBetween(lastLocation, latLng).roundToInt()
                liveDistance.value = distance
            }
            locations.add(latLng)
            liveLocations.value = locations
        }
    }
    @SuppressLint("MissingPermission")
    fun trackUser() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000

        client.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }
}