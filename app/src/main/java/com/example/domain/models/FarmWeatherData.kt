package com.example.domain.models

enum class WeatherRiskLevel {
    HIGH_DANGER,   // Heavy Rain (>20mm) - Severe Leaching / Runoff Risk
    WARNING,       // Extreme Heat (>35°C) or High Winds - Volatilization / Drift Risk
    CAUTION,       // Moderate Rain (5-20mm) - Reduced efficiency
    OPTIMAL        // Mild / Clear (22-32°C, <5mm rain) - Ideal Application Window
}

enum class WeatherScenario {
    LIVE_GPS,      // Real-time GPS or Open-Meteo REST API
    HEAVY_RAIN,    // 38mm Heavy Rain Expected (Typhoon / Monsoon)
    EXTREME_HEAT,  // 37.5°C Heatwave Alert
    HIGH_WINDS,    // 28 km/h Strong Winds
    OPTIMAL_CLEAR  // 27°C Sunny with Light Breeze
}

data class FertilizerAdvisory(
    val riskLevel: WeatherRiskLevel,
    val title: String,
    val summary: String,
    val ureaAdvice: String,
    val npkAdvice: String,
    val actionStep: String,
    val bestApplicationWindow: String
)

data class DailyForecastDay(
    val dateLabel: String,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationMm: Double,
    val rainProbPercent: Int,
    val weatherCode: Int,
    val condition: String
)

data class FarmWeatherData(
    val locationName: String = "Nueva Ecija (Rice Granary)",
    val lat: Double = 15.4827,
    val lng: Double = 120.9723,
    val currentTempC: Double = 31.5,
    val maxTempC: Double = 33.0,
    val minTempC: Double = 24.5,
    val relativeHumidity: Int = 78,
    val precipitationSumMm: Double = 2.5,
    val precipitationProbPercent: Int = 20,
    val windSpeedKmh: Double = 12.0,
    val weatherCondition: String = "Partly Cloudy ⛅",
    val aqiIndex: Int = 41,
    val dailyForecast: List<DailyForecastDay> = emptyList(),
    val isLiveApi: Boolean = false,
    val isCachedData: Boolean = true,
    val lastSyncTime: String = "Offline Cache",
    val advisory: FertilizerAdvisory = calculateFertilizerAdvisory(
        precipitationSumMm = 2.5,
        maxTempC = 33.0,
        windSpeedKmh = 12.0
    )
)

fun calculateFertilizerAdvisory(
    precipitationSumMm: Double,
    maxTempC: Double,
    windSpeedKmh: Double,
    language: AppLanguage = AppLanguage.ENGLISH
): FertilizerAdvisory {
    return when {
        precipitationSumMm >= 20.0 -> {
            when (language) {
                AppLanguage.TAGALOG -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.HIGH_DANGER,
                    title = "⚠️ BABALA SA MALAKAS NA ULAN: IPAGPALIBAN ANG PAG-AABONO",
                    summary = "Inaasahan ang malakas na ulan (${String.format("%.1f", precipitationSumMm)} mm) sa susunod na 24-48 oras. Ang paglalagay ng abono ngayon ay magdudulot ng pagkakahugas at pagkaanod ng sustansya.",
                    ureaAdvice = "🔴 HUWAG maglagay ng Urea. Malaking panganib na maanod ang nitroheno sa mga kanal.",
                    npkAdvice = "🔴 Ipagpaliban ang Complete 14-14-14 / DAP. Maghintay hanggang sa humupa ang tubig.",
                    actionStep = "Ipagpaliban ng 48-72 oras ang pag-aabono. Suriin ang mga pilapil at panatilihin ang kontrol sa tubig.",
                    bestApplicationWindow = "Pagkatapos ng ulan (2 hanggang 3 araw matapos humupa ang baha)"
                )
                AppLanguage.TAGLISH -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.HIGH_DANGER,
                    title = "⚠️ HEAVY RAINFALL WARNING: DELAY FERTILIZER APPLICATION",
                    summary = "May heavy rainfall (${String.format("%.1f", precipitationSumMm)} mm) forecast. Magkakaroon ng nutrient leaching at runoff kapag nag-abono ngayon.",
                    ureaAdvice = "🔴 Huwag munang mag-apply ng Urea para hindi masayang ang nitrogen.",
                    npkAdvice = "🔴 Postpone Complete 14-14-14 / DAP basal application hanggang mag-stabilize ang tubig.",
                    actionStep = "Delay application by 48-72 hours. Inspect field drainage bunds and retain paddy water.",
                    bestApplicationWindow = "Post-rain window (2 to 3 days after drainage)"
                )
                AppLanguage.ILOCANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.HIGH_DANGER,
                    title = "⚠️ PAKDAAR TI NAPIGSA A TODO: ITONDONG TI PANAG-IPAITABA",
                    summary = "Manamnama ti napigsa a todo (${String.format("%.1f", precipitationSumMm)} mm) iti sumungad a 24-48 nga oras. Mayanud laeng ti abono no agipaitabaka ita.",
                    ureaAdvice = "🔴 SAAN nga agikabil ti Urea. Dakkel ti posibilidad a mayanud ti nitroheno.",
                    npkAdvice = "🔴 Isardeng pay ti panangikabil ti Complete 14-14-14 wenno DAP.",
                    actionStep = "Iyalat ti 48-72 nga oras. Salimetmetan dagiti tambak ti talon.",
                    bestApplicationWindow = "Kalpasan ti tudo (2 agingga 3 nga aldaw)"
                )
                AppLanguage.CEBUANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.HIGH_DANGER,
                    title = "⚠️ PAHINUMDOM SA KUSOG NGA ULAN: ILANGAN ANG PAG-ABUNO",
                    summary = "Gilauman ang kusog nga ulan (${String.format("%.1f", precipitationSumMm)} mm) sa sunod nga 24-48 ka oras. Maanod ug mausik lamang ang abuno.",
                    ureaAdvice = "🔴 AYAW una pagbutang og Urea. Peligro nga maanod ang nitroheno.",
                    npkAdvice = "🔴 Ipalabay una ang pagbutang og Complete 14-14-14 o DAP.",
                    actionStep = "Ilangan og 48-72 ka oras. Susiha ang mga pilapil aron dili maanod ang tubig.",
                    bestApplicationWindow = "Human sa ulan (2 hangtod 3 ka adlaw)"
                )
                else -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.HIGH_DANGER,
                    title = "⚠️ HEAVY RAINFALL WARNING: DELAY FERTILIZER APPLICATION",
                    summary = "Heavy rainfall (${String.format("%.1f", precipitationSumMm)} mm) forecast in the next 24-48 hours. Applying fertilizer now will cause severe nutrient leaching and runoff.",
                    ureaAdvice = "🔴 Do NOT apply Urea. High risk of nitrogen washing away into drainage canals.",
                    npkAdvice = "🔴 Postpone Complete 14-14-14 / DAP basal application. Wait until field water stabilizes.",
                    actionStep = "Delay application by 48-72 hours. Inspect field drainage bunds and retain paddy water.",
                    bestApplicationWindow = "Post-rain window (In 2 to 3 days after fields drain)"
                )
            }
        }
        maxTempC >= 35.0 -> {
            when (language) {
                AppLanguage.TAGALOG -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "🌡️ BABALA SA MATINDING INIT: PANGANIB SA PAGKAWALA NG NITROHENO",
                    summary = "Mataas na temperatura (${String.format("%.1f", maxTempC)}°C) ang naitala. Mabilis sumingaw ang Urea bilang ammonia gas sa matinding sikat ng araw.",
                    ureaAdvice = "⚠️ Iwasang magsabog ng Urea sa tanghaling tapat (10 AM - 3 PM).",
                    npkAdvice = "🟡 Ihalo ang NPK sa lupa o maglagay kapag may mababaw na patubig.",
                    actionStep = "Magsabog lamang tuwing maagang umaga (6-8 AM) o dapit-hapon (4-6 PM) upang maiwasan ang pagkapaso ng tanom.",
                    bestApplicationWindow = "Maagang Umaga (6:00 AM - 8:30 AM) o Dapit-hapon"
                )
                AppLanguage.TAGLISH -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "🌡️ EXTREME HEAT ALERT: HIGH VOLATILIZATION RISK",
                    summary = "High temperature (${String.format("%.1f", maxTempC)}°C) detected. Mabilis sumingaw ang Urea kapag mainit masyado.",
                    ureaAdvice = "⚠️ Avoid topdressing Urea during peak sunshine hours (10 AM - 3 PM).",
                    npkAdvice = "🟡 Incorporate NPK fertilizers into soil or apply with light standing irrigation water.",
                    actionStep = "Apply fertilizer strictly early morning (6-8 AM) or late afternoon (4-6 PM).",
                    bestApplicationWindow = "Early Morning (6:00 AM - 8:30 AM) or Late Afternoon"
                )
                AppLanguage.ILOCANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "🌡️ PAKDAAR TI NAPIGSA NGA INIT: KASASO TI UREA",
                    summary = "Nangato ti pudot (${String.format("%.1f", maxTempC)}°C). Nalaka a sumingaw ti Urea no napudot unay ti init.",
                    ureaAdvice = "⚠️ Liklikan ti agibelleng ti Urea iti tengnga ti aldaw (10 AM - 3 PM).",
                    npkAdvice = "🟡 Ipauneg ti NPK iti daga wenno agikabil no adda bassit a danum.",
                    actionStep = "Agikabil laeng iti nasapa a bigat (6-8 AM) wenno lumnek ti init (4-6 PM).",
                    bestApplicationWindow = "Nasapa a Bigat (6:00 AM - 8:30 AM) wenno Rabii"
                )
                AppLanguage.CEBUANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "🌡️ PAHINUMDOM SA GRABE NGA INIT: SINGAW SA UREA",
                    summary = "Taas kaayo ang temperatura (${String.format("%.1f", maxTempC)}°C). Dali mo-alisngaw ang Urea sa grabeng kainit.",
                    ureaAdvice = "⚠️ Likayi ang pag-abuno og Urea sa udto (10 AM - 3 PM).",
                    npkAdvice = "🟡 Isagol ang NPK sa yuta o ibutang kon dunay gamay nga tubig.",
                    actionStep = "Mag-abuno lamang sayo sa buntag (6-8 AM) o hapon na (4-6 PM).",
                    bestApplicationWindow = "Sayo sa Buntag (6:00 AM - 8:30 AM) o Hapon"
                )
                else -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "🌡️ EXTREME HEAT ALERT: HIGH VOLATILIZATION RISK",
                    summary = "Extreme temperature peak (${String.format("%.1f", maxTempC)}°C) detected. High temperatures accelerate Urea ammonia gas loss into the air.",
                    ureaAdvice = "⚠️ Avoid topdressing Urea during peak sunshine hours (10 AM - 3 PM).",
                    npkAdvice = "🟡 Incorporate NPK fertilizers into soil or apply with light standing irrigation water.",
                    actionStep = "Apply fertilizer strictly during early morning (6-8 AM) or late afternoon (4-6 PM) to prevent crop scorching and nitrogen gas loss.",
                    bestApplicationWindow = "Early Morning (6:00 AM - 8:30 AM) or Late Evening"
                )
            }
        }
        windSpeedKmh >= 25.0 -> {
            when (language) {
                AppLanguage.TAGALOG -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "💨 BABALA SA MALAKAS NA HANGIN: HINDI PANTAY NA PAGSABOG",
                    summary = "Malalakas na bugso ng hangin (${String.format("%.1f", windSpeedKmh)} km/h). Hindi magiging pantay ang pagsasabog ng pataba o foliar spray.",
                    ureaAdvice = "⚠️ Iwasan ang foliar spray o pinong butil ng Urea sa mahanging bukirin.",
                    npkAdvice = "🟡 Ilagay ang basal NPK nang malapit sa lupa o sa antas ng tubig.",
                    actionStep = "Ipagpaliban ang foliar spray sa oras na kalmado ang hangin.",
                    bestApplicationWindow = "Kalmadong Umaga / Mahinang Hangin (< 15 km/h)"
                )
                AppLanguage.TAGLISH -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "💨 HIGH WIND ALERT: DRIFT & UNEVEN SPREAD",
                    summary = "Strong wind gusts (${String.format("%.1f", windSpeedKmh)} km/h). Magiging uneven ang pagsabog ng granular at foliar spray.",
                    ureaAdvice = "⚠️ Avoid foliar nitrogen spray or fine granular broadcasting in open windy fields.",
                    npkAdvice = "🟡 Apply basal granules directly close to soil surface.",
                    actionStep = "Reschedule foliar liquid spraying to calm morning hours.",
                    bestApplicationWindow = "Calm Morning / Low Wind Window (< 15 km/h)"
                )
                AppLanguage.ILOCANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "💨 PAKDAAR TI NAPIGSA NGA ANGIN: DIMO AGIBELLENG",
                    summary = "Napigsa ti angin (${String.format("%.1f", windSpeedKmh)} km/h). Saan a maipabukel a nasayaat ti abono.",
                    ureaAdvice = "⚠️ Liklikan ti foliar spray wenno abono a pino.",
                    npkAdvice = "🟡 Ikabil ti basal NPK nga asideg iti daga.",
                    actionStep = "Urnayen ti panagbomba iti nasapa a kalmado ti angin.",
                    bestApplicationWindow = "Kalmado a Bigat (< 15 km/h)"
                )
                AppLanguage.CEBUANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "💨 PAHINUMDOM SA KUSOG NGA HANGIN: DILI PATAS NGA PAGKASABWAG",
                    summary = "Kusog ang hangin (${String.format("%.1f", windSpeedKmh)} km/h). Dili maapod-apod og maayo ang abuno o foliar spray.",
                    ureaAdvice = "⚠️ Likayi ang foliar spray o pino nga Urea sa mahangin nga basakan.",
                    npkAdvice = "🟡 Ibutang ang NPK duol sa yuta.",
                    actionStep = "Ibalhin ang pag-spray sa buntag nga walay hangin.",
                    bestApplicationWindow = "Kalmado nga Buntag (< 15 km/h)"
                )
                else -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.WARNING,
                    title = "💨 HIGH WIND ALERT: DRIFT & UNEVEN SPREAD",
                    summary = "Strong wind gusts (${String.format("%.1f", windSpeedKmh)} km/h). Granular and foliar sprays will suffer from uneven field distribution.",
                    ureaAdvice = "⚠️ Avoid foliar nitrogen spray or fine granular broadcasting in open windy fields.",
                    npkAdvice = "🟡 Apply basal granules directly close to soil surface or flood-water line.",
                    actionStep = "Use broadcast shield or reschedule foliar liquid fertilizer spraying to calm morning hours.",
                    bestApplicationWindow = "Calm Morning / Low Wind Window (< 15 km/h)"
                )
            }
        }
        precipitationSumMm in 8.0..19.9 -> {
            when (language) {
                AppLanguage.TAGALOG -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.CAUTION,
                    title = "🌧️ KATAMTAMANG ULAN: MAG-INGAT SA PAG-AABONO",
                    summary = "Katamtamang ulan (${String.format("%.1f", precipitationSumMm)} mm) ang inaasahan. Maaari itong makatulong sa pagkatunaw ng abono, ngunit bantayan ang pag-apaw ng tubig.",
                    ureaAdvice = "🟡 Maglagay ng Urea kung kontrolado ang tubig sa palayan (< 3cm).",
                    npkAdvice = "🟢 Angkop para sa pagsasabog bago maghuling suyod o pagpapatag.",
                    actionStep = "Siguruhing sarado ang mga tagusan o labasan ng tubig sa pilapil.",
                    bestApplicationWindow = "Agad (Siguruhing nakasara ang labasan ng tubig)"
                )
                AppLanguage.TAGLISH -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.CAUTION,
                    title = "🌧️ MODERATE RAIN EXPECTED: PROCEED WITH CAUTION",
                    summary = "Moderate rain (${String.format("%.1f", precipitationSumMm)} mm) expected. Good for fertilizer dissolving, but keep paddy water level low.",
                    ureaAdvice = "🟡 Apply Urea only if controlled ang standing water (< 3cm).",
                    npkAdvice = "🟢 Suitable for basal soil incorporation prior to harrowing.",
                    actionStep = "Ensure field spillways are closed before broadcasting.",
                    bestApplicationWindow = "Immediate (Keep bunds closed)"
                )
                AppLanguage.ILOCANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.CAUTION,
                    title = "🌧️ KABASSIT A TODO: AGALWAD ITI PANAG-IPAITABA",
                    summary = "Adda bassit a todo (${String.format("%.1f", precipitationSumMm)} mm). Nasayaat a pagrunaw ti abono ngem alwadan ti danum.",
                    ureaAdvice = "🟡 Agikabil no kontrolado ti danum (< 3cm).",
                    npkAdvice = "🟢 Nasayaat para iti basal sakbay ti panagsuyod.",
                    actionStep = "Iserra dagiti luwasan ti danum iti tambak.",
                    bestApplicationWindow = "Ita a mismo (Iserra ti tambak)"
                )
                AppLanguage.CEBUANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.CAUTION,
                    title = "🌧️ KASARANGANG ULAN: PAG-AMPING SA PAG-ABUNO",
                    summary = "Adunay kasarangan nga ulan (${String.format("%.1f", precipitationSumMm)} mm). Maayo sa pagkatunaw sa abuno apan bantayi ang lebel sa tubig.",
                    ureaAdvice = "🟡 Butangi og Urea kon kontrolado ang tubig (< 3cm).",
                    npkAdvice = "🟢 Maayo para sa basal sa dili pa mag-suyod.",
                    actionStep = "Siguroha nga sirado ang gawasanan sa tubig sa pilapil.",
                    bestApplicationWindow = "Karon dayon (Sirad-i ang pilapil)"
                )
                else -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.CAUTION,
                    title = "🌧️ MODERATE RAIN EXPECTED: PROCEED WITH CAUTION",
                    summary = "Moderate rainfall (${String.format("%.1f", precipitationSumMm)} mm) expected. Light rain can assist nutrient dissolution, but excess water risks runoff.",
                    ureaAdvice = "🟡 Apply Urea only if paddy water level is controlled (< 3cm standing water).",
                    npkAdvice = "🟢 Suitable for basal soil incorporation prior to harrowing.",
                    actionStep = "Ensure field spillways are closed before broadcasting to trap dissolved nutrients.",
                    bestApplicationWindow = "Immediate (Ensure field bunds are closed)"
                )
            }
        }
        else -> {
            when (language) {
                AppLanguage.TAGALOG -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.OPTIMAL,
                    title = "✅ TAMANG PANAHON PARA SA PAGLALAGAY NG ABONO",
                    summary = "Maganda at paborableng panahon (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm ulan). Pinakamataas ang bisa ng abono sa panahong ito.",
                    ureaAdvice = "🟢 Tamang-tama para sa pagpapasabog ng Urea (21 DAT / Paglilihi).",
                    npkAdvice = "🟢 Mainam para sa Complete 14-14-14 / DAP basal application.",
                    actionStep = "Ipatuloy ang nakaiskedyul na pag-aabono. Panatilihin ang 2-3cm na tubig sa palayan.",
                    bestApplicationWindow = "Ngayon at sa Susunod na 48 Oras (Magandang Kondisyon)"
                )
                AppLanguage.TAGLISH -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.OPTIMAL,
                    title = "✅ OPTIMAL WEATHER WINDOW FOR FERTILIZER APPLICATION",
                    summary = "Very favorable weather (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm rain). High fertilizer absorption efficiency.",
                    ureaAdvice = "🟢 Ideal for Urea topdressing (21 DAT / Panicle Initiation).",
                    npkAdvice = "🟢 Optimal for Complete 14-14-14 / DAP application.",
                    actionStep = "Proceed with scheduled fertilizer application. Maintain 2-3cm water in rice paddies.",
                    bestApplicationWindow = "Today & Next 48 Hours (Optimal Conditions)"
                )
                AppLanguage.ILOCANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.OPTIMAL,
                    title = "✅ KANUSTO A PANAWEN ITI PANAG-IPAITABA",
                    summary = "Nasayaat unay ti panawen (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm todo). Agserbi a naimbag ti abono.",
                    ureaAdvice = "🟢 Paborable unay iti panagibelleng ti Urea.",
                    npkAdvice = "🟢 Nasayaat iti Complete 14-14-14 / DAP.",
                    actionStep = "Ituloy ti nakaiskedyul a panag-ipaitaba. Salimetmetan ti 2-3cm a danum.",
                    bestApplicationWindow = "Ita ken iti sumungad a 48 nga oras"
                )
                AppLanguage.CEBUANO -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.OPTIMAL,
                    title = "✅ HAUM NGA PANAHON SA PAG-ABUNO",
                    summary = "Maayo kaayo ang panahon (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm ulan). Taas kaayo ang epekto sa abuno.",
                    ureaAdvice = "🟢 Maayo kaayo sa pag-sabwag og Urea.",
                    npkAdvice = "🟢 Haum kaayo sa Complete 14-14-14 / DAP.",
                    actionStep = "Ipadayon ang gi-eskedyul nga pag-abuno. Hupti ang 2-3cm nga tubig sa basakan.",
                    bestApplicationWindow = "Karon ug sa sunod nga 48 ka oras"
                )
                else -> FertilizerAdvisory(
                    riskLevel = WeatherRiskLevel.OPTIMAL,
                    title = "✅ OPTIMAL WEATHER WINDOW FOR FERTILIZER APPLICATION",
                    summary = "Favorable weather conditions (${String.format("%.1f", maxTempC)}°C, ${String.format("%.1f", precipitationSumMm)} mm rain). Excellent condition for basal and topdress fertilizer efficiency.",
                    ureaAdvice = "🟢 Ideal for Urea topdressing (21 DAT / Panicle Initiation).",
                    npkAdvice = "🟢 Optimal for Complete 14-14-14 / DAP application.",
                    actionStep = "Proceed with scheduled fertilizer application. Maintain 2-3cm standing water in rice paddies.",
                    bestApplicationWindow = "Today & Next 48 Hours (Optimal Field Conditions)"
                )
            }
        }
    }
}

fun localizeWeatherCondition(condition: String, language: AppLanguage): String {
    return when (language) {
        AppLanguage.TAGALOG -> when {
            condition.contains("Clear", ignoreCase = true) || condition.contains("Sunny", ignoreCase = true) -> "Maaliwalas / Maaraw ☀️"
            condition.contains("Partly Cloudy", ignoreCase = true) -> "Bahagyang Maulap ⛅"
            condition.contains("Overcast", ignoreCase = true) || condition.contains("Cloudy", ignoreCase = true) -> "Maulap ☁️"
            condition.contains("Thunderstorm", ignoreCase = true) -> "May Kidlat at Kulog ⛈️"
            condition.contains("Heavy Rain", ignoreCase = true) -> "Malakas na Ulan 🌧️"
            condition.contains("Light Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> "Mahinang Ulan 🌧️"
            condition.contains("Rain", ignoreCase = true) -> "May Pag-ulan 🌧️"
            else -> condition
        }
        AppLanguage.TAGLISH -> when {
            condition.contains("Clear", ignoreCase = true) -> "Clear Sky ☀️"
            condition.contains("Partly Cloudy", ignoreCase = true) -> "Partly Cloudy ⛅"
            condition.contains("Overcast", ignoreCase = true) || condition.contains("Cloudy", ignoreCase = true) -> "Cloudy / Maulap ☁️"
            condition.contains("Thunderstorm", ignoreCase = true) -> "Thunderstorm ⛈️"
            condition.contains("Heavy Rain", ignoreCase = true) -> "Heavy Rain 🌧️"
            condition.contains("Light Rain", ignoreCase = true) -> "Light Rain 🌧️"
            else -> condition
        }
        AppLanguage.ILOCANO -> when {
            condition.contains("Clear", ignoreCase = true) || condition.contains("Sunny", ignoreCase = true) -> "Nalitnaw / Mainit ☀️"
            condition.contains("Partly Cloudy", ignoreCase = true) -> "Bassit nga Ulep ⛅"
            condition.contains("Overcast", ignoreCase = true) || condition.contains("Cloudy", ignoreCase = true) -> "Naulep ☁️"
            condition.contains("Thunderstorm", ignoreCase = true) -> "Gurruod ken Kimat ⛈️"
            condition.contains("Heavy Rain", ignoreCase = true) -> "Napigsa a Todo 🌧️"
            condition.contains("Light Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> "Nalag-an a Todo 🌧️"
            else -> condition
        }
        AppLanguage.CEBUANO -> when {
            condition.contains("Clear", ignoreCase = true) || condition.contains("Sunny", ignoreCase = true) -> "Hayag / Mainit ☀️"
            condition.contains("Partly Cloudy", ignoreCase = true) -> "Gamayng Dag-um ⛅"
            condition.contains("Overcast", ignoreCase = true) || condition.contains("Cloudy", ignoreCase = true) -> "Dag-umon ☁️"
            condition.contains("Thunderstorm", ignoreCase = true) -> "Dalugdog ug Kilat ⛈️"
            condition.contains("Heavy Rain", ignoreCase = true) -> "Kusog nga Ulan 🌧️"
            condition.contains("Light Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> "Hinay nga Ulan 🌧️"
            else -> condition
        }
        else -> condition
    }
}

fun localizeDateLabel(label: String, language: AppLanguage): String {
    return when (language) {
        AppLanguage.TAGALOG -> when {
            label.equals("Today", ignoreCase = true) -> "Ngayon"
            label.equals("Tomorrow", ignoreCase = true) -> "Bukas"
            label.startsWith("Mon", ignoreCase = true) -> "Lunes"
            label.startsWith("Tue", ignoreCase = true) -> "Martes"
            label.startsWith("Wed", ignoreCase = true) -> "Miyerkules"
            label.startsWith("Thu", ignoreCase = true) -> "Huwebes"
            label.startsWith("Fri", ignoreCase = true) -> "Biyernes"
            label.startsWith("Sat", ignoreCase = true) -> "Sabado"
            label.startsWith("Sun", ignoreCase = true) -> "Linggo"
            else -> label
        }
        AppLanguage.TAGLISH -> when {
            label.equals("Today", ignoreCase = true) -> "Today (Ngayon)"
            label.equals("Tomorrow", ignoreCase = true) -> "Tomorrow (Bukas)"
            else -> label
        }
        AppLanguage.ILOCANO -> when {
            label.equals("Today", ignoreCase = true) -> "Ita"
            label.equals("Tomorrow", ignoreCase = true) -> "Bigat"
            label.startsWith("Mon", ignoreCase = true) -> "Lunes"
            label.startsWith("Tue", ignoreCase = true) -> "Martes"
            label.startsWith("Wed", ignoreCase = true) -> "Mierkoles"
            label.startsWith("Thu", ignoreCase = true) -> "Huebes"
            label.startsWith("Fri", ignoreCase = true) -> "Biernes"
            label.startsWith("Sat", ignoreCase = true) -> "Sabado"
            label.startsWith("Sun", ignoreCase = true) -> "Domingo"
            else -> label
        }
        AppLanguage.CEBUANO -> when {
            label.equals("Today", ignoreCase = true) -> "Karon"
            label.equals("Tomorrow", ignoreCase = true) -> "Ugma"
            label.startsWith("Mon", ignoreCase = true) -> "Lunes"
            label.startsWith("Tue", ignoreCase = true) -> "Martes"
            label.startsWith("Wed", ignoreCase = true) -> "Miyerkules"
            label.startsWith("Thu", ignoreCase = true) -> "Huwebes"
            label.startsWith("Fri", ignoreCase = true) -> "Biyernes"
            label.startsWith("Sat", ignoreCase = true) -> "Sabado"
            label.startsWith("Sun", ignoreCase = true) -> "Domingo"
            else -> label
        }
        else -> label
    }
}

