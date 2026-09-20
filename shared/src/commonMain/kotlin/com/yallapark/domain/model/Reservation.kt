package com.yallapark.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentMethod(val label: String) {
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    DUBAI_NOL_CARD("RTA NOL Card"),
    CREDIT_DEBIT_CARD("Credit / Debit Card")
}

@Serializable
enum class ReservationStatus {
    HOLD_LOCKED, // 15-minute guaranteed hold upon booking
    ACTIVE_PARKED, // Vehicle parked / checked in
    EXTENDED,
    COMPLETED,
    EXPIRED_RELEASED,
    CANCELLED
}

@Serializable
data class Reservation(
    val id: String,
    val lotId: String,
    val lotName: String,
    val bayId: String,
    val bayNumber: String,
    val bayType: BayType,
    val vehiclePlateNumber: String, // e.g. "Dubai A 12345"
    val startTimeEpochMs: Long,
    val durationMinutes: Int,
    val endTimeEpochMs: Long,
    val lockExpiryEpochMs: Long, // 15 min lock hold
    val hourlyRateAed: Double,
    val totalCostAed: Double,
    val paymentMethod: PaymentMethod,
    val status: ReservationStatus = ReservationStatus.HOLD_LOCKED,
    val qrPassCode: String = "YP-${bayNumber}-${vehiclePlateNumber.takeLast(4)}"
)
