package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class BayType {
    STANDARD,
    PEOPLE_OF_DETERMINATION, // POD Accessibility
    WOMEN_ONLY_PINK,         // Designated Pink Bays
    DELIVERY_RIDER,          // Quick 15-20 min drop-offs to prevent double parking
    EV_CHARGING              // Electric Vehicle Charging
}

@Serializable
enum class BayStatus {
    AVAILABLE,
    OCCUPIED,
    RESERVED,
    MAINTENANCE
}

@Serializable
data class ParkingBay(
    val id: String,
    val bayNumber: String,
    val type: BayType,
    val status: BayStatus,
    val floorLevel: String = "Ground",
    val sensorId: String = "SENSOR_${bayNumber}",
    val reservedByUserId: String? = null
)
