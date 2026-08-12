package com.example.data.repository

import com.example.domain.models.DailyForecastDay
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.WeatherScenario
import com.example.domain.models.calculateFertilizerAdvisory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

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
        AgriculturalRegion("Nueva Ecija (Rice Granary)", "Central Luzon", 15.4827, 120.9723, 31.5, 2.5, 12.0, "Partly Cloudy ⛅"),
        AgriculturalRegion("Isabela (Corn & Rice Hub)", "Cagayan Valley", 16.9754, 121.8107, 34.0, 1.0, 10.0, "Mostly Sunny ☀️"),
        AgriculturalRegion("Pangasinan (Northern Plains)", "Ilocos Region", 15.8920, 120.2810, 32.8, 3.2, 14.0, "Sun & Clouds 🌤️"),
        AgriculturalRegion("Iloilo (Panay Basin)", "Western Visayas", 10.7202, 122.5621, 30.5, 18.0, 16.0, "Scattered Rain Showers 🌦️"),
        AgriculturalRegion("Camarines Sur (Bicol Delta)", "Bicol Region", 13.6218, 123.1948, 29.0, 28.5, 22.0, "Heavy Tropical Rain ⛈️"),
        AgriculturalRegion("Leyte (Eastern Visayas)", "Eastern Visayas", 11.2404, 124.9990, 30.0, 12.5, 18.0, "Light Rain 🌧️"),
        AgriculturalRegion("Bukidnon (Highland Agriculture)", "Northern Mindanao", 8.1565, 125.1278, 27.5, 4.5, 8.0, "Cool & Cloudy ☁️"),
        AgriculturalRegion("Davao del Norte (Agri Zone)", "Davao Region", 7.4473, 125.8086, 33.2, 1.5, 9.0, "Warm & Clear ☀️")
    )

    fun mapWmoCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky ☀️"
            1 -> "Mainly Clear 🌤️"
            2 -> "Partly Cloudy ⛅"
            3 -> "Overcast ☁️"
            45, 48 -> "Foggy 🌫️"
            51, 53, 55 -> "Drizzle 🌧️"
            56, 57 -> "Freezing Drizzle 🌨️"
            61 -> "Light Rain 🌧️"
            63 -> "Moderate Rain 🌧️"
            65 -> "Heavy Rain 🌧️"
            66, 67 -> "Freezing Rain 🌨️"
            71, 73, 75, 77 -> "Snow ❄️"
            80 -> "Light Rain Showers 🌦️"
            81 -> "Moderate Rain Showers 🌦️"
            82 -> "Violent Rain Showers ⛈️"
            85, 86 -> "Snow Showers ❄️"
            95 -> "Thunderstorm 🌩️"
            96, 99 -> "Thunderstorm & Hail ⛈️"
            else -> "Partly Cloudy ⛅"
        }
    }

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
                val mockForecast = listOf(
                    DailyForecastDay("Today", 28.0, 23.5, 38.5, 95, 82, "Heavy Rain ⛈️"),
                    DailyForecastDay("Tomorrow", 28.5, 24.0, 25.0, 85, 65, "Heavy Rain 🌧️"),
                    DailyForecastDay("Day 3", 30.0, 24.0, 8.0, 50, 80, "Light Rain 🌦️"),
                    DailyForecastDay("Day 4", 31.5, 24.5, 2.0, 20, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Day 5", 32.0, 25.0, 0.5, 10, 1, "Mainly Clear 🌤️")
                )
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
                    dailyForecast = mockForecast,
                    isLiveApi = false,
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.EXTREME_HEAT -> {
                val rain = 0.0
                val temp = 37.8
                val wind = 11.0
                val mockForecast = listOf(
                    DailyForecastDay("Today", 38.5, 26.0, 0.0, 5, 0, "Clear Sky ☀️"),
                    DailyForecastDay("Tomorrow", 38.0, 26.2, 0.0, 5, 0, "Clear Sky ☀️"),
                    DailyForecastDay("Day 3", 37.5, 25.8, 0.0, 10, 1, "Mainly Clear 🌤️"),
                    DailyForecastDay("Day 4", 36.8, 25.5, 1.0, 15, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Day 5", 35.5, 25.0, 3.0, 25, 2, "Partly Cloudy ⛅")
                )
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
                    dailyForecast = mockForecast,
                    isLiveApi = false,
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.HIGH_WINDS -> {
                val rain = 1.2
                val temp = 31.0
                val wind = 29.5
                val mockForecast = listOf(
                    DailyForecastDay("Today", 32.5, 24.0, 1.2, 25, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Tomorrow", 31.8, 24.2, 2.5, 35, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Day 3", 30.5, 23.8, 5.0, 45, 80, "Light Showers 🌦️"),
                    DailyForecastDay("Day 4", 31.0, 24.0, 1.0, 20, 1, "Mainly Clear 🌤️"),
                    DailyForecastDay("Day 5", 32.0, 24.5, 0.0, 10, 0, "Clear Sky ☀️")
                )
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
                    dailyForecast = mockForecast,
                    isLiveApi = false,
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.OPTIMAL_CLEAR -> {
                val rain = 0.5
                val temp = 28.5
                val wind = 10.0
                val mockForecast = listOf(
                    DailyForecastDay("Today", 30.0, 23.0, 0.5, 10, 1, "Sunny & Clear 🌤️"),
                    DailyForecastDay("Tomorrow", 30.5, 23.2, 0.0, 5, 0, "Clear Sky ☀️"),
                    DailyForecastDay("Day 3", 31.0, 23.5, 0.0, 10, 1, "Mainly Clear 🌤️"),
                    DailyForecastDay("Day 4", 31.2, 24.0, 1.0, 15, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Day 5", 30.8, 23.8, 0.5, 10, 1, "Sunny & Clear 🌤️")
                )
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
                    dailyForecast = mockForecast,
                    isLiveApi = false,
                    advisory = calculateFertilizerAdvisory(rain, temp, wind)
                )
            }
            WeatherScenario.LIVE_GPS -> {
                val resolvedName = resolveExactLocation(lat, lng, locationName)
                val liveAqi = fetchAqi(lat, lng)

                // Try live query to Open-Meteo REST API
                try {
                    val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max&timezone=Asia%2FManila&forecast_days=5"
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
                        val currWmoCode = currentObj?.optInt("weather_code", 2) ?: 2
                        val windKmh = currentObj?.optDouble("wind_speed_10m", 12.0) ?: 12.0

                        val maxTemp = dailyObj?.optJSONArray("temperature_2m_max")?.optDouble(0, tempC + 2.0) ?: (tempC + 2.0)
                        val minTemp = dailyObj?.optJSONArray("temperature_2m_min")?.optDouble(0, tempC - 5.0) ?: (tempC - 5.0)
                        val precipSum = dailyObj?.optJSONArray("precipitation_sum")?.optDouble(0, currRain) ?: currRain
                        val precipProb = dailyObj?.optJSONArray("precipitation_probability_max")?.optInt(0, 20) ?: 20

                        val condition = mapWmoCodeToCondition(currWmoCode)

                        // Parse 5-day daily forecast
                        val dailyForecastList = mutableListOf<DailyForecastDay>()
                        if (dailyObj != null) {
                            val timeArr = dailyObj.optJSONArray("time")
                            val wmoArr = dailyObj.optJSONArray("weather_code")
                            val maxTempArr = dailyObj.optJSONArray("temperature_2m_max")
                            val minTempArr = dailyObj.optJSONArray("temperature_2m_min")
                            val precipSumArr = dailyObj.optJSONArray("precipitation_sum")
                            val precipProbArr = dailyObj.optJSONArray("precipitation_probability_max")

                            val count = timeArr?.length() ?: 0
                            val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val outFormat = SimpleDateFormat("EEE, MMM d", Locale.US)

                            for (i in 0 until minOf(count, 5)) {
                                val rawDateStr = timeArr?.optString(i, "") ?: ""
                                val dateLabel = when (i) {
                                    0 -> "Today"
                                    1 -> "Tomorrow"
                                    else -> {
                                        try {
                                            val parsedDate = inFormat.parse(rawDateStr)
                                            if (parsedDate != null) outFormat.format(parsedDate) else rawDateStr
                                        } catch (e: Exception) {
                                            rawDateStr
                                        }
                                    }
                                }
                                val dayWmo = wmoArr?.optInt(i, 2) ?: 2
                                val dayMaxT = maxTempArr?.optDouble(i, 32.0) ?: 32.0
                                val dayMinT = minTempArr?.optDouble(i, 24.0) ?: 24.0
                                val dayPrecip = precipSumArr?.optDouble(i, 0.0) ?: 0.0
                                val dayProb = precipProbArr?.optInt(i, 10) ?: 10

                                dailyForecastList.add(
                                    DailyForecastDay(
                                        dateLabel = dateLabel,
                                        maxTempC = dayMaxT,
                                        minTempC = dayMinT,
                                        precipitationMm = dayPrecip,
                                        rainProbPercent = dayProb,
                                        weatherCode = dayWmo,
                                        condition = mapWmoCodeToCondition(dayWmo)
                                    )
                                )
                            }
                        }

                        return@withContext FarmWeatherData(
                            locationName = resolvedName,
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
                            aqiIndex = liveAqi,
                            dailyForecast = dailyForecastList,
                            isLiveApi = true,
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
                val condition = region?.condition ?: "Partly Cloudy ⛅"

                val fallbackForecast = listOf(
                    DailyForecastDay("Today", temp + 2.0, temp - 6.0, rain, if (rain > 15) 85 else 20, 2, condition),
                    DailyForecastDay("Tomorrow", temp + 1.5, temp - 5.5, rain * 0.8, if (rain > 15) 70 else 15, 2, "Partly Cloudy ⛅"),
                    DailyForecastDay("Day 3", temp + 2.2, temp - 5.0, 1.0, 10, 1, "Mainly Clear 🌤️"),
                    DailyForecastDay("Day 4", temp + 2.5, temp - 4.8, 0.0, 5, 0, "Clear Sky ☀️"),
                    DailyForecastDay("Day 5", temp + 2.0, temp - 5.2, 0.5, 10, 1, "Sunny 🌤️")
                )

                return@withContext FarmWeatherData(
                    locationName = resolvedName,
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
                    aqiIndex = 41,
                    dailyForecast = fallbackForecast,
                    isLiveApi = false,
                    advisory = calculateFertilizerAdvisory(rain, temp + 2.0, wind)
                )
            }
        }
    }

    private fun resolveExactLocation(lat: Double, lng: Double, defaultName: String): String {
        try {
            val urlStr = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$lat&longitude=$lng&localityLanguage=en"
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            if (conn.responseCode == 200) {
                val stream = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(stream)
                val locality = json.optString("locality", "")
                val city = json.optString("city", "")
                val principalSubdivision = json.optString("principalSubdivision", "")

                val exactName = when {
                    locality.isNotBlank() && city.isNotBlank() && !city.contains(locality, ignoreCase = true) -> "$locality"
                    locality.isNotBlank() -> locality
                    city.isNotBlank() -> city
                    principalSubdivision.isNotBlank() -> principalSubdivision
                    else -> defaultName
                }
                if (exactName.isNotBlank()) return exactName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultName
    }

    private fun fetchAqi(lat: Double, lng: Double): Int {
        try {
            val urlStr = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lng&current=us_aqi"
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 2500
            conn.readTimeout = 2500
            if (conn.responseCode == 200) {
                val stream = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(stream)
                val currObj = json.optJSONObject("current")
                return currObj?.optInt("us_aqi", 41) ?: 41
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 41
    }
}
