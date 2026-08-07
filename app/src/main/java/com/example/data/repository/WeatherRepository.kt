package com.example.data.repository

import com.example.domain.models.FarmWeatherData
import com.example.domain.models.WeatherScenario
import com.example.domain.models.calculateFertilizerAdvisory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AgriculturalRegion(
    val name: String,
    val province: String,
    val lat: Double,
    val lng: Double,
    val defaultTempC: Double,
    val defaultRainMm: Double,
    val defaultWindKmh: Double,
    val condition: String
)

class WeatherRepository {

    val defaultRegions = listOf(
        AgriculturalRegion("Nueva Ecija (Rice Granary)", "Central Luzon", 15.4827, 120.9723, 31.5, 2.5, 12.0, "Partly Cloudy"),
        AgriculturalRegion("Isabela (Corn & Rice Hub)", "Cagayan Valley", 16.9754, 121.8107, 34.0, 1.0, 10.0, "Mostly Sunny"),
        AgriculturalRegion("Pangasinan (Northern Plains)", "Ilocos Region", 15.8920, 120.2810, 32.8, 3.2, 14.0, "Sun & Clouds"),
        AgriculturalRegion("Iloilo (Panay Basin)", "Western Visayas", 10.7202, 122.5621, 30.5, 18.0, 16.0, "Scattered Rain Showers"),
        AgriculturalRegion("Camarines Sur (Bicol Delta)", "Bicol Region", 13.6218, 123.1948, 29.0, 28.5, 22.0, "Heavy Tropical Rain"),
        AgriculturalRegion("Leyte (Eastern Visayas)", "Eastern Visayas", 11.2404, 124.9990, 30.0, 12.5, 18.0, "Light Rain"),
        AgriculturalRegion("Bukidnon (Highland Agriculture)", "Northern Mindanao", 8.1565, 125.1278, 27.5, 4.5, 8.0, "Cool & Cloudy"),
        AgriculturalRegion("Davao del Norte (Agri Zone)", "Davao Region", 7.4473, 125.8086, 33.2, 1.5, 9.0, "Warm & Clear")
    )

    suspend fun fetchWeatherForLocation(
        lat: Double,
        lng: Double,
        locationName: String = "GPS Field Location",
        scenario: WeatherScenario = WeatherScenario.LIVE_GPS
    ): FarmWeatherData = withContext(Dispatchers.IO) {

        // Check if manual scenario override is active
        when (scenario) {
            WeatherScenario.HEAVY_RAIN -> {
                val rain = 38.5
                val temp = 27.0
                val wind = 24.0
                return@withContext FarmWeatherData(
                    locationName = "$locationName (Simulated Heavy Rain)",
                    lat = lat,
                    lng = lng,
                    currentTempC = temp,
                    maxTempC = 28.0,
                    minTempC = 23.5,
                    relativeHumidity = 92,
                    precipitationSumMm = rain,
                    precipitationProbPercent = 95,
                    windSpeedKmh = wind,
                    weatherCondition = "Heavy Rain & Thunderstorms ⛈️",
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.EXTREME_HEAT -> {
                val rain = 0.0
                val temp = 37.8
                val wind = 11.0
                return@withContext FarmWeatherData(
                    locationName = "$locationName (Simulated Heatwave)",
                    lat = lat,
                    lng = lng,
                    currentTempC = temp,
                    maxTempC = 38.5,
                    minTempC = 26.0,
                    relativeHumidity = 52,
                    precipitationSumMm = rain,
                    precipitationProbPercent = 5,
                    windSpeedKmh = wind,
                    weatherCondition = "Extreme Heatwave 🌡️",
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.HIGH_WINDS -> {
                val rain = 1.2
                val temp = 31.0
                val wind = 29.5
                return@withContext FarmWeatherData(
                    locationName = "$locationName (Simulated High Winds)",
                    lat = lat,
                    lng = lng,
                    currentTempC = temp,
                    maxTempC = 32.5,
                    minTempC = 24.0,
                    relativeHumidity = 70,
                    precipitationSumMm = rain,
                    precipitationProbPercent = 25,
                    windSpeedKmh = wind,
                    weatherCondition = "Strong Gusty Winds 💨",
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.OPTIMAL_CLEAR -> {
                val rain = 0.5
                val temp = 28.5
                val wind = 10.0
                return@withContext FarmWeatherData(
                    locationName = "$locationName (Optimal Weather)",
                    lat = lat,
                    lng = lng,
                    currentTempC = temp,
                    maxTempC = 30.0,
                    minTempC = 23.0,
                    relativeHumidity = 72,
                    precipitationSumMm = rain,
                    precipitationProbPercent = 10,
                    windSpeedKmh = wind,
                    weatherCondition = "Sunny & Clear Skies 🌤️",
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.LIVE_GPS -> {
                // Try live query to Open-Meteo REST API
                try {
                    val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max&timezone=Asia%2FManila"
                    val url = URL(urlString)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.connectTimeout = 4000
                    conn.readTimeout = 4000

                    if (conn.responseCode == 200) {
                        val stream = conn.inputStream.bufferedReader().use { it.readText() }
                        val json = JSONObject(stream)

                        val currentObj = json.optJSONObject("current")
                        val dailyObj = json.optJSONObject("daily")

                        val tempC = currentObj?.optDouble("temperature_2m", 31.0) ?: 31.0
                        val humidity = currentObj?.optInt("relative_humidity_2m", 75) ?: 75
                        val currRain = currentObj?.optDouble("precipitation", 0.0) ?: 0.0
                        val windKmh = currentObj?.optDouble("wind_speed_10m", 12.0) ?: 12.0

                        val maxTemp = dailyObj?.optJSONArray("temperature_2m_max")?.optDouble(0, tempC + 2.0) ?: (tempC + 2.0)
                        val minTemp = dailyObj?.optJSONArray("temperature_2m_min")?.optDouble(0, tempC - 5.0) ?: (tempC - 5.0)
                        val precipSum = dailyObj?.optJSONArray("precipitation_sum")?.optDouble(0, currRain) ?: currRain
                        val precipProb = dailyObj?.optJSONArray("precipitation_probability_max")?.optInt(0, 20) ?: 20

                        val condition = when {
                            precipSum >= 20.0 -> "Heavy Rain Forecast"
                            precipSum >= 5.0 -> "Rain Showers Expected"
                            maxTemp >= 35.0 -> "Extreme Heat"
                            windKmh >= 25.0 -> "Strong Winds"
                            else -> "Partly Cloudy / Fair"
                        }

                        return@withContext FarmWeatherData(
                            locationName = locationName,
                            lat = lat,
                            lng = lng,
                            currentTempC = tempC,
                            maxTempC = maxTemp,
                            minTempC = minTemp,
                            relativeHumidity = humidity,
                            precipitationSumMm = precipSum,
                            precipitationProbPercent = precipProb,
                            windSpeedKmh = windKmh,
                            weatherCondition = condition,
                            advisory = calculateFertilizerAdvisory(precipSum, maxTemp, windKmh)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Fallback to matched region or standard values if offline
                val region = defaultRegions.find { it.name == locationName }
                val rain = region?.defaultRainMm ?: 2.5
                val temp = region?.defaultTempC ?: 31.5
                val wind = region?.defaultWindKmh ?: 12.0
                val condition = region?.condition ?: "Partly Cloudy"

                return@withContext FarmWeatherData(
                    locationName = locationName,
                    lat = lat,
                    lng = lng,
                    currentTempC = temp,
                    maxTempC = temp + 2.0,
                    minTempC = temp - 6.0,
                    relativeHumidity = 78,
                    precipitationSumMm = rain,
                    precipitationProbPercent = if (rain > 15) 85 else 20,
                    windSpeedKmh = wind,
                    weatherCondition = condition,
                    advisory = calculateFertilizerAdvisory(rain, temp + 2.0, wind)
                )
            }
        }
    }
}
