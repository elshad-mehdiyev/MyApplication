package com.location.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.location.myapplication.databinding.FragmentMainBinding
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.model.TimeLocationData
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: LocationViewModel by viewModels()
    private var firstPosition = LatLng(0.0,0.0)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getFromUpdates()
        binding.showInMap.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToMapsActivity()
            findNavController().navigate(action)
        }
        binding.showInList.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToShowAllLocationInList()
            findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getFromUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(5)
            maxWaitTime = TimeUnit.SECONDS.toMillis(5)
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
                        val distance = SphericalUtil.computeDistanceBetween(firstPosition, lastPosition)
                        if (distance > 60) {
                            if (Build.VERSION.SDK_INT > 25) {
                                val now = LocalDateTime.now()
                                val model = TimeLocationData(
                                    date = now.toString(),
                                    locationLongitude = lastPosition.longitude.toString(),
                                    locationLatitude = lastPosition.latitude.toString(),
                                    distance = distance
                                )
                                viewModel.insertDate(model)
                            }
                            firstPosition = lastPosition
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }
}