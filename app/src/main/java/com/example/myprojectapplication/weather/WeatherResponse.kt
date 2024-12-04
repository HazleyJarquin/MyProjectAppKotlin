package com.example.myprojectapplication.weather

data class WeatherResponse (
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Float,
    val humidity: Int
)

data class Weather (
    val description: String,
    val icon: String
)