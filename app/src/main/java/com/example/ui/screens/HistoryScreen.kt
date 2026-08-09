package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmRecord
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmRed
import com.example.ui.theme.FarmRedLight
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.OutlinedButton

import com.example.domain.models.AppLanguage
import com.example.ui.components.LanguageBar

@Composable
fun HistoryScreen(
    searchQuery: String,
    farms: List<FarmRecord>,
    totalFarms: Int,
    totalArea: Double,
    onSearchChange: (String) -> Unit,
    onDeleteFarm: (FarmRecord) -> Unit,
    onDeleteAllFarms: () -> Unit,
    onOpenDeleteAccount: (() -> Unit)? = null,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var farmToDelete by remember { mutableStateOf<FarmRecord?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Language Selector Bar
        LanguageBar(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val titleText = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farm History"
                AppLanguage.TAGALOG -> "Nakaraang Tala ng Bukid"
                AppLanguage.TAGLISH -> "Farm History & Records"
                AppLanguage.ILOCANO -> "Nakalabas a Rekord ti Talon"
                AppLanguage.CEBUANO -> "Agi-an sa Humayan"
            }
            Text(
                text = titleText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark
            )

            if (farms.isNotEmpty()) {
                val clearAllText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Clear All"
                    AppLanguage.TAGALOG -> "Burahin Lahat"
                    AppLanguage.TAGLISH -> "Clear All"
                    AppLanguage.ILOCANO -> "Punasan Amin"
                    AppLanguage.CEBUANO -> "Pufason Tanan"
                }
                TextButton(
                    onClick = { showDeleteAllDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = FarmRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Clear All",
                        tint = FarmRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = clearAllText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarmRed
                    )
                }
            }
        }

        // Search Bar
        val searchPlaceholder = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Search saved farms..."
            AppLanguage.TAGALOG -> "Maghanap ng na-save na bukid..."
            AppLanguage.TAGLISH -> "Search ng saved farms..."
            AppLanguage.ILOCANO -> "Biroken ti naidulin a talon..."
            AppLanguage.CEBUANO -> "Pangitaa ang na-save nga humayan..."
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(searchPlaceholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_history"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = FarmBorder,
                focusedBorderColor = FarmGreenPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total Farms & Total Area Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Farms Card (Dark Green)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenHeader),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val totalFarmsLabel = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Total Farms"
                        AppLanguage.TAGALOG -> "Kabuuan ng Bukid"
                        AppLanguage.TAGLISH -> "Total ng Bukid"
                        AppLanguage.ILOCANO -> "Pagsasao ti Talon"
                        AppLanguage.CEBUANO -> "Tanan nga Humayan"
                    }
                    Text(
                        text = totalFarmsLabel,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalFarms",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Total Area Card (Light Green)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = FarmGreenLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val totalAreaLabel = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Total Area"
                        AppLanguage.TAGALOG -> "Kabuuan ng Sukat"
                        AppLanguage.TAGLISH -> "Total Area"
                        AppLanguage.ILOCANO -> "Kabuuan nga Sukat"
                        AppLanguage.CEBUANO -> "Tanan nga Sukat"
                    }
                    Text(
                        text = totalAreaLabel,
                        fontSize = 13.sp,
                        color = FarmTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", totalArea),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ha",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farm Records List
        if (farms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val emptyText = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "No saved farms found"
                        AppLanguage.TAGALOG -> "Walang nahanap na na-save na bukid"
                        AppLanguage.TAGLISH -> "No saved farms found"
                        AppLanguage.ILOCANO -> "Awan ti naidulin a talon"
                        AppLanguage.CEBUANO -> "Walay na-save nga humayan"
                    }
                    Text(
                        text = emptyText,
                        color = FarmTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            farms.forEach { farm ->
                FarmRecordItemCard(
                    farm = farm,
                    onDelete = { farmToDelete = farm }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (onOpenDeleteAccount != null) {
            Spacer(modifier = Modifier.height(16.dp))
            val deleteAccountLabel = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Delete Account"
                AppLanguage.TAGALOG -> "Burahin ang Account"
                AppLanguage.TAGLISH -> "Delete Account"
                AppLanguage.ILOCANO -> "Pukawen ti Account"
                AppLanguage.CEBUANO -> "Pufason ang Account"
            }
            OutlinedButton(
                onClick = onOpenDeleteAccount,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, FarmRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_history_delete_account")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PersonRemove,
                        contentDescription = deleteAccountLabel,
                        tint = FarmRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = deleteAccountLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Delete Single Farm Confirmation Modal
    farmToDelete?.let { farm ->
        AlertDialog(
            onDismissRequest = { farmToDelete = null },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FarmRedLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Farm Warning",
                            tint = FarmRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Delete Farm History?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${farm.name}\" (${String.format("%.2f", farm.areaHectares)} ha) from your history? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFarm(farm)
                        farmToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("button_confirm_delete_farm_history")
                ) {
                    Text(
                        text = "Yes, Delete Record",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { farmToDelete = null },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("button_cancel_delete_farm_history")
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        color = FarmTextDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete All Farms Confirmation Modal
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FarmRedLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Clear History Warning",
                            tint = FarmRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Clear All Farm History?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ALL $totalFarms farm history records? This will permanently erase your saved records.",
                    fontSize = 14.sp,
                    color = FarmTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllFarms()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("button_confirm_clear_all_history")
                    ) {
                    Text(
                        text = "Yes, Clear All History",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteAllDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("button_cancel_clear_all_history")
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        color = FarmTextDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun FarmRecordItemCard(
    farm: FarmRecord,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FarmGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = farm.name,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = farm.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = farm.dateFormatted,
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { expanded = !expanded }
                ) {
                    Text(
                        text = String.format("%.2f", farm.areaHectares),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                    Text(
                        text = "hectares",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = Color.Gray
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = FarmBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: Perimeter & Walked
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Perimeter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Perimeter ",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Text(
                                text = "${farm.perimeterMeters.toInt()} m",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        // Walked
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Walked ",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            val walkedVal = if (farm.walkedMeters > 0) farm.walkedMeters else farm.perimeterMeters
                            Text(
                                text = "${walkedVal.toInt()} m",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: GPS Accuracy & Boundary Points
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GPS Accuracy",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = farm.gpsAccuracy.ifBlank { "Fair" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Boundary Points",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val ptCount = if (farm.boundaryPointsCount > 0) {
                                farm.boundaryPointsCount
                            } else {
                                val count = farm.pointsJson.windowed(3).count { it == "lat" }
                                if (count > 0) count else 0
                            }
                            Text(
                                text = "$ptCount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 3: Date Measured & Delete Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Date Measured",
                                fontSize = 13.sp,
                                color = FarmTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val fullDateStr = remember(farm.timestamp) {
                                try {
                                    SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.US).format(Date(farm.timestamp))
                                } catch (e: Exception) {
                                    farm.dateFormatted
                                }
                            }
                            Text(
                                text = fullDateStr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = FarmRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
