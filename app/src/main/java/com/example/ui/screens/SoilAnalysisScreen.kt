package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmBrownDark
import com.example.ui.theme.FarmBrownHeader
import com.example.ui.theme.FarmBrownLight
import com.example.ui.theme.FarmBrownPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.ui.theme.FarmYellowAccent
import com.example.ui.viewmodel.SoilRecommendation

@Composable
fun SoilAnalysisScreen(
    crop: String,
    soilType: String,
    nitrogen: String,
    phosphorus: String,
    potassium: String,
    organicMatter: String,
    recommendation: SoilRecommendation?,
    onCropChange: (String) -> Unit,
    onSoilTypeChange: (String) -> Unit,
    onNitrogenChange: (String) -> Unit,
    onPhosphorusChange: (String) -> Unit,
    onPotassiumChange: (String) -> Unit,
    onOrganicMatterChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Camera Scan, 1 = Manual Input, 2 = Reports
    var selectedTab by remember { mutableIntStateOf(0) }

    // Camera Scan States
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var isSimulatingScan by remember { mutableStateOf(false) }
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedImageUri = uri
            isSimulatingScan = true
        }
    }

    var selectedSwatch by remember { mutableStateOf("Clay Loam") }
    var estimatedPh by remember { mutableStateOf("6.2 (Optimal for Rice)") }
    var moistureContent by remember { mutableStateOf("48% (Sufficient)") }
    var npkStatus by remember { mutableStateOf("N: Low • P: Medium • K: Medium") }

    // Target Fertilizer Recommendations (kg/ha)
    var targetN by remember { mutableStateOf("90") }
    var targetP by remember { mutableStateOf("30") }
    var targetK by remember { mutableStateOf("40") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F3))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FarmTextDark)
            }
            Text(
                text = "Soil Analysis & Recommendation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(36.dp))
        }

        // Header Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Flask",
                    tint = FarmBrownHeader,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "AI Camera Soil Test & Fertilizer Target",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Analyze soil automatically using camera chromatic sensors or input test kit data.",
                        fontSize = 12.sp,
                        color = FarmTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Tab Navigation Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFE8E1))
                .border(1.dp, FarmBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Camera Scan
                val isTab0 = selectedTab == 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isTab0) Color.White else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = if (isTab0) FarmBrownHeader else FarmTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Camera Scan",
                                fontSize = 12.sp,
                                fontWeight = if (isTab0) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTab0) FarmTextDark else FarmTextSecondary
                            )
                        }
                        if (isTab0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(FarmBrownHeader)
                            )
                        }
                    }
                }

                // Tab 2: Manual Input
                val isTab1 = selectedTab == 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isTab1) Color.White else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (isTab1) FarmBrownHeader else FarmTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Manual Input",
                                fontSize = 12.sp,
                                fontWeight = if (isTab1) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTab1) FarmTextDark else FarmTextSecondary
                            )
                        }
                        if (isTab1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(FarmBrownHeader)
                            )
                        }
                    }
                }

                // Tab 3: Reports (0)
                val isTab2 = selectedTab == 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isTab2) Color.White else Color.Transparent)
                        .clickable { selectedTab = 2 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isTab2) FarmBrownHeader else FarmTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reports (0)",
                                fontSize = 12.sp,
                                fontWeight = if (isTab2) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTab2) FarmTextDark else FarmTextSecondary
                            )
                        }
                        if (isTab2) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(FarmBrownHeader)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // TAB 0: CAMERA SCAN

                // Action Buttons Row (Snap Soil Photo | Upload Sample)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (!hasCameraPermission) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                uploadedImageUri = null
                                isSimulatingScan = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_snap_soil_photo"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3225))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!hasCameraPermission) "Enable Camera" else "Snap Soil Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_upload_sample"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Sample", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CAMERA VIEWFINDER CONTAINER (LIVE CAMERA PREVIEW / UPLOADED SAMPLE)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF281C15))
                        .clickable {
                            if (!hasCameraPermission && uploadedImageUri == null) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                ) {
                    // Background Live Camera Preview or Uploaded Sample
                    if (uploadedImageUri != null) {
                        AsyncImage(
                            model = uploadedImageUri,
                            contentDescription = "Uploaded Soil Sample",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (hasCameraPermission) {
                        SoilCameraPreviewView(modifier = Modifier.fillMaxSize())
                    } else {
                        // Camera Permission Request Placeholder
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tap to Enable Camera Sensor",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Live spectral soil sensor requires camera permission",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Top-Left Badge: Spectral Chromatic Sensor Active
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = FarmYellowAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uploadedImageUri != null) "Sample Image Sensor Active" else "Spectral Lens Sensor Active",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Center Reticle Viewfinder
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .border(2.dp, FarmYellowAccent.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusWeak,
                                contentDescription = "Reticle",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSimulatingScan) "Analyzing Soil Chromatic Spectrum..." else "Align soil sample in target reticle",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // Bottom Simulate / Scan Button
                    Button(
                        onClick = {
                            isSimulatingScan = true
                        },
                        enabled = !isSimulatingScan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(42.dp)
                            .align(Alignment.BottomCenter)
                            .testTag("btn_simulate_chromatic_scan"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3225))
                    ) {
                        if (isSimulatingScan) {
                            Text("Analyzing Chromatic Spectrum...", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze Soil with Camera Sensor", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    if (isSimulatingScan) {
                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1500)
                            isSimulatingScan = false
                            onSoilTypeChange(selectedSwatch)
                            onNitrogenChange("Low")
                            onPhosphorusChange("Medium")
                            onPotassiumChange("Medium")
                            onOrganicMatterChange("2-4%")
                            estimatedPh = "6.2 (Optimal for Rice)"
                            moistureContent = "48% (Sufficient)"
                            npkStatus = "N: Low • P: Medium • K: Medium"
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PRESET SAMPLE CALIBRATION SWATCHES
                Text(
                    text = "Preset Sample Calibration Swatches:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val swatches = listOf(
                        Triple("Clay Loam", Color(0xFF4A3225), "Clay Loam"),
                        Triple("Sandy Loam", Color(0xFF8C6246), "Sandy Loam"),
                        Triple("Silt Clay", Color(0xFF362116), "Silt Clay")
                    )

                    swatches.forEach { (name, color, typeValue) ->
                        val isSelected = selectedSwatch == name
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) FarmYellowAccent else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedSwatch = name
                                    onSoilTypeChange(typeValue)
                                    when (name) {
                                        "Clay Loam" -> {
                                            estimatedPh = "6.2 (Optimal for Rice)"
                                            moistureContent = "48% (Sufficient)"
                                            npkStatus = "N: Low • P: Medium • K: Medium"
                                            targetN = "90"
                                            targetP = "30"
                                            targetK = "40"
                                        }
                                        "Sandy Loam" -> {
                                            estimatedPh = "6.8 (Neutral)"
                                            moistureContent = "35% (Dry/Needs Irrigation)"
                                            npkStatus = "N: Low • P: Low • K: Medium"
                                            targetN = "110"
                                            targetP = "45"
                                            targetK = "50"
                                        }
                                        "Silt Clay" -> {
                                            estimatedPh = "5.8 (Slightly Acidic)"
                                            moistureContent = "52% (High Retention)"
                                            npkStatus = "N: Medium • P: High • K: Low"
                                            targetN = "70"
                                            targetP = "20"
                                            targetK = "60"
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OPTICAL SOIL SENSOR ANALYSIS CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, FarmBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAE1)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = FarmBrownHeader,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = FarmBrownHeader,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Optical Soil Sensor Analysis",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmBrownHeader
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Row 1: Detected Soil Texture
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Detected Soil Texture:", fontSize = 12.sp, color = FarmTextSecondary)
                            Text(selectedSwatch, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row 2: Estimated Soil pH
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Soil pH:", fontSize = 12.sp, color = FarmTextSecondary)
                            Text(estimatedPh, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row 3: Moisture Content
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Moisture Content:", fontSize = 12.sp, color = FarmTextSecondary)
                            Text(moistureContent, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row 4: N-P-K Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("N-P-K Status:", fontSize = 12.sp, color = FarmTextSecondary)
                            Text(npkStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // TARGET FERTILIZER RECOMMENDATION (KG/HA)
                Text(
                    text = "Target Fertilizer Recommendation (kg/ha)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Target N
                    OutlinedTextField(
                        value = targetN,
                        onValueChange = { targetN = it },
                        label = { Text("Target N", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_target_n"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = FarmBrownHeader,
                            unfocusedBorderColor = FarmBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Target P₂O₅
                    OutlinedTextField(
                        value = targetP,
                        onValueChange = { targetP = it },
                        label = { Text("Target P₂O₅", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_target_p"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = FarmBrownHeader,
                            unfocusedBorderColor = FarmBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Target K₂O
                    OutlinedTextField(
                        value = targetK,
                        onValueChange = { targetK = it },
                        label = { Text("Target K₂O", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_target_k"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = FarmBrownHeader,
                            unfocusedBorderColor = FarmBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_calculate_target_fertilizer"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmBrownHeader)
                ) {
                    Text("GENERATE FERTILIZER BLEND RECOMMENDATION", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            1 -> {
                // TAB 1: MANUAL INPUT FORM
                Column {
                    Text(
                        text = "Manual Soil Test Kit Input",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = "Select values measured from laboratory or field soil testing kit.",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DropdownSelector(
                        label = "Crop Type",
                        selected = crop,
                        options = listOf("Rice", "Corn", "Vegetables", "Sugarcane"),
                        onSelect = onCropChange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DropdownSelector(
                        label = "Soil Type",
                        selected = soilType,
                        options = listOf("Clay", "Loam", "Sandy", "Clay Loam"),
                        onSelect = onSoilTypeChange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Nitrogen (N)",
                                selected = nitrogen,
                                options = listOf("Low", "Medium", "High"),
                                onSelect = onNitrogenChange
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Phosphorus (P)",
                                selected = phosphorus,
                                options = listOf("Low", "Medium", "High"),
                                onSelect = onPhosphorusChange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Potassium (K)",
                                selected = potassium,
                                options = listOf("Low", "Medium", "High"),
                                onSelect = onPotassiumChange
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Organic Matter",
                                selected = organicMatter,
                                options = listOf("<2%", "2-4%", ">4%"),
                                onSelect = onOrganicMatterChange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onGenerate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_soil_rec"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmBrownHeader)
                    ) {
                        Text("GENERATE RECOMMENDATIONS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            2 -> {
                // TAB 2: REPORTS (HISTORY)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = FarmTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Saved Soil Test Reports Yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scan or enter soil test data to generate and save your field nutrient history.",
                        fontSize = 12.sp,
                        color = FarmTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // DISPLAY GENERATED RECOMMENDATIONS IF AVAILABLE
        recommendation?.let { rec ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FarmBrownHeader, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Field Recommendation Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmBrownHeader
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rec.summary,
                        fontSize = 13.sp,
                        color = FarmTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Specific Recommendations:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    rec.recommendations.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FarmBrownHeader,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = item, fontSize = 13.sp, color = FarmTextDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Application Split Schedule:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    rec.applicationSchedule.forEach { step ->
                        Text(
                            text = "• $step",
                            fontSize = 13.sp,
                            color = FarmBrownHeader,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DropdownSelector(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FarmTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, FarmBorder, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selected, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SoilCameraPreviewView(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isCameraAvailable by remember { mutableStateOf(true) }

    if (!isCameraAvailable) {
        Box(
            modifier = modifier.background(Color(0xFF1E2F23)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Soil Scanner",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Soil Camera Sensor Ready",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Sample Field Preview Active",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                try {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            if (cameraProvider.hasCamera(cameraSelector)) {
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            } else {
                                isCameraAvailable = false
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            isCameraAvailable = false
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                } catch (e: Throwable) {
                    e.printStackTrace()
                    isCameraAvailable = false
                }
                previewView
            },
            onRelease = { view ->
                try {
                    val cameraProvider = ProcessCameraProvider.getInstance(view.context).get()
                    cameraProvider.unbindAll()
                } catch (e: Throwable) {
                    // ignore
                }
            },
            modifier = modifier
        )
    }
}

