package com.example.data.repository

import com.example.domain.models.GuideArticle
import com.example.domain.models.GuideSection

class GuideRepository {
    private val articles = listOf(
        GuideArticle(
            id = "rice_production",
            title = "Rice Production Guide",
            subtitle = "Land preparation, planting, and crop care.",
            summary = "This practical booklet helps rice farmers make clear field decisions using simple steps. Follow local agricultural guidance and product labels when applying farm inputs.",
            sections = listOf(
                GuideSection(
                    sectionTitle = "1. Land Preparation",
                    content = "Proper field leveling and tilling ensures uniform water distribution and suppresses weed growth effectively.",
                    tips = listOf(
                        "Plow field 14-21 days before planting.",
                        "Harrow twice at 7-day intervals.",
                        "Level field carefully to maintain 2-3 cm shallow water layer."
                    )
                ),
                GuideSection(
                    sectionTitle = "2. Seed Selection & Preparation",
                    content = "Use high-quality certified seeds suited to your climate zone and rainfed or irrigated setup.",
                    tips = listOf(
                        "Soak seeds in clean water for 24 hours.",
                        "Incubate for 24-36 hours until white buds emerge.",
                        "Sow uniformly on seedbed."
                    )
                ),
                GuideSection(
                    sectionTitle = "3. Transplanting & Direct Seeding",
                    content = "Transplant 14-18 day old seedlings at 20x20 cm spacing, or broadcast pre-germinated seeds evenly on saturated soil.",
                    tips = listOf(
                        "Maintain 2 seedlings per hill for irrigated fields.",
                        "Replant missing hills within 7 days."
                    )
                )
            )
        ),
        GuideArticle(
            id = "fertilizer_guide",
            title = "Fertilizer Guide",
            subtitle = "Safe and effective nutrient application.",
            summary = "Understand Nitrogen (N), Phosphorus (P), and Potassium (K) timing to maximize grain yields while minimizing input waste.",
            sections = listOf(
                GuideSection(
                    sectionTitle = "Basal Application",
                    content = "Apply Complete fertilizer (14-14-14) or DAP at 0-14 days after transplanting (DAT) to build healthy root systems.",
                    tips = listOf("Incorporate into soil during final land leveling if possible.")
                ),
                GuideSection(
                    sectionTitle = "Top Dressing (Mid-Vegetative)",
                    content = "Apply Urea or Ammonium Sulfate at active tillering (21-28 DAT) to promote tiller production and stem strength.",
                    tips = listOf("Ensure field is moist before applying broadcast fertilizer.")
                ),
                GuideSection(
                    sectionTitle = "Panicle Initiation Split",
                    content = "Apply MOP (Muriate of Potash) and Urea at panicle initiation (40-45 DAT) to boost grain size and fill.",
                    tips = listOf("Avoid over-applying nitrogen late in season to reduce lodging risk.")
                )
            )
        ),
        GuideArticle(
            id = "irrigation",
            title = "Irrigation",
            subtitle = "Water management for every growth stage.",
            summary = "Smart water management techniques like Alternate Wetting and Drying (AWD) save water without sacrificing yield.",
            sections = listOf(
                GuideSection(
                    sectionTitle = "Alternate Wetting & Drying (AWD)",
                    content = "Install a simple perforated field water tube (PaniPipe) to monitor subterranean water levels.",
                    tips = listOf(
                        "Allow field water depth to drop to 15 cm below soil surface before re-flooding.",
                        "Keep field flooded during flowering/heading stage (5 cm water)."
                    )
                ),
                GuideSection(
                    sectionTitle = "Terminal Drainage",
                    content = "Drain field completely 10-14 days before expected harvest to uniform grain ripening and prepare land for harvester machinery.",
                    tips = listOf("Prevents mud clogging during mechanical harvesting.")
                )
            )
        ),
        GuideArticle(
            id = "harvesting",
            title = "Harvesting",
            subtitle = "Timing, drying, and storage guidance.",
            summary = "Harvest when 80-85% of grains are clear straw-colored to reduce shatter losses and grain cracking.",
            sections = listOf(
                GuideSection(
                    sectionTitle = "Optimal Harvest Timing",
                    content = "Harvest at 20-24% grain moisture content.",
                    tips = listOf("Thresh immediately after manual cutting to avoid fungal growth.")
                ),
                GuideSection(
                    sectionTitle = "Drying & Moisture Control",
                    content = "Dry paddy grain to 14% moisture for safe short-term storage or 12% for long-term seed preservation.",
                    tips = listOf("Use solar mechanical dryer or clean tarpaulin bed.")
                )
            )
        ),
        GuideArticle(
            id = "government_programs",
            title = "Government Programs",
            subtitle = "Support and services for rice farmers.",
            summary = "Information on agricultural credit, seed subsidies, crop insurance (PCIC), and mechanical equipment grants.",
            sections = listOf(
                GuideSection(
                    sectionTitle = "Rice Competitiveness Enhancement Fund (RCEF)",
                    content = "Provides high-yielding inbred seeds, farm mechanization equipment, and agricultural training.",
                    tips = listOf("Register in RSBSA (Registry System for Basic Sectors in Agriculture) at municipal agriculture office.")
                ),
                GuideSection(
                    sectionTitle = "PCIC Crop Insurance",
                    content = "Provides financial safety nets for Typhoon, Flood, Drought, and Pest damages.",
                    tips = listOf("Submit crop insurance application before planting season.")
                )
            )
        )
    )

    fun getArticles(): List<GuideArticle> = articles

    fun getArticleById(id: String): GuideArticle? = articles.find { it.id == id }

    fun searchArticles(query: String): List<GuideArticle> {
        if (query.isBlank()) return articles
        val lower = query.lowercase()
        return articles.filter { 
            it.title.lowercase().contains(lower) || 
            it.subtitle.lowercase().contains(lower) ||
            it.summary.lowercase().contains(lower)
        }
    }
}
