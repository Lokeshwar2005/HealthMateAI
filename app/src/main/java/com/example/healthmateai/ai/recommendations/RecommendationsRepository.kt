package com.example.healthmateai.ai.recommendations

import com.example.healthmateai.BuildConfig
import com.example.healthmateai.ai.HealthPredictionSnapshot
import com.example.healthmateai.network.openrouter.OpenRouterApiClient
import com.example.healthmateai.network.openrouter.OpenRouterMessage
import com.example.healthmateai.network.openrouter.OpenRouterRequest
import com.example.healthmateai.network.openrouter.firstMessageContent
import com.example.healthmateai.network.openrouter.openRouterAuthHeader

data class RecommendationSection(
    val title: String,
    val items: List<String>,
    val emoji: String
)

class RecommendationsRepository {

    private var cachedRecommendations: List<RecommendationSection>? = null
    private var cachedSnapshotHash: Int = 0

    suspend fun getRecommendations(snapshot: HealthPredictionSnapshot): List<RecommendationSection> {
        val snapshotHash = snapshot.hashCode()
        if (snapshotHash == cachedSnapshotHash && cachedRecommendations != null) {
            return cachedRecommendations!!
        }

        val prompt = buildPrompt(snapshot)
        val request = OpenRouterRequest(
            model = BuildConfig.OPENROUTER_MODEL,
            messages = listOf(
                OpenRouterMessage(role = "system", content = SYSTEM_PROMPT),
                OpenRouterMessage(role = "user", content = prompt)
            ),
            temperature = 0.4,
            maxTokens = 800
        )

        val response = OpenRouterApiClient.service.createChatCompletion(
            authorization = openRouterAuthHeader(),
            request = request
        )

        val rawText = response.firstMessageContent()
        val sections = parseRecommendations(rawText)
        cachedRecommendations = sections
        cachedSnapshotHash = snapshotHash
        return sections
    }

    fun clearCache() {
        cachedRecommendations = null
        cachedSnapshotHash = 0
    }

    private fun buildPrompt(snapshot: HealthPredictionSnapshot): String {
        return buildString {
            appendLine("Generate personalized health recommendations for a user with the following health profile:")
            appendLine()
            if (snapshot.hasDiabetesPrediction) {
                appendLine("- Diabetes Risk: ${(snapshot.diabetesRisk * 100).toInt()}%")
            }
            if (snapshot.hasHeartPrediction) {
                appendLine("- Heart Disease Risk: ${(snapshot.heartRisk * 100).toInt()}%")
            }
            snapshot.age?.let { appendLine("- Age: $it years") }
            snapshot.bmi?.let { appendLine("- BMI: %.1f".format(it)) }
            snapshot.weight?.let { appendLine("- Weight: %.1f kg".format(it)) }
            if (snapshot.lifestyleSummary != "Not enough lifestyle data yet") {
                appendLine("- ${ snapshot.lifestyleSummary}")
            }
            appendLine()
            appendLine("Provide recommendations in exactly 4 sections with this format:")
            appendLine("## 🥗 Diet Suggestions")
            appendLine("- item 1")
            appendLine("- item 2")
            appendLine("- item 3")
            appendLine("## 🏃 Exercise Advice")
            appendLine("- item 1")
            appendLine("- item 2")
            appendLine("- item 3")
            appendLine("## 📊 Monitoring Tips")
            appendLine("- item 1")
            appendLine("- item 2")
            appendLine("- item 3")
            appendLine("## 🌿 Lifestyle Improvements")
            appendLine("- item 1")
            appendLine("- item 2")
            appendLine("- item 3")
            appendLine()
            appendLine("Keep each item concise (1 sentence). Be specific and actionable based on their risk levels.")
        }
    }

    private fun parseRecommendations(text: String): List<RecommendationSection> {
        val sections = mutableListOf<RecommendationSection>()
        var currentTitle = ""
        var currentEmoji = "💡"
        var currentItems = mutableListOf<String>()

        for (line in text.lines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("## ") || trimmed.startsWith("### ")) {
                if (currentTitle.isNotBlank() && currentItems.isNotEmpty()) {
                    sections.add(RecommendationSection(currentTitle, currentItems.toList(), currentEmoji))
                }
                val headerText = trimmed.removePrefix("## ").removePrefix("### ").trim()
                currentEmoji = headerText.take(2).takeIf { it.length == 2 && !it[0].isLetterOrDigit() } ?: "💡"
                currentTitle = headerText.removePrefix(currentEmoji).trim()
                currentItems = mutableListOf()
            } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                currentItems.add(trimmed.removePrefix("- ").removePrefix("* ").trim())
            } else if (trimmed.matches(Regex("^\\d+\\.\\s+.*"))) {
                currentItems.add(trimmed.replaceFirst(Regex("^\\d+\\.\\s+"), "").trim())
            }
        }

        if (currentTitle.isNotBlank() && currentItems.isNotEmpty()) {
            sections.add(RecommendationSection(currentTitle, currentItems.toList(), currentEmoji))
        }

        // Fallback if parsing fails
        if (sections.isEmpty()) {
            sections.add(
                RecommendationSection(
                    title = "General Recommendations",
                    items = text.lines()
                        .filter { it.trim().isNotBlank() }
                        .take(6)
                        .map { it.trim().removePrefix("- ").removePrefix("* ") },
                    emoji = "💡"
                )
            )
        }

        return sections
    }

    companion object {
        private const val SYSTEM_PROMPT = """You are HealthMate AI, a professional healthcare recommendation assistant. 
Provide evidence-based, personalized health recommendations based on the user's risk profile and health data.
Be specific, actionable, and encouraging. Do not diagnose conditions - only provide lifestyle and wellness suggestions.
Always format your response with the exact section headers requested."""
    }
}
