package com.example.domain.models

data class GuideArticle(
    val id: String,
    val title: String,
    val subtitle: String,
    val summary: String,
    val sections: List<GuideSection>
)

data class GuideSection(
    val sectionTitle: String,
    val content: String,
    val tips: List<String> = emptyList()
)
