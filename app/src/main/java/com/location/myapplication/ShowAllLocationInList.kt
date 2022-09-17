package com.location.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.location.myapplication.adapter.LocationAdapter
import com.location.myapplication.databinding.FragmentShowAllLocationInListBinding
import com.location.myapplication.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowAllLocationInList : Fragment() {
    private var _binding: FragmentShowAllLocationInListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private val adapter = LocationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowAllLocationInListBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observe()
    }
    private fun initAdapter() {
        binding.recycleList.layoutManager = LinearLayoutManager(context)
        binding.recycleList.adapter = adapter
    }
    private fun observe() {
        viewModel.allLocation.observe(viewLifecycleOwner) {
            it?.let {
                adapter.locationList = it
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}