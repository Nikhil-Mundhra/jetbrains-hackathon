package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PredictiveForecast(
    val lotId: String,
    val targetEtaMinutes: Int, // e.g. arrival in 15, 30, 45, 60 minutes
    val estimatedOpenBays: Int,
    val openBayProbabilityPercentage: Int, // e.g. 88%
    val decayRatePer10Min: Double, // rate at which slots fill up
    val congestionLevel: String, // "Low Risk", "Moderate", "High Turnover"
    val recommendation: String
)
