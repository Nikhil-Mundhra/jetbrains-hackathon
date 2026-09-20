package com.yallapark

import com.yallapark.data.engine.PredictiveOccupancyEngine
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.FacilityType
import com.yallapark.domain.model.ParkingLot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PredictiveOccupancyEngineTest {

    private val engine = PredictiveOccupancyEngine()

    private val sampleLot = ParkingLot(
        id = "TEST_LOT_01",
        name = "Al Fahidi Test Lot",
        zone = DubaiZone.BUR_DUBAI,
        facilityType = FacilityType.SURFACE_OPEN_LOT,
        latitude = 25.2605,
        longitude = 55.2985,
        totalCapacity = 60,
        availableBays = 20,
        hourlyRateAed = 4.0
    )

    @Test
    fun testProbabilityDecaysWithIncreasingEta() {
        val forecast15 = engine.calculateForecast(sampleLot, etaMinutes = 15)
        val forecast45 = engine.calculateForecast(sampleLot, etaMinutes = 45)

        assertTrue(
            forecast15.openBayProbabilityPercentage >= forecast45.openBayProbabilityPercentage,
            "15-minute arrival should have higher or equal probability than 45-minute arrival"
        )
        assertTrue(
            forecast15.estimatedOpenBays >= forecast45.estimatedOpenBays,
            "15-minute arrival should project higher or equal open bays than 45-minute arrival"
        )
    }

    @Test
    fun testZoneTurnoverFactors() {
        val karamaLot = sampleLot.copy(zone = DubaiZone.KARAMA)
        val downtownLot = sampleLot.copy(zone = DubaiZone.DOWNTOWN)

        val karamaForecast = engine.calculateForecast(karamaLot, etaMinutes = 30)
        val downtownForecast = engine.calculateForecast(downtownLot, etaMinutes = 30)

        // Karama has faster commercial turnover/decay than Downtown
        assertTrue(
            karamaForecast.decayRatePer10Min > downtownForecast.decayRatePer10Min,
            "Karama should have higher slot turnover decay rate than Downtown"
        )
    }

    @Test
    fun testEtaZeroReturnsHighProbability() {
        val forecast = engine.calculateForecast(sampleLot, etaMinutes = 0)
        assertTrue(forecast.openBayProbabilityPercentage > 50, "Zero ETA should have >50% probability")
        assertTrue(forecast.estimatedOpenBays > 0, "Zero ETA should have estimated open bays > 0")
    }

    @Test
    fun testLargeEtaReturnsLowProbability() {
        val forecast = engine.calculateForecast(sampleLot, etaMinutes = 120)
        assertTrue(forecast.openBayProbabilityPercentage < 20, "2-hour ETA should have <20% probability")
    }

    @Test
    fun testFullCapacityLot() {
        val fullLot = sampleLot.copy(totalCapacity = 10, availableBays = 10)
        val forecast = engine.calculateForecast(fullLot, etaMinutes = 15)
        assertTrue(forecast.estimatedOpenBays > 5, "Full capacity lot should show high open bays initially")
        assertEquals(98, forecast.openBayProbabilityPercentage, "Upper bound probability should be clamped at 98%")
    }

    @Test
    fun testZeroCapacityLot() {
        val emptyLot = sampleLot.copy(totalCapacity = 0, availableBays = 0)
        val forecast = engine.calculateForecast(emptyLot, etaMinutes = 15)
        assertEquals(0, forecast.estimatedOpenBays, "Zero capacity lot should have 0 open bays")
        assertEquals(10, forecast.openBayProbabilityPercentage, "Zero capacity lot should return baseline 10%")
    }
}
