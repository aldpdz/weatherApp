package com.example.weatherchallenge.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherchallenge.data.models.domain.WeatherInfo
import com.example.weatherchallenge.databinding.ListWeatherBinding

class WeatherAdapter (private val clickListener: WeatherListener) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    var data = listOf<WeatherInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListWeatherBinding.inflate(layoutInflater,
                    parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            item: WeatherInfo,
            clickListener: WeatherListener
        ) {
            binding.weatherInfo = item
            binding.clickListener = clickListener
        }
    }
}

class WeatherListener(val clickListener: (dt: Long) -> Unit) {
    fun onClick(dt: Long) = clickListener(dt)
}