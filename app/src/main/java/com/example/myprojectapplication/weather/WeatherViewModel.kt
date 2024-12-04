package com.example.myprojectapplication.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprojectapplication.forecast.ForecastResponse
import com.example.myprojectapplication.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    private val _forecastData = MutableLiveData<ForecastResponse?>()

    val weatherData: LiveData<WeatherResponse?> = _weatherData
    val forecastData: LiveData<ForecastResponse?> = _forecastData

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, apiKey)
                _weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchForecast(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getForecast(city, apiKey)
                _forecastData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}