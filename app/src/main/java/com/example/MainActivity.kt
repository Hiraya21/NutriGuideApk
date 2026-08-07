package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.ui.components.BottomNavBar
import com.example.ui.components.DeleteAccountModal
import com.example.ui.components.SplashScreen
import com.example.ui.screens.BookletScreen
import com.example.ui.screens.FertilizerScreen
import com.example.ui.screens.GuideDetailScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeasurementScreen
import com.example.ui.screens.SoilAnalysisScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            org.osmdroid.config.Configuration.getInstance().load(
                applicationContext,
                getSharedPreferences("osmdroid", MODE_PRIVATE)
            )
            org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RiceFarmAssistantApp()
            }
        }
    }
}

@Composable
fun RiceFarmAssistantApp(
    viewModel: FarmViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedGuide by viewModel.selectedGuide.collectAsStateWithLifecycle()
    var isSoilAnalysisOpen by remember { mutableStateOf(false) }
    var isSplashVisible by remember { mutableStateOf(true) }

    if (isSplashVisible) {
        SplashScreen(
            onAnimationFinished = { isSplashVisible = false }
        )
        return
    }

    // Measurement states
    val cropType by viewModel.selectedCrop.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val boundaryPoints by viewModel.boundaryPoints.collectAsStateWithLifecycle()
    val walkingMeters by viewModel.walkingDistanceMeters.collectAsStateWithLifecycle()
    val estimatedHectares by viewModel.estimatedAreaHectares.collectAsStateWithLifecycle()
    val gpsAccuracy by viewModel.gpsAccuracyText.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()

    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    // Fertilizer states
    val fertilizerFarmArea by viewModel.fertilizerFarmArea.collectAsStateWithLifecycle()
    val fertilizerList by viewModel.availableFertilizers.collectAsStateWithLifecycle()
    val calculationResult by viewModel.calculationResult.collectAsStateWithLifecycle()

    // Weather states
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val agriculturalRegions = viewModel.agriculturalRegions
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val selectedWeatherScenario by viewModel.selectedWeatherScenario.collectAsStateWithLifecycle()

    // Soil states
    val soilCrop by viewModel.soilCrop.collectAsStateWithLifecycle()
    val soilType by viewModel.soilType.collectAsStateWithLifecycle()
    val nitrogenLevel by viewModel.nitrogenLevel.collectAsStateWithLifecycle()
    val phosphorusLevel by viewModel.phosphorusLevel.collectAsStateWithLifecycle()
    val potassiumLevel by viewModel.potassiumLevel.collectAsStateWithLifecycle()
    val organicMatter by viewModel.organicMatter.collectAsStateWithLifecycle()
    val soilRecommendation by viewModel.soilRecommendation.collectAsStateWithLifecycle()
    val activeSoilReport by viewModel.activeSoilReport.collectAsStateWithLifecycle()
    val savedSoilReports by viewModel.savedSoilReports.collectAsStateWithLifecycle()

    // Booklet states
    val bookletSearchQuery by viewModel.bookletSearchQuery.collectAsStateWithLifecycle()
    val bookletArticles by viewModel.bookletArticles.collectAsStateWithLifecycle()

    // History states
    val historySearchQuery by viewModel.historySearchQuery.collectAsStateWithLifecycle()
    val filteredFarms by viewModel.filteredFarms.collectAsStateWithLifecycle()
    val totalFarmsCount by viewModel.totalFarmsCount.collectAsStateWithLifecycle()
    val totalAreaHectares by viewModel.totalAreaHectares.collectAsStateWithLifecycle()

    // Delete Account modal state
    val isDeleteAccountModalVisible by viewModel.showDeleteAccountModal.collectAsStateWithLifecycle()
    val accountDeletedMessage by viewModel.accountDeletedMessage.collectAsStateWithLifecycle()

    // Autosave notification state
    val restoredSessionNotice by viewModel.restoredSessionNotice.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(accountDeletedMessage) {
        accountDeletedMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissAccountDeletedMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                currentLanguage = currentLanguage,
                onTabSelected = { tab ->
                    isSoilAnalysisOpen = false
                    viewModel.closeGuide()
                    viewModel.selectTab(tab)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isSoilAnalysisOpen) {
                SoilAnalysisScreen(
                    crop = soilCrop,
                    soilType = soilType,
                    nitrogen = nitrogenLevel,
                    phosphorus = phosphorusLevel,
                    potassium = potassiumLevel,
                    organicMatter = organicMatter,
                    recommendation = soilRecommendation,
                    activeReport = activeSoilReport,
                    savedReports = savedSoilReports,
                    onCropChange = { viewModel.setSoilCrop(it) },
                    onSoilTypeChange = { viewModel.setSoilType(it) },
                    onNitrogenChange = { viewModel.setNitrogenLevel(it) },
                    onPhosphorusChange = { viewModel.setPhosphorusLevel(it) },
                    onPotassiumChange = { viewModel.setPotassiumLevel(it) },
                    onOrganicMatterChange = { viewModel.setOrganicMatter(it) },
                    onGenerate = { viewModel.generateSoilRecommendation() },
                    onGenerateCustom = { c, t, n, p, k, om, ph, moisture ->
                        viewModel.generateSoilRecommendationWithCustomValues(c, t, n, p, k, om, ph, moisture)
                    },
                    onSaveReport = { viewModel.saveActiveSoilReport() },
                    onDeleteReport = { viewModel.deleteSoilReport(it) },
                    onSelectSavedReport = { viewModel.selectSavedSoilReport(it) },
                    onBack = { isSoilAnalysisOpen = false }
                )
            } else if (selectedGuide != null) {
                GuideDetailScreen(
                    article = selectedGuide!!,
                    onBack = { viewModel.closeGuide() }
                )
            } else {
                when (currentTab) {
                    0 -> HomeScreen(
                        currentLanguage = currentLanguage,
                        weatherData = weatherData,
                        agriculturalRegions = agriculturalRegions,
                        selectedRegion = selectedRegion,
                        selectedWeatherScenario = selectedWeatherScenario,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onRegionSelected = { reg -> viewModel.selectWeatherRegion(reg) },
                        onScenarioSelected = { scenario -> viewModel.setWeatherScenario(scenario) },
                        onRefreshWeather = { viewModel.refreshWeatherData() },
                        onNavigateToTab = { tab -> viewModel.selectTab(tab) },
                        onOpenSoilAnalysis = { isSoilAnalysisOpen = true },
                        onOpenDeleteAccount = { viewModel.openDeleteAccountModal() }
                    )
                    1 -> MeasurementScreen(
                        cropType = cropType,
                        isTracking = isTracking,
                        isPaused = isPaused,
                        boundaryPoints = boundaryPoints,
                        walkingMeters = walkingMeters,
                        estimatedHectares = estimatedHectares,
                        gpsAccuracy = gpsAccuracy,
                        currentLocation = currentLocation,
                        currentLanguage = currentLanguage,
                        restoredNotice = restoredSessionNotice,
                        onDismissRestoredNotice = { viewModel.dismissRestoredNotice() },
                        onLocationPermissionGranted = { viewModel.onLocationPermissionGranted() },
                        onCropChange = { viewModel.setCrop(it) },
                        onStartTracking = { viewModel.startTracking() },
                        onPauseTracking = { viewModel.pauseTracking() },
                        onMarkPoint = { viewModel.markPoint() },
                        onUndoPoint = { viewModel.undoLastPoint() },
                        onClearPoints = { viewModel.clearAllPoints() },
                        onDeletePointAt = { index -> viewModel.deletePointAt(index) },
                        onAddPointAt = { lat, lng -> viewModel.addManualPointOnMap(lat, lng) },
                        onSaveFarm = { farmName -> viewModel.saveCompletedFarm(farmName) }
                    )
                    2 -> FertilizerScreen(
                        farmArea = fertilizerFarmArea,
                        fertilizerList = fertilizerList,
                        calculationResult = calculationResult,
                        selectedCrop = cropType,
                        currentLanguage = currentLanguage,
                        weatherData = weatherData,
                        agriculturalRegions = agriculturalRegions,
                        selectedRegion = selectedRegion,
                        selectedWeatherScenario = selectedWeatherScenario,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onRegionSelected = { reg -> viewModel.selectWeatherRegion(reg) },
                        onScenarioSelected = { scenario -> viewModel.setWeatherScenario(scenario) },
                        onRefreshWeather = { viewModel.refreshWeatherData() },
                        onAreaChange = { viewModel.setFertilizerFarmArea(it) },
                        onToggleSelected = { id -> viewModel.toggleFertilizerSelected(id) },
                        onToggleAvailability = { id -> viewModel.toggleFertilizerAvailability(id) },
                        onUpdatePrice = { id, price -> viewModel.updateFertilizerPrice(id, price) },
                        onRunCalculation = { viewModel.runCalculation() },
                        onDismissResult = { viewModel.clearCalculationResult() }
                    )
                    3 -> BookletScreen(
                        searchQuery = bookletSearchQuery,
                        articles = bookletArticles,
                        onSearchChange = { viewModel.setBookletSearchQuery(it) },
                        onSelectGuide = { article -> viewModel.openGuide(article) }
                    )
                    4 -> HistoryScreen(
                        searchQuery = historySearchQuery,
                        farms = filteredFarms,
                        totalFarms = totalFarmsCount,
                        totalArea = totalAreaHectares,
                        onSearchChange = { viewModel.setHistorySearchQuery(it) },
                        onDeleteFarm = { farm -> viewModel.deleteFarmRecord(farm) },
                        onDeleteAllFarms = { viewModel.deleteAllFarms() },
                        onOpenDeleteAccount = { viewModel.openDeleteAccountModal() }
                    )
                }
            }

            // Global Delete Account Modal
            DeleteAccountModal(
                isVisible = isDeleteAccountModalVisible,
                currentLanguage = currentLanguage,
                onConfirmDelete = { viewModel.deleteUserAccount() },
                onDismiss = { viewModel.closeDeleteAccountModal() }
            )
        }
    }
}

