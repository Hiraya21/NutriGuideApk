package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun OfflineStatusBanner(
    isOffline: Boolean,
    isCachedContent: Boolean,
    currentLanguage: AppLanguage,
    lastSyncTime: String,
    onRetrySync: () -> Unit,
    isForcedOffline: Boolean = false,
    onToggleForcedOffline: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val bannerTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> if (isOffline) "Offline Mode Active" else "Online • Live Sync"
        AppLanguage.TAGALOG -> if (isOffline) "Naka-Offline Mode" else "Online • Live Sync"
        AppLanguage.TAGLISH -> if (isOffline) "Offline Mode Active" else "Online • Real-Time Sync"
        AppLanguage.ILOCANO -> if (isOffline) "Offline Mode ti Talon" else "Online • Sibibiag a Datos"
        AppLanguage.CEBUANO -> if (isOffline) "Naka-Offline Mode" else "Online • Live nga Datos"
    }

    val bannerSubtitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> if (isOffline) "Accessing local cached content • 100% offline ready" else "Weather & GPS connected"
        AppLanguage.TAGALOG -> if (isOffline) "Gamit ang naka-save na datos • Gumagana kahit walang internet" else "Konektado ang Live Weather & GPS"
        AppLanguage.TAGLISH -> if (isOffline) "Using cached local data • Tuloy ang pagsukat at kalkula" else "Live Weather & GPS connected"
        AppLanguage.ILOCANO -> if (isOffline) "Us-usaren ti nakasagana a datos ti selpon" else "Nakakonektar ti Weather ken GPS"
        AppLanguage.CEBUANO -> if (isOffline) "Naggamit sa na-save nga datos • Dili kinahanglan ang internet" else "Konektado ang Live Weather & GPS"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = isOffline || isCachedContent,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                color = Color(0xFFFFF3E0), // Warm amber background for high contrast & outdoor readability
                contentColor = Color(0xFFE65100),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("banner_offline_mode")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFB74D)
                        )
                        .clickable { showDialog = true }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Offline Icon Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9800)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline Mode Indicator",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bannerTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFBF360C)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFE082))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isForcedOffline) "SIMULATED" else "CACHED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Text(
                            text = bannerSubtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF795548),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick Refresh Button
                    IconButton(
                        onClick = onRetrySync,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .testTag("btn_offline_retry_sync")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry Live Sync",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Info Button to open details dialog
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .testTag("btn_offline_info_details")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Offline Cache Details",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        OfflineInfoDialog(
            isOffline = isOffline,
            isCachedContent = isCachedContent,
            isForcedOffline = isForcedOffline,
            currentLanguage = currentLanguage,
            lastSyncTime = lastSyncTime,
            onDismiss = { showDialog = false },
            onRetrySync = {
                onRetrySync()
                showDialog = false
            },
            onToggleForcedOffline = onToggleForcedOffline
        )
    }
}

@Composable
fun OfflineInfoDialog(
    isOffline: Boolean,
    isCachedContent: Boolean,
    isForcedOffline: Boolean,
    currentLanguage: AppLanguage,
    lastSyncTime: String,
    onDismiss: () -> Unit,
    onRetrySync: () -> Unit,
    onToggleForcedOffline: () -> Unit
) {
    val dialogTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Offline Mode & Local Cache"
        AppLanguage.TAGALOG -> "Offline Mode at Naka-save na Datos"
        AppLanguage.TAGLISH -> "Offline Mode & Cached Content Info"
        AppLanguage.ILOCANO -> "Impormasyon ti Offline Cache"
        AppLanguage.CEBUANO -> "Impormasyon sa Offline Cache"
    }

    val dialogDescription = when (currentLanguage) {
        AppLanguage.ENGLISH -> "NutriGuide is designed for farm fields with poor or no signal. All core calculating and measuring tools operate 100% locally on your phone without internet."
        AppLanguage.TAGALOG -> "Ang NutriGuide ay ginawa para sa mga bukid kahit walang signal o internet. Lahat ng pangunahing sukat, gabay, at kalkulasyon ay 100% gumagana sa loob ng iyong telepono."
        AppLanguage.TAGLISH -> "Made specifically for remote rice fields. Lahat ng measurement at fertilizer computation ay 100% on-device at offline-ready."
        AppLanguage.ILOCANO -> "Naiplano ti NutriGuide para kadagiti talon nga awan signal na. Amin a panagrukod ken kalkulasyon ket agtartrabaho uray awan ti koneksyon."
        AppLanguage.CEBUANO -> "Gihimo ang NutriGuide para sa kaumahan nga walay signal. Ang tanang pagsukod ug kalkulasyon 100% nga magamit bisan walay internet."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_offline_mode_info"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isOffline) Color(0xFFFFE082) else Color(0xFFC8E6C9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (isOffline) Color(0xFFE65100) else FarmGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = dialogTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOffline) "Accessing Local Cache" else "Real-time Sync Active",
                        fontSize = 12.sp,
                        color = if (isOffline) Color(0xFFE65100) else FarmGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = dialogDescription,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Offline Feature Availability:"
                        AppLanguage.TAGALOG -> "Katayuan ng mga Gamit (Features):"
                        AppLanguage.TAGLISH -> "Offline Features Status:"
                        AppLanguage.ILOCANO -> "Kasasaad dagiti Features:"
                        AppLanguage.CEBUANO -> "Kahimtang sa mga Features:"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OfflineFeatureRow(
                    title = "GPS Land Boundary Measurement",
                    status = "100% Offline (GPS Hardware)",
                    isFullyOffline = true
                )
                OfflineFeatureRow(
                    title = "Fertilizer Matrix (Cramer's Rule)",
                    status = "100% Offline (Local Math Engine)",
                    isFullyOffline = true
                )
                OfflineFeatureRow(
                    title = "PhilRice Guides & Palay Booklet",
                    status = "100% Offline (Local Storage)",
                    isFullyOffline = true
                )
                OfflineFeatureRow(
                    title = "Soil Color & Chemistry Assistant",
                    status = "100% Offline (On-Device Camera)",
                    isFullyOffline = true
                )
                OfflineFeatureRow(
                    title = "Agrometeorology & Weather Advisory",
                    status = if (isOffline) "Cached Regional Baseline ($lastSyncTime)" else "Real-time Live API",
                    isFullyOffline = !isOffline
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Last Sync Info Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Data Source: ${if (isOffline) "Local Device Storage & Cache" else "Live Cloud Services"} ($lastSyncTime)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF616161)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRetrySync,
                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Now / Refresh")
            }
        },
        dismissButton = {
            Row {
                OutlinedButton(
                    onClick = onToggleForcedOffline
                ) {
                    Text(if (isForcedOffline) "Exit Sim" else "Simulate Offline", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
private fun OfflineFeatureRow(
    title: String,
    status: String,
    isFullyOffline: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isFullyOffline) Color(0xFF2E7D32) else Color(0xFFF57C00),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                maxLines = 1
            )
        }
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isFullyOffline) Color(0xFF2E7D32) else Color(0xFFE65100)
        )
    }
}
