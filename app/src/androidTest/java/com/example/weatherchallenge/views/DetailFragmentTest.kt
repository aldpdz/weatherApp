package com.example.weatherchallenge.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherchallenge.R
import com.example.weatherchallenge.data.models.remote.Main
import com.example.weatherchallenge.data.models.remote.Weather
import com.example.weatherchallenge.data.models.remote.WeatherData
import com.example.weatherchallenge.di.WeatherRepositoryModule
import com.example.weatherchallenge.repositories.IWeatherRepository
import com.example.weatherchallenge.utils.Result
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@MediumTest
@HiltAndroidTest
@UninstallModules(WeatherRepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class DetailFragmentTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var weatherRepository: IWeatherRepository

    @Before
    fun init() { hiltRule.inject() }

    private val listWeatherData = listOf(
        WeatherData(1,
            Main(75.5f, 78.4f),
            listOf(Weather(1, "cloudy", "cloud"))),
        WeatherData(2,
            Main(76.6f, 79.5f),
            listOf(Weather(1, "sunny", "sun")))
    )

    @Test
    fun showDetailWeatherData() {
        // Given a valid location
        weatherRepository = Mockito.mock(IWeatherRepository::class.java)
        runBlocking {
            Mockito.`when`(weatherRepository
                .getWeatherConditions(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Result.Success(listWeatherData))
        }

        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.et_city)).perform(ViewActions.replaceText("london"))
        onView(withId(R.id.btn_lookup)).perform(ViewActions.click())

        // When selecting an aircraft
        onView(withId(R.id.rv_weather_data))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<WeatherAdapter.ViewHolder>(
                    1, ViewActions.click()
                )
            )

        onView(withId(R.id.tv_temp_detail)).check(matches(withText("76.6")))
        onView(withId(R.id.tv_feels_like)).check(matches(withText("79.5")))
        onView(withId(R.id.tv_main)).check(matches(withText("sunny")))
        onView(withId(R.id.tv_description)).check(matches(withText("sun")))
    }

}