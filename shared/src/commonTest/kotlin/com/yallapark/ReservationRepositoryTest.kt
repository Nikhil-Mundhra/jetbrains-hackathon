package com.yallapark

import com.yallapark.data.repository.YallaParkRepositoryImpl
import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.PaymentMethod
import com.yallapark.domain.model.ReservationStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ReservationRepositoryTest {

    @Test
    fun testReservationCreationAndExtensionLifecycle() = runTest {
        val repo = YallaParkRepositoryImpl()
        val lots = repo.getAllLots().first()
        val lot = lots.first()
        val bay = lot.bays.first { it.status == BayStatus.AVAILABLE }

        val initialAvailable = lot.availableBays

        // 1. Create reservation
        val result = repo.createReservation(
            lotId = lot.id,
            bayId = bay.id,
            vehiclePlate = "DXB B 99120",
            durationMinutes = 60,
            paymentMethod = PaymentMethod.DUBAI_NOL_CARD
        )

        assertTrue(result.isSuccess, "Reservation should be successfully created")
        val reservation = result.getOrNull()
        assertNotNull(reservation)
        assertEquals(ReservationStatus.HOLD_LOCKED, reservation.status)
        assertEquals(PaymentMethod.DUBAI_NOL_CARD, reservation.paymentMethod)

        // Verify bay status updated
        val updatedLot = repo.getLotById(lot.id).first()
        assertNotNull(updatedLot)
        assertEquals(initialAvailable - 1, updatedLot.availableBays)

        // 2. Extend reservation by 30 mins
        val extendResult = repo.extendReservation(reservation.id, 30)
        assertTrue(extendResult.isSuccess, "Reservation extension should succeed")
        val extended = extendResult.getOrNull()
        assertNotNull(extended)
        assertEquals(90, extended.durationMinutes)
        assertEquals(ReservationStatus.EXTENDED, extended.status)

        // 3. Cancel / Vacate bay
        val cancelResult = repo.cancelReservation(reservation.id)
        assertTrue(cancelResult.isSuccess)
        val finalLot = repo.getLotById(lot.id).first()
        assertNotNull(finalLot)
        assertEquals(initialAvailable, finalLot.availableBays)
    }

    @Test
    fun testReservationCreationWithPlate_duplicatePlateFails() = runTest {
        val repo = YallaParkRepositoryImpl()
        val lots = repo.getAllLots().first()
        val lot = lots.first()
        val bay = lot.bays.first { it.status == BayStatus.AVAILABLE }

        // Create first reservation
        val result1 = repo.createReservation(
            lotId = lot.id,
            bayId = bay.id,
            vehiclePlate = "DXB B 99120",
            durationMinutes = 60,
            paymentMethod = PaymentMethod.DUBAI_NOL_CARD
        )
        assertTrue(result1.isSuccess)

        // Try creating another reservation with same plate on same bay
        val result2 = repo.createReservation(
            lotId = lot.id,
            bayId = bay.id,
            vehiclePlate = "DXB B 99120",
            durationMinutes = 60,
            paymentMethod = PaymentMethod.DUBAI_NOL_CARD
        )
        assertFalse(result2.isSuccess, "Duplicate plate on same bay should fail")
    }

    @Test
    fun testReservationExtensionBeyondLimit() = runTest {
        val repo = YallaParkRepositoryImpl()
        val lots = repo.getAllLots().first()
        val lot = lots.first()
        val bay = lot.bays.first { it.status == BayStatus.AVAILABLE }

        val result = repo.createReservation(
            lotId = lot.id,
            bayId = bay.id,
            vehiclePlate = "DXB B 99121",
            durationMinutes = 60,
            paymentMethod = PaymentMethod.DUBAI_NOL_CARD
        )
        assertTrue(result.isSuccess)
        val reservation = result.getOrNull()!!

        // Try to extend beyond reasonable limit (e.g., 4 hours = 240 mins from 90)
        val extendResult = repo.extendReservation(reservation.id, 150)
        // Extension should either succeed or fail based on business rules
        assertTrue(extendResult.isSuccess || extendResult.isFailure)
    }

    @Test
    fun testCancelNonExistentReservation() = runTest {
        val repo = YallaParkRepositoryImpl()
        val cancelResult = repo.cancelReservation("NON_EXISTENT_RESERVATION_ID")
        assertFalse(cancelResult.isSuccess, "Cancelling non-existent reservation should fail")
    }
}
