package com.example.do_an.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object WeatherRepository {
    private const val API_KEY = "666eb450f1c549ced70ca5a87331e956"

    suspend fun fetchWeather(cityName: String): Pair<JSONObject?, String?> {
        return try {
            val currentResponse = withContext(Dispatchers.IO) {
                URL("https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY&units=metric&lang=vi").readText()
            }
            val currentJson = JSONObject(currentResponse)

            if (currentJson.optInt("cod", 0) == 200) {
                Pair(currentJson, null)
            } else {
                Pair(null, "Không tìm thấy thành phố này.")
            }
        } catch (e: Exception) {
            Pair(null, "Lỗi khi tải dữ liệu: ${e.localizedMessage}")
        }
    }

    suspend fun fetchForecast(cityName: String): Pair<List<JSONObject>, String?> {
        return try {
            val forecastResponse = withContext(Dispatchers.IO) {
                URL("https://api.openweathermap.org/data/2.5/forecast?q=$cityName&appid=$API_KEY&units=metric&lang=vi").readText()
            }
            val forecastJson = JSONObject(forecastResponse)

            if (forecastJson.optString("cod") == "200") {
                val list = forecastJson.getJSONArray("list")
                val forecasts = mutableListOf<JSONObject>()
                for (i in 0 until list.length()) {
                    forecasts.add(list.getJSONObject(i))
                }
                Pair(forecasts, null)
            } else {
                Pair(emptyList(), "Không lấy được dự báo thời tiết.")
            }
        } catch (e: Exception) {
            Pair(emptyList(), "Lỗi khi tải dữ liệu dự báo: ${e.localizedMessage}")
        }
    }
}
