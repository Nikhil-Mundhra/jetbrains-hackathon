package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(val title: String, val subtitle: String, val badgeColor: Long) {
    DRIVER(
        title = "Motorist / Driver",
        subtitle = "Search, predict ETA, book bays & pay",
        badgeColor = 0xFF00897B // Teal
    ),
    RTA_AUTHORITY(
        title = "RTA Public Authority",
        subtitle = "Municipal regulation, citywide tariffs & compliance",
        badgeColor = 0xFF1565C0 // Blue
    ),
    MAWAQIF_OPERATOR(
        title = "Mawaqif Operator",
        subtitle = "Zone enforcement & live sensor telemetry",
        badgeColor = 0xFFE65100 // Deep Orange
    ),
    PRIVATE_OPERATOR(
        title = "Commercial Garage Operator",
        subtitle = "Add/remove facility, capacity & dynamic rates",
        badgeColor = 0xFF4A148C // Purple
    )
}

@Serializable
data class MerchantPerk(
    val id: String,
    val merchantName: String,
    val location: String, // e.g. "Al Fahidi, Bur Dubai"
    val perkTitle: String, // e.g. "20% off Specialty Coffee"
    val requiredEcoPoints: Int,
    val promoCode: String,
    val category: String = "Dining"
)
