package com.example.healthmateai.ai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HealthPredictionSnapshot(
    val diabetesRisk: Float = 0f,
    val heartRisk: Float = 0f,
    val age: Int? = null,
    val bmi: Float? = null,
    val weight: Float? = null,
    val lifestyleSummary: String = "Not enough lifestyle data yet"
)

class HealthInsightsViewModel : ViewModel() {

    private val _snapshot = MutableStateFlow(HealthPredictionSnapshot())
    val snapshot: StateFlow<HealthPredictionSnapshot> = _snapshot.asStateFlow()

    fun updatePrediction(disease: String, probability: Float, inputs: Map<String, Any>) {
        val current = _snapshot.value
        val age = extractNumber(inputs["Age"] ?: inputs["age"])?.toInt() ?: current.age
        val bmi = extractNumber(inputs["BMI"] ?: inputs["bmi"]) ?: current.bmi
        val weight = extractNumber(inputs["weight"] ?: inputs["Weight"]) ?: current.weight

        val lifestyleTokens = buildList {
            appendFlag(inputs, "smoking", "smoking")
            appendFlag(inputs, "high_blood_pressure", "high blood pressure")
            appendFlag(inputs, "diabetes", "diabetes history")
            appendFlag(inputs, "anaemia", "anaemia")
        }
        val lifestyleSummary = if (lifestyleTokens.isEmpty()) {
            current.lifestyleSummary
        } else {
            "Lifestyle indicators: ${lifestyleTokens.joinToString()}"
        }

        _snapshot.value = if (disease.equals("diabetes", ignoreCase = true)) {
            current.copy(
                diabetesRisk = probability,
                age = age,
                bmi = bmi,
                weight = weight,
                lifestyleSummary = lifestyleSummary
            )
        } else {
            current.copy(
                heartRisk = probability,
                age = age,
                bmi = bmi,
                weight = weight,
                lifestyleSummary = lifestyleSummary
            )
        }
    }

    private fun extractNumber(value: Any?): Float? {
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull()
            else -> null
        }
    }

    private fun MutableList<String>.appendFlag(inputs: Map<String, Any>, key: String, label: String) {
        val raw = inputs[key] ?: return
        val isActive = when (raw) {
            is Number -> raw.toFloat() >= 0.5f
            is Boolean -> raw
            is String -> raw == "1" || raw.equals("true", ignoreCase = true)
            else -> false
        }
        if (isActive) add(label)
    }
}
