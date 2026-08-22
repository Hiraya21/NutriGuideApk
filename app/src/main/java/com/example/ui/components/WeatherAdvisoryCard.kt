package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AgriculturalRegion
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.WeatherRiskLevel
import com.example.domain.models.WeatherScenario
import com.example.domain.models.localizeDateLabel
import com.example.domain.models.localizeWeatherCondition
import com.example.util.NotificationHelper

@Composable
fun WeatherAdvisoryCard(
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    weatherData: FarmWeatherData,
    regions: List<AgriculturalRegion>,
    selectedRegion: AgriculturalRegion,
    selectedScenario: WeatherScenario,
    onRegionSelected: (AgriculturalRegion) -> Unit,
    onScenarioSelected: (WeatherScenario) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var optionsMenuExpanded by remember { mutableStateOf(false) }
    var regionDropdownExpanded by remember { mutableStateOf(false) }
    var showScenarioControls by remember { mutableStateOf(false) }
    var showMoreDetails by remember { mutableStateOf(false) }

    val adv = weatherData.advisory

    // Localized condition text
    val localizedCondition = localizeWeatherCondition(weatherData.weatherCondition, currentLanguage)

    // Sky gradient background
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF427ECF),
            Color(0xFF5D95DE),
            Color(0xFF82B0EB),
            Color(0xFFA5CAFA)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_weather_advisory"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(skyGradient)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Bar: Add/Region, Exact Location Title, Options Menu (⋮)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Quick Region Switcher
                    IconButton(
                        onClick = { regionDropdownExpanded = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Select Region",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Center: Exact Location Name
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clickable { regionDropdownExpanded = true }
                    ) {
                        Text(
                            text = weatherData.locationName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (weatherData.isLiveApi) Color(0xFF69F0AE) else Color(0xFFFFD54F))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (weatherData.isLiveApi) "GPS Live Forecast" else "Simulated Forecast",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Right: Options Menu (⋮)
                    Box {
                        IconButton(
                            onClick = { optionsMenuExpanded = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Weather Options",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = optionsMenuExpanded,
                            onDismissRequest = { optionsMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            when (currentLanguage) {
                                                AppLanguage.TAGALOG -> "I-refresh ang Panahon"
                                                AppLanguage.ILOCANO -> "Pabaroen ti Panawen"
                                                AppLanguage.CEBUANO -> "I-refresh ang Panahon"
                                                else -> "Refresh Forecast"
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    optionsMenuExpanded = false
                                    onRefresh()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            when (currentLanguage) {
                                                AppLanguage.TAGALOG -> "Pumili ng Rehiyon"
                                                AppLanguage.ILOCANO -> "Pilien ti Rehiyon"
                                                AppLanguage.CEBUANO -> "Pilia ang Rehiyon"
                                                else -> "Choose Region"
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    optionsMenuExpanded = false
                                    regionDropdownExpanded = true
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57C00), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (showScenarioControls) "Itago ang Simulators" else "Simulate Scenarios"
                                        )
                                    }
                                },
                                onClick = {
                                    optionsMenuExpanded = false
                                    showScenarioControls = !showScenarioControls
                                }
                            )
                        }

                        DropdownMenu(
                            expanded = regionDropdownExpanded,
                            onDismissRequest = { regionDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "📍 ${if (currentLanguage == AppLanguage.TAGALOG) "Kasalukuyang Lokasyon ng GPS" else "Current GPS Location"}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                },
                                onClick = {
                                    onScenarioSelected(WeatherScenario.LIVE_GPS)
                                    regionDropdownExpanded = false
                                }
                            )
                            HorizontalDivider()
                            regions.forEach { reg ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(reg.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${reg.province} • Temp ${reg.defaultTempC}°C", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        onRegionSelected(reg)
                                        regionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Large Temperature Display (matching reference image style)
                Text(
                    text = "${weatherData.currentTempC.toInt()} °C",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 3. Condition + Max/Min Temperatures
                Text(
                    text = "$localizedCondition ${weatherData.maxTempC.toInt()}°/${weatherData.minTempC.toInt()}°",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.95f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. AQI Chip Pill (matching reference image)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color(0xFF69F0AE),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "AQI ${weatherData.aqiIndex}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (weatherData.aqiIndex <= 50) "• Good" else "• Moderate",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. 5-Day Forecast Card (Frosted Glass Panel style from reference image)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header: 🕒 5-day forecast  |  More details ▶
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMoreDetails = !showMoreDetails },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val forecastHeaderTitle = when (currentLanguage) {
                                    AppLanguage.TAGALOG -> "5-araw na taya ng panahon"
                                    AppLanguage.TAGLISH -> "5-day forecast"
                                    AppLanguage.ILOCANO -> "5-aldaw a panawen"
                                    AppLanguage.CEBUANO -> "5-ka adlaw nga forecast"
                                    else -> "5-day forecast"
                                }
                                Text(
                                    text = forecastHeaderTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val moreDetailsLabel = when (currentLanguage) {
                                    AppLanguage.TAGALOG -> if (showMoreDetails) "Itago" else "Mga detalye"
                                    AppLanguage.ILOCANO -> if (showMoreDetails) "Ilemmeng" else "Detalyado"
                                    AppLanguage.CEBUANO -> if (showMoreDetails) "Itago" else "Dugang detalye"
                                    else -> if (showMoreDetails) "Less details" else "More details"
                                }
                                Text(
                                    text = moreDetailsLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = if (showMoreDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5-Day Forecast list rows
                        val forecastDays = if (weatherData.dailyForecast.isNotEmpty()) {
                            weatherData.dailyForecast
                        } else {
                            listOf(
                                com.example.domain.models.DailyForecastDay("Today", weatherData.maxTempC, weatherData.minTempC, weatherData.precipitationSumMm, weatherData.precipitationProbPercent, 2, weatherData.weatherCondition),
                                com.example.domain.models.DailyForecastDay("Tomorrow", weatherData.maxTempC - 1, weatherData.minTempC - 1, 1.0, 30, 95, "Thunderstorm 🌩️"),
                                com.example.domain.models.DailyForecastDay("Fri", weatherData.maxTempC - 1, weatherData.minTempC - 1, 4.0, 60, 61, "Light rain 🌧️"),
                                com.example.domain.models.DailyForecastDay("Sat", weatherData.maxTempC, weatherData.minTempC, 2.0, 40, 80, "Scattered rain 🌦️"),
                                com.example.domain.models.DailyForecastDay("Sun", weatherData.maxTempC + 1, weatherData.minTempC, 0.5, 15, 1, "Mainly clear 🌤️")
                            )
                        }

                        forecastDays.take(5).forEachIndexed { index, day ->
                            val dateLabel = localizeDateLabel(day.dateLabel, currentLanguage)
                            val conditionLabel = localizeWeatherCondition(day.condition, currentLanguage)

                            val iconEmoji = when {
                                day.condition.contains("Thunderstorm", ignoreCase = true) -> "⛈️"
                                day.condition.contains("Heavy", ignoreCase = true) || day.condition.contains("Rain", ignoreCase = true) -> "🌧️"
                                day.condition.contains("Showers", ignoreCase = true) -> "🌦️"
                                day.condition.contains("Cloudy", ignoreCase = true) || day.condition.contains("Overcast", ignoreCase = true) -> "⛅"
                                else -> "☀️"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = iconEmoji,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateLabel,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.width(75.dp)
                                    )
                                    Text(
                                        text = conditionLabel.replace(Regex("[^\\p{L}\\p{Nd}\\s/]"), "").trim(),
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = "${day.maxTempC.toInt()}° / ${day.minTempC.toInt()}°",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            if (index < 4) {
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.15f),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // 6. Expandable Detailed Metrics & Agricultural Fertilizer Advisory
                AnimatedVisibility(visible = showMoreDetails) {
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        // Weather Metrics Grid (White card)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${String.format("%.1f", weatherData.precipitationSumMm)} mm", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text("${if (currentLanguage == AppLanguage.TAGALOG) "Peligro ng Ulan" else "Rain Risk"} ${weatherData.precipitationProbPercent}%", fontSize = 10.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Air, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${weatherData.windSpeedKmh.toInt()} km/h", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text("${if (currentLanguage == AppLanguage.TAGALOG) "Halumigmig" else "Humidity"} ${weatherData.relativeHumidity}%", fontSize = 10.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${weatherData.currentTempC.toInt()}°C", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text("Max ${weatherData.maxTempC.toInt()}°C", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Agricultural Fertilizer Safety Advisory
                        val alertBg = when (adv.riskLevel) {
                            WeatherRiskLevel.HIGH_DANGER -> Color(0xFFFFEBEE)
                            WeatherRiskLevel.WARNING -> Color(0xFFFFF3E0)
                            WeatherRiskLevel.CAUTION -> Color(0xFFFFFDE7)
                            WeatherRiskLevel.OPTIMAL -> Color(0xFFE8F5E9)
                        }
                        val alertBorder = when (adv.riskLevel) {
                            WeatherRiskLevel.HIGH_DANGER -> Color(0xFFE53935)
                            WeatherRiskLevel.WARNING -> Color(0xFFFB8C00)
                            WeatherRiskLevel.CAUTION -> Color(0xFFFDD835)
                            WeatherRiskLevel.OPTIMAL -> Color(0xFF43A047)
                        }
                        val alertText = when (adv.riskLevel) {
                            WeatherRiskLevel.HIGH_DANGER -> Color(0xFFB71C1C)
                            WeatherRiskLevel.WARNING -> Color(0xFFE65100)
                            WeatherRiskLevel.CAUTION -> Color(0xFFF57F17)
                            WeatherRiskLevel.OPTIMAL -> Color(0xFF1B5E20)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(alertBg)
                                .border(1.dp, alertBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (adv.riskLevel == WeatherRiskLevel.OPTIMAL) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = alertText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = adv.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = alertText
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = adv.summary,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.8f))
                                        .padding(8.dp)
                                ) {
                                    Text("• Urea: ${adv.ureaAdvice}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("• NPK: ${adv.npkAdvice}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("• Best Time: ${adv.bestApplicationWindow}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = alertText)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7. Notification Button (Fully Localized to active language)
                val notifyBtnText = when (currentLanguage) {
                    AppLanguage.TAGALOG -> "🔔 Ipadala sa Notification"
                    AppLanguage.TAGLISH -> "🔔 Send Weather Alert to Phone"
                    AppLanguage.ILOCANO -> "🔔 Ipatulod ti Pakdaar ti Cellphone"
                    AppLanguage.CEBUANO -> "🔔 Ipadala ang Pahibalo sa Telepono"
                    else -> "🔔 Notify Phone"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable {
                            NotificationHelper.sendWeatherWarningNotification(
                                context = context,
                                advisory = adv,
                                language = currentLanguage
                            )
                            val toastMsg = when (currentLanguage) {
                                AppLanguage.TAGALOG -> "Naipadala ang babala sa panahon sa notification ng telepono!"
                                AppLanguage.TAGLISH -> "Weather advisory sent to system notifications!"
                                AppLanguage.ILOCANO -> "Naipatulod ti pakdaar iti notification ti cellphone!"
                                AppLanguage.CEBUANO -> "Napadala ang pahibalo sa panahon sa notification sa telepono!"
                                else -> "Weather advisory sent to system notifications!"
                            }
                            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF1E5BB0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = notifyBtnText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E5BB0)
                    )
                }

                // 8. Scenario Simulator Toggle Panel
                AnimatedVisibility(visible = showScenarioControls) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Test Weather Scenarios on Fertilizer:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E5BB0)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScenarioChip(
                                label = "🌧️ Heavy Rain",
                                isSelected = selectedScenario == WeatherScenario.HEAVY_RAIN,
                                onClick = { onScenarioSelected(WeatherScenario.HEAVY_RAIN) },
                                modifier = Modifier.weight(1f)
                            )
                            ScenarioChip(
                                label = "🌡️ Heatwave",
                                isSelected = selectedScenario == WeatherScenario.EXTREME_HEAT,
                                onClick = { onScenarioSelected(WeatherScenario.EXTREME_HEAT) },
                                modifier = Modifier.weight(1f)
                            )
                            ScenarioChip(
                                label = "☀️ Clear",
                                isSelected = selectedScenario == WeatherScenario.OPTIMAL_CLEAR,
                                onClick = { onScenarioSelected(WeatherScenario.OPTIMAL_CLEAR) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScenarioChip(
                                label = "💨 High Wind",
                                isSelected = selectedScenario == WeatherScenario.HIGH_WINDS,
                                onClick = { onScenarioSelected(WeatherScenario.HIGH_WINDS) },
                                modifier = Modifier.weight(1f)
                            )
                            ScenarioChip(
                                label = "📡 Live GPS",
                                isSelected = selectedScenario == WeatherScenario.LIVE_GPS,
                                onClick = { onScenarioSelected(WeatherScenario.LIVE_GPS) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenarioChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF1E5BB0) else Color(0xFFECEFF1))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}
