package com.example.weatherapp

data class WeatherItem (
        var city: String = "Springfield",
        var temperatureCelsius: Double = 20.0,
        var humidity: Double = 60.0,
        var weather: String = "Sunny"
        )