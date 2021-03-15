package com.example.weatherchallenge.viewmodels

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherchallenge.MainCoroutineRule
import com.example.weatherchallenge.data.models.remote.*
import com.example.weatherchallenge.getOrAwaitValue
import com.example.weatherchallenge.repositories.WeatherRepository
import com.example.weatherchallenge.utils.RemoteStatus
import com.example.weatherchallenge.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.annotation.Config
import java.lang.Exception
import java.lang.NullPointerException

@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SearchViewModelTest{

    private lateinit var searchViewModel: SearchViewModel
    private val context: Context = ApplicationProvider.getApplicationContext()

    private val listWeatherData = listOf(
            WeatherData(1,
                    Main(75.5f, 78.4f),
                    listOf(Weather(1, "cloudy", "cloud"))),
            WeatherData(2,
                    Main(76.6f, 79.5f),
                    listOf(Weather(1, "cloudy", "cloud"))))

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun searchCity_isSuccess() {
        // Given a good location
        val mockWeatherRepository = mock(WeatherRepository::class.java)
        runBlocking {
            `when`(mockWeatherRepository.getWeatherConditions("london", ""))
                    .thenReturn(Result.Success(listWeatherData))
        }

        // Pause dispatcher so you can verify initial values
        mainCoroutineRule.pauseDispatcher()

        searchViewModel = SearchViewModel(context, mockWeatherRepository)
        searchViewModel.searchCity("london")

        assertThat(searchViewModel.networkStatus.getOrAwaitValue(), `is`(RemoteStatus.LOADING))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        val networkStatus = searchViewModel.networkStatus.getOrAwaitValue()
        val weatherData = searchViewModel.weatherData.getOrAwaitValue()
        val navigateToListWeather = searchViewModel.navigateToListWeather.getOrAwaitValue()
        val city = searchViewModel.nameCity.getOrAwaitValue()
        val listWeatherInfo = searchViewModel.listWeatherInfo.getOrAwaitValue()

        assertThat(networkStatus, `is`(RemoteStatus.DONE))
        assertThat(weatherData, IsEqual(listWeatherData))
        assertThat(navigateToListWeather.getContentIfNotHandled(), IsEqual(true))
        assertThat(city , IsEqual("london"))
        assertThat(listWeatherInfo, IsEqual(listWeatherData.toListWeatherInfo()))
        runBlocking {
            verify(mockWeatherRepository).getWeatherConditions(anyString(), anyString())
        }
    }

    @Test
    fun searchCity_Error() {
        // Given an error
        val mockWeatherRepository = mock(WeatherRepository::class.java)
        runBlocking {
            `when`(mockWeatherRepository.getWeatherConditions("", ""))
                .thenReturn(Result.Error(Exception()))
        }

        // Pause dispatcher so you can verify initial values
        mainCoroutineRule.pauseDispatcher()

        searchViewModel = SearchViewModel(context, mockWeatherRepository)
        searchViewModel.searchCity("")

        assertThat(searchViewModel.networkStatus.getOrAwaitValue(), `is`(RemoteStatus.LOADING))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        val message = searchViewModel.showError.getOrAwaitValue()

        // Then the message is shown
        assertThat(message, IsEqual(message))
    }

    @Test
    fun searchCity_Exception() {
        // Given an exception
        val mockWeatherRepository = mock(WeatherRepository::class.java)
        runBlocking {
            `when`(mockWeatherRepository.getWeatherConditions("", ""))
                .thenThrow(NullPointerException())
        }

        // Pause dispatcher so you can verify initial values
        mainCoroutineRule.pauseDispatcher()

        searchViewModel = SearchViewModel(context, mockWeatherRepository)
        searchViewModel.searchCity("")

        assertThat(searchViewModel.networkStatus.getOrAwaitValue(), `is`(RemoteStatus.LOADING))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        val networkStatus = searchViewModel.networkStatus.getOrAwaitValue()

        // The status has an error
        assertThat(networkStatus, `is`(RemoteStatus.ERROR))
    }

    @Test
    fun getDetailWeatherInfo() {
        // Given a good location
        val mockWeatherRepository = mock(WeatherRepository::class.java)
        runBlocking {
            `when`(mockWeatherRepository.getWeatherConditions("", ""))
                .thenReturn(Result.Success(listWeatherData))
        }

        searchViewModel = SearchViewModel(context, mockWeatherRepository)
        searchViewModel.searchCity("")
        searchViewModel.getDetailWeatherInfo(2)

        val detailInfo = searchViewModel.detailInfo.getOrAwaitValue()
        val navigateToDetail = searchViewModel.navigateToDetail.getOrAwaitValue()

        // The detail information is retrieve and navigate to detail is true
        assertThat(detailInfo, IsEqual(listWeatherData.first { it.dt == 2L }.toDetailData()))
        assertThat(navigateToDetail.getContentIfNotHandled(), IsEqual(true))
    }
}