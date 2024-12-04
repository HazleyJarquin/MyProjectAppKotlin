package com.example.myprojectapplication.forecast

import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): ForecastResponse
}