package com.example.pixelglassweather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(val current: Current, val hourly: Hourly, val daily: Daily)
data class Current(
 @SerializedName("temperature_2m") val temperature: Double,
 @SerializedName("relative_humidity_2m") val humidity: Int,
 @SerializedName("apparent_temperature") val feelsLike: Double,
 @SerializedName("weather_code") val code: Int,
 @SerializedName("wind_speed_10m") val wind: Double
)
data class Hourly(
 val time: List<String>,
 @SerializedName("temperature_2m") val temperature: List<Double>,
 @SerializedName("precipitation_probability") val rain: List<Int>
)
data class Daily(
 val time: List<String>,
 @SerializedName("temperature_2m_max") val high: List<Double>,
 @SerializedName("temperature_2m_min") val low: List<Double>,
 @SerializedName("precipitation_probability_max") val rain: List<Int>,
 val sunrise: List<String>, val sunset: List<String>,
 @SerializedName("uv_index_max") val uv: List<Double>
)
fun description(code: Int) = when(code) {
 0 -> "Clear sky"; 1 -> "Mostly clear"; 2 -> "Partly cloudy"; 3 -> "Overcast"
 45,48 -> "Foggy"; in 51..57 -> "Drizzle"; in 61..67 -> "Rain"
 in 71..77 -> "Snow"; in 80..82 -> "Rain showers"; in 95..99 -> "Thunderstorm"
 else -> "Current conditions"
}
fun message(code: Int, rain: Int) = when {
 code in 95..99 -> "Stormy outside! Stay safe ⚡"
 rain >= 50 || code in 51..67 || code in 80..82 -> "Take your umbrella ☔ It's raining!"
 code == 0 -> "Beautiful day—go enjoy it! ☀️😉"
 else -> "Fresh air and good vibes 😊"
}
