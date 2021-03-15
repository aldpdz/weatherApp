package com.example.weatherchallenge.repositories

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherchallenge.data.WeatherService
import com.example.weatherchallenge.data.models.remote.Content
import com.example.weatherchallenge.data.models.remote.Main
import com.example.weatherchallenge.data.models.remote.Weather
import com.example.weatherchallenge.data.models.remote.WeatherData
import com.example.weatherchallenge.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.annotation.Config
import retrofit2.Response

@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest {

    private val listWeatherData = listOf(
        WeatherData(1,
            Main(75.5f, 78.4f),
            listOf(Weather(1, "cloudy", "cloud"))),
        WeatherData(2,
            Main(76.6f, 79.5f),
            listOf(Weather(1, "cloudy", "cloud")))
    )

    @Test
    fun getWeatherConditions_Success() = runBlockingTest {
        // Given a good location
        val response = Content("200", "", listWeatherData)
        val fakeWeatherService = mock(WeatherService::class.java)
        `when`(fakeWeatherService.getWeatherConditions(anyString(), anyString()))
            .thenReturn(Response.success(response))

        val weatherRepository = WeatherRepository(fakeWeatherService)

        // When the user look for a location
        val result = weatherRepository.getWeatherConditions(anyString(), anyString())
        result as Result.Success

        // Then the weather condition is retrieved
        assertThat(result.data, IsEqual(response.list))
    }

    @Test
    fun getWeatherConditions_Filed_Error() = runBlockingTest{
        // Given a bad location
        val response = Content("404", "City not found", emptyList())
        val fakeWeatherService = mock(WeatherService::class.java)
        `when`(fakeWeatherService.getWeatherConditions(anyString(), anyString()))
            .thenReturn(Response.success(response))

        val weatherRepository = WeatherRepository(fakeWeatherService)

        // When the user look for a location
        val result = weatherRepository.getWeatherConditions(anyString(), anyString())
        result as Result.Error

        // Then an message is shown
        assertThat(result, instanceOf(Result.Error::class.java))
    }

    @Test
    fun getWeatherConditions_SomethingElse() = runBlockingTest {
        // Given a different code
        val response = Content("403", "City not found", emptyList())
        val fakeWeatherService = mock(WeatherService::class.java)
        `when`(fakeWeatherService.getWeatherConditions(anyString(), anyString()))
            .thenReturn(Response.success(response))

        val weatherRepository = WeatherRepository(fakeWeatherService)

        // When the user look for a location
        val result = weatherRepository.getWeatherConditions(anyString(), anyString())
        result as Result.Error

        // Then we get an Error instance
        assertThat(result, instanceOf(Result.Error::class.java))
    }

    @Test
    fun getWeatherConditions_Connection() = runBlockingTest{
        // Given bad connection
        val response = Response
            .error<Content>(400, "{}"
                .toResponseBody("application/json"
                    .toMediaTypeOrNull()))

        val fakeWeatherService = mock(WeatherService::class.java)
        `when`(fakeWeatherService.getWeatherConditions(anyString(), anyString()))
            .thenReturn(response)

        val weatherRepository = WeatherRepository(fakeWeatherService)

        // When the user look for a location
        val result = weatherRepository.getWeatherConditions(anyString(), anyString())
        result as Result.Error

        // Then we get an Error instance
        assertThat(result, instanceOf(Result.Error::class.java))
    }
}