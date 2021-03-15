package com.example.weatherchallenge.data.models.remote

import com.example.weatherchallenge.data.models.domain.DetailData
import com.example.weatherchallenge.data.models.domain.WeatherInfo

data class WeatherData (
        val dt: Long,
        val main: Main,
        val weather: List<Weather>)

fun List<WeatherData>.toListWeatherInfo(): List<WeatherInfo>{
    return map {
        WeatherInfo(it.dt, it.weather.first().main, it.main.temp.toString())
    }
}

fun WeatherData.toDetailData(): DetailData {
    return DetailData(
            main.temp.toString(),
            main.feels_like.toString(),
            weather.first().main,
            weather.first().description)
}