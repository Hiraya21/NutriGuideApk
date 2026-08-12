package com.example.domain.models

enum class WeatherRiskLevel {
    HIGH_DANGER,   // Heavy Rain (>20mm) - Severe Leaching / Runoff Risk
    WARNING,       // Extreme Heat (>35°C) or High Winds - Volatilization / Drift Risk
    CAUTION,       // Moderate Rain (5-20mm) - Reduced efficiency
    OPTIMAL        // Mild / Clear (22-32°C, <5mm rain) - Ideal Application Window
}

enum class WeatherScenario {
    LIVE_GPS,      // Real-time GPS or Open-Meteo REST API
    HEAVY_RAIN,    // 38mm Heavy Rain Expected (Typhoon / Monsoon)
    EXTREME_HEAT,  // 37.5°C Heatwave Alert
    HIGH_WINDS,    // 28 km/h Strong Winds
    OPTIMAL_CLEAR  // 27°C Sunny with Light Breeze
}

data class FertilizerAdvisory(
    val riskLevel: WeatherRiskLevel,
    val title: String,
    val summary: String,
    val ureaAdvice: String,
    val npkAdvice: String,
    val actionStep: String,
    val bestApplicationWindow: String
)

data class DailyForecastDay(
    val dateLabel: String,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationMm: Double,
    val rainProbPercent: Int,
    val weatherCode: Int,
    val condition: String
)

data class FarmWeatherData(
    val locationName: String = "Nueva Ecija (Rice Granary)",
    val lat: Double = 15.4827,
    val lng: Double = 120.9723,
    val currentTempC: Double = 31.5,
    val maxTempC: Double = 33.0,
    val minTempC: Double = 24.5,
    val relativeHumidity: Int = 78,
    val precipitationSumMm: Double = 2.5,
    val precipitationProbPercent: Int = 20,
    val windSpeedKmh: Double = 12.0,
    val weatherCondition: String = "Partly Cloudy ⛅",
    val aqiIndex: Int = 41,
    val dailyForecast: List<DailyForecastDay> = emptyList(),
    val isLiveApi: Boolean = false,
    val advisory: FertilizerAdvisory = calculateFertilizerAdvisory(
        precipitationSumMm = 2.5,
        maxTempC = 33.0,
        windSpeedKmh = 12.0
    )
)

fun calculateFertilizerAdvisory(
    precipitationSumMm: Double,
    maxTempC: Double,
    windSpeedKmh: Double
): FertilizerAdvisory {
    return when {
        precipitationSumMm >= 20.0 -> {
            FertilizerAdvisory(
                riskLevel = WeatherRiskLevel.HIGH_DANGER,
                title = "⚠️ HEAVY RAINFALL WARNING: DELAY FERTILIZER APPLICATION",
                summary = "Heavy rainfall (${String.format("%.1f", precipitationSumMm)} mm) forecast in the next 24-48 hours. Applying fertilizer now will cause severe nutrient leaching and runoff.",
                ureaAdvice = "🔴 Do NOT apply Urea. High risk of nitrogen washing away into drainage canals.",
                npkAdvice = "🔴 Postpone Complete 14-14-14 / DAP basal application. Wait until field water stabilizes.",
                actionStep = "Delay application by 48-72 hours. Inspect field drainage bunds and retain paddy water.",
                bestApplicationWindow = "Post-rain window (In 2 to 3 days after fields drain)"
            )
        }
        maxTempC >= 35.0 -> {
            FertilizerAdvisory(
                riskLevel = WeatherRiskLevel.WARNING,
                title = "🌡️ EXTREME HEAT ALERT: HIGH VOLATILIZATION RISK",
                summary = "Extreme temperature peak (${String.format("%.1f", maxTempC)}°C) detected. High temperatures accelerate Urea ammonia gas loss into the air.",
                ureaAdvice = "⚠️ Avoid topdressing Urea during peak sunshine hours (10 AM - 3 PM).",
                npkAdvice = "🟡 Incorporate NPK fertilizers into soil or apply with light standing irrigation water.",
                actionStep = "Apply fertilizer strictly during early morning (6-8 AM) or late afternoon (4-6 PM) to prevent crop scorching and nitrogen gas loss.",
                bestApplicationWindow = "Early Morning (6:00 AM - 8:30 AM) or Late Evening"
            )
        }
        windSpeedKmh >= 25.0 -> {
            FertilizerAdvisory(
                riskLevel = WeatherRiskLevel.WARNING,
                title = "💨 HIGH WIND ALERT: DRIFT & UNEVEN SPREAD",
                summary = "Strong wind gusts (${String.format("%.1f", windSpeedKmh)} km/h). Granular and foliar sprays will suffer from uneven field distribution.",
                ureaAdvice = "⚠️ Avoid foliar nitrogen spray or fine granular broadcasting in open windy fields.",
                npkAdvice = "🟡 Apply basal granules directly close to soil surface or flood-water line.",
                actionStep = "Use broadcast shield or reschedule foliar liquid fertilizer spraying to calm morning hours.",
                bestApplicationWindow = "Calm Morning / Low Wind Window (< 15 km/h)"
            )
        }
        precipitationSumMm in 8.0..19.9 -> {
            FertilizerAdvisory(
                riskLevel = WeatherRiskLevel.CAUTION,
                title = "🌧️ MODERATE RAIN EXPECTED: PROCEED WITH CAUTION",
                summary = "Moderate rainfall (${String.format("%.1f", precipitationSumMm)} mm) expected. Light rain can assist nutrient dissolution, but excess water risks runoff.",
                ureaAdvice = "🟡 Apply Urea only if paddy water level is controlled (< 3cm standing water).",
                npkAdvice = "🟢 Suitable for basal soil incorporation prior to harrowing.",
                actionStep = "Ensure field spillways are closed before broadcasting to trap dissolved nutrients.",
                bestApplicationWindow = "Immediate (Ensure field bunds are closed)"
            )
        }
        else -> {
            FertilizerAdvisory(
                riskLevel = WeatherRiskLevel.OPTIMAL,
                title = "✅ OPTIMAL WEATHER WINDOW FOR FERTILIZER APPLICATION",
                summary = "Favorable weather conditions (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm rain). Excellent condition for basal and topdress fertilizer efficiency.",
                ureaAdvice = "🟢 Ideal for Urea topdressing (21 DAT / Panicle Initiation).",
                npkAdvice = "🟢 Optimal for Complete 14-14-14 / DAP application.",
                actionStep = "Proceed with scheduled fertilizer application. Maintain 2-3cm standing water in rice paddies.",
                bestApplicationWindow = "Today & Next 48 Hours (Optimal Field Conditions)"
            )
        }
    }
}
