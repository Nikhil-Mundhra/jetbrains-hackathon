package com.yallapark.ui.components

import com.yallapark.data.engine.PredictiveOccupancyEngine
import com.yallapark.domain.model.DubaiZone
import com.yallapark.domain.model.FacilityType
import com.yallapark.domain.model.ParkingLot
import kotlin.test.Test
import kotlin.test.assertTrue

class PredictiveSliderTest {

    private val engine = PredictiveOccupancyEngine()

    private val sampleLot = ParkingLot(
        id = "TEST_LOT_SLIDER",
        name = "Slider Test Lot",
        zone = DubaiZone.BUR_DUBAI,
        facilityType = FacilityType.SURFACE_OPEN_LOT,
        latitude = 25.2605,
        longitude = 55.2985,
        totalCapacity = 60,
        availableBays = 20,
        hourlyRateAed = 4.0
    )

    @Test
    fun testPredictiveSliderHighProbabilityFormatting() {
        val forecast = engine.calculateForecast(sampleLot, etaMinutes = 5)
        assertTrue(
            forecast.openBayProbabilityPercentage in 5..98,
            "Probability should be within engine bounds [5, 98]"
        )
        assertTrue(
            forecast.recommendation.isNotBlank(),
            "Recommendation text must not be blank"
        )
    }

    @Test
    fun testPredictiveSliderLowProbabilityFormatting() {
        val forecast = engine.calculateForecast(sampleLot, etaMinutes = 120, hourOfDay = 13)
        assertTrue(
            forecast.openBayProbabilityPercentage in 5..98,
            "Probability should be within engine bounds [5, 98]"
        )
        assertTrue(
            forecast.congestionLevel.isNotBlank(),
            "Congestion level text must not be blank"
        )
    }

    @Test
    fun testSliderEtaRangeValues() {
        val etaValues = listOf(5, 15, 30, 45, 60, 90, 120)
        val forecasts = etaValues.map { eta ->
            engine.calculateForecast(sampleLot, etaMinutes = eta)
        }
        // Probability should decrease (or stay equal) as ETA increases
        for (i in 0 until forecasts.size - 1) {
            assertTrue(
                forecasts[i].openBayProbabilityPercentage >= forecasts[i + 1].openBayProbabilityPercentage,
                "Probability at ETA=${etaValues[i]} should be >= probability at ETA=${etaValues[i + 1]}"
            )
        }
    }

    @Test
    fun testDecayRateIsPositive() {
        val forecast = engine.calculateForecast(sampleLot, etaMinutes = 30)
        assertTrue(
            forecast.decayRatePer10Min > 0.0,
            "Decay rate per 10 minutes should always be positive"
        )
    }
}