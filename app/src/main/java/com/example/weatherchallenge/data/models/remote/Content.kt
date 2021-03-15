package com.example.weatherchallenge.data.models.remote

data class Content (val cod: String, val message: String, val list: List<WeatherData>)