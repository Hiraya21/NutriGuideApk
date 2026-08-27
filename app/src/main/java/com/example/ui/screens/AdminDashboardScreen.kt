package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AdminAuditLog
import com.example.domain.models.AppLanguage
import com.example.domain.models.FarmerRegistryItem
import com.example.domain.models.UserAccount
import com.example.domain.models.UserRole
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun AdminDashboardScreen(
    currentUser: UserAccount,
    farmerRegistry: List<FarmerRegistryItem>,
    auditLogs: List<AdminAuditLog>,
    currentLanguage: AppLanguage,
    onBackToFarmerView: () -> Unit,
    onLogout: () -> Unit,
    onBroadcastAlert: (title: String, message: String, priority: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf("All") }

    // Broadcast form states
    var alertTitle by remember { mutableStateOf("Monsoon Heavy Rain: Postpone Nitrogen Application") }
    var alertMessage by remember { mutableStateOf("Advisory from DA-PhilRice Muñoz: Heavy rainfall expected in Central Luzon for the next 48 hours. Farmers are advised to delay urea and ammonium sulfate topdress to avoid fertilizer runoff.") }
    var alertPriority by remember { mutableStateOf("High Advisory") }
    var broadcastSuccessMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBackToFarmerView()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .testTag("screen_admin_dashboard")
    ) {
        // Modern Top App Bar Header
        Surface(
            color = Color(0xFF0D47A1),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)
            ) {
                // Top Navigation and Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackToFarmerView,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_admin_back_to_farmer")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Farmer View",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DA-PhilRice Console",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "Agronomy Command Center",
                            fontSize = 11.sp,
                            color = Color(0xFFBBDEFB),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Action Buttons (Farmer View Switch & Logout)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackToFarmerView,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFFA5D6A7)))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_switch_to_farmer_app")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color(0xFFA5D6A7)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Farmer View",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_admin_logout")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Log Out",
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Logout", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Personnel Identity Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D47A1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.fullName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentUser.displayRoleLabel} • Badge #${currentUser.badgeOrPersonnelId}",
                                fontSize = 10.sp,
                                color = Color(0xFFE3F2FD),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E7D32))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "VERIFIED DA",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modern Scrollable Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = Color(0xFFE0E0E0)) },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF0D47A1),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "📊 Overview & KPIs",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_overview")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "👥 Farmer Roster (${farmerRegistry.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_farmers")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "🧪 Fertilizer Demand",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 2) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_fertilizer")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Text(
                        "📢 Advisory Broadcast",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 3) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_broadcast")
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = {
                    Text(
                        "🛡️ Audit Logs",
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 4) Color(0xFF0D47A1) else Color(0xFF616161)
                    )
                },
                modifier = Modifier.testTag("admin_tab_audit")
            )
        }

        // Tab Content List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item { AdminOverviewTab(farmerRegistry = farmerRegistry) }
                }
                1 -> {
                    item {
                        FarmerRosterHeader(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            selectedFilter = selectedFilterStatus,
                            onFilterChange = { selectedFilterStatus = it }
                        )
                    }
                    val filteredFarmers = farmerRegistry.filter {
                        (searchQuery.isBlank() || it.fullName.contains(searchQuery, ignoreCase = true) || it.rsbsaId.contains(searchQuery, ignoreCase = true) || it.barangay.contains(searchQuery, ignoreCase = true)) &&
                        (selectedFilterStatus == "All" || it.subsidyStatus.contains(selectedFilterStatus, ignoreCase = true))
                    }
                    if (filteredFarmers.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF9E9E9E),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Walang nahanap na magsasaka sa query \"$searchQuery\"",
                                        fontSize = 13.sp,
                                        color = Color(0xFF616161),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredFarmers) { farmer ->
                            FarmerRegistryCard(farmer = farmer)
                        }
                    }
                }
                2 -> {
                    item { RegionalFertilizerDemandTab(farmerRegistry = farmerRegistry) }
                }
                3 -> {
                    item {
                        AdvisoryDispatcherTab(
                            title = alertTitle,
                            message = alertMessage,
                            priority = alertPriority,
                            successMsg = broadcastSuccessMessage,
                            onTitleChange = { alertTitle = it },
                            onMessageChange = { alertMessage = it },
                            onPriorityChange = { alertPriority = it },
                            onSend = {
                                onBroadcastAlert(alertTitle, alertMessage, alertPriority)
                                broadcastSuccessMessage = "Advisory broadcast successfully dispatched to 142 registered farmers in Muñoz & Nueva Ecija!"
                            }
                        )
                    }
                }
                4 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF37474F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Immutable Security & Personnel Audit Logs",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF263238)
                                    )
                                }
                                Text(
                                    text = "Every administrative access and policy change is cryptographically logged for DA Compliance.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    items(auditLogs) { log ->
                        AuditLogCard(log = log)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminOverviewTab(farmerRegistry: List<FarmerRegistryItem>) {
    val totalHectares = farmerRegistry.sumOf { it.farmSizeHa }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Municipality Agronomic Snapshot",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Muñoz, Nueva Ecija",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            }
        }

        // 4 KPI Metric Cards in a balanced 2x2 grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricKpiCard(
                title = "Registered Farmers",
                value = "${farmerRegistry.size + 138}",
                subtitle = "+12 enrolled this month",
                icon = Icons.Default.Person,
                cardColor = Color(0xFFE8F5E9),
                accentColor = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )

            MetricKpiCard(
                title = "Mapped Land Area",
                value = String.format("%.1f Ha", totalHectares + 472.4),
                subtitle = "GPS Polygon Verified",
                icon = Icons.Default.Map,
                cardColor = Color(0xFFE3F2FD),
                accentColor = Color(0xFF1565C0),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricKpiCard(
                title = "RCEF Vouchers",
                value = "94.2%",
                subtitle = "134 / 142 Distributed",
                icon = Icons.Default.FactCheck,
                cardColor = Color(0xFFFFF3E0),
                accentColor = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )

            MetricKpiCard(
                title = "Dominant Soil Class",
                value = "Maligaya Clay",
                subtitle = "Avg pH 6.1 (Optimal)",
                icon = Icons.Default.Science,
                cardColor = Color(0xFFF3E5F5),
                accentColor = Color(0xFF7B1FA2),
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Alert Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF176))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF9C4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Active Weather & Nutrient Advisory",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = "Monsoon rain advisory active. Farmers advised to hold urea topdressing until sunny breaks to prevent nutrient leaching.",
                        fontSize = 11.sp,
                        color = Color(0xFF424242),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Top Varieties Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🌾 Certified Rice Varieties in Muñoz Zone",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Wet Season 2026",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                VarietyShareRow("NSIC Rc 222 (Tubigan 21)", "High Yielding Inbred", "48% adoption", 0.48f, Color(0xFF2E7D32))
                VarietyShareRow("NSIC Rc 160 (Tubigan 14)", "Premium Soft Eating Quality", "26% adoption", 0.26f, Color(0xFF1976D2))
                VarietyShareRow("Mestiso 20 / Mestiso 29", "Commercial Hybrid Seed", "16% adoption", 0.16f, Color(0xFFF57C00))
                VarietyShareRow("Green Super Rice (NSIC Rc 480)", "Drought/Submergence Tolerant", "10% adoption", 0.10f, Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
private fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    cardColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF616161),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun VarietyShareRow(name: String, desc: String, share: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(desc, fontSize = 10.sp, color = Color(0xFF757575))
            }
            Text(share, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun FarmerRosterHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Mag-search ng Magsasaka, RSBSA ID, o Barangay...", fontSize = 12.sp, color = Color(0xFF757575)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1565C0)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = Color(0xFF757575))
                    }
                }
            },
            singleLine = true,
            textStyle = TextStyle(color = Color(0xFF212121), fontSize = 13.sp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_admin_farmer_search"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF212121),
                unfocusedTextColor = Color(0xFF212121),
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color(0xFFB0BEC5),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                "All" to "Lahat",
                "Approved" to "RSBSA Approved",
                "Voucher" to "Voucher Ready",
                "Review" to "For Verification"
            )
            items(filterOptions) { (key, label) ->
                val isSelected = selectedFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF0D47A1) else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF0D47A1) else Color(0xFFCFD8DC),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onFilterChange(key) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF37474F)
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmerRegistryCard(farmer: FarmerRegistryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FarmGreenPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(farmer.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                        Text("RSBSA: ${farmer.rsbsaId}", fontSize = 10.sp, color = Color(0xFF757575))
                    }
                }

                val badgeBg = when {
                    farmer.subsidyStatus.contains("Approved", ignoreCase = true) -> Color(0xFFE8F5E9)
                    farmer.subsidyStatus.contains("Voucher", ignoreCase = true) -> Color(0xFFE3F2FD)
                    else -> Color(0xFFFFF3E0)
                }
                val badgeText = when {
                    farmer.subsidyStatus.contains("Approved", ignoreCase = true) -> Color(0xFF2E7D32)
                    farmer.subsidyStatus.contains("Voucher", ignoreCase = true) -> Color(0xFF1565C0)
                    else -> Color(0xFFE65100)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = farmer.subsidyStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lokasyon", fontSize = 9.sp, color = Color(0xFF757575))
                    Text(
                        text = "${farmer.barangay}, ${farmer.municipality}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F),
                        maxLines = 1
                    )
                }

                Column(
                    modifier = Modifier.weight(0.7f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sukat ng Lupa", fontSize = 9.sp, color = Color(0xFF757575))
                    Text(
                        text = "${farmer.farmSizeHa} Ha",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Binhi / Barayti", fontSize = 9.sp, color = Color(0xFF757575))
                    Text(
                        text = farmer.cropVariety,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionalFertilizerDemandTab(farmerRegistry: List<FarmerRegistryItem>) {
    val totalHa = farmerRegistry.sumOf { it.farmSizeHa } + 472.4

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Aggregated Municipal Fertilizer Demands (Wet Season)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )

        // Summary Metric Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Verified Area", fontSize = 11.sp, color = Color(0xFFBBDEFB))
                    Text(String.format("%.1f Hectares", totalHa), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D47A1))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("4,074 Total Bags Needed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                }
            }
        }

        // Fertilizer Demands Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Estimated Bag Requirements (50kg Standard Bags)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(10.dp))

                FertilizerDemandRow("Urea (46-0-0)", "Nitrogen Booster (Tillering & Panicle)", "1,164 Bags", Color(0xFF1976D2))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Complete (14-14-14)", "Basal Application Foundation", "1,940 Bags", Color(0xFF2E7D32))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Muriate of Potash (0-0-60)", "Grain Filling Potash Booster", "582 Bags", Color(0xFFE65100))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                FertilizerDemandRow("Ammonium Sulfate (21-0-0 + 24S)", "Sulfur replenishment for Maligaya Clay", "388 Bags", Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
private fun FertilizerDemandRow(fertilizer: String, note: String, bags: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(fertilizer, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            Text(note, fontSize = 10.sp, color = Color(0xFF757575))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(bags, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun AdvisoryDispatcherTab(
    title: String,
    message: String,
    priority: String,
    successMsg: String?,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Broadcast Farmer Advisory",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "Dispatches push SMS & agronomy notices directly to farmers",
                        fontSize = 10.sp,
                        color = Color(0xFF546E7A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Priority Selector Pills
            Text("Advisory Priority Level", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "High Advisory" to Color(0xFFD32F2F),
                    "Weather Alert" to Color(0xFFE65100),
                    "Fertilizer Tip" to Color(0xFF2E7D32)
                ).forEach { (pName, pColor) ->
                    val isSelected = priority == pName
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) pColor.copy(alpha = 0.15f) else Color(0xFFF5F5F5))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) pColor else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onPriorityChange(pName) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pName,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) pColor else Color(0xFF616161)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Advisory Headline", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Medium) },
                placeholder = { Text("hal. Flash Flood Warning / Basal Fertilizer Schedule", fontSize = 12.sp, color = Color(0xFF757575)) },
                singleLine = true,
                textStyle = TextStyle(color = Color(0xFF0D47A1), fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_broadcast_title"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0D47A1),
                    unfocusedTextColor = Color(0xFF1E211D),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF90CAF9),
                    cursorColor = Color(0xFF0D47A1),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF37474F)
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Advisory Message / Action Instructions", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Medium) },
                placeholder = { Text("Isulat ang advisory instructions para sa mga magsasaka...", fontSize = 12.sp, color = Color(0xFF757575)) },
                minLines = 3,
                textStyle = TextStyle(color = Color(0xFF1E211D), fontSize = 13.sp, fontWeight = FontWeight.Normal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_broadcast_message"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E211D),
                    unfocusedTextColor = Color(0xFF1E211D),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF90CAF9),
                    cursorColor = Color(0xFF0D47A1),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF37474F)
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (successMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "✅ $successMsg",
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onSend,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_dispatch_broadcast")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dispatch Broadcast to All Farmers", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AuditLogCard(log: AdminAuditLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text("${log.personnelName} (${log.personnelId})", fontSize = 11.sp, color = Color(0xFF1565C0))
                Text(log.timestampFormatted, fontSize = 10.sp, color = Color(0xFF757575))
            }
        }
    }
}
