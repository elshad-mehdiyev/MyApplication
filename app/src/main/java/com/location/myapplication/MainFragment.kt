package com.location.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.location.myapplication.databinding.FragmentMainBinding
import com.location.myapplication.model.CurrentLocationModel
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: LocationViewModel by viewModels()
    private val scope = MainScope() // could also use an other scope such as viewModelScope if available
    private var job: Job? = null


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
        startUpdates()
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


    private fun startUpdates() {
        job = scope.launch {
            while(true) {
                getLocation()
                delay(5000)
            }
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        /*fusedLocationClient.lastLocation
            .addOnSuccessListener {
                it?.let {
                        val model = CurrentLocationModel(
                            latitude = it.latitude.toString(),
                            longitude = it.longitude.toString(),
                            accuracy = it.accuracy.toString()
                        )
                        viewModel.saveLocation(model)
                }
            }*/
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            maxWaitTime = 100
        }.priority
        fusedLocationClient.getCurrentLocation(locationRequest, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(requireContext(), "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                   val model = CurrentLocationModel(
                       latitude = location.latitude.toString(),
                       longitude = location.longitude.toString(),
                       accuracy = location.accuracy.toString()
                   )
                    viewModel.saveLocation(model)
                }

            }
    }
}