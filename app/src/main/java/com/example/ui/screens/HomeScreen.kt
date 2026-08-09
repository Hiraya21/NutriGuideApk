package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.repository.AgriculturalRegion
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.WeatherScenario
import com.example.ui.components.LanguageDropdown
import com.example.ui.components.WeatherAdvisoryCard
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.util.NotificationHelper

import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.IconButton

@Composable
fun HomeScreen(
    currentLanguage: AppLanguage,
    weatherData: FarmWeatherData = FarmWeatherData(),
    agriculturalRegions: List<AgriculturalRegion> = emptyList(),
    selectedRegion: AgriculturalRegion = AgriculturalRegion("Nueva Ecija (Rice Granary)", "Central Luzon", 15.4827, 120.9723, 31.5, 2.5, 12.0, "Partly Cloudy"),
    selectedWeatherScenario: WeatherScenario = WeatherScenario.LIVE_GPS,
    onLanguageSelected: (AppLanguage) -> Unit,
    onRegionSelected: (AgriculturalRegion) -> Unit = {},
    onScenarioSelected: (WeatherScenario) -> Unit = {},
    onRefreshWeather: () -> Unit = {},
    onNavigateToTab: (Int) -> Unit,
    onOpenSoilAnalysis: () -> Unit,
    onOpenDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var pendingNotificationAction by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            when (pendingNotificationAction) {
                "WEATHER" -> {
                    NotificationHelper.sendWeatherReminder(context)
                    Toast.makeText(context, "Weather forecast reminder sent!", Toast.LENGTH_SHORT).show()
                }
                "ACTIVITY" -> {
                    NotificationHelper.sendDailyActivityReminder(context)
                    Toast.makeText(context, "Daily activity reminder sent!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Notification permission is required to send reminders.", Toast.LENGTH_SHORT).show()
        }
        pendingNotificationAction = null
    }

    fun triggerNotification(action: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                if (action == "WEATHER") {
                    NotificationHelper.sendWeatherReminder(context)
                    Toast.makeText(context, "Weather forecast reminder sent!", Toast.LENGTH_SHORT).show()
                } else {
                    NotificationHelper.sendDailyActivityReminder(context)
                    Toast.makeText(context, "Daily activity reminder sent!", Toast.LENGTH_SHORT).show()
                }
            } else {
                pendingNotificationAction = action
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (action == "WEATHER") {
                NotificationHelper.sendWeatherReminder(context)
                Toast.makeText(context, "Weather forecast reminder sent!", Toast.LENGTH_SHORT).show()
            } else {
                NotificationHelper.sendDailyActivityReminder(context)
                Toast.makeText(context, "Daily activity reminder sent!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Deep Green Top Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FarmGreenHeader)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val appSubtitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Rice Farm Assistant"
                        AppLanguage.TAGALOG -> "Katulong sa Bukid ng Palay"
                        AppLanguage.TAGLISH -> "Rice Farm Assistant Tool"
                        AppLanguage.ILOCANO -> "Katulong ti Talon ti Bagas"
                        AppLanguage.CEBUANO -> "Matawag sa Humayan"
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = appSubtitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Offline Badge Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFA5D6A7),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Offline Ready",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Global Language Dropdown Toggle
                        LanguageDropdown(
                            currentLanguage = currentLanguage,
                            onLanguageSelected = onLanguageSelected
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Account & Delete Account Button
                        IconButton(
                            onClick = onOpenDeleteAccount,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("btn_open_delete_account_header")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = "Delete Account",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val mainTitle = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Nutrient Budget\nCalculator"
                    AppLanguage.TAGALOG -> "Kalkulador ng\nSustansya sa Lupa"
                    AppLanguage.TAGLISH -> "Nutrient Budget\nCalculator ng Palay"
                    AppLanguage.ILOCANO -> "Kalkulador ti\nSustansia ti Talon"
                    AppLanguage.CEBUANO -> "Kalkulador sa\nSustansya sa Yuta"
                }

                Text(
                    text = mainTitle,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val tagline = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Measure, plan, and grow with confidence."
                    AppLanguage.TAGALOG -> "Sukatin, planuhin, at palaguin nang may tiwala."
                    AppLanguage.TAGLISH -> "Measure at mag-plan para sa magandang ani."
                    AppLanguage.ILOCANO -> "Sukatin, planuon, ken padakkelen ti ani."
                    AppLanguage.CEBUANO -> "Sukdon, planohon, ug padakoon ang ani."
                }

                Text(
                    text = tagline,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Weather & Fertilizer Safety Alert Card
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            WeatherAdvisoryCard(
                weatherData = weatherData,
                regions = agriculturalRegions,
                selectedRegion = selectedRegion,
                selectedScenario = selectedWeatherScenario,
                onRegionSelected = onRegionSelected,
                onScenarioSelected = onScenarioSelected,
                onRefresh = onRefreshWeather
            )
        }

        // Sheet section: What do you want to do?
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            val sectionHeader = when (currentLanguage) {
                AppLanguage.ENGLISH -> "What do you want to do?"
                AppLanguage.TAGALOG -> "Ano ang gusto mong gawin?"
                AppLanguage.TAGLISH -> "Ano ang gusto mong gawin today?"
                AppLanguage.ILOCANO -> "Ania ti kayatmo nga aramiden?"
                AppLanguage.CEBUANO -> "Unsay gusto nimong buhaton?"
            }

            Text(
                text = sectionHeader,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Measure Farm
            val card1Title = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Measure Farm"
                AppLanguage.TAGALOG -> "Sukatin ang Bukid"
                AppLanguage.TAGLISH -> "Sukatin ang Farm Area"
                AppLanguage.ILOCANO -> "Rukoden ti Talon"
                AppLanguage.CEBUANO -> "Sukdon ang Humayan"
            }
            val card1Sub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Walk the field boundary using GPS"
                AppLanguage.TAGALOG -> "Lakaran ang gilid ng bukid gamit ang GPS"
                AppLanguage.TAGLISH -> "Walk along the farm boundary via GPS"
                AppLanguage.ILOCANO -> "Magnaka iti igid ti talon babaen ti GPS"
                AppLanguage.CEBUANO -> "Baklay sa kilid sa humayan gamit ang GPS"
            }
            FeatureCard(
                title = card1Title,
                subtitle = card1Sub,
                icon = Icons.Default.Map,
                testTag = "card_measure_farm",
                onClick = { onNavigateToTab(1) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 2: Fertilizer Calculator
            val card2Title = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Fertilizer Calculator"
                AppLanguage.TAGALOG -> "Kalkulador ng Pataba"
                AppLanguage.TAGLISH -> "Fertilizer Calculator"
                AppLanguage.ILOCANO -> "Kalkulador ti Paitaba"
                AppLanguage.CEBUANO -> "Kalkulador sa Abono"
            }
            val card2Sub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Plan bags, prices, and recommendations"
                AppLanguage.TAGALOG -> "Kalkulahin ang sako, presyo, at rekomendasyon"
                AppLanguage.TAGLISH -> "Compute bags, price list & recommendations"
                AppLanguage.ILOCANO -> "Kalkulaen ti sako, presyo, ken giya"
                AppLanguage.CEBUANO -> "Kalkulaha ang sako, presyo, ug rekomendasyon"
            }
            FeatureCard(
                title = card2Title,
                subtitle = card2Sub,
                icon = Icons.Default.Calculate,
                testTag = "card_fertilizer_calculator",
                onClick = { onNavigateToTab(2) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 3: Soil Analysis
            val card3Title = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Soil Analysis"
                AppLanguage.TAGALOG -> "Pagsusuri ng Lupa"
                AppLanguage.TAGLISH -> "Soil Analysis & Nutrients"
                AppLanguage.ILOCANO -> "Panagrasor ti Daga"
                AppLanguage.CEBUANO -> "Pagsusi sa Yuta"
            }
            val card3Sub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Check NPK nutrient needs"
                AppLanguage.TAGALOG -> "Suriin ang kailangan na NPK sa lupa"
                AppLanguage.TAGLISH -> "Check NPK nutrient requirements"
                AppLanguage.ILOCANO -> "Kitaen ti masapul nga NPK ti daga"
                AppLanguage.CEBUANO -> "Tan-awa ang gikinahanglang NPK sa yuta"
            }
            FeatureCard(
                title = card3Title,
                subtitle = card3Sub,
                icon = Icons.Default.Spa,
                testTag = "card_soil_analysis",
                onClick = onOpenSoilAnalysis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 4: Farming Booklet
            val card4Title = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farming Booklet"
                AppLanguage.TAGALOG -> "Gabay sa Pagsasaka"
                AppLanguage.TAGLISH -> "Farming Booklet Guides"
                AppLanguage.ILOCANO -> "Libro ti Panagtalon"
                AppLanguage.CEBUANO -> "Giyahan sa Pag-uma"
            }
            val card4Sub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Read practical guides and tips"
                AppLanguage.TAGALOG -> "Magbasa ng mga gabay at payo sa palayan"
                AppLanguage.TAGLISH -> "Read tips and practical farming guides"
                AppLanguage.ILOCANO -> "Basaen ti payo ken giya ti panagtalon"
                AppLanguage.CEBUANO -> "Basaha ang mga tip ug giya sa pag-uma"
            }
            FeatureCard(
                title = card4Title,
                subtitle = card4Sub,
                icon = Icons.Default.MenuBook,
                testTag = "card_farming_booklet",
                onClick = { onNavigateToTab(3) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card: Farm Reminders & Local Notifications
            val reminderTitle = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farm Reminders"
                AppLanguage.TAGALOG -> "Paalala sa Palayan"
                AppLanguage.TAGLISH -> "Farm Reminders"
                AppLanguage.ILOCANO -> "Pakaammo ti Talon"
                AppLanguage.CEBUANO -> "Pahimangno sa Humayan"
            }
            val reminderSub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Receive weather forecast and daily activity notifications"
                AppLanguage.TAGALOG -> "Makatanggap ng paalala sa panahon at pang-araw-araw na gawain"
                AppLanguage.TAGLISH -> "Get weather forecast and activity log reminders"
                AppLanguage.ILOCANO -> "Makaawat ti pakaammo iti tiemani ken aramid"
                AppLanguage.CEBUANO -> "Makadawat og pahimangno sa panahon ug buluhaton"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_farm_reminders")
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FarmGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = reminderTitle,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = reminderTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = reminderSub,
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { triggerNotification("WEATHER") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FarmGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_weather_reminder")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Weather",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { triggerNotification("ACTIVITY") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FarmGreenHeader,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_activity_reminder")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daily Activity",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 5: Account & Data Settings (Delete Account)
            val card5Title = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Account & Privacy"
                AppLanguage.TAGALOG -> "Account at Datos"
                AppLanguage.TAGLISH -> "Account Settings"
                AppLanguage.ILOCANO -> "Panagurnos ti Account"
                AppLanguage.CEBUANO -> "Mga Setting sa Account"
            }
            val card5Sub = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Delete account and reset saved data"
                AppLanguage.TAGALOG -> "Burahin ang account at burahin ang datos"
                AppLanguage.TAGLISH -> "Delete account and clear farm records"
                AppLanguage.ILOCANO -> "Buraen ti account ken datos ti talon"
                AppLanguage.CEBUANO -> "I-delete ang account ug pasirahi ang datos"
            }
            FeatureCard(
                title = card5Title,
                subtitle = card5Sub,
                icon = Icons.Default.PersonRemove,
                testTag = "card_delete_account",
                onClick = onOpenDeleteAccount
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light Green Icon Container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FarmGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = FarmTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = Color(0xFFB0BEC5),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
