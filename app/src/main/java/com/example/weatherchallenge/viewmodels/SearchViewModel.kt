package com.example.weatherchallenge.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherchallenge.utils.RemoteStatus
import com.example.weatherchallenge.utils.Result
import com.example.weatherchallenge.data.models.domain.DetailData
import com.example.weatherchallenge.data.models.remote.WeatherData
import com.example.weatherchallenge.data.models.remote.toDetailData
import com.example.weatherchallenge.R
import com.example.weatherchallenge.data.models.remote.toListWeatherInfo
import com.example.weatherchallenge.repositories.IWeatherRepository
import com.example.weatherchallenge.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val app: Context,
    private val weatherRepository: IWeatherRepository): ViewModel() {

    private var key = ""

    private val _networkStatus = MutableLiveData<RemoteStatus>()
    val networkStatus: LiveData<RemoteStatus> get() = _networkStatus

    private val _weatherData = MutableLiveData<List<WeatherData>>()
    val weatherData: LiveData<List<WeatherData>> get() = _weatherData

    val listWeatherInfo = _weatherData.switchMap { weatherData ->
        liveData {
            emit(weatherData.toListWeatherInfo())
        }
    }

    private val _navigateToListWeather = MutableLiveData<Event<Boolean>>()
    val navigateToListWeather: LiveData<Event<Boolean>> get() = _navigateToListWeather

    private val _navigateToDetail = MutableLiveData<Event<Boolean>>()
    val navigateToDetail: LiveData<Event<Boolean>> get() = _navigateToDetail

    private val _detailInfo = MutableLiveData<DetailData>()
    val detailInfo: LiveData<DetailData> get() = _detailInfo

    private val _nameCity = MutableLiveData<String>()
    val nameCity: LiveData<String> get() = _nameCity

    private val _showError = MutableLiveData<Event<String>>()
    val showError: LiveData<Event<String>> get() = _showError

    fun setKey(key: String){
        this.key = key
    }

    fun getDetailWeatherInfo(dt: Long) {
        val weatherDetail = _weatherData.value?.first { it.dt == dt }
        _detailInfo.value = weatherDetail?.toDetailData()
        _navigateToDetail.value = Event(true)
    }

    fun searchCity(city: String) {
        _networkStatus.value = RemoteStatus.LOADING
        _nameCity.value = city
        viewModelScope.launch {
            try {
                val result = weatherRepository.getWeatherConditions(city, key)
                _networkStatus.value = RemoteStatus.DONE

                when(result){
                    is Result.Success -> {
                        result.data.let{ _weatherData.value = it}
                        _navigateToListWeather.value = Event(true)
                    }
                    is Result.Message -> {
                        _showError.value = Event(result.message)
                    }
                    is Result.Error -> {
                        Log.i("Error", result.exception.toString())
                        _showError.value = Event(app.getString(R.string.no_result))
                    }
                }
            }catch (e: Exception){
                Log.i("Error", e.toString())
                _networkStatus.value = RemoteStatus.ERROR
            }
        }
    }
}