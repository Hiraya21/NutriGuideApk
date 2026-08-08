package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AgriculturalRegion
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.FertilizerItem
import com.example.domain.models.WeatherScenario
import com.example.ui.components.LanguageBar
import com.example.ui.components.WeatherAdvisoryCard
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.ui.viewmodel.CalculationResult
import com.example.util.PdfExportHelper

data class NpkTargetPreset(
    val name: String,
    val n: String,
    val p: String,
    val k: String
)

@Composable
fun FertilizerScreen(
    farmArea: String,
    targetN: String = "120",
    targetP: String = "40",
    targetK: String = "30",
    fertilizerList: List<FertilizerItem>,
    calculationResult: CalculationResult?,
    selectedCrop: String = "Rice",
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    weatherData: FarmWeatherData = FarmWeatherData(),
    agriculturalRegions: List<AgriculturalRegion> = emptyList(),
    selectedRegion: AgriculturalRegion = AgriculturalRegion("Nueva Ecija (Rice Granary)", "Central Luzon", 15.4827, 120.9723, 31.5, 2.5, 12.0, "Partly Cloudy"),
    selectedWeatherScenario: WeatherScenario = WeatherScenario.LIVE_GPS,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    onRegionSelected: (AgriculturalRegion) -> Unit = {},
    onScenarioSelected: (WeatherScenario) -> Unit = {},
    onRefreshWeather: () -> Unit = {},
    onAreaChange: (String) -> Unit,
    onTargetNChange: (String) -> Unit = {},
    onTargetPChange: (String) -> Unit = {},
    onTargetKChange: (String) -> Unit = {},
    onToggleSelected: (String) -> Unit,
    onToggleAvailability: (String) -> Unit = {},
    onUpdatePrice: (String, Double) -> Unit,
    onUpdateNutrients: (String, Double, Double, Double) -> Unit = { _, _, _, _ -> },
    onRunCalculation: () -> Unit,
    onDismissResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val areaNum = farmArea.toDoubleOrNull() ?: 1.0
    val selectedList = fertilizerList.filter { it.isSelected }

    // Reset Confirmation Dialog state
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var pendingPresetToApply by remember { mutableStateOf<NpkTargetPreset?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Language Bar Selection
        LanguageBar(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val titleText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Fertilizer Calculator & Matrix NPK Solver"
            AppLanguage.TAGALOG -> "Kalkulador at Matrix ng Pataba"
            AppLanguage.TAGLISH -> "Fertilizer Calculator & Matrix NPK"
            AppLanguage.ILOCANO -> "Kalkulador ti Paitaba ken Matrix NPK"
            AppLanguage.CEBUANO -> "Kalkulador sa Abono ug Matrix NPK"
        }

        Text(
            text = titleText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = FarmTextDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        // Regional Weather & Fertilizer Advisory Card
        WeatherAdvisoryCard(
            weatherData = weatherData,
            regions = agriculturalRegions,
            selectedRegion = selectedRegion,
            selectedScenario = selectedWeatherScenario,
            onRegionSelected = onRegionSelected,
            onScenarioSelected = onScenarioSelected,
            onRefresh = onRefreshWeather,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Farm Area Input Label
        Text(
            text = "Farm Area (hectares)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FarmTextDark
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = farmArea,
            onValueChange = onAreaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_farm_area"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = FarmGreenPrimary,
                unfocusedBorderColor = FarmBorder
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target NPK Recommendation Section (Editable Manual Target or Preset Selector)
        val npkPresets = listOf(
            NpkTargetPreset("Rice High Yield", "120", "40", "30"),
            NpkTargetPreset("Rice Med Yield", "90", "30", "30"),
            NpkTargetPreset("Corn / Maize", "120", "90", "60"),
            NpkTargetPreset("Vegetables", "150", "60", "120"),
            NpkTargetPreset("Root Crops", "80", "40", "120"),
            NpkTargetPreset("Sugarcane", "160", "80", "140")
        )

        val activePreset = npkPresets.find {
            it.n == targetN && it.p == targetP && it.k == targetK
        }
        val isManualTarget = activePreset == null

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, if (isManualTarget) Color(0xFF2E7D32) else FarmBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = if (isManualTarget) Color(0xFFF1F8E9) else FarmGreenLight)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = FarmGreenHeader,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target Nutrient Recommendation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                    }

                    // Display Tag for Active Target Mode (Manual vs Preset)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isManualTarget) Color(0xFF2E7D32) else Color(0xFF1565C0))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isManualTarget) Icons.Default.Edit else Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isManualTarget) "Manual Target" else "Preset: ${activePreset.name}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets Selection Bar
                Text(
                    text = "Select Preset or Edit Custom Manual Target:",
                    fontSize = 11.sp,
                    color = FarmTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    // Custom Manual Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isManualTarget) Color(0xFF2E7D32) else FarmGreenPrimary.copy(alpha = 0.15f))
                            .border(1.dp, if (isManualTarget) Color(0xFF2E7D32) else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable {
                                // Keep existing manual values or focus text field
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✏️ Custom Manual Target",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isManualTarget) Color.White else FarmGreenHeader
                        )
                    }

                    npkPresets.forEach { preset ->
                        val isSelected = activePreset?.name == preset.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1565C0) else FarmGreenPrimary.copy(alpha = 0.15f))
                                .border(1.dp, if (isSelected) Color(0xFF1565C0) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isManualTarget && !isSelected) {
                                        pendingPresetToApply = preset
                                        showResetConfirmDialog = true
                                    } else {
                                        onTargetNChange(preset.n)
                                        onTargetPChange(preset.p)
                                        onTargetKChange(preset.k)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${preset.name} (${preset.n}-${preset.p}-${preset.k})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else FarmGreenHeader
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Editable Input Fields for Target N, P2O5, K2O
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // N Target
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nitrogen (N) kg/ha", fontSize = 11.sp, color = FarmTextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedTextField(
                            value = targetN,
                            onValueChange = onTargetNChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_target_n"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = if (isManualTarget) Color(0xFF2E7D32) else FarmGreenPrimary,
                                unfocusedBorderColor = FarmBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    // P2O5 Target
                    Column(modifier = Modifier.weight(1f)) {
                        Text("P₂O₅ (P) kg/ha", fontSize = 11.sp, color = FarmTextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedTextField(
                            value = targetP,
                            onValueChange = onTargetPChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_target_p"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = if (isManualTarget) Color(0xFF2E7D32) else FarmGreenPrimary,
                                unfocusedBorderColor = FarmBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    // K2O Target
                    Column(modifier = Modifier.weight(1f)) {
                        Text("K₂O (K) kg/ha", fontSize = 11.sp, color = FarmTextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedTextField(
                            value = targetK,
                            onValueChange = onTargetKChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_target_k"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = if (isManualTarget) Color(0xFF2E7D32) else FarmGreenPrimary,
                                unfocusedBorderColor = FarmBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary Total Target Banner
                val nNum = targetN.toDoubleOrNull() ?: 0.0
                val pNum = targetP.toDoubleOrNull() ?: 0.0
                val kNum = targetK.toDoubleOrNull() ?: 0.0
                val totalNpkPerHa = nNum + pNum + kNum
                val totalFieldNpk = totalNpkPerHa * areaNum

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, FarmBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Target NPK Ratio: $targetN - $targetP - $targetK NPK kg/ha",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark
                        )
                        Text(
                            text = "Total Field Target: ${String.format("%.1f", totalFieldNpk)} kg across ${String.format("%.2f", areaNum)} ha",
                            fontSize = 10.sp,
                            color = FarmTextSecondary
                        )
                    }

                    if (isManualTarget) {
                        TextButton(
                            onClick = {
                                pendingPresetToApply = null
                                showResetConfirmDialog = true
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.testTag("btn_reset_manual_npk")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Reset NPK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = {
                    showResetConfirmDialog = false
                    pendingPresetToApply = null
                },
                modifier = Modifier.testTag("dialog_reset_npk_confirm"),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "Reset Manual NPK Entries?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = FarmTextDark
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Are you sure you want to reset your custom manual NPK target entries ($targetN - $targetP - $targetK kg/ha)?",
                            fontSize = 13.sp,
                            color = FarmTextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val desc = if (pendingPresetToApply != null) {
                            "This will overwrite your manual entries with the '${pendingPresetToApply?.name}' preset (${pendingPresetToApply?.n}-${pendingPresetToApply?.p}-${pendingPresetToApply?.k} kg/ha)."
                        } else {
                            "This will restore the default recommended target NPK ratio (120-40-30 kg/ha)."
                        }
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = FarmTextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val preset = pendingPresetToApply
                            if (preset != null) {
                                onTargetNChange(preset.n)
                                onTargetPChange(preset.p)
                                onTargetKChange(preset.k)
                            } else {
                                onTargetNChange("120")
                                onTargetPChange("40")
                                onTargetKChange("30")
                            }
                            showResetConfirmDialog = false
                            pendingPresetToApply = null
                            Toast.makeText(context, "Manual NPK entries reset successfully", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_confirm_reset_npk")
                    ) {
                        Text("Reset NPK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showResetConfirmDialog = false
                            pendingPresetToApply = null
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_cancel_reset_npk")
                    ) {
                        Text("Cancel", fontSize = 12.sp, color = FarmTextDark)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Available Fertilizers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available Fertilizer Materials",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark
            )
            Text(
                text = "Edit NPK % & Price",
                fontSize = 12.sp,
                color = FarmTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Checklist of Fertilizers with Editable Nutrients & Price
        fertilizerList.forEach { item ->
            FertilizerItemRow(
                item = item,
                onToggle = { onToggleSelected(item.id) },
                onToggleAvailability = { onToggleAvailability(item.id) },
                onPriceChange = { newPrice -> onUpdatePrice(item.id, newPrice) },
                onNutrientsChange = { n, p, k -> onUpdateNutrients(item.id, n, p, k) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RUN CALCULATION Button
        Button(
            onClick = onRunCalculation,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_run_calculation"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FarmGreenHeader,
                contentColor = Color.White
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOLVE NPK MATRIX & COMPUTATION",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FERTILIZER RECOMMENDATION DISPLAY ON THE FORM
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, FarmGreenPrimary, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Recommendation",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fertilizer Recommendations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedList.isEmpty()) {
                    Text(
                        text = "Select one or more available fertilizers above to view tailored field recommendations for your farm.",
                        fontSize = 13.sp,
                        color = FarmTextSecondary
                    )
                } else {
                    Text(
                        text = "Land Measurement: ${String.format("%.2f", areaNum)} ha ($selectedCrop) | Target: $targetN-$targetP-$targetK NPK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    selectedList.forEach { item ->
                        val totalBags = kotlin.math.ceil(item.bagsPerHectare * areaNum * 10) / 10.0
                        val costText = if (item.customPrice > 0) "Est: ₱${String.format("%,.0f", totalBags * item.customPrice)}" else "₱00.0"

                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${item.name} (${item.nPercent.toInt()}-${item.pPercent.toInt()}-${item.kPercent.toInt()}) — $totalBags bags ($costText)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmTextDark
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Calculation Summary Result Dialog with Export PDF & Cramer's Matrix Solution
    calculationResult?.let { result ->
        AlertDialog(
            onDismissRequest = onDismissResult,
            containerColor = Color.White,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NPK Calculation Report",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Farm Area: ${result.farmArea} hectare(s) | Crop: $selectedCrop",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = "Target NPK: $targetN-$targetP-$targetK kg/ha",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (result.cramerMatrixExplanation.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = FarmGreenLight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Cramer's Rule Matrix Solution:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenHeader)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(result.cramerMatrixExplanation, fontSize = 11.sp, color = FarmTextDark)
                            }
                        }
                    }

                    if (result.items.isEmpty()) {
                        Text(
                            text = "No fertilizers selected. Please check at least one fertilizer item.",
                            color = FarmTextSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        result.items.forEach { breakdown ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = breakdown.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = FarmTextDark
                                    )
                                    Text(
                                        text = "${breakdown.bagsNeeded} bags @ ₱${if (breakdown.pricePerBag > 0) String.format("%,.0f", breakdown.pricePerBag) else "00.0"}/bag",
                                        fontSize = 12.sp,
                                        color = FarmTextSecondary
                                    )
                                }
                                Text(
                                    text = "₱ ${String.format("%,.2f", breakdown.totalCost)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FarmGreenPrimary
                                )
                            }
                            Divider(color = FarmBorder, modifier = Modifier.padding(vertical = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL ESTIMATED COST:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                            Text(
                                text = "₱ ${String.format("%,.2f", result.totalCost)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenHeader
                            )
                        }

                        if (result.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = FarmBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Field Guidelines:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenHeader
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            result.recommendations.forEach { rec ->
                                Text(
                                    text = "• $rec",
                                    fontSize = 12.sp,
                                    color = FarmTextDark,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            PdfExportHelper.printFertilizerReport(
                                context = context,
                                result = result,
                                crop = selectedCrop,
                                targetN = targetN.toDoubleOrNull() ?: 120.0,
                                targetP = targetP.toDoubleOrNull() ?: 40.0,
                                targetK = targetK.toDoubleOrNull() ?: 30.0,
                                cramerMatrixExplanation = result.cramerMatrixExplanation
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_export_pdf")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onDismissResult,
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
fun FertilizerItemRow(
    item: FertilizerItem,
    onToggle: () -> Unit,
    onToggleAvailability: () -> Unit,
    onPriceChange: (Double) -> Unit,
    onNutrientsChange: (Double, Double, Double) -> Unit = { _, _, _ -> }
) {
    var priceText by remember(item.customPrice) {
        mutableStateOf(if (item.customPrice > 0) item.customPrice.toInt().toString() else "00.0")
    }

    var nText by remember(item.nPercent) { mutableStateOf(item.nPercent.toInt().toString()) }
    var pText by remember(item.pPercent) { mutableStateOf(item.pPercent.toInt().toString()) }
    var kText by remember(item.kPercent) { mutableStateOf(item.kPercent.toInt().toString()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, FarmBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onToggle)
                ) {
                    Checkbox(
                        checked = item.isSelected,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FarmGreenPrimary,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Column {
                        Text(
                            text = item.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmTextDark
                        )

                        // NPK % inputs & Stock Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "N:", fontSize = 11.sp, color = FarmTextSecondary)
                            BasicTextField(
                                value = nText,
                                onValueChange = {
                                    nText = it
                                    onNutrientsChange(it.toDoubleOrNull() ?: 0.0, pText.toDoubleOrNull() ?: 0.0, kText.toDoubleOrNull() ?: 0.0)
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenHeader),
                                modifier = Modifier.width(24.dp).padding(horizontal = 2.dp)
                            )
                            Text(text = "% | P:", fontSize = 11.sp, color = FarmTextSecondary)
                            BasicTextField(
                                value = pText,
                                onValueChange = {
                                    pText = it
                                    onNutrientsChange(nText.toDoubleOrNull() ?: 0.0, it.toDoubleOrNull() ?: 0.0, kText.toDoubleOrNull() ?: 0.0)
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenHeader),
                                modifier = Modifier.width(24.dp).padding(horizontal = 2.dp)
                            )
                            Text(text = "% | K:", fontSize = 11.sp, color = FarmTextSecondary)
                            BasicTextField(
                                value = kText,
                                onValueChange = {
                                    kText = it
                                    onNutrientsChange(nText.toDoubleOrNull() ?: 0.0, pText.toDoubleOrNull() ?: 0.0, it.toDoubleOrNull() ?: 0.0)
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenHeader),
                                modifier = Modifier.width(24.dp).padding(horizontal = 2.dp)
                            )
                            Text(text = "%", fontSize = 11.sp, color = FarmTextSecondary)
                        }
                    }
                }

                // Editable Price Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(105.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, FarmGreenPrimary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "₱",
                        fontSize = 13.sp,
                        color = FarmTextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = priceText,
                            onValueChange = { newValue ->
                                var cleanValue = newValue
                                if (priceText == "00.0" && newValue != "00.0") {
                                    cleanValue = newValue.replace("00.0", "")
                                }
                                if (cleanValue.all { it.isDigit() || it == '.' } && cleanValue.length <= 8) {
                                    priceText = cleanValue
                                    val parsed = cleanValue.toDoubleOrNull() ?: 0.0
                                    onPriceChange(parsed)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_price_${item.id}")
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Price",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}


