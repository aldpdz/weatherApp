package com.example.weatherchallenge.di

import com.example.weatherchallenge.data.Constants
import com.example.weatherchallenge.data.WeatherService
import com.example.weatherchallenge.repositories.IWeatherRepository
import com.example.weatherchallenge.repositories.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WeatherApiServiceModule {

    @Singleton
    @Provides
    fun providesWeatherApiService(): WeatherService {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(WeatherService::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object WeatherRepositoryModule {
    @Provides
    fun provideWeatherRepository(
        weatherService: WeatherService
    ): IWeatherRepository {
        return WeatherRepository(weatherService)
    }
}
