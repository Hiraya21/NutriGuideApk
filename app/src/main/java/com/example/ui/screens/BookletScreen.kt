package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.domain.models.GuideArticle
import com.example.domain.models.getLocalized
import com.example.ui.components.LanguageBar
import com.example.ui.components.LanguageDropdown
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenLight
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

@Composable
fun BookletScreen(
    searchQuery: String,
    articles: List<GuideArticle>,
    onSearchChange: (String) -> Unit,
    onSelectGuide: (GuideArticle) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        val headerTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Farming Booklet & Guides"
            AppLanguage.TAGALOG -> "Gabay at Libro sa Pagsasaka"
            AppLanguage.TAGLISH -> "Farming Booklet & Guides"
            AppLanguage.ILOCANO -> "Libro ti Panagmula ken Gabay"
            AppLanguage.CEBUANO -> "Libro ug Giya sa Pag-uuma"
        }
        Text(
            text = headerTitle,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = FarmTextDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Search Bar
        val searchPlaceholder = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Search farming guide..."
            AppLanguage.TAGALOG -> "Maghanap ng gabay sa pagsasaka..."
            AppLanguage.TAGLISH -> "Search ng farming guide..."
            AppLanguage.ILOCANO -> "Biroken ti libro ti panagmula..."
            AppLanguage.CEBUANO -> "Pangita og giya sa pag-uuma..."
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(searchPlaceholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_booklet"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                unfocusedBorderColor = FarmBorder,
                focusedBorderColor = FarmGreenPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Guides Cards List
        articles.forEach { article ->
            val localizedArticle = article.getLocalized(currentLanguage)
            GuideCategoryCard(
                article = localizedArticle,
                onClick = { onSelectGuide(localizedArticle) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GuideCategoryCard(
    article: GuideArticle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FarmBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FarmGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = article.title,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = article.subtitle,
                    fontSize = 12.sp,
                    color = FarmTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open",
                tint = Color(0xFFCFD8DC),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
