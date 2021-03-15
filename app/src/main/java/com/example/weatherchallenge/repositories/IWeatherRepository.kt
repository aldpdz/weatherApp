package com.example.weatherchallenge.repositories

import com.example.weatherchallenge.utils.Result
import com.example.weatherchallenge.data.models.remote.WeatherData

interface IWeatherRepository {
    suspend fun getWeatherConditions(city: String, key: String): Result<List<WeatherData>>?
}