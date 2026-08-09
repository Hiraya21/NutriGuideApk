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
        // Language Selector
        LanguageBar(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Header
        val headerTitle = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Farming Booklet & Guides"
            AppLanguage.TAGALOG -> "Gabay at Libro sa Pagsasaka"
            AppLanguage.TAGLISH -> "Farming Booklet & Guides"
            AppLanguage.ILOCANO -> "Libro ti Panagmula ken Gabay"
            AppLanguage.CEBUANO -> "Libro sa Pag-uuma ug Giyahan"
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
            AppLanguage.CEBUANO -> "Pangitaa ang giya sa pag-uuma..."
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
            GuideCategoryCard(
                article = article,
                currentLanguage = currentLanguage,
                onClick = { onSelectGuide(article) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GuideCategoryCard(
    article: GuideArticle,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onClick: () -> Unit
) {
    val displayTitle = when (article.id) {
        "rice_production" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Rice Production Guide"
            AppLanguage.TAGALOG -> "Gabay sa Pagtatanim ng Palay"
            AppLanguage.TAGLISH -> "Rice Production Guide"
            AppLanguage.ILOCANO -> "Gabay ti Panagmula ti Pagay"
            AppLanguage.CEBUANO -> "Giya sa Pagtanom og Humay"
        }
        "soil_nutrient" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Soil & Nutrient Management"
            AppLanguage.TAGALOG -> "Pangangalaga sa Lupa at Pataba"
            AppLanguage.TAGLISH -> "Soil & Nutrient Management"
            AppLanguage.ILOCANO -> "Panangipagpateg ti Daga ken Pataba"
            AppLanguage.CEBUANO -> "Pag-atiman sa Yuta ug Abono"
        }
        "pest_disease" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Pest & Disease Control"
            AppLanguage.TAGALOG -> "Pagsugpo sa Peste at Sakit"
            AppLanguage.TAGLISH -> "Pest & Disease Management"
            AppLanguage.ILOCANO -> "Panglaban ti Peste ken Sakit"
            AppLanguage.CEBUANO -> "Pagluwas sa Peste ug Sakit"
        }
        "water_irrigation" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Water & Irrigation Management"
            AppLanguage.TAGALOG -> "Pangangalaga sa Tubig at Patubig"
            AppLanguage.TAGLISH -> "Water & Irrigation Management"
            AppLanguage.ILOCANO -> "Panangipagpateg ti Danum ken Patubig"
            AppLanguage.CEBUANO -> "Pag-atiman sa Tubig ug Patubig"
        }
        else -> article.title
    }

    val displaySubtitle = when (article.id) {
        "rice_production" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Step-by-step land prep, seed rate & fertilizer schedule."
            AppLanguage.TAGALOG -> "Lahat ng hakbang sa paghahanda ng lupa, binhi, at pataba."
            AppLanguage.TAGLISH -> "Step-by-step land prep, binhi, at fertilizer schedule."
            AppLanguage.ILOCANO -> "Paset-paset a panaghanda ti daga, bukel ken pataba."
            AppLanguage.CEBUANO -> "Lihok-sa-lihok nga pag-andam sa yuta, binhi ug abono."
        }
        "soil_nutrient" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "NPK requirements, organic matter, and pH balance."
            AppLanguage.TAGALOG -> "Pangangailangan sa NPK, patabang natural, at pH ng lupa."
            AppLanguage.TAGLISH -> "NPK requirements, organic matter, at pH level."
            AppLanguage.ILOCANO -> "Amas ti NPK, organiko a banag, ken pH ng daga."
            AppLanguage.CEBUANO -> "NPK nga kinahanglan, natural nga pataba ug pH balance."
        }
        "pest_disease" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Identify golden apple snail, stem borer, and leaf blast early."
            AppLanguage.TAGALOG -> "Pagkilala sa kuhol, uod sa puno, at lapnos sa dahon."
            AppLanguage.TAGLISH -> "Kilalanin ang kuhol, stem borer, at leaf blast."
            AppLanguage.ILOCANO -> "Panangbigbig ti bisukol, igges, ken sakit ti bulong."
            AppLanguage.CEBUANO -> "Pag-ila sa kuhol, ulod sa punoan, ug sakit sa dahon."
        }
        "water_irrigation" -> when (currentLanguage) {
            AppLanguage.ENGLISH -> "Alternate wetting and drying (AWD) techniques for water saving."
            AppLanguage.TAGALOG -> "Paraan ng AWD para sa pagtitipid ng tubig sa palayan."
            AppLanguage.TAGLISH -> "AWD techniques para sa pagtitipid ng tubig."
            AppLanguage.ILOCANO -> "Tanda ti AWD tapno makatiped ti danum."
            AppLanguage.CEBUANO -> "AWD nga paagi para sa pagtipig og tubig sa humayan."
        }
        else -> article.subtitle
    }
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
                    text = displayTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displaySubtitle,
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
