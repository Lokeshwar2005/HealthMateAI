package com.example.healthmateai.ai.diet

data class DietPlan(
    val summary: String,
    val mealSuggestions: List<String>,
    val hydrationTips: List<String>,
    val foodsToAvoid: List<String>,
    val healthyHabits: List<String>,
    val calorieAwareAdvice: List<String>
)

data class DietPlanPayload(
    val diabetesRisk: Float,
    val heartRisk: Float,
    val age: Int?,
    val bmi: Float?,
    val weight: Float?,
    val lifestyle: String
)

data class DietPlanJson(
    val summary: String? = null,
    val mealSuggestions: List<String>? = null,
    val hydrationTips: List<String>? = null,
    val foodsToAvoid: List<String>? = null,
    val healthyHabits: List<String>? = null,
    val calorieAwareAdvice: List<String>? = null
)
