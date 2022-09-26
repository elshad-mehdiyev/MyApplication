package com.location.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.location.myapplication.databinding.ActivityMapsBinding
import com.location.myapplication.model.TimeLocationData
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: LocationViewModel by viewModels()
    private var differ = 0L
    private var myLocation = LatLng(0.0, 0.0)
    private var myEndLocation = LatLng(0.0, 0.0)
    private lateinit var circleOptions: CircleOptions
    private var first = LatLng(0.0, 0.0)
    private var timeString = ""
    private var pathPoints = mutableListOf<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true
        observeData()
    }
    private fun addLocationToRoute(locations: List<TimeLocationData>) {
        val polylineOptions = PolylineOptions()
        mMap.clear()
        val originalLatLngList = polylineOptions.points
        val latLngList = locations.map {
            LatLng(it.locationLatitude.toDouble(), it.locationLongitude.toDouble())}
        originalLatLngList.addAll(latLngList)
        mMap.addPolyline(polylineOptions)
    }
    private fun observeData() {
        viewModel.allDate.observe(this) {
            it?.let {
                addLocationToRoute(it)
            }
            viewModel.allMarkerLocation.observe(this) { all ->
                all?.let {
                    for (i in  all.indices) {
                        if (i > 0 && Build.VERSION.SDK_INT > 25) {
                            val startTime = LocalDateTime.parse(all[i - 1].date)
                            val endTime = LocalDateTime.parse(all[i].date)
                            differ = ChronoUnit.SECONDS.between(startTime, endTime)
                            val hours = if(differ / 3600 > 0) "${differ / 3600} saat" else ""
                            val minutes =if((differ % 3600) / 60 > 0) "${(differ % 3600) / 60} deqiqe" else ""
                            val second = if((differ % 3600) % 60 > 0) "${(differ % 3600) % 60} saniye" else ""
                            timeString = "Burada $hours $minutes $second vaxt kecirib"
                            myLocation = LatLng(
                                all[i - 1].locationLatitude.toDouble(),
                                all[i - 1].locationLongitude.toDouble()
                            )
                            mMap.addMarker(MarkerOptions().position(myLocation))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                            if(differ > 900) {
                                myEndLocation = LatLng(
                                    all[i].locationLatitude.toDouble(),
                                    all[i].locationLongitude.toDouble()
                                )
                                val midLatitude = (myLocation.latitude + myEndLocation.latitude) / 2
                                val midLongitude = (myLocation.longitude + myEndLocation.longitude) / 2
                                val midLocation = LatLng(midLatitude, midLongitude)
                                mMap.addMarker(MarkerOptions().position(midLocation).title(timeString))
                                circleOptions = CircleOptions()
                                    .center(midLocation)
                                    .radius(60.0)
                                    .strokeColor(Color.BLACK)
                                    .fillColor(Color.CYAN)
                                    .strokeWidth(2f)
                                mMap.addCircle(circleOptions)
                            }
                            if(i == all.lastIndex) {
                                myEndLocation = LatLng(
                                    all[i].locationLatitude.toDouble(),
                                    all[i].locationLongitude.toDouble()
                                )
                                mMap.addMarker(MarkerOptions().position(myEndLocation))
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myEndLocation, 15f))
                            }
                        }
                    }
                }
            }
        }
    }
}