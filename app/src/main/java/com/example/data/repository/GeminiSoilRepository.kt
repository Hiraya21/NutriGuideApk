package com.example.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class GeminiSoilAnalysisResult(
    val soilType: String,
    val healthScore: Int,
    val healthStatus: String,
    val nitrogenLevel: String,
    val nitrogenPpm: Int,
    val phosphorusLevel: String,
    val phosphorusPpm: Int,
    val potassiumLevel: String,
    val potassiumPpm: Int,
    val phValue: Double,
    val organicMatterPct: Double,
    val moisturePct: Double,
    val visualObservations: String,
    val summary: String,
    val recommendations: List<String>,
    val applicationSchedule: List<String>
)

class GeminiSoilRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private fun Bitmap.toBase64Jpeg(): String {
        val outputStream = ByteArrayOutputStream()
        // Compress image to reasonable size for API transfer
        val scaledBitmap = if (width > 1024 || height > 1024) {
            val scale = 1024.0f / maxOf(width, height)
            Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
        } else {
            this
        }
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun analyzeSoilImage(bitmap: Bitmap, crop: String): Result<GeminiSoilAnalysisResult> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API Key is not configured. Please set GEMINI_API_KEY in Secrets.")
                )
            }

            val base64Image = bitmap.toBase64Jpeg()

            val promptText = """
                You are a senior soil scientist and precision agriculture AI assistant.
                Analyze this soil image captured from the field for target crop: $crop.
                
                Examine visual characteristics in the image:
                1. Soil color (chroma, value, hue) and organic dark matter tint.
                2. Texture, grain size, aggregate structure (e.g. Clay, Clay Loam, Sandy Loam, Silt Clay, Peat).
                3. Surface moisture shine, cohesion, or cracking (estimated moisture %).
                4. Compaction, aeration, and nutrient deficiency indicators.

                Respond strictly with valid JSON conforming to this schema:
                {
                  "soilType": "Clay Loam",
                  "healthScore": 78,
                  "healthStatus": "MODERATE NUTRIENT BALANCE",
                  "nitrogenLevel": "Medium",
                  "nitrogenPpm": 28,
                  "phosphorusLevel": "Low",
                  "phosphorusPpm": 12,
                  "potassiumLevel": "Medium",
                  "potassiumPpm": 135,
                  "phValue": 6.3,
                  "organicMatterPct": 3.1,
                  "moisturePct": 45.0,
                  "visualObservations": "Rich dark brown chroma indicating moderate organic matter with granular loam structure and surface dampness.",
                  "summary": "Soil shows good moisture retention and aggregate stability, with slight phosphorus deficiency for target $crop.",
                  "recommendations": [
                    "Apply DAP (18-46-0) at basal stage to boost phosphorus.",
                    "Maintain balanced nitrogen top-dressing during active tillering."
                  ],
                  "applicationSchedule": [
                    "Basal (0-14 DAT): 2 bags Complete 14-14-14 / ha",
                    "Mid-tillering (21-28 DAT): 1.5 bags Urea / ha",
                    "Panicle Initiation (40-45 DAT): 1 bag MOP / ha"
                  ]
                }
            """.trimIndent()

            // Build request JSON
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                            put(JSONObject().put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            }))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Gemini API HTTP Error ${response.code}: $responseBodyString")
                )
            }

            val jsonResponse = JSONObject(responseBodyString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textResult = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (textResult.isBlank()) {
                return@withContext Result.failure(Exception("Gemini returned empty response text."))
            }

            // Clean markdown code fence if present
            val cleanedJson = textResult.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsedObj = JSONObject(cleanedJson)

            val recsArray = parsedObj.optJSONArray("recommendations")
            val recsList = mutableListOf<String>()
            if (recsArray != null) {
                for (i in 0 until recsArray.length()) {
                    recsList.add(recsArray.getString(i))
                }
            }

            val schedArray = parsedObj.optJSONArray("applicationSchedule")
            val schedList = mutableListOf<String>()
            if (schedArray != null) {
                for (i in 0 until schedArray.length()) {
                    schedList.add(schedArray.getString(i))
                }
            }

            val result = GeminiSoilAnalysisResult(
                soilType = parsedObj.optString("soilType", "Clay Loam"),
                healthScore = parsedObj.optInt("healthScore", 75),
                healthStatus = parsedObj.optString("healthStatus", "MODERATE NUTRIENT BALANCE"),
                nitrogenLevel = parsedObj.optString("nitrogenLevel", "Medium"),
                nitrogenPpm = parsedObj.optInt("nitrogenPpm", 25),
                phosphorusLevel = parsedObj.optString("phosphorusLevel", "Low"),
                phosphorusPpm = parsedObj.optInt("phosphorusPpm", 15),
                potassiumLevel = parsedObj.optString("potassiumLevel", "Medium"),
                potassiumPpm = parsedObj.optInt("potassiumPpm", 130),
                phValue = parsedObj.optDouble("phValue", 6.2),
                organicMatterPct = parsedObj.optDouble("organicMatterPct", 3.0),
                moisturePct = parsedObj.optDouble("moisturePct", 45.0),
                visualObservations = parsedObj.optString("visualObservations", "Sample analyzed by Gemini AI."),
                summary = parsedObj.optString("summary", "Soil health assessment generated by Gemini AI."),
                recommendations = if (recsList.isNotEmpty()) recsList else listOf("Apply balanced N-P-K fertilizer based on crop schedule."),
                applicationSchedule = if (schedList.isNotEmpty()) schedList else listOf("Basal application at planting stage.")
            )

            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
