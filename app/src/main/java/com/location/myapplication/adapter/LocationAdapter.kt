package com.location.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.location.myapplication.databinding.LocationItemBinding
import com.location.myapplication.model.CurrentLocationModel

class LocationAdapter: RecyclerView.Adapter<LocationAdapter.LocationHolder>() {
    class LocationHolder(var binding: LocationItemBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<CurrentLocationModel>() {
        override fun areItemsTheSame(
            oldItem: CurrentLocationModel,
            newItem: CurrentLocationModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CurrentLocationModel,
            newItem: CurrentLocationModel
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val list = AsyncListDiffer(this, diffUtil)

    var locationList: List<CurrentLocationModel>
    get() = list.currentList
    set(value) = list.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.binding.location = locationList[position]
    }

    override fun getItemCount(): Int {
        return locationList.size
    }
}