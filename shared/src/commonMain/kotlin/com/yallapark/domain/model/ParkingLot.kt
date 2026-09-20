package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DubaiZone(val displayName: String, val lat: Double, val lon: Double) {
    BUR_DUBAI("Bur Dubai", 25.2530, 55.2970),
    KARAMA("Al Karama", 25.2425, 55.3050),
    DEIRA("Deira", 25.2680, 55.3180),
    DOWNTOWN("Downtown Dubai", 25.1972, 55.2744)
}

@Serializable
enum class FacilityType {
    SURFACE_OPEN_LOT,
    MULTI_STOREY_GARAGE,
    UNDERGROUND_SMART_PARK,
    ON_STREET_METRIC
}

@Serializable
data class ParkingLot(
    val id: String,
    val name: String,
    val zone: DubaiZone,
    val facilityType: FacilityType,
    val latitude: Double,
    val longitude: Double,
    val totalCapacity: Int,
    val availableBays: Int,
    val hourlyRateAed: Double,
    val operatorName: String = "RTA Dubai Public Parking",
    val isManagedByRta: Boolean = true,
    val bays: List<ParkingBay> = emptyList(),
    val hasPodBays: Boolean = true,
    val hasWomenPinkBays: Boolean = true,
    val hasDeliveryBays: Boolean = true,
    val hasEvCharging: Boolean = false,
    val operatingHours: String = "24/7 (Paid 8AM - 10PM)",
    val address: String = ""
) {
    val occupancyRate: Double
        get() = if (totalCapacity > 0) (totalCapacity - availableBays).toDouble() / totalCapacity else 0.0

    val occupancyPercentage: Int
        get() = (occupancyRate * 100).toInt().coerceIn(0, 100)

    val podAvailableCount: Int
        get() = bays.count { it.type == BayType.PEOPLE_OF_DETERMINATION && it.status == BayStatus.AVAILABLE }

    val womenPinkAvailableCount: Int
        get() = bays.count { it.type == BayType.WOMEN_ONLY_PINK && it.status == BayStatus.AVAILABLE }

    val deliveryAvailableCount: Int
        get() = bays.count { it.type == BayType.DELIVERY_RIDER && it.status == BayStatus.AVAILABLE }
}
