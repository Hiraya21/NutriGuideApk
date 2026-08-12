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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
    onSaveComputation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (calculationResult != null) {
        MatrixResultPage(
            result = calculationResult,
            selectedCrop = selectedCrop,
            targetN = targetN,
            targetP = targetP,
            targetK = targetK,
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            onDismiss = onDismissResult,
            onSaveComputation = onSaveComputation,
            modifier = modifier
        )
    } else {
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
        val formFarmAreaLabel = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Farm Area (hectares)"
            AppLanguage.TAGALOG -> "Laki ng Bukid (hektarya)"
            AppLanguage.TAGLISH -> "Farm Area (hectares)"
            AppLanguage.ILOCANO -> "Rukod ti Talon (hektarya)"
            AppLanguage.CEBUANO -> "Sukad sa Yuta (hektarya)"
        }

        val formTargetTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Target Nutrient Recommendation"
            AppLanguage.TAGALOG -> "Target na Rekomendasyon ng Nutrisyon"
            AppLanguage.TAGLISH -> "Target NPK Nutrient Ratio"
            AppLanguage.ILOCANO -> "Panggep a Nutrisyon ti Paitaba"
            AppLanguage.CEBUANO -> "Target nga Nutrisyon sa Abuno"
        }

        val formManualTargetTag = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Manual Target"
            AppLanguage.TAGALOG -> "Mano-manong Target"
            AppLanguage.TAGLISH -> "Manual Target"
            AppLanguage.ILOCANO -> "Mano-mano a Target"
            AppLanguage.CEBUANO -> "Mano-mano nga Target"
        }

        val formPresetPrompt = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Select Preset or Edit Custom Manual Target:"
            AppLanguage.TAGALOG -> "Pumili ng Preset o Baguhin ang Target NPK:"
            AppLanguage.TAGLISH -> "Select Preset or Edit Manual Target:"
            AppLanguage.ILOCANO -> "Piliin ti Preset wenno Sukatan ti NPK:"
            AppLanguage.CEBUANO -> "Pili og Preset o Bag-oha ang NPK:"
        }

        val formCustomChipText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "✏️ Custom Manual Target"
            AppLanguage.TAGALOG -> "✏️ Sariling Target NPK"
            AppLanguage.TAGLISH -> "✏️ Custom Manual Target"
            AppLanguage.ILOCANO -> "✏️ Bukod a Target NPK"
            AppLanguage.CEBUANO -> "✏️ Kaugalingong Target NPK"
        }

        val formMaterialsTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Available Fertilizer Materials"
            AppLanguage.TAGALOG -> "Mga Magagamit na Abono"
            AppLanguage.TAGLISH -> "Available Fertilizer Materials"
            AppLanguage.ILOCANO -> "Dagiti Magun-od a Paitaba"
            AppLanguage.CEBUANO -> "Mga Magamit nga Abuno"
        }

        val formSolveButtonText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "SOLVE NPK MATRIX & COMPUTATION"
            AppLanguage.TAGALOG -> "KALKULAHIN ANG ABONO (MATRIX SOLUTION)"
            AppLanguage.TAGLISH -> "SOLVE FERTILIZER MATRIX COMPUTATION"
            AppLanguage.ILOCANO -> "KALKULAHEN TI PAITABA (MATRIX)"
            AppLanguage.CEBUANO -> "KALKULAHA ANG ABUNO (MATRIX)"
        }

        val formRequiredHeaderTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Required Fertilizers (Breakdown)"
            AppLanguage.TAGALOG -> "Mga Kailangang Abono (Fertilizers)"
            AppLanguage.TAGLISH -> "Required Fertilizers List"
            AppLanguage.ILOCANO -> "Dagiti Masapul a Paitaba"
            AppLanguage.CEBUANO -> "Mga Kinahanglanon nga Abuno"
        }

        val formEmptySelectionNote = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Select one or more fertilizers above to calculate exact bag requirements."
            AppLanguage.TAGALOG -> "Pumili ng isa o higit pang abono sa itaas upang makalkula ang eksaktong bilang ng sako."
            AppLanguage.TAGLISH -> "Select one or more fertilizers above to calculate required bags."
            AppLanguage.ILOCANO -> "Piliin ti maysa wenno ad-adu a paitaba tapno magun-od ti bilang ti sako."
            AppLanguage.CEBUANO -> "Pili og usa o labaw pa nga abuno sa taas aron makalkula ang mga sako."
        }

        val formPerBagUnitLabel = when (currentLanguage) {
            AppLanguage.ENGLISH -> "bag"
            AppLanguage.TAGALOG -> "sako"
            AppLanguage.TAGLISH -> "sako"
            AppLanguage.ILOCANO -> "sako"
            AppLanguage.CEBUANO -> "sako"
        }

        val formCostLabel = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Est. Cost:"
            AppLanguage.TAGALOG -> "Tinatayang Halaga:"
            AppLanguage.TAGLISH -> "Est. Cost:"
            AppLanguage.ILOCANO -> "Tinatayang Gastos:"
            AppLanguage.CEBUANO -> "Tinatayang Halaga:"
        }

        val formSakoBadgeText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "BAGS"
            AppLanguage.TAGALOG -> "SAKO"
            AppLanguage.TAGLISH -> "SAKO / BAGS"
            AppLanguage.ILOCANO -> "SAKO"
            AppLanguage.CEBUANO -> "SAKO"
        }

        val titleText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Fertilizer Calculator & Matrix NPK Solver"
            AppLanguage.TAGALOG -> "Kalkulador at Matrix ng Pataba"
            AppLanguage.TAGLISH -> "Fertilizer Calculator & Matrix NPK"
            AppLanguage.ILOCANO -> "Kalkulador ti Paitaba ken Matrix NPK"
            AppLanguage.CEBUANO -> "Kalkulador sa Abuno ug Matrix NPK"
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

        // Farm Area Input Label
        Text(
            text = formFarmAreaLabel,
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
                            text = formTargetTitle,
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
                                text = if (isManualTarget) formManualTargetTag else "Preset: ${activePreset.name}",
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
                    text = formPresetPrompt,
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
                            text = formCustomChipText,
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
                text = formMaterialsTitle,
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
                    text = formSolveButtonText,
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
                .border(2.dp, FarmGreenHeader, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Recommendation",
                            tint = FarmGreenHeader,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formRequiredHeaderTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedList.isEmpty()) {
                    Text(
                        text = formEmptySelectionNote,
                        fontSize = 14.sp,
                        color = FarmTextSecondary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(FarmGreenLight)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        val areaSummaryPrefix = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Farm Area:"
                            AppLanguage.TAGALOG -> "Laki ng Bukid:"
                            AppLanguage.TAGLISH -> "Farm Area:"
                            AppLanguage.ILOCANO -> "Dakkel ti Talon:"
                            AppLanguage.CEBUANO -> "Sukad sa Yuta:"
                        }
                        val unitLabel = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Hectare"
                            AppLanguage.TAGALOG -> "Hektarya"
                            AppLanguage.TAGLISH -> "Hectare"
                            AppLanguage.ILOCANO -> "Hektarya"
                            AppLanguage.CEBUANO -> "Hektarya"
                        }
                        Text(
                            text = "$areaSummaryPrefix ${String.format("%.2f", areaNum)} $unitLabel ($selectedCrop) | Target: $targetN-$targetP-$targetK NPK",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    selectedList.forEach { item ->
                        val totalBags = kotlin.math.ceil(item.bagsPerHectare * areaNum * 10) / 10.0
                        val costVal = totalBags * item.customPrice
                        val costText = if (item.customPrice > 0) "₱${String.format("%,.0f", costVal)}" else "₱0.00"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = FarmTextDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "NPK Grade: ${item.nPercent.toInt()}-${item.pPercent.toInt()}-${item.kPercent.toInt()}",
                                        fontSize = 13.sp,
                                        color = FarmTextSecondary
                                    )
                                    if (item.customPrice > 0) {
                                        Text(
                                            text = "@ ₱${String.format("%,.0f", item.customPrice)} / $formPerBagUnitLabel  ➔  $formCostLabel $costText",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FarmGreenHeader
                                        )
                                    }
                                }

                                // PROMINENT BAGS BADGE
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FarmGreenHeader)
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$totalBags",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = formSakoBadgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MatrixResultPage(
    result: CalculationResult,
    selectedCrop: String,
    targetN: String,
    targetP: String,
    targetK: String,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    onDismiss: () -> Unit,
    onSaveComputation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Translations for all 5 languages (ENGLISH, TAGALOG, TAGLISH, ILOCANO, CEBUANO)
    val titleText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "🌾 Matrix NPK Calculation Result"
        AppLanguage.TAGALOG -> "🌾 Resulta ng Kalkulasyon sa Abono"
        AppLanguage.TAGLISH -> "🌾 Matrix NPK Result"
        AppLanguage.ILOCANO -> "🌾 Resulta ti Kalkulasyon ti Paitaba"
        AppLanguage.CEBUANO -> "🌾 Resulta sa Kalkulasyon sa Abuno"
    }

    val subtitleText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Fertilizer Requirement & Recommendation Report"
        AppLanguage.TAGALOG -> "Ulat ng Kailangang Abono at Rekomendasyon sa Bukid"
        AppLanguage.TAGLISH -> "Fertilizer Requirement & Field Recommendations"
        AppLanguage.ILOCANO -> "Ulat ti Masapul a Paitaba ken Rekomendasyon ti Talon"
        AppLanguage.CEBUANO -> "Talaan sa Kinahanglanon nga Abuno ug Giya sa Yuta"
    }

    val farmInfoText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Farm Area: ${result.farmArea} Hectare(s) | Crop: $selectedCrop"
        AppLanguage.TAGALOG -> "Laki ng Bukid: ${result.farmArea} Hectare(s) | Halaman: $selectedCrop"
        AppLanguage.TAGLISH -> "Farm Area: ${result.farmArea} Hectare(s) | Crop: $selectedCrop"
        AppLanguage.ILOCANO -> "Dakkel ti Talon: ${result.farmArea} Hectare(s) | Mula: $selectedCrop"
        AppLanguage.CEBUANO -> "Sukad sa Yuta: ${result.farmArea} Hectare(s) | Tanum: $selectedCrop"
    }

    val targetRatioText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Target NPK Nutrient Ratio: $targetN-$targetP-$targetK kg/ha"
        AppLanguage.TAGALOG -> "Rasyon ng Nutrisyon NPK: $targetN-$targetP-$targetK kg/ha"
        AppLanguage.TAGLISH -> "Target NPK Nutrient Ratio: $targetN-$targetP-$targetK kg/ha"
        AppLanguage.ILOCANO -> "Panggep a Rasyon NPK: $targetN-$targetP-$targetK kg/ha"
        AppLanguage.CEBUANO -> "Target NPK Nutrient Ratio: $targetN-$targetP-$targetK kg/ha"
    }

    val costBannerTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "TOTAL ESTIMATED FERTILIZER COST"
        AppLanguage.TAGALOG -> "KABUUANG TINATAYANG GASTOS SA ABONO"
        AppLanguage.TAGLISH -> "TOTAL ESTIMATED COST SA FERTILIZER"
        AppLanguage.ILOCANO -> "OBLIGADO NGA KABUKLAN A GASTOS TI PAITABA"
        AppLanguage.CEBUANO -> "TINATAYANG KINATBUKANG GASTOS SA ABUNO"
    }

    val costBannerSubtitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "For ${result.farmArea} Hectare ($selectedCrop)"
        AppLanguage.TAGALOG -> "Para sa ${result.farmArea} Hectare ($selectedCrop)"
        AppLanguage.TAGLISH -> "For ${result.farmArea} Hectare ($selectedCrop)"
        AppLanguage.ILOCANO -> "Para ti ${result.farmArea} Hectare ($selectedCrop)"
        AppLanguage.CEBUANO -> "Para sa ${result.farmArea} Hectare ($selectedCrop)"
    }

    val bagsHeaderTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "📦 Required Fertilizer Bags to Purchase:"
        AppLanguage.TAGALOG -> "📦 Bilang ng Sako ng Abono na Kailangan Bilhin:"
        AppLanguage.TAGLISH -> "📦 Number of Fertilizer Bags to Buy:"
        AppLanguage.ILOCANO -> "📦 Bilang ti Sako ti Paitaba a Masapul a Gatangen:"
        AppLanguage.CEBUANO -> "📦 Pila ka Sako sa Abuno ang Kinahanglan Paliton:"
    }

    val noFertilizerText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "No fertilizer selected."
        AppLanguage.TAGALOG -> "Walang napiling pataba."
        AppLanguage.TAGLISH -> "Walang napiling fertilizer."
        AppLanguage.ILOCANO -> "Awan ti napili a paitaba."
        AppLanguage.CEBUANO -> "Walay napili nga abuno."
    }

    val perBagLabel = when (currentLanguage) {
        AppLanguage.ENGLISH -> "per bag"
        AppLanguage.TAGALOG -> "bawat sako"
        AppLanguage.TAGLISH -> "per sako"
        AppLanguage.ILOCANO -> "maysa a sako"
        AppLanguage.CEBUANO -> "kada sako"
    }

    val subtotalLabel = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Total:"
        AppLanguage.TAGALOG -> "Kabuuan:"
        AppLanguage.TAGLISH -> "Total Cost:"
        AppLanguage.ILOCANO -> "Kabuklan:"
        AppLanguage.CEBUANO -> "Kinatbukangan:"
    }

    val badgeLabel = when (currentLanguage) {
        AppLanguage.ENGLISH -> "BAGS"
        AppLanguage.TAGALOG -> "SAKO"
        AppLanguage.TAGLISH -> "SAKO / BAGS"
        AppLanguage.ILOCANO -> "SAKO"
        AppLanguage.CEBUANO -> "SAKO"
    }

    val scheduleHeaderTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "📅 Recommended Fertilizer Application Schedule:"
        AppLanguage.TAGALOG -> "📅 Iskedyul at Paraan ng Pag-aabono sa Bukid:"
        AppLanguage.TAGLISH -> "📅 Fertilizer Application Schedule:"
        AppLanguage.ILOCANO -> "📅 Iskedyul ti Panag-Ipauneg ti Paitaba:"
        AppLanguage.CEBUANO -> "📅 Iskedyul ug Paagi sa Pag-abuno:"
    }

    val matrixHeaderTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "📐 Matrix Solution Steps (Cramer's Rule):"
        AppLanguage.TAGALOG -> "📐 Hakbang sa Pagkalkula ng Matrix (Cramer's Rule Solution):"
        AppLanguage.TAGLISH -> "📐 Matrix Solution Steps (Cramer's Rule):"
        AppLanguage.ILOCANO -> "📐 Dagiti Addang ti Pagkalkula ti Matrix (Cramer's Rule):"
        AppLanguage.CEBUANO -> "📐 Mga Lakang sa Pagkalkula sa Matrix (Cramer's Rule):"
    }

    val recommendationsHeaderTitle = when (currentLanguage) {
        AppLanguage.ENGLISH -> "🌱 Practical Field Advice & Safety Guidelines:"
        AppLanguage.TAGALOG -> "🌱 Mahalagang Paalala at Gabay sa Pag-aabono:"
        AppLanguage.TAGLISH -> "🌱 Important Guidelines for Farmers:"
        AppLanguage.ILOCANO -> "🌱 Dagiti Pakdaar ken Gabay ti Talon:"
        AppLanguage.CEBUANO -> "🌱 Importante nga Giya ug Pahinumdom sa Yuta:"
    }

    val btnSaveText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "💾 Save Computation to History"
        AppLanguage.TAGALOG -> "💾 I-save ang Kalkulasyon sa History"
        AppLanguage.TAGLISH -> "💾 Save Computation to History"
        AppLanguage.ILOCANO -> "💾 Isagrap ti Kalkulasyon (Save)"
        AppLanguage.CEBUANO -> "💾 I-save sa History (Save Computation)"
    }

    val btnExportText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Export PDF"
        AppLanguage.TAGALOG -> "I-export PDF"
        AppLanguage.TAGLISH -> "Export PDF"
        AppLanguage.ILOCANO -> "I-export PDF"
        AppLanguage.CEBUANO -> "I-export PDF"
    }

    val btnBackText = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Close (Back)"
        AppLanguage.TAGALOG -> "Isara (Bumalik)"
        AppLanguage.TAGLISH -> "Close (Back)"
        AppLanguage.ILOCANO -> "Iserra (Mapan)"
        AppLanguage.CEBUANO -> "I-close (Mobalik)"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // TOP APP BAR / HEADER WITH LANGUAGE SELECTOR & SAVE BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F8E9))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FarmGreenHeader
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FarmGreenHeader
                )
                Text(
                    text = subtitleText,
                    fontSize = 11.sp,
                    color = FarmTextSecondary,
                    maxLines = 1
                )
            }

            if (onSaveComputation != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        onSaveComputation()
                        Toast.makeText(context, "Saved to History!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FarmGreenHeader)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Divider(color = FarmBorder, thickness = 1.dp)

        // SCROLLABLE CONTENT BODY
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // FARM & CROP INFO BAR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = FarmGreenHeader, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = farmInfoText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FarmGreenHeader
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = targetRatioText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = FarmTextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BIG TOTAL ESTIMATED COST BANNER FOR FARMERS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = costBannerTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₱ ${String.format("%,.2f", result.totalCost)}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = costBannerSubtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = bagsHeaderTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FarmTextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (result.items.isEmpty()) {
                Text(
                    text = noFertilizerText,
                    color = FarmTextSecondary,
                    fontSize = 15.sp
                )
            } else {
                result.items.forEach { breakdown ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC0CA33))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = breakdown.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = FarmTextDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "@ ₱${if (breakdown.pricePerBag > 0) String.format("%,.0f", breakdown.pricePerBag) else "0"} $perBagLabel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = FarmTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$subtotalLabel ₱${String.format("%,.2f", breakdown.totalCost)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FarmGreenHeader
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // LARGE SAKO COUNTER BADGE
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1B5E20))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${breakdown.bagsNeeded}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = badgeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // APPLICATION SCHEDULE SECTION
            if (result.applicationSchedule.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = scheduleHeaderTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FarmGreenHeader
                )
                Spacer(modifier = Modifier.height(10.dp))

                result.applicationSchedule.forEach { sched ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF59D))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = sched,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmTextDark
                            )
                        }
                    }
                }
            }

            // COLLAPSIBLE MATRIX SOLUTION EXPLANATION (FARMER FRIENDLY)
            if (result.cramerMatrixExplanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(18.dp))
                var showMathDetails by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMathDetails = !showMathDetails },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = FarmGreenHeader,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = matrixHeaderTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FarmGreenHeader
                                )
                            }
                            Text(
                                text = if (showMathDetails) "▲ Itago" else "▼ Ipakita Math",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Calculated using 3x3 Cramer's Rule Matrix Optimization to achieve exact $targetN-$targetP-$targetK NPK nutrient balance."
                                AppLanguage.TAGALOG -> "Kinalkula gamit ang 3x3 Cramer's Rule Matrix Optimization upang makuha ang tamang $targetN-$targetP-$targetK NPK sa bukid."
                                AppLanguage.TAGLISH -> "Calculated using 3x3 Cramer's Rule Matrix to match target $targetN-$targetP-$targetK NPK ratio."
                                AppLanguage.ILOCANO -> "Kinalkula babaen ti 3x3 Cramer's Rule Matrix tapno magun-od ti $targetN-$targetP-$targetK NPK balance."
                                AppLanguage.CEBUANO -> "Gikalkula pinaagi sa 3x3 Cramer's Rule Matrix aron maabot ang $targetN-$targetP-$targetK NPK target."
                            },
                            fontSize = 13.sp,
                            color = FarmTextSecondary
                        )

                        if (showMathDetails) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFA5D6A7), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = result.cramerMatrixExplanation,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1B2E1B)
                            )
                        }
                    }
                }
            }

            // FARM ADVISORY & FIELD GUIDELINES (EASY TO READ STYLED CARDS)
            if (result.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = recommendationsHeaderTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FarmGreenHeader
                )
                Spacer(modifier = Modifier.height(12.dp))

                result.recommendations.forEach { rec ->
                    val isWarning = rec.contains("Warning", ignoreCase = true) || rec.contains("⚠️") || rec.contains("DELAY", ignoreCase = true)
                    val isWeather = rec.contains("Weather", ignoreCase = true) || rec.contains("🌤️")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isWarning -> Color(0xFFFFF3E0)
                                isWeather -> Color(0xFFE1F5FE)
                                else -> Color.White
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when {
                                isWarning -> Color(0xFFFFB74D)
                                isWeather -> Color(0xFF81D4FA)
                                else -> Color(0xFFE0E0E0)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = when {
                                    isWarning -> Icons.Default.CheckCircle
                                    isWeather -> Icons.Default.LocationOn
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = when {
                                    isWarning -> Color(0xFFE65100)
                                    isWeather -> Color(0xFF0288D1)
                                    else -> FarmGreenHeader
                                },
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = rec,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmTextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM FIXED ACTION BUTTONS BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (onSaveComputation != null) {
                    Button(
                        onClick = {
                            onSaveComputation()
                            Toast.makeText(context, "Saved to History!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_computation")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = btnSaveText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_export_pdf")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(btnExportText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(btnBackText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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


