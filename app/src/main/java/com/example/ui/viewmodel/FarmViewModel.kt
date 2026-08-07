package com.example.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.FarmRecord
import com.example.data.repository.AgriculturalRegion
import com.example.data.repository.FarmRepository
import com.example.data.repository.GuideRepository
import com.example.data.repository.WeatherRepository
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmWeatherData
import com.example.domain.models.FertilizerItem
import com.example.domain.models.GuideArticle
import com.example.domain.models.MapPoint
import com.example.domain.models.MapUtils
import com.example.domain.models.WeatherRiskLevel
import com.example.domain.models.WeatherScenario
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FarmRepository(database.farmDao())
    val guideRepository = GuideRepository()

    // Database flow
    val allFarms: StateFlow<List<FarmRecord>> = repository.allFarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // History search & stats
    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery = _historySearchQuery.asStateFlow()

    val filteredFarms: StateFlow<List<FarmRecord>> = combine(allFarms, historySearchQuery) { farms, query ->
        if (query.isBlank()) farms
        else farms.filter { it.name.contains(query, ignoreCase = true) || it.cropType.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFarmsCount: StateFlow<Int> = combine(allFarms) { (farms) -> farms.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalAreaHectares: StateFlow<Double> = combine(allFarms) { (farms) ->
        farms.fold(0.0) { acc, item -> acc + item.areaHectares }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Language State with SharedPreferences Persistence
    private val prefs = application.getSharedPreferences("farm_app_prefs", android.content.Context.MODE_PRIVATE)
    private val initialLang = try {
        AppLanguage.valueOf(prefs.getString("selected_lang", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name)
    } catch (e: Exception) {
        AppLanguage.ENGLISH
    }
    private val _currentLanguage = MutableStateFlow(initialLang)
    val currentLanguage = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("selected_lang", language.name).apply()
    }

    // Navigation Tab
    private val _currentTab = MutableStateFlow(0) // 0: Home, 1: Measurement, 2: Fertilizer, 3: Booklet, 4: History
    val currentTab = _currentTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    // Selected guide details route
    private val _selectedGuide = MutableStateFlow<GuideArticle?>(null)
    val selectedGuide = _selectedGuide.asStateFlow()

    fun openGuide(guide: GuideArticle) {
        _selectedGuide.value = guide
        _currentTab.value = 3 // Booklet tab
    }

    fun closeGuide() {
        _selectedGuide.value = null
    }

    // Booklet Search
    private val _bookletSearchQuery = MutableStateFlow("")
    val bookletSearchQuery = _bookletSearchQuery.asStateFlow()

    fun setBookletSearchQuery(query: String) {
        _bookletSearchQuery.value = query
    }

    val bookletArticles: StateFlow<List<GuideArticle>> = combine(bookletSearchQuery) { (query) ->
        guideRepository.searchArticles(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), guideRepository.getArticles())

    // ----------------------------------------------------
    // MEASUREMENT STATE & CALCULATIONS
    // ----------------------------------------------------
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private var locationCallback: LocationCallback? = null

    private val _currentLocation = MutableStateFlow<MapPoint?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()

    private val _selectedCrop = MutableStateFlow("Rice")
    val selectedCrop = _selectedCrop.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _boundaryPoints = MutableStateFlow<List<MapPoint>>(emptyList())
    val boundaryPoints = _boundaryPoints.asStateFlow()

    private val _walkingDistanceMeters = MutableStateFlow(0.0)
    val walkingDistanceMeters = _walkingDistanceMeters.asStateFlow()

    private val _estimatedAreaHectares = MutableStateFlow(0.0)
    val estimatedAreaHectares = _estimatedAreaHectares.asStateFlow()

    private val _gpsAccuracyText = MutableStateFlow("High (±2m)")
    val gpsAccuracyText = _gpsAccuracyText.asStateFlow()

    fun onLocationPermissionGranted() {
        _hasLocationPermission.value = true
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val app = getApplication<Application>()
        val finePerm = ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarsePerm = ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (_currentLocation.value == null) {
            _currentLocation.value = MapPoint(15.4827, 120.9723)
        }

        if (!finePerm && !coarsePerm) {
            _hasLocationPermission.value = false
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    val p = MapPoint(loc.latitude, loc.longitude)
                    _currentLocation.value = p
                    val acc = loc.accuracy.toInt()
                    _gpsAccuracyText.value = "±${acc}m (${if (acc <= 5) "High" else if (acc <= 15) "Medium" else "Low"})"
                }
            }.addOnFailureListener {
                if (_currentLocation.value == null) {
                    _currentLocation.value = MapPoint(15.4827, 120.9723)
                }
            }

            val priority = if (finePerm) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val locationRequest = LocationRequest.Builder(priority, 2000L)
                .setMinUpdateIntervalMillis(1000L)
                .setMinUpdateDistanceMeters(1.0f)
                .build()

            if (locationCallback == null) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val loc = result.lastLocation ?: return
                        val newPoint = MapPoint(loc.latitude, loc.longitude)
                        _currentLocation.value = newPoint
                        val accuracyMeters = loc.accuracy.toInt()
                        _gpsAccuracyText.value = "±${accuracyMeters}m (${if (accuracyMeters <= 5) "High" else if (accuracyMeters <= 15) "Medium" else "Low"})"

                        if (_isTracking.value && !_isPaused.value) {
                            val currentList = _boundaryPoints.value.toMutableList()
                            val lastPoint = currentList.lastOrNull()

                            if (lastPoint == null || MapUtils.calculateDistanceMeters(lastPoint, newPoint) >= 1.5) {
                                currentList.add(newPoint)
                                _boundaryPoints.value = currentList
                                recalculateMeasurementMetrics(currentList)
                            }
                        }
                    }
                }
            }

            locationCallback?.let { cb ->
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    cb,
                    Looper.getMainLooper()
                ).addOnFailureListener {
                    // Fail gracefully if GPS or Location service is unavailable
                    if (_currentLocation.value == null) {
                        _currentLocation.value = MapPoint(15.4827, 120.9723)
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            if (_currentLocation.value == null) {
                _currentLocation.value = MapPoint(15.4827, 120.9723)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            if (_currentLocation.value == null) {
                _currentLocation.value = MapPoint(15.4827, 120.9723)
            }
        }
    }

    fun setCrop(crop: String) {
        _selectedCrop.value = crop
    }

    fun startTracking() {
        _isTracking.value = true
        _isPaused.value = false
        if (_boundaryPoints.value.isEmpty()) {
            val startPoint = _currentLocation.value ?: MapPoint(15.4827, 120.9723)
            _boundaryPoints.value = listOf(startPoint)
        }
        if (_hasLocationPermission.value && locationCallback == null) {
            startLocationUpdates()
        }
    }

    fun pauseTracking() {
        _isPaused.value = true
    }

    fun resumeTracking() {
        _isPaused.value = false
    }

    fun markPoint(lat: Double? = null, lng: Double? = null) {
        val currentList = _boundaryPoints.value.toMutableList()
        val currLoc = _currentLocation.value

        val newLat = lat ?: (currLoc?.lat ?: ((currentList.lastOrNull()?.lat ?: 15.4827) + (java.util.Random().nextDouble() - 0.5) * 0.0006))
        val newLng = lng ?: (currLoc?.lng ?: ((currentList.lastOrNull()?.lng ?: 120.9723) + (java.util.Random().nextDouble() - 0.5) * 0.0006))

        val newPoint = MapPoint(newLat, newLng)
        currentList.add(newPoint)
        _boundaryPoints.value = currentList

        recalculateMeasurementMetrics(currentList)
    }

    fun addManualPointOnMap(lat: Double, lng: Double) {
        val currentList = _boundaryPoints.value.toMutableList()
        currentList.add(MapPoint(lat, lng))
        _boundaryPoints.value = currentList
        recalculateMeasurementMetrics(currentList)
    }

    private fun recalculateMeasurementMetrics(points: List<MapPoint>) {
        val dist = MapUtils.calculateTotalDistance(points)
        _walkingDistanceMeters.value = dist
        
        if (points.size >= 3) {
            val sqMeters = MapUtils.calculatePolygonAreaSquareMeters(points)
            _estimatedAreaHectares.value = MapUtils.squareMetersToHectares(sqMeters)
        } else {
            _estimatedAreaHectares.value = 0.0
        }
    }

    fun resetMeasurement() {
        _isTracking.value = false
        _isPaused.value = false
        _boundaryPoints.value = emptyList()
        _walkingDistanceMeters.value = 0.0
        _estimatedAreaHectares.value = 0.0
    }

    fun saveCompletedFarm(farmName: String = "Rice Farm") {
        viewModelScope.launch {
            val points = _boundaryPoints.value
            val area = if (points.size >= 3) _estimatedAreaHectares.value else (if (_estimatedAreaHectares.value > 0) _estimatedAreaHectares.value else 1.25)
            val dist = if (_walkingDistanceMeters.value > 0) _walkingDistanceMeters.value else 180.0
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val dateStr = dateFormat.format(Date())

            val jsonPoints = if (points.isNotEmpty()) {
                points.joinToString(",", "[", "]") {
                    "{\"lat\":${it.lat},\"lng\":${it.lng}}"
                }
            } else {
                "[{\"lat\":15.4827,\"lng\":120.9723},{\"lat\":15.4835,\"lng\":120.9730},{\"lat\":15.4820,\"lng\":120.9735}]"
            }

            val newRecord = FarmRecord(
                name = farmName.ifBlank { "${_selectedCrop.value} Farm" },
                dateFormatted = dateStr,
                timestamp = System.currentTimeMillis(),
                areaHectares = area,
                perimeterMeters = dist,
                cropType = _selectedCrop.value,
                pointsJson = jsonPoints,
                walkedMeters = dist,
                gpsAccuracy = "High",
                boundaryPointsCount = if (points.isNotEmpty()) points.size else 3
            )

            repository.insertFarm(newRecord)
            resetMeasurement()
            _currentTab.value = 4 // Automatically navigate to History tab
        }
    }

    // ----------------------------------------------------
    // FERTILIZER CALCULATOR STATE & NPK TARGETS
    // ----------------------------------------------------
    private val _fertilizerFarmArea = MutableStateFlow("1.0")
    val fertilizerFarmArea = _fertilizerFarmArea.asStateFlow()

    private val _targetN = MutableStateFlow("120")
    val targetN = _targetN.asStateFlow()

    private val _targetP = MutableStateFlow("40")
    val targetP = _targetP.asStateFlow()

    private val _targetK = MutableStateFlow("30")
    val targetK = _targetK.asStateFlow()

    private val _draftRestoredMessage = MutableStateFlow<String?>(null)
    val draftRestoredMessage = _draftRestoredMessage.asStateFlow()

    fun clearDraftRestoredMessage() {
        _draftRestoredMessage.value = null
    }

    private val _availableFertilizers = MutableStateFlow(
        listOf(
            FertilizerItem("complete", "Complete (14-14-14)", defaultPrice = 1850.0, bagsPerHectare = 4.0, isSelected = true, customPrice = 1850.0, nPercent = 14.0, pPercent = 14.0, kPercent = 14.0),
            FertilizerItem("ammophos", "AmmoPhos (16-20-0)", defaultPrice = 1650.0, bagsPerHectare = 3.0, isSelected = true, customPrice = 1650.0, nPercent = 16.0, pPercent = 20.0, kPercent = 0.0),
            FertilizerItem("urea", "Urea (46-0-0)", defaultPrice = 1450.0, bagsPerHectare = 3.0, isSelected = true, customPrice = 1450.0, nPercent = 46.0, pPercent = 0.0, kPercent = 0.0),
            FertilizerItem("nk", "N-K Fertilizer (17-0-17)", defaultPrice = 1750.0, bagsPerHectare = 3.0, isSelected = false, customPrice = 1750.0, nPercent = 17.0, pPercent = 0.0, kPercent = 17.0),
            FertilizerItem("dap", "DAP (18-46-0)", defaultPrice = 2100.0, bagsPerHectare = 2.5, isSelected = false, customPrice = 2100.0, nPercent = 18.0, pPercent = 46.0, kPercent = 0.0),
            FertilizerItem("mop", "MOP (0-0-60)", defaultPrice = 1950.0, bagsPerHectare = 2.0, isSelected = false, customPrice = 1950.0, nPercent = 0.0, pPercent = 0.0, kPercent = 60.0),
            FertilizerItem("amsulf", "Ammonium Sulfate (21-0-0)", defaultPrice = 950.0, bagsPerHectare = 2.0, isSelected = false, customPrice = 950.0, nPercent = 21.0, pPercent = 0.0, kPercent = 0.0),
            FertilizerItem("organic", "Organic Fertilizer (2-1-2)", defaultPrice = 450.0, bagsPerHectare = 10.0, isSelected = false, customPrice = 450.0, nPercent = 2.0, pPercent = 1.0, kPercent = 2.0)
        )
    )
    val availableFertilizers = _availableFertilizers.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult = _calculationResult.asStateFlow()

    fun setFertilizerFarmArea(areaStr: String) {
        _fertilizerFarmArea.value = areaStr
        saveDraftState()
    }

    fun setTargetN(value: String) {
        _targetN.value = value
        saveDraftState()
    }

    fun setTargetP(value: String) {
        _targetP.value = value
        saveDraftState()
    }

    fun setTargetK(value: String) {
        _targetK.value = value
        saveDraftState()
    }

    fun toggleFertilizerSelected(id: String) {
        val list = _availableFertilizers.value.map { item ->
            if (item.id == id) item.copy(isSelected = !item.isSelected) else item
        }
        _availableFertilizers.value = list
        saveDraftState()
    }

    fun toggleFertilizerAvailability(id: String) {
        val list = _availableFertilizers.value.map { item ->
            if (item.id == id) item.copy(isAvailable = !item.isAvailable) else item
        }
        _availableFertilizers.value = list
        saveDraftState()
    }

    fun updateFertilizerPrice(id: String, newPrice: Double) {
        val list = _availableFertilizers.value.map { item ->
            if (item.id == id) item.copy(customPrice = newPrice) else item
        }
        _availableFertilizers.value = list
        saveDraftState()
    }

    fun updateFertilizerNutrients(id: String, n: Double, p: Double, k: Double) {
        val list = _availableFertilizers.value.map { item ->
            if (item.id == id) item.copy(nPercent = n, pPercent = p, kPercent = k) else item
        }
        _availableFertilizers.value = list
        saveDraftState()
    }

    fun runCalculation() {
        val area = _fertilizerFarmArea.value.toDoubleOrNull() ?: 1.0
        val nReq = _targetN.value.toDoubleOrNull() ?: 120.0
        val pReq = _targetP.value.toDoubleOrNull() ?: 40.0
        val kReq = _targetK.value.toDoubleOrNull() ?: 30.0

        val crop = _selectedCrop.value
        val selected = _availableFertilizers.value.filter { it.isSelected && it.isAvailable }

        var matrixExplanation = ""

        if (selected.size == 3) {
            val f1 = selected[0]
            val f2 = selected[1]
            val f3 = selected[2]

            val solution = com.example.util.MatrixSolver.solve3x3(
                f1.nPercent, f1.pPercent, f1.kPercent,
                f2.nPercent, f2.pPercent, f2.kPercent,
                f3.nPercent, f3.pPercent, f3.kPercent,
                nReq, pReq, kReq
            )

            if (solution.isUniqueSolution) {
                f1.bagsPerHectare = solution.xKgPerHa / 50.0
                f2.bagsPerHectare = solution.yKgPerHa / 50.0
                f3.bagsPerHectare = solution.zKgPerHa / 50.0
                matrixExplanation = solution.explanation
            }
        }

        val breakdowns = selected.map { item ->
            val totalBags = item.bagsPerHectare * area
            val roundedBags = kotlin.math.ceil(totalBags * 10) / 10.0
            val cost = roundedBags * item.customPrice
            FertilizerBreakdown(item.name, item.customPrice, roundedBags, cost)
        }

        val totalCost = breakdowns.sumOf { it.totalCost }
        val (recs, schedule) = generateFertilizerRecommendations(area, crop, selected, breakdowns)
        _calculationResult.value = CalculationResult(area, breakdowns, totalCost, recs, schedule, matrixExplanation)
        saveDraftState()
    }

    // Auto Save & Restore Draft Helper Logic
    private fun saveDraftState() {
        try {
            val editor = prefs.edit()
            editor.putString("draft_crop", _selectedCrop.value)
            editor.putString("draft_area", _fertilizerFarmArea.value)
            editor.putString("draft_target_n", _targetN.value)
            editor.putString("draft_target_p", _targetP.value)
            editor.putString("draft_target_k", _targetK.value)
            editor.putString("draft_soil_crop", _soilCrop.value)
            editor.putString("draft_soil_type", _soilType.value)
            editor.putString("draft_soil_n", _nitrogenLevel.value)
            editor.putString("draft_soil_p", _phosphorusLevel.value)
            editor.putString("draft_soil_k", _potassiumLevel.value)

            // Persist custom fertilizer prices and availability for local suppliers
            _availableFertilizers.value.forEach { fert ->
                editor.putFloat("fert_price_${fert.id}", fert.customPrice.toFloat())
                editor.putBoolean("fert_avail_${fert.id}", fert.isAvailable)
                editor.putBoolean("fert_select_${fert.id}", fert.isSelected)
            }

            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkAndRestoreAutoSavedProgress() {
        try {
            val savedCrop = prefs.getString("draft_crop", null)
            val savedArea = prefs.getString("draft_area", null)
            val savedTargetN = prefs.getString("draft_target_n", null)

            // Restore customized fertilizer prices & availability
            val restoredList = _availableFertilizers.value.map { fert ->
                val priceKey = "fert_price_${fert.id}"
                val availKey = "fert_avail_${fert.id}"
                val selectKey = "fert_select_${fert.id}"

                var updated = fert
                if (prefs.contains(priceKey)) {
                    val p = prefs.getFloat(priceKey, fert.customPrice.toFloat()).toDouble()
                    updated = updated.copy(customPrice = p)
                }
                if (prefs.contains(availKey)) {
                    val a = prefs.getBoolean(availKey, fert.isAvailable)
                    updated = updated.copy(isAvailable = a)
                }
                if (prefs.contains(selectKey)) {
                    val s = prefs.getBoolean(selectKey, fert.isSelected)
                    updated = updated.copy(isSelected = s)
                }
                updated
            }
            _availableFertilizers.value = restoredList

            if (savedCrop != null || savedArea != null || savedTargetN != null) {
                if (savedCrop != null) _selectedCrop.value = savedCrop
                if (savedArea != null) _fertilizerFarmArea.value = savedArea
                if (savedTargetN != null) _targetN.value = savedTargetN
                prefs.getString("draft_target_p", null)?.let { _targetP.value = it }
                prefs.getString("draft_target_k", null)?.let { _targetK.value = it }
                prefs.getString("draft_soil_crop", null)?.let { _soilCrop.value = it }
                prefs.getString("draft_soil_type", null)?.let { _soilType.value = it }
                prefs.getString("draft_soil_n", null)?.let { _nitrogenLevel.value = it }
                prefs.getString("draft_soil_p", null)?.let { _phosphorusLevel.value = it }
                prefs.getString("draft_soil_k", null)?.let { _potassiumLevel.value = it }

                _draftRestoredMessage.value = "Auto-Save Restored: Your previous farm progress & settings were safely recovered."
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateFertilizerRecommendations(
        area: Double,
        crop: String,
        selected: List<FertilizerItem>,
        breakdowns: List<FertilizerBreakdown>
    ): Pair<List<String>, List<String>> {
        val recs = mutableListOf<String>()
        val schedule = mutableListOf<String>()

        if (selected.isEmpty()) {
            recs.add("No available fertilizers selected. Please enable and select at least one fertilizer.")
            schedule.add("No active application schedule. Check available fertilizer options above.")
            return Pair(recs, schedule)
        }

        val totalBagsCount = breakdowns.sumOf { it.bagsNeeded }
        val missingPrices = selected.count { it.customPrice <= 0 }

        val wData = _weatherData.value
        val adv = wData.advisory
        recs.add("🌤️ Regional Weather (${wData.locationName}): Temp ${wData.currentTempC}°C | Rain ${String.format("%.1f", wData.precipitationSumMm)} mm | ${wData.weatherCondition}")
        recs.add("⚠️ Fertilizer Weather Warning: ${adv.title} - ${adv.summary}")

        if (adv.riskLevel != WeatherRiskLevel.OPTIMAL) {
            schedule.add("🚨 CRITICAL WEATHER SAFETY: ${adv.actionStep}")
        } else {
            schedule.add("✅ WEATHER APPLICATION WINDOW: ${adv.bestApplicationWindow}")
        }

        recs.add("Land Measurement & Crop: $area hectare(s) for $crop.")
        recs.add("Calculation Result: Total ${String.format("%.1f", totalBagsCount)} bags required across ${selected.size} selected available fertilizer(s).")

        if (missingPrices > 0) {
            recs.add("Notice: $missingPrices selected fertilizer(s) currently set at ₱00.0. Enter market price per bag to update field cost estimation.")
        }

        selected.forEach { item ->
            val bags = kotlin.math.ceil(item.bagsPerHectare * area * 10) / 10.0
            val priceNote = if (item.customPrice > 0) "₱${String.format("%,.0f", item.customPrice)}/bag" else "₱00.0 (Price pending input)"
            val costNote = if (item.customPrice > 0) "₱${String.format("%,.0f", bags * item.customPrice)}" else "₱0.00"

            when (item.id) {
                "urea" -> {
                    recs.add("Urea ($bags bags for $area ha $crop @ $priceNote = $costNote): High-nitrogen source for rapid foliage and tiller growth.")
                    when (crop.lowercase()) {
                        "corn" -> schedule.add("Corn Topdress (25-30 DAP): Apply $bags bags Urea along rows before side-dressing.")
                        "vegetables" -> schedule.add("Vegetable Growth Stage: Apply $bags bags Urea in weekly split doses.")
                        "sugarcane" -> schedule.add("Sugarcane Topdress (45-60 DAP): Broadcast $bags bags Urea along cane rows.")
                        else -> {
                            val halfBags = String.format("%.1f", bags / 2.0)
                            schedule.add("Rice 1st Topdress (21 DAT): Apply $halfBags bags Urea in 2-3cm standing water.")
                            schedule.add("Rice 2nd Topdress (40 DAT): Apply $halfBags bags Urea at Panicle Initiation.")
                        }
                    }
                }
                "complete" -> {
                    recs.add("Complete 14-14-14 ($bags bags for $area ha $crop @ $priceNote = $costNote): Balanced N-P-K for root development and tiller strength.")
                    schedule.add("Basal Land Prep (0-14 DAT/DAP): Broadcast $bags bags Complete 14-14-14 during final harrowing.")
                }
                "dap" -> {
                    recs.add("DAP 18-46-0 ($bags bags for $area ha $crop @ $priceNote = $costNote): High phosphorus for root expansion and seedling vigor.")
                    schedule.add("Basal Application: Place $bags bags DAP directly near seedling root zone.")
                }
                "mop" -> {
                    recs.add("MOP / Potash ($bags bags for $area ha $crop @ $priceNote = $costNote): Enhances grain filling, disease resistance, and stalk integrity.")
                    schedule.add("Grain Filling Stage (40-50 DAT/DAP): Apply $bags bags MOP to improve grain weight.")
                }
                "amsulf" -> {
                    recs.add("Ammonium Sulfate ($bags bags for $area ha $crop @ $priceNote = $costNote): Supplies nitrogen and sulfur for plant greening.")
                    schedule.add("Mid-Tillering Stage (25-30 DAT/DAP): Apply $bags bags Ammonium Sulfate.")
                }
                "organic" -> {
                    recs.add("Organic Fertilizer ($bags bags for $area ha $crop @ $priceNote = $costNote): Enhances soil organic matter and water holding capacity.")
                    schedule.add("Pre-Planting (-14 Days): Broadcast $bags bags Organic Fertilizer during initial land preparation.")
                }
                else -> {
                    recs.add("${item.name} ($bags bags for $area ha $crop @ $priceNote = $costNote): Field application recommended.")
                    schedule.add("Active Stage: Apply $bags bags ${item.name}.")
                }
            }
        }

        return Pair(recs, schedule)
    }

    fun clearCalculationResult() {
        _calculationResult.value = null
    }

    // ----------------------------------------------------
    // SOIL ANALYSIS STATE
    // ----------------------------------------------------
    private val _soilCrop = MutableStateFlow("Rice")
    val soilCrop = _soilCrop.asStateFlow()

    private val _soilType = MutableStateFlow("Clay Loam")
    val soilType = _soilType.asStateFlow()

    private val _nitrogenLevel = MutableStateFlow("Low")
    val nitrogenLevel = _nitrogenLevel.asStateFlow()

    private val _phosphorusLevel = MutableStateFlow("Medium")
    val phosphorusLevel = _phosphorusLevel.asStateFlow()

    private val _potassiumLevel = MutableStateFlow("Medium")
    val potassiumLevel = _potassiumLevel.asStateFlow()

    private val _organicMatter = MutableStateFlow("2-4%")
    val organicMatter = _organicMatter.asStateFlow()

    private val _soilRecommendation = MutableStateFlow<SoilRecommendation?>(null)
    val soilRecommendation = _soilRecommendation.asStateFlow()

    fun setSoilCrop(crop: String) { _soilCrop.value = crop }
    fun setSoilType(type: String) { _soilType.value = type }
    fun setNitrogenLevel(level: String) { _nitrogenLevel.value = level }
    fun setPhosphorusLevel(level: String) { _phosphorusLevel.value = level }
    fun setPotassiumLevel(level: String) { _potassiumLevel.value = level }
    fun setOrganicMatter(om: String) { _organicMatter.value = om }

    fun generateSoilRecommendation() {
        val n = _nitrogenLevel.value
        val p = _phosphorusLevel.value
        val k = _potassiumLevel.value
        val soil = _soilType.value
        val om = _organicMatter.value

        val recs = mutableListOf<String>()
        val schedule = mutableListOf<String>()

        val wData = _weatherData.value
        val adv = wData.advisory
        if (adv.riskLevel != WeatherRiskLevel.OPTIMAL) {
            recs.add("🌤️ Weather Precaution (${wData.locationName}): ${adv.title} — ${adv.actionStep}")
        }

        if (n == "Low") {
            recs.add("High Nitrogen deficiency detected. Boost basal application with Urea or Complete fertilizer.")
            schedule.add("Basal (0-14 DAT): 2 bags Complete (14-14-14) / ha")
            schedule.add("Active Tillering (21-28 DAT): 2 bags Urea (46-0-0) / ha")
            schedule.add("Panicle Initiation (40-45 DAT): 1.5 bags Urea + 1 bag MOP / ha")
        } else if (n == "Medium") {
            recs.add("Moderate Nitrogen level. Standard 3-split fertilizer schedule recommended.")
            schedule.add("Basal (0-14 DAT): 1.5 bags Complete / ha")
            schedule.add("Active Tillering (21-28 DAT): 1 bag Urea / ha")
            schedule.add("Panicle Initiation (40-45 DAT): 1 bag Urea / ha")
        } else {
            recs.add("Sufficient Nitrogen present. Reduce Urea applications to avoid crop lodging and pest buildup.")
            schedule.add("Basal (0-14 DAT): 1 bag Complete / ha")
            schedule.add("Panicle Initiation: 0.5 bag Urea / ha")
        }

        if (p == "Low") {
            recs.add("Phosphorus is low. Incorporate DAP (18-46-0) during final land preparation to support root branching.")
        }
        if (k == "Low") {
            recs.add("Potassium is deficient. Apply MOP (0-0-60) at booting stage for disease resistance and filled grains.")
        }

        if (soil == "Sandy") {
            recs.add("Sandy soil loses nutrients quickly through leaching. Split fertilizer into 4 smaller top-dressings.")
        } else if (soil == "Clay") {
            recs.add("Clay soil holds water well. Maintain shallow 2-3 cm water layer to maximize nutrient uptake.")
        }

        if (om == "<2%") {
            recs.add("Low Organic Matter. Incorporate 10-20 bags organic compost or paddy straw post-harvest.")
        }

        _soilRecommendation.value = SoilRecommendation(
            summary = "Targeted nutrient recommendations for ${_soilCrop.value} in $soil soil.",
            recommendations = recs,
            applicationSchedule = schedule
        )
    }

    fun clearSoilRecommendation() {
        _soilRecommendation.value = null
    }

    // ----------------------------------------------------
    // ACCOUNT & PREFERENCES MANAGEMENT
    // ----------------------------------------------------
    private val _showDeleteAccountModal = MutableStateFlow(false)
    val showDeleteAccountModal = _showDeleteAccountModal.asStateFlow()

    private val _accountDeletedMessage = MutableStateFlow<String?>(null)
    val accountDeletedMessage = _accountDeletedMessage.asStateFlow()

    fun openDeleteAccountModal() {
        _showDeleteAccountModal.value = true
    }

    fun closeDeleteAccountModal() {
        _showDeleteAccountModal.value = false
    }

    fun dismissAccountDeletedMessage() {
        _accountDeletedMessage.value = null
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            repository.deleteAllFarms()
            resetMeasurement()
            clearCalculationResult()
            clearSoilRecommendation()
            _historySearchQuery.value = ""
            _bookletSearchQuery.value = ""
            _selectedGuide.value = null
            _currentTab.value = 0 // Navigate back to home
            _showDeleteAccountModal.value = false

            val msg = when (_currentLanguage.value) {
                AppLanguage.ENGLISH -> "Account and all farm records have been successfully deleted."
                AppLanguage.TAGALOG -> "Matagumpay na nabura ang iyong account at lahat ng data ng bukid."
                AppLanguage.TAGLISH -> "Account and farm data deleted successfully."
                AppLanguage.ILOCANO -> "Nainaganan ken nabura ammin a rekord ti talon."
                AppLanguage.CEBUANO -> "Nafungkal ug nabura na ang tanang rekord sa farm."
            }
            _accountDeletedMessage.value = msg
        }
    }

    // ----------------------------------------------------
    // WEATHER INTEGRATION & FERTILIZER SAFETY ALERTS
    // ----------------------------------------------------
    private val weatherRepository = WeatherRepository()
    val agriculturalRegions = weatherRepository.defaultRegions

    private val _selectedRegion = MutableStateFlow(weatherRepository.defaultRegions[0])
    val selectedRegion = _selectedRegion.asStateFlow()

    private val _selectedWeatherScenario = MutableStateFlow(WeatherScenario.LIVE_GPS)
    val selectedWeatherScenario = _selectedWeatherScenario.asStateFlow()

    private val _weatherData = MutableStateFlow(FarmWeatherData())
    val weatherData: StateFlow<FarmWeatherData> = _weatherData.asStateFlow()

    init {
        checkAndRestoreAutoSavedProgress()
        refreshWeatherData()
    }

    fun selectWeatherRegion(region: AgriculturalRegion) {
        _selectedRegion.value = region
        refreshWeatherData()
    }

    fun setWeatherScenario(scenario: WeatherScenario) {
        _selectedWeatherScenario.value = scenario
        refreshWeatherData()
    }

    fun refreshWeatherData() {
        viewModelScope.launch {
            val currLoc = currentLocation.value
            val reg = _selectedRegion.value
            val lat = currLoc?.lat ?: reg.lat
            val lng = currLoc?.lng ?: reg.lng
            val locName = if (currLoc != null && _selectedWeatherScenario.value == WeatherScenario.LIVE_GPS) "GPS Field Location (${reg.province})" else reg.name

            val data = weatherRepository.fetchWeatherForLocation(
                lat = lat,
                lng = lng,
                locationName = locName,
                scenario = _selectedWeatherScenario.value
            )
            _weatherData.value = data
        }
    }

    // ----------------------------------------------------
    // HISTORY ACTIONS
    // ----------------------------------------------------
    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun deleteFarmRecord(farm: FarmRecord) {
        viewModelScope.launch {
            repository.deleteFarm(farm)
        }
    }

    fun deleteAllFarms() {
        viewModelScope.launch {
            repository.deleteAllFarms()
        }
    }
}

data class CalculationResult(
    val farmArea: Double,
    val items: List<FertilizerBreakdown>,
    val totalCost: Double,
    val recommendations: List<String> = emptyList(),
    val applicationSchedule: List<String> = emptyList(),
    val cramerMatrixExplanation: String = ""
)

data class FertilizerBreakdown(
    val name: String,
    val pricePerBag: Double,
    val bagsNeeded: Double,
    val totalCost: Double
)

data class SoilRecommendation(
    val summary: String,
    val recommendations: List<String>,
    val applicationSchedule: List<String>
)
