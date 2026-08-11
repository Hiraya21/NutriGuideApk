package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextSecondary

data class NavTabItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val navTabItems = listOf(
    NavTabItem(Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    NavTabItem(Icons.Filled.Map, Icons.Outlined.Map, "tab_measurement"),
    NavTabItem(Icons.Filled.Science, Icons.Outlined.Science, "tab_fertilizer"),
    NavTabItem(Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_booklet"),
    NavTabItem(Icons.Filled.Person, Icons.Outlined.Person, "tab_history")
)

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Divider(color = FarmBorder, thickness = 1.dp)
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            navTabItems.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val tabTitle = when (index) {
                    0 -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Home"
                        AppLanguage.TAGALOG -> "Tahanan"
                        AppLanguage.TAGLISH -> "Home"
                        AppLanguage.ILOCANO -> "Purok"
                        AppLanguage.CEBUANO -> "Balay"
                    }
                    1 -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Measure"
                        AppLanguage.TAGALOG -> "Sukat"
                        AppLanguage.TAGLISH -> "Measure"
                        AppLanguage.ILOCANO -> "Rukod"
                        AppLanguage.CEBUANO -> "Sukat"
                    }
                    2 -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Fertilizer"
                        AppLanguage.TAGALOG -> "Pataba"
                        AppLanguage.TAGLISH -> "Fertilizer"
                        AppLanguage.ILOCANO -> "Paitaba"
                        AppLanguage.CEBUANO -> "Abuno"
                    }
                    3 -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Booklet"
                        AppLanguage.TAGALOG -> "Gabay"
                        AppLanguage.TAGLISH -> "Booklet"
                        AppLanguage.ILOCANO -> "Libro"
                        AppLanguage.CEBUANO -> "Giya"
                    }
                    else -> when (currentLanguage) {
                        AppLanguage.ENGLISH -> "History"
                        AppLanguage.TAGALOG -> "Nakaraan"
                        AppLanguage.TAGLISH -> "History"
                        AppLanguage.ILOCANO -> "Nakalabas"
                        AppLanguage.CEBUANO -> "Talaan"
                    }
                }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.testTag(item.testTag),
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = tabTitle,
                            tint = if (isSelected) FarmGreenHeader else FarmTextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = tabTitle,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            color = if (isSelected) FarmGreenHeader else FarmTextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = FarmGreenContainer
                    )
                )
            }
        }
    }
}
