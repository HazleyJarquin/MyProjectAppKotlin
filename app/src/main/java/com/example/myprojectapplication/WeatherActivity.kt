package com.example.myprojectapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myprojectapplication.databinding.ActivityWeatherBinding
import com.example.myprojectapplication.weather.WeatherViewModel

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.fetchWeather(intent.extras?.getString("City") ?: "Managua", "b37458401b415a652387e52d4883efb4")
        viewModel.weatherData.observe(this, Observer { weather ->
            weather?.let {
                binding.temperatureTextView.text = "${it.main.temp}°C"
                binding.descriptionTextView.text = it.weather[0].description
                binding.cityNameTextView.text = it.name
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

}