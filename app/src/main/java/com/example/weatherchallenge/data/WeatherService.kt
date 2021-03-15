package com.example.weatherchallenge.data

import com.example.weatherchallenge.data.models.remote.Content
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET(Constants.FORECAST)
    suspend fun getWeatherConditions(
            @Query("q") city: String,
            @Query("appid") appId: String
    ): Response<Content>
}