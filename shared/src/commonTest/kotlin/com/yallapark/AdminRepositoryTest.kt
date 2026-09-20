package com.yallapark

import com.yallapark.data.repository.YallaParkRepositoryImpl
import com.yallapark.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdminRepositoryTest {

    @Test
    fun testWay2AddAndRemoveParkingLot() = runTest {
        val repo = YallaParkRepositoryImpl()
        val initialCount = repo.getAllLots().first().size

        // Add new facility
        val newLot = ParkingLot(
            id = "LOT_TEST_ADMIN",
            name = "Test Municipal Facility",
            zone = DubaiZone.KARAMA,
            facilityType = FacilityType.SURFACE_OPEN_LOT,
            latitude = 25.242,
            longitude = 55.302,
            totalCapacity = 40,
            availableBays = 40,
            hourlyRateAed = 4.0,
            operatorName = "RTA Municipal",
            bays = listOf(
                ParkingBay("BAY_1", "P-1", BayType.PEOPLE_OF_DETERMINATION, BayStatus.AVAILABLE),
                ParkingBay("BAY_2", "W-1", BayType.WOMEN_ONLY_PINK, BayStatus.AVAILABLE),
                ParkingBay("BAY_3", "D-1", BayType.DELIVERY_RIDER, BayStatus.AVAILABLE),
                ParkingBay("BAY_4", "S-1", BayType.STANDARD, BayStatus.AVAILABLE)
            )
        )

        val addResult = repo.addParkingLot(newLot)
        assertTrue(addResult.isSuccess)
        assertEquals(initialCount + 1, repo.getAllLots().first().size)

        // Simulate sensor trigger on bay
        val sensorResult = repo.simulateLiveSensorToggle("LOT_TEST_ADMIN", "BAY_4")
        assertTrue(sensorResult.isSuccess)
        assertEquals(BayStatus.OCCUPIED, sensorResult.getOrNull())

        // Delete facility
        val deleteResult = repo.deleteParkingLot("LOT_TEST_ADMIN")
        assertTrue(deleteResult.isSuccess)
        assertEquals(initialCount, repo.getAllLots().first().size)
    }

    @Test
    fun testWay2AddDuplicateLotFails() = runTest {
        val repo = YallaParkRepositoryImpl()
        val lot = ParkingLot(
            id = "DUPLICATE_LOT",
            name = "Duplicate Lot",
            zone = DubaiZone.BUR_DUBAI,
            facilityType = FacilityType.SURFACE_OPEN_LOT,
            latitude = 25.0,
            longitude = 55.0,
            totalCapacity = 10,
            availableBays = 10,
            hourlyRateAed = 5.0,
            operatorName = "Test Operator",
            bays = emptyList()
        )

        val addFirst = repo.addParkingLot(lot)
        assertTrue(addFirst.isSuccess)

        val addSecond = repo.addParkingLot(lot)
        assertFalse(addSecond.isSuccess)
    }

    @Test
    fun testWay2ModifyBayStatus() = runTest {
        val repo = YallaParkRepositoryImpl()
        val newLot = ParkingLot(
            id = "LOT_MODIFY_BAY",
            name = "Modify Bay Test",
            zone = DubaiZone.KARAMA,
            facilityType = FacilityType.SURFACE_OPEN_LOT,
            latitude = 25.242,
            longitude = 55.302,
            totalCapacity = 20,
            availableBays = 20,
            hourlyRateAed = 3.0,
            operatorName = "RTA Municipal",
            bays = listOf(
                ParkingBay("BAY_1", "P-1", BayType.STANDARD, BayStatus.AVAILABLE)
            )
        )

        repo.addParkingLot(newLot)
        val modifyResult = repo.simulateLiveSensorToggle("LOT_MODIFY_BAY", "BAY_1")
        assertTrue(modifyResult.isSuccess)
        assertEquals(BayStatus.OCCUPIED, modifyResult.getOrNull())

        val modifyBack = repo.simulateLiveSensorToggle("LOT_MODIFY_BAY", "BAY_1")
        assertTrue(modifyBack.isSuccess)
        assertEquals(BayStatus.AVAILABLE, modifyBack.getOrNull())
    }
}
