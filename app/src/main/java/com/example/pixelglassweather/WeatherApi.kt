package com.example.pixelglassweather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
 @GET("v1/forecast")
 suspend fun forecast(
  @Query("latitude") lat: Double, @Query("longitude") lon: Double,
  @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m",
  @Query("hourly") hourly: String = "temperature_2m,precipitation_probability",
  @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset,uv_index_max",
  @Query("timezone") timezone: String = "auto", @Query("forecast_days") days: Int = 7
 ): WeatherResponse
}
object WeatherService {
 val api: WeatherApi = Retrofit.Builder().baseUrl("https://api.open-meteo.com/")
  .addConverterFactory(GsonConverterFactory.create()).build().create(WeatherApi::class.java)
}
