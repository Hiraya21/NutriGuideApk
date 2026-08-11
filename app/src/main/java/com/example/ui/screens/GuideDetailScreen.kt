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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppLanguage
import com.example.domain.models.GuideArticle
import com.example.domain.models.getLocalized
import com.example.ui.components.LanguageBar
import com.example.ui.theme.FarmBorder
import com.example.ui.theme.FarmGreenHeader
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTextDark
import com.example.ui.theme.FarmTextSecondary

@Composable
fun GuideDetailScreen(
    article: GuideArticle,
    onBack: () -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val localizedArticle = article.getLocalized(currentLanguage)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FarmTextDark)
            }
            val titleText = when (currentLanguage) {
                AppLanguage.ENGLISH -> "Guide"
                AppLanguage.TAGALOG -> "Gabay"
                AppLanguage.TAGLISH -> "Guide"
                AppLanguage.ILOCANO -> "Gabay"
                AppLanguage.CEBUANO -> "Giya"
            }
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FarmTextDark,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Main Guide Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, FarmBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Large Book Icon
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = localizedArticle.title,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = localizedArticle.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenHeader
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Summary
                Text(
                    text = localizedArticle.summary,
                    fontSize = 14.sp,
                    color = FarmTextDark,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Back Link
                val backLinkText = when (currentLanguage) {
                    AppLanguage.ENGLISH -> "← Back to all guides"
                    AppLanguage.TAGALOG -> "← Bumalik sa lahat ng gabay"
                    AppLanguage.TAGLISH -> "← Back to all guides"
                    AppLanguage.ILOCANO -> "← Agsubli iti amin a gabay"
                    AppLanguage.CEBUANO -> "← Balik sa tanang giya"
                }
                Text(
                    text = backLinkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onBack)
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = FarmBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Sections
                localizedArticle.sections.forEach { section ->
                    Text(
                        text = section.sectionTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenHeader
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = section.content,
                        fontSize = 14.sp,
                        color = FarmTextDark,
                        lineHeight = 20.sp
                    )

                    if (section.tips.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        section.tips.forEach { tip ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tip,
                                    fontSize = 13.sp,
                                    color = FarmTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
