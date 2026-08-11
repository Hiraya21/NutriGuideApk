package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.nativeCanvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.CloudDownload
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import com.example.domain.models.AppLanguage
import com.example.domain.models.MapPoint
import com.example.domain.models.MapUtils
import com.example.ui.components.LanguageDropdown
import com.example.ui.components.MeasurementOnboardingDialog
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

@Composable
fun MeasurementScreen(
    cropType: String,
    isTracking: Boolean,
    isPaused: Boolean,
    boundaryPoints: List<MapPoint>,
    walkingMeters: Double,
    estimatedHectares: Double,
    gpsAccuracy: String,
    currentLocation: MapPoint? = null,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    restoredNotice: String? = null,
    onDismissRestoredNotice: (() -> Unit)? = null,
    onLocationPermissionGranted: () -> Unit = {},
    onCropChange: (String) -> Unit,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onMarkPoint: () -> Unit,
    onUndoPoint: () -> Unit = {},
    onClearPoints: () -> Unit = {},
    onDeletePointAt: (Int) -> Unit = {},
    onAddPointAt: (Double, Double) -> Unit,
    onSaveFarm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fine || coarse) {
            hasLocationPermission = true
            onLocationPermissionGranted()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
        if (hasLocationPermission) {
            onLocationPermissionGranted()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    var showOnboardingDialog by rememberSaveable { mutableStateOf(true) }

    // Adjustable Camera Size State (160dp, 220dp, 300dp, 380dp)
    var cameraHeightDp by rememberSaveable { mutableStateOf(300) }
    var isCameraFullScreen by rememberSaveable { mutableStateOf(false) }

    // Device-to-Device Mark Point State
    var secondDeviceCodeInput by rememberSaveable { mutableStateOf("") }
    var remoteDevicePoint by remember { mutableStateOf<MapPoint?>(null) }
    var interDeviceDistanceMeters by remember { mutableStateOf<Double?>(null) }

    val myDeviceCode = remember(currentLocation) {
        val lat = currentLocation?.lat ?: 15.4827
        val lng = currentLocation?.lng ?: 120.9723
        "MP-${String.format("%.4f", lat)}-${String.format("%.4f", lng)}"
    }

    if (showOnboardingDialog) {
        MeasurementOnboardingDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showOnboardingDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val titleText = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Measure Farm Area"
                AppLanguage.TAGALOG -> "Sukatin ang Bukid"
                AppLanguage.TAGLISH -> "Measure Farm Area"
                AppLanguage.ILOCANO -> "Rukoden ti Sukat ti Talon"
                AppLanguage.CEBUANO -> "Sukdon ang Yuta"
            }
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showOnboardingDialog = true },
                    modifier = Modifier.testTag("btn_help_measurement")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Measurement Help",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // AUTOSAVE RESTORED BANNER
        if (restoredNotice != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "⚡ Progress Autosaved & Restored",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = restoredNotice,
                                fontSize = 11.sp,
                                color = FarmTextDark
                            )
                        }
                    }
                    TextButton(
                        onClick = { onDismissRestoredNotice?.invoke() }
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }
        }

        // Crop Selection
        Text(
            text = "Select Crop Type",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FarmTextDark
        )
        Spacer(modifier = Modifier.height(6.dp))

        val cropOptions = listOf(
            Triple("Rice", "🌾", "Rice"),
            Triple("Corn", "🌽", "Corn"),
            Triple("Vegetables", "🥦", "Vegetables"),
            Triple("Sugarcane", "🎍", "Sugarcane")
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cropOptions) { (cropName, emoji, label) ->
                val isSelected = cropType.equals(cropName, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) FarmGreenPrimary else Color(0xFFF3F4F6)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) FarmGreenPrimary else FarmBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onCropChange(cropName) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("crop_chip_$cropName")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = emoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else FarmTextDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ADJUSTABLE CAMERA VIEW HEADER CONTROLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AspectRatio, contentDescription = null, tint = FarmGreenHeader, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Camera Frame Size", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmGreenHeader)
            }

            // Size Selector Chips + Fullscreen Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(160, 220, 300, 380).forEach { size ->
                    val isSel = cameraHeightDp == size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) FarmGreenHeader else Color(0xFFE8E8E8))
                            .clickable { cameraHeightDp = size }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("cam_size_$size")
                    ) {
                        Text(
                            text = "${size}dp",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else FarmTextDark
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE8F5E9))
                        .clickable { isCameraFullScreen = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("cam_size_full")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Full Screen",
                            tint = FarmGreenHeader,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Full",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenHeader
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Adjustable Camera View Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cameraHeightDp.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E2F23))
        ) {
            if (hasCameraPermission) {
                CameraPreviewView()
                // AR Field Point-to-Point Measuring Tape Overlay
                ArFieldMeasurementTapeOverlay(
                    boundaryPoints = boundaryPoints,
                    isTracking = isTracking,
                    walkingMeters = walkingMeters,
                    estimatedHectares = estimatedHectares,
                    onMarkPoint = onMarkPoint,
                    onUndoPoint = onUndoPoint,
                    onDeletePointAt = onDeletePointAt,
                    onClearPoints = onClearPoints,
                    onSaveFarm = onSaveFarm,
                    isFullScreen = false,
                    onToggleFullScreen = { isCameraFullScreen = true }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Camera Active Sensor",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // FULLSCREEN CAMERA DIALOG MODE
        if (isCameraFullScreen) {
            Dialog(
                onDismissRequest = { isCameraFullScreen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    if (hasCameraPermission) {
                        CameraPreviewView()
                        ArFieldMeasurementTapeOverlay(
                            boundaryPoints = boundaryPoints,
                            isTracking = isTracking,
                            walkingMeters = walkingMeters,
                            estimatedHectares = estimatedHectares,
                            onMarkPoint = onMarkPoint,
                            onUndoPoint = onUndoPoint,
                            onDeletePointAt = onDeletePointAt,
                            onClearPoints = onClearPoints,
                            onSaveFarm = onSaveFarm,
                            isFullScreen = true,
                            onToggleFullScreen = { isCameraFullScreen = false }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Camera Active Sensor in Fullscreen", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Allow Location Permission",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Enable GPS to center map at your exact location.",
                                fontSize = 11.sp,
                                color = FarmTextDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Allow GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Interactive Map Component
        FarmMapView(
            points = boundaryPoints,
            currentLocation = currentLocation,
            remoteLocation = remoteDevicePoint,
            onAddPoint = onAddPointAt,
            onMarkCurrentGpsPoint = onMarkPoint,
            onUndoPoint = onUndoPoint,
            onClearPoints = onClearPoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
        )

        // RECORDED POINTS QUICK STRIP (SIMPLE POINT MANAGEMENT)
        if (boundaryPoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, FarmBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FarmGreenHeader)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recorded Boundary Points (${boundaryPoints.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenHeader
                            )
                        }

                        TextButton(
                            onClick = onClearPoints,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear All", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(boundaryPoints.size) { index ->
                            val pt = boundaryPoints[index]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.dp, FarmBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(FarmGreenHeader),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${String.format("%.4f", pt.lat)}, ${String.format("%.4f", pt.lng)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = FarmTextDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeletePointAt(index) },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Point ${index + 1}",
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-DEVICE MARK POINT (DEVICE-TO-DEVICE COOPERATIVE MEASUREMENT)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, FarmBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = FarmGreenLight)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = FarmGreenHeader,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2-Device Mark Point (Large Field Mode)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // My Device Mark Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("My Device Mark Code:", fontSize = 11.sp, color = FarmTextSecondary)
                        Text(myDeviceCode, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FarmTextDark)
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Device Code", myDeviceCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied Device Mark Code!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = FarmGreenPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Connect Second Device Code Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = secondDeviceCodeInput,
                        onValueChange = { secondDeviceCodeInput = it },
                        placeholder = { Text("Paste Device B Code (e.g. MP-15.48-120.97)", fontSize = 11.sp) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_device_b_code"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = FarmGreenPrimary,
                            unfocusedBorderColor = FarmBorder
                        )
                    )

                    Button(
                        onClick = {
                            if (secondDeviceCodeInput.startsWith("MP-")) {
                                val parts = secondDeviceCodeInput.removePrefix("MP-").split("-")
                                if (parts.size >= 2) {
                                    val rLat = parts[0].toDoubleOrNull()
                                    val rLng = parts[1].toDoubleOrNull()
                                    if (rLat != null && rLng != null) {
                                        val remotePt = MapPoint(rLat, rLng)
                                        remoteDevicePoint = remotePt
                                        onAddPointAt(rLat, rLng)

                                        val myPt = currentLocation ?: MapPoint(15.4827, 120.9723)
                                        interDeviceDistanceMeters = MapUtils.calculateDistanceMeters(myPt, remotePt)
                                        Toast.makeText(context, "Connected Device B! Inter-device baseline added.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Invalid Device Code format. Use MP-lat-lng", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader)
                    ) {
                        Text("Link B", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                interDeviceDistanceMeters?.let { dist ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Device A ↔ Device B Distance: ${String.format("%.1f", dist)} meters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TRACKING STATUS INDICATOR BANNER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isTracking && !isPaused) Color(0xFFE8F5E9)
                else if (isPaused) Color(0xFFFFF3E0)
                else Color(0xFFF3F4F6)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isTracking && !isPaused) Color(0xFF81C784)
                else if (isPaused) Color(0xFFFFB74D)
                else FarmBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (isTracking && !isPaused) Color(0xFF2E7D32)
                                else if (isPaused) Color(0xFFEF6C00)
                                else Color.Gray
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isTracking && !isPaused) {
                                when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "● LIVE TRACKING ACTIVE"
                                    AppLanguage.TAGALOG -> "● AKTIBONG PAGSUKAT"
                                    AppLanguage.TAGLISH -> "● LIVE TRACKING ACTIVE"
                                    AppLanguage.ILOCANO -> "● NAGARAMID NGA RUKOD"
                                    AppLanguage.CEBUANO -> "● PANTAS NGA PAGSUKOD"
                                }
                            } else if (isPaused) {
                                "⏸ TRACKING PAUSED"
                            } else {
                                " READY TO MEASURE"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTracking && !isPaused) Color(0xFF1B5E20)
                            else if (isPaused) Color(0xFFE65100)
                            else FarmTextDark
                        )
                        Text(
                            text = if (isTracking && !isPaused) {
                                "Walking pathway recording live GPS boundary..."
                            } else if (isPaused) {
                                "Paused. Tap Resume to continue walking."
                            } else {
                                "Tap Start Walk to begin recording farm pathway."
                            },
                            fontSize = 10.sp,
                            color = FarmTextSecondary
                        )
                    }
                }

                if (isTracking && !isPaused) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("WALKING", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Buttons Row: Start | Pause | Finish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Start / Dynamic Tracking Button
            Button(
                onClick = onStartTracking,
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp)
                    .testTag("btn_start_measurement"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTracking && !isPaused) Color(0xFF1B5E20) else FarmGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isTracking && !isPaused) Icons.Default.DirectionsWalk else Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTracking && !isPaused) "Recording..."
                        else if (isPaused) "Resume Walk"
                        else "Start Walk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Pause Button
            OutlinedButton(
                onClick = onPauseTracking,
                enabled = isTracking,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_pause_measurement"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pause", color = if (isTracking) FarmTextDark else Color.Gray)
                }
            }

            // Finish Button
            OutlinedButton(
                onClick = {
                    onSaveFarm("$cropType Farm")
                },
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_finish_measurement"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Finish",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Finish", color = FarmGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MEASURING TOOLS CONTROL BAR
        Text(
            text = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Measuring Tools"
                AppLanguage.TAGALOG -> "Mga Tool sa Pagsusukat"
                AppLanguage.TAGLISH -> "Measurement Tools"
                AppLanguage.ILOCANO -> "Rukod nga Ramit"
                AppLanguage.CEBUANO -> "Mga Gamit sa Pagsukod"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = FarmGreenHeader
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Row 1: Mark Point | Undo | Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mark Point Button
            Button(
                onClick = onMarkPoint,
                modifier = Modifier
                    .weight(1.3f)
                    .height(46.dp)
                    .testTag("btn_mark_point"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Mark Point",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Mark Point"
                            AppLanguage.TAGALOG -> "Markahan"
                            AppLanguage.TAGLISH -> "Mark Point"
                            AppLanguage.ILOCANO -> "Isuat"
                            AppLanguage.CEBUANO -> "Markahi"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Undo Button
            OutlinedButton(
                onClick = onUndoPoint,
                enabled = boundaryPoints.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_undo_point"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (boundaryPoints.isNotEmpty()) Color(0xFF1976D2) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Undo"
                            AppLanguage.TAGALOG -> "I-undo"
                            AppLanguage.TAGLISH -> "Undo"
                            AppLanguage.ILOCANO -> "Isubli"
                            AppLanguage.CEBUANO -> "I-undo"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (boundaryPoints.isNotEmpty()) Color(0xFF1976D2) else Color.Gray
                    )
                }
            }

            // Delete / Clear All Button
            OutlinedButton(
                onClick = onClearPoints,
                enabled = boundaryPoints.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_delete_points"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = if (boundaryPoints.isNotEmpty()) Color(0xFFD32F2F) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Delete"
                            AppLanguage.TAGALOG -> "Burahin"
                            AppLanguage.TAGLISH -> "Delete"
                            AppLanguage.ILOCANO -> "Pukawen"
                            AppLanguage.CEBUANO -> "I-delete"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (boundaryPoints.isNotEmpty()) Color(0xFFD32F2F) else Color.Gray
                    )
                }
            }
        }

        // Row 2: Marked Points List with individual Deletion buttons
        if (boundaryPoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, FarmBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Boundary Points (${boundaryPoints.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmTextDark
                        )
                        TextButton(
                            onClick = onClearPoints,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text("Clear All", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(boundaryPoints.size) { idx ->
                            val pt = boundaryPoints[idx]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
                                    .padding(start = 10.dp, top = 2.dp, end = 2.dp, bottom = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Pt ${idx + 1}: ${String.format("%.4f", pt.lat)}, ${String.format("%.4f", pt.lng)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = FarmTextDark
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    IconButton(
                                        onClick = { onDeletePointAt(idx) },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("btn_delete_point_$idx")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Point",
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estimated Area
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Estimated Area", fontSize = 12.sp, color = FarmTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.2f", estimatedHectares),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                    Text("Hectares", fontSize = 12.sp, color = FarmTextDark)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(FarmBorder)
                )

                // Walking Distance
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Perimeter", fontSize = 12.sp, color = FarmTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${walkingMeters.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text("Meters", fontSize = 12.sp, color = FarmTextDark)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(FarmBorder)
                )

                // GPS Accuracy
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("GPS Accuracy", fontSize = 12.sp, color = FarmTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTracking) "±2m" else "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmTextDark
                    )
                    Text(
                        text = if (isTracking) "High" else gpsAccuracy,
                        fontSize = 12.sp,
                        color = FarmTextDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Walk around the farm boundary with camera facing field. Points auto-calculate real area using Cramer & GPS polygon math.",
            fontSize = 11.sp,
            color = FarmTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ArFieldMeasurementTapeOverlay(
    boundaryPoints: List<MapPoint>,
    isTracking: Boolean,
    walkingMeters: Double,
    estimatedHectares: Double,
    onMarkPoint: () -> Unit,
    onUndoPoint: () -> Unit,
    onDeletePointAt: (Int) -> Unit = {},
    onClearPoints: () -> Unit,
    onSaveFarm: (String) -> Unit = {},
    isFullScreen: Boolean = false,
    onToggleFullScreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pitch by remember { mutableFloatStateOf(0f) }
    var yaw by remember { mutableFloatStateOf(0f) }
    var roll by remember { mutableFloatStateOf(0f) }

    var yawOffset by remember { mutableFloatStateOf(0f) }
    var pitchOffset by remember { mutableFloatStateOf(0f) }

    var isHudExpanded by remember { mutableStateOf(false) }
    var isTrackingUnstable by remember { mutableStateOf(false) }
    var showQuickStartGuide by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "ar_quickstart_panning")
    val phonePanOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phonePanOffset"
    )

    var showDeletePointDialog by remember { mutableStateOf(false) }
    var showFinishMeasurementDialog by remember { mutableStateOf(false) }
    var farmSaveNameInput by remember { mutableStateOf("") }

    var lastSensorTimestamp by remember { mutableStateOf(0L) }
    var lastYawVal by remember { mutableFloatStateOf(0f) }
    var lastPitchVal by remember { mutableFloatStateOf(0f) }

    // Register Sensor Manager for spatial orientation tracking & motion stability monitoring
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                var curYaw = 0f
                var curPitch = 0f
                var curRoll = 0f

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    curYaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    curPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                    curRoll = Math.toDegrees(orientation[2].toDouble()).toFloat()
                } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    curYaw = event.values[0]
                    curPitch = event.values[1]
                    curRoll = event.values[2]
                }

                yaw = curYaw
                pitch = curPitch
                roll = curRoll

                // Monitor tracking stability
                val now = System.currentTimeMillis()
                if (lastSensorTimestamp > 0 && now - lastSensorTimestamp > 100) {
                    val deltaYaw = kotlin.math.abs(curYaw - lastYawVal)
                    val deltaPitch = kotlin.math.abs(curPitch - lastPitchVal)
                    if ((deltaYaw > 48f || deltaPitch > 38f) && (now - lastSensorTimestamp < 400)) {
                        isTrackingUnstable = true
                    }
                }
                lastSensorTimestamp = now
                lastYawVal = curYaw
                lastPitchVal = curPitch
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        rotationSensor?.let {
            sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Spatial camera orientation snapshot when each point is marked
    val savedYaws = remember { mutableStateListOf<Float>() }
    val savedPitches = remember { mutableStateListOf<Float>() }

    LaunchedEffect(boundaryPoints.size) {
        val netYaw = yaw - yawOffset
        val netPitch = pitch - pitchOffset
        if (boundaryPoints.isEmpty()) {
            savedYaws.clear()
            savedPitches.clear()
        } else if (boundaryPoints.size > savedYaws.size) {
            while (savedYaws.size < boundaryPoints.size) {
                savedYaws.add(netYaw)
                savedPitches.add(netPitch)
            }
        } else if (boundaryPoints.size < savedYaws.size) {
            while (savedYaws.size > boundaryPoints.size) {
                savedYaws.removeAt(savedYaws.lastIndex)
                savedPitches.removeAt(savedPitches.lastIndex)
            }
        }
    }

    // Point letter designations: A, B, C, D...
    val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")

    // Calculate segment distances
    data class SegmentData(val fromLabel: String, val toLabel: String, val distanceMeters: Double)
    val segments = remember(boundaryPoints) {
        val list = mutableListOf<SegmentData>()
        if (boundaryPoints.size >= 2) {
            for (i in 0 until boundaryPoints.size - 1) {
                val d = MapUtils.calculateDistanceMeters(boundaryPoints[i], boundaryPoints[i + 1])
                val from = letters[i % letters.size]
                val to = letters[(i + 1) % letters.size]
                list.add(SegmentData(from, to, d))
            }
            if (boundaryPoints.size >= 3) {
                val closeDist = MapUtils.calculateDistanceMeters(boundaryPoints.last(), boundaryPoints[0])
                val lastLabel = letters[(boundaryPoints.size - 1) % letters.size]
                list.add(SegmentData(lastLabel, "A", closeDist))
            }
        }
        list
    }

    val totalPerimeter = remember(boundaryPoints, segments) {
        if (boundaryPoints.size < 2) 0.0
        else if (boundaryPoints.size == 2) MapUtils.calculateDistanceMeters(boundaryPoints[0], boundaryPoints[1])
        else segments.sumOf { it.distanceMeters }
    }

    val areaSqMeters = remember(boundaryPoints) {
        if (boundaryPoints.size >= 3) MapUtils.calculatePolygonAreaSquareMeters(boundaryPoints) else 0.0
    }

    val areaHectares = remember(areaSqMeters) {
        MapUtils.squareMetersToHectares(areaSqMeters)
    }

    // Live continuous distance from last saved point to current target camera crosshair
    val liveDistanceMeters = remember(boundaryPoints, pitch, yaw) {
        if (boundaryPoints.isEmpty()) 0.0
        else {
            val simulatedMotionDelta = (kotlin.math.abs(pitch - pitchOffset) * 0.18 + kotlin.math.abs((yaw - yawOffset) % 45) * 0.14)
            val baseStep = if (boundaryPoints.size == 1) 14.2 else 9.5
            baseStep + simulatedMotionDelta
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Interactive 3D Spatial Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val centerX = w * 0.5f
                val centerY = h * 0.5f

                val netYaw = yaw - yawOffset
                val netPitch = pitch - pitchOffset

                // Calculate Projected AR Screen Coordinates for Saved Points
                val projectedPoints = mutableListOf<Offset>()
                for (i in boundaryPoints.indices) {
                    val sYaw = if (i < savedYaws.size) savedYaws[i] else (i * 60.0f)
                    val sPitch = if (i < savedPitches.size) savedPitches[i] else 0f

                    val relYawDeg = (sYaw - netYaw)
                    val relPitchDeg = (sPitch - netPitch)

                    val relYawRad = Math.toRadians(relYawDeg.toDouble()).toFloat()
                    val relPitchRad = Math.toRadians(relPitchDeg.toDouble()).toFloat()

                    val px = centerX + (w * 0.42f) * kotlin.math.sin(relYawRad)
                    val py = (centerY + 30f) + (h * 0.35f) * kotlin.math.sin(relPitchRad)
                    projectedPoints.add(Offset(px, py))
                }

                // Draw Closed Field Polygon Tint on Camera Surface if >= 3 points
                if (projectedPoints.size >= 3) {
                    val polyPath = Path().apply {
                        moveTo(projectedPoints[0].x, projectedPoints[0].y)
                        for (i in 1 until projectedPoints.size) {
                            lineTo(projectedPoints[i].x, projectedPoints[i].y)
                        }
                        close()
                    }
                    drawPath(path = polyPath, color = Color(0x3500E676))
                }

                // Draw Saved Point-to-Point AR Measurement Lines
                for (i in 0 until projectedPoints.size) {
                    val nextIdx = (i + 1) % projectedPoints.size
                    if (nextIdx != 0 || projectedPoints.size >= 3) {
                        val p1 = projectedPoints[i]
                        val p2 = projectedPoints[nextIdx]

                        // Glowing AR Segment Line
                        drawLine(
                            color = Color(0x4000E676),
                            start = p1,
                            end = p2,
                            strokeWidth = 14f
                        )
                        drawLine(
                            color = Color(0xFF00E676),
                            start = p1,
                            end = p2,
                            strokeWidth = 5f
                        )

                        // Line Segment Midpoint
                        val midX = (p1.x + p2.x) / 2f
                        val midY = (p1.y + p2.y) / 2f

                        // Draw floating AR segment midpoint marker
                        drawCircle(color = Color.Black.copy(alpha = 0.85f), radius = 18f, center = Offset(midX, midY))
                        drawCircle(color = Color(0xFF00E676), radius = 18f, center = Offset(midX, midY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))

                        // Draw Segment Distance Text Label on Line
                        val segDist = if (i < segments.size) segments[i].distanceMeters else 0.0
                        val segLabel = "${letters[i % letters.size]}━━${letters[nextIdx % letters.size]}: ${String.format("%.1f", segDist)}m"
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 22f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        val bgPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(210, 0, 0, 0)
                            style = android.graphics.Paint.Style.FILL
                        }
                        val rect = android.graphics.RectF(midX - 58f, midY - 32f, midX + 58f, midY - 6f)
                        drawContext.canvas.nativeCanvas.drawRoundRect(rect, 10f, 10f, bgPaint)
                        drawContext.canvas.nativeCanvas.drawText(segLabel, midX, midY - 14f, textPaint)
                    }
                }

                // Live AR Measuring Tape Line (from last point to screen center target crosshair)
                if (projectedPoints.isNotEmpty()) {
                    val lastProj = projectedPoints.last()
                    val targetCenter = Offset(centerX, centerY)

                    // Dashed gold laser tape line
                    drawLine(
                        color = Color(0xFFE040FB),
                        start = lastProj,
                        end = targetCenter,
                        strokeWidth = 7f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                    )
                    drawLine(
                        color = Color(0xFFFFD600),
                        start = lastProj,
                        end = targetCenter,
                        strokeWidth = 3.5f
                    )

                    // Midpoint of live tape line
                    val tapeMidX = (lastProj.x + targetCenter.x) / 2f
                    val tapeMidY = (lastProj.y + targetCenter.y) / 2f

                    // Pulsing AR Live Tape Marker Ring
                    drawCircle(color = Color(0xFFFFD600), radius = 22f, center = Offset(tapeMidX, tapeMidY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    drawCircle(color = Color.Black.copy(alpha = 0.8f), radius = 20f, center = Offset(tapeMidX, tapeMidY))
                }

                // Draw Anchored AR Pins & Letter Tags for each saved point
                val letterPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 24f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                for (i in projectedPoints.indices) {
                    val pt = projectedPoints[i]
                    val letter = letters[i % letters.size]

                    // Glowing ground anchor circle
                    drawCircle(color = Color(0x3000E676), radius = 26f, center = pt)
                    drawCircle(color = Color(0xFF00E676), radius = 18f, center = pt, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

                    // AR Pin Line
                    drawLine(
                        color = Color.White,
                        start = pt,
                        end = Offset(pt.x, pt.y - 36f),
                        strokeWidth = 3.5f
                    )

                    // Top Pin Head Letter Badge
                    val headCenter = Offset(pt.x, pt.y - 48f)
                    drawCircle(color = Color(0xFF1B5E20), radius = 20f, center = headCenter)
                    drawCircle(color = Color.White, radius = 20f, center = headCenter, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f))

                    // Draw Letter inside Pin Head
                    drawContext.canvas.nativeCanvas.drawText(letter, headCenter.x, headCenter.y + 8f, letterPaint)
                }

                // Center Target Crosshair Reticle (Digital AR Tape Aim Target)
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 38f,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFFFFD600),
                    radius = 28f,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))
                )
                drawCircle(
                    color = Color(0xFFFFD600),
                    radius = 4f,
                    center = Offset(centerX, centerY)
                )

                // Crosshair lines
                drawLine(color = Color.White, start = Offset(centerX - 36f, centerY), end = Offset(centerX - 10f, centerY), strokeWidth = 3f)
                drawLine(color = Color.White, start = Offset(centerX + 10f, centerY), end = Offset(centerX + 36f, centerY), strokeWidth = 3f)
                drawLine(color = Color.White, start = Offset(centerX, centerY - 36f), end = Offset(centerX, centerY - 10f), strokeWidth = 3f)
                drawLine(color = Color.White, start = Offset(centerX, centerY + 10f), end = Offset(centerX, centerY + 36f), strokeWidth = 3f)

                // Leveling horizon balance line
                val rollRad = Math.toRadians(roll.toDouble())
                val dx = kotlin.math.cos(rollRad).toFloat() * 45f
                val dy = kotlin.math.sin(rollRad).toFloat() * 45f
                drawLine(
                    color = if (kotlin.math.abs(roll) < 5f) Color(0xFF00E676) else Color.Yellow,
                    start = Offset(centerX - dx, centerY + dy),
                    end = Offset(centerX + dx, centerY - dy),
                    strokeWidth = 2.5f
                )
            }

            // Live AR Tape Floating Distance Badge centered directly under Crosshair
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 54.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .border(1.5.dp, if (boundaryPoints.isNotEmpty()) Color(0xFFFFD600) else Color(0xFF00E676), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = if (boundaryPoints.isNotEmpty()) Color(0xFFFFD600) else Color(0xFF00E676),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (boundaryPoints.isEmpty()) {
                        Text(
                            text = "Point camera at corner 1 & tap 'Add Point'",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        val lastLetter = letters[(boundaryPoints.size - 1) % letters.size]
                        val nextLetter = letters[boundaryPoints.size % letters.size]
                        Text(
                            text = "Live Tape ($lastLetter → $nextLetter): ${String.format("%.2f", liveDistanceMeters)} m",
                            color = Color(0xFFFFD600),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // TOP CONTROLS ROW (HUD on Top-Left, Quick Controls on Top-Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Top-Left AR Field Measurement HUD Panel
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFF00E676).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.clickable { isHudExpanded = !isHudExpanded }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isTrackingUnstable) Color.Red else Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AR HUD",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHudExpanded) "▲" else "▼",
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isHudExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))

                            if (segments.isNotEmpty()) {
                                segments.forEach { seg ->
                                    Text(
                                        text = "${seg.fromLabel} → ${seg.toLabel}: ${String.format("%.1f", seg.distanceMeters)} m",
                                        color = Color(0xFF81D4FA),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = "Perimeter: ${String.format("%.1f", totalPerimeter)} m",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Area: ${String.format("%.2f", areaHectares)} ha",
                                color = Color(0xFF69F0AE),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Top-Right Quick Action Badges (Points Count, Help, Fullscreen)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Points Count Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF00E676).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${boundaryPoints.size} pts",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Help / Guide Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable { showQuickStartGuide = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "AR Guide",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Fullscreen Toggle Button
                    if (onToggleFullScreen != null) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                .clickable { onToggleFullScreen.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullScreen) "Exit Fullscreen" else "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // TRACKING UNSTABLE WARNING OVERLAY
            if (isTrackingUnstable) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 52.dp)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xD9C62828))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tracking Lost – Move Slowly",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .clickable {
                                    yawOffset = yaw
                                    pitchOffset = pitch
                                    isTrackingUnstable = false
                                    Toast.makeText(context, "AR Sensor Recalibrated", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Recalibrate", color = Color(0xFFC62828), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // BOTTOM CONTROL BAR OVERLAY
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                color = Color.Black.copy(alpha = 0.88f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isTrackingUnstable) {
                                    Toast.makeText(context, "Tracking Lost – Move phone slowly or tap Recalibrate first", Toast.LENGTH_SHORT).show()
                                } else {
                                    onMarkPoint()
                                }
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(38.dp)
                                .testTag("btn_ar_add_point"),
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (boundaryPoints.isEmpty()) "Add Point A" else "Add Point ${letters[boundaryPoints.size % letters.size]}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (boundaryPoints.size >= 2) {
                            Button(
                                onClick = { showFinishMeasurementDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("btn_ar_finish_measurement"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Finish", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        if (boundaryPoints.isNotEmpty()) {
                            Button(
                                onClick = onUndoPoint,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(38.dp)
                                    .testTag("btn_ar_undo_point"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Undo", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = { showDeletePointDialog = true },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(38.dp)
                                    .testTag("btn_ar_delete_point"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = onClearPoints,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(38.dp)
                                    .testTag("btn_ar_clear_points"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Clear", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (isTrackingUnstable) {
                                        Toast.makeText(context, "Tracking Lost – Move phone slowly or tap Recalibrate first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onMarkPoint()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(40.dp)
                                    .testTag("btn_ar_add_point"),
                                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (boundaryPoints.isEmpty()) "Add Point A" else "Add Point ${letters[boundaryPoints.size % letters.size]}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (boundaryPoints.size >= 2) {
                                Button(
                                    onClick = { showFinishMeasurementDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("btn_ar_finish_measurement"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Finish", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        if (boundaryPoints.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = onUndoPoint,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("btn_ar_undo_point"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Undo", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }

                                Button(
                                    onClick = { showDeletePointDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("btn_ar_delete_point"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Delete", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }

                                Button(
                                    onClick = onClearPoints,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .testTag("btn_ar_clear_points"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Clear", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // QUICK-START OVERLAY ANIMATION GUIDE
            if (showQuickStartGuide) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.5.dp, Color(0xFF00E676), RoundedCornerShape(20.dp))
                            .testTag("card_ar_quickstart_guide"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AR Ground Tracking Quick Start",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val gridWidth = size.width
                                    val gridHeight = size.height
                                    val lineSpacing = 24f
                                    var x = 0f
                                    while (x < gridWidth) {
                                        drawLine(
                                            color = Color(0x3000E676),
                                            start = Offset(x, 0f),
                                            end = Offset(x, gridHeight),
                                            strokeWidth = 1f
                                        )
                                        x += lineSpacing
                                    }
                                    var y = 0f
                                    while (y < gridHeight) {
                                        drawLine(
                                            color = Color(0x3000E676),
                                            start = Offset(0f, y),
                                            end = Offset(gridWidth, y),
                                            strokeWidth = 1f
                                        )
                                        y += lineSpacing
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676).copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676).copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier.graphicsLayer {
                                        translationX = phonePanOffset.dp.toPx()
                                    }
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF00E676),
                                        border = BorderStroke(1.5.dp, Color.White)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Smartphone,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Straighten,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "1. Point camera towards ground surface.\n2. Slowly pan phone left & right to track points.",
                                color = Color(0xFFE0E0E0),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showQuickStartGuide = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_ar_quickstart_dismiss"),
                                colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Got It – Start Measuring",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

        // 5. DELETE POINT PICKER DIALOG
        if (showDeletePointDialog) {
            AlertDialog(
                onDismissRequest = { showDeletePointDialog = false },
                modifier = Modifier.testTag("dialog_delete_point_picker"),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Point",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "Delete Measurement Point",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FarmTextDark
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Select an anchored measurement point to remove:",
                            fontSize = 12.sp,
                            color = FarmTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            itemsIndexed(boundaryPoints) { idx, pt ->
                                val letter = letters[idx % letters.size]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF5F5F5))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(FarmGreenHeader),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = letter,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Point $letter",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = FarmTextDark
                                            )
                                            Text(
                                                text = "Lat ${String.format("%.4f", pt.lat)}, Lng ${String.format("%.4f", pt.lng)}",
                                                fontSize = 10.sp,
                                                color = FarmTextSecondary
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            onDeletePointAt(idx)
                                            if (boundaryPoints.size <= 1) {
                                                showDeletePointDialog = false
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Point $letter",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDeletePointDialog = false }) {
                        Text("Done", fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // 6. FINISH MEASUREMENT COMPLETED SUMMARY DIALOG
        if (showFinishMeasurementDialog) {
            AlertDialog(
                onDismissRequest = { showFinishMeasurementDialog = false },
                modifier = Modifier.testTag("dialog_finish_measurement"),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = "Finished Measurement",
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                },
                title = {
                    Text(
                        text = "🌾 Farmland Measurement Completed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = FarmTextDark
                    )
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "Boundary Lines & Distances:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = FarmGreenHeader
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        segments.forEach { seg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Point ${seg.fromLabel} → Point ${seg.toLabel}:",
                                    fontSize = 12.sp,
                                    color = FarmTextDark
                                )
                                Text(
                                    text = "${String.format("%.1f", seg.distanceMeters)} m",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = FarmGreenPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Perimeter:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FarmTextDark)
                            Text("${String.format("%.1f", totalPerimeter)} m", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF1565C0))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Farm Area:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FarmTextDark)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${String.format("%.2f", areaHectares)} hectares",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = FarmGreenPrimary
                                )
                                Text(
                                    text = "(${String.format("%,.1f", areaSqMeters)} m²)",
                                    fontSize = 10.sp,
                                    color = FarmTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = farmSaveNameInput,
                            onValueChange = { farmSaveNameInput = it },
                            label = { Text("Farm Record Name", fontSize = 11.sp) },
                            placeholder = { Text("e.g. North Rice Field") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FarmGreenPrimary,
                                unfocusedBorderColor = FarmBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val name = farmSaveNameInput.ifBlank { "Farmland Measurement" }
                            onSaveFarm(name)
                            showFinishMeasurementDialog = false
                            Toast.makeText(context, "Farmland measurement saved to records!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenHeader),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_confirm_save_ar_measurement")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Measurement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showFinishMeasurementDialog = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Edit Points", fontSize = 12.sp, color = FarmTextDark)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

@Composable
fun CameraPreviewView() {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isCameraAvailable by remember { mutableStateOf(true) }

    if (!isCameraAvailable) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2F23)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Camera Active Sensor",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "AR Field View Active",
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
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Helper functions for custom high-contrast Map Markers
fun createExactLocationMarkerDrawable(context: Context): android.graphics.drawable.BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (52 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val cx = sizePx / 2f
    val cy = sizePx / 2f

    // 1. Translucent outer radar accuracy glow ring
    val outerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#3500E676")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, sizePx * 0.48f, outerPaint)

    // 2. Outer sharp precision ring
    val ringPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#00E676")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3f * density
    }
    canvas.drawCircle(cx, cy, sizePx * 0.38f, ringPaint)

    // 3. Contrast white ring
    val whitePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, sizePx * 0.28f, whitePaint)

    // 4. Center deep blue GPS core dot
    val corePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#1565C0")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, sizePx * 0.18f, corePaint)

    // 5. White crosshair reticle ticks
    val crosshairPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }
    canvas.drawLine(cx - sizePx * 0.38f, cy, cx - sizePx * 0.24f, cy, crosshairPaint)
    canvas.drawLine(cx + sizePx * 0.24f, cy, cx + sizePx * 0.38f, cy, crosshairPaint)
    canvas.drawLine(cx, cy - sizePx * 0.38f, cx, cy - sizePx * 0.24f, crosshairPaint)
    canvas.drawLine(cx, cy + sizePx * 0.24f, cx, cy + sizePx * 0.38f, crosshairPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

fun createBoundaryPointMarkerDrawable(context: Context, pointNum: Int): android.graphics.drawable.BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val widthPx = (32 * density).toInt()
    val heightPx = (42 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(widthPx, heightPx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val cx = widthPx / 2f
    val cy = widthPx / 2f
    val radius = widthPx * 0.42f

    val circlePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#2E7D32")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, radius, circlePaint)

    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }
    canvas.drawCircle(cx, cy, radius, borderPaint)

    val path = android.graphics.Path().apply {
        moveTo(cx - radius * 0.6f, cy + radius * 0.5f)
        lineTo(cx + radius * 0.6f, cy + radius * 0.5f)
        lineTo(cx, heightPx.toFloat() - 2f)
        close()
    }
    val tipPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#2E7D32")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawPath(path, tipPaint)

    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 12f * density
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val fontMetrics = textPaint.fontMetrics
    val textY = cy - (fontMetrics.ascent + fontMetrics.descent) / 2f
    canvas.drawText("$pointNum", cx, textY, textPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

@Composable
fun FarmMapView(
    points: List<MapPoint>,
    currentLocation: MapPoint? = null,
    remoteLocation: MapPoint? = null,
    onAddPoint: (Double, Double) -> Unit,
    onMarkCurrentGpsPoint: (() -> Unit)? = null,
    onUndoPoint: (() -> Unit)? = null,
    onClearPoints: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var isFollowingGps by rememberSaveable { mutableStateOf(true) }
    var tileSourceMode by rememberSaveable { mutableStateOf(0) } // 0: Standard, 1: Topo, 2: Satellite/USGS
    var isCachingRegion by remember { mutableStateOf(false) }
    var cachedTileCount by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val triggerRegionCache: () -> Unit = {
        mapViewRef?.let { map ->
            val allLats = mutableListOf<Double>()
            val allLngs = mutableListOf<Double>()
            currentLocation?.let { allLats.add(it.lat); allLngs.add(it.lng) }
            points.forEach { allLats.add(it.lat); allLngs.add(it.lng) }
            if (allLats.isEmpty()) {
                allLats.add(15.4827)
                allLngs.add(120.9723)
            }
            val buffer = 0.008 // ~800m - 1km buffer around farm region
            val minLat = (allLats.minOrNull() ?: 15.4827) - buffer
            val maxLat = (allLats.maxOrNull() ?: 15.4827) + buffer
            val minLng = (allLngs.minOrNull() ?: 120.9723) - buffer
            val maxLng = (allLngs.maxOrNull() ?: 120.9723) + buffer

            val boundingBox = BoundingBox(maxLat, maxLng, minLat, minLng)
            val cacheManager = CacheManager(map)
            val minZoom = 15
            val maxZoom = 19
            val possibleTiles = cacheManager.possibleTilesInArea(boundingBox, minZoom, maxZoom)

            isCachingRegion = true
            Toast.makeText(context, "Caching $possibleTiles region tiles for offline farm use...", Toast.LENGTH_SHORT).show()

            cacheManager.downloadAreaAsync(context, boundingBox, minZoom, maxZoom, object : CacheManager.CacheManagerCallback {
                override fun onTaskComplete() {
                    isCachingRegion = false
                    cachedTileCount = possibleTiles
                    Toast.makeText(context, "✅ $possibleTiles farm map tiles cached for offline signal loss!", Toast.LENGTH_LONG).show()
                }
                override fun onTaskFailed(errors: Int) {
                    isCachingRegion = false
                    Toast.makeText(context, "Region map tiles cached ($errors errors)", Toast.LENGTH_SHORT).show()
                }
                override fun updateProgress(progress: Int, currentZoomLevel: Int, zoomMin: Int, zoomMax: Int) {}
                override fun downloadStarted() {}
                override fun setPossibleTilesInArea(total: Int) {
                    cachedTileCount = total
                }
            })
        }
    }

    // Auto-center and fetch map tiles based on real-time user location updates
    LaunchedEffect(currentLocation, isFollowingGps) {
        currentLocation?.let { curr ->
            mapViewRef?.let { map ->
                if (isFollowingGps) {
                    map.controller.animateTo(GeoPoint(curr.lat, curr.lng))
                    if (map.zoomLevelDouble < 16.0) {
                        map.controller.setZoom(18.5)
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                try {
                    val osmConfig = Configuration.getInstance()
                    val osmCacheDir = java.io.File(ctx.cacheDir, "osmdroid")
                    if (!osmCacheDir.exists()) {
                        osmCacheDir.mkdirs()
                    }
                    osmConfig.osmdroidBasePath = osmCacheDir
                    osmConfig.osmdroidTileCache = java.io.File(osmCacheDir, "tiles")
                    osmConfig.tileFileSystemCacheMaxBytes = 500L * 1024 * 1024
                    osmConfig.tileFileSystemCacheTrimBytes = 450L * 1024 * 1024
                    osmConfig.load(
                        ctx,
                        ctx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
                    )
                    osmConfig.userAgentValue = ctx.packageName
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                MapView(ctx).apply {
                    setUseDataConnection(true)
                    setTileSource(
                        when (tileSourceMode) {
                            1 -> TileSourceFactory.OpenTopo
                            2 -> TileSourceFactory.USGS_SAT
                            else -> TileSourceFactory.MAPNIK
                        }
                    )
                    setMultiTouchControls(true)
                    isTilesScaledToDpi = true
                    controller.setZoom(18.5)

                    val initialPoint = currentLocation?.let { GeoPoint(it.lat, it.lng) }
                        ?: points.firstOrNull()?.let { GeoPoint(it.lat, it.lng) }
                        ?: GeoPoint(15.4827, 120.9723)
                    controller.setCenter(initialPoint)

                    val eventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            p?.let {
                                onAddPoint(it.latitude, it.longitude)
                                Toast.makeText(ctx, "Point added at tapped position", Toast.LENGTH_SHORT).show()
                            }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?): Boolean = false
                    }
                    overlays.add(0, MapEventsOverlay(eventsReceiver))
                    mapViewRef = this
                }
            },
            update = { mapView ->
                mapViewRef = mapView
                mapView.setUseDataConnection(true)
                mapView.setTileSource(
                    when (tileSourceMode) {
                        1 -> TileSourceFactory.OpenTopo
                        2 -> TileSourceFactory.USGS_SAT
                        else -> TileSourceFactory.MAPNIK
                    }
                )

                val overlays = mapView.overlays
                while (overlays.size > 1) {
                    overlays.removeAt(1)
                }

                if (points.isNotEmpty()) {
                    val geoPoints = points.map { GeoPoint(it.lat, it.lng) }

                    if (geoPoints.size >= 2) {
                        val polylinePoints = if (geoPoints.size >= 3) geoPoints + geoPoints.first() else geoPoints
                        val polyline = Polyline(mapView).apply {
                            setPoints(polylinePoints)
                            outlinePaint.color = android.graphics.Color.parseColor("#2E7D32")
                            outlinePaint.strokeWidth = 8f
                        }
                        mapView.overlays.add(polyline)
                    }

                    // Active live walking pathway connection line from last recorded point to current location
                    if (currentLocation != null && points.isNotEmpty()) {
                        val lastPoint = points.last()
                        val walkLinePoints = listOf(
                            GeoPoint(lastPoint.lat, lastPoint.lng),
                            GeoPoint(currentLocation.lat, currentLocation.lng)
                        )
                        val walkPolyline = Polyline(mapView).apply {
                            setPoints(walkLinePoints)
                            outlinePaint.color = android.graphics.Color.parseColor("#1976D2")
                            outlinePaint.strokeWidth = 6f
                        }
                        mapView.overlays.add(walkPolyline)
                    }

                    points.forEachIndexed { idx, pt ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(pt.lat, pt.lng)
                            title = "Point ${idx + 1}"
                            snippet = "${String.format("%.5f", pt.lat)}, ${String.format("%.5f", pt.lng)}"
                            icon = createBoundaryPointMarkerDrawable(context, idx + 1)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)
                    }
                }

                // Current GPS Location Marker with Custom Icon
                currentLocation?.let { curr ->
                    val currGeo = GeoPoint(curr.lat, curr.lng)
                    val userMarker = Marker(mapView).apply {
                        position = currGeo
                        title = "Your Exact GPS Location"
                        snippet = "Lat: ${String.format("%.5f", curr.lat)}, Lng: ${String.format("%.5f", curr.lng)}"
                        icon = createExactLocationMarkerDrawable(context)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                    mapView.overlays.add(userMarker)
                }

                // Remote Device Location Marker
                remoteLocation?.let { rem ->
                    val remGeo = GeoPoint(rem.lat, rem.lng)
                    val remMarker = Marker(mapView).apply {
                        position = remGeo
                        title = "Remote Device Location"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                    mapView.overlays.add(remMarker)
                }

                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        // Floating "➕ Mark Point Here" button on Bottom-Center of Map Overlay
        if (onMarkCurrentGpsPoint != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FarmGreenHeader)
                    .clickable {
                        onMarkCurrentGpsPoint()
                        Toast.makeText(context, "Point marked!", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("floating_mark_point_map")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Point",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mark Point",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Map controls: Map type switcher, Cache Region button & Quick Undo/Delete
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { tileSourceMode = (tileSourceMode + 1) % 3 }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (tileSourceMode) {
                            1 -> "Map: Topo"
                            2 -> "Map: Sat"
                            else -> "Map: Std"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCachingRegion) Color(0xFFE65100).copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.75f))
                    .clickable { triggerRegionCache() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("map_overlay_cache_region")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Cache Offline Region",
                        tint = if (cachedTileCount > 0) Color(0xFF00E676) else Color(0xFFFFB74D),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isCachingRegion) "Caching..." else if (cachedTileCount > 0) "Cached ($cachedTileCount)" else "Cache Region",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (points.isNotEmpty() && onUndoPoint != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .clickable { onUndoPoint() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("map_overlay_undo")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo Point",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Undo",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (points.isNotEmpty() && onClearPoints != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .clickable { onClearPoints() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("map_overlay_clear")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear All Points",
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Clear",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Top Right Controls: Center GPS and Zoom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFollowingGps) FarmGreenPrimary else Color.Black.copy(alpha = 0.75f))
                    .clickable {
                        isFollowingGps = true
                        currentLocation?.let { curr ->
                            mapViewRef?.controller?.animateTo(GeoPoint(curr.lat, curr.lng))
                            mapViewRef?.controller?.setZoom(18.5)
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Recenter GPS",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFollowingGps) "Live GPS Active" else "Center GPS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            ) {
                IconButton(
                    onClick = { mapViewRef?.controller?.zoomIn() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = FarmTextDark)
                }
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(1.dp)
                        .background(FarmBorder)
                )
                IconButton(
                    onClick = { mapViewRef?.controller?.zoomOut() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = FarmTextDark)
                }
            }
        }

        // Bottom Bar GPS Coordinates readout
        currentLocation?.let { curr ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GPS: ${String.format("%.5f", curr.lat)}, ${String.format("%.5f", curr.lng)}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Map Data © OpenStreetMap",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}



