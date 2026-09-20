package com.yallapark.domain.repository

import com.yallapark.domain.model.PaymentMethod
import com.yallapark.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getActiveReservation(): Flow<Reservation?>
    suspend fun createReservation(
        lotId: String,
        bayId: String,
        vehiclePlate: String,
        durationMinutes: Int,
        paymentMethod: PaymentMethod
    ): Result<Reservation>
    suspend fun extendReservation(reservationId: String, additionalMinutes: Int): Result<Reservation>
    suspend fun cancelReservation(reservationId: String): Result<Boolean>
}
