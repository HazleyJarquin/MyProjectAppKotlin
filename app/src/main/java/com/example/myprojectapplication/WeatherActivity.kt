package com.example.myprojectapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.example.myprojectapplication.adapters.ForecastAdapter
import com.example.myprojectapplication.adapters.ForecastItem
import com.example.myprojectapplication.databinding.ActivityWeatherBinding
import com.example.myprojectapplication.weather.WeatherViewModel
import java.util.Calendar

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layout = findViewById<ConstraintLayout>(R.id.bgWeather)

        updateBackgroundBasedOnTime(layout)

        viewModel.fetchWeather(intent.extras?.getString("City") ?: "Managua", "b37458401b415a652387e52d4883efb4")
        viewModel.fetchForecast(intent.extras?.getString("City") ?: "Managua", "b37458401b415a652387e52d4883efb4")
        viewModel.weatherData.observe(this, Observer { weather ->
            weather?.let {
                binding.temperatureTextView.text = "${it.main.temp}°C"
                binding.descriptionTextView.text = it.weather[0].description
                binding.cityNameTextView.text = it.name
                binding.dateTextView.text = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(it.dt * 1000))
            }
        })

        viewModel.forecastData.observe(this, Observer { weather ->

            weather?.let {

                val forecastItems = it.list.map { forecast ->
                    val weatherIconUrl = "https://openweathermap.org/img/wn/${forecast.weather[0].icon}@2x.png"
                    ForecastItem(
                        date = java.text.SimpleDateFormat(
                            "dd MMM yyyy, HH:mm",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(forecast.dt * 1000)),
                        temp = "${forecast.main.temp}°C",
                        description = forecast.weather[0].description,
                        iconUrl = weatherIconUrl
                    )
                }

                val adapter = ForecastAdapter(forecastItems)
                binding.forecastViewPager.adapter = adapter
            }
        })


        binding.apply {
            cityEditText.setText(intent.extras?.getString("City") ?: "Managua")
            searchButton.setOnClickListener {
                val city = cityEditText.text.toString()
                viewModel.fetchWeather(city, "b37458401b415a652387e52d4883efb4")
            }

        }

    }
    private fun updateBackgroundBasedOnTime(layout: ConstraintLayout) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val isDayTime = hour in 6..18
        if (isDayTime) {
            layout.setBackgroundResource(R.drawable.bg_weather_day)
        } else {
            layout.setBackgroundResource(R.drawable.weather_background)
        }
    }

}