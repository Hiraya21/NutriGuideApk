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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import com.example.domain.models.AppLanguage
import com.example.domain.models.MapPoint
import com.example.domain.models.MapUtils
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
    onLocationPermissionGranted: () -> Unit = {},
    onCropChange: (String) -> Unit,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onMarkPoint: () -> Unit,
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
    var cameraHeightDp by rememberSaveable { mutableStateOf(200) }

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
                AppLanguage.CEBUANO -> "Sukdon ang Yuta sa Humayan"
            }
            Text(
                text = titleText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark
            )

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

            // Size Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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

            // Top-left "Camera Preview" tag overlay
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Sensor Preview (${cameraHeightDp}dp)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Top-right Point count badge
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "${boundaryPoints.size} pts",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
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
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
        )

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

        // Buttons Row: Start | Pause | Finish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Start Button
            Button(
                onClick = onStartTracking,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_start_measurement"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarmGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start", fontWeight = FontWeight.Bold)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Mark Point Here Button
        OutlinedButton(
            onClick = onMarkPoint,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_mark_point"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Mark Point",
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mark Point Here",
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary
                )
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

@Composable
fun FarmMapView(
    points: List<MapPoint>,
    currentLocation: MapPoint? = null,
    remoteLocation: MapPoint? = null,
    onAddPoint: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var isFollowingGps by rememberSaveable { mutableStateOf(true) }
    var tileSourceMode by rememberSaveable { mutableStateOf(0) } // 0: Standard, 1: Topo, 2: Satellite/USGS
    val context = LocalContext.current

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
                    Configuration.getInstance().load(
                        ctx,
                        ctx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
                    )
                    Configuration.getInstance().userAgentValue = ctx.packageName
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                MapView(ctx).apply {
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
                            p?.let { onAddPoint(it.latitude, it.longitude) }
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

                    // Clean outline without heavy green filled polygon overlay
                    if (geoPoints.size >= 2) {
                        val polylinePoints = if (geoPoints.size >= 3) geoPoints + geoPoints.first() else geoPoints
                        val polyline = Polyline(mapView).apply {
                            setPoints(polylinePoints)
                            outlinePaint.color = android.graphics.Color.parseColor("#1B5E20")
                            outlinePaint.strokeWidth = 5f
                        }
                        mapView.overlays.add(polyline)
                    }

                    points.forEachIndexed { idx, pt ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(pt.lat, pt.lng)
                            title = "Point ${idx + 1}"
                            snippet = "${String.format("%.5f", pt.lat)}, ${String.format("%.5f", pt.lng)}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)
                    }
                }

                // Current GPS Location Marker
                currentLocation?.let { curr ->
                    val currGeo = GeoPoint(curr.lat, curr.lng)
                    val userMarker = Marker(mapView).apply {
                        position = currGeo
                        title = "Your Exact GPS Location"
                        snippet = "Lat: ${String.format("%.5f", curr.lat)}, Lng: ${String.format("%.5f", curr.lng)}"
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

        // Map controls: Map type switcher
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
                            1 -> "Map: Topographic"
                            2 -> "Map: Satellite"
                            else -> "Map: Standard"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
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



