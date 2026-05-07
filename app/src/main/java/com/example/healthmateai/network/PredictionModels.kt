package com.example.healthmateai.network

data class PredictionRequest(
    val disease: String,
    val inputs: Map<String, Any>
)

data class PredictionResponse(
    val risk: String,
    val probability: Double,
    val factors: List<String>,
    val advice: String
)