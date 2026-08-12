package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AgriculturalRegion
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.WeatherRiskLevel
import com.example.domain.models.WeatherScenario
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.util.NotificationHelper

@Composable
fun WeatherAdvisoryCard(
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
    var regionDropdownExpanded by remember { mutableStateOf(false) }
    var showScenarioControls by remember { mutableStateOf(false) }

    val adv = weatherData.advisory

    val alertBgColor = when (adv.riskLevel) {
        WeatherRiskLevel.HIGH_DANGER -> Color(0xFFFFEBEE)
        WeatherRiskLevel.WARNING -> Color(0xFFFFF3E0)
        WeatherRiskLevel.CAUTION -> Color(0xFFFFFDE7)
        WeatherRiskLevel.OPTIMAL -> Color(0xFFE8F5E9)
    }

    val alertBorderColor = when (adv.riskLevel) {
        WeatherRiskLevel.HIGH_DANGER -> Color(0xFFE53935)
        WeatherRiskLevel.WARNING -> Color(0xFFFB8C00)
        WeatherRiskLevel.CAUTION -> Color(0xFFFDD835)
        WeatherRiskLevel.OPTIMAL -> Color(0xFF43A047)
    }

    val alertTextColor = when (adv.riskLevel) {
        WeatherRiskLevel.HIGH_DANGER -> Color(0xFFB71C1C)
        WeatherRiskLevel.WARNING -> Color(0xFFE65100)
        WeatherRiskLevel.CAUTION -> Color(0xFFF57F17)
        WeatherRiskLevel.OPTIMAL -> Color(0xFF1B5E20)
    }

    val alertIcon = when (adv.riskLevel) {
        WeatherRiskLevel.HIGH_DANGER -> Icons.Default.Warning
        WeatherRiskLevel.WARNING -> Icons.Default.Warning
        WeatherRiskLevel.CAUTION -> Icons.Default.WaterDrop
        WeatherRiskLevel.OPTIMAL -> Icons.Default.CheckCircle
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_weather_advisory"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Row: Location Dropdown + Refresh Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = FarmGreenHeader,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))

                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF4F6F4))
                            .clickable { regionDropdownExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = selectedRegion.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▼",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = regionDropdownExpanded,
                        onDismissRequest = { regionDropdownExpanded = false }
                    ) {
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

                // Live API Indicator Badge
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (weatherData.isLiveApi) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (weatherData.isLiveApi) "📡 LIVE" else "⚡ OFF",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (weatherData.isLiveApi) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_refresh_weather")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Forecast",
                        tint = FarmGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weather Condition Summary Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEBE9))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Live Status: ${weatherData.weatherCondition}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
                Text(
                    text = weatherData.locationName,
                    fontSize = 11.sp,
                    color = Color(0xFF6D4C41),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weather Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAF8))
                    .border(0.5.dp, FarmBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric 1: Temperature
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${String.format("%.1f", weatherData.currentTempC)}°C", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text("Max ${String.format("%.0f", weatherData.maxTempC)}°C", fontSize = 10.sp, color = Color.Gray)
                }

                // Metric 2: Rainfall Forecast
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${String.format("%.1f", weatherData.precipitationSumMm)} mm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text("Rain Risk ${weatherData.precipitationProbPercent}%", fontSize = 10.sp, color = Color.Gray)
                }

                // Metric 3: Wind
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Air, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${String.format("%.0f", weatherData.windSpeedKmh)} km/h", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text("Humidity ${weatherData.relativeHumidity}%", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // 5-DAY WEATHER FORECAST CARDS
            if (weatherData.dailyForecast.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📅 5-Day Farm Weather & Fertilizer Window:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenHeader
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weatherData.dailyForecast.forEach { day ->
                        val isHeavyRain = day.precipitationMm >= 20.0
                        val isModerateRain = day.precipitationMm in 5.0..19.9
                        val isExtremeHeat = day.maxTempC >= 35.0

                        val dayBg = when {
                            isHeavyRain -> Color(0xFFFFEBEE)
                            isModerateRain -> Color(0xFFFFF3E0)
                            isExtremeHeat -> Color(0xFFFFFDE7)
                            else -> Color(0xFFF1F8E9)
                        }
                        val dayBorder = when {
                            isHeavyRain -> Color(0xFFEF5350)
                            isModerateRain -> Color(0xFFFFB74D)
                            isExtremeHeat -> Color(0xFFFDD835)
                            else -> Color(0xFFA5D6A7)
                        }

                        val appStatus = when {
                            isHeavyRain -> "🔴 Delay"
                            isModerateRain -> "🟡 Caution"
                            isExtremeHeat -> "⚠️ Heat"
                            else -> "🟢 Ideal"
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = dayBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, dayBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day.dateLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = day.condition,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%.0f", day.maxTempC)}° / ${String.format("%.0f", day.minTempC)}°C",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "💧 ${String.format("%.1f", day.precipitationMm)}mm (${day.rainProbPercent}%)",
                                    fontSize = 9.sp,
                                    color = Color(0xFF1565C0)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = appStatus,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Fertilizer Risk Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(alertBgColor)
                    .border(1.dp, alertBorderColor, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = alertIcon,
                            contentDescription = null,
                            tint = alertTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = adv.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = alertTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = adv.summary,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Actionable Guidelines
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.7f))
                            .padding(8.dp)
                    ) {
                        Text("• Urea Safety: ${adv.ureaAdvice}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("• NPK Basal: ${adv.npkAdvice}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("• Best Time: ${adv.bestApplicationWindow}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = alertTextColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Actions: Notify & Test Scenarios Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Send Weather Warning Notification Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FarmGreenHeader)
                        .clickable {
                            NotificationHelper.sendWeatherWarningNotification(
                                context = context,
                                title = adv.title,
                                message = adv.summary
                            )
                            Toast
                                .makeText(
                                    context,
                                    "Weather warning alert sent to system notifications!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Notify Phone",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Weather Simulator Toggle
                Text(
                    text = if (showScenarioControls) "Hide Simulation ▲" else "Simulate Weather Scenarios ▼",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary,
                    modifier = Modifier.clickable { showScenarioControls = !showScenarioControls }
                )
            }

            // Interactive Scenario Simulation Bar
            AnimatedVisibility(visible = showScenarioControls) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF2F4F2))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Test Weather Conditions on Fertilizer Efficiency:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

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
            .background(if (isSelected) FarmGreenHeader else Color.White)
            .border(
                1.dp,
                if (isSelected) FarmGreenHeader else FarmBorder,
                RoundedCornerShape(8.dp)
            )
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
