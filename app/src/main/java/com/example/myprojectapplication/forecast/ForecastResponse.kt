package com.example.myprojectapplication.forecast

import com.example.myprojectapplication.weather.Main
import com.example.myprojectapplication.weather.Weather

data class ForecastResponse(
    val cod: String,
    val message: String,
    val cnt: Int,
    val list: List<ListF>
)

data class ListF(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
)

