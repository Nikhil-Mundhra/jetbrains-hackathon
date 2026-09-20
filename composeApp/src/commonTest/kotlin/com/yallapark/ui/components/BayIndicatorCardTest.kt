package com.yallapark.ui.components

import com.yallapark.domain.model.BayStatus
import com.yallapark.domain.model.BayType
import com.yallapark.domain.model.ParkingBay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BayIndicatorCardTest {

    @Test
    fun testPODBayProperties() {
        val podBay = ParkingBay(
            id = "BAY_1",
            bayNumber = "P-1",
            type = BayType.PEOPLE_OF_DETERMINATION,
            status = BayStatus.AVAILABLE
        )
        assertEquals(BayType.PEOPLE_OF_DETERMINATION, podBay.type)
        assertEquals(BayStatus.AVAILABLE, podBay.status)
        assertEquals("P-1", podBay.bayNumber)
    }

    @Test
    fun testWomenOnlyBayProperties() {
        val pinkBay = ParkingBay(
            id = "BAY_2",
            bayNumber = "W-1",
            type = BayType.WOMEN_ONLY_PINK,
            status = BayStatus.AVAILABLE
        )
        assertEquals(BayType.WOMEN_ONLY_PINK, pinkBay.type)
        assertEquals(BayStatus.AVAILABLE, pinkBay.status)
    }

    @Test
    fun testDeliveryRiderBayOccupied() {
        val deliveryBay = ParkingBay(
            id = "BAY_3",
            bayNumber = "D-1",
            type = BayType.DELIVERY_RIDER,
            status = BayStatus.OCCUPIED
        )
        assertEquals(BayType.DELIVERY_RIDER, deliveryBay.type)
        assertEquals(BayStatus.OCCUPIED, deliveryBay.status)
    }

    @Test
    fun testEVChargingBayReserved() {
        val evBay = ParkingBay(
            id = "BAY_4",
            bayNumber = "S-1",
            type = BayType.EV_CHARGING,
            status = BayStatus.RESERVED
        )
        assertEquals(BayType.EV_CHARGING, evBay.type)
        assertEquals(BayStatus.RESERVED, evBay.status)
    }

    @Test
    fun testStandardBayAvailable() {
        val standardBay = ParkingBay(
            id = "BAY_5",
            bayNumber = "S-2",
            type = BayType.STANDARD,
            status = BayStatus.AVAILABLE
        )
        assertEquals(BayType.STANDARD, standardBay.type)
        assertEquals(BayStatus.AVAILABLE, standardBay.status)
    }

    @Test
    fun testAllBayTypesAreCovered() {
        val allTypes = BayType.entries
        assertTrue(allTypes.size >= 5, "Should have at least 5 bay types (Standard, POD, Women, Delivery, EV)")
    }

    @Test
    fun testAllBayStatusesAreCovered() {
        val allStatuses = BayStatus.entries
        assertTrue(allStatuses.size >= 3, "Should have at least 3 statuses (Available, Occupied, Reserved)")
    }
}