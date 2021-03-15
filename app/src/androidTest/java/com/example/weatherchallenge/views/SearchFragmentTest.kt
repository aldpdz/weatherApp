package com.example.weatherchallenge.views

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherchallenge.R
import com.example.weatherchallenge.data.models.remote.Main
import com.example.weatherchallenge.data.models.remote.Weather
import com.example.weatherchallenge.data.models.remote.WeatherData
import com.example.weatherchallenge.di.WeatherRepositoryModule
import com.example.weatherchallenge.launchFragmentInHiltContainer
import com.example.weatherchallenge.repositories.IWeatherRepository
import com.example.weatherchallenge.utils.Result
import com.example.weatherchallenge.withError
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@MediumTest
@HiltAndroidTest
@UninstallModules(WeatherRepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class SearchFragmentTest{
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
            listOf(Weather(1, "cloudy", "cloud")))
    )

    @Test
    fun successWeatherConditions_navigateToListView() {
        // Given a valid location
        weatherRepository = mock(IWeatherRepository::class.java)
        runBlocking {
            `when`(weatherRepository.getWeatherConditions(anyString(), anyString()))
                .thenReturn(Result.Success(listWeatherData))
        }

        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<SearchFragment>(Bundle(), R.style.Theme_WeatherChallenge) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        // When looking for a weather condition
        onView(withId(R.id.et_city)).perform(replaceText("london"))
        onView(withId(R.id.btn_lookup)).perform(click())

        // Then navigates to the list view
        verify(navController).navigate(
            SearchFragmentDirections.actionFirstFragmentToSecondFragment()
        )
    }

    @Test
    fun failWeatherConditions_showValidation() {

        val context = ApplicationProvider.getApplicationContext<Context>()
        val validation = context.getString(R.string.valid_city)

        weatherRepository = mock(IWeatherRepository::class.java)
        launchFragmentInHiltContainer<SearchFragment>(Bundle(), R.style.Theme_WeatherChallenge)

        // When trying to look for an empty string
        onView(withId(R.id.btn_lookup)).perform(click())

        // Then the validation is displayed
        onView(withId(R.id.textInputLayoutCity)).check(matches(withError(validation)))
    }

    @Test
    fun failWeatherConditions_displayMessage() {
        // Given a bad request
        weatherRepository = mock(IWeatherRepository::class.java)
        runBlocking {
            `when`(weatherRepository.getWeatherConditions(anyString(), anyString()))
                .thenReturn(Result.Message("City not found"))
        }

        launchFragmentInHiltContainer<SearchFragment>(Bundle(), R.style.Theme_WeatherChallenge)

        // When looking for a weather condition
        onView(withId(R.id.et_city)).perform(replaceText("london"))
        onView(withId(R.id.btn_lookup)).perform(click())

        // Assert that the message is shown
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(ViewMatchers.withText("City not found")))
    }
}