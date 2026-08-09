package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.domain.models.AppLanguage
import com.example.ui.components.LanguageBar
import com.example.ui.theme.FarmBrownLight
import com.example.ui.theme.FarmBrownPrimary
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import com.example.ui.theme.FarmYellowAccent
import com.example.ui.viewmodel.SoilRecommendation
import com.example.ui.viewmodel.SoilReport
import com.example.util.PdfExportHelper
import kotlinx.coroutines.delay

@Composable
fun SoilAnalysisScreen(
    crop: String,
    soilType: String,
    nitrogen: String,
    phosphorus: String,
    potassium: String,
    organicMatter: String,
    recommendation: SoilRecommendation?,
    activeReport: SoilReport?,
    savedReports: List<SoilReport>,
    isGeminiAnalyzing: Boolean = false,
    geminiError: String? = null,
    onCropChange: (String) -> Unit,
    onSoilTypeChange: (String) -> Unit,
    onNitrogenChange: (String) -> Unit,
    onPhosphorusChange: (String) -> Unit,
    onPotassiumChange: (String) -> Unit,
    onOrganicMatterChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onGenerateCustom: (crop: String, type: String, n: String, p: String, k: String, om: String, ph: Double, moisture: Double) -> Unit,
    onAnalyzeWithGemini: ((android.graphics.Bitmap, String, (Boolean) -> Unit) -> Unit)? = null,
    onDismissGeminiError: (() -> Unit)? = null,
    onSaveReport: () -> Unit,
    onDeleteReport: (SoilReport) -> Unit,
    onSelectSavedReport: (SoilReport) -> Unit,
    onBack: () -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 0 = Camera/Upload, 1 = Manual Input, 2 = Soil Health Dashboard, 3 = Reports History
    var selectedTab by remember { mutableIntStateOf(if (activeReport != null) 2 else 0) }

    // Camera Scan & ImageCapture States
    val imageCapture = remember {
        try {
            androidx.camera.core.ImageCapture.Builder()
                .setCaptureMode(androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
        } catch (e: Exception) {
            null
        }
    }

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
    var uploadedFileName by remember { mutableStateOf<String?>(null) }

    // Visual Loading Indicator States for Gemini API & Camera Workflow
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var scanStepText by remember { mutableStateOf("Initializing Gemini AI Analysis...") }

    LaunchedEffect(isGeminiAnalyzing, isSimulatingScan) {
        if (isGeminiAnalyzing || isSimulatingScan) {
            scanProgress = 0.12f
            scanStepText = "1/3: Capturing & pre-processing soil image..."
            kotlinx.coroutines.delay(800)
            if (isGeminiAnalyzing || isSimulatingScan) {
                scanProgress = 0.42f
                scanStepText = "2/3: Analyzing spectral chroma with Gemini 3.5 Flash..."
            }
            kotlinx.coroutines.delay(1400)
            if (isGeminiAnalyzing || isSimulatingScan) {
                scanProgress = 0.78f
                scanStepText = "3/3: Extrapolating N-P-K nutrient values & pH level..."
            }
            kotlinx.coroutines.delay(1400)
            if (isGeminiAnalyzing || isSimulatingScan) {
                scanProgress = 0.95f
                scanStepText = "3/3: Finalizing custom fertilizer plan..."
            }
        } else {
            scanProgress = 0f
            scanStepText = ""
        }
    }

    // Upload soil analysis report file (CSV/PDF/Image)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedImageUri = uri
            uploadedFileName = uri.lastPathSegment ?: "soil_analysis_sample.jpg"
        }
    }

    fun triggerImageAnalysis() {
        if (onAnalyzeWithGemini != null) {
            if (uploadedImageUri != null) {
                val bitmap = try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        android.graphics.ImageDecoder.decodeBitmap(android.graphics.ImageDecoder.createSource(context.contentResolver, uploadedImageUri!!))
                    } else {
                        android.graphics.BitmapFactory.decodeStream(context.contentResolver.openInputStream(uploadedImageUri!!))
                    }
                } catch (e: Exception) {
                    null
                }
                if (bitmap != null) {
                    onAnalyzeWithGemini(bitmap, crop) { success ->
                        if (success) {
                            selectedTab = 2
                        }
                    }
                    return
                }
            }

            if (hasCameraPermission && imageCapture != null) {
                try {
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : androidx.camera.core.ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(imageProxy: androidx.camera.core.ImageProxy) {
                                val bitmap = imageProxy.toBitmap()
                                imageProxy.close()
                                onAnalyzeWithGemini(bitmap, crop) { success ->
                                    if (success) {
                                        selectedTab = 2
                                    }
                                }
                            }

                            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                                exception.printStackTrace()
                                isSimulatingScan = true
                            }
                        }
                    )
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Fallback synthetic image for Gemini or local simulation
            val fallbackBitmap = android.graphics.Bitmap.createBitmap(512, 512, android.graphics.Bitmap.Config.ARGB_8888).apply {
                eraseColor(android.graphics.Color.rgb(85, 55, 35))
            }
            onAnalyzeWithGemini(fallbackBitmap, crop) { success ->
                if (success) {
                    selectedTab = 2
                }
            }
        } else {
            isSimulatingScan = true
        }
    }

    var selectedSwatch by remember { mutableStateOf("Clay Loam") }
    var inputPh by remember { mutableStateOf(6.2f) }
    var inputMoisture by remember { mutableStateOf(48.0f) }

    // Custom PPM fields
    var customNPpm by remember { mutableStateOf("22") }
    var customPPpm by remember { mutableStateOf("18") }
    var customKPpm by remember { mutableStateOf("140") }

    var showResetLabConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F3))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Language Bar
        LanguageBar(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Top Back Row & Header Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FarmTextDark)
            }
            val screenTitle = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Soil Health Dashboard"
                AppLanguage.TAGALOG -> "Dashboard ng Kalusugan ng Lupa"
                AppLanguage.TAGLISH -> "Soil Health Dashboard"
                AppLanguage.ILOCANO -> "Dashboard ti Daga"
                AppLanguage.CEBUANO -> "Dashboard sa Kalusugan sa Yuta"
            }
            Text(
                text = screenTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("AI Ready", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }

        // Header Info Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Analytics",
                    tint = FarmBrownHeader,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    val bannerTitle = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Soil Analysis & Health Dashboard"
                        AppLanguage.TAGALOG -> "Pagsusuri sa Lupa at Kalusugan"
                        AppLanguage.TAGLISH -> "Soil Analysis & Health Dashboard"
                        AppLanguage.ILOCANO -> "Panagrukod ken Dashboard ti Daga"
                        AppLanguage.CEBUANO -> "Pagsusi ug Dashboard sa Yuta"
                    }
                    val bannerDesc = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Upload soil test data, scan chromatic samples, or enter lab metrics to view visual soil health diagnostics."
                        AppLanguage.TAGALOG -> "Kumuha ng litrato ng lupa o ipasok ang resulta sa lab para sa pagsusuri ng kalusugan ng lupa."
                        AppLanguage.TAGLISH -> "Upload soil test data, mag-scan ng soil photo, o mag-enter ng lab metrics."
                        AppLanguage.ILOCANO -> "Mangikabil ti litrato ti daga wenno datos ti lab tapno makita ti sukat ti daga."
                        AppLanguage.CEBUANO -> "I-upload ang litrato sa yuta o ibutang ang lab data para sa pagsusi sa yuta."
                    }
                    Text(
                        text = bannerTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = bannerDesc,
                        fontSize = 12.sp,
                        color = FarmTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (geminiError != null) {
            val isInvalidImage = geminiError.contains("INVALID IMAGE DETECTED", ignoreCase = true)
            AlertDialog(
                onDismissRequest = {
                    uploadedImageUri = null
                    uploadedFileName = null
                    selectedTab = 0
                    scanProgress = 0f
                    onDismissGeminiError?.invoke()
                },
                containerColor = Color.White,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = if (isInvalidImage) "INVALID IMAGE DETECTED" else "Gemini Soil Analysis Notice",
                        fontWeight = FontWeight.Bold,
                        color = if (isInvalidImage) Color(0xFFD32F2F) else Color(0xFF212121),
                        fontSize = 18.sp
                    )
                },
                text = {
                    if (isInvalidImage) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "The scanned image is non-soil or unreadable. Analysis results have been automatically invalidated.",
                                fontSize = 13.sp,
                                color = Color(0xFF424242),
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    } else {
                        Text(
                            text = geminiError,
                            fontSize = 13.sp,
                            color = Color(0xFF212121),
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            uploadedImageUri = null
                            uploadedFileName = null
                            selectedTab = 0
                            scanProgress = 0f
                            onDismissGeminiError?.invoke()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInvalidImage) Color(0xFFD32F2F) else FarmBrownDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isInvalidImage) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RESTART SCANNING", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            } else {
                                Text("OK", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs Row (Camera/Upload | Input Form | Dashboard | History)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFE8E1))
                .border(1.dp, FarmBorder, RoundedCornerShape(12.dp))
                .padding(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tab0Text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Upload/Scan"
                    AppLanguage.TAGALOG -> "Litratuhan"
                    AppLanguage.TAGLISH -> "Upload/Scan"
                    AppLanguage.ILOCANO -> "Litrato"
                    AppLanguage.CEBUANO -> "Litrato sa Yuta"
                }
                val tab1Text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Lab Input"
                    AppLanguage.TAGALOG -> "Mano-mano"
                    AppLanguage.TAGLISH -> "Lab Input"
                    AppLanguage.ILOCANO -> "Isurat"
                    AppLanguage.CEBUANO -> "Ibutang"
                }
                val tab2Text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "Dashboard"
                    AppLanguage.TAGALOG -> "Dashboard"
                    AppLanguage.TAGLISH -> "Dashboard"
                    AppLanguage.ILOCANO -> "Dashboard"
                    AppLanguage.CEBUANO -> "Dashboard"
                }
                val tab3Text = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "History (${savedReports.size})"
                    AppLanguage.TAGALOG -> "Talaan (${savedReports.size})"
                    AppLanguage.TAGLISH -> "History (${savedReports.size})"
                    AppLanguage.ILOCANO -> "Nakalabas (${savedReports.size})"
                    AppLanguage.CEBUANO -> "Agi-an (${savedReports.size})"
                }

                // Tab 0: Upload / Scan
                TabButton(
                    title = tab0Text,
                    icon = Icons.Default.FileUpload,
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )

                // Tab 1: Input Form
                TabButton(
                    title = tab1Text,
                    icon = Icons.Default.Edit,
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Dashboard
                TabButton(
                    title = tab2Text,
                    icon = Icons.Default.BarChart,
                    isSelected = selectedTab == 2,
                    onClick = {
                        if (activeReport == null) {
                            onGenerate()
                        }
                        selectedTab = 2
                    },
                    modifier = Modifier.weight(1f)
                )

                // Tab 3: History
                TabButton(
                    title = tab3Text,
                    icon = Icons.Default.AccessTime,
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // TAB 0: CAMERA SCAN & FILE UPLOAD
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
                                triggerImageAnalysis()
                            }
                        },
                        enabled = !isGeminiAnalyzing && !isSimulatingScan,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_snap_soil_photo"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3225))
                    ) {
                        if (isGeminiAnalyzing || isSimulatingScan) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scanning...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (!hasCameraPermission) "Enable Camera" else "Snap Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        },
                        enabled = !isGeminiAnalyzing && !isSimulatingScan,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_upload_sample"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        if (isGeminiAnalyzing || isSimulatingScan) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Processing...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Report/Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VIEWPORT CAMERA / FILE SCAN CONTAINER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF281C15))
                        .clickable {
                            if (!hasCameraPermission && uploadedImageUri == null) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                ) {
                    if (uploadedImageUri != null) {
                        AsyncImage(
                            model = uploadedImageUri,
                            contentDescription = "Uploaded Soil Data",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (hasCameraPermission) {
                        SoilCameraPreviewView(
                            modifier = Modifier.fillMaxSize(),
                            imageCapture = imageCapture
                        )
                    } else {
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
                                text = "Tap to Enable Camera Spectral Sensor",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Or upload soil lab report PDF, CSV, or sample photo above",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Top Sensor Badge
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
                                text = if (uploadedImageUri != null) "Uploaded Report: ${uploadedFileName ?: "Sample Data"}" else "Gemini Spectral Sensor Active",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Reticle Focus Target (shown when not processing)
                    if (!isGeminiAnalyzing && !isSimulatingScan) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.35f))
                                    .border(2.dp, FarmYellowAccent.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CenterFocusWeak,
                                    contentDescription = "Reticle",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Align soil sample in target reticle",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Interactive Loading Overlay during Gemini / Camera Analysis
                    if (isGeminiAnalyzing || isSimulatingScan) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.88f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { scanProgress },
                                        modifier = Modifier.size(64.dp),
                                        color = FarmYellowAccent,
                                        trackColor = Color.White.copy(alpha = 0.2f),
                                        strokeWidth = 4.dp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = FarmYellowAccent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${(scanProgress * 100).toInt()}% Analysed",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { scanProgress },
                                    modifier = Modifier
                                        .fillMaxWidth(0.82f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = FarmYellowAccent,
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = scanStepText,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Bottom Scan Action Button
                    Button(
                        onClick = { triggerImageAnalysis() },
                        enabled = !isSimulatingScan && !isGeminiAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(42.dp)
                            .align(Alignment.BottomCenter)
                            .testTag("btn_simulate_chromatic_scan"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3225))
                    ) {
                        if (isGeminiAnalyzing || isSimulatingScan) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyzing Soil... (${(scanProgress * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FarmYellowAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze Soil Sample with Gemini AI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    if (isSimulatingScan) {
                        LaunchedEffect(Unit) {
                            delay(1200)
                            isSimulatingScan = false
                            onGenerateCustom(crop, selectedSwatch, "Low", "Medium", "Medium", "2-4%", 6.2, 48.0)
                            selectedTab = 2 // Switch to Dashboard
                            Toast.makeText(context, "Soil Analysis Complete! Displaying Dashboard.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Floating Gemini Analysis Status Card
                if (isGeminiAnalyzing || isSimulatingScan) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF281C15)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarmYellowAccent.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = FarmYellowAccent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Gemini 3.5 Flash Processing Image...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = scanStepText,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${(scanProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmYellowAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { scanProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = FarmYellowAccent,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calibration Swatches
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
                        Triple("Silt Clay", Color(0xFF362116), "Silt Clay"),
                        Triple("Peat", Color(0xFF231610), "Peat")
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
                                        "Clay Loam" -> onGenerateCustom(crop, typeValue, "Low", "Medium", "Medium", "2-4%", 6.2, 48.0)
                                        "Sandy Loam" -> onGenerateCustom(crop, typeValue, "Low", "Low", "Medium", "<2%", 6.8, 35.0)
                                        "Silt Clay" -> onGenerateCustom(crop, typeValue, "Medium", "High", "Low", ">4%", 5.8, 52.0)
                                        "Peat" -> onGenerateCustom(crop, typeValue, "High", "Medium", "Low", ">4%", 5.2, 60.0)
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: MANUAL LAB DATA INPUT FORM
                Column {
                    Text(
                        text = "Laboratory / Field Test Kit Input",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = "Enter measured soil N-P-K levels, pH value, and organic matter percentage.",
                        fontSize = 12.sp,
                        color = FarmTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Target Crop",
                                selected = crop,
                                options = listOf("Rice", "Corn", "Vegetables", "Sugarcane"),
                                onSelect = onCropChange
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Soil Texture",
                                selected = soilType,
                                options = listOf("Clay", "Loam", "Sandy", "Clay Loam", "Silt Clay", "Peat"),
                                onSelect = onSoilTypeChange
                            )
                        }
                    }

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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Soil pH Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, FarmBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soil pH Level: ${String.format("%.1f", inputPh)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmTextDark
                            )
                            val phStatus = when {
                                inputPh < 5.8 -> "Acidic"
                                inputPh in 5.8..7.2 -> "Optimal"
                                else -> "Alkaline"
                            }
                            Text(
                                text = phStatus,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (phStatus == "Optimal") Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = inputPh,
                            onValueChange = { inputPh = it },
                            valueRange = 4.0f..9.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = FarmBrownHeader,
                                activeTrackColor = FarmBrownHeader,
                                inactiveTrackColor = FarmBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showResetLabConfirmDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_reset_lab_entries"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Lab Data", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD32F2F))
                        }

                        Button(
                            onClick = {
                                onGenerateCustom(
                                    crop,
                                    soilType,
                                    nitrogen,
                                    phosphorus,
                                    potassium,
                                    organicMatter,
                                    inputPh.toDouble(),
                                    inputMoisture.toDouble()
                                )
                                selectedTab = 2 // Switch to Dashboard
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .testTag("btn_generate_soil_rec"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FarmBrownHeader)
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GENERATE DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (showResetLabConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showResetLabConfirmDialog = false },
                        modifier = Modifier.testTag("dialog_reset_lab_confirm"),
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
                                text = "Reset Manual Lab Entries?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = FarmTextDark
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to reset all custom manual soil laboratory entries? This action will restore crop, soil texture, N-P-K nutrient status, and pH to default levels.",
                                fontSize = 13.sp,
                                color = FarmTextDark
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onCropChange("Rice")
                                    onSoilTypeChange("Loam")
                                    onNitrogenChange("Low")
                                    onPhosphorusChange("Medium")
                                    onPotassiumChange("Medium")
                                    onOrganicMatterChange("2-4%")
                                    inputPh = 6.2f
                                    inputMoisture = 48.0f
                                    showResetLabConfirmDialog = false
                                    Toast.makeText(context, "Manual lab entries reset to defaults", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_confirm_reset_lab")
                            ) {
                                Text("Reset Data", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = { showResetLabConfirmDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_cancel_reset_lab")
                            ) {
                                Text("Cancel", fontSize = 12.sp, color = FarmTextDark)
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            2 -> {
                // TAB 2: VISUAL SOIL HEALTH DASHBOARD
                SoilHealthDashboardView(
                    report = activeReport,
                    onSaveReport = {
                        onSaveReport()
                        Toast.makeText(context, "Soil report saved to history!", Toast.LENGTH_SHORT).show()
                    },
                    onExportPdf = {
                        if (activeReport != null) {
                            PdfExportHelper.printSoilReport(context, activeReport)
                        } else {
                            Toast.makeText(context, "No report available to export", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            3 -> {
                // TAB 3: REPORTS HISTORY
                Column {
                    Text(
                        text = "Saved Soil Analysis Reports (${savedReports.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (savedReports.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
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
                                text = "Run a soil analysis scan or lab input to save your field nutrient history.",
                                fontSize = 12.sp,
                                color = FarmTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        savedReports.forEach { rep ->
                            SavedReportCard(
                                report = rep,
                                onViewDashboard = {
                                    onSelectSavedReport(rep)
                                    selectedTab = 2
                                },
                                onDelete = { onDeleteReport(rep) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FarmBrownHeader else FarmTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) FarmTextDark else FarmTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SoilHealthDashboardView(
    report: SoilReport?,
    onSaveReport: () -> Unit,
    onExportPdf: () -> Unit
) {
    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No soil analysis data available. Please run input or scan first.")
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // OVERALL SOIL HEALTH INDEX (SHI) SCORE CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = FarmBrownHeader,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Soil Health Index (SHI)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (report.healthScore >= 80) Color(0xFFE8F5E9)
                                else if (report.healthScore >= 65) Color(0xFFFFF3E0)
                                else Color(0xFFFFEBEE)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = report.healthStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (report.healthScore >= 80) Color(0xFF2E7D32)
                            else if (report.healthScore >= 65) Color(0xFFE65100)
                            else Color(0xFFC62828)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Circular / Meter Visual Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Score Gauge Dial
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (report.healthScore >= 80) Color(0xFF81C784) else Color(0xFFFFB74D),
                                        if (report.healthScore >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${report.healthScore}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "/100",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crop: ${report.crop} | Soil: ${report.soilType}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.summary,
                            fontSize = 12.sp,
                            color = FarmTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // NUTRIENT LEVEL VISUAL METERS (N, P, K)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nutrient Level Breakdown (N-P-K)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Nitrogen Meter
                NutrientMeterRow(
                    label = "Nitrogen (N)",
                    level = report.nitrogenLevel,
                    ppm = "${report.nitrogenPpm} ppm",
                    fraction = if (report.nitrogenLevel == "High") 0.9f else if (report.nitrogenLevel == "Medium") 0.6f else 0.25f,
                    color = if (report.nitrogenLevel == "Low") Color(0xFFE53935) else Color(0xFF43A047)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phosphorus Meter
                NutrientMeterRow(
                    label = "Phosphorus (P)",
                    level = report.phosphorusLevel,
                    ppm = "${report.phosphorusPpm} ppm",
                    fraction = if (report.phosphorusLevel == "High") 0.9f else if (report.phosphorusLevel == "Medium") 0.65f else 0.3f,
                    color = if (report.phosphorusLevel == "Low") Color(0xFFFB8C00) else Color(0xFF43A047)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Potassium Meter
                NutrientMeterRow(
                    label = "Potassium (K)",
                    level = report.potassiumLevel,
                    ppm = "${report.potassiumPpm} ppm",
                    fraction = if (report.potassiumLevel == "High") 0.95f else if (report.potassiumLevel == "Medium") 0.7f else 0.35f,
                    color = if (report.potassiumLevel == "Low") Color(0xFFFB8C00) else Color(0xFF43A047)
                )
            }
        }

        // INTERACTIVE SOIL pH SPECTRUM GAUGE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Soil pH Spectrum Gauge",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = "${report.phValue} pH",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FarmBrownHeader
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Spectrum Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFE53935), // 4.0 Acidic
                                    Color(0xFFFB8C00), // 5.5
                                    Color(0xFF43A047), // 6.5 Neutral
                                    Color(0xFF1E88E5), // 7.5
                                    Color(0xFF8E24AA)  // 9.0 Alkaline
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("4.0 (Acidic)", fontSize = 10.sp, color = FarmTextSecondary)
                    Text("6.5 (Optimal)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("9.0 (Alkaline)", fontSize = 10.sp, color = FarmTextSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (report.phValue in 5.8..7.2) "✅ Optimal pH balance for ${report.crop}. Nutrient availability is maximized."
                    else if (report.phValue < 5.8) "⚠️ Acidic Soil: Lime application recommended to raise pH towards 6.5."
                    else "⚠️ Alkaline Soil: Gypsum or Ammonium Sulfate recommended to reduce pH.",
                    fontSize = 11.sp,
                    color = FarmTextDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ORGANIC MATTER & MOISTURE GAUGES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, FarmBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAE1)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Grass, contentDescription = null, tint = FarmBrownHeader, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Organic Matter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${report.organicMatterPct}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = FarmBrownHeader)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (report.organicMatterPct / 5.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = FarmBrownHeader,
                        trackColor = Color.White
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, FarmBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Soil Moisture", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${report.moisturePct}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (report.moisturePct / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF2E7D32),
                        trackColor = Color.White
                    )
                }
            }
        }

        // CROP SUITABILITY INDEX
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Crop Suitability Index",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Spacer(modifier = Modifier.height(10.dp))

                val crops = listOf(
                    "Rice" to 92,
                    "Corn" to 78,
                    "Vegetables" to 85,
                    "Sugarcane" to 80
                )

                crops.forEach { (cName, pct) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp), color = FarmTextDark)
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (pct >= 85) Color(0xFF2E7D32) else Color(0xFFFB8C00),
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$pct%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                    }
                }
            }
        }

        // RECOMMENDATIONS & APPLICATION SCHEDULE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FarmBrownHeader, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Field Corrective Action Plan:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmBrownHeader
                )
                Spacer(modifier = Modifier.height(8.dp))

                report.recommendations.forEach { item ->
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
                        Text(text = item, fontSize = 12.sp, color = FarmTextDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Application Split Schedule:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                report.applicationSchedule.forEach { step ->
                    Text(
                        text = "• $step",
                        fontSize = 12.sp,
                        color = FarmBrownHeader,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        // ACTION BUTTONS ROW (Save to History | Export PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onSaveReport,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onExportPdf,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FarmBrownHeader)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NutrientMeterRow(
    label: String,
    level: String,
    ppm: String,
    fraction: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FarmTextDark)
            Text("$level ($ppm)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun SavedReportCard(
    report: SoilReport,
    onViewDashboard: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FarmBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${report.crop} Field — ${report.soilType}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = report.dateFormatted,
                        fontSize = 11.sp,
                        color = FarmTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (report.healthScore >= 80) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Index: ${report.healthScore}/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (report.healthScore >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "N: ${report.nitrogenLevel} • P: ${report.phosphorusLevel} • K: ${report.potassiumLevel} | pH ${report.phValue}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FarmBrownHeader
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onViewDashboard,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmBrownHeader),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("View Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
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
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FarmTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
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
                Text(text = selected, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 13.sp) },
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
fun SoilCameraPreviewView(
    modifier: Modifier = Modifier,
    imageCapture: androidx.camera.core.ImageCapture? = null
) {
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
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Soil Camera Sensor Ready",
                    color = Color.White,
                    fontSize = 13.sp,
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
                                if (imageCapture != null) {
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                                } else {
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                                }
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
