package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import com.example.ui.theme.FarmBrownPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.domain.models.UserAccount
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark

@Composable
fun TopNavBar(
    currentTab: Int,
    isSoilAnalysisOpen: Boolean,
    isGuideDetailOpen: Boolean,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    currentUser: UserAccount? = null,
    onOpenAuthModal: () -> Unit = {},
    onOpenAdminDashboard: () -> Unit = {},
    onLogout: () -> Unit = {},
    isHighContrastMode: Boolean = false,
    onToggleHighContrastMode: () -> Unit = {},
    isOffline: Boolean = false,
    onOfflineClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var accountMenuExpanded by remember { mutableStateOf(false) }

    val screenTitle = when {
        isSoilAnalysisOpen -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Soil Analysis"
            AppLanguage.TAGALOG -> "Pagsusuri sa Lupa"
            AppLanguage.TAGLISH -> "Soil Analysis"
            AppLanguage.ILOCANO -> "Panagrukod ti Daga"
            AppLanguage.CEBUANO -> "Pagsusi sa Yuta"
        }
        isGuideDetailOpen -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Farming Guide"
            AppLanguage.TAGALOG -> "Gabay sa Pagsasaka"
            AppLanguage.TAGLISH -> "Farming Guide"
            AppLanguage.ILOCANO -> "Libro ti Panagmula"
            AppLanguage.CEBUANO -> "Giya sa Pag-uuma"
        }
        else -> when (currentTab) {
            0 -> "PalaySmart"
            1 -> when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farm Measurement"
                AppLanguage.TAGALOG -> "Sukat ng Bukid"
                AppLanguage.TAGLISH -> "Farm Measurement"
                AppLanguage.ILOCANO -> "Rukod ti Talon"
                AppLanguage.CEBUANO -> "Sukat sa Yuta"
            }
            2 -> when (currentLanguage) {
                AppLanguage.ENGLISH -> "Fertilizer Calculator"
                AppLanguage.TAGALOG -> "Kalkulador ng Pataba"
                AppLanguage.TAGLISH -> "Fertilizer Calculator"
                AppLanguage.ILOCANO -> "Kalkulador ti Paitaba"
                AppLanguage.CEBUANO -> "Kalkulador sa Abuno"
            }
            3 -> when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farming Booklet"
                AppLanguage.TAGALOG -> "Gabay at Libro"
                AppLanguage.TAGLISH -> "Farming Booklet"
                AppLanguage.ILOCANO -> "Libro ti Panagtalon"
                AppLanguage.CEBUANO -> "Giya ug Libro"
            }
            else -> when (currentLanguage) {
                AppLanguage.ENGLISH -> "Farm Records"
                AppLanguage.TAGALOG -> "Tala ng Bukid"
                AppLanguage.TAGLISH -> "Farm History"
                AppLanguage.ILOCANO -> "Nakalabas a Rekord"
                AppLanguage.CEBUANO -> "Talaan sa Yuta"
            }
        }
    }

    Surface(
        color = FarmGreenHeader,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Grass,
                contentDescription = null,
                tint = FarmGreenLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = screenTitle,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Offline Mode Indicator Pill
            if (isOffline) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFF9800))
                        .clickable { onOfflineClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("top_bar_offline_badge")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Offline Mode Active",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Quick High Contrast / Sunlight Mode Toggle Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isHighContrastMode) Color(0xFFFFD54F) // Vibrant Amber/Yellow badge when active in sunlight
                        else Color.White.copy(alpha = 0.18f)
                    )
                    .clickable { onToggleHighContrastMode() }
                    .padding(horizontal = 9.dp, vertical = 6.dp)
                    .testTag("toggle_high_contrast_mode")
            ) {
                Icon(
                    imageVector = if (isHighContrastMode) Icons.Default.WbSunny else Icons.Default.BrightnessMedium,
                    contentDescription = "Toggle Sunlight High Contrast Mode",
                    tint = if (isHighContrastMode) Color(0xFF2A1E17) else Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isHighContrastMode) "Sun ☀️" else "Sunlight",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighContrastMode) Color(0xFF2A1E17) else Color.White
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Persistent Language Switcher Icon Button & Dropdown
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .clickable { expanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("top_bar_language_switcher")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language Switcher",
                        tint = FarmBrownPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = currentLanguage.displayName,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmBrownPrimary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = FarmBrownPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .padding(vertical = 4.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = currentLanguage == lang
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) FarmGreenPrimary else FarmTextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = FarmGreenPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onLanguageSelected(lang)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // User / Admin Account Button with Quick Actions & Logout
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (currentUser?.isAdmin == true) Color(0xFF1565C0)
                            else Color.White.copy(alpha = 0.22f)
                        )
                        .clickable { accountMenuExpanded = true }
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                        .testTag("top_bar_account_button")
                ) {
                    Icon(
                        imageVector = if (currentUser?.isAdmin == true) Icons.Default.Shield else Icons.Default.Person,
                        contentDescription = "User & Admin Accounts",
                        tint = if (currentUser?.isAdmin == true) Color(0xFFFFD54F) else Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentUser?.isAdmin == true) "Admin" else currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Account",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = accountMenuExpanded,
                    onDismissRequest = { accountMenuExpanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .padding(vertical = 4.dp)
                ) {
                    // Header item showing user identity
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = currentUser?.fullName ?: "Active Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FarmTextDark
                                )
                                Text(
                                    text = if (currentUser?.isAdmin == true) "DA / PhilRice Authorized" else "RSBSA: ${currentUser?.rsbsaNumber ?: "Registered"}",
                                    fontSize = 11.sp,
                                    color = FarmTextSecondary
                                )
                            }
                        },
                        onClick = {
                            accountMenuExpanded = false
                            onOpenAuthModal()
                        }
                    )

                    HorizontalDivider()

                    if (currentUser?.isAdmin == true) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color(0xFF1565C0),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Open Admin Dashboard",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FarmTextDark
                                    )
                                }
                            },
                            onClick = {
                                accountMenuExpanded = false
                                onOpenAdminDashboard()
                            }
                        )
                        HorizontalDivider()
                    }

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log Out (Bumalik sa Landing)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        },
                        onClick = {
                            accountMenuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}
