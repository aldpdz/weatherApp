package com.example.weatherchallenge.repositories

import com.example.weatherchallenge.utils.Result
import com.example.weatherchallenge.data.WeatherService
import com.example.weatherchallenge.data.models.remote.Content
import com.example.weatherchallenge.data.models.remote.WeatherData
import com.squareup.moshi.Moshi

class WeatherRepository(private val weatherService: WeatherService) : IWeatherRepository {

    override suspend fun getWeatherConditions(city: String, key: String)
    : Result<List<WeatherData>>?{
        val response = weatherService.getWeatherConditions(city, key)
        return if (response.isSuccessful) {
            response.body()?.let {
                when(it.cod){
                    "200" -> Result.Success(it.list)
                    else -> Result.Error(Exception())
                }
            }
        }else {
            if(response.code() == 404) {
                return Result.Message(response.message())
            }
            Result.Error(Exception())
        }
    }
}